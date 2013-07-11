package org.liveSense.jcr.explorer.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;
import javax.servlet.Servlet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.base.NodeTypeLoader;
import org.liveSense.jcr.explorer.domain.JcrNode;
import org.liveSense.jcr.explorer.domain.JcrProperty;
import org.liveSense.jcr.explorer.domain.RemoteFile;
import org.liveSense.jcr.explorer.service.JcrService;
import org.liveSense.jcr.explorer.service.SerializedException;
import org.liveSense.service.gwt.GWTRPCServlet;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import com.google.common.collect.Maps;

/**
 * @author Robert Csakany
 * This code originally derived from https://code.google.com/p/jackrabbitexplorer/
 * @author James Pickup
 *
 */

class JcrServiceImplParameterProvider {
	public static final String VALUE_TYPE_REP_SYSTEM = "rep:system=/jcrexplorer/images/icons/operation.png";
	public static final String VALUE_TYPE_NT_FOLDER = "nt:folder=/jcrexplorer/images/icons/folder_open.png";
	public static final String VALUE_TYPE_NT_UNSTRUCTURED = "nt:unstructured=/jcrexplorer/images/icons/complexType.gif";
	public static final String VALUE_TYPE_REP_VERSION_STORAGE = "rep:versionStorage=/jcrexplorer/images/icons/simpleType.png";
	public static final String VALUE_TYPE_REP_ACTIVITIES = "rep:Activities=/jcrexplorer/splashyIcons/bullet_blue.png";
	public static final String VALUE_TYPE_REP_NODE_TYPES = "rep:nodeTypes=/jcrexplorer/images/icons/complexType.gif";
	public static final String VALUE_TYPE_NT_FILE = "nt:file=/jcrexplorer/images/icons/file.png";
	public static final String VALUE_TYPE_NT_RESOURCE = "nt:resource=/jcrexplorer/images/icons/data.png";
	public static final String PROP_TYPES = "resourceTypes";
	
	public static final String PROP_BROWSABLE_FILTER_REGEX = "browsableContentFilterRegex";
	public static final String VALUE_BROWSABLE_FILTER_REGEX = "^https?:\\/\\/.+?$";

	public static final String PROP_ICON_PATH = "iconPath";
	public static final String VALUE_ICON_PATH = "/jcrexplorer/images/icons";

	public static String[] getAllTypes() {
		
		return new String[] {JcrServiceImplParameterProvider.VALUE_TYPE_REP_SYSTEM, 
			JcrServiceImplParameterProvider.VALUE_TYPE_NT_FOLDER, 
			JcrServiceImplParameterProvider.VALUE_TYPE_NT_RESOURCE, 
			JcrServiceImplParameterProvider.VALUE_TYPE_NT_UNSTRUCTURED, 
			JcrServiceImplParameterProvider.VALUE_TYPE_REP_ACTIVITIES, 
			JcrServiceImplParameterProvider.VALUE_TYPE_REP_NODE_TYPES, 
			JcrServiceImplParameterProvider.VALUE_TYPE_REP_SYSTEM,
			JcrServiceImplParameterProvider.VALUE_TYPE_REP_VERSION_STORAGE};
	}
	
	public static List<Map<String, String>> getTypeParameters(ComponentContext context) {
		List<Map<String, String>> ret = new ArrayList<Map<String,String>>();
		for (String str : PropertiesUtil.toStringArray(context.getProperties().get(PROP_TYPES), getAllTypes())) {
			Map<String, String> m = Maps.newHashMapWithExpectedSize(1);
			String strs[] = StringUtils.split(str, "=");
			m.put(strs[0], strs[1]);
			ret.add(m);
		}
		return ret;
	}
	
	public static String getBrowsableFilterRegex(ComponentContext context) {
		return PropertiesUtil.toString(context.getProperties().get(PROP_BROWSABLE_FILTER_REGEX), VALUE_BROWSABLE_FILTER_REGEX);
	}

	public static String getIconPath(ComponentContext context) {
		return PropertiesUtil.toString(context.getProperties().get(PROP_ICON_PATH), VALUE_ICON_PATH);
	}
	

	public static void updateTypeParameter(ComponentContext context) {
		
	}
}

@Component(immediate = true,  metatype = true, label = "%organization.post.servlet.post.name", description = "%organization.post.servlet.post.description")
@Service(value = Servlet.class)
@Properties({
	@Property(name = "service.description", value = "Jcr Explorer upload Post Servlet"),
	@Property(name = "service.vendor", value = "Ajanlom.hu"),
	@Property(name = "sling.servlet.paths", value = "/jcrexplorer/jcrexplorer/JcrService"),
	@Property(name = "sling.servlet.methods", value = {"GET","POST"}),
	@Property(name = JcrServiceImplParameterProvider.PROP_TYPES, value = {JcrServiceImplParameterProvider.VALUE_TYPE_REP_SYSTEM, 
			JcrServiceImplParameterProvider.VALUE_TYPE_NT_FOLDER, 
			JcrServiceImplParameterProvider.VALUE_TYPE_NT_RESOURCE, 
			JcrServiceImplParameterProvider.VALUE_TYPE_NT_UNSTRUCTURED, 
			JcrServiceImplParameterProvider.VALUE_TYPE_REP_ACTIVITIES, 
			JcrServiceImplParameterProvider.VALUE_TYPE_REP_NODE_TYPES, 
			JcrServiceImplParameterProvider.VALUE_TYPE_REP_SYSTEM,
			JcrServiceImplParameterProvider.VALUE_TYPE_REP_VERSION_STORAGE}, cardinality=Integer.MAX_VALUE),
	@Property(name = JcrServiceImplParameterProvider.PROP_BROWSABLE_FILTER_REGEX, value = JcrServiceImplParameterProvider.VALUE_BROWSABLE_FILTER_REGEX),
	@Property(name = JcrServiceImplParameterProvider.PROP_ICON_PATH, value = JcrServiceImplParameterProvider.VALUE_ICON_PATH)

})
public class JcrServiceImpl extends GWTRPCServlet implements JcrService {
	private static final long serialVersionUID = 8840001785942628602L;
	
	private final Log log = LogFactory.getLog(this.getClass());
	private final static String FULL_TEXT_SEARCH = "fullTextSearch";
	private final static String XPATH_SEARCH = "xpathSearch";
	private final static String SQL_SEARCH = "sqlSearch";

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	private ConfigurationAdmin configAdminService;

	private String[] nodeTypeIcons;
	
	
	private ComponentContext context;
	
	@Activate
	protected void activate(ComponentContext context) {
		this.context = context;
		super.setClassLoader(this.getClass().getClassLoader());
	}
	
	protected Session getJcrSession() throws Exception {
		return ((SlingHttpServletRequest)getThreadLocalRequest()).getResourceResolver().adaptTo(Session.class);
	}

	/**
	 * Delete all files and sub directories
	 * @param path
	 * @return boolean. True for success
	 */

	protected boolean deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return( path.delete() );
	}

	@Override
	public String getBrowsableContentFilterRegex() throws SerializedException {
		return JcrServiceImplParameterProvider.getBrowsableFilterRegex(context);
	}

	/**
	 * Retrieve the node type icon mappings from properties file
	 */
	@Override
	public List<Map<String, String>> getNodeTypeIcons() throws SerializedException {
		return JcrServiceImplParameterProvider.getTypeParameters(context);
	}

	/**
	 * Method used to navigate to a specific path, used for the 'go' browse button and the
	 * search when clicking on a search result
	 * @see org.liveSense.jcr.explorer.service.JcrService#getNodeTree(java.lang.String)
	 */
	@Override
	public List<Map<String, List<JcrNode>>> getNodeTree(String path) throws SerializedException {
		List<Map<String, List<JcrNode>>> returnList;
		try {
			if (null == path || path.equals("")) {
				path = "/";
			}
			String[] pathSplit = path.split("/");
			returnList = new ArrayList<Map<String, List<JcrNode>>>();
			Map<String, List<JcrNode>> treeAssociationMap = null;
			//iterate over each path element to gather and return the nodes at each level to be mapped to the tree on the front end
			StringBuffer pathBuilder = new StringBuffer();
			for (int i = 0; i < pathSplit.length; i++) {
				treeAssociationMap = new HashMap<String, List<JcrNode>>();
				pathBuilder.append(pathSplit[i].trim() + "/");
				treeAssociationMap.put(pathBuilder.toString(), getNode(pathBuilder.toString()));
				returnList.add(treeAssociationMap);
			}
		} catch (Exception e) {
			log.error("Failed fetching Node Tree. ", e);
			throw new SerializedException(e.getMessage());
		}

		return returnList;
	}

	private Value convertToValue(ValueFactory factory, JcrProperty prop, int idx) {
		switch (prop.getType()) {
			case PropertyType.BOOLEAN:
				return factory.createValue(prop.getBooleanValue(idx));
			case PropertyType.DATE:
				Calendar cal = Calendar.getInstance();
				cal.setTime(prop.getDateValue(idx));
				return factory.createValue(cal);
			case PropertyType.DECIMAL:
				return factory.createValue(prop.getDecimalValue(idx));
			case PropertyType.DOUBLE:
				return factory.createValue(prop.getDoubleValue(idx));
			case PropertyType.LONG:
				return factory.createValue(prop.getLongValue(idx));
			case PropertyType.NAME:
			case PropertyType.PATH:
			case PropertyType.STRING:
			case PropertyType.UNDEFINED:
			case PropertyType.URI:
			case PropertyType.BINARY:
			case PropertyType.WEAKREFERENCE:
				return factory.createValue(prop.getStringValue(idx));
			default:
				return null;
		}
	}
	
	private void setNodeProperty(Node node, String name, JcrProperty prop) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		if (prop.isMultiValue()) {
			Value[] values = new Value[prop.getSize()];
			for (int i=0; i<prop.getSize(); i++) {
				values[i] = convertToValue(node.getSession().getValueFactory(), prop, i);
			}
			node.setProperty(name, values);
		} else {
			node.setProperty(name, convertToValue(node.getSession().getValueFactory(), prop, 0));
		}
	}
	
	private JcrProperty convertProperty(javax.jcr.Property property, boolean readOnly) throws ValueFormatException, RepositoryException {
		JcrProperty prop = new JcrProperty();
		
		for (int i = 0; i<(property.isMultiple()?property.getValues().length:1); i++) {
			Value value;
			if (property.isMultiple()) {
				value = property.getValues()[i];
			} else {
				value = property.getValue();
			}
			
			prop.setType(property.getType());
			prop.setName(property.getName());
			prop.setReadOnly(readOnly);

			switch (value.getType()) {
				case PropertyType.BOOLEAN:
					prop.addBooleanValue(value.getBoolean());
					break;
				case PropertyType.DATE:
					prop.addDateValue(value.getDate().getTime());
					break;
				case PropertyType.DECIMAL:
					prop.addDecimalValue(value.getDecimal());
					break;
				case PropertyType.DOUBLE:
					prop.addDoubleValue(value.getDouble());
					break;
				case PropertyType.LONG:
					prop.addLongValue(value.getLong());
					break;
				case PropertyType.NAME:
				case PropertyType.PATH:
				case PropertyType.STRING:
				case PropertyType.UNDEFINED:
				case PropertyType.URI:
				case PropertyType.BINARY:
				case PropertyType.WEAKREFERENCE:
					prop.addStringValue(value.getString());
					break;
				default:
					break;
			}
		}
		return prop;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final Set<String> ignoredPropertyNames = new HashSet(Arrays.asList(new String[]{
		"jcr:primaryType",
		"jcr:mixinTypes"
	}));

	/**
	 * Used to gather a list of the children of the node at the given path (parent node)
	 * @see org.liveSense.jcr.explorer.service.JcrService#getNode(java.lang.String)
	 */
	@Override
	public List<JcrNode> getNode(String path) throws SerializedException {
		List<JcrNode> jcrNodeList;
		try {
			if (null == path || path.equals("")) {
				path = "/";
			}
			Item item = null;
			JcrNode newJcrNode = null;
			NodeIterator nodeIterator = null;
			PropertyIterator propertyIterator = null;
			Map<String, JcrProperty> properties;
			jcrNodeList = new ArrayList<JcrNode>();
			item = getJcrSession().getItem(path);
			if (null == item || !(item instanceof Node)) {
				return null;
			}
			Node pathNode = (Node) item;
			nodeIterator = pathNode.getNodes();
			for (int i = 0; i < nodeIterator.getSize(); i++) {
				Node node = nodeIterator.nextNode();
				propertyIterator = node.getProperties();
				properties = new HashMap<String, JcrProperty>();
				for (int j = 0; j < propertyIterator.getSize(); j++) {
					javax.jcr.Property property = propertyIterator.nextProperty();
					if (!ignoredPropertyNames.contains(property.getName())) {
						if (property.getName().startsWith("jcr:")) {
							properties.put(property.getName(), convertProperty(property, true));
						} else {
							properties.put(property.getName(), convertProperty(property, false));
						}
					}
				}
				if (node.getPath().toString().contains("[") && node.getPath().toString().contains("]")) {
					String nameWithBrackets = node.getPath().toString().substring( node.getPath().toString().lastIndexOf('/') + 1,  node.getPath().toString().length());
					newJcrNode = new JcrNode(nameWithBrackets, node.getPath().toString(), node.getPrimaryNodeType()
							.getName(), properties);
				} else {
					newJcrNode = new JcrNode(node.getName().toString(), node.getPath().toString(), node.getPrimaryNodeType()
							.getName(), properties);
				}
				if (node.getMixinNodeTypes() != null) {
					String[] mixinTypes = new String[node.getMixinNodeTypes().length];
					for (int j=0; j<node.getMixinNodeTypes().length; j++) {
						mixinTypes[j] = node.getMixinNodeTypes()[j].getName();
					}
					newJcrNode.setMixinTypes(mixinTypes);
				}

				jcrNodeList.add(newJcrNode);
			}
		} catch (Exception e) {
			log.error("Failed fetching Node. ", e);
			throw new SerializedException("Failed fetching Node. " + e.getMessage());
		}

		return jcrNodeList;
	}

	@Override
	public List<String> getAvailableNodeTypes() throws SerializedException {
		List<String> availableNodeTypeList;
		try {
			availableNodeTypeList = new ArrayList<String>();
			NodeTypeIterator nodeTypeIterator = getJcrSession().getWorkspace().getNodeTypeManager().getAllNodeTypes();
			for (int j = 0; j < nodeTypeIterator.getSize(); j++) {
				NodeType nodeType = nodeTypeIterator.nextNodeType();
				availableNodeTypeList.add(nodeType.getName());
			}
		} catch (Exception e) {
			log.error("Failed fetching available node types. ", e);
			throw new SerializedException(e.getMessage());
		}
		return availableNodeTypeList;
	}

	@Override
	public List<String> getMixinNodeTypes() throws SerializedException {
		List<String> mixinNodeTypeList;
		try {
			mixinNodeTypeList = new ArrayList<String>();
			NodeTypeIterator nodeTypeIterator = getJcrSession().getWorkspace().getNodeTypeManager().getAllNodeTypes();
			for (int j = 0; j < nodeTypeIterator.getSize(); j++) {
				NodeType nodeType = nodeTypeIterator.nextNodeType();
				if (nodeType.isMixin())
					mixinNodeTypeList.add(nodeType.getName());
			}
		} catch (Exception e) {
			log.error("Failed fetching available node types. ", e);
			throw new SerializedException(e.getMessage());
		}
		return mixinNodeTypeList;
	}

	/**
	 * Add new node and add mandatory jcr:content child node if the node type is a file type
	 */
	@Override
	public String addNewNode(String path, String newNodeName, String primaryNodeType, String[] mixinTypes, String jcrContentFileName, boolean cancel) throws SerializedException {
		// TODO Deprecated, because the standard uploading have use it
		if (cancel) {
//			deleteDirectory(new File(REAL_ABSOLUTE_PATH + TEMP_FILES + getThreadLocalRequest().getSession().getId()));
//			return "Removed files";
			return "Removed files";
		}
		if (null == path || path.equals("") || null == primaryNodeType || primaryNodeType.equals("")) {
			throw new SerializedException("New node not added.");
		}
		Node pathNode;
		Session session;
		try {
			Item item = null;
			session = getJcrSession();
			item = session.getItem(path);

			if (null == item || !(item instanceof Node)) {
				return null;
			}
			
			
			pathNode = (Node) item;
			Node newNode = pathNode.addNode(newNodeName, primaryNodeType);
			
			/*
			if (primaryNodeType.contains("file") || primaryNodeType.contains("File")) {
				Node resNode = newNode.addNode("jcr:content", "nt:resource");

				// TODO Servlet can handle!
				//Fixes: http://code.google.com/p/jackrabbitexplorer/issues/detail?id=19
				//Must ascertain MIME type.
				
				MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
				String mimeType = mimeTypesMap.getContentType(jcrContentFileName);

				resNode.setProperty ("jcr:mimeType", mimeType);
				resNode.setProperty ("jcr:encoding", "");

				resNode.setProperty ("jcr:data", getJcrSession().getValueFactory().createBinary(new FileInputStream (REAL_ABSOLUTE_PATH + TEMP_FILES +
						getThreadLocalRequest().getSession().getId() + "/" + jcrContentFileName)));
			}
			*/
			if (mixinTypes != null && mixinTypes.length > 0) {
				for (String mixinName : mixinTypes) newNode.addMixin(mixinName);
			}
			session.save();
		} catch (Exception e) {
			log.error("Node not added. ", e);
			throw new SerializedException("Node not added. " + e.getMessage());
		} finally {
			if (null != getThreadLocalRequest()) {
//				deleteDirectory(new File(REAL_ABSOLUTE_PATH + TEMP_FILES + getThreadLocalRequest().getSession().getId()));
			}
		}

		return "New node successfully created.";
	}

	
	@Override
	public String addMixinType(String path, String mixinType)
			throws SerializedException {
		if (null == path || path.equals("") || null == mixinType || mixinType.equals("")) {
			throw new SerializedException("Mixin type not added. ");
		}
		Node pathNode;
		Session session;
		try {
			Item item = null;
			session = getJcrSession();
			item = session.getItem(path);
			if (null == item || !(item instanceof Node)) {
				return null;
			}
			Node node = (Node)item;
			node.addMixin(mixinType);
			session.save();
		} catch (Exception e) {
			log.error("Mixin type not added. ", e);
			throw new SerializedException("Mixin type not added.  " + e.getMessage());
		} finally {
			if (null != getThreadLocalRequest()) {
//				deleteDirectory(new File(REAL_ABSOLUTE_PATH + TEMP_FILES + getThreadLocalRequest().getSession().getId()));
			}
		}
		return "Mixin type successfully added.";
	}

	@Override
	public String removeMixinType(String path, String mixinType)
			throws SerializedException {
		if (null == path || path.equals("") || null == mixinType || mixinType.equals("")) {
			throw new SerializedException("Mixin type not removed. ");
		}
		Node pathNode;
		Session session;
		try {
			Item item = null;
			session = getJcrSession();
			item = session.getItem(path);
			if (null == item || !(item instanceof Node)) {
				return null;
			}
			Node node = (Node)item;
			node.removeMixin(mixinType);
			session.save();
		} catch (Exception e) {
			log.error("Mixin type not removed. ", e);
			throw new SerializedException("Mixin type not removed. " + e.getMessage());
		} finally {
			if (null != getThreadLocalRequest()) {
//				deleteDirectory(new File(REAL_ABSOLUTE_PATH + TEMP_FILES + getThreadLocalRequest().getSession().getId()));
			}
		}
		return "Mixin type successfully removed.";
	}

	@Override
	public String moveNode(String sourcePath, String destinationPath) throws SerializedException {
		String sourceName;
		Session session;
		try {
			if (null == sourcePath || sourcePath.equals("") || null == destinationPath || destinationPath.equals("")) {
				throw new Exception("Node not moved.");
			}
			session = getJcrSession();
			int lastIndexOfSlash = sourcePath.lastIndexOf('/');
			sourceName = sourcePath.substring(lastIndexOfSlash + 1, sourcePath.length());
			if (sourceName.indexOf('[') >= 0) {
				sourceName = sourceName.substring(0, sourceName.indexOf('['));
			}
			if (destinationPath.equals("/")) {
				session.move(sourcePath, destinationPath + sourceName);
			} else {
				session.move(sourcePath, destinationPath + "/" + sourceName);
			}
			session.save();
		} catch (Exception e) {
			log.error("Node Not Moved. ", e);
			throw new SerializedException("Node Not Moved. " + e.getMessage());
		}

		return "Successfully moved. " + sourcePath + " to " + destinationPath;
	}

	@Override
	public String renameNode(String sourcePath, String newName) throws SerializedException {
		String oldName;
		String newPath;
		Session session;
		try {
			if (null == sourcePath || sourcePath.equals("") || null == newName || newName.equals("")) {
				throw new Exception("Node not renamed.");
			}
			session = getJcrSession();
			int lastIndexOfSlash = sourcePath.lastIndexOf('/');
			oldName = sourcePath.substring(lastIndexOfSlash + 1, sourcePath.length());
			newPath = sourcePath.substring(0, lastIndexOfSlash + 1) + newName;
			session.move(sourcePath, newPath);
			session.save();
		} catch (Exception e) {
			log.error("Node Not Renamed. ", e);
			throw new SerializedException("Node Not Renamed. " + e.getMessage());
		}

		return "Successfully renamed from " + oldName + " to " + newName;
	}

	@Override
	public String moveNodes(Map<String, String> nodeMap) throws SerializedException {
		// if (null == sourcePath || sourcePath.equals("") ||
		// null == destinationPath || destinationPath.equals("")) {
		// return null;
		// }
		// int lastIndexOfSlash = sourcePath.lastIndexOf('/');
		// String sourceName = sourcePath.substring(lastIndexOfSlash + 1,
		// sourcePath.length());
		// try {
		// getJcrSession().move(sourcePath, destinationPath + "/" + sourceName);
		// getJcrSession().save();
		// } catch (Exception e) {
		// return "Node Not Moved. " + e.getMessage();
		// }
		//		
		// return "Successfully Moved. " + sourcePath + " to " +
		// destinationPath;
		return null;
	}

	@Override
	public String cutAndPasteNode(String sourcePath, String destinationPath) throws SerializedException {
		try {
			if (null == sourcePath || sourcePath.equals("") || null == destinationPath || destinationPath.equals("")) {
				throw new Exception("Node not cut.");
			}
			copyNode(sourcePath, destinationPath);
			deleteNode(sourcePath);
		} catch (Exception e) {
			log.error("Node not cut. ", e);
			throw new SerializedException("Node not cut. " + e.getMessage());
		}

		return "Successfully cut and pasted " + sourcePath + " to " + destinationPath;
	}

	@Override
	public String copyNode(String sourcePath, String destinationPath) throws SerializedException {
		String sourceName;
		Session session;
		try {
			if (null == sourcePath || sourcePath.equals("") || null == destinationPath || destinationPath.equals("")) {
				throw new Exception("Node not copied.");
			}
			int lastIndexOfSlash = sourcePath.lastIndexOf('/');
			sourceName = sourcePath.substring(lastIndexOfSlash + 1, sourcePath.length());
			if (sourceName.indexOf('[') >= 0) {
				sourceName = sourceName.substring(0, sourceName.indexOf('['));
			}
			session = getJcrSession();
			if (destinationPath.equals("/")) {
				session.getWorkspace().copy(sourcePath, destinationPath + sourceName);
			} else {
				session.getWorkspace().copy(sourcePath, destinationPath + "/" + sourceName);
			}
			session.save();
		} catch (Exception e) {
			log.error("Node not copied. ", e);
			throw new SerializedException("Node not copied. " + e.getMessage());
		}

		return "Successfully copied " + sourcePath + " to " + destinationPath;
	}

	@Override
	public String copyNodes(Map<String, String> nodeMap) throws SerializedException {
		// if (null == sourcePath || sourcePath.equals("") ||
		// null == destinationPath || destinationPath.equals("")) {
		// return null;
		// }
		// int lastIndexOfSlash = sourcePath.lastIndexOf('/');
		// String sourceName = sourcePath.substring(lastIndexOfSlash + 1,
		// sourcePath.length());
		// try {
		// getJcrSession().getWorkspace().copy(sourcePath, destinationPath + "/"
		// +
		// sourceName);
		// getJcrSession().save();
		// } catch (Exception e) {
		// return "Node Not Copied. " + e.getMessage();
		// }
		//		
		// return "Successfully Copied. " + sourcePath + " to " +
		// destinationPath;
		return null;
	}

	@Override
	public String deleteNode(String sourcePath) throws SerializedException {
		if (null == sourcePath || sourcePath.equals("")) {
			throw new SerializedException("Node source missing");
		}
		Session session;
		try {
			session = getJcrSession();
			Item item = session.getItem(sourcePath);
			item.remove();
			session.save();
		} catch (Exception e) {
			log.error("Node not deleted. ", e);
			throw new SerializedException("Node not deleted. " + e.getMessage());
		}

		return "Successfully deleted. " + sourcePath;
	}

	//not used
	@Override
	public String saveNodeDetails(String sourcePath, JcrNode jcrNode) throws SerializedException {
		//		if (null == jcrNode) {
		//			throw new SerializedException("Details not saved.");
		//		}
		//		try {
		//			Item item = getJcrSession().getItem(sourcePath);
		//			if (null == item && !(item instanceof Node)) {
		//				return null;
		//			}
		//			Node pathNode = (Node) item;
		//			pathNode.setProperty("name", jcrNode.getName());
		//			pathNode.setProperty("path", jcrNode.getPath());
		//			pathNode.setProperty("primaryNodeType", jcrNode.getPrimaryNodeType());
		//			getJcrSession().save();
		//		} catch (Exception e) {
		//			log.error("Node details not saved. ", e);
		//			throw new SerializedException("Node details not saved. " + e.getMessage());
		//		}
		//
		//		return "Successfully saved. " + sourcePath;
		return "";
	}

	// Properties
	@Override
	public String addNewProperty(String sourcePath, String name, JcrProperty value) throws SerializedException {
		Session session = null;

		if (null == sourcePath || sourcePath.equals("")) {
			throw new SerializedException("Property not added.");
		}
		try {
			session = getJcrSession();
			Item item = session.getItem(sourcePath);
			if (null == item || !(item instanceof Node)) {
				return null;
			}
			Node pathNode = (Node) item;
			setNodeProperty(pathNode, name, value);

			session.save();
		} catch (Exception e) {
			log.error("Property not added. ", e);
			throw new SerializedException("Property not added. " + e.getMessage());
		}

		return "Successfully added new property at " + sourcePath;
	}

	@Override
	public String deleteProperty(String sourcePath, String name) throws SerializedException {
		if (null == sourcePath || sourcePath.equals("") || null == name || name.equals("")) {
			throw new SerializedException("Property not deleted.");
		}
		Session session;
		try {
			session = getJcrSession();
			Item item = session.getItem(sourcePath);
			if (null == item || !(item instanceof Node)) {
				return null;
			}
			Node pathNode = (Node) item;
			pathNode.getProperty(name).remove();

			session.save();
		} catch (Exception e) {
			log.error("Property not deleted. ", e);
			throw new SerializedException("Property not deleted. " + e.getMessage());
		}

		return "Successfully deleted " + name + " property at " + sourcePath;
	}

	@Override
	public String saveProperties(String sourcePath, JcrNode jcrNode) throws SerializedException {
		if (null == jcrNode) {
			throw new SerializedException("Properties not saved.");
		}
		Session session;
		try {
			session = getJcrSession();
			Item item = session.getItem(sourcePath);
			if (null == item  || !(item instanceof Node)) {
				return null;
			}
			
			Node pathNode = (Node) item;
			for (Iterator<Map.Entry<String, JcrProperty>> iterator = jcrNode.getProperties().entrySet().iterator(); iterator.hasNext();) {
				Map.Entry<String, JcrProperty> propertyPair = iterator.next();
				setNodeProperty(pathNode, propertyPair.getValue().getName(),  propertyPair.getValue());
			}

			session.save();
		} catch (Exception e) {
			log.error("Properties not saved. ", e);
			throw new SerializedException("Properties not saved. " + e.getMessage());
		}

		return "Successfully saved. " + sourcePath;
	}


	@Override
	public String saveProperty(String sourcePath, String property, JcrProperty value) throws SerializedException {
		if (null == sourcePath || null == property || null == value) {
			throw new SerializedException("Property not saved.");
		}
		Session session;
		try {
			session = getJcrSession();
			Item item = session.getItem(sourcePath);
			if (null == item && !(item instanceof Node)) {
				return null;
			}
			Node pathNode = (Node) item;
			setNodeProperty(pathNode, property, value);

			session.save();
		} catch (Exception e) {
			log.error("Property value not saved. ", e);
			throw new SerializedException("Property not saved. " + e.getMessage());
		}

		return "Successfully saved property " + sourcePath;
	}

	/**
	 * 
	 * @param sourcePath
	 * @param property
	 * @param value
	 * @return String success message
	 * @throws SerializedException
	 */
	public String savePropertyBinaryValue(String sourcePath, String property, InputStream inputStream) throws SerializedException {
		if (null == sourcePath || null == property || null == inputStream) {
			throw new SerializedException("Property not saved.");
		}
		Session session;
		try {
			session = getJcrSession();
			Item item = session.getItem(sourcePath);
			if (null == item && !(item instanceof Node)) {
				return null;
			}
			Node pathNode = (Node) item;
			pathNode.setProperty(property, session.getValueFactory().createBinary(inputStream));

			session.save();
		} catch (Exception e) {
			log.error("Binary Property not saved. ", e);
			throw new SerializedException("Property not saved. " + e.getMessage());
		}

		return "Successfully saved. " + sourcePath;
	}

	/*
	 * Search(non-Javadoc)
	 * @see com.priocept.jcr.client.JcrService#fullTextSearch(java.lang.String)
	 */
	@Override
	public List<String> fullTextSearch(String queryString) throws SerializedException {
		return searchHelper(queryString, FULL_TEXT_SEARCH);
	}

	@Override
	public List<String> xpathSearch(String queryString) throws SerializedException {
		return searchHelper(queryString, XPATH_SEARCH);
	}

	@Override
	public List<String> sqlSearch(String queryString) throws SerializedException {
		return searchHelper(queryString, SQL_SEARCH);
	}

	/**
	 *
	 * @param queryString
	 * @param searchType
	 * @return Search Results as String in a List
	 * @throws SerializedException
	 */
	protected List<String> searchHelper(String queryString, String searchType) throws SerializedException {
		if (null == queryString || null == searchType) {
			return null;
		}
		
		// TODO Update to SQL2
		List<String> results = new ArrayList<String>();
		Session session;
		try {
			session = getJcrSession();
			QueryManager qm = session.getWorkspace().getQueryManager();
			Query query = null;
			if (searchType.equals(FULL_TEXT_SEARCH)) {
				queryString = "//*[jcr:contains(., '" + queryString + "')]";
				query = qm.createQuery(queryString, Query.XPATH);
			} else if (searchType.equals(XPATH_SEARCH)) {
				query = qm.createQuery(queryString, Query.XPATH);
			} else if (searchType.equals(SQL_SEARCH)) {
				query = qm.createQuery(queryString, Query.SQL);
			}
			QueryResult queryResult = query.execute();
			NodeIterator resultIterator = queryResult.getNodes();
			while (resultIterator.hasNext()) {
				Node node = resultIterator.nextNode();
				results.add(node.getPath());
			}
		} catch (Exception e) {
			log.error("Search Failed. queryString='" + queryString + "' searchType='" + searchType + "' "+e.getMessage());
			throw new SerializedException(e.getMessage());
		}
		return results;
	}

	/*
	 * Management of node types(non-Javadoc)
	 */
	@Override
	public Boolean addNodeTypes(String cnd) throws SerializedException
	{
		//Convert cnd String to InputStream
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(cnd.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error("Failed to register node types", e);
			throw new SerializedException(e.getMessage());
		}

		try {
			Session session = getJcrSession();
			NodeTypeLoader.registerNodeType(session, is);
			session.save();
		} catch (IOException e) {
			log.error("Failed to register node types", e);
			throw new SerializedException(e.getMessage());
		} catch (RepositoryException e) {
			log.error("Failed to register node types", e);
			throw new SerializedException(e.getMessage());
		} catch (Exception e) {
			log.error("Failed to register node types", e);
			throw new SerializedException(e.getMessage());
		}

		return true;
	}

	@Override
	public List<RemoteFile> getPossibleIconPaths(String path) throws SerializedException {

		if(path == null) {
			path = JcrServiceImplParameterProvider.getIconPath(context);
		}
		List<RemoteFile> children = new ArrayList<RemoteFile>();
		try {
			NodeIterator iter = getJcrSession().getNode(path).getNodes();
			while (iter.hasNext()) {
				Node n = iter.nextNode();
				RemoteFile remoteFile = new RemoteFile(n.getPath(), false);
				children.add(remoteFile);
			}
		} catch (RepositoryException e) {
			log.error("Could not get path: "+path, e);
		} catch (Exception e) {
			log.error("Could not get path: "+path, e);
		}
		
		return children;
	}

	@Override
	public Boolean changeNodeTypeIconAssociation(String nodeType, String iconPath) throws SerializedException {
		return false;
		/*
		Properties properties = new Properties();
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(getServletContext().getRealPath(File.separator + "WEB-INF") + File.separator + NODETYPE_ICONS_PROPERTIES_FILE);
			properties.load(inputStream);
			outputStream = new FileOutputStream(getServletContext().getRealPath(File.separator + "WEB-INF") + File.separator + NODETYPE_ICONS_PROPERTIES_FILE);

			//Correct iconpath to be a URL path
			iconPath = iconPath.replaceAll("\\\\", "\\/");

			properties.setProperty(nodeType, iconPath);			
			properties.store(outputStream, null);
		} catch (FileNotFoundException e) {
			log.error("Unable to find " + NODETYPE_ICONS_PROPERTIES_FILE + " : ", e);
			throw new SerializedException(e.getMessage());
		} catch (IOException e) {
			log.error("Exception reading/writing " + NODETYPE_ICONS_PROPERTIES_FILE + " : ", e);
			throw new SerializedException(e.getMessage());
		}
		finally{
			if(inputStream != null) {
				try {
					inputStream.close();
				}
				catch(Exception e) {
					log.error("Exception closing input stream from " + NODETYPE_ICONS_PROPERTIES_FILE, e);
				}
			}
			if(outputStream != null) {
				try {
					outputStream.close();
				}
				catch(Exception e) {
					log.error("Exception closing output stream to " + NODETYPE_ICONS_PROPERTIES_FILE, e);
				}
			}
		}
		return true;
		*/
	}

	@Override
	public void callInit() throws Throwable {
	}

	@Override
	public void callFinal() throws Throwable {
	}

}
