package doctor.model.restrictions.dc;

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


public class DC02 extends SparqlRestriction {
	
	
	public DC02() {
		super("DC-02");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "\n"
				+ "SELECT DISTINCT ?className WHERE {\n"
				+ "    ?class a dcf:Class .\n"
				+ "    ?class dcf:name ?className .\n"
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
	        String className = sol.get("className").toString(); 
	        boolean isSingular = Utils.isSingular(className);
	        boolean isCapitalised = Character.isUpperCase(className.charAt(0));
	        
	        if(!isSingular || !isCapitalised) {
		        ReportEntry entry = ReportEntry.create(getId(),Type.ERROR);
	        	entry.getMessages().put( Level.MIN, "Hay un error en las clases del sistema.");
	        	entry.getMessages().put( Level.HINT, "Hay un error con el nombre de, por lo menos una, clase del sistema.");
	        	entry.getMessages().put( Level.DETAILED, "Hay una clase cuyo nombre no es singular o no empieza por mayúsculas.");

		        if(!isSingular && isCapitalised) {
		        	entry.getMessages().put( Level.SOLUTION, "La clase '"+className+"' posee un nombre en plural y solo se permiten singulares.");
		        }else if(isSingular && !isCapitalised) {
		        	entry.getMessages().put( Level.SOLUTION, "La clase '"+className+"' posee un nombre que no empieza por mayúscula.");
		        } else if(!isSingular&& !isCapitalised) {
		        	entry.getMessages().put( Level.SOLUTION, "La clase '"+className+"' posee un nombre que no empieza por mayúscula y además está en plural.");
		        }
		        entries.add(entry);
	        }
	    }

	    qe.close(); 
		
		return entries;
	}
	
	
}
