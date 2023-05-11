package doctor.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.jena.ext.com.google.common.collect.Maps;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

public class Comparison {
	private static Map<String,String> synonyms = Maps.newHashMap();
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
	
	
	
	
	
	//public static StringMetric ;

	public static Float stringComparison(String str1, String str2) {
//		Float cosine = StringMetrics.cosineSimilarity().compare(n1, n2);
//		Float wunch = StringMetrics.needlemanWunch().compare(n1, n2);
//		Float edit = StringMetrics.levenshtein().compare(n1, n2);
//		Float commonSubstring = StringMetrics.longestCommonSubstring().compare(n1, n2);
		return StringMetrics.jaroWinkler().compare(str1, str2);
	}
	
	
	
	
	private static Float similarity(String n1, String n2) {
		Float cosine = StringMetrics.cosineSimilarity().compare(n1, n2);
		Float wunch = StringMetrics.needlemanWunch().compare(n1, n2);
		Float edit = StringMetrics.levenshtein().compare(n1, n2);
		Float commonSubstring = StringMetrics.longestCommonSubstring().compare(n1, n2);
		
		//System.out.println(n1+" "+n2);
		//System.out.println("\t\t-Cosine: "+cosine+" Wunch:"+wunch+" Edit:"+edit+" Jarow:"+jaroW+" cmSub:"+commonSubstring);
		return StringMetrics.jaroWinkler().compare(n1, n2);
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


	
	
	
}
