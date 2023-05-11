package test.dcu;

import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.apache.jena.rdf.model.Model;
import org.junit.Assert;
import org.junit.Test;

import doctor.Utils;
import doctor.model.Helio;
import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
import doctor.model.restrictions.Restriction;
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

public class TestDCU {

	public static List<Restriction> restrictions  = Lists.newArrayList();
	static {
		
		restrictions.add(new DCU01());
		restrictions.add(new DCU02());
		//restrictions.add(new DCU03());
		restrictions.add(new DCU04());
		restrictions.add(new DCU05());
		//restrictions.add(new DCU06());
		restrictions.add(new DCU07());
		//restrictions.add(new DCU08());
		//restrictions.add(new DCU09());
		restrictions.add(new DCU10());
		restrictions.add(new DCU11());
		restrictions.add(new DCU12());
		restrictions.add(new DCU13());

	}
	
	@Test
	public void testDCU01() {
		String uml = Utils.readFile("./src/test/resources/2293.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU01()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-01"));
	}
	
	@Test
	public void testDCU02() {
		String uml = Utils.readFile("./src/test/resources/9591_V2.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU02()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-02"));
	}
	
	@Test
	public void testDCU03() {
		String uml = Utils.readFile("./src/test/resources/6071.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU03()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-03"));
	}
	
	@Test
	public void testDCU04() {
		String uml = Utils.readFile("./src/test/resources/9055.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU04()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-04"));
	}
	
	@Test
	public void testDCU05() {
		String uml = Utils.readFile("./src/test/resources/3601.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU05()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-05"));
	}
	
	@Test
	public void testDCU06() {
		String uml = Utils.readFile("./src/test/resources/5058.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU06()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-06"));
	}
	
	@Test
	public void testDCU07() {
		String uml = Utils.readFile("./src/test/resources/3653.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU07()).evaluate(model);
		Assert.assertTrue(entries.size()>0);
		entries.parallelStream().forEach(elem -> Assert.assertTrue(elem.getId().equals("DCU-07")));
	}
	
	@Test
	public void testDCU08() {
		String uml = Utils.readFile("./src/test/resources/3285.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU08()).evaluate(model);
		Assert.assertTrue(entries.size()!=0 && entries.get(0).getId().equals("DCU-08"));
	}
	
	@Test
	public void testDCU08_1() {
		String uml = Utils.readFile("./src/test/resources/3285_V2.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU08()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-08"));
	}
	
	
	
//	@Test
//	public void testDCU09() {
//		String uml = Utils.readFile("./src/test/resources/9907.xml");
//		Model model = Helio.toRDF(uml);
//		List<ReportEntry> entries = (new DCU09()).evaluate(model);
//		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-09"));
//	}
//	
	@Test
	public void testDCU10() {
		String uml = Utils.readFile("./src/test/resources/2505.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU10()).evaluate(model);
		Assert.assertTrue(entries.size()==2 && entries.get(0).getId().equals("DCU-10"));
	}
	
	@Test
	public void testDCU11() {
		String uml = Utils.readFile("./src/test/resources/2076.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU11()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-11"));
	}
	
	@Test
	public void testDCU12() {
		String uml = Utils.readFile("./src/test/resources/7918.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU12()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-12"));
	}
	
//	@Test
//	public void testDCU13() {
//		String uml = Utils.readFile("./src/test/resources/2125.xml");
//		Model model = Helio.toRDF(uml);
//		List<ReportEntry> entries = (new DCU13()).evaluate(model);
//		model.write(System.out,"turtle");
//		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-13"));
//	}
	
	
	@Test
	public void testDCU14() {
		String uml = Utils.readFile("./src/test/resources/9591.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU14()).evaluate(model);
		
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DCU-14"));
	}
	
	@Test
	public void testDCU17() {
		String uml = Utils.readFile("./src/test/resources/IWSIT21_01.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DCU17()).evaluate(model);
		
		Assert.assertTrue(entries.size()>0);
		entries.forEach(entry -> Assert.assertTrue(entry.getId().equals("DCU-17")));
	}
}
