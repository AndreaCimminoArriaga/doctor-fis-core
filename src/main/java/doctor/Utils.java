package doctor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.apache.jena.ext.com.google.common.collect.Maps;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import helio.blueprints.TranslationUnit;
import helio.blueprints.UnitBuilder;
import helio.blueprints.components.ComponentType;
import helio.blueprints.components.Components;
import helio.blueprints.exceptions.ExtensionNotFoundException;
import helio.blueprints.exceptions.TranslationUnitExecutionException;
import helio.builder.siot.rx.SIoTRxBuilder;

public class Utils {

	
	
	
	public static String clean(String s) {
		if(s!=null)
			return s.replaceAll("\\d*", "").replace("[", "").replace("]", "").replace("#", "").replace("_", "")
				.replace(" ", "").trim().toLowerCase();
		return null;
	}
	
	public static String readFile(String file) {
		File fileF = new File(file);

		try {
			return FileUtils.readFileToString(fileF, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static Model readModel(String data, String format) {
		Model model = ModelFactory.createDefaultModel();
		model.read(new ByteArrayInputStream(data.getBytes()), null, format);
		return model;
	}
	
	public static String makePost(String link, String body) {
		String result=null;
		try {
			result = Request.post(link).addHeader("Accept", "application/json").bodyString(body, ContentType.APPLICATION_JSON)
	        .execute()
	        .returnContent().asString();
			
		}catch(Exception e) {
			
			System.out.println(link+" "+e.getMessage());
		}
		return result;
	}
	
	public static boolean isSingular(String word) {
		boolean isSingular = true; 
		String body = "{ \"filter\": [ ], \"form\": \"LEMMA\", \"lang\": \"es\", \"multigrams\": false, \"text\": \""+word.toLowerCase()+"\" }";
		String raw = Utils.makePost("https://librairy.linkeddata.es/nlp/annotations", body);
		isSingular = raw!=null;
		if(isSingular) {
		    JsonObject object = (new Gson()).fromJson(raw, JsonObject.class);
		    if(object.has("annotatedText")) {
		    	JsonArray array = object.get("annotatedText").getAsJsonArray();
		    	
		    	for(int index=0 ; index < array.size(); index++) {
		    	JsonObject annotatedText = array.get(index).getAsJsonObject().get("token").getAsJsonObject();
		    	String lemma = annotatedText.get("lemma").getAsString().toLowerCase();
		    	String target = annotatedText.get("target").getAsString().toLowerCase();
		    	isSingular &= lemma.equals(target);
		    	if(!isSingular) 
		    		isSingular =  annotatedText.get("pos").getAsString().equals("INTERJECTION") || annotatedText.get("pos").getAsString().equals("ADJECTIVE")|| annotatedText.get("pos").getAsString().equals("VERB");
		    	
		    	}
		    }
		}
		return isSingular;
	}
	
	public static String getSingular(String word) {
		boolean isSingular = true;
		String body = "{ \"filter\": [ ], \"form\": \"LEMMA\", \"lang\": \"es\", \"multigrams\": false, \"text\": \""+word.toLowerCase()+"\" }";
		String raw = Utils.makePost("https://librairy.linkeddata.es/nlp/annotations", body);
		isSingular = raw!=null;
		String singular = word;
		if(isSingular) {
		    JsonObject object = (new Gson()).fromJson(raw, JsonObject.class);
		    if(object.has("annotatedText")) {
		    	JsonArray array = object.get("annotatedText").getAsJsonArray();
		    	
		    	for(int index=0 ; index < array.size(); index++) {
		    	JsonObject annotatedText = array.get(index).getAsJsonObject().get("token").getAsJsonObject();
		    	String lemma = annotatedText.get("lemma").getAsString().toLowerCase();
		    	String target = annotatedText.get("target").getAsString().toLowerCase();
		    	isSingular &= lemma.equals(target);
		    	if(!isSingular && ! (annotatedText.get("pos").getAsString().equals("INTERJECTION") || annotatedText.get("pos").getAsString().equals("ADJECTIVE")|| annotatedText.get("pos").getAsString().equals("VERB"))) {
		    		singular = lemma;
		    		break;
		    	}
		    		
		    }
		}
		}
		return singular;
	}
	
}
