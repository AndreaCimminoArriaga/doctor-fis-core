package doctor.model.restrictions.dcu;

import java.util.List;
import org.apache.commons.compress.utils.Lists;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;

import org.apache.jena.rdf.model.Model;

import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
import doctor.model.report.Type;
import doctor.model.restrictions.SparqlRestriction;

public class DCU14 extends SparqlRestriction {

	public DCU14() {
		super("DCU-14");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n" + "ASK { \n" + "    ?actor1 a dcf:Actor .\n"
				+ "    ?actor1 dcf:name ?name .\n" + "    FILTER(strlen(?name) > 0)\n" + "  }");
	}

	public List<ReportEntry> evaluate(Model model) {
		List<ReportEntry> entries = Lists.newArrayList();

		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
		boolean exists = qe.execAsk();
		if (!exists) {
			ReportEntry entry = ReportEntry.create(getId(), Type.WARNING);
			entry.getMessages().put(Level.MIN, "Hay un error en los casos de uso del sistema.");
			entry.getMessages().put(Level.HINT, "Hay un error con los actores del sistema.");
			entry.getMessages().put(Level.DETAILED, "No hay actores en el sistema (o su nombre es vacio).");
			entry.getMessages().put(Level.SOLUTION, "El diagrama debe contener por lo menos un actor con un nombre válido.");
			entries.add(entry);
		}

		qe.close();

		return entries;
	}

}
