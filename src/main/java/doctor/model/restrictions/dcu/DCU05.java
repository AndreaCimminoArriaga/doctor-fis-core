package doctor.model.restrictions.dcu;

import java.util.List;
import org.apache.commons.compress.utils.Lists;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import doctor.Utils;
import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
import doctor.model.report.Type;
import doctor.model.restrictions.SparqlRestriction;


public class DCU05 extends SparqlRestriction {
	
	
	public DCU05() {
		super("DCU-05");
		this.setQuery("PREFIX urn: <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX dcf: <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "PREFIX uml: <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "\n"
				+ "SELECT distinct  ?actorName WHERE { \n"
				+ " ?actor a dcf:Actor .\n"
				+ " ?actor dcf:name ?actorName .\n"
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String actorName = sol.get("actorName").toString(); 
	        boolean isSingular = Utils.isSingular(actorName);
	        boolean isCapitalised = Character.isUpperCase(actorName.charAt(0));

	        if(!isSingular || !isCapitalised) {
		        ReportEntry entry = ReportEntry.create(getId(),Type.ERROR);
	        	entry.getMessages().put( Level.MIN, "Hay un error en los actores del sistema.");
	        	entry.getMessages().put( Level.HINT, "Hay un error con el nombre de los actores.");
	        	entry.getMessages().put( Level.DETAILED, "Hay un actor cuyo nombre no está en singular o no empieza por mayúsculas.");

		        if(!isSingular && isCapitalised) {
		        	entry.getMessages().put( Level.SOLUTION, "El actor '"+actorName+"' posee un nombre en plural y solo se permiten singulares.");
		        }else if(isSingular && !isCapitalised) {
		        	entry.getMessages().put( Level.SOLUTION, "El actor '"+actorName+"' posee un nombre que no empieza por mayúscula.");
		        } else if(!isSingular&& !isCapitalised) {
		        	entry.getMessages().put( Level.SOLUTION, "El actor '"+actorName+"' posee un nombre que no empieza por mayúscula y además está en plural.");
		        }
		        entries.add(entry);
	        }
	    }

	    qe.close(); 
		
		return entries;
	}
	
	
}
