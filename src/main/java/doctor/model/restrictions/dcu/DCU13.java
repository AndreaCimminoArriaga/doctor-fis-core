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

public class DCU13 extends SparqlRestriction {

	public DCU13() {
		super("DCU-13");
		this.setQuery("PREFIX urn: <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX dcf: <http://upm.es/doctor-fis/1.1/voc#>\n" + "PREFIX uml: <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "\n"
				+ "SELECT distinct ?sourceName ?targetName  WHERE { \n" + "\n" + " ?relation a dcf:Generalization .\n"
				+ " ?relation dcf:hasSourceElement ?source .\n" + " ?relation dcf:hasTargetElement ?target .\n"
				+ " ?source a ?type1 .\n" + " ?source dcf:name ?sourceName .\n" + " ?target a ?type2 .\n"
				+ " ?target dcf:name ?targetName .\n" + "    FILTER (\n"
				+ "         (?type1 = dcf:UseCase && ?type2 = dcf:Actor ) ||\n"
				+ "         (?type1 = dcf:Actor && ?type2 = dcf:UseCase )  \n" + "        )\n" + "} ");
	}

	public List<ReportEntry> evaluate(Model model) {
		List<ReportEntry> entries = Lists.newArrayList();

		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution sol = rs.nextSolution();
			String sourceName = sol.get("sourceName").toString();
			String targetName = sol.get("targetName").toString();
			ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);

			entry.getMessages().put(Level.MIN, "Hay un error en el diagrama de casos de uso.");
			entry.getMessages().put(Level.HINT, "Hay un conflicto entre los actores y los casos de uso.");
			entry.getMessages().put(Level.DETAILED,
					"Una herencia en el diagrama de casos de uso es conceptualmente incorrecta.");
			entry.getMessages().put(Level.SOLUTION, "La herencia entre las entidades '" + sourceName + "' y '"
					+ targetName
					+ "' no es correcta ya que la herencia solo puede existir entre casos de uso o actores, pero no entre un caso de uso y un actor (o vice versa).");
			entries.add(entry);
		}

		qe.close();

		return entries;
	}
}
