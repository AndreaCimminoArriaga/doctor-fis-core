
<@action type="JsonCast" conf= { "format" : "xml" } data=umlRaw; json>

@prefix dcf: <http://upm.es/doctor-fis/1.1/voc#> .
@prefix urn: <http://upm.es/doctor-fis/resource/> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
<#list json?replace("\\","")?eval['xmi:XMI']['xmi:Extension'].connectors.connector as json >
 <@printConnector connector=json />
</#list>
</@>

<#macro printConnector connector>
<#assign connectorId = connector['xmi:idref']>

<#-- skipping keys always empty: documentation, xrefs, parameterSubstitutions, tags, constraints -->
<#-- skipping keys not useful: 
    appearance, 
    label (<@printCardinalities connector=connector id=id/>),  maybe Direction can be useful?
    modifiers (<@printModifier connector=connector id=id/>) 
    style -> contains properties of the relationship
    model -> at the root level contains no useful data (it becomes interesting in target/source)
-->

<#-- Relationship type (properties) -->
<#assign relationshipType = connector.properties.ea_type>
<#if connector.properties.subtype??><#assign relationshipType = relationshipType+connector.properties.subtype ></#if>
<#--  **Improve Association type for use cases -->
<#assign target = connector.target.model.type>
<#assign source = connector.source.model.type>

<#if relationshipType=="Association" && (target?contains("UseCase") || target?contains("Actor"))  && (source?contains("UseCase") || source?contains("Actor"))>
  <#assign relationshipType = "UseCaseAssociation" >
<#elseif relationshipType=="UseCase" && (target?contains("UseCase") || target?contains("Actor"))  && (source?contains("UseCase") || source?contains("Actor"))>
  <#-- DONE: this is a usage line which is incorrect in the diagram -->
  <#assign relationshipType = "UseRelationship" >
</#if>

<#--  **Print type -->
urn:[=connectorId] a dcf:[=relationshipType] .
<#if connector.name??> urn:[=connectorId] dcf:label "[=connector.name]" . </#if> 

<#-- Connect relationship to association class (extendedProperties.associationclass)  -->
<#if connector.extendedProperties?? && connector.extendedProperties?is_hash && connector.extendedProperties.associationclass??> 
 urn:[=connectorId] dcf:hasAssociationClass urn:[=connector.extendedProperties.associationclass] . 
</#if>
<@printRelatedClass class=connector.source parent=connectorId type="source"/>
<@printRelatedClass class=connector.target parent=connectorId type="target"/>


</#macro>


<#macro printRelatedClass class parent type>
<#-- skipping keys always empty: xrefs, documentation, tags, constraints, role -->
<#-- skipping keys not useful: 
  style -> contains properties of the relationship
  model -> has data about the source/target, maybe interesting in the future
-->

<#assign id = class['xmi:idref']> 
urn:[=parent] dcf:has[=type?capitalize]Element urn:[=id] .
urn:[=id] dcf:hasRelationship urn:[=parent] .

<#-- Cardinality (type.multiplicity) -->
<#if class.type?? && class.type.multiplicity??>
urn:[=parent] dcf:[=type]Cardinality "[=class.type.multiplicity]" .
</#if>

<#-- Navigation (modifiers.isNavigable) -->
<#if class.modifiers?? && class.modifiers.isNavigable??>
urn:[=parent] dcf:[=type]IsNavigable "[=class.modifiers.isNavigable?string('true','false')]"^^xsd:boolean .
</#if>

</#macro>





<#-- Following methods are not used -->
<#macro printModifier connector id>
<#if connector.modifiers??> 
<#list connector.modifiers as k, v>
<#if v?is_boolean>
urn:[=id] dcf:[=k] "[=v?string('true','false')]"^^xsd:boolean .
<#else>
urn:[=id] dcf:[=k] "[=v]" .
</#if>
</#list>
</#if>
</#macro>

<#macro printCardinalities connector id>
<#if connector.labels??> 
 <#assign label = connector.labels>
 <#if label?is_string && label?has_content> Check odd label (cardinality) in 'macro:printCardinalities': [=label] </#if>
 <#if !label?is_string && label.rb??> <#-- Skip this, its extracted from the key 'target' --> </#if>
 <#if !label?is_string && label.lb??> <#-- Skip this, its extracted from the key 'source' --> </#if>
 <#if !label?is_string && label.mt??> urn:[=id] dcf:label  [=label.mt] mt </#if> <#-- puede ser el nombre o el tipo de herencia -->
 <#if !label?is_string && label.mb??> 
   <#if label.mb?contains("extend")> urn:[=id] a uml:Extend .
   <#elseif label.mb?contains("include")> urn:[=id] a uml:Include .
   <#else> Unknown type (macro:printCardinalities)
  </#if>
 </#if>
</#if>
</#macro>