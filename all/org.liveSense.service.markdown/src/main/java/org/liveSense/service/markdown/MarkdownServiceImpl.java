package org.liveSense.service.markdown;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyOption;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.liveSense.core.Configurator;
import org.osgi.service.component.ComponentContext;
import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.Parser;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.RootNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Markdown service
 */
@Component(immediate=true, metatype=true, label="%markdown.service.name", description="%markdown.service.description")
@Service(value = MarkdownService.class)
@Properties(value={
		@Property(name="service.vendor", value="org.liveSense"),
		@Property(name = MarkdownServiceImpl.PARAM_EXTENSIONS_TYPE, value = MarkdownServiceImpl.DEFAULT_EXTENSIONS_TYPE, label = "%markdown.extensions.type.name", description = "%markdown.extensions.type.description",
		options = {
				@PropertyOption(name = MarkdownServiceImpl.PARAM_VALUE_EXTENSIONS_TYPE_ALL, value = MarkdownServiceImpl.PARAM_VALUE_EXTENSIONS_TYPE_ALL),
				@PropertyOption(name = MarkdownServiceImpl.PARAM_VALUE_EXTENSIONS_TYPE_NONE, value = MarkdownServiceImpl.PARAM_VALUE_EXTENSIONS_TYPE_NONE),
				@PropertyOption(name = MarkdownServiceImpl.PARAM_VALUE_EXTENSIONS_TYPE_CUSTOM, value = MarkdownServiceImpl.PARAM_VALUE_EXTENSIONS_TYPE_CUSTOM)
		}),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_ABBREVIATIONS, label = "%markdown.extensions.abbreviations.name", description = "%markdown.extensions.abbreviations.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_ABBREVIATIONS),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_AUTOLINKS, label = "%markdown.extensions.autolinks.name", description = "%markdown.extensions.autolinks.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_AUTOLINKS),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_DEFINITIONS, label = "%markdown.extensions.definitions.name", description = "%markdown.extensions.definitions.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_DEFINITIONS),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_FENCED, label = "%markdown.extensions.fenced.name", description = "%markdown.extensions.fenced.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_FENCED),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_HARDWRAPS, label = "%markdown.extensions.hardwraps.name", description = "%markdown.extensions.hardwraps.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_HARDWRAPS),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_QUOTES, label = "%markdown.extensions.quotes.name", description = "%markdown.extensions.quotes.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_QUOTES),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_SMARTS, label = "%markdown.extensions.smarts.name", description = "%markdown.extensions.smarts.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_SMARTS),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_SMARTYPANTS, label = "%markdown.extensions.smartypants.name", description = "%markdown.extensions.smartypants.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_SMARTYPANTS),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_SUPPRESS_ALL_HTML, label = "%markdown.extensions.suppressallhtml.name", description = "%markdown.extensions.suppressallhtml.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_SUPPRESS_ALL_HTML),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_SUPPRESS_HTML_BLOCKS, label = "%markdown.extensions.suppresshtmlblocks.name", description = "%markdown.extensions.suppresshtmlblocks.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_SUPPRESS_HTML_BLOCKS),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_SUPPRESS_INLINE_HTML, label = "%markdown.extensions.suppressinlinehtml.name", description = "%markdown.extensions.suppressinlinehtml.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_SUPPRESS_INLINE_HTML),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_TABLES, label = "%markdown.extensions.tables.name", description = "%markdown.extensions.tables.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_TABLES),
		@Property(name=MarkdownServiceImpl.PARAM_EXTENSIONS_WIKILINKS, label = "%markdown.extensions.wikilinks.name", description = "%markdown.extensions.wikilinks.description", boolValue = MarkdownServiceImpl.DEFAULT_EXTENSIONS_WIKILINKS)
})
public class MarkdownServiceImpl implements MarkdownService {

	/**
	 * default log
	 */
	private final Logger log = LoggerFactory.getLogger(MarkdownServiceImpl.class);

	private int extensions = Extensions.ALL;
	private String extensions_type = PARAM_VALUE_EXTENSIONS_TYPE_ALL;

	private PegDownProcessor default_processor = null;

	public static final String PARAM_VALUE_EXTENSIONS_TYPE_NONE = "None - Standard Markdown";
	public static final String PARAM_VALUE_EXTENSIONS_TYPE_ALL = "All - All extensions listed below";
	public static final String PARAM_VALUE_EXTENSIONS_TYPE_CUSTOM = "Custom - Use the checked extensions below";

	public static final String PARAM_EXTENSIONS_TYPE = "markdown.extensions.type";
	public static final String DEFAULT_EXTENSIONS_TYPE = PARAM_VALUE_EXTENSIONS_TYPE_ALL;

	public static final String PARAM_EXTENSIONS_SMARTS = "markdown.extensions.smart";
	public static final boolean DEFAULT_EXTENSIONS_SMARTS = false;

	public static final String PARAM_EXTENSIONS_QUOTES = "markdown.extensions.quotes";
	public static final boolean DEFAULT_EXTENSIONS_QUOTES = false;

	public static final String PARAM_EXTENSIONS_SMARTYPANTS = "markdown.extensions.smartypants";
	public static final boolean DEFAULT_EXTENSIONS_SMARTYPANTS = false;

	public static final String PARAM_EXTENSIONS_ABBREVIATIONS = "markdown.extensions.abbreviations";
	public static final boolean DEFAULT_EXTENSIONS_ABBREVIATIONS = false;

	public static final String PARAM_EXTENSIONS_HARDWRAPS = "markdown.extensions.hardwraps";
	public static final boolean DEFAULT_EXTENSIONS_HARDWRAPS = false;

	public static final String PARAM_EXTENSIONS_AUTOLINKS = "markdown.extensions.autolinks";
	public static final boolean DEFAULT_EXTENSIONS_AUTOLINKS = false;

	public static final String PARAM_EXTENSIONS_TABLES = "markdown.extensions.tables";
	public static final boolean DEFAULT_EXTENSIONS_TABLES = false;

	public static final String PARAM_EXTENSIONS_DEFINITIONS = "markdown.extensions.definitions";
	public static final boolean DEFAULT_EXTENSIONS_DEFINITIONS = false;

	public static final String PARAM_EXTENSIONS_FENCED = "markdown.extensions.fenced";
	public static final boolean DEFAULT_EXTENSIONS_FENCED = false;

	public static final String PARAM_EXTENSIONS_WIKILINKS = "markdown.extensions.wikilinks";
	public static final boolean DEFAULT_EXTENSIONS_WIKILINKS = false;

	public static final String PARAM_EXTENSIONS_SUPPRESS_HTML_BLOCKS = "markdown.extensions.suppresshtmlblocks";
	public static final boolean DEFAULT_EXTENSIONS_SUPPRESS_HTML_BLOCKS = false;

	public static final String PARAM_EXTENSIONS_SUPPRESS_INLINE_HTML = "markdown.extensions.suppressinlinehtml";
	public static final boolean DEFAULT_EXTENSIONS_SUPPRESS_INLINE_HTML = false;

	public static final String PARAM_EXTENSIONS_SUPPRESS_ALL_HTML = "markdown.extensions.suppressallhtml";
	public static final boolean DEFAULT_EXTENSIONS_SUPPRESS_ALL_HTML = false;

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	Configurator configurator;

	/**
	 * Activates this component.
	 *
	 * @param componentContext The OSGi <code>ComponentContext</code> of this
	 *            component.
	 *            
	 * The created 'default_processor' is not used, the purpose of the creation is the slow creation of the first parser
	 * 
	 * IMPORTANT: PegDown parser is NOT THREAD-SAFE! We have to create a new parser in every call
	 *            
	 */
	@Activate
	protected void activate(ComponentContext componentContext) throws Exception {
		extensions_type = PropertiesUtil.toString(componentContext.getProperties().get(PARAM_EXTENSIONS_TYPE), DEFAULT_EXTENSIONS_TYPE);		
		if (extensions_type == PARAM_VALUE_EXTENSIONS_TYPE_ALL) {
			extensions = Extensions.ALL;
		} else
			if (extensions_type == PARAM_VALUE_EXTENSIONS_TYPE_NONE) {
				extensions = Extensions.NONE;
			} else
				if (extensions_type == PARAM_VALUE_EXTENSIONS_TYPE_CUSTOM) {
					extensions = Extensions.NONE;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_ABBREVIATIONS), DEFAULT_EXTENSIONS_ABBREVIATIONS))
						extensions = extensions & Extensions.ABBREVIATIONS;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_AUTOLINKS), DEFAULT_EXTENSIONS_AUTOLINKS))
						extensions = extensions & Extensions.AUTOLINKS;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_DEFINITIONS), DEFAULT_EXTENSIONS_DEFINITIONS))
						extensions = extensions & Extensions.DEFINITIONS;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_FENCED), DEFAULT_EXTENSIONS_FENCED))
						extensions = extensions & Extensions.FENCED_CODE_BLOCKS;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_HARDWRAPS), DEFAULT_EXTENSIONS_HARDWRAPS))
						extensions = extensions & Extensions.HARDWRAPS;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_QUOTES), DEFAULT_EXTENSIONS_QUOTES))
						extensions = extensions & Extensions.QUOTES;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_SMARTS), DEFAULT_EXTENSIONS_SMARTS))
						extensions = extensions & Extensions.SMARTS;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_SMARTYPANTS), DEFAULT_EXTENSIONS_SMARTYPANTS))
						extensions = extensions & Extensions.SMARTYPANTS;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_SUPPRESS_ALL_HTML), DEFAULT_EXTENSIONS_SUPPRESS_ALL_HTML))
						extensions = extensions & Extensions.SUPPRESS_ALL_HTML;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_SUPPRESS_HTML_BLOCKS), DEFAULT_EXTENSIONS_SUPPRESS_HTML_BLOCKS))
						extensions = extensions & Extensions.SUPPRESS_HTML_BLOCKS;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_SUPPRESS_INLINE_HTML), DEFAULT_EXTENSIONS_SUPPRESS_INLINE_HTML))
						extensions = extensions & Extensions.SUPPRESS_INLINE_HTML;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_TABLES), DEFAULT_EXTENSIONS_TABLES))
						extensions = extensions & Extensions.TABLES;
					if (PropertiesUtil.toBoolean(componentContext.getProperties().get(PARAM_EXTENSIONS_WIKILINKS), DEFAULT_EXTENSIONS_WIKILINKS))
						extensions = extensions & Extensions.WIKILINKS;
				};

				log.info("PegDown parser init start");
				try {
					default_processor = new PegDownProcessor();
					log.info("PegDown parser successfully initializated!");
				} catch (Exception ex) {
					log.error("Error on creating PegDown parser", ex);
					throw new Exception(ex.getMessage());
				}

	}

	@Deactivate
	public void deactivate(ComponentContext componentContext) {
		log.info("Deactivate Markdown bundle");
	}

	/**
	 * Gives the bundle's default processor.
	 * 
	 * IMPORTANT: not thread safe! Use it only when you can provide thread safety!
	 *  
	 */	  
	public PegDownProcessor getDefaultProcessor() {
		return default_processor;
	}

	/**
	 * Creates a new processor instance using the enabled extensions by OSGi framework's property
	 */	  
	public PegDownProcessor getProcessor() {
		PegDownProcessor processor = new PegDownProcessor(extensions);
		return processor;
	}

	/**
	 * Creates a new processor instance with the given {@link org.pegdown.Extensions}.
	 *
	 * @param options the flags of the extensions to enable as a bitmask
	 */
	public PegDownProcessor getProcessor(int options) {
		PegDownProcessor processor = new PegDownProcessor(options);
		return processor;
	}

	/**
	 * Creates a new processor instance using the given Parser and tabstop width.
	 *
	 * @param parser the parser instance to use
	 */    
	public PegDownProcessor getProcessor(Parser parser) {
		PegDownProcessor processor = new PegDownProcessor(parser);
		return processor;
	}

	/**
	 * Converts the given markdown source to HTML with a new processor (thread safe!)
	 *
	 * @param markdownSource the markdown source to convert
	 * @return the HTML
	 */    
	public String markdownToHtml(String markdownSource)	{
		PegDownProcessor processor = getProcessor();
		return processor.markdownToHtml(markdownSource.toCharArray());
	}

	/**
	 * Converts the given markdown source to HTML.
	 *
	 * @param markdownSource the markdown source to convert
	 * @param linkRenderer the LinkRenderer to use
	 * @return the HTML
	 */    
	public String markdownToHtml(String markdownSource, LinkRenderer linkRenderer) {
		PegDownProcessor processor = getProcessor();
		return processor.markdownToHtml(markdownSource.toCharArray(), linkRenderer);
	}

	/**
	 * Converts the given markdown source to HTML.
	 *
	 * @param markdownSource the markdown source to convert
	 * @return the HTML
	 */    
	public String markdownToHtml(char[] markdownSource) {
		PegDownProcessor processor = getProcessor();
		return processor.markdownToHtml(markdownSource, new LinkRenderer());
	}

	/**
	 * Converts the given markdown source to HTML.
	 *
	 * @param markdownSource the markdown source to convert
	 * @param linkRenderer the LinkRenderer to use
	 * @return the HTML
	 */    
	public String markdownToHtml(char[] markdownSource, LinkRenderer linkRenderer) {
		PegDownProcessor processor = getProcessor();
		RootNode astRoot = processor.parseMarkdown(markdownSource);
		return new ToHtmlSerializer(linkRenderer).toHtml(astRoot);
	}
}
