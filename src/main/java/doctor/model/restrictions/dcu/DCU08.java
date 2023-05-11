package doctor.model.restrictions.dcu;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;
import org.apache.jena.ext.com.google.common.collect.Maps;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
import doctor.model.report.Type;
import doctor.model.restrictions.SparqlRestriction;

public class DCU08 extends SparqlRestriction {
	
	
	public DCU08() {
		super("DCU-08");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "SELECT DISTINCT  ?ucSuper ?superActor  ?ucSub  ?subActor  {\n"
				+ "    \n"
				+ "    ?inherit a dcf:Generalization .\n"
				+ "    ?inherit dcf:hasSourceElement ?uc1 .\n"
				+ "    ?inherit dcf:hasTargetElement ?uc2 .\n"
				+ "    ?uc1 a dcf:UseCase .\n"
				+ "    ?uc1 dcf:name ?ucSub .\n"
				+ "    OPTIONAL{  ?uc1 dcf:hasRelationship  ?relationship1 .\n"
				+ "    ?relationship1 ?targets1 ?actor1 .\n"
				+ "    ?actor1 a dcf:Actor .\n"
				+ "    ?actor1 dcf:name ?subActor .}\n"
				+ "    \n"
				+ "    ?uc2 a dcf:UseCase .\n"
				+ "    ?uc2 dcf:name ?ucSuper .\n"
				+ "    ?uc2 dcf:hasRelationship  ?relationship2 .\n"
				+ "   \n"
				+ "    OPTIONAL{  ?uc2 dcf:hasRelationship  ?relationship2 .\n"
				+ "    ?relationship2 ?targets2 ?actor2 .\n"
				+ "    ?actor2 a dcf:Actor .\n"
				+ "    ?actor2 dcf:name ?superActor .}\n"
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    Map<String,String> superUcs = Maps.newHashMap();
	    Map<String,String> childs = Maps.newHashMap();
	    Map<String,String> subUcs = Maps.newHashMap();

	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String superActor =  processNode(sol.get("superActor"));
	        String subActor =  processNode(sol.get("subActor"));
	        String superUC =  processNode(sol.get("ucSuper"));
	        String subUC =  processNode(sol.get("ucSub"));
	        // 
	        updateMap(superUcs, superUC, superActor);
	        updateMap(childs, superUC, subUC);
	        updateMap(subUcs, subUC, subActor);
	    }
	
	    entries = checkInheritance(superUcs, childs, subUcs);
	   
	    qe.close(); 
		
		return entries;
	}
	
	private String processNode(RDFNode node) {
		if(node==null)
			return null;
		return node.toString();
	}
	
	private void updateMap(Map<String,String> map, String key, String value) {
		String oldValue = map.get(key);
		if(value==null)
			value="";
		if(oldValue == null || value.isEmpty()) {
			oldValue = value;
		}else {
			oldValue += ","+ value;
		}
		map.put(key, oldValue);
	}

	private List<ReportEntry> checkInheritance(Map<String, String> superUcs, Map<String, String> childs, Map<String, String> subUcs) {
		List<ReportEntry> entries = Lists.newArrayList();
        
		List<String> parents = childs.entrySet().parallelStream().map(entry -> entry.getKey()).collect(Collectors.toList());
		
		for(int index=0; index < parents.size(); index++) {
			String parent = parents.get(index);
			List<String> children = children(childs, parent);
			List<String> actorsParent = children(superUcs, parent);
			for(int marker=0; marker < children.size(); marker++) {
				String child = children.get(marker);
				List<String> actors = children(subUcs, child);
				if(actors.isEmpty()) { 
					// A child UC must always be related to an actor
					ReportEntry entry = ReportEntry.create(getId(),Type.ERROR);
			    	entry.getMessages().put( Level.MIN, "Hay un error con los casos de use del sistema.");
			    	entry.getMessages().put( Level.HINT, "Hay un error en la herencia en los casos de uso.");
			    	entry.getMessages().put(Level.DETAILED, "Hay un error con un caso de uso que hereda de otro.");
			    	entry.getMessages().put(Level.SOLUTION, "El caso de uso '"+child+"' no está relacionado con ningún actor, a pesar de ser sub-caso de uso de '"+parent+"' que posee más de 1 sub-caso de uso.");
			        entries.add(entry);
				}
			}
			if(children.size()== 1 && actorsParent.size()==0) {
				// A parent UC must always be related to an actor is the number of children is 1
				ReportEntry entry = ReportEntry.create(getId(),Type.ERROR);
		    	entry.getMessages().put( Level.MIN, "Hay un error con los casos de use del sistema.");
		    	entry.getMessages().put( Level.HINT, "Hay un error en la herencia en los casos de uso.");
		    	entry.getMessages().put(Level.DETAILED, "Hay un error con un casos de uso que es super en la herencia.");
		    	entry.getMessages().put(Level.SOLUTION, "El caso de uso '"+parent+"' no está relacionado con ningún actor, a pesar de tener unicamente un sub-caso de uso de '"+children.get(0)+"'.");
		        entries.add(entry);
			}
		}
		return entries;
		
	}
	
	private List<String> children(Map<String, String> childs, String parent){
		String children = childs.get(parent);
		List<String> result = Lists.newArrayList();
		if(children!=null && !children.isEmpty())
			result = Lists.newArrayList(Arrays.asList(children.split(",")).iterator());
		return result;
	}
	
	
}
