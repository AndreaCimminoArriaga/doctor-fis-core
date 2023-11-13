package test.dcu;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import doctor.Utils;
import doctor.model.Helio;
import doctor.model.report.Level;
import doctor.model.report.ReportEntry;
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
import doctor.model.restrictions.dcu.DCU02;
import helio.blueprints.components.Components;

public class TestDC {
	
	@Before
	public void setup() {
	     Helio.loadExternalComponents(); 
	}
	
	@Test
	public void testDC01() {
		String uml = Utils.readFile("./src/test/resources/19242.xml");
		Model model = Helio.toRDF(uml);
		model.write(System.out, "Turtle");
		List<ReportEntry> entries = (new DC01()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DC-01"));
	}
	
	@Test
	public void testDC02() {
		String uml = Utils.readFile("./src/test/resources/55228.xml");
		Model model = Helio.toRDF(uml);
		
		List<ReportEntry> entries = (new DC02()).evaluate(model);
		Assert.assertTrue(entries.size()==4 && entries.get(0).getId().equals("DC-02"));
	}
	
	@Test
	public void testDC03() {
		String uml = Utils.readFile("./src/test/resources/55228.xml");
		Model model = Helio.toRDF(uml);
		
		List<ReportEntry> entries = (new DC03()).evaluate(model);
		Assert.assertTrue(entries.size()==2 && entries.get(0).getId().equals("DC-03"));
	}
	
	@Test
	public void testDC03_2() {
		String uml = Utils.readFile("./src/test/resources/65297.xml");
		Model model = Helio.toRDF(uml);
		
		List<ReportEntry> entries = (new DC03()).evaluate(model);
		Assert.assertTrue(entries.size()==3 && entries.get(0).getId().equals("DC-03"));
	}
	
	
	@Test
	public void testDC04() {
		String uml = Utils.readFile("./src/test/resources/64703.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DC04()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DC-04"));
	}
	
	@Test
	public void testDC05() {
		String uml = Utils.readFile("./src/test/resources/29502.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DC05()).evaluate(model);
		Assert.assertTrue(entries.size()==2 && entries.get(0).getId().equals("DC-05"));
	}
	
	@Test
	public void testDC06() {
		String uml = Utils.readFile("./src/test/resources/52853.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DC06()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DC-06"));
	}
	
//	@Test
//	public void testDC09() {
//		String uml = Utils.readFile("./src/test/resources/37732.xml");
//		Model model = Helio.toRDF(uml);
//		//List<ReportEntry> entries = (new DC09()).evaluate(model);
//		//Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DC-10"));
//	}
	
	@Test
	public void testDC10() {
		String uml = Utils.readFile("./src/test/resources/99289.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DC10()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DC-10"));
	}
	
	@Test
	public void testDC11() {
		String uml = Utils.readFile("./src/test/resources/41781.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DC11()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DC-11"));
	}
	
	
	@Test
	public void testDC12() {
		String uml = Utils.readFile("./src/test/resources/48520.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DC12()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DC-12"));
	}
	@Test
	public void testDC13() {
		String uml = Utils.readFile("./src/test/resources/14388.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DC13()).evaluate(model);
		Assert.assertTrue(entries.size()==1 && entries.get(0).getId().equals("DC-13"));
	}
	
	@Test
	public void testDC13a() {
		String uml = Utils.readFile("./src/test/resources/14388.xml");
		Model model = Helio.toRDF(uml);
		List<ReportEntry> entries = (new DC13a()).evaluate(model);
		Assert.assertTrue(entries.size()==2 && entries.get(0).getId().equals("DC-13a"));
	}
}
