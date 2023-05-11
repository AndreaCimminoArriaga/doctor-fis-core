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
import doctor.model.restrictions.Restrictions;
import doctor.model.restrictions.SparqlRestriction;

public class DCU10 extends SparqlRestriction {
	
	
	public DCU10() {
		super("DCU-10");
		this.setQuery("PREFIX urn: <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX dcf: <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "\n"
				+ "SELECT distinct ?name WHERE { \n"
				+ " ?actor a dcf:Actor .\n"
				+ " ?actor dcf:name ?name .\n"
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    while(rs.hasNext())
	    {
	        QuerySolution sol = rs.nextSolution();
	        String actor = sol.get("name").toString(); 
	        if(Restrictions.systemLabel.contains(actor.toLowerCase())) {
		        ReportEntry entry = ReportEntry.create(getId(),Type.ERROR);
	        	entry.getMessages().put( Level.MIN, "Hay un error con los actores del sistema.");
	        	entry.getMessages().put(Level.HINT, "Hay un actor idenficado incorrectamente.");
	        	entry.getMessages().put(Level.DETAILED, "El actor '"+actor+"' no puede ser una representación del sistema.");
	        	entry.getMessages().put(Level.SOLUTION, "El actor '"+actor+"' no puede ser una representación del sistema.");
		        entries.add(entry);
	        }
	    }

	    qe.close(); 
		
		return entries;
	}
}
