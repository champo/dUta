package ar.edu.itba.pdc.duta.http;

import java.util.HashMap;
import java.util.Map;

public abstract class MessageHeader {

	private Map<String, String> fields = new HashMap<String, String>();

	abstract void setStartLine(String s) throws Exception;

	public String getField(String fieldName) {

		return fields.get(fieldName);
	}

	public Map<String, String> getFields() {
	
		return fields;
	}
	
	void setField(String fieldName, String fieldValue) {
		
		if (fields.containsKey(fieldName)) {
			fieldValue = fields.get(fieldName) + fieldValue;
		}
		
		fields.put(fieldName, fieldValue);
	}
	
	void trimValues() {

		for(Map.Entry<String, String> field: fields.entrySet()) {

			field.setValue(field.getValue().trim());
		}
	}

}
