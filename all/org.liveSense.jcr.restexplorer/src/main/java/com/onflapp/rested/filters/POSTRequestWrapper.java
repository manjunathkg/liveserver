/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.onflapp.rested.filters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.request.RequestParameter;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.wrappers.SlingHttpServletResponseWrapper;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.apache.sling.engine.EngineConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.framework.Constants;

public class POSTRequestWrapper extends SlingHttpServletRequestWrapper {
	private com.onflapp.rested.filters.ParameterMap mypars;

	public POSTRequestWrapper (SlingHttpServletRequest req) {
		super (req);
		RequestParameterMap pars = super.getRequestParameterMap();
		mypars = new com.onflapp.rested.filters.ParameterMap();

		Iterator it = pars.entrySet().iterator();
    while (it.hasNext()) {
    	Map.Entry vp = (Map.Entry)it.next();
      String key = (String)vp.getKey();
			RequestParameter[] val = (RequestParameter[])vp.getValue();

			if (key.endsWith("@NameFrom")) {
				String pname = key.substring (0, key.length() - 9);
				RequestParameter nval = pars.getValue(pname);
				if (nval != null) {
					mypars.removeParameter(pname);
					mypars.setParameters(nval.getString(),val);
				}
			}
			else if (key.startsWith(":") && key.endsWith("@ValueFrom")) {
				String oname = key.substring (0, key.length() - 10);
				String pname = val[0].getString();
				RequestParameter[] nval = pars.getValues(pname);
				if (nval != null) {
					mypars.removeParameter(pname);
					mypars.setParameters(oname,nval);
				}
			}
			else {
				mypars.setParameters(key, val);
			}
		}
	}

	public String getParameter(String name) {
		RequestParameter v = mypars.getValue(name);
		if (v == null) return null;
		else return v.getString();
	}

	public RequestParameterMap getRequestParameterMap() {
		return mypars;
	}

	public RequestParameter getRequestParameter(String name) {
		return mypars.getValue(name);
	}

	public RequestParameter[] getRequestParameters(String name) {
		return mypars.getValues(name);
	}
}


