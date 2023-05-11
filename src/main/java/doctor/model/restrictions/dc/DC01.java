package doctor.model.restrictions.dc;

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
import doctor.model.restrictions.Restrictions;
import doctor.model.restrictions.SparqlRestriction;

public class DC01 extends SparqlRestriction {

	public DC01() {
		super("DC-01");
		this.setQuery("PREFIX urn: <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX dcf: <http://upm.es/doctor-fis/1.1/voc#>\n" + "PREFIX uml: <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "\n"
				+ "SELECT distinct ?className WHERE { \n" + "\n" + " ?class a dcf:Class .\n"
				+ "  ?class dcf:name ?className .\n" + "    \n" + "} ");
	}

	public List<ReportEntry> evaluate(Model model) {
		List<ReportEntry> entries = Lists.newArrayList();

		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution sol = rs.nextSolution();
			String className = sol.get("className").toString();
			if (Restrictions.systemLabel.contains(className.toLowerCase())) {
				ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);
				entry.getMessages().put(Level.MIN, "Hay un error en el diagrama de clases.");
				entry.getMessages().put(Level.HINT, "Hay una clase cuyo nombre es incorrecto.");
				entry.getMessages().put(Level.DETAILED,
						"La clase '" + className + "' no puede ser una representación del sistema.");
				entry.getMessages().put(Level.SOLUTION,
						"La clase '" + className + "' no puede ser una representación del sistema.");
				entries.add(entry);
			}
		}

		qe.close();

		return entries;
	}
}
