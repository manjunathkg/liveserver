package org.liveSense.jcr.explorer.domain;

public enum PropertyDataTypes {
	UNDEFINED("Undefined", 0),	
	STRING("String", 1),
	BINARY("Binary", 2),
	LONG("Long", 3),
	DOUBLE("Double", 4),
	DATE("Date", 5),
	BOOLEAN("Boolean", 6), 
	NAME("Name", 7),
	PATH("Path", 8),
	REFERENCE("Reference", 9),
	WEAKREFERENCE("Weakreference", 10),
	URI("Uri", 11),
	DECIMAL("Decimal", 12);
	
	private final String typeName;
	private final int typeNum;
	
	PropertyDataTypes(String typeName, int typeNum) {
		this.typeName = typeName;
		this.typeNum = typeNum;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public int getTypeNum() {
		return typeNum;
	}
	
	public static PropertyDataTypes getTypeByTypeNum(int typeNum) {
		if (STRING.getTypeNum() == typeNum) return STRING;
		else if (BINARY.getTypeNum() == typeNum) return BINARY;
		else if (LONG.getTypeNum() == typeNum) return LONG;
		else if (DOUBLE.getTypeNum() == typeNum) return DOUBLE;
		else if (DATE.getTypeNum() == typeNum) return DATE;
		else if (BOOLEAN.getTypeNum() == typeNum) return BOOLEAN;
		else if (NAME.getTypeNum() == typeNum) return NAME;
		else if (PATH.getTypeNum() == typeNum) return PATH;
		else if (REFERENCE.getTypeNum() == typeNum) return REFERENCE;
		else if (WEAKREFERENCE.getTypeNum() == typeNum) return WEAKREFERENCE;
		else if (URI.getTypeNum() == typeNum) return URI;
		else if (DECIMAL.getTypeNum() == typeNum) return DECIMAL;
		else return UNDEFINED;
	}
}