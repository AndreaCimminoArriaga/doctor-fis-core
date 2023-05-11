package doctor;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.jena.ext.com.google.common.collect.Maps;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.exec.RowSet;
import org.apache.jena.sparql.exec.http.QueryExecHTTPBuilder;
import org.simmetrics.metrics.StringMetrics;

public class MainComparison {
	private static Map<String,String> synonyms = Maps.newHashMap();
	private static Set<String> common = Sets.newHashSet();
	private static Set<String> ucsSolution = Sets.newHashSet();
	private static Set<String> ucs = Sets.newHashSet();
	public static void main(String[] args) throws IOException {
//		String query = "PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
//				+ "PREFIX  uml:  <http://upm.es/uml/1.1/voc#>\n"
//				+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
//				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
//				+ "PREFIX fn: <http://example.org/function#>\n"
//				+ "\n"
//				+ "SELECT DISTINCT ?ucname1 ?ucname2  \n"
//				+ "WHERE\n"
//				+ "  { \n"
//				+ "    GRAPH <http://upm.es/project/iwsit21-02> {\n"
//				+ "     ?uc1 rdf:type dcf:UseCase .\n"
//				+ "     ?uc1 dcf:name ?ucname1 .\n"
//				+ "     }\n"
//				+ "    GRAPH <http://upm.es/project/solution> {\n"
//				+ "     ?uc2 rdf:type dcf:UseCase .\n"
//				+ "     ?uc2 dcf:name ?ucname2 .\n"
//				+ "    }\n"
//				+ "  \n"
//			//	+ "   BIND( fn:cosine(?ucname1, ?ucname2) as ?score)"
//				+ "  }\n"
//				+ "\n"
//				+ "";
		String query = "PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  uml:  <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "\n"
				+ "SELECT DISTINCT ?ucname1 ?ucname2\n"
				+ "WHERE\n"
				+ "  { \n"
				+ "    GRAPH <http://upm.es/project/iwsit21-02> {\n"
				+ "     ?uc1 rdf:type dcf:Class .\n"
				+ "     ?uc1 dcf:name ?ucname1 .\n"
				+ "        MINUS {?uc1 rdf:type dcf:AssociationClass} .\n"
				+ "     }\n"
				+ "    GRAPH <http://upm.es/project/solution> {\n"
				+ "     ?uc2 rdf:type dcf:Class .\n"
				+ "     ?uc2 dcf:name ?ucname2 .\n"
				+ "         MINUS {?uc2 rdf:type dcf:AssociationClass} .\n"
				+ "    }\n"
				+ "  \n"
				+ "  }\n"
				+ "\n"
				+ "";
		RowSet rows = QueryExecHTTPBuilder.service("http://localhost:7200/repositories/doctorfis").query(query).select();
		while(rows.hasNext()) {
			Binding bd = rows.next();
			String n1 =clean( bd.get("ucname1").getLiteral().getValue().toString());
			String n2 =clean( bd.get("ucname2").getLiteral().getValue().toString());
			Float score = similarity(n1, n2); 
			if(score >= 0.95) {
				System.out.println(n1+" "+n2+" "+score);
				common.add(n1);
				ucsSolution.remove(n2);
				ucs.remove(n1);
			}else {
				//System.out.println("-\t"+n1+" "+n2+" "+score);

				ucsSolution.add(n2);
				ucs.add(n1);
			}
		}
		
		System.out.println(ucsSolution.size()+" "+ucs.size()+" found "+common.size());
		System.out.println("-------");
		System.out.println(ucs);
		System.out.println("****");
		System.out.println(ucsSolution);
	}
	
	private static Float similarity(String n1, String n2) {
		Float cosine = StringMetrics.cosineSimilarity().compare(n1, n2);
		Float wunch = StringMetrics.needlemanWunch().compare(n1, n2);
		Float edit = StringMetrics.levenshtein().compare(n1, n2);
		Float jaroW = StringMetrics.jaroWinkler().compare(n1, n2);
		Float commonSubstring = StringMetrics.longestCommonSubstring().compare(n1, n2);
		
		//System.out.println(n1+" "+n2);
		//System.out.println("\t\t-Cosine: "+cosine+" Wunch:"+wunch+" Edit:"+edit+" Jarow:"+jaroW+" cmSub:"+commonSubstring);
		return jaroW;
	}
	
	private static String clean(String s) {
		return synonims(s.replaceAll("\\d*", "").replace("[", "").replace("]","").replace("#", "").replace("_", "").replace(" ", "").trim().toLowerCase());
	}


	private static String synonims(String word) {
		for(Entry<String,String> entry:synonyms.entrySet()) {
			if(word.startsWith(entry.getKey())) {
				word = word.replace(entry.getKey(), entry.getValue());
				break;
			}
		}
		
		return word;//Utils.getSingular(word);
	}
	
	
	static{
		synonyms.put("daralta", "crear");
		synonyms.put("alta", "crear");
		synonyms.put("dardealta", "crear");
		synonyms.put("darbaja", "eliminar");
		synonyms.put("baja", "eliminar");
		synonyms.put("dardebaja", "eliminar");
		synonyms.put("modificar", "editar");
		synonyms.put("inscripcion", "inscribirse");
		synonyms.put("apuntarse", "inscribirse");
		synonyms.put("estudiante", "alumno");
		//synonyms.put("reserva", "reservar");
	}
	
}
