package org.liveSense.jcr.explorer.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

public class JcrProperty implements Serializable {

	private static final long serialVersionUID = -1136987040729966315L;

	String name;
	int type = 0;

	private boolean multiValue = false;

	boolean readOnly = false;

	private JcrValue[] values = null;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private void determinateSize(int size) {
		if (values == null || values.length == 0) {
			values = new JcrValue[size];
			for (int i = 0; i<size; i++) values[i] = new JcrValue();
		} else {
			if (size != values.length) setSize(size);
			for (int i = 0; i<size; i++) 
				if (values[i] == null) values[i] = new JcrValue();
		}
	}
	
	public Boolean getBooleanValue() {
		if (values == null || values.length == 0) return null;
		return values[0].getBooleanValue();
	}
	public Date getDateValue() {
		if (values == null || values.length == 0) return null;
		return this.values[0].getDateValue();
	}
	public BigDecimal getDecimalValue() {
		if (values == null || values.length == 0) return null;
		return this.values[0].getDecimalValue();
	}
	public Double getDoubleValue() {
		if (values == null || values.length == 0) return null;
		return this.values[0].getDoubleValue();
	}
	public Long getLongValue() {
		if (values == null || values.length == 0) return null;
		return this.values[0].getLongValue();
	}
	public String getStringValue() {
		if (values == null || values.length == 0) return null;
		return this.values[0].getStringValue();
	}
	public String getBinaryLink() {
		if (values == null || values.length == 0) return null;
		return this.values[0].getStringValue();
	}

	
	public Boolean[] getBooleanValues() {
		if (values == null || values.length == 0) return null;
		Boolean[] ret = new Boolean[values.length];
		for (int i=0; i<values.length; i++) {
			if (values[i] != null) ret[i] = values[i].getBooleanValue();
		}
		return ret;
	}
	public Date[] getDateValues() {
		if (values == null || values.length == 0) return null;
		Date[] ret = new Date[values.length];
		for (int i=0; i<values.length; i++) {
			if (values[i] != null) ret[i] = values[i].getDateValue();
		}
		return ret;
	}
	public BigDecimal[] getDecimalValues() {
		if (values == null || values.length == 0) return null;
		BigDecimal[] ret = new BigDecimal[values.length];
		for (int i=0; i<values.length; i++) {
			if (values[i] != null) ret[i] = values[i].getDecimalValue();
		}
		return ret;
	}
	public Double[] getDoubleValues() {
		if (values == null || values.length == 0) return null;
		Double[] ret = new Double[values.length];
		for (int i=0; i<values.length; i++) {
			if (values[i] != null) ret[i] = values[i].getDoubleValue();
		}
		return ret;
	}
	public Long[] getLongValues() {
		if (values == null || values.length == 0) return null;
		Long[] ret = new Long[values.length];
		for (int i=0; i<values.length; i++) {
			if (values[i] != null) ret[i] = values[i].getLongValue();
		}
		return ret;
	}
	public String[] getStringValues() {
		if (values == null || values.length == 0) return null;
		String[] ret = new String[values.length];
		for (int i=0; i<values.length; i++) {
			if (values[i] != null) ret[i] = values[i].getStringValue();
		}
		return ret;
	}
	public String[] getBinaryLinks() {
		if (values == null || values.length == 0) return null;
		String[] ret = new String[values.length];
		for (int i=0; i<values.length; i++) {
			if (values[i] != null) ret[i] = values[i].getStringValue();
		}
		return ret;
	}

	public Boolean getBooleanValue(int idx) {
		if (values == null || values.length == 0 || values.length<idx+1 || values[idx] == null ) return null;
		return values[idx].getBooleanValue();
	}
	public Date getDateValue(int idx) {
		if (values == null || values.length == 0 || values.length<idx+1 || values[idx] == null ) return null;
		return this.values[idx].getDateValue();
	}
	public BigDecimal getDecimalValue(int idx) {
		if (values == null || values.length == 0 || values.length<idx+1 || values[idx] == null ) return null;
		return this.values[idx].getDecimalValue();
	}
	public Double getDoubleValue(int idx) {
		if (values == null || values.length == 0 || values.length<idx+1 || values[idx] == null ) return null;
		return this.values[idx].getDoubleValue();
	}
	public Long getLongValue(int idx) {
		if (values == null || values.length == 0 || values.length<idx+1 || values[idx] == null ) return null;
		return this.values[idx].getLongValue();
	}
	public String getStringValue(int idx) {
		if (values == null || values.length == 0 || values.length<idx+1 || values[idx] == null ) return null;
		return this.values[idx].getStringValue();
	}
	public String getBinaryLink(int idx) {
		if (values == null || values.length == 0 || values.length<idx+1 || values[idx] == null ) return null;
		return this.values[idx].getStringValue();
	}

	public void setBooleanValue(Boolean booleanValue) {
		determinateSize(1);
		this.values[0].setBooleanValue(booleanValue);
	}
	public void setDateValue(Date dateValue) {
		determinateSize(1);
		this.values[0].setDateValue(dateValue);
	}
	public void setDecimalValue(BigDecimal decimalValue) {
		determinateSize(1);
		this.values[0].setDecimalValue(decimalValue);
	}
	public void setDoubleValue(Double doubleValue) {
		determinateSize(1);
		this.values[0].setDoubleValue(doubleValue);
	}
	public void setLongValue(Long longValue) {
		determinateSize(1);
		this.values[0].setLongValue(longValue);
	}
	public void setStringValue(String stringValue) {
		determinateSize(1);
		this.values[0].setStringValue(stringValue);
	}
	public void setBooleanValue(int idx, Boolean booleanValue) {
		determinateSize(idx+1);
		this.values[idx].setBooleanValue(booleanValue);
	}
	public void setDateValue(int idx, Date dateValue) {
		determinateSize(idx+1);
		this.values[idx].setDateValue(dateValue);
	}
	public void setDecimalValue(int idx, BigDecimal decimalValue) {
		determinateSize(idx+1);
		this.values[idx].setDecimalValue(decimalValue);
	}
	public void setDoubleValue(int idx, Double doubleValue) {
		determinateSize(idx+1);
		this.values[idx].setDoubleValue(doubleValue);
	}
	public void setLongValue(int idx, Long longValue) {
		determinateSize(idx+1);
		this.values[idx].setLongValue(longValue);
	}
	public void setStringValue(int idx, String stringValue) {
		determinateSize(idx+1);
		this.values[idx].setStringValue(stringValue);
	}

	private JcrValue[] insertArray(int index, JcrValue[] arr, JcrValue value) {
		JcrValue[] newArray = new JcrValue[arr.length + 1];
		System.arraycopy(arr, 0, newArray, 0, index);
		System.arraycopy(arr, index, newArray, index + 1, 
				arr.length - index);
		newArray[index] = value;
		arr = newArray;
		return newArray;
	}
	
	private JcrValue[] deleteFromArray(JcrValue[] arr, JcrValue value) {
		boolean[] deleteNumber = new boolean[arr.length];
		int size = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(value)) {
				deleteNumber[i] = true;
			} else {
				deleteNumber[i] = false;
				size++;
			}
		}
		JcrValue[] newArr = new JcrValue[size];
		int in = 0;
		for (int i = 0; i < arr.length; i++) {
			if (!deleteNumber[i]) {
				newArr[in++] = arr[i];
			}
		}
		return newArr;
	}

	private JcrValue[] deleteFromArray(int index, JcrValue[] arr) {
		boolean[] deleteNumber = new boolean[arr.length];

		int size = 0;
		for (int i = 0; i < arr.length; i++) {
			if (i == index) {
				deleteNumber[i] = true;
			} else {
				deleteNumber[i] = false;
				size++;
			}
		}
		JcrValue[] newArr = new JcrValue[size];
		int in = 0;
		for (int i = 0; i < arr.length; i++) {
			if (!deleteNumber[i]) {
				newArr[in++] = arr[i];
			}
		}
		return newArr;
	}

	private void setSize(int size) {
		JcrValue newValues[] = new JcrValue[size];
		for (int i=0; i<((size >= getSize())?getSize():size); i++) {
			newValues[i] = values[i];
		}
		values = newValues;
		if (size > 1) {
			multiValue = true;
		}
		determinateSize(size);
	}

	public void addBooleanValue(Boolean booleanValue) {
		setSize(getSize()+1);
		this.values[getSize()-1].setBooleanValue(booleanValue);
	}
	public void addDateValue(Date dateValue) {
		setSize(getSize()+1);
		this.values[getSize()-1].setDateValue(dateValue);
	}
	public void addDecimalValue(BigDecimal decimalValue) {
		setSize(getSize()+1);
		this.values[getSize()-1].setDecimalValue(decimalValue);
	}
	public void addDoubleValue(Double doubleValue) {
		setSize(getSize()+1);
		this.values[getSize()-1].setDoubleValue(doubleValue);
	}
	public void addLongValue(Long longValue) {
		setSize(getSize()+1);
		this.values[getSize()-1].setLongValue(longValue);
	}
	public void addStringValue(String stringValue) {
		setSize(getSize()+1);
		this.values[getSize()-1].setStringValue(stringValue);
	}

	public void addBooleanValue(int idx, Boolean booleanValue) {
		this.values = insertArray(idx, values, new JcrValue(booleanValue));
	}
	public void addDateValue(int idx, Date dateValue) {
		this.values = insertArray(idx, values, new JcrValue(dateValue));
	}
	public void addDecimalValue(int idx, BigDecimal decimalValue) {
		this.values = insertArray(idx, values, new JcrValue(decimalValue));
	}
	public void addDoubleValue(int idx, Double doubleValue) {
		this.values = insertArray(idx, values, new JcrValue(doubleValue));
	}
	public void addLongValue(int idx, Long longValue) {
		this.values = insertArray(idx, values, new JcrValue(longValue));
	}
	public void addStringValue(int idx, String stringValue) {
		this.values = insertArray(idx, values, new JcrValue(stringValue));
	}
	
	public void deleteValue(int idx) {
		this.values = deleteFromArray(idx, values);
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public int getSize() {
		if (values == null) return 0;
		return values.length;
	}

	public boolean isMultiValue() {
		return multiValue;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JcrProperty [name=").append(name).append(", type=")
				.append(type).append(", multiValue=").append(multiValue)
				.append(", readOnly=").append(readOnly).append(", values=")
				.append(Arrays.toString(values)).append("]");
		return builder.toString();
	}

	
}

