package doctor.model.restrictions.dcu;

import java.util.List;
import org.apache.commons.compress.utils.Lists;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
import doctor.model.report.Type;
import doctor.model.restrictions.SparqlRestriction;

public class DCU06 extends SparqlRestriction {

	public DCU06() {
		super("DCU-06");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "SELECT DISTINCT  ?actorN1 ?actorN2 ?ucName  {\n" + "    ?inherit a dcf:Generalization .\n"
				+ "    ?inherit dcf:hasSourceElement ?actor1 .\n" + "    ?inherit dcf:hasTargetElement ?actor2 .\n"
				+ "    ?actor1 a dcf:Actor .\n" + "    ?actor1 dcf:name ?actorN1 .\n"
				+ "    ?actor1 dcf:hasRelationship  ?relationship1 .\n" + "    ?relationship1 ?targets1 ?uc .\n" + "\n"
				+ "    ?actor2 a dcf:Actor .\n" + "    ?actor2 dcf:name ?actorN2 .\n"
				+ "    ?actor2 dcf:hasRelationship  ?relationship2 .\n" + "    ?relationship2 ?targets2 ?uc .\n"
				+ "    ?uc   rdf:type dcf:UseCase .\n" + "    ?uc dcf:name ?ucName .\n" + "}");
	}

	public List<ReportEntry> evaluate(Model model) {
		List<ReportEntry> entries = Lists.newArrayList();

		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution sol = rs.nextSolution();
			// actorN1 ?actorN2 ?ucName
			String actorName1 = sol.get("actorN1").toString();
			String actorName2 = sol.get("actorN2").toString();
			String ucName = sol.get("ucName").toString();

			ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
			entry.getMessages().put(Level.MIN, "Hay un error en los actores del sistema.");
			entry.getMessages().put(Level.HINT, "Hay un error en las relaciones que poseen los actores");
			entry.getMessages().put(Level.DETAILED, "Hay un error con la herencia entre actores.");
			entry.getMessages().put(Level.SOLUTION, "Los actores '" + actorName1 + "' '"+actorName2+"' poseen una relación de herencia, sin embargo están conectados ambos al caso de uso '"+ucName+"'.");

			entries.add(entry);

		}

		qe.close();

		return entries;
	}

}
