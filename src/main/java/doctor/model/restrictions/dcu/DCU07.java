package doctor.model.restrictions.dcu;

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

public class DCU07 extends SparqlRestriction {
	
	
	public DCU07() {
		super("DCU-07");
		this.setQuery("PREFIX urn: <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX dcf: <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "PREFIX uml: <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "\n"
				+ "SELECT distinct  ?ucName WHERE { \n"
				+ " ?uc a dcf:UseCase .\n"
				+ " ?uc dcf:name ?ucName .   \n"
				
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String ucName = sol.get("ucName").toString(); 
	        if(!ucName.matches(".*\\d{4,5}.*")) {
		        ReportEntry entry = ReportEntry.create(getId(),Type.ERROR);
	        	entry.getMessages().put( Level.MIN, "Hay un error con los casos de use del sistema.");
	        	entry.getMessages().put( Level.HINT, "Hay un error de trazabilidad en los casos de uso.");
	        	entry.getMessages().put(Level.DETAILED, "Hay un caso de uso que no posee un código que permita su trazabilidad.");
	        	entry.getMessages().put(Level.SOLUTION, "El caso de uso '"+ucName+"' carece de un código que premita su trazabilidad.");
		        entries.add(entry);
	        }
	    }

	    qe.close(); 
		
		return entries;
	}
}
