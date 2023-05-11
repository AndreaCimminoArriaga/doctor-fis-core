package doctor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.jena.rdf.model.Model;

import doctor.model.Helio;

public class MainLoader {
	
	public static void main(String[] args) throws IOException {
		String fileAddr = "/Users/andreacimmino/Desktop/iwsit21-02.xml"; 
		String uml = Utils.readFile(fileAddr);
		Model model = Helio.toRDF(uml);
		Writer w = new StringWriter();
		 model.write(w, "turtle");
		String data = w.toString();
		w.close();
		FileWriter f = new FileWriter(new File("/Users/andreacimmino/Desktop/iwsit21-02.ttl"));
		f.write(data);
		f.flush();
		f.close();
		System.exit(-1);
	}
	
	

	
}
