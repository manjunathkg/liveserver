package org.liveSense.jcr.explorer.client.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.liveSense.jcr.explorer.client.JcrExplorer;
import org.liveSense.jcr.explorer.client.callback.AvailableNodeTypesServiceCallback;
import org.liveSense.jcr.explorer.client.callback.CRUDServiceCallback;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.SubmitValuesHandler;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.layout.HStack;

public class MixinNodeTypes extends HStack {

	private static ImgButton mixinAddButton = new ImgButton();
	private static Map<String, HStack> mixinTypeComponents = new HashMap<String, HStack>();
	private final JcrExplorer jcrExplorer;

	public MixinNodeTypes(JcrExplorer jcrExplorer) {
		Label label = new Label("&nbsp;Mixin&nbsp;Node&nbsp;Types");
		label.setWidth(100);
		this.addMember(label);
		this.jcrExplorer = jcrExplorer;
	}
	
	protected void removeMixinType(final String mixinType) {
		final String deleteCellPath = JcrExplorer.cellMouseDownTreeGrid.getSelectedRecord().getAttribute("path");
		SC.confirm("Proceed with deleting " + deleteCellPath, new BooleanCallback() {  
			@Override
			public void execute(Boolean value) {  
				if (value != null && value) {  
					jcrExplorer.showLoadingImg();
					jcrExplorer.service.removeMixinType(deleteCellPath, mixinType, new CRUDServiceCallback(jcrExplorer, null, deleteCellPath));
				} else {  
					return;
				}  
			}  
		});  
	}

	protected void addMixinType(String mixinType) {

	}

	public void setMixinTypes(String[] mixinTypes) {
		if (mixinTypes == null) mixinTypes = new String[0];
		//Set<String> existingComponents = new HashSet<String>(mixinTypeComponents.keySet());
		Set<String> existingComponents = new HashSet<String>(mixinTypeComponents.keySet());
		Set<String> remove = new HashSet<String>(mixinTypeComponents.keySet());
		Set<String> add = new HashSet<String>();

		for (String mixinType : mixinTypes) {
			if (mixinType != null && !mixinType.equalsIgnoreCase("")) {
				if (existingComponents.contains(mixinType)) {
					remove.remove(mixinType);
				} else {
					add.add(mixinType);
				}
			}
		}

		for (String removable : remove.toArray(new String[remove.size()])) {
			HStack itm = mixinTypeComponents.get(removable);
			if (itm != null) {
				this.removeMember(itm);
				mixinTypeComponents.remove(removable);
			}
		}

		for (String addable : add.toArray(new String[add.size()])) {
			Label mixinName = new Label(addable);
			mixinName.setMargin(4);
			ImgButton mixinRemoveButton = new ImgButton();  
			mixinRemoveButton.setSize(18);
			mixinRemoveButton.setShowRollOver(false);  
			mixinRemoveButton.setSrc("icons/delete16x16.png");
			class RemoveMixinTypeClickHandler implements com.smartgwt.client.widgets.events.ClickHandler {
				private final String mixinType;
				public RemoveMixinTypeClickHandler(String mixinType) {
					this.mixinType = mixinType;
				}
				@Override
				public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
					removeMixinType(mixinType);
				}
				
			}
			mixinRemoveButton.addClickHandler(new RemoveMixinTypeClickHandler(addable));
			
			HStack comp = new HStack();
			comp.setBackgroundColor("lightblue");
			comp.setBorder("1px solid #aaaaaa");
			comp.setPageLeft(4);
			
			comp.getElement().setAttribute("style", comp.getAttribute("style") + "border: 1px solid #aaaaaa; padding: 0px 4px 0px 4px; border-radius: 4px;");
			comp.addMember(mixinName);
			comp.addMember(mixinRemoveButton);
			mixinTypeComponents.put(addable, comp);
			this.addMember(comp);
		}
	}


	public Window addMixinTypeBox(JcrExplorer jackrabbitExplorer) {
		final Window addMixinTypeWindow = new Window();

		addMixinTypeWindow.setShowMinimizeButton(false);  
		addMixinTypeWindow.setIsModal(true);  
		addMixinTypeWindow.setShowModalMask(true);  
		//addNewNodeWindow.centerInPage();
		addMixinTypeWindow.setTitle("Add New Mixin type");
		addMixinTypeWindow.setCanDragReposition(true);
		addMixinTypeWindow.setCanDragResize(true);
		addMixinTypeWindow.setHeight(120);
		addMixinTypeWindow.setWidth(400);
		addMixinTypeWindow.setTop("30%");
		addMixinTypeWindow.setLeft("35%");
		//addNewNodeWindow.setAutoCenter(true);

		final DynamicForm addMixinTypeForm = new DynamicForm();
		addMixinTypeForm.setNumCols(2);
		addMixinTypeForm.setAlign(Alignment.CENTER);
		
		final ComboBoxItem mixinNodeType = new ComboBoxItem();
		if (!AvailableNodeTypesServiceCallback.valueMap.containsKey(AvailableNodeTypesServiceCallback.DEFAULT_NODE_TYPE)
				&& !AvailableNodeTypesServiceCallback.valueMap.containsValue(AvailableNodeTypesServiceCallback.DEFAULT_NODE_TYPE)) {
			AvailableNodeTypesServiceCallback.valueMap.put(AvailableNodeTypesServiceCallback.DEFAULT_NODE_TYPE, AvailableNodeTypesServiceCallback.DEFAULT_NODE_TYPE);
		}
		mixinNodeType.setValueMap(AvailableNodeTypesServiceCallback.valueMap);
		mixinNodeType.setDefaultValue(AvailableNodeTypesServiceCallback.DEFAULT_NODE_TYPE);
		mixinNodeType.setType("Select");
		//newNodeType.setName("newNodeType");
		mixinNodeType.setTitle("Added&nbsp;Mixin&nbsp;Type");
		mixinNodeType.setWidth(250);
		mixinNodeType.setRequired(true);
		
		SubmitItem addMixinTypeSubmitItem = new SubmitItem("addMixin");
		addMixinTypeSubmitItem.setTitle("Add Mixin");
		addMixinTypeSubmitItem.setWidth(100);

		class AddNodeSubmitValuesHandler implements SubmitValuesHandler {  
			private final JcrExplorer jackrabbitExplorer;
			public AddNodeSubmitValuesHandler(JcrExplorer jackrabbitExplorer) {
				this.jackrabbitExplorer = jackrabbitExplorer;
			}
			@Override
			public void onSubmitValues(com.smartgwt.client.widgets.form.events.SubmitValuesEvent event) {
				if (addMixinTypeForm.validate()) {
					String path = JcrExplorer.cellMouseDownTreeGrid.getSelectedRecord().getAttribute("path");
					JcrExplorer.showLoadingImg();
					JcrExplorer.service.addMixinType(path, mixinNodeType.getValueAsString(), new CRUDServiceCallback(jackrabbitExplorer, path, null));
				}
			}  
		};
		addMixinTypeForm.addSubmitValuesHandler(new AddNodeSubmitValuesHandler(jackrabbitExplorer));
		addMixinTypeForm.setItems(mixinNodeType, addMixinTypeSubmitItem);
		addMixinTypeWindow.addItem(addMixinTypeForm);
		addMixinTypeWindow.show();
		mixinNodeType.focusInItem();
		return addMixinTypeWindow;

	}
}
