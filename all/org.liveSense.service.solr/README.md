# [liveSense :: Service :: Solr based search service. - org.liveSense.service.solr](http://github.com/liveSense/org.liveSense.service.solr)

## Description
A Solr embeded server service available or references a remote server. (Derived from Sakai Nakumara - https://github.com/ieb/solr)

## OSGi Exported packages
* org.liveSense.service.solr.api(1.0.0.SNAPSHOT)
* org.liveSense.service.solr.impl(1.0.0.SNAPSHOT)

## OSGi Dependencies
* __System Bundle - org.apache.felix.framework (3.0.8)__
	* javax.naming
	* javax.xml.parsers
	* org.osgi.framework
	* org.osgi.service.packageadmin
	* org.xml.sax
* __Apache Felix Declarative Services - org.apache.felix.scr (1.6.0)__
	* org.osgi.service.component
* __Apache Commons IO Bundle - org.apache.commons.io (1.4)__
	* org.apache.commons.io
* __Commons Lang - org.apache.commons.lang (2.5)__
	* org.apache.commons.lang
* __Guava: Google Core Libraries for Java 1.5 - com.google.guava (10.0.0)__
	* com.google.common.collect
* __[liveSense :: Core - org.liveSense.core (2-SNAPSHOT)](http://github.com/liveSense/org.liveSense.core)__
	* org.liveSense.core
	* org.liveSense.core.service
* __[liveSense :: Framework :: Apache Solr Indexer/Search engine - org.liveSense.framework.solr (2-SNAPSHOT)](http://github.com/liveSense/org.liveSense.framework.solr)__
	* org.apache.solr.client.solrj
	* org.apache.solr.client.solrj.embedded
	* org.apache.solr.client.solrj.response
	* org.apache.solr.common
	* org.apache.solr.core
	* org.apache.solr.handler.admin
	* org.apache.solr.handler.component
	* org.apache.solr.request
	* org.apache.solr.response
	* org.apache.solr.schema
	* org.apache.solr.search
	* org.apache.solr.update.processor
	* org.apache.solr.util.plugin
* __Apache Lucene :: Analyzers :: Common - lucene-analyzers-common (4.0.0)__
	* org.apache.lucene.analysis.util
* __slf4j-api - slf4j.api (1.6.1)__
	* org.slf4j
* __Apache Lucene :: Core - lucene-core (4.0.0)__
	* org.apache.lucene.codecs

## OSGi Embedded JARs
* opencsv-2.3.jar

## Dependency Graph
![alt text](http://raw.github.com.everydayimmirror.in/liveSense/org.liveSense.service.solr/master/osgidependencies.svg "")