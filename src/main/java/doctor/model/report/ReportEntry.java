package doctor.model.report;

import java.util.Map;

import org.apache.jena.ext.com.google.common.collect.Maps;

public class ReportEntry {
	
	private Type type;
	private Map<Level, String> messages;
	private String id;
	
	public ReportEntry() {
		super();
	}
	
	
	public ReportEntry(String id, Type type) {
		super();
		this.id = id;
		this.type = type;
		messages = Maps.newHashMap();
	}


	public static ReportEntry create(String id, Type type) {
		
		return new ReportEntry(id, type) ;
	}

	public void addLevel(Level level, String message) {
		
	}
	
	
	public Type getType() {
		return type;
	}


	public void setType(Type type) {
		this.type = type;
	}


	


	public Map<Level, String> getMessages() {
		return messages;
	}


	public void setMessages(Map<Level, String> messages) {
		this.messages = messages;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	@Override
	public String toString() {
		return "ReportEntry [id="+  id +", type=" + type + ", messages=" + messages + "]";
	}


	
	

}
