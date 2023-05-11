package doctor.model.restrictions.dc;

import java.util.List;
import org.apache.commons.compress.utils.Lists;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;

import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
import doctor.model.report.Type;
import doctor.model.restrictions.SparqlRestriction;

public class DC06 extends SparqlRestriction {
		
		
	public DC06() {
		super("DC-06");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "PREFIX uml: <http://upm.es/uml/1.1/voc#>\n"
				+ "\n"
				+ "ASK {\n"
				+ "    ?comment a dcf:Comment .\n"
				+ "    ?comment dcf:comment ?text .\n"
				+ "   \n"
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    Boolean exist = qe.execAsk();
	    if (!exist) {
	        ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
	    	entry.getMessages().put(  Level.MIN, "Hay un error en el diagrama de clases.");
	        entry.getMessages().put(  Level.HINT, "El diagrama de clases carece de un elemento.");
		    entry.getMessages().put(  Level.DETAILED, "Hay un error relacionado con los getters, setters, constructores, y destructores.");
    		entry.getMessages().put(  Level.SOLUTION, "El diagrama carece de una nota que indique que se asume que todas las clases poseen getters, setters, constructores, y destructores.");
    		entries.add(entry);
	    }
	    qe.close(); 
	    
	   
		return entries;
	}
	
	
}
