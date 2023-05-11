
<@action type="JsonCast" conf= { "format" : "xml" } data=umlRaw; json>
@prefix dcf: <http://upm.es/doctor-fis/1.1/voc#> .
@prefix urn: <http://upm.es/doctor-fis/resource/> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .


<#assign elements = json?eval['xmi:XMI']['xmi:Extension'].elements.element>
<#if elements?is_sequence >
 <#list elements as json>
 <@printElement element=json />
 </#list>
<#else>
  <@printElement element=elements />
</#if>

</@>


<#macro printElement element>
<#-- skipping keys always empty:xrefs, tags  -->
<#-- skipping keys not useful: 
    style, times, flags, project, packageproperties, paths
    code (indicates if the code is Java, none or others)
    ['EAModel.scenario'] (I think defines extended use cases)
    links (encodes the relationships, but these data is generated with a different mapping already)
    properties (encodes information of the element, not used for the moment, maybe in future?)
    constraints,
    model, 
    extendedProperties
-->
<#-- Process only things with type -->
<#if element['xmi:type']??> 
 <#assign elementId = element['xmi:idref']>
 urn:[=elementId] a dcf:[=element['xmi:type']?replace("uml:","")] .
 urn:[=elementId] dcf:visibility dcf:[=element.scope?capitalize] .
 <#if element.name??> urn:[=elementId] dcf:name "[=element.name?replace('\n','')]" . </#if>  
 <#if element.attributes??> <@printAttributes attributes=element.attributes.attribute parent=elementId/> </#if>
 <#if element.operations??> <@printOperations operations=element.operations.operation parent=elementId/> </#if>

</#if>
</#macro>

<#macro printAttributes attributes parent>
 <#-- Recursive call: the attribute is an array-->
 <#if attributes?is_sequence> 
  <#list attributes as attribute>
      <@printAttributes attributes=attribute parent=parent />
  </#list>
 <#else>
  <#-- Case base: the attribute is a json-->
  <@printAttribute attribute=attributes parent=parent />
 </#if>
</#macro>

<#macro printAttribute attribute parent>
 <#-- skipping keys always:  xrefs, tags, documentation, containment, initial, options -->
 <#-- skipping keys not useful: styleex, style, coords, model -->
 <#-- try to add: stereotype -->
 <#assign id = attribute['xmi:idref']>
 <#assign visibility = attribute.scope>
 <#assign name = attribute.name>
 <#assign minCardinality = attribute.bounds.lower>
 <#assign maxCardinality = attribute.bounds.upper>
 <#-- Properties of attribute (properties), NOTE: there are more than those extracted here -->
 <#assign isCollection = attribute.properties.collection?string('true','false')>
 <#assign isStatic = attribute.properties.static?replace("0","false")?replace("1", "true")>
 urn:[=parent] dcf:hasAttribute urn:[=id] .
 urn:[=id] a dcf:ClassAttribute .
 urn:[=id] dcf:name "[=name]" .
 urn:[=id] dcf:visibility dcf:[=visibility?capitalize] .
 urn:[=id] dcf:minCardinality [=minCardinality] .
 urn:[=id] dcf:maxCardinality [=maxCardinality] .
 urn:[=id] dcf:isCollection "[=isCollection]"^^xsd:boolean .
 urn:[=id] dcf:isStatic "[=isStatic]"^^xsd:boolean .
</#macro>


<#macro printOperations operations parent>
 <#-- Recursive call: the operations is an array-->
 <#if operations?is_sequence> 
  <#list operations as operation>
      <@printOperations operations=operation parent=parent />
  </#list>
 <#else>
  <#-- Case base: the attribute is a json-->
  <@printOperation operation=operations parent=parent />
 </#if>
</#macro>


<#macro printOperation operation parent>
 <#-- skipping keys always:  xrefs, code, documentation, behaviour, tags, styleex, style, stereotype-->
 <#-- skipping keys not useful: model, properties -->

 <#assign id = operation['xmi:idref']>
 <#assign visibility = operation.scope>
 <#assign name = operation.name>
 <#-- Properties of method (type), NOTE: there are more than those extracted here -->
 <#assign isStatic = operation.type.static?string('true','false')>
 urn:[=parent] dcf:hasMethod urn:[=id] .
 urn:[=id] a dcf:ClassMethod .
 urn:[=id] dcf:name "[=name]" .
 urn:[=id] dcf:visibility dcf:[=visibility?capitalize] .
 urn:[=id] dcf:isStatic "[=isStatic]"^^xsd:boolean .
 
</#macro>
