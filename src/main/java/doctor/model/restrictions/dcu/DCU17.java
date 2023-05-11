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

public class DCU17 extends SparqlRestriction {

	public DCU17() {
		super("DCU-17");
		this.setQuery("PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
				+ "PREFIX  uml:  <http://upm.es/uml/1.1/voc#>\n"
				+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
				+ "\n"
				+ "SELECT DISTINCT ?sname ?tname ?rType ?relationships\n"
				+ "WHERE\n"
				+ "  { \n"
				+ "     ?relationships rdf:type ?rType .\n"
				+ "     ?relationships dcf:hasSourceElement ?sElement .\n"
				+ "     ?relationships dcf:hasTargetElement ?tElement .\n"
				+ "     ?sElement rdf:type ?sType .\n"
				+ "     ?sElement dcf:name ?sname .\n"
				+ "     ?tElement dcf:name ?tname.\n"
				+ "     ?tElement rdf:type ?tType .\n"
				+ "    VALUES ?rType { dcf:UseRelationship dcf:UseCaseAssociation}\n"
				+ "    FILTER( (?sType=dcf:Actor && ?tType=dcf:UseCase) || (?tType=dcf:Actor && ?sType=dcf:UseCase))\n"
				+ "      \n"
				+ "  }\n"
				+ "\n"
				+ "");
	}

	public List<ReportEntry> evaluate(Model model) {
		List<ReportEntry> entries = Lists.newArrayList();

		QueryExecution qe = QueryExecutionFactory.create(this.getQuery(), model);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution sol = rs.nextSolution();
			String sourceName = sol.get("sname").toString();
			String targetName = sol.get("tname").toString();
			ReportEntry entry = ReportEntry.create(getId(), Type.ERROR);

			entry.getMessages().put(Level.MIN, "Hay un error en los casos de uso del sistema.");
			entry.getMessages().put(Level.HINT, "Hay un error en las relaciones descritas en el sistema.");
			entry.getMessages().put(Level.DETAILED, "Hay un error debido al uso de un conector 'use' en lugar del conector asociación entre actores y casos de uso.");
			entry.getMessages().put(Level.SOLUTION, "Los elementos del diagrama '"+sourceName+"' y '"+targetName+"' están unidos con un a relación 'use' en lugar de una asociación.");
			entries.add(entry);
		}
		

		qe.close();

		return entries;
	}

}
