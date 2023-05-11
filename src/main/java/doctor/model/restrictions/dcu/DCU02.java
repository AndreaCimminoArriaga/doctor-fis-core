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


public class DCU02 extends SparqlRestriction {
	
	
	public DCU02() {
		super("DCU-02");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  uml:  <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "\n"
				+ "SELECT DISTINCT  ?ucName (COUNT(DISTINCT ?entity) AS ?relations)\n"
				+ "WHERE\n"
				+ "  { ?uc  rdf:type  dcf:UseCase ;\n"
				+ "         dcf:name  ?ucName\n"
				+ "    OPTIONAL\n"
				+ "      { ?uc       dcf:hasRelationship  ?relationships .\n"
				+ "        ?relationships rdf:type ?type ;\n"
				+ "                  ?anyRelationship     ?entity .\n"
				+ "        ?entity   rdf:type             ?entityType\n"
				+ "        VALUES ?entityType { dcf:Actor uml:UseCase }\n"
				+ "        FILTER ( ?uc != ?entity )\n"
				+ "      }\n"
				+ "    \n"
				+ "  }\n"
				+ "GROUP BY ?ucName\n"
				+ "HAVING ( ?relations = 0 )");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String ucName = sol.get("ucName").toString();
	        Integer relations = Integer.valueOf(sol.get("relations").asLiteral().getInt());
	        if(relations == 0) {
		        ReportEntry entry = ReportEntry.create(getId(),Type.ERROR);
	        	entry.getMessages().put(Level.MIN, "Hay un error en los casos de uso del sistema.");
	        	entry.getMessages().put(Level.HINT, "Hay un error con las relaciones de, por lo menos, un caso de uso.");
	        	entry.getMessages().put(Level.DETAILED, "Hay un caso de uso que no está relacionado con ningún caso de uso ni con un actor.");
	        	entry.getMessages().put(Level.SOLUTION, "El caso de uso '"+ucName+"' no está relacionado con ningún caso de uso ni con un actor.");
	        	entries.add(entry);
	        }
	    }

	    qe.close(); 
		
		return entries;
	}
	
}
