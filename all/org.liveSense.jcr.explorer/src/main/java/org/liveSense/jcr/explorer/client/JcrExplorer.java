package org.liveSense.jcr.explorer.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.liveSense.jcr.explorer.client.callback.AvailableNodeTypesServiceCallback;
import org.liveSense.jcr.explorer.client.callback.CRUDServiceCallback;
import org.liveSense.jcr.explorer.client.callback.ChangeNodeTypeIconAssociationCallback;
import org.liveSense.jcr.explorer.client.callback.GetBrowsableContentFilterRegexsServiceCallback;
import org.liveSense.jcr.explorer.client.callback.GetNodeServiceCallback;
import org.liveSense.jcr.explorer.client.callback.GetNodeTreeServiceCallback;
import org.liveSense.jcr.explorer.client.callback.GetNodeTypeIconsServiceCallback;
import org.liveSense.jcr.explorer.client.callback.NewBooleanCallback;
import org.liveSense.jcr.explorer.client.callback.RemoteFileServiceCallback;
import org.liveSense.jcr.explorer.client.handler.IconGridHandler;
import org.liveSense.jcr.explorer.client.ui.AddNewNode;
import org.liveSense.jcr.explorer.client.ui.Details;
import org.liveSense.jcr.explorer.client.ui.EditProperty;
import org.liveSense.jcr.explorer.domain.JcrProperty;
import org.liveSense.jcr.explorer.domain.JcrPropertyListGridRecord;
import org.liveSense.jcr.explorer.domain.JcrTreeNode;
import org.liveSense.jcr.explorer.domain.PropertyDataTypes;
import org.liveSense.jcr.explorer.service.JcrService;
import org.liveSense.jcr.explorer.service.JcrServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.LayoutResizeBarPolicy;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.CellMouseDownHandler;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemSeparator;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * 
 * @author James Pickup
 *
 */
public class JcrExplorer implements EntryPoint {
	public final static String BINARY_SERVLET_PATH = "/jackrabbitexplorer/BinaryServlet?path=";
	public final static String UPLOAD_SERVLET_PATH = "/jackrabbitexplorer/UploadServlet";
	
	public JcrExplorer() {
		super();
	}

	public final static String NT_UNSTRUCTURED = "nt:unstructured";
	public static String browsableContentFilterRegex = "";
	public static final String defaultIcon = "icons/folder_open.png";
	public JcrTreeNode jcrRoot = new JcrTreeNode("", "", NT_UNSTRUCTURED, null, new HashMap<String, JcrProperty>(), 
			new JcrTreeNode("root", "/", NT_UNSTRUCTURED, null, new HashMap<String, JcrProperty>(), 
					defaultIcon));
	public Tree jcrTree = new Tree();
	public TreeGrid jcrTreeGrid = new TreeGrid();
	private final HLayout layout = new HLayout();
	public static JcrServiceAsync service;
	public TabSet bottomRightTabSet = new TabSet();
	private final ListGrid propertiesListGrid = new ListGrid();  
	private JcrPropertyListGridRecord[] propertiesListGridRecords = new JcrPropertyListGridRecord[]{};
	public ListGrid searchResultsListGrid = new ListGrid();
	private final ListGrid remoteIconFilesListGrid = new ListGrid();
	private final Window possibleIconsWindow = createPossibleIconsWindow(remoteIconFilesListGrid);
	private String sourcePath = "";
	private String destinationPath = "";
	private String copyCellPath = null;
	private String cutCellPath = null;
	private String changeIconCellType = null;
	private String deleteCellPath = null;
	public static TreeGrid cellMouseDownTreeGrid;
	public static Window loginWindow = new Window();
	public static String loadingImgPath = "loading/loading.gif";
	public static Img loadingImg = new Img(loadingImgPath);
	public static HLayout disabledHLayout = new HLayout();
	public static List<Map<String, String>> customNodeList = null;
	public static void setCustomTreeIcon(TreeNode treeNode, String primaryNodeType) {
		if (null != customNodeList) {//rep:system nt:folder
			for (Map<String, String> property : customNodeList) {
				if (property.containsKey(primaryNodeType)) {
					treeNode.setAttribute("treeGridIcon", property.get(primaryNodeType));
				}
			}
		}

	}

	public ListGrid getRemoteIconFilesListGrid() {
		return remoteIconFilesListGrid;
	}

	private static HLayout mainLayout = new HLayout();
	@Override
	public void onModuleLoad() {
		try {
			service = (JcrServiceAsync) GWT.create(JcrService.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) service;
			endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "JcrService");
		} catch (Exception e) {
			SC.warn("There was an error: " + e.getMessage(), new NewBooleanCallback());
		}
		getNodeTypeIcons();
		service.getBrowsableContentFilterRegex(new GetBrowsableContentFilterRegexsServiceCallback());
		service.getAvailableNodeTypes(new AvailableNodeTypesServiceCallback());

		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setBackgroundColor("#F0F0F0");
		mainLayout.draw();
		hideLoadingImg();
		drawMainLayout();
	}

	public void getNodeTypeIcons() {
		service.getNodeTypeIcons(new GetNodeTypeIconsServiceCallback());
	}

	public void drawMainLayout() {
		mainLayout.setLayoutMargin(15);
		Label navigationLabel = new Label();
		navigationLabel.setAlign(Alignment.CENTER);
		navigationLabel.setOverflow(Overflow.HIDDEN);
		navigationLabel.setWidth("40%");
		navigationLabel.setShowResizeBar(true);
//		navigationLabel.setBorder("1px solid blue");
		mainLayout.addMember(navigationLabel);
		VLayout vLayout = new VLayout();
		vLayout.setWidth("60%");
		Label topRightLabel = new Label();
		topRightLabel.setAlign(Alignment.CENTER);
		topRightLabel.setOverflow(Overflow.HIDDEN);
		topRightLabel.setHeight("185");
		topRightLabel.setShowResizeBar(true);
//		topRightLabel.setBorder("1px solid blue");
		Label bottonRightLabel = new Label();
		bottonRightLabel.setAlign(Alignment.CENTER);
		bottonRightLabel.setOverflow(Overflow.HIDDEN);
		bottonRightLabel.setHeight("100%");
//		bottonRightLabel.setBorder("1px solid blue");
		navigationLabel.addChild(createNavigation());
		topRightLabel.addChild(new Details().createDetails(this));
		bottomRightTabSet.setWidth100();
		bottomRightTabSet.setHeight100();
		bottomRightTabSet.setTabs(createPropertiesTab(), createSearchResultsTab());
		bottonRightLabel.addChild(bottomRightTabSet);
		vLayout.addMember(topRightLabel);
		vLayout.addMember(bottonRightLabel);
		vLayout.setAlign(VerticalAlignment.CENTER);
		mainLayout.addMember(vLayout);
	}

	public static void showLoadingImg() {
		showDisableLayer();
		loadingImg.setSize("100px","100px");
		loadingImg.setTop(mainLayout.getHeight()/2 - 50); //loading image height is 50px
		loadingImg.setLeft(mainLayout.getWidth()/2 - 50); //loading image width is 50px
		loadingImg.show();
		loadingImg.bringToFront();
	}

	public static void showDisableLayer() {
		disabledHLayout.setSize("100%", "100%");
		disabledHLayout.setStyleName("disabledBackgroundStyle");
		disabledHLayout.show();
	}

	public static void hideDisableLayer() {
		disabledHLayout.hide();
	}

	public static void hideLoadingImg() {
		loadingImg.hide();
		hideDisableLayer();
	}

	public void refreshFromRoot() {
		treeRecordClick(null, true, "");
	}

	public void treeRecordClick(TreeGrid selectedTreeGrid, boolean refresh, String parentPath) {
		showLoadingImg();
		JcrTreeNode selectedAnimateTreeNode = jcrRoot;
		if (null != selectedTreeGrid && null == parentPath) {
			selectedAnimateTreeNode = (JcrTreeNode) selectedTreeGrid.getSelectedRecord();
		}
		if (null != parentPath) {
			selectedAnimateTreeNode = (JcrTreeNode) jcrTree.find("/root" + parentPath);
			if (null == selectedAnimateTreeNode) {
				if (null != parentPath && !parentPath.equals("/")) {
					service.getNodeTree(parentPath, new GetNodeTreeServiceCallback(this));
				} else {
					service.getNode(parentPath, new GetNodeServiceCallback(this, parentPath));
				}
				Details.nameTxt.setValue("");
				Details.pathTxt.setValue("");
				Details.primaryType.setValue("");
				Details.mixinNodeTypes.setMixinTypes(null);

				return;
			}
		}
		if (selectedAnimateTreeNode.getTitle().contains("[")) {
			Details.nameTxt.setValue(selectedAnimateTreeNode.getTitle().substring(0, selectedAnimateTreeNode.getTitle().lastIndexOf("[")));
		} else {
			Details.nameTxt.setValue(selectedAnimateTreeNode.getTitle());
		}
		Details.pathTxt.setValue(selectedAnimateTreeNode.getAttribute("path"));
		Details.primaryType.setValue(selectedAnimateTreeNode.getAttribute("primaryNodeType"));
		Details.mixinNodeTypes.setMixinTypes(selectedAnimateTreeNode.getMixinNodeTypes());
		if (selectedAnimateTreeNode.getAttribute("path").equals("/")) {
			Details.nameTxt.setDisabled(true);
		} else {
			Details.nameTxt.setDisabled(false);
		}
		
		jcrTree.openFolder(selectedAnimateTreeNode);
		if ((null != selectedAnimateTreeNode
				&& selectedAnimateTreeNode.getAttribute("children").length() < 1) || refresh) {
			if (refresh) {
				TreeNode[] selectedTreeNodeChildren = jcrTree.getChildren(selectedAnimateTreeNode);
				for (int j = 0; j < selectedTreeNodeChildren.length; j++) {
					TreeNode treeNode = selectedTreeNodeChildren[j];
					jcrTree.remove(treeNode);
				}
			}
			if (null != parentPath && !parentPath.equals("/")) {
				service.getNodeTree(parentPath, new GetNodeTreeServiceCallback(this));
			} else {
				service.getNode(selectedAnimateTreeNode.getAttribute("path"), new GetNodeServiceCallback(this, parentPath));
			}
		} else {
			hideLoadingImg();
		}
		Iterator<Map.Entry<String, JcrProperty>> it = selectedAnimateTreeNode.getProperties().entrySet().iterator();
		JcrPropertyListGridRecord listGridRecord;
		propertiesListGridRecords = new JcrPropertyListGridRecord[selectedAnimateTreeNode.getProperties().size()];
		int i = 0;
		
		// TODO : Value conversion
		while (it.hasNext()) {
			Map.Entry<String, JcrProperty> pairs = it.next();
			listGridRecord = new JcrPropertyListGridRecord();
			if (pairs.getKey().contains("jcr:data")) {
				listGridRecord.setAttribute("property", "<b>" + pairs.getKey() + "</b>");
				listGridRecord.setAttribute("multiValue", false);
				listGridRecord.setAttribute("type", PropertyDataTypes.BINARY.getTypeName());
				listGridRecord.setAttribute("value", "<b>" + pairs.getValue().getBinaryLink() + "</b>");
			} else {
				listGridRecord.setProperty(pairs.getValue());
				listGridRecord.setAttribute("property", pairs.getKey());
				listGridRecord.setAttribute("multiValue", pairs.getValue().isMultiValue());
				listGridRecord.setAttribute("type", PropertyDataTypes.getTypeByTypeNum(pairs.getValue().getType()).getTypeName());
				StringBuilder sb = new StringBuilder();
				for (int j=0; j<pairs.getValue().getSize(); j++) {
					if (j > 0) sb.append(" | ");
					PropertyDataTypes type = PropertyDataTypes.getTypeByTypeNum(pairs.getValue().getType());
					JcrProperty property = pairs.getValue();
					if (type == PropertyDataTypes.BOOLEAN) {
						sb.append(property.getBooleanValue().toString());
					} else if (type == PropertyDataTypes.DATE) {
						sb.append(property.getDateValue().toString());
					} else if (type == PropertyDataTypes.DECIMAL) {
						sb.append(property.getDecimalValue().toString());
					} else if (type == PropertyDataTypes.DOUBLE) {
						sb.append(property.getDoubleValue().toString());
					} else if (type == PropertyDataTypes.LONG) {
						sb.append(property.getLongValue().toString());
					} else {
						sb.append(property.getStringValue());
					}
				}
				listGridRecord.setAttribute("value", sb.toString());
			}
			propertiesListGridRecords[i] = listGridRecord;
			i++;
		}
		propertiesListGrid.setData(propertiesListGridRecords);
		Details.addNodeSubmitItem.setDisabled(false);
		Details.addPropertySubmitItem.setDisabled(false);	    
		Details.addMixinTypeSubmitItem.setDisabled(false);

	}

	public void treeDeleteUpdate(String parentPath) {
		showLoadingImg();
		JcrTreeNode selectedAnimateTreeNode = jcrRoot;

		if (null != parentPath) {
			selectedAnimateTreeNode = (JcrTreeNode) jcrTree.find("/root" + parentPath);
		}
		Details.nameTxt.setValue("");
		Details.pathTxt.setValue(parentPath);
		Details.primaryType.setValue("");
		Details.mixinNodeTypes.setMixinTypes(null);

		if (parentPath.equals("/")) {
			Details.nameTxt.setDisabled(true);
		} else {
			Details.nameTxt.setDisabled(false);
		}
		jcrTree.openFolder(selectedAnimateTreeNode);
		if (null != selectedAnimateTreeNode) {
			TreeNode[] selectedTreeNodeChildren = jcrTree.getChildren(selectedAnimateTreeNode);
			for (int j = 0; j < selectedTreeNodeChildren.length; j++) {
				TreeNode treeNode = selectedTreeNodeChildren[j];
				jcrTree.remove(treeNode);
			}
			//service.getNode(selectedAnimateTreeNode.getAttribute("path"), new GetNodeServiceCallback(this, parentPath));
		}
		propertiesListGridRecords = new JcrPropertyListGridRecord[0];
		propertiesListGrid.setData(propertiesListGridRecords);
	}


	class JcrTreeGridCellClickHandler implements CellClickHandler {
		@Override
		public void onCellClick(CellClickEvent event) {
			treeRecordClick((TreeGrid) event.getSource(), false, null);
		}
	};

	class JcrTreeDropHandler implements DropHandler {
		private final JcrExplorer jackrabbitExplorer;
		public JcrTreeDropHandler(JcrExplorer jackrabbitExplorer) {
			this.jackrabbitExplorer = jackrabbitExplorer;
		}
		@Override
		public void onDrop(com.smartgwt.client.widgets.events.DropEvent event) {
			TreeGrid dropToTreeGrid =(TreeGrid) event.getSource();
			TreeGrid sourceTreeGrid = cellMouseDownTreeGrid;
			sourcePath = sourceTreeGrid.getSelectedRecord().getAttribute("path");
			destinationPath = dropToTreeGrid.getRecord(dropToTreeGrid.getEventRow()).getAttribute("path");
			SC.confirm("Proceed with moving " + sourcePath + " to " + 
					destinationPath, new BooleanCallback() {  
				@Override
				public void execute(Boolean value) {  
					if (value != null && !value.equals("")) {  
						showLoadingImg();
						service.moveNode(sourcePath, destinationPath, new CRUDServiceCallback(jackrabbitExplorer, destinationPath, sourcePath));
					} else {  
						return;
					}  
				}  
			});  
			jcrTreeGrid.deselectAllRecords();
		}
	}

	class JcrTreeCellMouseDownHandler implements CellMouseDownHandler {
		@Override
		public void onCellMouseDown(com.smartgwt.client.widgets.grid.events.CellMouseDownEvent event) {
			TreeGrid dropToTreeGrid =(TreeGrid) event.getSource();
			dropToTreeGrid.getTreeFieldTitle();
			cellMouseDownTreeGrid = dropToTreeGrid;
		}
	}

	private TreeGrid createNavigation() {
		jcrTree.setModelType(TreeModelType.PARENT);
		jcrTree.setNameProperty("title");
		jcrTree.setRoot(jcrRoot);
		jcrTreeGrid.setData(jcrTree);
		jcrTreeGrid.setWidth100();
		jcrTreeGrid.setHeight100();
		jcrTreeGrid.setCanDragResize(true);
		jcrTreeGrid.setAnimateFolders(true);
		jcrTreeGrid.setAnimateFolderSpeed(450);
		jcrTreeGrid.setCustomIconProperty("treeGridIcon");
		// TODO Selectable workspaces
		jcrTreeGrid.setTreeFieldTitle("Workspace: Default");
		jcrTreeGrid.setCanReorderRecords(true);  
		jcrTreeGrid.setCanAcceptDroppedRecords(true);  
		jcrTreeGrid.setCanDragRecordsOut(true);
		jcrTreeGrid.setShowConnectors(true);
		jcrTreeGrid.setSelectionType(SelectionStyle.SINGLE);
		jcrTreeGrid.setContextMenu(createRightClickMenu());
		jcrTreeGrid.addCellMouseDownHandler(new JcrTreeCellMouseDownHandler());
		jcrTreeGrid.addDropHandler(new JcrTreeDropHandler(this));
		layout.setCanDragResize(true);
		layout.setMembersMargin(10);
		layout.addChild(jcrTreeGrid);
		jcrTreeGrid.addCellClickHandler(new JcrTreeGridCellClickHandler());
		return jcrTreeGrid;
	}

	private Menu createRightClickMenu() {
		Menu rightClickMenu = new Menu();
		MenuItem newMenuItem = new MenuItem("Add New Node", "icons/icon_add_files.png", "Ctrl+N");
		class NewClickHandler implements com.smartgwt.client.widgets.menu.events.ClickHandler {
			JcrExplorer jackrabbitExplorer = null;
			NewClickHandler(JcrExplorer jackrabbitExplorer) {
				this.jackrabbitExplorer = jackrabbitExplorer;
			}
			@Override
			public void onClick(com.smartgwt.client.widgets.menu.events.MenuItemClickEvent event) {
				new AddNewNode().addNewNodeBox(jackrabbitExplorer);
			}
		};
		newMenuItem.addClickHandler(new NewClickHandler(this));

		MenuItem changeIconMenuItem = new MenuItem("Change Icon for Type", "icons/pencil.png", "Ctrl+I");
		class ChangeClickHandler implements com.smartgwt.client.widgets.menu.events.ClickHandler {
			JcrExplorer jackrabbitExplorer = null;
			ChangeClickHandler(JcrExplorer jackrabbitExplorer) {
				this.jackrabbitExplorer = jackrabbitExplorer;
			}
			@Override
			public void onClick(com.smartgwt.client.widgets.menu.events.MenuItemClickEvent event) {
				TreeGrid selectedJcrTreeGrid = (TreeGrid) event.getTarget();				
				changeIconCellType = selectedJcrTreeGrid.getSelectedRecord().getAttribute("primaryNodeType");
				showLoadingImg();
				jackrabbitExplorer.showPossibleIcons(null);
			}
		};
		changeIconMenuItem.addClickHandler(new ChangeClickHandler(this));

		MenuItem cutMenuItem = new MenuItem("Cut", "icons/cut.png", "Ctrl+X");
		class cutClickHandler implements com.smartgwt.client.widgets.menu.events.ClickHandler {
			@Override
			public void onClick(com.smartgwt.client.widgets.menu.events.MenuItemClickEvent event) {
				TreeGrid selectedJcrTreeGrid = (TreeGrid) event.getTarget();
				cutCellPath = selectedJcrTreeGrid.getSelectedRecord().getAttribute("path");
				selectedJcrTreeGrid.removeSelectedData();
			}
		};
		cutMenuItem.addClickHandler(new cutClickHandler());
		MenuItem copyMenuItem = new MenuItem("Copy", "icons/copy.png", "Ctrl+C");
		class copyClickHandler implements com.smartgwt.client.widgets.menu.events.ClickHandler {
			@Override
			public void onClick(com.smartgwt.client.widgets.menu.events.MenuItemClickEvent event) {
				TreeGrid selectedJcrTreeGrid = (TreeGrid) event.getTarget();
				copyCellPath = selectedJcrTreeGrid.getSelectedRecord().getAttribute("path");
			}
		};
		copyMenuItem.addClickHandler(new copyClickHandler());
		MenuItem pasteMenuItem = new MenuItem("Paste", "icons/paste.png", "Ctrl+P");
		class PasteClickHandler implements com.smartgwt.client.widgets.menu.events.ClickHandler {
			private final JcrExplorer jackrabbitExplorer;
			public PasteClickHandler(JcrExplorer jackrabbitExplorer) {
				this.jackrabbitExplorer = jackrabbitExplorer;
			}
			@Override
			public void onClick(com.smartgwt.client.widgets.menu.events.MenuItemClickEvent event) {
				TreeGrid selectedJcrTreeGrid = (TreeGrid) event.getTarget();
				String copyToPath = selectedJcrTreeGrid.getSelectedRecord().getAttribute("path");
				if (null != copyCellPath) {
					showLoadingImg();
					service.copyNode(copyCellPath, copyToPath, new CRUDServiceCallback(jackrabbitExplorer, copyToPath, null));
					copyCellPath = null;
				} else if (null != cutCellPath) {
					showLoadingImg();
					service.cutAndPasteNode(cutCellPath, copyToPath, new CRUDServiceCallback(jackrabbitExplorer, copyToPath, cutCellPath));
					cutCellPath = null;
				} else {
					SC.say("Nothing to paste.");
					return;
				}
			}
		};
		pasteMenuItem.addClickHandler(new PasteClickHandler(this));
		MenuItem refreshMenuItem = new MenuItem("Refresh", "icons/refresh.png", "Ctrl+R");
		class RefreshClickHandler implements com.smartgwt.client.widgets.menu.events.ClickHandler {
			@Override
			public void onClick(com.smartgwt.client.widgets.menu.events.MenuItemClickEvent event) {
				TreeGrid selectedJcrTreeGrid = (TreeGrid) event.getTarget();
				treeRecordClick(selectedJcrTreeGrid, true, null);
			}
		};
		refreshMenuItem.addClickHandler(new RefreshClickHandler());
		MenuItem deleteMenuItem = new MenuItem("Delete", "icons/icon_remove_files.png");
		class DeleteClickHandler implements com.smartgwt.client.widgets.menu.events.ClickHandler {
			private final JcrExplorer jackrabbitExplorer;
			public DeleteClickHandler(JcrExplorer jackrabbitExplorer) {
				this.jackrabbitExplorer = jackrabbitExplorer;
			}
			@Override
			public void onClick(com.smartgwt.client.widgets.menu.events.MenuItemClickEvent event) {
				TreeGrid selectedJcrTreeGrid = (TreeGrid) event.getTarget();
				deleteCellPath = selectedJcrTreeGrid.getSelectedRecord().getAttribute("path");
				SC.confirm("Proceed with deleting " + deleteCellPath, new BooleanCallback() {  
					@Override
					public void execute(Boolean value) {  
						if (value != null && value) {  
							showLoadingImg();
							service.deleteNode(deleteCellPath, new CRUDServiceCallback(jackrabbitExplorer, null, deleteCellPath));
						} else {  
							return;
						}  
					}  
				});  
			}
		};
		deleteMenuItem.addClickHandler(new DeleteClickHandler(this));
		rightClickMenu.setItems(newMenuItem, new MenuItemSeparator(), refreshMenuItem, new MenuItemSeparator(),
				cutMenuItem, copyMenuItem, pasteMenuItem, new MenuItemSeparator(), changeIconMenuItem, new MenuItemSeparator(), deleteMenuItem);
		return rightClickMenu;
	}

	public void navigateTo(String path) {
		showLoadingImg();
		service.getNodeTree(path, new GetNodeTreeServiceCallback(this));
	}

	private Tab createSearchResultsTab() {
		Tab searchResultsTab = new Tab();
		searchResultsTab.setTitle("Search Results");
		searchResultsListGrid.setWidth(500);  
		searchResultsListGrid.setHeight(224);  
		searchResultsListGrid.setAlternateRecordStyles(true);  
		searchResultsListGrid.setShowAllRecords(true);  
		searchResultsListGrid.setCanEdit(false);  
		searchResultsListGrid.setEditByCell(false); 
		searchResultsListGrid.setShowHover(true);
		ListGridField pathField = new ListGridField("path", "Path");  
		pathField.setShowHover(true);
		searchResultsListGrid.setFields(pathField);  
		searchResultsListGrid.setCanResizeFields(true);  
		searchResultsListGrid.setWidth100();
		searchResultsListGrid.setHeight100();
		ListGridRecord listGridRecord = new ListGridRecord(); 
		searchResultsListGrid.setData(new ListGridRecord[] {listGridRecord});
		searchResultsListGrid.addClickHandler(new SearchResultsClickClickHandler(this));
		searchResultsTab.setPane(searchResultsListGrid);
		return searchResultsTab;
	}

	private Window createPossibleIconsWindow(ListGrid listGrid) {
		Window changeNodeTypeWindow = new Window();
		changeNodeTypeWindow.setShowMinimizeButton(false);  
		changeNodeTypeWindow.setIsModal(true);  
		changeNodeTypeWindow.setShowModalMask(true);  
		changeNodeTypeWindow.setTitle("Change Node Type Icon");
		changeNodeTypeWindow.setCanDragReposition(true);
		changeNodeTypeWindow.setCanDragResize(false);
		changeNodeTypeWindow.setAutoCenter(true);
		changeNodeTypeWindow.setWidth(500);
		changeNodeTypeWindow.setHeight(400);
		changeNodeTypeWindow.setAlign(VerticalAlignment.BOTTOM);

		Layout verticalStack = new VStack();
		verticalStack.setHeight100();
		verticalStack.setWidth100();
		verticalStack.setPadding(10);
		verticalStack.setAlign(VerticalAlignment.BOTTOM);

		listGrid.setHeight(300);  
		listGrid.setAlternateRecordStyles(true);  
		listGrid.setShowAllRecords(true);  
		listGrid.setCanEdit(false);  
		listGrid.setEditByCell(false); 
		listGrid.setShowHover(true);
		ListGridField iconField = new ListGridField("imagePath", "Icon");
		iconField.setAlign(Alignment.CENTER);  
		iconField.setType(ListGridFieldType.IMAGE);
		iconField.setImageURLPrefix("");
		iconField.setWidth("40");
		ListGridField pathField = new ListGridField("path", "Path");  
		pathField.setShowHover(true);
		listGrid.setFields(iconField, pathField);  
		listGrid.setCanResizeFields(false);  
		listGrid.setWidth100();
		listGrid.addCellClickHandler(new IconGridHandler(this));

		HStack horizontalStack = new HStack();
		//horizontalStack.setHeight(50);
		horizontalStack.setAutoHeight();
		horizontalStack.setWidth100();
		horizontalStack.setPadding(10);
		horizontalStack.setAlign(Alignment.RIGHT);

		Button cancelButton = new Button("Cancel");

		class CancelClickHandler implements com.smartgwt.client.widgets.events.ClickHandler {
			JcrExplorer jackrabbitExplorer = null;
			CancelClickHandler(JcrExplorer jackrabbitExplorer) {
				this.jackrabbitExplorer = jackrabbitExplorer;
			}
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				jackrabbitExplorer.hidePossibleIconsWindow();
			}
		};

		cancelButton.addClickHandler(new CancelClickHandler(this));
		horizontalStack.addMember(cancelButton);

		verticalStack.addMember(listGrid);
		verticalStack.addMember(horizontalStack);

		changeNodeTypeWindow.addItem(verticalStack);
		return changeNodeTypeWindow;
	}

	public void showPossibleIconsWindow() {
		possibleIconsWindow.show();
	}

	public void hidePossibleIconsWindow() {
		possibleIconsWindow.hide();
	}

	class SearchResultsClickClickHandler implements com.smartgwt.client.widgets.events.ClickHandler {
		private final JcrExplorer jackrabbitExplorer;
		public SearchResultsClickClickHandler(JcrExplorer jackrabbitExplorer) {
			this.jackrabbitExplorer = jackrabbitExplorer;
		}
		@Override
		public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
			ListGrid selectedSearchResultTreeGrid = (ListGrid) event.getSource();
			showLoadingImg();
			service.getNodeTree(selectedSearchResultTreeGrid.getSelectedRecord().getAttribute("path"), new GetNodeTreeServiceCallback(jackrabbitExplorer));
		}
	}

	private Tab createPropertiesTab() {
		Tab propertiesTab = new Tab();
		propertiesTab.setTitle("Node Properties");
		propertiesListGrid.setWidth(500);  
		propertiesListGrid.setHeight(224);  
		propertiesListGrid.setAlternateRecordStyles(true);  
		propertiesListGrid.setShowAllRecords(true);  
		propertiesListGrid.setCanEdit(true);  
		propertiesListGrid.setEditEvent(ListGridEditEvent.CLICK);  
		propertiesListGrid.setEditByCell(true); 

		ListGridField propertyField = new ListGridField("property", "Property");  
		propertyField.setCanEdit(false);
		propertyField.setShowHover(true);

		ListGridField typeField = new ListGridField("type", "Type");  
		typeField.setWidth(60);
		typeField.setCanEdit(false);
		typeField.setShowHover(true);

		ListGridField multiValueField = new ListGridField("multiValue", "Multi Value");  
		typeField.setWidth(60);
		multiValueField.setCanEdit(false);
		multiValueField.setShowHover(true);

		ListGridField valueField = new ListGridField("value", "Value");  
		valueField.setShowHover(true);
		
		propertiesListGrid.setFields(propertyField, typeField, multiValueField, valueField);  
		propertiesListGrid.setCanResizeFields(true);  
		propertiesListGrid.setWidth100();
		propertiesListGrid.setHeight100();
		ListGridRecord listGridRecord = new ListGridRecord(); 
		propertiesListGrid.setData(new ListGridRecord[] {listGridRecord});
		propertiesListGrid.addCellSavedHandler(new PropertiesCellSavedHandler(this));
		propertiesListGrid.addCellClickHandler(new PropertiesCellClickHandler());
		propertiesListGrid.setContextMenu(createPropertyRightClickMenu());
		propertiesTab.setPane(propertiesListGrid);
		return propertiesTab;
	}

	class PropertiesCellSavedHandler implements CellSavedHandler   {  
		private final JcrExplorer jackrabbitExplorer;
		public PropertiesCellSavedHandler(JcrExplorer jackrabbitExplorer) {
			this.jackrabbitExplorer = jackrabbitExplorer;
		}
		@Override
		public void onCellSaved(com.smartgwt.client.widgets.grid.events.CellSavedEvent event) {
			String selectedNodePath = cellMouseDownTreeGrid.getSelectedRecord().getAttribute("path");
			String propertyName = event.getRecord().getAttribute("property");
			if (event.getColNum() == 0) {
				//update property name
				//service.savePropertyStringValue(selectedNodePath, propertyName, event.getNewValue().toString(), SimpleStringServiceCallback);
			} else if (event.getColNum() == 1) {
				//update value
				showLoadingImg();
// TODO: Saving in cell editor
//				service.saveProperty(selectedNodePath, propertyName, event.getNewValue().toString(),
//						new CRUDServiceCallback(jackrabbitExplorer, selectedNodePath, null));
			}
		}  
	};

	JcrPropertyListGridRecord selectedPropertyListGridRecord = null;
	class PropertiesCellClickHandler implements CellClickHandler {
		@Override
		public void onCellClick(com.smartgwt.client.widgets.grid.events.CellClickEvent event) {
			selectedPropertyListGridRecord = (JcrPropertyListGridRecord)event.getRecord(); 
			// TODO Proper handling of node types
			//String propertyName = selectedPropertyListGridRecord.getAttribute("property");
			if (selectedPropertyListGridRecord.getProperty().getName().contains("jcr:data")) {
				propertiesListGrid.setCanEdit(false);

				String selectedNodePath = cellMouseDownTreeGrid.getSelectedRecord().getAttribute("path");
				String mimeType = "";
				for (int i = 0; i < propertiesListGridRecords.length; i++) {
					if ("jcr:mimeType".equalsIgnoreCase(propertiesListGridRecords[i].getAttribute("property"))) {
						mimeType = propertiesListGridRecords[i].getAttribute("value");
					}
				}
				if (mimeType.startsWith("image")) {
					createBinaryImgWindow(selectedNodePath, selectedNodePath + "/" +  "jcr:data", mimeType);
				} else {
					com.google.gwt.user.client.Window.open(BINARY_SERVLET_PATH + selectedNodePath + "/" +  "jcr:data" , 
							"_blank", "toolbar=no,menubar=no,location=no,resizable=yes,scrollbars=yes,status=no"); 
				}
				return;
			} else {
				propertiesListGrid.setCanEdit(false);
				new EditProperty().editPropertyBox(JcrExplorer.this, selectedPropertyListGridRecord.getProperty(), true);
			}
		}
	}

	private Window createRemoteWindow(String title, String url) {
		Window remoteWindow = new Window();
		HTMLPane htmlPane = new HTMLPane();
		htmlPane.setContentsURL(url);
		htmlPane.setContentsType(ContentsType.PAGE);
		remoteWindow.addItem(htmlPane);
		remoteWindow.setTitle(title);
		remoteWindow.setShowMaximizeButton(true);
		remoteWindow.setCanDragReposition(true);
		remoteWindow.setCanDragResize(true);
		remoteWindow.setHeight("40%");
		remoteWindow.setWidth("40%");
		remoteWindow.setAutoCenter(true);
		remoteWindow.setShowResizeBar(true);
		remoteWindow.setDefaultResizeBars(LayoutResizeBarPolicy.MARKED);
		remoteWindow.show();
		return remoteWindow;
	}

	private Window createBinaryImgWindow(String title, String path, String mimeType) {
		Window binaryWindow = new Window();
		Img img = new Img(BINARY_SERVLET_PATH + path);
		img.setImageType(ImageStyle.STRETCH);
		img.setHeight100();
		img.setWidth100();
		binaryWindow.addItem(img);
		binaryWindow.setTitle(title);
		binaryWindow.setShowMaximizeButton(true);
		binaryWindow.setCanDragReposition(true);
		binaryWindow.setCanDragResize(true);
		binaryWindow.setHeight("40%");
		binaryWindow.setWidth("40%");
		binaryWindow.setAutoCenter(true);
		binaryWindow.setShowResizeBar(true);
		//binaryWindow.setDefaultResizeBars(LayoutResizeBarPolicy.MARKED);
		binaryWindow.show();
		return binaryWindow;
	}

	String deletePropertyString;
	private Menu createPropertyRightClickMenu() {
		Menu rightClickMenu = new Menu();
		MenuItem deleteMenuItem = new MenuItem("Delete", "icons/icon_remove_files.png");
		class DeleteClickHandler implements com.smartgwt.client.widgets.menu.events.ClickHandler {
			private final JcrExplorer jackrabbitExplorer;
			public DeleteClickHandler(JcrExplorer jackrabbitExplorer) {
				this.jackrabbitExplorer = jackrabbitExplorer;
			}
			@Override
			public void onClick(com.smartgwt.client.widgets.menu.events.MenuItemClickEvent event) {
				sourcePath = cellMouseDownTreeGrid.getSelectedRecord().getAttribute("path");
				ListGrid selectedPropListGrid = (ListGrid) event.getTarget();
				deletePropertyString = selectedPropListGrid.getSelectedRecord().getAttribute("property");
				SC.confirm("Proceed with deleting property " + sourcePath + "?", new BooleanCallback() {  
					@Override
					public void execute(Boolean value) {  
						if (value != null && value) {  
							showLoadingImg();
							service.deleteProperty(sourcePath, deletePropertyString,
									new CRUDServiceCallback(jackrabbitExplorer, null, sourcePath));
						} else {  
							return;
						}  
					}  
				});  
			}
		};
		deleteMenuItem.addClickHandler(new DeleteClickHandler(this));
		rightClickMenu.setItems(deleteMenuItem);
		return rightClickMenu;
	}


//	private final TextItem rmiUrlTxt = new TextItem();
//	private final TextItem workspaceTxt = new TextItem();

	private void showPossibleIcons(String path) {
		showLoadingImg();
		service.getPossibleIconPaths(path, new RemoteFileServiceCallback(this));
	}

	public void changeCurrentNodeTypeAssociation(String iconPath) {
		changeNodeTypeIconAssociation(changeIconCellType, iconPath);
	}

	private void changeNodeTypeIconAssociation(String nodeType, String iconPath) {
		service.changeNodeTypeIconAssociation(nodeType, iconPath, new ChangeNodeTypeIconAssociationCallback(this));
	}
}