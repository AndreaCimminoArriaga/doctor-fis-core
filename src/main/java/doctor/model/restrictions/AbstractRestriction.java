package doctor.model.restrictions;

public abstract class AbstractRestriction implements Restriction{

	private String id;

	
	
	public AbstractRestriction(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
	
	
}
