package ar.edu.itba.pdc.duta.http.model;

import java.util.HashMap;
import java.util.Map;

public abstract class MessageHeader {

	private Map<String, String> fields;
	private Map<String, String> fieldNames;
	
	protected String HTTPVersion;

	protected MessageHeader(Map<String, String> fields) {

		Map<String, String> fieldNames = new HashMap<String, String>();
		
		for(String fieldName: fields.keySet()) {
			fieldNames.put(fieldName, fieldName);
		}
		
		this.fields = fields;
		this.fieldNames = fieldNames;
	}

	protected MessageHeader(Map<String, String> fields, Map<String, String> fieldNames) {
		
		this.fields = fields;
		this.fieldNames = fieldNames;
	}

	public String getField(String fieldName) {

		return fields.get(fieldName);
	}

	public Map<String, String> getFields() {
	
		return fields;
	}

	public String getFieldName(String fieldName) {
	
		return fieldNames.get(fieldName);
	}
	
	public Map<String, String> getFieldNames() {
	
		return fieldNames;
	}
	
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append(getStartLine());
		
		for (Map.Entry<String, String> field : fields.entrySet()) {
			res.append(fieldNames.get(field.getKey()))
				.append(": ")
				.append(field.getValue())
				.append("\r\n");
		}
		
		res.append("\r\n");
		
		return res.toString();
	}
	
	public String getHTTPVersion() {
		return HTTPVersion;
	}

	protected abstract String getStartLine();

	public void setField(String name, String value) {
		
		fields.put(name, value);
	}
	
	public void removeField(String name) {
		fields.remove(name);
	}
}
