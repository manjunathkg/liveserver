package org.liveSense.jcr.explorer.client.ui;

import java.math.BigDecimal;

import org.liveSense.jcr.explorer.client.JcrExplorer;
import org.liveSense.jcr.explorer.client.callback.CRUDServiceCallback;
import org.liveSense.jcr.explorer.domain.JcrProperty;
import org.liveSense.jcr.explorer.domain.JcrPropertyListGridRecord;
import org.liveSense.jcr.explorer.domain.PropertyDataTypes;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.SubmitValuesHandler;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * 
 * @author James Pickup
 *
 */
public class EditProperty {

	private TextItem propName;

	private RadioGroupItem propType;

	private	CheckboxItem propMultiValue;  

	// Multivalue
	private ListGrid propValueGrid;  

	// Single value
	private DateItem propDateValue;  
	private TextItem propStringValue;  
	private IntegerItem propLongValue;  
	private CheckboxItem propBooleanValue;  
	private FloatItem propFloatValue;  
	private DynamicForm editPropForm;
	private Canvas propGridCanvas;  


	private void disableAllFields() {
		propDateValue.setVisible(false);
		propStringValue.setVisible(false);
		propLongValue.setVisible(false);
		propBooleanValue.setVisible(false);
		propFloatValue.setVisible(false);
		propGridCanvas.setVisible(false);
	}
	// TODO: Browse for reference
	private void setFieldEnable(String typeName, boolean multiValue) {
		disableAllFields();
		PropertyDataTypes type = PropertyDataTypes.valueOf(typeName.toUpperCase());

		if (!multiValue) {
			if (type == PropertyDataTypes.BOOLEAN) {
				propBooleanValue.setTitle("Value (Boolean)");
				propBooleanValue.setVisible(true);
			} else if (type == PropertyDataTypes.DATE) {
				propDateValue.setTitle("Value (Date)");
				propDateValue.setVisible(true);
			} else if (type == PropertyDataTypes.DECIMAL || type == PropertyDataTypes.DOUBLE) {
				propFloatValue.setTitle("Value ("+type.getTypeName()+")");
				propFloatValue.setVisible(true);
			} else if (type == PropertyDataTypes.LONG) {
				propLongValue.setTitle("Value ("+type.getTypeName()+")");
				propLongValue.setVisible(true);
			} else {
				propStringValue.setTitle("Value ("+type.getTypeName()+")");
				propStringValue.setVisible(true);
			}
		} else {

			ListGridField valueField; // = new ListGridField("value", "Value");
			if (type == PropertyDataTypes.BOOLEAN) {
				valueField = new ListGridField("value", "Value (Boolean)");
				valueField.setType(ListGridFieldType.BOOLEAN);  
				//				property.setBooleanValue((Boolean)propBooleanValue.getValue());
			} else if (type == PropertyDataTypes.DATE) {
				valueField = new ListGridField("value", "Value (Date)");
				valueField.setType(ListGridFieldType.DATETIME); 
				//				property.setDateValue(propDateValue.getValueAsDate());
			} else if (type == PropertyDataTypes.DECIMAL) {
				valueField = new ListGridField("value", "Value (Decimal)");
				valueField.setType(ListGridFieldType.FLOAT); 
				//				property.setDecimalValue(new BigDecimal(propFloatValue.getValueAsFloat().toString()));
			} else if (type == PropertyDataTypes.DOUBLE) {
				valueField = new ListGridField("value", "Value (Double)");
				valueField.setType(ListGridFieldType.FLOAT); 
				//				property.setDoubleValue(new Double(propFloatValue.getValueAsFloat()));
			} else if (type == PropertyDataTypes.LONG) {
				valueField = new ListGridField("value", "Value (Long)");
				valueField.setType(ListGridFieldType.INTEGER); 
				//				property.setLongValue(new Long(propLongValue.getValueAsInteger()));
			} else {
				valueField = new ListGridField("value", "Value ("+type.getTypeName()+")");
				valueField.setType(ListGridFieldType.TEXT); 
				//				property.setStringValue(propStringValue.getValueAsString());
			}

			propValueGrid.setFields(valueField);
			//			newPropValueGrid.setData(CountryData.getRecords());  
			propGridCanvas.setVisible(true);

			propValueGrid.draw();  
		}
		editPropForm.redraw();
	}

	private void setFieldsFromProperty(JcrProperty property) {
		if (property == null) return;
		propMultiValue.setValue(property.isMultiValue());
		propName.setValue(property.getName());
		propType.setValue(PropertyDataTypes.getTypeByTypeNum(property.getType()).getTypeName());
		PropertyDataTypes type = PropertyDataTypes.getTypeByTypeNum(property.getType());
		if (!property.isMultiValue()) {
			if (type == PropertyDataTypes.BOOLEAN) {
				propBooleanValue.setValue(property.getBooleanValue());
			} else if (type == PropertyDataTypes.DATE) {
				propDateValue.setValue(property.getDateValue());
			} else if (type == PropertyDataTypes.DECIMAL) {
				propFloatValue.setValue(property.getDecimalValue().doubleValue());
			} else if (type == PropertyDataTypes.DOUBLE) {
				propFloatValue.setValue(property.getDoubleValue());
			} else if (type == PropertyDataTypes.LONG) {
				propLongValue.setValue(property.getLongValue());
			} else {
				propStringValue.setValue(property.getStringValue());
			}
		} else {
			JcrPropertyListGridRecord[] records = new JcrPropertyListGridRecord[property.getSize()];
			ListGridField valueField;
			/*
			if (type == PropertyDataTypes.BOOLEAN) {
				valueField = new ListGridField("value", "Value (Boolean)");
				valueField.setType(ListGridFieldType.BOOLEAN);  
				property.setBooleanValue((Boolean)propBooleanValue.getValue());
			} else if (type == PropertyDataTypes.DATE) {
				valueField = new ListGridField("value", "Value (Date)");
				valueField.setType(ListGridFieldType.DATETIME); 
				property.setDateValue(propDateValue.getValueAsDate());
			} else if (type == PropertyDataTypes.DECIMAL) {
				valueField = new ListGridField("value", "Value (Decimal)");
				valueField.setType(ListGridFieldType.FLOAT); 
				property.setDecimalValue(new BigDecimal(propFloatValue.getValueAsFloat().toString()));
			} else if (type == PropertyDataTypes.DOUBLE) {
				valueField = new ListGridField("value", "Value (Double)");
				valueField.setType(ListGridFieldType.FLOAT); 
				property.setDoubleValue(new Double(propFloatValue.getValueAsFloat()));
			} else if (type == PropertyDataTypes.LONG) {
				valueField = new ListGridField("value", "Value (Long)");
				valueField.setType(ListGridFieldType.INTEGER); 
				property.setLongValue(new Long(propLongValue.getValueAsInteger()));
			} else {
				valueField = new ListGridField("value", "Value ("+type.getTypeName()+")");
				valueField.setType(ListGridFieldType.TEXT); 
				property.setStringValue(propStringValue.getValueAsString());
			}
			 */
			for (int i=0; i<property.getSize(); i++) {
				ListGridRecord rec = new ListGridRecord();
				//records[i] = rec;
				//rec.setProperty(property);
				if (type == PropertyDataTypes.BOOLEAN) {
					rec.setAttribute("value", property.getBooleanValue());
				} else if (type == PropertyDataTypes.DATE) {
					rec.setAttribute("value", property.getDateValue());
				} else if (type == PropertyDataTypes.DECIMAL) {
					rec.setAttribute("value", property.getDecimalValue().doubleValue());
				} else if (type == PropertyDataTypes.DOUBLE) {
					rec.setAttribute("value", property.getDoubleValue());
				} else if (type == PropertyDataTypes.LONG) {
					rec.setAttribute("value", property.getLongValue());
				} else {
					rec.setAttribute("value", property.getStringValue());
				}
			}
			//propValueGrid.setFields(valueField);
			propValueGrid.setData(records);

		}
		editPropForm.redraw();
	}

	private JcrProperty getPropertyFromFields() {
		PropertyDataTypes type = PropertyDataTypes.valueOf(((String)propType.getValue()).toUpperCase());
		JcrProperty property = new JcrProperty();
		property.setName(propName.getValue().toString());
		property.setType(type.getTypeNum());
		if (!(Boolean)propMultiValue.getValue()) {
			if (type == PropertyDataTypes.BOOLEAN) {
				property.setBooleanValue((Boolean)propBooleanValue.getValue());
			} else if (type == PropertyDataTypes.DATE) {
				property.setDateValue(propDateValue.getValueAsDate());
			} else if (type == PropertyDataTypes.DECIMAL) {
				property.setDecimalValue(new BigDecimal(propFloatValue.getValueAsFloat().toString()));
			} else if (type == PropertyDataTypes.DOUBLE) {
				property.setDoubleValue(new Double(propFloatValue.getValueAsFloat()));
			} else if (type == PropertyDataTypes.LONG) {
				property.setLongValue(new Long(propLongValue.getValueAsInteger()));
			} else {
				property.setStringValue(propStringValue.getValueAsString());
			}
		} else {
			ListGridField nameField;
			RecordList rl = propValueGrid.getDataAsRecordList();
			for (int i=0; i<rl.getLength(); i++) {
				Record rec = rl.get(i);
				if (type == PropertyDataTypes.BOOLEAN) {
					property.addBooleanValue(rec.getAttributeAsBoolean("value"));
				} else if (type == PropertyDataTypes.DATE) {
					property.addDateValue(rec.getAttributeAsDate("value"));
				} else if (type == PropertyDataTypes.DECIMAL) {
					property.addDecimalValue(new BigDecimal(rec.getAttributeAsFloat("value")));
				} else if (type == PropertyDataTypes.DOUBLE) {
					property.addDoubleValue(new Double(rec.getAttributeAsFloat("value")));
				} else if (type == PropertyDataTypes.LONG) {
					property.addLongValue(new Long(rec.getAttributeAsInt("value")));
				} else {
					property.addStringValue(propStringValue.getValueAsString());
				}

			}

			/*
			if (type == PropertyDataTypes.BOOLEAN) {
				nameField = new ListGridField("value", "Value (Boolean)");
				property.setBooleanValue((Boolean)propBooleanValue.getValue());
			} else if (type == PropertyDataTypes.DATE) {
				nameField = new ListGridField("value", "Value (Date)");
				property.setDateValue(propDateValue.getValueAsDate());
			} else if (type == PropertyDataTypes.DECIMAL) {
				nameField = new ListGridField("value", "Value (Decimal)");
				property.setDecimalValue(new BigDecimal(propFloatValue.getValueAsFloat().toString()));
			} else if (type == PropertyDataTypes.DOUBLE) {
				nameField = new ListGridField("value", "Value (Double)");
				property.setDoubleValue(new Double(propFloatValue.getValueAsFloat()));
			} else if (type == PropertyDataTypes.LONG) {
				nameField = new ListGridField("value", "Value (Long)");
				property.setLongValue(new Long(propLongValue.getValueAsInteger()));
			} else {
				nameField = new ListGridField("value", "Value ("+type.getTypeName()+")");
				property.setStringValue(propStringValue.getValueAsString());
			}
			 */
			//			propValueGrid.setFields(nameField);
			//			propValueGrid.setDa
			//			newPropValueGrid.setData(CountryData.getRecords());  
			//			newPropValueGrid.draw();  
		}
		return property;
	}


	public Window editPropertyBox(JcrExplorer jackrabbitExplorer, JcrProperty prop, final boolean isNew) {
		final Window editPropertyWindow = new Window();

		editPropertyWindow.setShowMinimizeButton(false);  
		editPropertyWindow.setIsModal(true);  
		editPropertyWindow.setShowModalMask(true);  
		editPropertyWindow.centerInPage();
		if (isNew)
			editPropertyWindow.setTitle("Add New Property");
		else 
			editPropertyWindow.setTitle("Edit Property");

		editPropertyWindow.setCanDragReposition(true);
		editPropertyWindow.setCanDragResize(true);
		editPropertyWindow.setHeight(600);
		editPropertyWindow.setWidth(800);
		editPropertyWindow.setAutoCenter(true);

		editPropForm = new DynamicForm();

		propName = new TextItem();

		propType = new RadioGroupItem();

		propMultiValue = new CheckboxItem();  

		// Multivalue
		propValueGrid = new ListGrid();  

		// Single value
		propDateValue = new DateItem();  
		propStringValue = new TextItem();  
		propLongValue = new IntegerItem();  
		propBooleanValue = new CheckboxItem();  
		propFloatValue = new FloatItem();  



		editPropForm.setPadding(10);
		editPropForm.setNumCols(2);

		propName.setName("newPropName");
		propName.setTitle("Name");
		propName.setWidth(250);
		propName.setRequired(true);

		propType.setVertical(false);
		propType.setTitle("Propery type");
		propType.setValueMap("String", "Boolean", "Date", "Decimal", "Double", "Long", "Name", "Path", "Reference");
		propType.setValueMap(PropertyDataTypes.STRING.getTypeName(), 
				PropertyDataTypes.DATE.getTypeName(), 
				PropertyDataTypes.DECIMAL.getTypeName(), 
				PropertyDataTypes.LONG.getTypeName(), 
				PropertyDataTypes.DOUBLE.getTypeName(), 
				PropertyDataTypes.BOOLEAN.getTypeName(), 
				PropertyDataTypes.NAME.getTypeName(), 
				PropertyDataTypes.PATH.getTypeName(), 
				PropertyDataTypes.URI.getTypeName(), 
				PropertyDataTypes.REFERENCE.getTypeName(),
				PropertyDataTypes.WEAKREFERENCE.getTypeName());
		propType.setDefaultValue(PropertyDataTypes.STRING.getTypeName());
		propType.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				setFieldEnable((String)event.getValue(), (Boolean)propMultiValue.getValue());
			}
		});


		propMultiValue.setTitle("Multi value");
		propMultiValue.setValue(false);
		propMultiValue.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				setFieldEnable((String)propType.getValue(), (Boolean)event.getValue());
			}
		});

		propValueGrid.setWidth(360);  
		propValueGrid.setHeight(224);  
		propValueGrid.setShowAllRecords(true);
		propValueGrid.setCanEdit(true);
		propGridCanvas = new Canvas();  

		IButton addValue = new IButton("Add value");  
		addValue.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {  
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {  
				ListGridRecord rec = new ListGridRecord();  
				propValueGrid.addData(rec);                 
			}             
		});  
		addValue.setLeft(0);  
		addValue.setTop(240);  
		addValue.setWidth(120);  

/*		
		IButton removeFirst = new IButton("Remove First");  
		removeFirst.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				propValueGrid.removeData(propValueGrid.getRecord(0));                 
			}
		});  
		removeFirst.setLeft(160);  
		removeFirst.setTop(240);  
		removeFirst.setWidth(145);  
*/
/*
		IButton removeSelected = new IButton("Remove First Selected");  
		removeSelected.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {  
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {  
				ListGridRecord selectedRecord = propValueGrid.getSelectedRecord();  
				if(selectedRecord != null) {  
					propValueGrid.removeData(selectedRecord);  
				} else {  
					SC.say("Select a record before performing this action");  
				}  
			}  

		});  
		removeSelected.setLeft(320);  
		removeSelected.setTop(240);  
		removeSelected.setWidth(150);  
*/
		IButton removeAll = new IButton("Remove Selected");  
		removeAll.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {  
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {  
				ListGridRecord[] selectedRecords = propValueGrid.getSelection();  
				boolean wasQueuing = RPCManager.startQueue();  
				for(ListGridRecord rec: selectedRecords) {  
					propValueGrid.removeData(rec);  
				}  
				if (!wasQueuing) RPCManager.sendQueue();  
			}             
		});  
		removeAll.setLeft(260);  
		removeAll.setTop(240);  
		removeAll.setWidth(120);  

		propGridCanvas.addChild(propValueGrid);  
		propGridCanvas.addChild(addValue);  
//		propGridCanvas.addChild(removeFirst);  
//		propGridCanvas.addChild(removeSelected);  
		propGridCanvas.addChild(removeAll);

		/*
		class PropertiesCellSavedHandler implements CellSavedHandler   {  
			private final JcrExplorer jackrabbitExplorer;
			public PropertiesCellSavedHandler(JcrExplorer jackrabbitExplorer) {
				this.jackrabbitExplorer = jackrabbitExplorer;
			}
			@Override
			public void onCellSaved(com.smartgwt.client.widgets.grid.events.CellSavedEvent event) {
				String propertyName = event.getRecord().getAttribute("value");
				if (event.getColNum() == 0) {
					//update property name
					//service.savePropertyStringValue(selectedNodePath, propertyName, event.getNewValue().toString(), SimpleStringServiceCallback);
				} else if (event.getColNum() == 1) {
					//update value
					showLoadingImg();
	// TODO: Saving in cell editor
//					service.saveProperty(selectedNodePath, propertyName, event.getNewValue().toString(),
//							new CRUDServiceCallback(jackrabbitExplorer, selectedNodePath, null));
				}
			}  
		};
		 */
		propDateValue.setWidth(250);
		propStringValue.setWidth(250);
		propLongValue.setWidth(250);
		propBooleanValue.setWidth(250);
		propFloatValue.setWidth(250);

		SubmitItem propertySubmitItem = new SubmitItem("submitProperty");
		if (isNew)
			propertySubmitItem.setTitle("Add Property");
		else
			propertySubmitItem.setTitle("Edit Property");

		propertySubmitItem.setWidth(100);
		class PropertySubmitValuesHandler implements SubmitValuesHandler {  
			private final JcrExplorer jackrabbitExplorer;
			public PropertySubmitValuesHandler(JcrExplorer jackrabbitExplorer) {
				this.jackrabbitExplorer = jackrabbitExplorer;
			}
			@Override
			public void onSubmitValues(com.smartgwt.client.widgets.form.events.SubmitValuesEvent event) {
				if (editPropForm.validate()) {
					String path = JcrExplorer.cellMouseDownTreeGrid.getSelectedRecord().getAttribute("path");
					JcrExplorer.showLoadingImg();
					if (isNew) {
						JcrExplorer.service.addNewProperty(path, propName.getValue().toString(), getPropertyFromFields(),
								new CRUDServiceCallback(jackrabbitExplorer, path, null));
					} else {
						JcrExplorer.service.saveProperty(path, propName.getValue().toString(), getPropertyFromFields(),
								new CRUDServiceCallback(jackrabbitExplorer, path, null));						
					}
				}
			}  
		};
		editPropForm.addSubmitValuesHandler(new PropertySubmitValuesHandler(jackrabbitExplorer));
		editPropForm.setSaveOnEnter(true);
		class PropertyButtonHandler implements ClickHandler {  
			private final JcrExplorer jackrabbitExplorer;
			public PropertyButtonHandler(JcrExplorer jackrabbitExplorer) {
				this.jackrabbitExplorer = jackrabbitExplorer;
			}
			@Override
			public void onClick(ClickEvent event) {  
				if (editPropForm.validate()) {
					String path = JcrExplorer.cellMouseDownTreeGrid.getSelectedRecord().getAttribute("path");
					JcrExplorer.showLoadingImg();
					if (isNew) {
						JcrExplorer.service.addNewProperty(path, propName.getValue().toString(), getPropertyFromFields(),
								new CRUDServiceCallback(jackrabbitExplorer, path, null));
					} else {
						JcrExplorer.service.saveProperty(path, propName.getValue().toString(), getPropertyFromFields(),
								new CRUDServiceCallback(jackrabbitExplorer, path, null));						
					}
				}
			}  
		};
		propertySubmitItem.addClickHandler(new PropertyButtonHandler(jackrabbitExplorer));
		SubmitItem cancelPropertySubmitItem = new SubmitItem("cancelProperty");
		cancelPropertySubmitItem.setTitle("Cancel");
		cancelPropertySubmitItem.setWidth(100);
		class CancelAddPropertyButtonHandler implements ClickHandler {  
			@Override
			public void onClick(ClickEvent event) {  
				editPropertyWindow.destroy();
			}  
		};
		cancelPropertySubmitItem.addClickHandler(new CancelAddPropertyButtonHandler());
		VStack vStack = new VStack();
		SpacerItem spacerItem1 = new SpacerItem();
		spacerItem1.setStartRow(true);
		spacerItem1.setEndRow(true);
		propertySubmitItem.setStartRow(true);
		propertySubmitItem.setEndRow(false);
		cancelPropertySubmitItem.setStartRow(false);
		cancelPropertySubmitItem.setEndRow(true);
		editPropForm.setItems(propName, propType, propMultiValue, propBooleanValue, propDateValue, propFloatValue, propLongValue, propStringValue, spacerItem1);        
		vStack.addMember(editPropForm);
		vStack.addMember(propGridCanvas);

		DynamicForm propertySubmitForm = new DynamicForm();
		propertySubmitForm.setNumCols(2);
		propertySubmitForm.setItems(propertySubmitItem, cancelPropertySubmitItem);
		//vStack.setTop(50);
		HStack hStack = new HStack();
		hStack.addMember(propertySubmitForm);
		hStack.setHeight(10);
		hStack.setWidth100();
		hStack.setAlign(Alignment.RIGHT);
		vStack.setHeight(100);
		vStack.setWidth100();
		vStack.setPadding(10);
		vStack.addMember(hStack);
		editPropertyWindow.addItem(vStack);
		editPropertyWindow.show();
		propName.focusInItem();
		if (prop != null) setFieldsFromProperty(prop);
		setFieldEnable((String)propType.getValue(), (Boolean)propMultiValue.getValue());
		return editPropertyWindow;
	}
}
