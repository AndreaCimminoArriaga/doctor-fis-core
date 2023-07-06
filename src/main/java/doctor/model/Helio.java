package doctor.model;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.jena.ext.com.google.common.collect.Maps;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import doctor.Utils;
import helio.blueprints.TranslationUnit;
import helio.blueprints.UnitBuilder;
import helio.blueprints.components.ComponentType;
import helio.blueprints.components.Components;
import helio.blueprints.exceptions.ExtensionNotFoundException;
import helio.builder.siot.rx.SIoTRxBuilder;

public class Helio {

	private static ExecutorService service = Executors.newScheduledThreadPool(10);
	private static  String mappingConnectors = "./cmp/ConnectorsDoctorFis.ftl";
	private static  String mappingElements = "./cmp/ElementsDoctorFis.ftl";
	private static  String mappingModel = "./cmp/ModelDoctorFis.ftl";
	
	static {
		try {
			Components.registerAndLoad("./cmp/helio-provider-url-0.1.0.jar", "provider.URLProvider", ComponentType.PROVIDER);
			Components.registerAndLoad("./cmp/helio-action-json-cast-0.1.0.jar", "helio.actions.JsonCast", ComponentType.ACTION);
		} catch (ExtensionNotFoundException e) {
			e.printStackTrace();
		}

	}
	

	public static String getMappingConnectors() {
		return mappingConnectors;
	}


	public static void setMappingConnectors(String mappingConnectors) {
		Helio.mappingConnectors = mappingConnectors;
	}


	public static String getMappingElements() {
		return mappingElements;
	}


	public static void setMappingElements(String mappingElements) {
		Helio.mappingElements = mappingElements;
	}


	public static String getMappingModel() {
		return mappingModel;
	}


	public static void setMappingModel(String mappingModel) {
		Helio.mappingModel = mappingModel;
	}


	public static Model toRDF(String uml) {
		Model model = ModelFactory.createDefaultModel();
		Map<String,Object> localVariables = Maps.newHashMap();
		localVariables.put("umlRaw", uml);
		
		UnitBuilder builder = new SIoTRxBuilder();
		try {
			Set<TranslationUnit> list = builder.parseMapping(Utils.readFile(mappingConnectors));
			list.addAll(builder.parseMapping(Utils.readFile(mappingElements)));
			list.addAll(builder.parseMapping(Utils.readFile(mappingModel)));
//			list.stream().map(unit -> processUnit(unit, localVariables))
//			                     .forEach(result ->  System.out.println(result));
			list.stream().map(unit -> processUnit(unit, localVariables))
			                     .map(result ->  Utils.readModel(result, "turtle"))
			                     .forEach(modelAux -> model.add(modelAux));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return model;
	}
	

	private static String processUnit(TranslationUnit unit, Map<String,Object> localVariables) {
		String result = "";
		try {
			Future<String> f = service.submit(unit.getTask(localVariables));
			result = f.get();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
