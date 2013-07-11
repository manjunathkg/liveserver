package  org.liveSense.service.solr.api;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EmbeddedOSGiClientSolrConfig extends SolrConfig {

  public EmbeddedOSGiClientSolrConfig(SolrResourceLoader loader, String name, InputSource is)
      throws ParserConfigurationException, IOException, SAXException {
    super(loader, name, is);
  }

}
