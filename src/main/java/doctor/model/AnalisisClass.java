package doctor.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.ext.com.google.common.collect.Maps;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.exec.RowSet;
import org.apache.jena.sparql.exec.http.QueryExecHTTPBuilder;

import com.google.common.collect.Sets;

import doctor.Utils;

public class AnalisisClass {

	private String name;
	private Set<String> attributes = Sets.newHashSet();
	private Set<String> methods = Sets.newHashSet();
	private static String query = "PREFIX  urn:  <http://upm.es/doctor-fis/resource/>\n"
			+ "PREFIX  uml:  <http://upm.es/uml/1.1/voc#>\n"
			+ "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX  dcf:  <http://upm.es/doctor-fis/1.1/voc#>\n"
			+ "\n"
			+ "SELECT DISTINCT ?className ?attrName ?methodName\n"
			+ "WHERE\n"
			+ "  { \n"
			+ "    GRAPH <##GRAPH##> {\n"
			+ "     ?class rdf:type dcf:Class .\n"
			+ "     ?class dcf:name ?className .\n"
			+ "        OPTIONAL{  ?class dcf:hasAttribute ?attr .\n"
			+ "        ?attr dcf:name ?attrName . }\n"
			+ "     OPTIONAL{ ?class dcf:hasMethod ?method .\n"
			+ "     ?method dcf:name ?methodName .}\n"
			+ "    \n"
			+ "     }\n"
			+ "  }";

	public AnalisisClass(String name) {
		super();
		this.name = name;
	}

	public AnalisisClass(String name, Set<String> attributes, Set<String> methods) {
		super();
		this.name = name;
		if(attributes!=null)
			this.attributes = attributes;
		if(methods!=null)
			this.methods = methods;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<String> attributes) {
		this.attributes = attributes;
	}

	public Set<String> getMethods() {
		return methods;
	}

	public void setMethods(Set<String> methods) {
		this.methods = methods;
	}

	public static List<AnalisisClass> loadFrom(String url, String graph) {
		RowSet rows = QueryExecHTTPBuilder.service(url).query(query.replace("##GRAPH##", graph)).select();
		Set<String> classes = Sets.newHashSet();
		Map<String, Set<String>> attrs = Maps.newHashMap();
		Map<String, Set<String>> methods = Maps.newHashMap();
		while (rows.hasNext()) {
			Binding bd = rows.next();
			String className = Utils.clean(getValue(bd.get("className")));
			String attrName = Utils.clean(getValue(bd.get("attrName")));
			String methodName = Utils.clean(getValue(bd.get("methodName")));
			classes.add(className);
			if (attrName != null) {
				Set<String> oldAttrs = Sets.newHashSet();
				if (attrs.containsKey(className))
					oldAttrs.addAll(attrs.get(className));
				oldAttrs.add(attrName);
				attrs.put(className, oldAttrs);
			}
			if (methodName != null) {
				Set<String> oldMethods = Sets.newHashSet();
				if (methods.containsKey(className))
					oldMethods.addAll(methods.get(className));
				oldMethods.add(methodName);
				methods.put(className, oldMethods);
			}
		}
		System.out.println(attrs.keySet());
		return classes.parallelStream().map(key -> new AnalisisClass(key, attrs.get(key), methods.get(key)))
				.collect(Collectors.toList());
	}

	private static String getValue(Node node) {
		if (node != null) 
			return node.getLiteral().getValue().toString();
		
		return null;
	}

	

	@Override
	public String toString() {
		return "AnalisisClass [name=" + name + ", attributes=" + attributes + ", methods=" + methods + "]";
	}

}
