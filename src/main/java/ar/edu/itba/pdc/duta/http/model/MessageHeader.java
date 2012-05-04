package ar.edu.itba.pdc.duta.http.model;

import java.util.Map;

public abstract class MessageHeader {

	private Map<String, String> fields;

	protected MessageHeader(Map<String, String> fields) {
		
		this.fields = fields;
	}

	public String getField(String fieldName) {

		return fields.get(fieldName);
	}

	public Map<String, String> getFields() {
	
		return fields;
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append(getStartLine());
		
		for (Map.Entry<String, String> field : fields.entrySet()) {
			res.append(field.getKey())
				.append(": ")
				.append(field.getValue())
				.append("\r\n");
		}
		
		res.append("\r\n");
		
		return res.toString();
	}

	protected abstract String getStartLine();

	public void setField(String name, String value) {
		
		fields.put(name, value);
	}
}
