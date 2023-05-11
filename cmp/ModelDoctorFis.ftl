<@action type="JsonCast" conf= { "format" : "xml" } data=umlRaw; json>
@prefix dcf: <http://upm.es/doctor-fis/1.1/voc#> .
@prefix uml: <http://upm.es/uml/1.1/voc#> .
@prefix urn: <http://upm.es/doctor-fis/resource/> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
<#assign packaged = json?eval['xmi:XMI']['uml:Model']['packagedElement']['packagedElement']>
<#if packaged?is_sequence >
 <#list packaged as json>
 <@printPackagedElement data=json parent="None" vis="1"/>
 </#list>
<#else>
 <@printPackagedElement data=packaged parent="None" vis="1"/>
</#if>

</@>

<#macro printPackagedElement data parent vis>
<#if data['xmi:id']??>
 <#if parent!="None"> 
   urn:[=parent] dcf:hasPackedElement urn:[=data['xmi:id']] . 
   urn:[=data['xmi:id']] dcf:isPackedElementOf urn:[=parent] .
  </#if>
 <@printGeneral values=data vis=vis/>
 <#-- Extends/Include (extend/include) -->
 <#if data.extend??> 
    <#if data.extend?is_sequence>
      <#list data.extend as extend>
        <@printPackagedElement data=extend parent=data['xmi:id'] vis=vis/> 
      </#list>
    <#else>
     <@printPackagedElement data=data.extend parent=data['xmi:id'] vis=vis/> 
    </#if>
 </#if>
 <#if data.include??> 
    <#if data.include?is_sequence>
      <#list data.include as include>
        <@printPackagedElement data=include parent=data['xmi:id'] vis=vis/> 
      </#list>
    <#else>
     <@printPackagedElement data=data.include parent=data['xmi:id'] vis=vis/> 
    </#if>
  </#if>
 <#-- Generalization (generalization) -->
 <#if data.generalization??> <@printPackagedElement data=data.generalization parent=data['xmi:id'] vis=vis/> </#if>
 <#-- Comments (ownedComment) -->
 <#if data.ownedComment??> 
   <#if data.ownedComment?is_sequence>
      <#list data.ownedComment as ownedComment>
        <@printPackagedElement data=ownedComment parent=data['xmi:id'] vis=vis/> 
      </#list>
    <#else>
      <@printPackagedElement data=data.ownedComment parent=data['xmi:id'] vis=vis/> 
    </#if>
  </#if> 

 <#-- EnumLiterals (ownedLiteral) -->
 <#if data.ownedLiteral??> <@printEnumLiteral literals=data.ownedLiteral parent=data['xmi:id'] vis=vis/> </#if>

 <#-- Attributes (ownedAttribute) -->
 <#if data.ownedAttribute??> <@printOwnedElements elements=data.ownedAttribute parent=data['xmi:id'] vis=vis/> </#if> 
 <#-- ownedEnd (ownedEnd) -->  
 <#if data.ownedEnd??> <@printOwnedElements elements=data.ownedEnd parent=data['xmi:id'] vis=vis/> </#if> 
 <#-- Methods (ownedOperation) --> 
 <#if data.ownedOperation??> <@printOwnedElements elements=data.ownedOperation parent=data['xmi:id'] vis=vis/> </#if> 
 
 <#-- MemberEnd (memberEnd) -->
 <#if data.memberEnd??> <@printMemberEnd elements=data.memberEnd parent=data['xmi:id'] vis=vis /> </#if> 



 <#-- Nested Call -->
 <#if data.packagedElement??>
 <#if  data.packagedElement?is_sequence>
 <#list data.packagedElement as nested>
 <@printPackagedElement data=nested parent=data['xmi:id'] vis=vis/>
 </#list>
 <#else>
     <@printPackagedElement data=data.packagedElement parent=data['xmi:id'] vis=vis/>
  </#if>
 </#if>

</#if> <#--DONE: si me salto el payload por no tener id, deberia comprobar si tiene packagedElement para hacer otra llamada recursiva-->

</#macro>

<#-- AUXILIARY FUNCTIONS -->
<#macro printGeneral values vis>
 <#if vis=="1">
  <#assign id = values['xmi:id']>
  <#-- General data -->
  <#if values.name??> urn:[=id] dcf:name "[=values.name?replace('\n','')?trim]" .</#if>
  <#if values['xmi:type']??> urn:[=id] a [=values['xmi:type']] .</#if>
  <#if values.visibility??> urn:[=id] dcf:visibility dcf:[=values.visibility?capitalize] .</#if>
  
  <#-- Extends/Include/Generalization/Comments only -->
  <#if values.extendedCase??> urn:[=id] dcf:extendedCase urn:[=values.extendedCase] .</#if>
  <#if values.addition??> urn:[=id] dcf:includedCase urn:[=values.addition] .</#if>
  <#if values.general??> urn:[=id] dcf:general urn:[=values.general] .</#if>
  <#if values.body??> urn:[=id] dcf:comment "[=values.body?replace("\n","")]" .</#if>
  
  <#-- Attributes (ownedAttribute) -->
  <#if values.isStatic??> urn:[=id] dcf:isStatic "[=values.isStatic?string('true','false')]"^^xsd:boolean .</#if>
  <#if values.isUnique??> urn:[=id] dcf:isUnique "[=values.isUnique?string('true','false')]"^^xsd:boolean .</#if>
  <#if values.isReadOnly??> urn:[=id] dcf:isReadOnly "[=values.isReadOnly?string('true','false')]"^^xsd:boolean .</#if>
  <#if values.isOrdered??> urn:[=id] dcf:isOrdered "[=values.isOrdered?string('true','false')]"^^xsd:boolean .</#if>
  <#if values.isDerivedUnion??> urn:[=id] dcf:isDerivedUnion "[=values.isDerivedUnion?string('true','false')]"^^xsd:boolean .</#if>
  <#if values.isDerived??> urn:[=id] dcf:isDerived "[=values.isDerived?string('true','false')]"^^xsd:boolean .</#if>
  <#if values.aggregation?? && values.aggregation!="none"> urn:[=id] dcf:aggregation urn:[=values.aggregation] .</#if>
  <#if values.association?? && values.association!="none"> urn:[=id] dcf:association urn:[=values.association] .</#if>
  <@printAttributeValues values=values parent=id />
  <#-- Methods (ownedOperation) -->
  <#if values.concurrency??> urn:[=id] dcf:concurrency urn:[=values.concurrency] .</#if>

 </#if>
</#macro>

<#macro printAttributeValues values parent>
  <#if values.upperValue??>
   <#assign nested = values.upperValue>
   <#assign attrId= nested['xmi:id']>
   urn:[=parent] dcf:hasAttributeRestriction urn:[=attrId] . 
   urn:[=attrId] dcf:isAttributeRestrictionOf urn:[=parent] .
   urn:[=attrId] a [=nested['xmi:type']] .
   urn:[=attrId] a dcf:UpperValueRestriction .
   urn:[=attrId] dcf:value "[=nested.value]" .
  </#if>
  <#if values.lowerValue??>
   <#assign nested = values.lowerValue>
   <#assign attrId= nested['xmi:id']>
   urn:[=parent] dcf:hasAttributeRestriction urn:[=attrId] . 
   urn:[=attrId] dcf:isAttributeRestrictionOf urn:[=parent] .
   urn:[=attrId] a [=nested['xmi:type']] .
   urn:[=attrId] a dcf:LowerValueRestriction .
   urn:[=attrId] dcf:value "[=nested.value]" .
  </#if>
  <#if values.type?? && values.type['xmi:idref']??>
    urn:[=parent] dcf:idref urn:[=values.type['xmi:idref']] .
  </#if>
</#macro>

<#-- FUNCTION: EnumLiterals (ownedLiteral) -->
<#macro printEnumLiteral literals parent vis>
<#if literals?is_sequence>
<#list literals as literal>
<@printPackagedElement data=literal parent=parent vis=vis/>
</#list>
<#else>
<@printPackagedElement data=literals parent=parent vis=vis/>
</#if>
</#macro>

<#macro printOwnedElements elements parent vis>
<#if elements?is_sequence>
 <#-- Nested call -->
 <#list elements as elem>
  <@printOwnedElements elements=elem parent=parent vis=vis/>
 </#list>
<#else>
 <#-- Caso base -->
 <@printGeneral values=elements vis=vis/>
</#if>
</#macro>

<#macro printMemberEnd elements parent vis>
<#if vis=="1">
<#if elements?is_sequence>
<#if elements?size == 2> 
  urn:[=parent] dcf:member urn:[=elements[0]['xmi:idref']] .
  urn:[=parent] dcf:member urn:[=elements[1]['xmi:idref']] .
<#else> This sequence should have only 2 elements! (macro: printMemberEnd) </#if>
<#else>
This should be a sequence! (macro: printMemberEnd)
</#if>
</#if>

</#macro>
