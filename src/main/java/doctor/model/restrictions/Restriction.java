package doctor.model.restrictions;

import java.util.List;

import org.apache.jena.rdf.model.Model;

import doctor.model.report.ReportEntry;

/**
 * 
 * @author andreacimmino
 *
 */
public interface Restriction {

	
	public List<ReportEntry> evaluate(Model model);
}
