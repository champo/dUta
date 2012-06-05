package ar.edu.itba.pdc.duta.http.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.WordUtils;

public abstract class MessageHeader {

	private Map<String, String> fields;
	private Map<String, String> fieldNames;
	
	protected String HTTPVersion;

	protected MessageHeader(Map<String, String> fields) {

		Map<String, String> normalizedFields = new HashMap<String, String>();
		Map<String, String> fieldNames = new HashMap<String, String>();

		for (Entry<String, String> field : fields.entrySet()) {
			
			String fieldName = WordUtils.capitalizeFully(field.getKey(), '-');
			normalizedFields.put(fieldName, field.getValue());
			fieldNames.put(fieldName, field.getKey());
		}

		this.fields = normalizedFields;
		this.fieldNames = fieldNames;
	}

	protected MessageHeader(Map<String, String> fields, Map<String, String> fieldNames) {

		this.fields = fields;
		this.fieldNames = fieldNames;
	}

	public String getField(String fieldName) {

		return fields.get(WordUtils.capitalizeFully(fieldName, '-'));
	}

	public Map<String, String> getFields() {
	
		return fields;
	}

	public String getFieldName(String fieldName) {
	
		return fieldNames.get(WordUtils.capitalizeFully(fieldName, '-'));
	}
	
	public Map<String, String> getFieldNames() {
	
		return fieldNames;
	}
	
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append(getStartLine());
		
		for (Map.Entry<String, String> field : fields.entrySet()) {
			String name = fieldNames.get(field.getKey());
			if (name == null) {
				name = field.getKey();
			}
			
			res.append(name)
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
