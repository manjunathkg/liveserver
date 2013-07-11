package org.liveSense.misc.queryBuilder.gwt;

import com.google.web.bindery.requestfactory.shared.ServiceLocator;

class DummyServiceLocator implements ServiceLocator {
    public Object getInstance( Class<?> clazz ) {
      return null;
    }
  }