package org.liveSense.service.cxf;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


public class SoapRequestWrapperTest {

	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	public String getPath(String path) {
		MockHttpServletRequest req = new MockHttpServletRequest("GET", path);
		req.setPathInfo(path);
		SoapRequestWrapper rw = new SoapRequestWrapper(req, "/webservices/servlet");
		
		return rw.getPathInfo();
	}

	public MediaType getMediaType(String path) {
		MockHttpServletRequest req = new MockHttpServletRequest("GET", path);
		req.setPathInfo(path);
		SoapRequestWrapper rw = new SoapRequestWrapper(req, "/webservices/servlet");
		
		return rw.getMediaType(new MediaType("mimetype","mimesubtype"));
	}

	@Test
	public void testGetPath() throws URISyntaxException {
		assertEquals("prefix/webservices/servlet", getPath("prefix/webservices/servlet"));
		assertEquals("/prefix/webservices/servlet", getPath("/prefix/webservices/servlet"));
		assertEquals("/prefix/webservices/servlet/selector", getPath("/prefix/webservices/servlet/selector"));
		assertEquals("/prefix/webservices/servlet/selector1.selector2", getPath("/prefix/webservices/servlet/selector1.selector2"));

		assertEquals("/webservices/servlet", getPath("/webservices/servlet"));
		assertEquals("/webservices/servlet?query", getPath("/webservices/servlet?query"));
		assertEquals("/webservices/servlet?query&query2", getPath("/webservices/servlet?query&query2"));
		assertEquals("/webservices/servlet?query=value", getPath("/webservices/servlet?query=value"));
		assertEquals("/webservices/servlet?query=value&query2=value2", getPath("/webservices/servlet?query=value&query2=value2"));
		assertEquals("/webservices/servlet#fragment", getPath("/webservices/servlet#fragment"));
		assertEquals("/webservices/servlet?query#fragment", getPath("/webservices/servlet?query#fragment"));
		assertEquals("/webservices/servlet?query&query2#fragment", getPath("/webservices/servlet?query&query2#fragment"));
		assertEquals("/webservices/servlet?query=value#fragment", getPath("/webservices/servlet?query=value#fragment"));
		assertEquals("/webservices/servlet?query=value&query2=value2#fragment", getPath("/webservices/servlet?query=value&query2=value2#fragment"));

		assertEquals("/webservices/servlet/", getPath("/webservices/servlet/"));
		assertEquals("/webservices/servlet/?query", getPath("/webservices/servlet/?query"));
		assertEquals("/webservices/servlet/?query&query2", getPath("/webservices/servlet/?query&query2"));
		assertEquals("/webservices/servlet/?query=value", getPath("/webservices/servlet/?query=value"));
		assertEquals("/webservices/servlet/?query=value&query2=value2", getPath("/webservices/servlet/?query=value&query2=value2"));
		assertEquals("/webservices/servlet/#fragment", getPath("/webservices/servlet/#fragment"));
		assertEquals("/webservices/servlet/?query#fragment", getPath("/webservices/servlet/?query#fragment"));
		assertEquals("/webservices/servlet/?query&query2#fragment", getPath("/webservices/servlet/?query&query2#fragment"));
		assertEquals("/webservices/servlet/?query=value#fragment", getPath("/webservices/servlet/?query=value#fragment"));
		assertEquals("/webservices/servlet/?query=value&query2=value2#fragment", getPath("/webservices/servlet/?query=value&query2=value2#fragment"));
		
		assertEquals("/webservices/servlet", getPath("/webservices/servlet."));
		assertEquals("/webservices/servlet?query", getPath("/webservices/servlet.?query"));
		assertEquals("/webservices/servlet?query&query2", getPath("/webservices/servlet.?query&query2"));
		assertEquals("/webservices/servlet?query=value", getPath("/webservices/servlet.?query=value"));
		assertEquals("/webservices/servlet?query=value&query2=value2", getPath("/webservices/servlet.?query=value&query2=value2"));
		assertEquals("/webservices/servlet#fragment", getPath("/webservices/servlet.#fragment"));
		assertEquals("/webservices/servlet?query#fragment", getPath("/webservices/servlet.?query#fragment"));
		assertEquals("/webservices/servlet?query&query2#fragment", getPath("/webservices/servlet.?query&query2#fragment"));
		assertEquals("/webservices/servlet?query=value#fragment", getPath("/webservices/servlet.?query=value#fragment"));
		assertEquals("/webservices/servlet?query=value&query2=value2#fragment", getPath("/webservices/servlet.?query=value&query2=value2#fragment"));

		assertEquals("/webservices/servlet/", getPath("/webservices/servlet./"));
		assertEquals("/webservices/servlet/?query", getPath("/webservices/servlet./?query"));
		assertEquals("/webservices/servlet/?query&query2", getPath("/webservices/servlet./?query&query2"));
		assertEquals("/webservices/servlet/?query=value", getPath("/webservices/servlet./?query=value"));
		assertEquals("/webservices/servlet/?query=value&query2=value2", getPath("/webservices/servlet./?query=value&query2=value2"));
		assertEquals("/webservices/servlet/#fragment", getPath("/webservices/servlet./#fragment"));
		assertEquals("/webservices/servlet/?query#fragment", getPath("/webservices/servlet./?query#fragment"));
		assertEquals("/webservices/servlet/?query&query2#fragment", getPath("/webservices/servlet./?query&query2#fragment"));
		assertEquals("/webservices/servlet/?query=value#fragment", getPath("/webservices/servlet./?query=value#fragment"));
		assertEquals("/webservices/servlet/?query=value&query2=value2#fragment", getPath("/webservices/servlet./?query=value&query2=value2#fragment"));

		assertEquals("/webservices/servlet.type", getPath("/webservices/servlet.type"));
		assertEquals("/webservices/servlet.type?query", getPath("/webservices/servlet.type?query"));
		assertEquals("/webservices/servlet.type?query&query2", getPath("/webservices/servlet.type?query&query2"));
		assertEquals("/webservices/servlet.type?query=value", getPath("/webservices/servlet.type?query=value"));
		assertEquals("/webservices/servlet.type?query=value&query2=value2", getPath("/webservices/servlet.type?query=value&query2=value2"));
		assertEquals("/webservices/servlet.type#fragment", getPath("/webservices/servlet.type#fragment"));
		assertEquals("/webservices/servlet.type?query#fragment", getPath("/webservices/servlet.type?query#fragment"));
		assertEquals("/webservices/servlet.type?query&query2#fragment", getPath("/webservices/servlet.type?query&query2#fragment"));
		assertEquals("/webservices/servlet.type?query=value#fragment", getPath("/webservices/servlet.type?query=value#fragment"));
		assertEquals("/webservices/servlet.type?query=value&query2=value2#fragment", getPath("/webservices/servlet.type?query=value&query2=value2#fragment"));

		assertEquals("/webservices/servlet/.type", getPath("/webservices/servlet.type/"));
		assertEquals("/webservices/servlet/.type?query", getPath("/webservices/servlet.type/?query"));
		assertEquals("/webservices/servlet/.type?query&query2", getPath("/webservices/servlet.type/?query&query2"));
		assertEquals("/webservices/servlet/.type?query=value", getPath("/webservices/servlet.type/?query=value"));
		assertEquals("/webservices/servlet/.type?query=value&query2=value2", getPath("/webservices/servlet.type/?query=value&query2=value2"));
		assertEquals("/webservices/servlet/.type#fragment", getPath("/webservices/servlet.type/#fragment"));
		assertEquals("/webservices/servlet/.type?query#fragment", getPath("/webservices/servlet.type/?query#fragment"));
		assertEquals("/webservices/servlet/.type?query&query2#fragment", getPath("/webservices/servlet.type/?query&query2#fragment"));
		assertEquals("/webservices/servlet/.type?query=value#fragment", getPath("/webservices/servlet.type/?query=value#fragment"));
		assertEquals("/webservices/servlet/.type?query=value&query2=value2#fragment", getPath("/webservices/servlet.type/?query=value&query2=value2#fragment"));

		assertEquals("/webservices/servlet.type.language", getPath("/webservices/servlet.type.language"));
		assertEquals("/webservices/servlet.type.language?query", getPath("/webservices/servlet.type.language?query"));
		assertEquals("/webservices/servlet.type.language?query&query2", getPath("/webservices/servlet.type.language?query&query2"));
		assertEquals("/webservices/servlet.type.language?query=value", getPath("/webservices/servlet.type.language?query=value"));
		assertEquals("/webservices/servlet.type.language?query=value&query2=value2", getPath("/webservices/servlet.type.language?query=value&query2=value2"));
		assertEquals("/webservices/servlet.type.language#fragment", getPath("/webservices/servlet.type.language#fragment"));
		assertEquals("/webservices/servlet.type.language?query#fragment", getPath("/webservices/servlet.type.language?query#fragment"));
		assertEquals("/webservices/servlet.type.language?query&query2#fragment", getPath("/webservices/servlet.type.language?query&query2#fragment"));
		assertEquals("/webservices/servlet.type.language?query=value#fragment", getPath("/webservices/servlet.type.language?query=value#fragment"));
		assertEquals("/webservices/servlet.type.language?query=value&query2=value2#fragment", getPath("/webservices/servlet.type.language?query=value&query2=value2#fragment"));

		assertEquals("/webservices/servlet/.type.language", getPath("/webservices/servlet.type.language/"));
		assertEquals("/webservices/servlet/.type.language?query", getPath("/webservices/servlet.type.language/?query"));
		assertEquals("/webservices/servlet/.type.language?query&query2", getPath("/webservices/servlet.type.language/?query&query2"));
		assertEquals("/webservices/servlet/.type.language?query=value", getPath("/webservices/servlet.type.language/?query=value"));
		assertEquals("/webservices/servlet/.type.language?query=value&query2=value2", getPath("/webservices/servlet.type.language/?query=value&query2=value2"));
		assertEquals("/webservices/servlet/.type.language#fragment", getPath("/webservices/servlet.type.language/#fragment"));
		assertEquals("/webservices/servlet/.type.language?query#fragment", getPath("/webservices/servlet.type.language/?query#fragment"));
		assertEquals("/webservices/servlet/.type.language?query&query2#fragment", getPath("/webservices/servlet.type.language/?query&query2#fragment"));
		assertEquals("/webservices/servlet/.type.language?query=value#fragment", getPath("/webservices/servlet.type.language/?query=value#fragment"));
		assertEquals("/webservices/servlet/.type.language?query=value&query2=value2#fragment", getPath("/webservices/servlet.type.language/?query=value&query2=value2#fragment"));

		
		assertEquals("/webservices/servlet/function", getPath("/webservices/servlet/function"));
		assertEquals("/webservices/servlet/function?query", getPath("/webservices/servlet/function?query"));
		assertEquals("/webservices/servlet/function?query&query2", getPath("/webservices/servlet/function?query&query2"));
		assertEquals("/webservices/servlet/function?query=value", getPath("/webservices/servlet/function?query=value"));
		assertEquals("/webservices/servlet/function?query=value&query2=value2", getPath("/webservices/servlet/function?query=value&query2=value2"));
		assertEquals("/webservices/servlet/function#fragment", getPath("/webservices/servlet/function#fragment"));
		assertEquals("/webservices/servlet/function?query#fragment", getPath("/webservices/servlet/function?query#fragment"));
		assertEquals("/webservices/servlet/function?query&query2#fragment", getPath("/webservices/servlet/function?query&query2#fragment"));
		assertEquals("/webservices/servlet/function?query=value#fragment", getPath("/webservices/servlet/function?query=value#fragment"));
		assertEquals("/webservices/servlet/function?query=value&query2=value2#fragment", getPath("/webservices/servlet/function?query=value&query2=value2#fragment"));
		
		assertEquals("/webservices/servlet/function", getPath("/webservices/servlet./function"));
		assertEquals("/webservices/servlet/function?query", getPath("/webservices/servlet./function?query"));
		assertEquals("/webservices/servlet/function?query&query2", getPath("/webservices/servlet./function?query&query2"));
		assertEquals("/webservices/servlet/function?query=value", getPath("/webservices/servlet./function?query=value"));
		assertEquals("/webservices/servlet/function?query=value&query2=value2", getPath("/webservices/servlet./function?query=value&query2=value2"));
		assertEquals("/webservices/servlet/function#fragment", getPath("/webservices/servlet./function#fragment"));
		assertEquals("/webservices/servlet/function?query#fragment", getPath("/webservices/servlet./function?query#fragment"));
		assertEquals("/webservices/servlet/function?query&query2#fragment", getPath("/webservices/servlet./function?query&query2#fragment"));
		assertEquals("/webservices/servlet/function?query=value#fragment", getPath("/webservices/servlet./function?query=value#fragment"));
		assertEquals("/webservices/servlet/function?query=value&query2=value2#fragment", getPath("/webservices/servlet./function?query=value&query2=value2#fragment"));

		assertEquals("/webservices/servlet/function/", getPath("/webservices/servlet./function/"));
		assertEquals("/webservices/servlet/function/?query", getPath("/webservices/servlet./function/?query"));
		assertEquals("/webservices/servlet/function/?query&query2", getPath("/webservices/servlet./function/?query&query2"));
		assertEquals("/webservices/servlet/function/?query=value", getPath("/webservices/servlet./function/?query=value"));
		assertEquals("/webservices/servlet/function/?query=value&query2=value2", getPath("/webservices/servlet./function/?query=value&query2=value2"));
		assertEquals("/webservices/servlet/function/#fragment", getPath("/webservices/servlet./function/#fragment"));
		assertEquals("/webservices/servlet/function/?query#fragment", getPath("/webservices/servlet./function/?query#fragment"));
		assertEquals("/webservices/servlet/function/?query&query2#fragment", getPath("/webservices/servlet./function/?query&query2#fragment"));
		assertEquals("/webservices/servlet/function/?query=value#fragment", getPath("/webservices/servlet./function/?query=value#fragment"));
		assertEquals("/webservices/servlet/function/?query=value&query2=value2#fragment", getPath("/webservices/servlet./function/?query=value&query2=value2#fragment"));

		assertEquals("/webservices/servlet/function.type", getPath("/webservices/servlet.type/function"));
		assertEquals("/webservices/servlet/function.type?query", getPath("/webservices/servlet.type/function?query"));
		assertEquals("/webservices/servlet/function.type?query&query2", getPath("/webservices/servlet.type/function?query&query2"));
		assertEquals("/webservices/servlet/function.type?query=value", getPath("/webservices/servlet.type/function?query=value"));
		assertEquals("/webservices/servlet/function.type?query=value&query2=value2", getPath("/webservices/servlet.type/function?query=value&query2=value2"));
		assertEquals("/webservices/servlet/function.type#fragment", getPath("/webservices/servlet.type/function#fragment"));
		assertEquals("/webservices/servlet/function.type?query#fragment", getPath("/webservices/servlet.type/function?query#fragment"));
		assertEquals("/webservices/servlet/function.type?query&query2#fragment", getPath("/webservices/servlet.type/function?query&query2#fragment"));
		assertEquals("/webservices/servlet/function.type?query=value#fragment", getPath("/webservices/servlet.type/function?query=value#fragment"));
		assertEquals("/webservices/servlet/function.type?query=value&query2=value2#fragment", getPath("/webservices/servlet.type/function?query=value&query2=value2#fragment"));

		assertEquals("/webservices/servlet/function.type", getPath("/webservices/servlet.type/function"));
		assertEquals("/webservices/servlet/function.type?query", getPath("/webservices/servlet.type/function?query"));
		assertEquals("/webservices/servlet/function.type?query&query2", getPath("/webservices/servlet.type/function?query&query2"));
		assertEquals("/webservices/servlet/function.type?query=value", getPath("/webservices/servlet.type/function?query=value"));
		assertEquals("/webservices/servlet/function.type?query=value&query2=value2", getPath("/webservices/servlet.type/function?query=value&query2=value2"));
		assertEquals("/webservices/servlet/function.type#fragment", getPath("/webservices/servlet.type/function#fragment"));
		assertEquals("/webservices/servlet/function.type?query#fragment", getPath("/webservices/servlet.type/function?query#fragment"));
		assertEquals("/webservices/servlet/function.type?query&query2#fragment", getPath("/webservices/servlet.type/function?query&query2#fragment"));
		assertEquals("/webservices/servlet/function.type?query=value#fragment", getPath("/webservices/servlet.type/function?query=value#fragment"));
		assertEquals("/webservices/servlet/function.type?query=value&query2=value2#fragment", getPath("/webservices/servlet.type/function?query=value&query2=value2#fragment"));

		assertEquals("/webservices/servlet/function.type.language", getPath("/webservices/servlet.type.language/function"));
		assertEquals("/webservices/servlet/function.type.language?query", getPath("/webservices/servlet.type.language/function?query"));
		assertEquals("/webservices/servlet/function.type.language?query&query2", getPath("/webservices/servlet.type.language/function?query&query2"));
		assertEquals("/webservices/servlet/function.type.language?query=value", getPath("/webservices/servlet.type.language/function?query=value"));
		assertEquals("/webservices/servlet/function.type.language?query=value&query2=value2", getPath("/webservices/servlet.type.language/function?query=value&query2=value2"));
		assertEquals("/webservices/servlet/function.type.language#fragment", getPath("/webservices/servlet.type.language/function#fragment"));
		assertEquals("/webservices/servlet/function.type.language?query#fragment", getPath("/webservices/servlet.type.language/function?query#fragment"));
		assertEquals("/webservices/servlet/function.type.language?query&query2#fragment", getPath("/webservices/servlet.type.language/function?query&query2#fragment"));
		assertEquals("/webservices/servlet/function.type.language?query=value#fragment", getPath("/webservices/servlet.type.language/function?query=value#fragment"));
		assertEquals("/webservices/servlet/function.type.language?query=value&query2=value2#fragment", getPath("/webservices/servlet.type.language/function?query=value&query2=value2#fragment"));

		assertEquals("/webservices/servlet/function.type.language", getPath("/webservices/servlet.type.language/function"));
		assertEquals("/webservices/servlet/function.type.language?query", getPath("/webservices/servlet.type.language/function?query"));
		assertEquals("/webservices/servlet/function.type.language?query&query2", getPath("/webservices/servlet.type.language/function?query&query2"));
		assertEquals("/webservices/servlet/function.type.language?query=value", getPath("/webservices/servlet.type.language/function?query=value"));
		assertEquals("/webservices/servlet/function.type.language?query=value&query2=value2", getPath("/webservices/servlet.type.language/function?query=value&query2=value2"));
		assertEquals("/webservices/servlet/function.type.language#fragment", getPath("/webservices/servlet.type.language/function#fragment"));
		assertEquals("/webservices/servlet/function.type.language?query#fragment", getPath("/webservices/servlet.type.language/function?query#fragment"));
		assertEquals("/webservices/servlet/function.type.language?query&query2#fragment", getPath("/webservices/servlet.type.language/function?query&query2#fragment"));
		assertEquals("/webservices/servlet/function.type.language?query=value#fragment", getPath("/webservices/servlet.type.language/function?query=value#fragment"));
		assertEquals("/webservices/servlet/function.type.language?query=value&query2=value2#fragment", getPath("/webservices/servlet.type.language/function?query=value&query2=value2#fragment"));
	}

	
	@Test
	public void testGetMimeType() throws URISyntaxException {
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("prefix/webservices/servlet").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/prefix/webservices/servlet").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/prefix/webservices/servlet/selector").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/prefix/webservices/servlet/selector1.selector2").toString());

		
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("prefix/webservices/servlet").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/prefix/webservices/servlet").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/prefix/webservices/servlet/selector").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/prefix/webservices/servlet/selector1.selector2").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/?query=value&query2=value2#fragment").toString());
		
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/?query=value&query2=value2#fragment").toString());

		
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/function").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/function?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/function?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/function?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/function?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/function#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/function?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/function?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/function?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet/function?query=value&query2=value2#fragment").toString());
		
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function/").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function/?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function/?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function/?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function/?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function/#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function/?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function/?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function/?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet./function/?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type/function?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query&query2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query=value").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query=value&query2=value2").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query&query2#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query=value#fragment").toString());
		assertEquals(new MediaType("mimetype", "mimesubtype").toString(), getMediaType("/webservices/servlet.type.language/function?query=value&query2=value2#fragment").toString());

		// Json types
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json?query").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json?query&query2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json?query=value").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json?query=value&query2=value2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json?query#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json?query&query2#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json?query=value#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/?query").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/?query&query2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/?query=value").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/?query=value&query2=value2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/?query#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/?query&query2#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/?query=value#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language?query").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language?query&query2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language?query=value").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language?query=value&query2=value2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language?query#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language?query&query2#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language?query=value#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/?query").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/?query&query2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/?query=value").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/?query=value&query2=value2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/?query#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/?query&query2#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/?query=value#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query&query2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query=value").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query=value&query2=value2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query&query2#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query=value#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query&query2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query=value").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query=value&query2=value2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query&query2#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query=value#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query&query2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query=value").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query=value&query2=value2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query&query2#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query=value#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query&query2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query=value").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query=value&query2=value2").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query&query2#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query=value#fragment").toString());
		assertEquals(MediaType.APPLICATION_JSON, getMediaType("/webservices/servlet.json.language/function?query=value&query2=value2#fragment").toString());
	
		// Text Plain types
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text?query").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text?query&query2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text?query=value").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text?query#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/?query").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/?query&query2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/?query=value").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/?query#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language?query").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language?query&query2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language?query=value").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language?query#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/?query").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/?query&query2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/?query=value").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/?query#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query&query2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query=value").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query&query2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query=value").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query&query2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query=value").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query&query2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query=value").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_PLAIN, getMediaType("/webservices/servlet.text.language/function?query=value&query2=value2#fragment").toString());

		// XML Types
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml?query").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml?query&query2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml?query=value").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml?query#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/?query").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/?query&query2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/?query=value").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/?query#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language?query").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language?query&query2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language?query=value").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language?query#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/?query").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/?query&query2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/?query=value").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/?query#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query&query2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query=value").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query&query2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query=value").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query&query2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query=value").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query&query2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query=value").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_XML, getMediaType("/webservices/servlet.xml.language/function?query=value&query2=value2#fragment").toString());

		// HTML Types
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html?query").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html?query&query2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html?query=value").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html?query#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/?query").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/?query&query2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/?query=value").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/?query#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language?query").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language?query&query2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language?query=value").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language?query#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/?query").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/?query&query2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/?query=value").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/?query#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query&query2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query=value").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query&query2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query=value").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query&query2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query=value").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query=value&query2=value2#fragment").toString());

		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query&query2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query=value").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query=value&query2=value2").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query&query2#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query=value#fragment").toString());
		assertEquals(MediaType.TEXT_HTML, getMediaType("/webservices/servlet.html.language/function?query=value&query2=value2#fragment").toString());

		// Custom type
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype?query").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype?query&query2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype?query=value").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype?query=value&query2=value2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype?query#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype?query&query2#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype?query=value#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/?query").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/?query&query2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/?query=value").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/?query=value&query2=value2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/?query#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/?query&query2#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/?query=value#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language?query").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language?query&query2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language?query=value").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language?query=value&query2=value2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language?query#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language?query&query2#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language?query=value#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/?query").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/?query&query2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/?query=value").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/?query=value&query2=value2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/?query#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/?query&query2#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/?query=value#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query&query2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query=value").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query=value&query2=value2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query&query2#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query=value#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query&query2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query=value").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query=value&query2=value2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query&query2#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query=value#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype/function?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query&query2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query=value").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query=value&query2=value2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query&query2#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query=value#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query=value&query2=value2#fragment").toString());

		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query&query2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query=value").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query=value&query2=value2").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query&query2#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query=value#fragment").toString());
		assertEquals(new MediaType("testtype","testsubtype").toString(), getMediaType("/webservices/servlet.testtype-testsubtype.language/function?query=value&query2=value2#fragment").toString());

		
	
	
	}
}
