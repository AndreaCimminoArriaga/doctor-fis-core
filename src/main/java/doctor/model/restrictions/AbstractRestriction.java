package doctor.model.restrictions;

/**
 * 
 * @author andreacimmino
 *
 */
public abstract class AbstractRestriction implements Restriction{

	private String id;

	
	/**
	 * 
	 * @param id
	 */
	protected AbstractRestriction(String id) {
		super();
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	
	
	
	
}
