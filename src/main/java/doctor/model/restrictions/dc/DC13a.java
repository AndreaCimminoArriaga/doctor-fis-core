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

public class DC13a extends SparqlRestriction {
		
		
	public DC13a() {
		super("DC-13a");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "PREFIX uml: <http://upm.es/uml/1.1/voc#>\n"
				+ "\n"
				+ "SELECT DISTINCT ?label ?label2 {\n"
				+ "    ?rel a ?type .\n"
				+ "    ?rel dcf:label ?label . \n"
				+ "    \n"
				+ "    ?rel2 a ?type .\n"
				+ "    ?rel2 dcf:label ?label2 . \n"
				+ "    FILTER (?rel != ?rel2 && ?label=?label2 && strlen(?label) > 0 )\n"
				+ "    \n"
				+ "    VALUES ?type { dcf:Association dcf:AggregationStrong dcf:AggregationWeak}\n"
				+ "}\n"
				+ "\n"
				+ "");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String repeatedLabel = sol.get("label").toString(); 
	        ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
	    	entry.getMessages().put(  Level.MIN, "Hay un error en el diagrama de clases.");
	        entry.getMessages().put(  Level.HINT, "Hay un error con, por lo menos una, de las relaciones diagrama.");
		    entry.getMessages().put(  Level.DETAILED, "Dos relaciones del diagrama son incorrectas.");
    		entry.getMessages().put(  Level.SOLUTION, "Dos relaciones del diagrama se llaman igual, esto es '"+repeatedLabel+"' ");
    		entries.add(entry);
	       
	    }
	    qe.close(); 
	    
	   
		return entries;
	}
	
	
}
