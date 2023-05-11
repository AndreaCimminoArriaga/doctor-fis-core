package doctor.model.restrictions.dc;

import java.util.List;
import org.apache.commons.compress.utils.Lists;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
import doctor.model.report.Type;
import doctor.model.restrictions.SparqlRestriction;

public class DC12 extends SparqlRestriction {
		
		
	public DC12() {
		super("DC-12");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "PREFIX uml: <http://upm.es/uml/1.1/voc#>\n"
				+ "\n"
				+ "SELECT DISTINCT ?label ?sC ?tC {\n"
				+ "    ?rel a ?type .\n"
				+ "    ?rel dcf:label ?label . \n"
				+ "    ?rel dcf:sourceCardinality ?sC.\n"
				+ "    ?rel dcf:targetCardinality ?tC\n"
				+ "    VALUES ?type { dcf:Association dcf:AggregationStrong dcf:AggregationWeak}\n"
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String sourceCardinality = sol.get("sC").toString(); 
	        String targetCardinality = sol.get("tC").toString(); 
	        String label = sol.get("label").toString();
	        if(sourceCardinality.equals("0") || targetCardinality.equals("0")) {
		        ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
		    	entry.getMessages().put(  Level.MIN, "Hay un error en el diagrama de clases.");
		        entry.getMessages().put(  Level.HINT, "Hay un error con una relación del diagrama de clases.");
			    entry.getMessages().put(  Level.DETAILED, "Hay un error en las cardinalidades de, por lo menos una, de las realciones del diagrama de clases.");
	    		entry.getMessages().put(  Level.SOLUTION, "La relación '"+label+"' posee una cardinalidad de 0.");
	    		entries.add(entry);
	        }
	    }
	    qe.close(); 
	    
		return entries;
	}
	

	
}
