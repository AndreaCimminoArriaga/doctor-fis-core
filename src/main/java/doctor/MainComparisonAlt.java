package doctor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.jena.rdf.model.Model;

import doctor.model.AnalisisClass;
import doctor.model.Helio;

public class MainComparisonAlt {
	
	public static void main(String[] args) throws IOException {
		List<AnalisisClass> group = AnalisisClass.loadFrom("http://localhost:7200/repositories/doctorfis", "http://upm.es/project/iwsit21-02");
		List<AnalisisClass> solution = AnalisisClass.loadFrom("http://localhost:7200/repositories/doctorfis", "http://upm.es/project/solution");

		group.forEach(elem -> System.out.println(elem));
		System.out.println("*****");
		solution.forEach(elem -> System.out.println(elem));
	}
	
	

	
}
