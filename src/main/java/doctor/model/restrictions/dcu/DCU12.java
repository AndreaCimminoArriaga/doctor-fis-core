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

public class DCU12 extends SparqlRestriction {

	public DCU12() {
		super("DCU-12");
		this.setQuery("PREFIX urn: <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX dcf: <http://upm.es/doctor-fis/1.1/voc#>\n" + "\n"
				+ "SELECT distinct ?name1 ?name2 WHERE { \n" + " ?uc1 a dcf:UseCase .\n" + " ?uc1 dcf:name ?name1 .\n"
				+ " ?uc2 a dcf:UseCase .\n" + " ?uc2 dcf:name ?name2 .\n"
				+ "    FILTER (lcase(str(?name1)) = lcase(str(?name2)) && ?uc1 != ?uc2) .\n" + "}");
	}

	public List<ReportEntry> evaluate(Model model) {
		List<ReportEntry> entries = Lists.newArrayList();

		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution sol = rs.nextSolution();
			String actor1 = sol.get("name1").toString();
			ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);

			entry.getMessages().put(Level.MIN, "Hay un error con los casos de use del sistema.");
			entry.getMessages().put(Level.HINT, "Hay dos casos de use que tienen un conflicto.");
			entry.getMessages().put(Level.DETAILED, "Dos casos de uso diferentes poseen el mismo nombre.");
			entry.getMessages().put(Level.SOLUTION,
					"Dos casos de uso diferentes poseen el mismo nombre '" + actor1 + "'.");
			entries.add(entry);
		}

		qe.close();

		return entries;
	}
}
