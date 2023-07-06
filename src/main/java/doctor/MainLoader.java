package doctor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.jena.rdf.model.Model;

import doctor.model.Helio;
import doctor.model.report.Level;
import doctor.model.report.ReportEntry;

public class MainLoader {
	
	public static void main(String[] args) throws IOException {
		String fileAddr = "/Users/andreacimmino/ownCloud/FIS/wetransfer_project-xml_2023-02-27_0843/UPMFit_XMI2.1.xml"; 
		String uml = Utils.readFile(fileAddr);
		DoctorFIS doc = DoctorFisImpl.create();
		
		List<ReportEntry> entries = doc.computeDCURestrictions(uml);
		
		entries.stream().forEach(elem -> System.out.println(elem.getId()+" "+elem.getMessages().get(Level.HINT)));
		
		
		
	}
	
	

	
}
