package doctor.model.restrictions.dc;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import com.github.jsonldjava.shaded.com.google.common.collect.Maps;
import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
import doctor.model.report.Type;
import doctor.model.restrictions.SparqlRestriction;

public class DC04 extends SparqlRestriction {
		
		
	public DC04() {
		super("DC-04");
		this.setQuery("PREFIX urn: <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX dcf: <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "PREFIX uml: <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "\n"
				+ "SELECT distinct ?parentName ?childName ?label WHERE { \n"
				+ "\n"
				+ "  ?inherit a dcf:Generalization .\n"
				+ "    OPTIONAL { ?inherit dcf:label ?label } .\n"
				+ "  ?inherit dcf:hasSourceElement ?child .\n"
				+ "  ?child a dcf:Class .\n"
				+ "  ?child dcf:name ?childName .\n"
				+ "  ?inherit dcf:hasTargetElement ?parent .  \n"
				+ "  ?parent dcf:name ?parentName .\n"
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    Map<String, String> maps = Maps.newHashMap();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        // ? ?childName ?label
	        String parent = sol.get("parentName").toString(); 
	        String label = processNode(sol.get("label")); 
	        if(!label.isEmpty()) {
	        	maps.put(parent, label);
	        }else if(label.isEmpty() && !maps.containsKey(parent)) {
	        	maps.put(parent, "None");
	        }
	    }
	    qe.close(); 
	    
	    List<String> lonlyParents = maps.entrySet().parallelStream().filter(entry -> entry.getValue().equals("None")).map(entry -> entry.getKey()).collect(Collectors.toList());
	    
	    for(String parent: lonlyParents) {
	    	ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
	    	entry.getMessages().put(  Level.MIN, "Hay un error en el diagrama de clases.");
	        entry.getMessages().put(  Level.HINT, "Hay un error en las herencias del diagrama de clases.");
		    entry.getMessages().put(  Level.DETAILED, "Hay herencias sin categorizar.");    
	        entry.getMessages().put(  Level.SOLUTION, "La herencia de la super clase '"+parent+"' carece de una categorizacion Total/Parcial y Disjunta/NoDisjunta/Solapada.");
	        entries.add(entry);
	    }
	    
	    List<Entry<String,String>> parents = maps.entrySet().parallelStream().filter(entry -> !entry.getValue().equals("None")).collect(Collectors.toList());
	    for(Entry<String,String> parentEntry: parents) {
	    	if(!isTagcorrect(parentEntry.getValue())) {
				ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);

	    		entry.getMessages().put(  Level.MIN, "Hay un error en el diagrama de clases.");
		        entry.getMessages().put(  Level.HINT, "Hay un error en las herencias del diagrama de clases.");
			    entry.getMessages().put(  Level.DETAILED, "Hay herencias sin categorizar correctamente.");
	    		entry.getMessages().put(  Level.SOLUTION, "La herencia de la clase padre '"+parentEntry.getKey()+"' carece de una categorizacion correcta, Total/Parcial y Disjunta/NoDisjunta/Solapada.");
	    		entries.add(entry);
	    	}
	    }
		
		return entries;
	}
	
	private boolean isTagcorrect(String name) {
		String tag = name.toLowerCase().replace(" ", "");
		boolean correct = tag.contains("solapada") || tag.contains("disjunta") || tag.contains("nodisjunta");
		correct &= tag.contains("total") || tag.contains("parcial");
		return correct;
	}
	
	private String processNode(RDFNode node) {
		String result = "";
		if(node!=null)
			result = node.toString();
		return result;
	}
}
