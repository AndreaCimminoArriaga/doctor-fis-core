package doctor.model.restrictions;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

public abstract class SparqlRestriction extends AbstractRestriction{

	private Query query;
	
	public SparqlRestriction(String id) {
		super(id);
	}
	
	public void setQuery(String query) {
		this.query = QueryFactory.create(query);
	}
	
	public Query getQuery() {
		return this.query;
	}
	

	public String getStringQuery() {
		return this.query.toString();
	}
	
	
	
}
