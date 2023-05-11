package doctor.model;

import java.util.List;
import java.util.Map;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.ext.com.google.common.collect.Maps;
import org.javatuples.Triplet;
import org.simmetrics.metrics.StringMetrics;

import doctor.Utils;

public class AnalisisClassComparison {

	private AnalisisClass proposed;
	private AnalisisClass solution;
	private Float nameSimilarity;
	private Triplet attributesAlignment;
	private Triplet methodAlignment;
	
	public AnalisisClassComparison(AnalisisClass proposed, AnalisisClass solution) {
		this.proposed = proposed;
		this.solution = solution;
		this.nameSimilarity =  StringMetrics.jaroWinkler().compare(proposed.getName(), solution.getName());
	}
	
	public static List<AnalisisClassComparison> getAlignments(List<AnalisisClass> proposed, List<AnalisisClass> solution, Integer threshold){
		
		List<AnalisisClassComparison> alignments = Lists.newArrayList();

		for(int index=0; index < proposed.size(); index++) {
			AnalisisClass proposedClass = proposed.get(0);
			for(int marker=0; marker < proposed.size(); marker++) {
				AnalisisClass solutionClass = solution.get(0);
				AnalisisClassComparison cmp = new AnalisisClassComparison(proposedClass, solutionClass);
				alignments.add(cmp);
			}
		}
		
		return alignments;
	}
	
	
	
}
