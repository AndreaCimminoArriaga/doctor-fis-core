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

public class DC11 extends SparqlRestriction {
		
		
	public DC11() {
		super("DC-11");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "PREFIX uml: <http://upm.es/uml/1.1/voc#>\n"
				+ "\n"
				+ "SELECT DISTINCT ?className WHERE {\n"
				+ "    ?class a dcf:Class .\n"
				+ "    ?class dcf:name ?className .\n"
				+ "    MINUS { ?class dcf:hasRelationship ?relationship . }\n"
				+ "    MINUS { ?class a uml:AssociationClass. }\n"
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String className = sol.get("className").toString(); 
	        ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
	    	entry.getMessages().put(  Level.MIN, "Hay un error en el diagrama de clases.");
	        entry.getMessages().put(  Level.HINT, "Hay un error con la relación que posee una de las clases en el diagrama.");
		    entry.getMessages().put(  Level.DETAILED, "Una clase no tiene relaciones con ninguna otra clase del diagrama.");
    		entry.getMessages().put(  Level.SOLUTION, "La clase '"+className+"' no posee relaciones con ninguna otra del diagrama.");
    		entries.add(entry);
	       
	    }
	    qe.close(); 
	    
	   
		return entries;
	}
	
	
}
