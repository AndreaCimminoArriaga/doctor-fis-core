package doctor.model.restrictions.dcu;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.compress.utils.Lists;
import org.apache.jena.ext.com.google.common.collect.Maps;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
import doctor.model.report.Type;

import org.apache.jena.query.QuerySolution;
import doctor.model.restrictions.SparqlRestriction;

public class DCU04 extends SparqlRestriction{

	
	public DCU04() {
		super("DCU-04");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "\n"
				+ "SELECT DISTINCT ?actorN1 ?ucName  WHERE { \n"
				+ "            ?actor1  a dcf:Actor .\n"
				+ "            ?actor1  dcf:name ?actorN1 .\n"
				+ "            ?actor1  dcf:hasRelationship ?relation1 .\n"
				+ "            ?relation1 ?targets1 ?uc .\n"
				+ "            ?uc a dcf:UseCase .\n"
				+ "            ?uc dcf:name ?ucName.    \n"
				+ "}");
	}
	
	public List<ReportEntry> evaluate(Model model){
		List<ReportEntry> entries = Lists.newArrayList();
		
		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
	    ResultSet rs = qe.execSelect();
	    Map<String,String> ucs = Maps.newHashMap();
	    while(rs.hasNext()){
	        QuerySolution sol = rs.nextSolution();
	        String actor1 = sol.get("actorN1").toString(); 
	        String uc = sol.get("ucName").toString(); 
	        String old = ucs.get(actor1);
	        if(old==null) {
	        	old = uc+",";
	        }else {
	        	old += uc+",";
	        }
	        ucs.put(actor1, old);
	    }
	    Map<Integer,String> hashes = Maps.newHashMap();
	    for(Entry<String,String> entry:ucs.entrySet()) {
	    	Integer hash = entry.getValue().hashCode();
	    	if(hashes.containsKey(hash)) {
	    		String oldActor = hashes.get(hash);
	    		 ReportEntry reportEntry = ReportEntry.create(getId(),Type.ERROR);
	    		 reportEntry.getMessages().put(Level.MIN, "Hay un error con los actores del sistema.");
	    		 reportEntry.getMessages().put(Level.HINT, "Hay actores redundantes.");
	    		 reportEntry.getMessages().put(Level.DETAILED, "Hay dos actores que poseen exactamente los mismos casos de uso.");
	    		 reportEntry.getMessages().put(Level.SOLUTION, "Dos actores del sistema ("+oldActor+", "+entry.getKey()+") poseen exatamente los mismos casos de uso");
	    	     entries.add(reportEntry);
	    	}else {
	    		hashes.put(hash, entry.getKey());
	    	}
	    }
	    
	   

	    qe.close(); 
		
		return entries;
	}
}
