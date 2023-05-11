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

public class DCU11 extends SparqlRestriction {

	public DCU11() {
		super("DCU-11");
		this.setQuery("PREFIX urn: <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX dcf: <http://upm.es/doctor-fis/1.1/voc#>\n" + "\n"
				+ "SELECT distinct ?name1 ?name2 WHERE { \n" + " ?actor1 a dcf:Actor .\n"
				+ " ?actor1 dcf:name ?name1 .\n" + " ?actor2 a dcf:Actor .\n" + " ?actor2 dcf:name ?name2 .\n"
				+ "    FILTER (?actor1 != ?actor2 && lcase(str(?name1)) = lcase(str(?name2)) ) .\n" + "}");
	}

	public List<ReportEntry> evaluate(Model model) {
		List<ReportEntry> entries = Lists.newArrayList();

		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution sol = rs.nextSolution();
			ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
			String actor1 = sol.get("name1").toString();
			entry.getMessages().put(Level.MIN, "Hay un error con los actores del sistema.");
			entry.getMessages().put(Level.HINT, "Hay dos actores que tienen un conflicto.");
			entry.getMessages().put(Level.DETAILED, "Dos actores diferentes poseen el mismo nombre.");
			entry.getMessages().put(Level.SOLUTION, "Dos actores diferentes poseen el mismo nombre '" + actor1 + "'.");
			entries.add(entry);
		}

		qe.close();

		return entries;
	}
}
