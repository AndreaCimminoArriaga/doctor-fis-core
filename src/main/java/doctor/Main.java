package doctor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;

import doctor.model.Helio;
import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
import doctor.model.restrictions.Restriction;
import doctor.model.restrictions.SparqlRestriction;
import doctor.model.restrictions.dc.DC01;
import doctor.model.restrictions.dc.DC02;
import doctor.model.restrictions.dc.DC03;
import doctor.model.restrictions.dc.DC04;
import doctor.model.restrictions.dc.DC05;
import doctor.model.restrictions.dc.DC06;
import doctor.model.restrictions.dc.DC10;
import doctor.model.restrictions.dc.DC11;
import doctor.model.restrictions.dc.DC12;
import doctor.model.restrictions.dc.DC13;
import doctor.model.restrictions.dc.DC13a;
import doctor.model.restrictions.dcu.DCU01;
import doctor.model.restrictions.dcu.DCU02;
import doctor.model.restrictions.dcu.DCU03;
import doctor.model.restrictions.dcu.DCU04;
import doctor.model.restrictions.dcu.DCU05;
import doctor.model.restrictions.dcu.DCU06;
import doctor.model.restrictions.dcu.DCU07;
import doctor.model.restrictions.dcu.DCU08;
import doctor.model.restrictions.dcu.DCU10;
import doctor.model.restrictions.dcu.DCU11;
import doctor.model.restrictions.dcu.DCU12;
import doctor.model.restrictions.dcu.DCU13;
import doctor.model.restrictions.dcu.DCU14;
import doctor.model.restrictions.dcu.DCU17;

public class Main {

	public static List<Restriction> restrictionsDC = new ArrayList<>();
	public static List<Restriction> restrictionsDCU = new ArrayList<>();
	static {

		restrictionsDCU.add(new DCU01());
		restrictionsDCU.add(new DCU02());
		restrictionsDCU.add(new DCU03());
		restrictionsDCU.add(new DCU04());
		restrictionsDCU.add(new DCU05());
		restrictionsDCU.add(new DCU06());
		restrictionsDCU.add(new DCU07());
		restrictionsDCU.add(new DCU08());
		// restrictions.add(new DCU09());
		restrictionsDCU.add(new DCU10());
		restrictionsDCU.add(new DCU11());
		restrictionsDCU.add(new DCU12());
		restrictionsDCU.add(new DCU13());
		restrictionsDCU.add(new DCU13());
		restrictionsDCU.add(new DCU14());
		restrictionsDCU.add(new DCU17());
		restrictionsDC.add(new DC01());
		restrictionsDC.add(new DC02());
		restrictionsDC.add(new DC03());
		restrictionsDC.add(new DC04());
		restrictionsDC.add(new DC05());
		restrictionsDC.add(new DC06());
		restrictionsDC.add(new DC10());
		restrictionsDC.add(new DC11());
		restrictionsDC.add(new DC12());
		restrictionsDC.add(new DC13());
		restrictionsDC.add(new DC13a());
	}

	public static void main(String[] args) throws IOException {
			File dir = new File("/Users/andreacimmino/ownCloud/FIS/entregas/");
			File[] files = dir.listFiles();
			for(File file:files) {
				
				if(file.getAbsolutePath().endsWith(".xml")) {
					File reportFile = new File(dir, file.getName().replace(".xml", ".txt"));
					 BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile));
					System.out.println("Processing "+file.getName());    
					
					    
					String fileAddr = file.getAbsolutePath(); // Put here the file name
					String uml = Utils.readFile(fileAddr);
					Model model = Helio.toRDF(uml);
	
					writer.write("Use case diagram feedback\n");
					for (int index = 0; index < restrictionsDCU.size(); index++) {
						Restriction res = restrictionsDCU.get(index);
						res.evaluate(model).forEach(elem -> {
							try {
								writer.write("\t-"+elem.getId()+" - "+elem.getMessages().get(Level.SOLUTION)+"\n");
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
					}
					writer.write("\n");
					writer.write("Class diagram feedback\n");
					for (int index = 0; index < restrictionsDC.size(); index++) {
						Restriction res = restrictionsDC.get(index);
						res.evaluate(model).forEach(elem -> {
							try {
								writer.write("\t*"+elem.getId()+" - "+elem.getMessages().get(Level.SOLUTION)+"\n");
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
					}
					writer.close();
					System.out.println("Written "+reportFile.getName());    
				}
			}

			System.exit(-1);
	}

}
