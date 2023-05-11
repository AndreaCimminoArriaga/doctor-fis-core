package doctor.model.restrictions;

import org.apache.jena.graph.Graph;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.Shapes;

import doctor.Utils;

public abstract class  ShaclRestriction extends AbstractRestriction {

	private Shapes shapes;
	public ShaclRestriction(String id) {
		super(id);
	}
	
	public void setShape(String shape) {
		Graph shapesGraph = Utils.readModel(shape, "turtle").getGraph();
		shapes = Shapes.parse(shapesGraph);

//	    ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);
//	    ShLib.printReport(report);
//	    System.out.println();
//	    RDFDataMgr.write(System.out, report.getModel(), Lang.TTL);
	}
	

	
	
}
