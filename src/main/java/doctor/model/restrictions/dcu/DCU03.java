package doctor.model.restrictions.dcu;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;
import org.apache.jena.ext.com.google.common.collect.Maps;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
import doctor.model.report.Type;
import doctor.model.restrictions.SparqlRestriction;

public class DCU03 extends SparqlRestriction {

//	  PREFIX urn: <http://upm.es/doctor-fis/resource/> PREFIX dcf:
//	  <http://upm.es/doctor-fis/1.1/voc#> PREFIX uml: <http://upm.es/uml/1.1/voc#>
//	  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
//	  
//	  SELECT distinct ?sourceName ?targetName ?otherRelationship WHERE {
//	  
//	  ?relationship a dcf:UseCaseIncludes . ?relationship dcf:hasSourceElement
//	  ?source . ?source dcf:name ?sourceName . ?source dcf:hasRelationship
//	  ?otherRelationship . MINUS {?otherRelationship a dcf:UseCaseAssociation . }
//	  ?relationship dcf:hasTargetElement ?target . ?target dcf:name ?targetName .
//	  
//	  
//	  FILTER (?otherRelationship != ?relationship)
//	  
//	  }
//	 
	
	// TODO:
	/**
	 * Todas las relaciones includes deben tener un UC principal que tenga una
	 * asociacion 
	 * Todas las relaciones extend deben tener un UC principal que tenga una
	 * asociacion Todos los casos de uso deben tener una asociacion con un actor
	 */
	public DCU03() {
		super("DCU-03");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  uml:  <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n" + "\n"
				+ "SELECT DISTINCT  ?ucName ?relationships ?type ?entityType ?entityName\n" + "WHERE\n"
				+ "  { ?uc       rdf:type             dcf:UseCase ;\n"
				+ "              dcf:name             ?ucName ;\n"
				+ "              dcf:hasRelationship  ?relationships .\n" + "    ?relationships\n"
				+ "              rdf:type             ?type ;\n" + "              dcf:hasTargetElement     ?entity ;\n"
				+ "              dcf:hasSourceElement ?uc .\n" + "    ?entity   rdf:type             ?entityType ;\n"
				+ "              dcf:name             ?entityName\n"
				+ "    VALUES ?entityType { dcf:Actor uml:UseCase }\n"
				+ "    FILTER strstarts(str(?type), \"http://upm.es/doctor-fis/1.1/voc#\")\n"
				+ "    FILTER ( ?uc != ?entity )\n" + "  }");
	}

	public List<ReportEntry> evaluate(Model model) {
		List<ReportEntry> entries = Lists.newArrayList();
		// Sobre esta query se puede saltar todo aquel UC que tenga relación con un
		// actor y se debe guardar
		// Si un uc tiene relacion (include/extend) con otro se guarda la relacion
		// A posteriori se ve si los UC con (include/extend) tienen un actor, y no si
		// noes el caso, estan realcionados con otro UC que tenga

		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
		ResultSet rs = qe.execSelect();
		Map<String, String> ucExtends = Maps.newHashMap(); // k extends a v ( k tiene que tener actores)
		Map<String, String> ucIncludes = Maps.newHashMap(); // k includes a v ( k tiene que tener actores)

		while (rs.hasNext()) {
			QuerySolution sol = rs.nextSolution();
			// ?ucName ?type ?entityType ?entityName
			String ucName = sol.get("ucName").toString();
			String relationType = sol.get("type").toString();
			String entityType = sol.get("entityType").toString();
			String entityName = sol.get("entityName").toString();
			if (entityType.endsWith("#UseCase") && !relationType.endsWith("#Generalization")) {
				if (relationType.endsWith("#UseCaseExtends")) {
					ucExtends.put(ucName, entityName);
				} else if (relationType.endsWith("#UseCaseIncludes")) {
					ucIncludes.put(ucName, entityName);
				}
			}
		}
		qe.close();

		// Extends: the extended UC may not have actors, but original UC must
		//ucExtends.entrySet().parallelStream().forEach(elem -> System.out.println(elem.getKey()+" "+hasActor(elem.getKey(), model)+", "+elem.getValue()+" "+hasActor(elem.getValue(), model)));
		List<Entry<String, String>> ucBaseWithoutActors = ucExtends.entrySet().parallelStream()
				.filter(entry -> !hasActor(entry.getValue(), model)).collect(Collectors.toList());
		for (Entry<String, String> ucs : ucBaseWithoutActors) {
			ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
			entry.getMessages().put(Level.MIN, "Hay un error en los casos de uso del sistema.");
			entry.getMessages().put(Level.HINT, "Hay un error con las relaciones de, por lo menos, un caso de uso.");
			entry.getMessages().put(Level.DETAILED, "Hay un error en un extend entre casos de uso.");
			entry.getMessages().put(Level.SOLUTION,
					"El caso de uso '" + ucs.getKey() + "' extiende a '" + ucs.getValue() + "', sin embargo '"
							+ ucs.getValue() + "' no está asociado a un actor y por lo tanto no puede ejecutarse.");
			entries.add(entry);
		}
		
		//System.out.println(".....");
		//ucIncludes.entrySet().parallelStream().forEach(elem -> System.out.println(elem.getKey()+" "+elem.getValue()+" "+hasActor(elem.getKey(), model)));
		// Include: the included UC may not have actors, but the original UC must
		ucBaseWithoutActors.clear();
		ucBaseWithoutActors = ucIncludes.entrySet().parallelStream()
				.filter(entry -> !hasActor(entry.getKey(), model)).collect(Collectors.toList());
		for (Entry<String, String> ucs : ucBaseWithoutActors) {
			ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
			entry.getMessages().put(Level.MIN, "Hay un error en los casos de uso del sistema.");
			entry.getMessages().put(Level.HINT, "Hay un error con las relaciones de, por lo menos, un caso de uso.");
			entry.getMessages().put(Level.DETAILED, "Hay un error en un include entre casos de uso.");
			entry.getMessages().put(Level.SOLUTION,
					"El caso de uso '" + ucs.getKey() + "' incluye a '" + ucs.getValue() + "', sin embargo '"
							+ ucs.getKey() + "' no está asociado a un actor y por lo tanto no puede ejecutarse.");
			entries.add(entry);
		}

		return entries;
	}

	private boolean hasActor(String uc, Model model) {
		QueryExecution qe = QueryExecutionFactory.create("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  uml:  <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "\n"
				+ "ASK { \n"
				+ "    ?uc       rdf:type             dcf:UseCase ;\n"
				+ "              dcf:name             \""+uc+"\" .\n"
				+ "    ?relationships ?relation2    ?uc .\n"
				+ "   ?relationships rdf:type    dcf:UseCaseAssociation .\n"
				+ "    ?relationships ?relation1    ?entity .\n"
				+ "               \n"
				+ "    ?entity   rdf:type             dcf:Actor ;\n"
				+ "              dcf:name             ?entityName .\n"
				+ "    MINUS {  ?relationships rdf:type  uml:Package} "
				+ "     \n"
				+ "  }", model);
		
		boolean hasActor  = qe.execAsk();
		if(!hasActor) {
			// Check if the extended uc has a generalization
			String query = "PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
					+ "PREFIX  uml:  <http://upm.es/uml/1.1/voc#>\n"
					+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
					+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
					+ "\n"
					+ "ASK { \n"
					+ "    ?uc       rdf:type             dcf:UseCase ;\n"
					+ "              dcf:name             \""+uc+"\" .\n"
					+ "    ?relationships ?relation2    ?uc .\n"
					+ "    ?relationships rdf:type    dcf:Generalization .\n"
					+ "    ?relationships ?relation1    ?entity .\n"
					+ "               \n"
					+ "    ?entity   rdf:type             dcf:UseCase ;\n"
					+ "              dcf:name             ?entityName .\n"
					+ "  }";
			qe = QueryExecutionFactory.create(query, model);
			hasActor = qe.execAsk();
		}
		return hasActor;
	}
}
