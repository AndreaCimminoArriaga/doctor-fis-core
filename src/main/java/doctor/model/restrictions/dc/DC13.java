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

public class DC13 extends SparqlRestriction {
		
		
	public DC13() {
		super("DC-13");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "PREFIX uml: <http://upm.es/uml/1.1/voc#>\n"
				+ "\n"
				+ "SELECT DISTINCT ?sourceName ?targetName {\n"
				+ "    ?rel a ?type .\n"
				+ "    MINUS { ?rel dcf:label ?label . }\n"
				+ "    ?rel dcf:hasTargetElement ?target.\n"
				+ "    ?rel dcf:hasSourceElement ?source .\n"
				+ "    ?target dcf:name ?targetName .\n"
				+ "    ?source dcf:name ?sourceName .\n"
				+ "    VALUES ?type { dcf:Association dcf:AggregationStrong dcf:AggregationWeak}\n"
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String sourceClass = sol.get("sourceName").toString(); 
	        String targetClass = sol.get("targetName").toString(); 
	        ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
	    	entry.getMessages().put(  Level.MIN, "Hay un error en el diagrama de clases.");
	        entry.getMessages().put(  Level.HINT, "Hay un error con, por lo menos una, de las relaciones diagrama.");
		    entry.getMessages().put(  Level.DETAILED, "Una de las relaciones en el diagrama carece de un elemento importante.");
    		entry.getMessages().put(  Level.SOLUTION, "La relación entre las clases '"+sourceClass+"' y '"+targetClass+"'no posee un nombre.");
    		entries.add(entry);
	       
	    }
	    qe.close(); 
	    
	   
		return entries;
	}
	
	
}
