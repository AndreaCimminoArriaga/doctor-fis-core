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


public class DCU01 extends SparqlRestriction {
	
	
	public DCU01() {
		super("DCU-01");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  uml:  <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "\n"
				+ "SELECT DISTINCT  ?actorName (COUNT(DISTINCT ?relationships) AS ?ucs) WHERE { \n"
				+ "    ?actor rdf:type dcf:Actor .\n"
				+ "    ?actor dcf:name ?actorName .\n"
				+ "    OPTIONAL { \n"
				+ "         ?actor dcf:hasRelationship  ?relationships .\n"
				+ "         ?relationships  rdf:type dcf:UseCaseAssociation \n"
				+ "    }\n"
				+ "  }\n"
				+ "GROUP BY ?actorName");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String actorName = sol.get("actorName").toString();
	        Integer ucs = Integer.valueOf(sol.get("ucs").asLiteral().getInt());
	        if(ucs == 0) {
		        ReportEntry entry = ReportEntry.create(getId(),Type.ERROR);
	        	entry.getMessages().put(Level.MIN, "Hay un error en los actores del sistema.");
	        	entry.getMessages().put(Level.HINT, "Hay un error con la relación entre actores y casos de uso.");
	        	entry.getMessages().put(Level.DETAILED, "Hay un actor que no está relacionado con ningún caso de uso.");
	        	entry.getMessages().put(Level.SOLUTION, "El actor '"+actorName+"' no está relacionado con ningún caso de uso.");
	        	entries.add(entry);
	        }
	    }

	    qe.close(); 
		
		return entries;
	}
	
}
