package org.liveSense.jcr.explorer.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class JcrValue implements Serializable {

	
	private static final long serialVersionUID = 2273118899714430912L;

	Boolean booleanValue = null;
	Date dateValue = null;
	BigDecimal decimalValue = null;
	Double doubleValue = null;
	Long longValue = null;
	String stringValue = null;
	
	Boolean readOnly = false;

	public JcrValue() {
	}
	
	public JcrValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public JcrValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public JcrValue(BigDecimal decimalValue) {
		this.decimalValue = decimalValue;
	}

	public JcrValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public JcrValue(long longValue) {
		this.longValue = longValue;
	}

	public JcrValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public boolean getBooleanValue() {
		return booleanValue;
	}
	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	public Date getDateValue() {
		return dateValue;
	}
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}
	public BigDecimal getDecimalValue() {
		return decimalValue;
	}
	public void setDecimalValue(BigDecimal decimalValue) {
		this.decimalValue = decimalValue;
	}
	public double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	public long getLongValue() {
		return longValue;
	}
	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String toString() {
		return "JcrValue [booleanValue=" + booleanValue + ", dateValue="
				+ dateValue + ", decimalValue=" + decimalValue
				+ ", doubleValue=" + doubleValue + ", longValue=" + longValue
				+ ", stringValue=" + stringValue + ", readOnly=" + readOnly
				+ "]";
	}
	
	
}

