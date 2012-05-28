package ar.edu.itba.pdc.duta.http.model;

import java.util.HashMap;
import java.util.Map;

public class MediaType {
	
	private String type;
	
	private Map<String, String> parameters;
	
	public MediaType(String value) {
		
		parameters = new HashMap<String, String>();
		if (value == null || value.trim().isEmpty()) {
			type = "";
		} else {
		
			String[] parts = value.trim().split(";");
			type = parts[0];
			
			for (int i = 1; i < parts.length; i++) {
				int equals = parts[i].indexOf('=');
				parameters.put(parts[i].substring(0, equals).trim(), parts[i].substring(equals + 1).trim());
			}
		
		}
		
	}
	
	public String getType() {
		return type;
	}
	
	public String getParameter(String name) {
		return parameters.get(name);
	}

}
