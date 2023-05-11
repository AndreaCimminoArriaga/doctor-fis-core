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

public class DC03 extends SparqlRestriction {
		
		
	public DC03() {
		super("DC-03");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  uml:  <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "\n"
				+ "SELECT DISTINCT ?classN1 ?classN2 WHERE {\n"
				+ "    ?class1 a dcf:Class .\n"
				+ "    ?class1 dcf:name ?classN1 .\n"
				+ "    ?class2 a dcf:Class .\n"
				+ "    ?class2 dcf:name ?classN2 .\n"
				+ "    FILTER(?class1 != ?class2 && ?classN1 = ?classN2)\n"
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String className = sol.get("classN1").toString(); 
	        ReportEntry entry = ReportEntry.create(getId(),Type.ERROR);
        	entry.getMessages().put(Level.MIN, "Hay un error en las clases del sistema.");
        	entry.getMessages().put(Level.HINT, "Hay un error con algún nombre de las clases del sistema.");
        	entry.getMessages().put(Level.DETAILED, "Hay un error con el nombre de dos clases del sistema.");
        	entry.getMessages().put(Level.SOLUTION, "Hay dos clases que poseen el mismo nombre, '"+className+"', a pesar de ser clases distintas.");
        	entries.add(entry);
	    }
	    

	    qe.close(); 
		
		return entries;
	}
	

}
