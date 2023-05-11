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

public class DC05 extends SparqlRestriction {
		
		
	public DC05() {
		super("DC-05");
		this.setQuery("PREFIX urn: <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX dcf: <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "PREFIX uml: <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "\n"
				+ "SELECT distinct ?attrName WHERE { \n"
				+ "\n"
				+ "  ?attr a dcf:ClassAttribute .\n"
				+ "  ?attr dcf:name ?attrName .\n"
				+ "  ?attr dcf:visibility dcf:Public .\n"
				+ "  MINUS {?attr a uml:EnumerationLiteral}\n"
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String attrName = sol.get("attrName").toString();
	        ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
	    	entry.getMessages().put(  Level.MIN, "Hay un error en el diagrama de clases.");
	        entry.getMessages().put(  Level.HINT, "Hay un error en los atributos del diagrama de clases.");
		    entry.getMessages().put(  Level.DETAILED, "Hay atributos que deberían ser privados.");
    		entry.getMessages().put(  Level.SOLUTION, "El atributo '"+attrName+"' se ha especificado público pero debería ser privado.");
    		entries.add(entry);
	       
	    }
	    qe.close(); 
	    
	   
		return entries;
	}
	
	
}
