package ar.edu.itba.pdc.duta.admin.endpoint.config;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

import ar.edu.itba.pdc.duta.admin.endpoint.Endpoint;
import ar.edu.itba.pdc.duta.http.MessageFactory;
import ar.edu.itba.pdc.duta.http.model.MediaType;
import ar.edu.itba.pdc.duta.http.model.Message;
import ar.edu.itba.pdc.duta.http.model.RequestHeader;
import ar.edu.itba.pdc.duta.net.Server;
import ar.edu.itba.pdc.duta.proxy.filter.Filter;
import ar.edu.itba.pdc.duta.proxy.filter.http.BlockFilter;
import ar.edu.itba.pdc.duta.proxy.filter.http.IPFilter;
import ar.edu.itba.pdc.duta.proxy.filter.http.ImageRotationFilter;
import ar.edu.itba.pdc.duta.proxy.filter.http.L33tFilter;
import ar.edu.itba.pdc.duta.proxy.filter.http.MediaTypeFilter;
import ar.edu.itba.pdc.duta.proxy.filter.http.SizeFilter;
import ar.edu.itba.pdc.duta.proxy.filter.http.URIFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddFilterEndpoint extends Endpoint {
	
	private static final Logger logger = Logger.getLogger(AddFilterEndpoint.class);

	@Override
	public Message process(Message msg) {
		RequestHeader header = (RequestHeader) msg.getHeader();
		
		MediaType type = MediaType.valueOf(header.getField("Content-Type"));
		if (!"POST".equalsIgnoreCase(header.getMethod())) {
			return MessageFactory.build400();
		} else if (!MediaType.APPLICATION_JSON_TYPE.isCompatible(type)) {
			return MessageFactory.build(406, "Not Acceptable", "");
		}
		
		byte[] bytes;

		try {
			bytes = msg.getBody().read();
		} catch (IOException e) {
			logger.error("Failed to read message", e);
			return MessageFactory.build500();
		}
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree;
		try {
			tree = mapper.readTree(bytes);
		} catch (JsonProcessingException e) {
			logger.warn("Jackson bailed", e);
			return MessageFactory.build400();
		} catch (IOException e) {
			logger.error("Failed to parse JSON due to IO error", e);
			return MessageFactory.build500();
		}
		
		Filter filter = extractFilter(tree);
		if (filter == null) {
			return MessageFactory.build400();
		}
		
		Set<Object> matches = extractMatches(tree);
		if (matches == null) {
			return MessageFactory.build400();
		}
		
		int id = Server.getFilters().addFilter(matches, filter);
		Message res = MessageFactory.build(201, "Created", null);
		res.getHeader().setField("Location", "/filters/" + id);
		
		return res;
	}

	private Set<Object> extractMatches(JsonNode tree) {
		return null;
	}

	private Filter extractFilter(JsonNode tree) {
		
		if (!tree.has("type")) {
			return null;
		}
		
		String type = tree.get("type").textValue();
		JsonNode configNode = tree.get("config");
		
		if ("deny-ip".equalsIgnoreCase(type))  {
			
			if (configNode == null) {
				return null;
			}
			
			String regexp = configNode.textValue();
			try {
				Pattern.compile(regexp);
			} catch (PatternSyntaxException e) {
				return null;
			}
			
			logger.debug("Config " + regexp);
			
			return new IPFilter(regexp);
 		} else if ("deny-url".equalsIgnoreCase(type)) {
 			
 			if (configNode == null) {
				return null;
			}
			
			String regexp = configNode.textValue();
			try {
				Pattern.compile(regexp);
			} catch (PatternSyntaxException e) {
				return null;
			}
			
			logger.debug("Config " + regexp);
			
			return new URIFilter(regexp);
 		} else if ("deny-type".equalsIgnoreCase(type)) {
 			
 			if (configNode == null) {
				return null;
			}
			
			String mediaType = configNode.textValue();

			try {
				MediaType.valueOf(mediaType);
			} catch (IllegalArgumentException e) {
				return null;
			}
			
			logger.debug("Config " + mediaType);
			
			return new MediaTypeFilter(mediaType);
 		} else if ("deny-size".equalsIgnoreCase(type)) {
 			
 			if (configNode == null || !configNode.canConvertToInt()) {
				return null;
			}
 			
 			logger.debug("Config " + configNode.asInt());
 			
 			return new SizeFilter(configNode.asInt());
 		} else if ("deny-all".equalsIgnoreCase(type)) {
 			return new BlockFilter();
 		} else if ("l33t".equalsIgnoreCase(type)) {
 			return new L33tFilter();
 		} else if ("rotate".equalsIgnoreCase(type)) {
 			return new ImageRotationFilter();
 		}
		
		return null;
	}

}
