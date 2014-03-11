/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ws.commons.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.ws.commons.schema.constants.Constants;
import org.apache.ws.commons.schema.extensions.ExtensionRegistry;
import org.apache.ws.commons.schema.utils.NamespacePrefixList;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Convert from the XML Schema class representation to the standard
 * XML representation.
  */
public class XmlSchemaSerializer {

    /**
     * Extension registry for the serializer
     */

    private ExtensionRegistry extReg;

    /**
     * Get the registry of extensions for this serializer.
     * @return the registry.
     */
    public ExtensionRegistry getExtReg() {
        return extReg;
    }

    /**
     * Set the registry of extensions for this serializer.
     * @param extReg the registry.
     */
    public void setExtReg(ExtensionRegistry extReg) {
        this.extReg = extReg;
    }


    private Hashtable schema_ns;

    String xsdPrefix = "xs";
    String xsdNamespace = "http://www.w3.org/2001/XMLSchema";
    ArrayList docs;
    Element schemaElement;

    private static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";

    /**
     * Create a new serializer.
     */
    public XmlSchemaSerializer() {
        docs = new ArrayList();
        schema_ns = new Hashtable();
    }

    /**
     * Serialize an entire schema, returning an array of DOM Documents, one per XSL file.
     * @param schemaObj The XML Schema.
     * @param serializeIncluded whether to create DOM trees for any included or imported
     * schemas.
     * @return Documents. If serializeIncluded is false, the array with have one entry.
     * The main document is always first.
     * @throws XmlSchemaSerializerException
     */
    public Document[] serializeSchema(XmlSchema schemaObj,
                                             boolean serializeIncluded) throws XmlSchemaSerializerException {
        return serializeSchemaElement(schemaObj, serializeIncluded);
    }

    Document[] serializeSchemaElement(XmlSchema schemaObj,
                                      boolean serializeIncluded) throws XmlSchemaSerializerException {

        XmlSchemaObjectCollection items = schemaObj.getItems();
        Document serializedSchemaDocs;
        try {
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            docFac.setNamespaceAware(true);
            DocumentBuilder builder = docFac.newDocumentBuilder();
            serializedSchemaDocs = builder.newDocument();
        } catch (ParserConfigurationException e) {
            throw new XmlSchemaException(e.getMessage());
        }

        Element serializedSchema;

        serializedSchema = setupNamespaces(serializedSchemaDocs, schemaObj);
        schemaElement = serializedSchema;

        if (schemaObj.syntacticalTargetNamespace != null) {
            serializedSchema.setAttribute("targetNamespace", schemaObj.syntacticalTargetNamespace);

            String targetNS =
                    (String)schema_ns.get(schemaObj.syntacticalTargetNamespace);

            //if the namespace is not entered then add
            //the targetNamespace
            if (targetNS == null) {
                String prefix = null;
                if(schemaObj.getNamespaceContext() != null) {
                    prefix = schemaObj.getNamespaceContext().getPrefix(schemaObj.syntacticalTargetNamespace);
                }
                if(prefix == null && schemaObj.parent != null && schemaObj.parent.getNamespaceContext() != null) {
                    prefix = schemaObj.parent.getNamespaceContext().getPrefix(schemaObj.syntacticalTargetNamespace);
                }
                //check if the chosen prefix is ok
                if(prefix == null) {
                    if (serializedSchema.getAttributeNode("xmlns") == null) {
                        prefix = "";
                    }
                } else {
                    String ns = serializedSchema.getAttribute("xmlns:" + prefix);
                    if (ns != null && !"".equals(ns)) {
                        prefix = null;
                    }
                }
                if (prefix == null) {
                    //find a usable prefix
                    int count = 0;
                    prefix = "tns";
                    String ns = serializedSchema.getAttribute("xmlns:" + prefix);
                    while (ns != null && !"".equals(ns)) {
                        ++count;
                        prefix = "tns" + count;
                        ns = serializedSchema.getAttribute("xmlns:" + prefix);
                    }
                }
                if ("".equals(prefix)) {
                    serializedSchema.setAttributeNS(XMLNS_NAMESPACE_URI,
                                                    "xmlns", schemaObj.syntacticalTargetNamespace);
                } else {
                    serializedSchema.setAttributeNS(XMLNS_NAMESPACE_URI,
                                                    "xmlns:" + prefix, schemaObj.syntacticalTargetNamespace);
                }
                schema_ns.put(schemaObj.syntacticalTargetNamespace, prefix);
            }
        }


        //todo: implement xml:lang,
        if (schemaObj.attributeFormDefault != null) {
            String formQualified = schemaObj.attributeFormDefault.getValue();

            if (!formQualified.equals(XmlSchemaForm.NONE))
                serializedSchema.setAttribute("attributeFormDefault", convertString(formQualified));
        }

        if (schemaObj.elementFormDefault != null) {
            String formQualified = schemaObj.elementFormDefault.getValue();

            if (!formQualified.equals(XmlSchemaForm.NONE))
                serializedSchema.setAttribute("elementFormDefault", convertString(formQualified));
        }


        if (schemaObj.annotation != null) {
            Element annotation = serializeAnnotation(serializedSchemaDocs,
                    schemaObj.annotation, schemaObj);
            serializedSchema.appendChild(annotation);
        }
        if (schemaObj.id != null) {
            serializedSchema.setAttribute("id",
                    schemaObj.id);
        }
        if (schemaObj.blockDefault != null) {
            String blockDefault = schemaObj.blockDefault.getValue();
            if (!blockDefault.equals(Constants.BlockConstants.NONE)) {
                blockDefault = convertString(blockDefault);
                serializedSchema.setAttribute("blockDefault", blockDefault);
            }
        }
        if (schemaObj.finalDefault != null) {
            String finalDefault = schemaObj.finalDefault.getValue();
            if (!finalDefault.equals(Constants.BlockConstants.NONE)) {
                finalDefault = convertString(finalDefault);
                serializedSchema.setAttribute("finalDefault", finalDefault);
            }
        }

        if (schemaObj.version != null) {
            serializedSchema.setAttribute("version", schemaObj.version);
        }

        //after serialize the schema add into documentation
        //and add to document collection array  which at the end
        //returned
        serializeSchemaChild(items, serializedSchema, serializedSchemaDocs,
                schemaObj, serializeIncluded);

        //process extension elements/attributes
        processExtensibilityComponents(schemaObj,serializedSchema);


        serializedSchemaDocs.appendChild(serializedSchema);
        docs.add(serializedSchemaDocs);


        Document[] serializedDocs = new Document[docs.size()];
        docs.toArray(serializedDocs);

        return serializedDocs;
    }

    private void serializeSchemaChild(XmlSchemaObjectCollection items,
                                      Element serializedSchema, Document serializedSchemaDocs,
                                      XmlSchema schemaObj, boolean serializeIncluded)
            throws XmlSchemaSerializerException {

        int itemsLength = items.getCount();
        /**
         * For each of the items that belong to this schema,
         * serialize each member found.
         * Permittable member is: element, simpleType, complexType,
         * group, attrributeGroup, Attribute, include, import and redefine.
         * if any of the member found then serialize the component.
         */

        // Since imports and includes need to be the first items of the
        // serialized schema. So this loop does the serialization of the
        // imports and includes

        for (int i = 0; i < itemsLength; i++) {
            XmlSchemaObject obj = items.getItem(i);
            if (obj instanceof XmlSchemaInclude) {
                Element e = serializeInclude(serializedSchemaDocs,
                        (XmlSchemaInclude) obj, schemaObj, serializeIncluded);
                serializedSchema.appendChild(e);
            } else if (obj instanceof XmlSchemaImport) {
                Element e = serializeImport(serializedSchemaDocs,
                        (XmlSchemaImport) obj, schemaObj, serializeIncluded);
                serializedSchema.appendChild(e);
            }
        }

        // reloop to serialize the others
        for (int i = 0; i < itemsLength; i++) {
            XmlSchemaObject obj = items.getItem(i);

            if (obj instanceof XmlSchemaElement) {
                Element e = serializeElement(serializedSchemaDocs,
                        (XmlSchemaElement) obj, schemaObj);
                serializedSchema.appendChild(e);

            } else if (obj instanceof XmlSchemaSimpleType) {
                Element e = serializeSimpleType(serializedSchemaDocs,
                        (XmlSchemaSimpleType) obj, schemaObj);
                serializedSchema.appendChild(e);
            } else if (obj instanceof XmlSchemaComplexType) {
                Element e = serializeComplexType(serializedSchemaDocs,
                        (XmlSchemaComplexType) obj, schemaObj);
                serializedSchema.appendChild(e);
            } else if (obj instanceof XmlSchemaGroup) {
                Element e = serializeGroup(serializedSchemaDocs,
                        (XmlSchemaGroup) obj, schemaObj);
                serializedSchema.appendChild(e);
            } else if (obj instanceof XmlSchemaAttributeGroup) {
                Element e = serializeAttributeGroup(serializedSchemaDocs,
                        (XmlSchemaAttributeGroup) obj, schemaObj);
                serializedSchema.appendChild(e);
            } else if (obj instanceof XmlSchemaAttribute) {
                Element e = serializeAttribute(serializedSchemaDocs,
                        (XmlSchemaAttribute) obj, schemaObj);
                serializedSchema.appendChild(e);
            } else if (obj instanceof XmlSchemaRedefine) {
                Element e = serializeRedefine(serializedSchemaDocs,
                        (XmlSchemaRedefine) obj, schemaObj);
                serializedSchema.appendChild(e);
            }
        }
    }

    /**
     * Set up &lt;schema&gt; namespaces appropriately and append that attr
     * into specified element
     */
    private Element setupNamespaces(Document schemaDocs, XmlSchema schemaObj) {
        NamespacePrefixList ctx = schemaObj.getNamespaceContext();
        if (ctx == null) {
        	schemaObj.schema_ns_prefix = null;
        	xsdPrefix = null;
        } else {
        	schemaObj.schema_ns_prefix = ctx.getPrefix(xsdNamespace);
        	xsdPrefix = schemaObj.schema_ns_prefix;
        }

        if(xsdPrefix == null) {
            //find a prefix to use
            xsdPrefix = XMLConstants.DEFAULT_NS_PREFIX;
            // Note: NULL_NS_URI is *not* the same as the null reference!
            // Java 1.4 hasn't got NULL_NS_URI, it's just "".
            if (ctx != null && !"".equals(ctx.getNamespaceURI(xsdPrefix))) {
                xsdPrefix = "xsd";
            }
            int count = 0;
            while (ctx != null && !"".equals(ctx.getNamespaceURI(xsdPrefix))) {
                xsdPrefix = "xsd" + ++count;
            }
            schemaObj.schema_ns_prefix = xsdPrefix;
        }

        Element schemaEl = createNewElement(schemaDocs, "schema",
                                            schemaObj.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (ctx != null) {
            String[] prefixes = ctx.getDeclaredPrefixes();
            for (int i = 0;  i < prefixes.length;  i++) {
                String prefix = prefixes[i];
                String uri = ctx.getNamespaceURI(prefix);
                if (uri != null && prefix != null) {
                    if ("".equals(prefix) || !schema_ns.containsKey(uri)) {
                        schema_ns.put(uri, prefix);
                    }
                    prefix = (prefix.length() > 0) ? "xmlns:" + prefix : "xmlns";
                    schemaEl.setAttributeNS(XMLNS_NAMESPACE_URI,
                                            prefix, uri);
                }
            }
        }
        //for schema that not set the xmlns attrib member
        if (schema_ns.get(xsdNamespace) == null) {
            schema_ns.put(xsdNamespace, xsdPrefix);
            if ("".equals(xsdPrefix)) {
                schemaEl.setAttributeNS(XMLNS_NAMESPACE_URI,
                                        "xmlns", xsdNamespace);
            } else {
                schemaEl.setAttributeNS(XMLNS_NAMESPACE_URI,
                                        "xmlns:" + xsdPrefix, xsdNamespace);
            }
            schemaObj.schema_ns_prefix = xsdPrefix;
        }
        return schemaEl;
    }

    /**
     * *********************************************************************
     * Element serializeInclude(Document doc, XmlSchemaInclude includeObj,
     * XmlSchema schema)throws XmlSchemaSerializerException
     * <p/>
     * set appropriate attribute as per this object attribute availability.
     * Call included schema to append to this schema document collection.
     * Then add the document created into document pool.
     * <p/>
     * Parameter:
     * doc          - Document the parent use.
     * includeObj   - XmlSchemaInclude that will be serialized.
     * schema       - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element object representation of XmlSchemaInclude
     * **********************************************************************
     */
    Element serializeInclude(Document doc, XmlSchemaInclude includeObj,
                             XmlSchema schema, boolean serializeIncluded)
            throws XmlSchemaSerializerException {

        Element includeEl = createNewElement(doc, "include",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (includeObj.schemaLocation != null) {
            includeEl.setAttribute("schemaLocation",
                    includeObj.schemaLocation);
        }

        if (includeObj.id != null)
            includeEl.setAttribute("id", includeObj.id);

        if (includeObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    includeObj.annotation, schema);
            includeEl.appendChild(annotation);
        }

        //Get the XmlSchema obj and append that to the content
        XmlSchema includedSchemaObj = includeObj.getSchema();
        if (includedSchemaObj != null && serializeIncluded) {
            XmlSchemaSerializer includeSeri = new XmlSchemaSerializer();
            includeSeri.serializeSchemaElement(includedSchemaObj, true);
//            XmlSchemaObjectCollection ii = includedSchemaObj.getItems();
            docs.addAll(includeSeri.docs);
        }

        //process includes
        processExtensibilityComponents(includeObj,includeEl);

        return includeEl;
    }

    /**
     * *********************************************************************
     * Element serializeImport(Document doc, XmlSchemaImport importObj,
     * XmlSchema schema)throws XmlSchemaSerializerException
     * <p/>
     * Add each of the attribute of XmlSchemaImport obj into import Element
     * Then serialize schema that is included by this import.  Include the
     * serialized schema into document pool.
     * <p/>
     * Parameter:
     * doc          - Document the parent use.
     * includeObj   - XmlSchemaInclude that will be serialized.
     * schema       - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element object representation of XmlSchemaImport
     * **********************************************************************
     */
    Element serializeImport(Document doc, XmlSchemaImport importObj,
                            XmlSchema schema, boolean serializeIncluded)
            throws XmlSchemaSerializerException {

        Element importEl = createNewElement(doc, "import",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (importObj.namespace != null)
            importEl.setAttribute("namespace",
                    importObj.namespace);

        if (importObj.schemaLocation != null && !importObj.schemaLocation.trim().equals(""))
            importEl.setAttribute("schemaLocation",
                    importObj.schemaLocation);

        if (importObj.id != null)
            importEl.setAttribute("id", importObj.id);

        if (importObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    importObj.annotation, schema);

            importEl.appendChild(annotation);
        }

        if (importObj.schema != null && serializeIncluded) {


            XmlSchemaSerializer importSeri = new XmlSchemaSerializer();
            importSeri.serializeSchemaElement(importObj.schema, serializeIncluded);
            docs.addAll(importSeri.docs);
        }

         //process extension
        processExtensibilityComponents(importObj,importEl);

        return importEl;
    }

    /**
     * *********************************************************************
     * Element serializeRedefine(Document doc, XmlSchemaRedefine redefineObj,
     * XmlSchema schema)throws XmlSchemaSerializerException
     * <p/>
     * Add each of the attribute of XmlSchemaImport obj into import Element
     * Then serialize schema that is included by this import.  Include the
     * serialized schema into document pool.
     * <p/>
     * Parameter:
     * doc           - Document the parent use.
     * redefineObj   - XmlSchemaInclude that will be serialized.
     * schema        - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element object representation of XmlSchemaRedefine
     * **********************************************************************
     */
    Element serializeRedefine(Document doc, XmlSchemaRedefine redefineObj,
                              XmlSchema schema) throws XmlSchemaSerializerException {

        Element redefine = createNewElement(doc, "redefine",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (redefineObj.schemaLocation != null)
            redefine.setAttribute("schemaLocation",
                    redefineObj.schemaLocation);
        else
            throw new XmlSchemaSerializerException("redefine must have "
                    + "schemaLocation fields fill");

        if (redefineObj.id != null)
            redefine.setAttribute("id", redefineObj.id);

        if (redefineObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    redefineObj.annotation, schema);
            redefine.appendChild(annotation);
        }
        int itemsLength = redefineObj.items.getCount();
        for (int i = 0; i < itemsLength; i++) {
            XmlSchemaObject obj = redefineObj.items.getItem(i);
            if (obj instanceof XmlSchemaSimpleType) {
                Element simpleType = serializeSimpleType(doc,
                        (XmlSchemaSimpleType) obj, schema);
                redefine.appendChild(simpleType);
            } else if (obj instanceof XmlSchemaComplexType) {
                Element complexType = serializeComplexType(doc,
                        (XmlSchemaComplexType) obj, schema);
                redefine.appendChild(complexType);
            } else if (obj instanceof XmlSchemaGroupRef) {
                Element groupRef = serializeGroupRef(doc,
                        (XmlSchemaGroupRef) obj, schema);
                redefine.appendChild(groupRef);
            } else if (obj instanceof XmlSchemaGroup) {
                Element group = serializeGroup(doc,
                        (XmlSchemaGroup) obj, schema);
                redefine.appendChild(group);
            } else if (obj instanceof XmlSchemaAttributeGroup) {
                Element attributeGroup = serializeAttributeGroup(doc,
                        (XmlSchemaAttributeGroup) obj, schema);
                redefine.appendChild(attributeGroup);
            } else if (obj instanceof XmlSchemaAttributeGroupRef) {
                Element attributeGroupRef = serializeAttributeGroupRef(doc,
                        (XmlSchemaAttributeGroupRef) obj, schema);
                redefine.appendChild(attributeGroupRef);
            }
        }

            //process extension
        processExtensibilityComponents(redefineObj,redefine);

        return redefine;
    }

    /**
     * *********************************************************************
     * Element serializeElement(Document doc, XmlSchemaElement elementObj,
     * XmlSchema schema) throws XmlSchemaSerializerException
     * <p/>
     * Each member of Element will be appended and pass the element
     * created.  Element processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc           - Document the parent use.
     * elementObj   - XmlSchemaInclude that will be serialized.
     * schema        - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element object of element.
     * **********************************************************************
     */
    Element serializeElement(Document doc, XmlSchemaElement elementObj,
                             XmlSchema schema) throws XmlSchemaSerializerException {
        Element serializedEl = createNewElement(doc, "element",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);


        if (elementObj.refName != null) {

            String resolvedName = resolveQName(elementObj.refName, schema);
            serializedEl.setAttribute("ref", resolvedName);
        } else if (elementObj.name != null && elementObj.name.length() > 0) {
            serializedEl.setAttribute("name",
                    elementObj.name);
        }

        if (elementObj.isAbstract)
            serializedEl.setAttribute("abstract", "true");

        String block = elementObj.block.getValue();
        if (!block.equals(Constants.BlockConstants.NONE)) {
            block = convertString(block);
            serializedEl.setAttribute("block", block);
        }
        if (elementObj.defaultValue != null)
            serializedEl.setAttribute("default",
                    elementObj.defaultValue);

        String finalDerivation = elementObj.finalDerivation.getValue();
        if (!finalDerivation.equals(Constants.BlockConstants.NONE)) {
            finalDerivation = convertString(finalDerivation);
            serializedEl.setAttribute("final",
                    finalDerivation);
        }
        if (elementObj.fixedValue != null)
            serializedEl.setAttribute("fixed",
                    elementObj.fixedValue);

        String formDef = elementObj.form.getValue();
        if (!formDef.equals(XmlSchemaForm.NONE)) {
            formDef = convertString(formDef);
            serializedEl.setAttribute("form", formDef);
        }
        if (elementObj.id != null)
            serializedEl.setAttribute("id", elementObj.id);


        serializeMaxMinOccurs(elementObj, serializedEl);


        if (elementObj.substitutionGroup != null) {
            String resolvedQName = resolveQName(elementObj.substitutionGroup, schema);
            serializedEl.setAttribute("substitutionGroup",
                    resolvedQName);
        }
        if (elementObj.schemaTypeName != null) {
            String resolvedName = resolveQName(elementObj.schemaTypeName, schema);
            serializedEl.setAttribute("type", resolvedName);
        }
        if (elementObj.annotation != null) {
            Element annotationEl = serializeAnnotation(doc,
                    elementObj.annotation, schema);
            serializedEl.appendChild(annotationEl);
        }
        if (elementObj.schemaType != null && elementObj.schemaTypeName == null) {
            if (elementObj.schemaType instanceof XmlSchemaComplexType) {

                Element complexType = serializeComplexType(doc,
                        (XmlSchemaComplexType) elementObj.schemaType, schema);
                serializedEl.appendChild(complexType);
            } else if (elementObj.schemaType instanceof XmlSchemaSimpleType) {
                Element simpleType = serializeSimpleType(doc,
                        (XmlSchemaSimpleType) elementObj.schemaType, schema);
                serializedEl.appendChild(simpleType);
            }
        }
        if (elementObj.constraints.getCount() > 0) {
            for (int i = 0; i < elementObj.constraints.getCount(); i++) {
                Element constraint = serializeIdentityConstraint(doc,
                        (XmlSchemaIdentityConstraint) elementObj.constraints.getItem(i),
                        schema);
                serializedEl.appendChild(constraint);
            }
        }
        if (elementObj.isNillable) {
            serializedEl.setAttribute("nillable", "true");
        }

            //process extension
        processExtensibilityComponents(elementObj,serializedEl);

        return serializedEl;
    }

    /**
     * *********************************************************************
     * Element serializeSimpleType(Document doc,
     * XmlSchemaSimpleType simpleTypeObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of simple type will be appended and pass the element
     * created.  Simple type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc               - Document the parent use.
     * simpleTypeObj     - XmlSchemaSimpleType that will be serialized.
     * schema            - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element object of SimpleType
     * **********************************************************************
     */
    Element serializeSimpleType(Document doc, XmlSchemaSimpleType simpleTypeObj,
                                XmlSchema schema) throws XmlSchemaSerializerException {

        Element serializedSimpleType = createNewElement(doc, "simpleType",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);


        String tmp;
        tmp = simpleTypeObj.finalDerivation.getValue();
        if (!tmp.equals(Constants.BlockConstants.NONE)) {

            tmp = convertString(tmp);
            serializedSimpleType.setAttribute("final", tmp);
        }
        if (simpleTypeObj.id != null)
            serializedSimpleType.setAttribute("id",
                    simpleTypeObj.id);
        if ((simpleTypeObj.name != null) && (!simpleTypeObj.name.equals("")))
            serializedSimpleType.setAttribute("name",
                    simpleTypeObj.name);
        if (simpleTypeObj.annotation != null) {
            Element annotationEl = serializeAnnotation(doc,
                    simpleTypeObj.annotation, schema);
            serializedSimpleType.appendChild(annotationEl);
        }
        if (simpleTypeObj.content != null) {
            if (simpleTypeObj.content instanceof XmlSchemaSimpleTypeRestriction) {
                Element restEl = serializeSimpleTypeRestriction(doc,
                        (XmlSchemaSimpleTypeRestriction) simpleTypeObj.content,
                        schema);
                serializedSimpleType.appendChild(restEl);
            } else if (simpleTypeObj.content instanceof XmlSchemaSimpleTypeList) {
                Element listEl = serializeSimpleTypeList(doc,
                        (XmlSchemaSimpleTypeList) simpleTypeObj.content, schema);
                serializedSimpleType.appendChild(listEl);
            } else if (simpleTypeObj.content instanceof XmlSchemaSimpleTypeUnion) {
                Element unionEl = serializeSimpleTypeUnion(doc,
                        (XmlSchemaSimpleTypeUnion) simpleTypeObj.content, schema);
                serializedSimpleType.appendChild(unionEl);
            }/*else
			   throw new XmlSchemaSerializerException("Invalid type inserted "
			   + "in simpleType content, the content is: "
			   + simpleTypeObj.content.getClass().getName()
			   + " valid content should be XmlSchemaSimpleTypeunion, "
			   + "XmlSchemaSimpleTyperestriction or list");*/
        }/*else
		   throw new XmlSchemaSerializerException("simple type must be set "
		   + "with content, either union, restriction or list");*/

            //process extension
        processExtensibilityComponents(simpleTypeObj,serializedSimpleType);

        return serializedSimpleType;
    }

    /**
     * *********************************************************************
     * Element serializeSimpleTypeRestriction(Document doc,
     * XmlSchemaSimpleTypeRestriction restrictionObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of simple type will be appended and pass the element
     * created.  Simple type's <restriction> processed according to w3c
     * Recommendation May 2 2001.
     * <p/>
     * Parameter:
     * doc               - Document the parent use.
     * restrictionObj    - XmlSchemaRestriction that will be serialized.
     * schema            - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of simple type restriction and its child.
     * **********************************************************************
     */
    Element serializeSimpleTypeRestriction(Document doc,
                                           XmlSchemaSimpleTypeRestriction restrictionObj, XmlSchema schema)
            throws XmlSchemaSerializerException {
        //todo: need to implement any attribute that related to non schema namespace
        Element serializedRestriction = createNewElement(doc, "restriction",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (schema.schema_ns_prefix.length() > 0)
            serializedRestriction.setPrefix(schema.schema_ns_prefix);
        if (restrictionObj.baseTypeName != null) {
            String baseType = resolveQName(restrictionObj.baseTypeName, schema);
            serializedRestriction.setAttribute("base", baseType);
        } else if (restrictionObj.baseType != null && restrictionObj.baseType
                instanceof XmlSchemaSimpleType) {
            Element inlineSimpleType = serializeSimpleType(doc,
                    restrictionObj.baseType, schema);
            serializedRestriction.appendChild(inlineSimpleType);
        } else
            throw new XmlSchemaSerializerException("restriction must be define "
                    + "with specifying base or inline simpleType");

        if (restrictionObj.id != null)
            serializedRestriction.setAttribute("id",
                    restrictionObj.id);

        if (restrictionObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    restrictionObj.annotation, schema);
            serializedRestriction.appendChild(annotation);
        }
        if (restrictionObj.facets.getCount() > 0) {
            int facetsNum = restrictionObj.facets.getCount();
            for (int i = 0; i < facetsNum; i++) {
                Element facetEl = serializeFacet(doc,
                        (XmlSchemaFacet) restrictionObj.facets.getItem(i), schema);
                serializedRestriction.appendChild(facetEl);
            }
        }

            //process extension
        processExtensibilityComponents(restrictionObj,serializedRestriction);

        return serializedRestriction;
    }

    /**
     * *********************************************************************
     * Element serializeFacet(Document doc, XmlSchemaFacet facetObj,
     * XmlSchema schema) throws XmlSchemaSerializerException{
     * <p/>
     * detect what type of facet and cass appropriatelly,
     * construct the element and pass it.
     * <p/>
     * Parameter:
     * doc       - Document the parent use.
     * facetObj  - XmlSchemaFacet that will be serialized.
     * schema    - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of simple type with facet.
     * **********************************************************************
     */
    Element serializeFacet(Document doc, XmlSchemaFacet facetObj,
                           XmlSchema schema) throws XmlSchemaSerializerException {

        Element serializedFacet;

        if (facetObj instanceof XmlSchemaMinExclusiveFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "minExclusive");
        else if (facetObj instanceof XmlSchemaMinInclusiveFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "minInclusive");
        else if (facetObj instanceof XmlSchemaMaxExclusiveFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "maxExclusive");
        else if (facetObj instanceof XmlSchemaMaxInclusiveFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "maxInclusive");
        else if (facetObj instanceof XmlSchemaTotalDigitsFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "totalDigits");
        else if (facetObj instanceof XmlSchemaFractionDigitsFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "fractionDigits");
        else if (facetObj instanceof XmlSchemaLengthFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "length");
        else if (facetObj instanceof XmlSchemaMinLengthFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "minLength");
        else if (facetObj instanceof XmlSchemaMaxLengthFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "maxLength");
        else if (facetObj instanceof XmlSchemaEnumerationFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "enumeration");
        else if (facetObj instanceof XmlSchemaWhiteSpaceFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "whiteSpace");
        else if (facetObj instanceof XmlSchemaPatternFacet)
            serializedFacet = constructFacet(facetObj, doc, schema,
                    "pattern");
        else
            throw new XmlSchemaSerializerException("facet not exist "
                    + facetObj.getClass().getName());

        if (facetObj.id != null)
            serializedFacet.setAttribute("id", facetObj.id);
//        if (facetObj.annotation != null) {
//            Element annotation = serializeAnnotation(doc, facetObj.annotation,
//                                                     schema);
//            serializedFacet.appendChild(annotation);
//        }

            //process extension
        processExtensibilityComponents(facetObj,serializedFacet);

        return serializedFacet;
    }

    private Element constructFacet(XmlSchemaFacet facetObj, Document doc,
                                   XmlSchema schema, String tagName) {

        Element facetEl = createNewElement(doc, tagName,
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        facetEl.setAttribute("value",
                facetObj.value.toString());
        if (facetObj.fixed)
            facetEl.setAttribute("fixed", "true");

        if (facetObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    facetObj.annotation, schema);
            facetEl.appendChild(annotation);
        }
        return facetEl;
    }

    /**
     * *********************************************************************
     * Element serializeComplexType(Document doc,
     * XmlSchemaComplexType complexTypeObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc             - Document the parent use.
     * complexTypeObj  - XmlSchemaFacet that will be serialized.
     * schema          - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of complexType.
     * **********************************************************************
     */
    Element serializeComplexType(Document doc,
                                 XmlSchemaComplexType complexTypeObj, XmlSchema schema)
            throws XmlSchemaSerializerException {

        //todo: need to implement abstract, id, mixed
        Element serializedComplexType = createNewElement(doc,
                "complexType", schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if ((complexTypeObj.name != null) && (!complexTypeObj.name.equals("")))
            serializedComplexType.setAttribute("name",
                    complexTypeObj.name);
        /*if(complexTypeObj.annotation != null){
		  Element annotationEl = serializeAnnotation(doc,
		  complexTypeObj.annotation, schema);
		  serializedComplexType.appendChild(annotationEl);
		  }*/

        if (complexTypeObj.isMixed)
            serializedComplexType.setAttribute("mixed", "true");
        if (complexTypeObj.isAbstract)
            serializedComplexType.setAttribute(
                    "abstract", "true");
        if (complexTypeObj.id != null)
            serializedComplexType.setAttribute("id",
                    complexTypeObj.id);

        if (complexTypeObj.annotation != null) {
            Element annotationEl = serializeAnnotation(doc,
                    complexTypeObj.annotation, schema);
            serializedComplexType.appendChild(annotationEl);
        }

        if (complexTypeObj.contentModel instanceof XmlSchemaSimpleContent) {
            Element simpleContent = serializeSimpleContent(doc,
                    (XmlSchemaSimpleContent) complexTypeObj.contentModel, schema);
            serializedComplexType.appendChild(simpleContent);
        } else if (complexTypeObj.contentModel instanceof
                XmlSchemaComplexContent) {

            Element complexContent = serializeComplexContent(doc,
                    (XmlSchemaComplexContent) complexTypeObj.contentModel, schema);
            serializedComplexType.appendChild(complexContent);
        }

        if (complexTypeObj.particle instanceof XmlSchemaSequence) {
            Element sequence = serializeSequence(doc,
                    (XmlSchemaSequence) complexTypeObj.particle, schema);
            serializedComplexType.appendChild(sequence);
        } else if (complexTypeObj.particle instanceof XmlSchemaChoice) {
            Element choice = serializeChoice(doc,
                    (XmlSchemaChoice) complexTypeObj.particle, schema);
            serializedComplexType.appendChild(choice);
        } else if (complexTypeObj.particle instanceof XmlSchemaAll) {
            Element all = serializeAll(doc,
                    (XmlSchemaAll) complexTypeObj.particle, schema);
            serializedComplexType.appendChild(all);
        } else if (complexTypeObj.particle instanceof XmlSchemaGroupRef) {
            Element group = serializeGroupRef(doc,
                    (XmlSchemaGroupRef) complexTypeObj.particle, schema);
            serializedComplexType.appendChild(group);
        }

        String block = complexTypeObj.block.getValue();
        if (!block.equals(Constants.BlockConstants.NONE)) {
            block = convertString(block);
            serializedComplexType.setAttribute(
                    "block", block);
        }
        String finalDerivation = complexTypeObj.finalDerivation.getValue();
        if (!finalDerivation.equals(Constants.BlockConstants.NONE)) {
            finalDerivation = convertString(finalDerivation);
            serializedComplexType.setAttribute("final",
                    finalDerivation);
        }

        XmlSchemaObjectCollection attrColl = complexTypeObj.attributes;
        if (attrColl.getCount() > 0)
            setupAttr(doc, attrColl, schema, serializedComplexType);

        XmlSchemaAnyAttribute anyAttribute = complexTypeObj.getAnyAttribute();
        if(anyAttribute != null) {
        	serializedComplexType.appendChild(serializeAnyAttribute(doc, anyAttribute, schema));
        }


            //process extension
        processExtensibilityComponents(complexTypeObj,serializedComplexType);

        return serializedComplexType;
    }

    /**
     * *********************************************************************
     * Element serializeSequence(Document doc, XmlSchemaSequence sequenceObj,
     * XmlSchema schema)throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  `Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc             - Document the parent use.
     * sequenceObj  - XmlSchemaFacet that will be serialized.
     * schema          - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of sequence particle.
     * **********************************************************************
     */
    Element serializeSequence(Document doc, XmlSchemaSequence sequenceObj,
                              XmlSchema schema) throws XmlSchemaSerializerException {

        Element sequence = createNewElement(doc, "sequence",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);


        if (sequenceObj.id != null)
            sequence.setAttribute("id", sequenceObj.id);


        serializeMaxMinOccurs(sequenceObj, sequence);

        XmlSchemaObjectCollection seqColl = sequenceObj.items;
        int containLength = seqColl.getCount();
        for (int i = 0; i < containLength; i++) {
            XmlSchemaObject obj = seqColl.getItem(i);
            if (obj instanceof XmlSchemaElement) {
                Element el = serializeElement(doc,
                        (XmlSchemaElement) obj, schema);
                sequence.appendChild(el);
            } else if (obj instanceof XmlSchemaGroupRef) {
                Element group = serializeGroupRef(doc,
                        (XmlSchemaGroupRef) obj, schema);
                sequence.appendChild(group);
            } else if (obj instanceof XmlSchemaChoice) {
                Element choice = serializeChoice(doc,
                        (XmlSchemaChoice) obj, schema);
                sequence.appendChild(choice);
            } else if (obj instanceof XmlSchemaSequence) {
                Element sequenceChild = serializeSequence(doc,
                        (XmlSchemaSequence) obj, schema);
                sequence.appendChild(sequenceChild);
            } else if (obj instanceof XmlSchemaAny) {
                Element any = serializeAny(doc, (XmlSchemaAny) obj, schema);
                sequence.appendChild(any);
            }
        }

            //process extension
        processExtensibilityComponents(sequenceObj,sequence);

        return sequence;
    }

    /**
     * A common method to serialize the max/min occurs
     * @param particle
     * @param element
     */
	private void serializeMaxMinOccurs(XmlSchemaParticle particle,
			Element element) {
		if (particle.maxOccurs < Long.MAX_VALUE &&
                (particle.maxOccurs > 1 || particle.maxOccurs == 0))
            element.setAttribute("maxOccurs",
                    particle.maxOccurs + "");
        else if (particle.maxOccurs == Long.MAX_VALUE)
            element.setAttribute("maxOccurs",
                    "unbounded");
        //else not serialized

        //1 is the default and hence not serialized
        //there is no valid case where min occurs can be unbounded!
        if (particle.minOccurs > 1 || particle.minOccurs == 0)
            element.setAttribute("minOccurs",
                    particle.minOccurs + "");
	}

    /**
     * *********************************************************************
     * Element serializeAttribute(Document doc, XmlSchemaAttribute attributeObj,
     * XmlSchema schema) throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  `Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc             - Document the parent use.
     * attributeObj    - XmlSchemaAttribute that will be serialized.
     * schema          - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of attribute.
     * **********************************************************************
     */
    Element serializeAttribute(Document doc, XmlSchemaAttribute attributeObj,
                               XmlSchema schema) throws XmlSchemaSerializerException {

        Element attribute = createNewElement(doc, "attribute",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);
        if (attributeObj.refName != null) {
            String refName =
                    resolveQName(attributeObj.refName, schema);
            attribute.setAttribute("ref", refName);
        } else if (attributeObj.name != null)
            attribute.setAttribute("name",
                    attributeObj.name);

        if (attributeObj.schemaTypeName != null) {
            String typeName =
                    resolveQName(attributeObj.schemaTypeName, schema);
            attribute.setAttribute("type", typeName);
        }

        if (attributeObj.defaultValue != null)
            attribute.setAttribute("default",
                    attributeObj.defaultValue);
        if (attributeObj.fixedValue != null)
            attribute.setAttribute("fixed",
                    attributeObj.fixedValue);

        String formType = attributeObj.form.getValue();
        if (!formType.equals(XmlSchemaForm.NONE)) {
            formType = convertString(formType);
            attribute.setAttribute("form", formType);
        }
        if (attributeObj.id != null)
            attribute.setAttribute("id", attributeObj.id);

        String useType = attributeObj.use.getValue();
        if (!useType.equals(Constants.BlockConstants.NONE)) {
            useType = convertString(useType);
            attribute.setAttribute("use", useType);
        }
        if (attributeObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    attributeObj.annotation, schema);
            attribute.appendChild(annotation);
        }


        if (attributeObj.schemaType != null) {
            try {
                XmlSchemaSimpleType simpleType =
                        attributeObj.schemaType;
                Element simpleTypeEl = serializeSimpleType(doc,
                        simpleType, schema);
                attribute.appendChild(simpleTypeEl);
            } catch (ClassCastException e) {
                throw new XmlSchemaSerializerException("only inline simple type allow as attribute's inline type");
            }
        }

        Attr[] unhandled = attributeObj.getUnhandledAttributes();

        Hashtable namespaces = new Hashtable();

        if (unhandled != null) {

            // this is to make the wsdl:arrayType work
            // since unhandles attributes are not handled this is a special case
            // but the basic idea is to see if there is any attibute whose value has ":"
            // if it is present then it is likely that it is a namespace prefix
            // do what is neccesary to get the real namespace for it and make
            // required changes to the prefix

            for (int i = 0; i < unhandled.length; i++) {
                String name = unhandled[i].getNodeName();
                String value = unhandled[i].getNodeValue();
                if (name.equals("xmlns")) {
                    namespaces.put("", value);
                } else if (name.startsWith("xmlns")) {
                    namespaces.put(name.substring(name.indexOf(":") + 1), value);
                }
            }

            for (int i = 0; i < unhandled.length; i++) {
                String value = unhandled[i].getNodeValue();
                String nodeName = unhandled[i].getNodeName();
                if (value.indexOf(":") > -1 && !nodeName.startsWith("xmlns")) {
                    String prefix = value.substring(0, value.indexOf(":"));
                    String oldNamespace;
                    if ((oldNamespace = (String) namespaces.get(prefix)) != null) {
                        value = value.substring(value.indexOf(":") + 1);
                        NamespacePrefixList ctx = schema.getNamespaceContext();
                        String[] prefixes = ctx.getDeclaredPrefixes();
                        for (int j = 0;  j < prefixes.length;  j++) {
                            String pref = prefixes[j];
                            String uri = ctx.getNamespaceURI(pref);
                            if (uri.equals(oldNamespace)) {
                                value = prefix + ":" + value;
                            }
                        }
                    }

                }
                if (unhandled[i].getNamespaceURI() != null)
                    attribute.setAttributeNS(unhandled[i].getNamespaceURI(), nodeName, value);
                else
                    attribute.setAttribute(nodeName, value);
            }
        }

            //process extension
        processExtensibilityComponents(attributeObj,attribute);

        return attribute;
    }

    /**
     * *********************************************************************
     * Element serializeChoice(Document doc, XmlSchemaChoice choiceObj,
     * XmlSchema schema) throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc             - Document the parent use.
     * choiceObj       - XmlSchemaChoice that will be serialized.
     * schema          - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of choice schema object.
     * **********************************************************************
     */
    Element serializeChoice(Document doc, XmlSchemaChoice choiceObj,
                            XmlSchema schema) throws XmlSchemaSerializerException {
        //todo: handle any non schema attri ?

        Element choice = createNewElement(doc, "choice",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);
        if (choiceObj.id != null)
            if (choiceObj.id.length() > 0)
                choice.setAttribute("id", choiceObj.id);


        serializeMaxMinOccurs(choiceObj, choice);


        /*
            if(choiceObj.maxOccursString != null)
            choice.setAttribute("maxOccurs",
            choiceObj.maxOccursString);
            else if(choiceObj.maxOccurs > 1)
            choice.setAttribute("maxOccurs",
            choiceObj.maxOccurs +"");
          */


        if (choiceObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    choiceObj.annotation, schema);
            choice.appendChild(annotation);
        }


        XmlSchemaObjectCollection itemColl = choiceObj.items;

        if (itemColl != null) {
            int itemLength = itemColl.getCount();

            for (int i = 0; i < itemLength; i++) {
                XmlSchemaObject obj = itemColl.getItem(i);

                if (obj instanceof XmlSchemaElement) {
                    Element el = serializeElement(doc,
                            (XmlSchemaElement) obj, schema);
                    choice.appendChild(el);
                } else if (obj instanceof XmlSchemaGroupRef) {
                    Element group = serializeGroupRef(doc,
                            (XmlSchemaGroupRef) obj, schema);
                    choice.appendChild(group);
                } else if (obj instanceof XmlSchemaChoice) {
                    Element inlineChoice = serializeChoice(doc,
                            (XmlSchemaChoice) obj, schema);
                    choice.appendChild(inlineChoice);
                } else if (obj instanceof XmlSchemaSequence) {
                    Element inlineSequence = serializeSequence(doc,
                            (XmlSchemaSequence) obj, schema);
                    choice.appendChild(inlineSequence);
                } else if (obj instanceof XmlSchemaAny) {
                    Element any = serializeAny(doc, (XmlSchemaAny) obj, schema);
                    choice.appendChild(any);
                }
            }
        }

            //process extension
        processExtensibilityComponents(choiceObj,choice);

        return choice;
    }

    /**
     * *********************************************************************
     * Element serializeAll(Document doc, XmlSchemaAll allObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc             - Document the parent use.
     * allObj          - XmlSchemaAll that will be serialized.
     * schema          - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of particle all.
     * **********************************************************************
     */
    Element serializeAll(Document doc, XmlSchemaAll allObj, XmlSchema schema)
            throws XmlSchemaSerializerException {
        Element allEl = createNewElement(doc, "all", schema.schema_ns_prefix,
                XmlSchema.SCHEMA_NS);

        serializeMaxMinOccurs(allObj, allEl);

        if (allObj.annotation != null) {
            Element annotation = serializeAnnotation(doc, allObj.annotation,
                    schema);
            allEl.appendChild(annotation);
        }

        XmlSchemaObjectCollection itemColl = allObj.items;

        if (itemColl != null) {
            int itemLength = itemColl.getCount();

            for (int i = 0; i < itemLength; i++) {
                XmlSchemaObject obj = itemColl.getItem(i);
                if (obj instanceof XmlSchemaElement) {
                    Element el = serializeElement(doc, (XmlSchemaElement) obj,
                            schema);
                    allEl.appendChild(el);
                } else
                    throw new XmlSchemaSerializerException("Only element "
                            + "allowed as child of all model type");
            }
        }

            //process extension
        processExtensibilityComponents(allObj,allEl);

        return allEl;
    }

    /**
     * *********************************************************************
     * Element serializeSimpleTypeList(Document doc,
     * XmlSchemaSimpleTypeList listObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc             - Document the parent use.
     * listObj         - XmlSchemaSimpleTypeList that will be serialized.
     * schema          - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of simple type with list method.
     * **********************************************************************
     */
    Element serializeSimpleTypeList(Document doc,
                                    XmlSchemaSimpleTypeList listObj, XmlSchema schema)
            throws XmlSchemaSerializerException {

        Element list = createNewElement(doc, "list",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (listObj.itemTypeName != null) {
            String listItemType = resolveQName(listObj.itemTypeName,
                    schema);
            list.setAttribute("itemType", listItemType);
        }
        if (listObj.id != null)
            list.setAttribute("id", listObj.id);

        else if (listObj.itemType != null) {
            Element inlineSimpleEl = serializeSimpleType(doc, listObj.itemType,
                    schema);
            list.appendChild(inlineSimpleEl);
        }
        if (listObj.annotation != null) {
            Element annotation = serializeAnnotation(doc, listObj.annotation, schema);
            list.appendChild(annotation);
        }

            //process extension
        processExtensibilityComponents(listObj,list);

        return list;
    }

    /**
     * *********************************************************************
     * Element serializeSimpleTypeUnion(Document doc,
     * XmlSchemaSimpleTypeUnion unionObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc              - Document the parent use.
     * unionObj         - XmlSchemaSimpleTypeUnion that will be serialized.
     * schema           - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of simple type with union method.
     * **********************************************************************
     */
    Element serializeSimpleTypeUnion(Document doc,
                                     XmlSchemaSimpleTypeUnion unionObj, XmlSchema schema)
            throws XmlSchemaSerializerException {


        Element union = createNewElement(doc, "union",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);
        if (unionObj.id != null)
            union.setAttribute("id", unionObj.id);

        if (unionObj.memberTypesSource != null)
            union.setAttribute("memberTypes",
                    unionObj.memberTypesSource);
        if (unionObj.baseTypes.getCount() > 0) {
            int baseTypesLength = unionObj.baseTypes.getCount();
            Element baseType;
            for (int i = 0; i < baseTypesLength; i++) {
                try {
                    baseType = serializeSimpleType(doc,
                            (XmlSchemaSimpleType) unionObj.baseTypes.getItem(i),
                            schema);
                    union.appendChild(baseType);
                } catch (ClassCastException e) {
                    throw new XmlSchemaSerializerException("only inline simple type allow as attribute's "
                            + "inline type");
                }
            }
        }
        if (unionObj.annotation != null) {
            Element annotation = serializeAnnotation(doc, unionObj.annotation,
                    schema);
            union.appendChild(annotation);
        }

            //process extension
        processExtensibilityComponents(unionObj,union);

        return union;
    }

    /**
     * *********************************************************************
     * Element serializeAny(Document doc, XmlSchemaAny anyObj, XmlSchema schema)
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc              - Document the parent use.
     * anyObj           - XmlSchemaAny that will be serialized.
     * schema           - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of any that is part of its parent.
     * **********************************************************************
     */
    Element serializeAny(Document doc, XmlSchemaAny anyObj, XmlSchema schema) {
        Element anyEl = createNewElement(doc, "any", schema.schema_ns_prefix,
                XmlSchema.SCHEMA_NS);
        if (anyObj.id != null)
            if (anyObj.id.length() > 0)
                anyEl.setAttribute("id", anyObj.id);

        serializeMaxMinOccurs(anyObj, anyEl);


        if (anyObj.namespace != null)
            anyEl.setAttribute("namespace",
                    anyObj.namespace);

        if (anyObj.processContent != null) {
            String value = anyObj.processContent.getValue();
            if (!value.equals(Constants.BlockConstants.NONE)) {
                String processContent = convertString(value);
                anyEl.setAttribute("processContents",
                        processContent);
            }
        }
        if (anyObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    anyObj.annotation, schema);
            anyEl.appendChild(annotation);
        }

            //process extension
        processExtensibilityComponents(anyObj,anyEl);

        return anyEl;
    }

    /**
     * *********************************************************************
     * Element serializeGroup(Document doc, XmlSchemaGroup groupObj,
     * XmlSchema schema) throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc                - Document the parent use.
     * groupObj           - XmlSchemaGroup that will be serialized.
     * schema             - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of group elements.
     * **********************************************************************
     */
    Element serializeGroup(Document doc, XmlSchemaGroup groupObj,
                           XmlSchema schema) throws XmlSchemaSerializerException {

        Element group = createNewElement(doc, "group",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (groupObj.name != null) {
            String grpName = groupObj.name.getLocalPart();
            if (grpName.length() > 0) {
                group.setAttribute("name", grpName);
            }
        } else
            throw new XmlSchemaSerializerException("Group must have " +
                    "name or ref");

        /* annotations are supposed to be written first!!!!! */
        if (groupObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    groupObj.annotation, schema);
            group.appendChild(annotation);
        }

        if (groupObj.particle instanceof XmlSchemaSequence) {
            Element sequence = serializeSequence(doc,
                    (XmlSchemaSequence) groupObj.particle, schema);
            group.appendChild(sequence);
        } else if (groupObj.particle instanceof XmlSchemaChoice) {
            Element choice = serializeChoice(doc,
                    (XmlSchemaChoice) groupObj.particle, schema);
            group.appendChild(choice);
        } else if (groupObj.particle instanceof XmlSchemaAll) {
            Element all = serializeAll(doc,
                    (XmlSchemaAll) groupObj.particle, schema);
            group.appendChild(all);
        }

            //process extension
        processExtensibilityComponents(groupObj,group);

        return group;
    }

    /**
     * *********************************************************************
     * Element serializeGroupRef(Document doc, XmlSchemaGroupRef groupRefObj,
     * XmlSchema schema) throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc                   - Document the parent use.
     * groupRefObj           - XmlSchemaGroupRef that will be serialized.
     * schema                - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of group elements ref inside its parent.
     * **********************************************************************
     */
    Element serializeGroupRef(Document doc, XmlSchemaGroupRef groupRefObj,
                              XmlSchema schema) throws XmlSchemaSerializerException {

        Element groupRef = createNewElement(doc, "group",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (groupRefObj.refName != null) {
            String groupRefName = resolveQName(groupRefObj.refName,
                    schema);
            groupRef.setAttribute("ref", groupRefName);
        } else
            throw new XmlSchemaSerializerException("Group must have name or ref");


        serializeMaxMinOccurs(groupRefObj, groupRef);



        if (groupRefObj.particle != null) {
            if (groupRefObj.particle instanceof XmlSchemaChoice)
                serializeChoice(doc, (XmlSchemaChoice) groupRefObj.particle, schema);
            else if (groupRefObj.particle instanceof XmlSchemaSequence)
                serializeSequence(doc,(XmlSchemaSequence) groupRefObj.particle, schema);
            else if (groupRefObj.particle instanceof XmlSchemaAll)
                serializeAll(doc,(XmlSchemaAll) groupRefObj.particle, schema);
            else
                throw new XmlSchemaSerializerException("The content of group "
                        + "ref particle should be"
                        + " sequence, choice or all reference:  "
                        + "www.w3.org/TR/xmlschema-1#element-group-3.7.2");
        }
        if (groupRefObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    groupRefObj.annotation, schema);
            groupRef.appendChild(annotation);
        }

            //process extension
        processExtensibilityComponents(groupRefObj,groupRef);

        return groupRef;
    }

    /**
     * *********************************************************************
     * Element serializeSimpleContent(Document doc,
     * XmlSchemaSimpleContent simpleContentObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc               - Document the parent use.
     * simpleContentObj  - XmlSchemaSimpleContent that will be serialized.
     * schema            - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of complex type simple content.
     * **********************************************************************
     */
    Element serializeSimpleContent(Document doc,
                                   XmlSchemaSimpleContent simpleContentObj, XmlSchema schema)
            throws XmlSchemaSerializerException {
        Element simpleContent = createNewElement(doc, "simpleContent",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        Element content;
        if (simpleContentObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    simpleContentObj.annotation, schema);
            simpleContent.appendChild(annotation);
        }
        if (simpleContentObj.content instanceof
                XmlSchemaSimpleContentRestriction)
            content = serializeSimpleContentRestriction(doc,
                    (XmlSchemaSimpleContentRestriction) simpleContentObj.content,
                    schema);
        else if (simpleContentObj.content instanceof
                XmlSchemaSimpleContentExtension)
            content = serializeSimpleContentExtension(doc,
                    (XmlSchemaSimpleContentExtension) simpleContentObj.content,
                    schema);
        else
            throw new XmlSchemaSerializerException("content of simple content "
                    + "must be restriction or extension");

        simpleContent.appendChild(content);

            //process extension
        processExtensibilityComponents(simpleContentObj,simpleContent);

        return simpleContent;
    }

    /**
     * *********************************************************************
     * Element serializeComplexContent(Document doc,
     * XmlSchemaComplexContent complexContentObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc                - Document the parent use.
     * complexContentObj  - XmlSchemaComplexContent that will be serialized.
     * schema             - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of complex type complex content.
     * **********************************************************************
     */
    Element serializeComplexContent(Document doc,
                                    XmlSchemaComplexContent complexContentObj, XmlSchema schema)
            throws XmlSchemaSerializerException {

        Element complexContent = createNewElement(doc, "complexContent",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);


        if (complexContentObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    complexContentObj.annotation, schema);
            complexContent.appendChild(annotation);
        }

        if (complexContentObj.mixed)
            complexContent.setAttribute("mixed", "true");
        if (complexContentObj.id != null)
            complexContent.setAttribute("id",
                    complexContentObj.id);

        Element content;
        if (complexContentObj.content instanceof
                XmlSchemaComplexContentRestriction)

            content = serializeComplexContentRestriction(doc,
                    (XmlSchemaComplexContentRestriction) complexContentObj.content,
                    schema);
        else if (complexContentObj.content instanceof
                XmlSchemaComplexContentExtension)
            content = serializeComplexContentExtension(doc,
                    (XmlSchemaComplexContentExtension) complexContentObj.content,
                    schema);
        else
            throw new XmlSchemaSerializerException("content of complexContent "
                    + "must be restriction or extension");

        complexContent.appendChild(content);

            //process extension
        processExtensibilityComponents(complexContentObj,complexContent);

        return complexContent;
    }

    /**
     * *********************************************************************
     * Element serializeIdentityConstraint(Document doc,
     * XmlSchemaIdentityConstraint constraintObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc               - Document the parent use.
     * constraintObj     - XmlSchemaIdentityConstraint that will be serialized.
     * schema            - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of key, keyref or unique that part of its parent.
     * **********************************************************************
     */
    Element serializeIdentityConstraint(Document doc,
                                        XmlSchemaIdentityConstraint constraintObj, XmlSchema schema)
            throws XmlSchemaSerializerException {

        Element constraint;

        if (constraintObj instanceof XmlSchemaUnique)
            constraint = createNewElement(doc, "unique",
                    schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);
        else if (constraintObj instanceof XmlSchemaKey)
            constraint = createNewElement(doc, "key",
                    schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);
        else if (constraintObj instanceof XmlSchemaKeyref) {
            constraint = createNewElement(doc, "keyref",
                    schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);
            XmlSchemaKeyref keyref = (XmlSchemaKeyref) constraintObj;
            if (keyref.refer != null) {
                String keyrefStr = resolveQName(keyref.refer, schema);
                constraint.setAttribute(
                        "refer", keyrefStr);
            }
        } else
            throw new XmlSchemaSerializerException("not valid identity "
                    + "constraint");

        if (constraintObj.name != null)
            constraint.setAttribute("name",
                    constraintObj.name);
        if (constraintObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    constraintObj.annotation, schema);
            constraint.appendChild(annotation);
        }

        if (constraintObj.selector != null) {
            Element selector = serializeSelector(doc,
                    constraintObj.selector, schema);
            constraint.appendChild(selector);
        }
        XmlSchemaObjectCollection fieldColl = constraintObj.fields;
        if (fieldColl != null) {
            int fieldLength = fieldColl.getCount();
            for (int i = 0; i < fieldLength; i++) {
                Element field = serializeField(doc,
                        (XmlSchemaXPath) fieldColl.getItem(i), schema);
                constraint.appendChild(field);
            }
        }

            //process extension
        processExtensibilityComponents(constraintObj,constraint);

        return constraint;
    }

    /**
     * *********************************************************************
     * Element serializeSelector(Document doc, XmlSchemaXPath selectorObj,
     * XmlSchema schema) throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc               - Document the parent use.
     * selectorObj      - XmlSchemaXPath that will be serialized.
     * schema            - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of selector with collection of xpath as its attrib.  The selector
     * itself is the part of identity type.  eg <key><selector xpath="..."
     * **********************************************************************
     */
    Element serializeSelector(Document doc, XmlSchemaXPath selectorObj,
                              XmlSchema schema) throws XmlSchemaSerializerException {

        Element selector = createNewElement(doc, "selector",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (selectorObj.xpath != null)
            selector.setAttribute("xpath",
                    selectorObj.xpath);
        else
            throw new XmlSchemaSerializerException("xpath can't be null");

        if (selectorObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    selectorObj.annotation, schema);
            selector.appendChild(annotation);
        }
            //process extension
        processExtensibilityComponents(selectorObj,selector);
        return selector;
    }

    /**
     * *********************************************************************
     * Element serializeField(Document doc, XmlSchemaXPath fieldObj,
     * XmlSchema schema) throws XmlSchemaSerializerException
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc           - Document the parent use.
     * fieldObj      - XmlSchemaXPath that will be serialized.
     * schema        - Schema Document object of the parent.
     * <p/>
     * Return:
     * field element that part of constraint.
     * **********************************************************************
     */
    Element serializeField(Document doc, XmlSchemaXPath fieldObj,
                           XmlSchema schema) throws XmlSchemaSerializerException {

        Element field = createNewElement(doc, "field", schema.schema_ns_prefix,
                XmlSchema.SCHEMA_NS);

        if (fieldObj.xpath != null)
            field.setAttribute("xpath", fieldObj.xpath);
        else
            throw new XmlSchemaSerializerException("xpath can't be null");

        if (fieldObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    fieldObj.annotation, schema);
            field.appendChild(annotation);
        }

            //process extension
        processExtensibilityComponents(fieldObj,field);

        return field;
    }

    /**
     * *********************************************************************
     * Element serializeAnnotation(Document doc, XmlSchemaAnnotation
     * annotationObj, XmlSchema schema)
     * <p/>
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc               - Document the parent use.
     * annotationObj      - XmlSchemaAnnotation that will be serialized.
     * schema            - Schema Document object of the parent.
     * <p/>
     * Return:
     * annotation element that part of any type. will contain document and
     * appinfo for child.
     * **********************************************************************
     */
    Element serializeAnnotation(Document doc, XmlSchemaAnnotation annotationObj,
                                XmlSchema schema) {

        Element annotation = createNewElement(doc, "annotation",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        XmlSchemaObjectCollection contents = annotationObj.items;
        int contentLength = contents.getCount();

        for (int i = 0; i < contentLength; i++) {
            XmlSchemaObject obj = contents.getItem(i);

            if (obj instanceof XmlSchemaAppInfo) {
                XmlSchemaAppInfo appinfo = (XmlSchemaAppInfo) obj;
                Element appInfoEl = serializeAppInfo(doc, appinfo, schema);
                annotation.appendChild(appInfoEl);
            } else if (obj instanceof XmlSchemaDocumentation) {
                XmlSchemaDocumentation documentation =
                        (XmlSchemaDocumentation) obj;

                Element documentationEl = serializeDocumentation(doc,
                        documentation, schema);


                annotation.appendChild(documentationEl);
            }
        }

            //process extension
        processExtensibilityComponents(annotationObj,annotation);

        return annotation;
    }

    /**
     * *********************************************************************
     * Element serializeAppInfo(Document doc,XmlSchemaAppInfo appInfoObj,
     * XmlSchema schema)
     * <p/>
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc               - Document the parent use.
     * appInfoObj        - XmlSchemaAppInfo that will be serialized.
     * schema            - Schema Document object of the parent.
     * <p/>
     * Return:
     * App info element that is part of the annotation.
     * **********************************************************************
     */
    Element serializeAppInfo(Document doc, XmlSchemaAppInfo appInfoObj,
                             XmlSchema schema) {

        Element appInfoEl = createNewElement(doc, "appinfo",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);
        if (appInfoObj.source != null)
            appInfoEl.setAttribute("source",
                    appInfoObj.source);

        if (appInfoObj.markup != null) {
            int markupLength = appInfoObj.markup.getLength();
            for (int j = 0; j < markupLength; j++) {
                Node n = (Node) appInfoObj.markup.item(j);
               appInfoEl.appendChild(doc.importNode(n,true));
            }
        }

            //process extension
        processExtensibilityComponents(appInfoObj,appInfoEl);

        return appInfoEl;
    }

    /**
     * *********************************************************************
     * Element serializeDocumentation(Document doc,XmlSchemaDocumentation
     * documentationObj, XmlSchema schema){
     * <p/>
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc               - Document the parent use.
     * documentationObj        - XmlSchemaAppInfo that will be serialized.
     * schema            - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element representation of documentation that is part of annotation.
     * **********************************************************************
     */
    Element serializeDocumentation(Document doc, XmlSchemaDocumentation
            documentationObj, XmlSchema schema) {


        Element documentationEl = createNewElement(doc, "documentation",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);
        if (documentationObj.source != null)
            documentationEl.setAttribute("source",
                    documentationObj.source);
        if (documentationObj.language != null)
            documentationEl.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:lang",
                    documentationObj.language);

        if (documentationObj.markup != null) {
            int markupLength = documentationObj.markup.getLength();
            for (int j = 0; j < markupLength; j++) {
                Node n = (Node) documentationObj.markup.item(j);

                switch (n.getNodeType()) {
                    case Node.ELEMENT_NODE:
                        appendElement(doc, documentationEl, n, schema);
                        break;
                    case Node.TEXT_NODE:
                        Text t = doc.createTextNode(n.getNodeValue());
                        documentationEl.appendChild(t);
                        break;
                    case Node.CDATA_SECTION_NODE:
                    	CDATASection s = doc.createCDATASection(n.getNodeValue());
                    	documentationEl.appendChild(s);
                    	break;
                    case Node.COMMENT_NODE:
                    	Comment c = doc.createComment(n.getNodeValue());
                    	documentationEl.appendChild(c);
                    	break;
                    default:
                        break;
                }
            }
        }
            //process extension
        processExtensibilityComponents(documentationObj,documentationEl);

        return documentationEl;
    }

    /**
     * *********************************************************************
     * Element serializeSimpleContentRestriction(Document doc,
     * XmlSchemaSimpleContentRestriction restrictionObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc               - Document the parent use.
     * restrictionObj    - XmlSchemaSimpleContentRestriction that will be
     * serialized.
     * schema            - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of simple content restriction.
     * **********************************************************************
     */
    Element serializeSimpleContentRestriction(Document doc,
                                              XmlSchemaSimpleContentRestriction restrictionObj, XmlSchema schema)
            throws XmlSchemaSerializerException {

        Element restriction = createNewElement(doc, "restriction",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (restrictionObj.baseTypeName != null) {
            String baseTypeName =
                    resolveQName(restrictionObj.baseTypeName, schema);

            restriction.setAttribute("base", baseTypeName);

        }
        if (restrictionObj.id != null)
            restriction.setAttribute("id", restrictionObj.id);

        if (restrictionObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    restrictionObj.annotation, schema);
            restriction.appendChild(annotation);
        }
        XmlSchemaObjectCollection facets = restrictionObj.facets;
        int facetLength = facets.getCount();
        for (int i = 0; i < facetLength; i++) {
            Element facet = serializeFacet(doc,
                    (XmlSchemaFacet) facets.getItem(i), schema);
            restriction.appendChild(facet);
        }
        int attrCollLength = restrictionObj.attributes.getCount();
        for (int i = 0; i < attrCollLength; i++) {
            XmlSchemaObject obj = restrictionObj.attributes.getItem(i);

            if (obj instanceof XmlSchemaAttribute) {
                Element attribute = serializeAttribute(doc,
                        (XmlSchemaAttribute) obj, schema);
                restriction.appendChild(attribute);
            } else if (obj instanceof XmlSchemaAttributeGroupRef) {
                Element attributeGroup = serializeAttributeGroupRef(doc,
                        (XmlSchemaAttributeGroupRef) obj, schema);
                restriction.appendChild(attributeGroup);
            }
        }
        if (restrictionObj.baseType != null) {
            Element inlineSimpleType = serializeSimpleType(doc,
                    restrictionObj.baseType, schema);
            restriction.appendChild(inlineSimpleType);
        }
        if (restrictionObj.anyAttribute != null) {
            Element anyAttribute = serializeAnyAttribute(doc,
                    restrictionObj.anyAttribute, schema);
            restriction.appendChild(anyAttribute);
        }


            //process extension
        processExtensibilityComponents(restrictionObj,restriction);

        return restriction;
    }

    /**
     * *********************************************************************
     * Element serializeSimpleContentExtension(Document doc,
     * XmlSchemaSimpleContentExtension extensionObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc                 - Document the parent use.
     * extensionObj        - XmlSchemaSimpleContentExtension
     * that will be serialized.
     * schema              - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of simple content extension.
     * **********************************************************************
     */
    Element serializeSimpleContentExtension(Document doc,
                                            XmlSchemaSimpleContentExtension extensionObj, XmlSchema schema)
            throws XmlSchemaSerializerException {

        Element extension = createNewElement(doc, "extension",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (extensionObj.baseTypeName != null) {
            String baseTypeName =
                    resolveQName(extensionObj.baseTypeName, schema);

            extension.setAttribute("base", baseTypeName);
        }

        if (extensionObj.id != null)
            extension.setAttribute("id", extensionObj.id);

        if (extensionObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    extensionObj.annotation, schema);
            extension.appendChild(annotation);
        }

        XmlSchemaObjectCollection attributes = extensionObj.attributes;
        int attributeLength = attributes.getCount();
        for (int i = 0; i < attributeLength; i++) {
            XmlSchemaObject obj = attributes.getItem(i);

            if (obj instanceof XmlSchemaAttribute) {
                Element attribute = serializeAttribute(doc,
                        (XmlSchemaAttribute) obj, schema);
                extension.appendChild(attribute);
            } else if (obj instanceof XmlSchemaAttributeGroupRef) {
                Element attributeGroupRef = serializeAttributeGroupRef(doc,
                        (XmlSchemaAttributeGroupRef) obj, schema);
                extension.appendChild(attributeGroupRef);
            }
        }

        /*
         * anyAttribute must come *after* any other attributes
         */
        if (extensionObj.anyAttribute != null) {
            Element anyAttribute = serializeAnyAttribute(doc,
                    extensionObj.anyAttribute, schema);
            extension.appendChild(anyAttribute);
        }

            //process extension
        processExtensibilityComponents(extensionObj,extension);

        return extension;
    }

    /**
     * *********************************************************************
     * Element serializeComplexContentRestriction(Document doc,
     * XmlSchemaComplexContentRestriction restrictionObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc                 - Document the parent use.
     * restrictionObj        - XmlSchemaSimpleContentRestriction
     * that will be serialized.
     * schema              - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of simple content restriction.
     * **********************************************************************
     */
    Element serializeComplexContentRestriction(Document doc,
                                               XmlSchemaComplexContentRestriction restrictionObj, XmlSchema schema)
            throws XmlSchemaSerializerException {

        Element restriction = createNewElement(doc, "restriction",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (restrictionObj.baseTypeName != null) {
            String baseTypeName = resolveQName(restrictionObj.baseTypeName, schema);
            restriction.setAttribute(
                    "base", baseTypeName);
        }

        if (restrictionObj.id != null)
            restriction.setAttribute("id",
                    restrictionObj.id);

        if (restrictionObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    restrictionObj.annotation, schema);
            restriction.appendChild(annotation);
        }

        if (restrictionObj.particle instanceof XmlSchemaSequence) {
            Element sequenceParticle = serializeSequence(doc,
                    (XmlSchemaSequence) restrictionObj.particle, schema);
            restriction.appendChild(sequenceParticle);
        } else if (restrictionObj.particle instanceof XmlSchemaChoice) {
            Element choiceParticle = serializeChoice(doc,
                    (XmlSchemaChoice) restrictionObj.particle, schema);
            restriction.appendChild(choiceParticle);
        } else if (restrictionObj.particle instanceof XmlSchemaAll) {
            Element allParticle = serializeAll(doc,
                    (XmlSchemaAll) restrictionObj.particle, schema);
            restriction.appendChild(allParticle);
        } else if (restrictionObj.particle instanceof XmlSchemaGroupRef) {
            Element groupRefParticle = serializeGroupRef(doc,
                    (XmlSchemaGroupRef) restrictionObj.particle, schema);
            restriction.appendChild(groupRefParticle);
        }

        int attributesLength = restrictionObj.attributes.getCount();
        for (int i = 0; i < attributesLength; i++) {
            XmlSchemaObject obj = restrictionObj.attributes.getItem(i);

            if (obj instanceof XmlSchemaAttribute) {
                Element attr = serializeAttribute(doc,
                        (XmlSchemaAttribute) obj, schema);
                restriction.appendChild(attr);
            } else if (obj instanceof XmlSchemaAttributeGroupRef) {
                Element attrGroup = serializeAttributeGroupRef(doc,
                        (XmlSchemaAttributeGroupRef) obj, schema);
                restriction.appendChild(attrGroup);
            }
        }

        if (restrictionObj.anyAttribute != null) {
            Element anyAttribute = serializeAnyAttribute(doc,
                    restrictionObj.anyAttribute, schema);
            restriction.appendChild(anyAttribute);
        }

            //process extension
        processExtensibilityComponents(restrictionObj,restriction);

        return restriction;
    }

    /**
     * *********************************************************************
     * Element serializeComplexContentExtension(Document doc,
     * XmlSchemaComplexContentExtension extensionObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc                 - Document the parent use.
     * extensionObj        - XmlSchemaComplexContentRestriction
     * that will be serialized.
     * schema              - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of complex content extension.
     * **********************************************************************
     */
    Element serializeComplexContentExtension(Document doc,
                                             XmlSchemaComplexContentExtension extensionObj, XmlSchema schema)
            throws XmlSchemaSerializerException {

        Element extension = createNewElement(doc, "extension",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);
        if (extensionObj.baseTypeName != null) {
            String baseType = resolveQName(extensionObj.baseTypeName,
                    schema);
            extension.setAttribute("base", baseType);
        }
        if (extensionObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    extensionObj.annotation, schema);
            extension.appendChild(annotation);
        }


        if (extensionObj.particle instanceof XmlSchemaSequence) {
            Element sequenceParticle = serializeSequence(doc,
                    (XmlSchemaSequence) extensionObj.particle, schema);
            extension.appendChild(sequenceParticle);
        } else if (extensionObj.particle instanceof XmlSchemaChoice) {
            Element choiceParticle = serializeChoice(doc,
                    (XmlSchemaChoice) extensionObj.particle, schema);
            extension.appendChild(choiceParticle);
        } else if (extensionObj.particle instanceof XmlSchemaAll) {
            Element allParticle = serializeAll(doc,
                    (XmlSchemaAll) extensionObj.particle, schema);
            extension.appendChild(allParticle);
        } else if (extensionObj.particle instanceof XmlSchemaGroupRef) {
            Element groupRefParticle = serializeGroupRef(doc,
                    (XmlSchemaGroupRef) extensionObj.particle, schema);
            extension.appendChild(groupRefParticle);
        }

        int attributesLength = extensionObj.attributes.getCount();
        for (int i = 0; i < attributesLength; i++) {
            XmlSchemaObject obj = extensionObj.attributes.getItem(i);

            if (obj instanceof XmlSchemaAttribute) {
                Element attr = serializeAttribute(doc,
                        (XmlSchemaAttribute) obj, schema);
                extension.appendChild(attr);
            } else if (obj instanceof XmlSchemaAttributeGroupRef) {
                Element attrGroup = serializeAttributeGroupRef(doc,
                        (XmlSchemaAttributeGroupRef) obj, schema);
                extension.appendChild(attrGroup);
            }
        }

        if (extensionObj.anyAttribute != null) {
            Element anyAttribute = serializeAnyAttribute(doc,
                    extensionObj.anyAttribute, schema);
            extension.appendChild(anyAttribute);
        }

            //process extension
        processExtensibilityComponents(extensionObj,extension);

        return extension;
    }

    /**
     * *********************************************************************
     * Element serializeAnyAttribute(Document doc,
     * XmlSchemaAnyAttribute anyAttributeObj, XmlSchema schema)
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc                 - Document the parent use.
     * anyAttributeObj     - XmlSchemaAnyAttribute
     * that will be serialized.
     * schema              - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of any attribute element.
     * **********************************************************************
     */
    Element serializeAnyAttribute(Document doc,
                                  XmlSchemaAnyAttribute anyAttributeObj, XmlSchema schema) {

        Element anyAttribute = createNewElement(doc, "anyAttribute",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);


        if (anyAttributeObj.namespace != null)
            anyAttribute.setAttribute("namespace",
                    anyAttributeObj.namespace);

        if (anyAttributeObj.id != null)
            anyAttribute.setAttribute("id",
                    anyAttributeObj.id);

        if (anyAttributeObj.processContent != null) {
            String processContent = anyAttributeObj.processContent.getValue();
            if(!Constants.BlockConstants.NONE.equals(processContent)){
                processContent = convertString(processContent);
                anyAttribute.setAttribute("processContents",
                    processContent);
            }
        }
        if (anyAttributeObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    anyAttributeObj.annotation, schema);
            anyAttribute.appendChild(annotation);
        }

            //process extension
        processExtensibilityComponents(anyAttributeObj,anyAttribute);

        return anyAttribute;
    }

    /**
     * *********************************************************************
     * Element serializeAttributeGroupRef(Document doc,
     * XmlSchemaAttributeGroupRef attributeGroupObj, XmlSchema schema)
     * throws XmlSchemaSerializerException
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc                 - Document the parent use.
     * attributeGroupObj     - XmlSchemaAttributeGroupRef
     * that will be serialized.
     * schema              - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of attribute group ref that part of type.
     * **********************************************************************
     */
    Element serializeAttributeGroupRef(Document doc,
                                       XmlSchemaAttributeGroupRef attributeGroupObj, XmlSchema schema)
            throws XmlSchemaSerializerException {

        Element attributeGroupRef = createNewElement(doc, "attributeGroup",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (attributeGroupObj.refName != null) {
            String refName = resolveQName(attributeGroupObj.refName,
                    schema);
            attributeGroupRef.setAttribute("ref", refName);
        } else
            throw new XmlSchemaSerializerException("Attribute group must have "
                    + "ref name set");

        if (attributeGroupObj.id != null)
            attributeGroupRef.setAttribute("id",
                    attributeGroupObj.id);

        if (attributeGroupObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    attributeGroupObj.annotation, schema);
            attributeGroupRef.appendChild(annotation);
        }

            //process extension
        processExtensibilityComponents(attributeGroupObj,attributeGroupRef);

        return attributeGroupRef;
    }

    /**
     * *********************************************************************
     * Element serializeAttributeGroup(Document doc,
     * XmlSchemaAttributeGroup attributeGroupObj, XmlSchema schema)
     * throws XmlSchemaSerializerException{
     * <p/>
     * Each member of complex type will be appended and pass the element
     * created.  Complex type processed according to w3c Recommendation
     * May 2 2001.
     * <p/>
     * Parameter:
     * doc                 - Document the parent use.
     * attributeGroupObj     - XmlSchemaAttributeGroup
     * that will be serialized.
     * schema              - Schema Document object of the parent.
     * <p/>
     * Return:
     * Element of attribute group.
     * **********************************************************************
     */
    Element serializeAttributeGroup(Document doc,
                                    XmlSchemaAttributeGroup attributeGroupObj, XmlSchema schema)
            throws XmlSchemaSerializerException {

        Element attributeGroup = createNewElement(doc, "attributeGroup",
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);

        if (attributeGroupObj.name != null) {
            String attGroupName = attributeGroupObj.name.getLocalPart();
            attributeGroup.setAttribute("name",
                    attGroupName);
        }else
            throw new XmlSchemaSerializerException("Attribute group must"
                    + "have name");
        if (attributeGroupObj.id != null)
            attributeGroup.setAttribute("id",
                    attributeGroupObj.id);

        if (attributeGroupObj.annotation != null) {
            Element annotation = serializeAnnotation(doc,
                    attributeGroupObj.annotation, schema);
            attributeGroup.appendChild(annotation);
        }
        int attributesLength = attributeGroupObj.attributes.getCount();
        for (int i = 0; i < attributesLength; i++) {
            XmlSchemaObject obj = attributeGroupObj.attributes.getItem(i);

            if (obj instanceof XmlSchemaAttribute) {
                Element attr = serializeAttribute(doc, (XmlSchemaAttribute) obj,
                        schema);
                attributeGroup.appendChild(attr);
            } else if (obj instanceof XmlSchemaAttributeGroupRef) {
                Element attrGroup = serializeAttributeGroupRef(doc,
                        (XmlSchemaAttributeGroupRef) obj, schema);
                attributeGroup.appendChild(attrGroup);
            }
        }

        if (attributeGroupObj.anyAttribute != null) {
            Element anyAttribute = serializeAnyAttribute(doc,
                    attributeGroupObj.anyAttribute, schema);
            attributeGroup.appendChild(anyAttribute);
        }

            //process extension
        processExtensibilityComponents(attributeGroupObj,attributeGroup);

        return attributeGroup;
    }

    //recursively add any attribute, text and children append all
    //found children base on parent as its root.
    private void appendElement(Document doc, Element parent,
                               Node children, XmlSchema schema) {
        Element elTmp = (Element) children;
        Element el = createNewElement(doc, elTmp.getLocalName(),
                schema.schema_ns_prefix, XmlSchema.SCHEMA_NS);
        NamedNodeMap attributes = el.getAttributes();
        //check if child node has attribute
        //create new element and append it if found
        int attributeLength = attributes.getLength();
        for (int i = 0; i < attributeLength; i++) {
            Node n = attributes.item(i);
            //assuming attributes got to throw exception if not later
            el.setAttribute(n.getNodeName(),
                    n.getNodeValue());
        }

        //check any descendant of this node
        //if there then append its child
        NodeList decendants = el.getChildNodes();
        int decendantLength = decendants.getLength();
        for (int i = 0; i < decendantLength; i++) {
            Node n = decendants.item(i);
            short nodeType = n.getNodeType();
            if (nodeType == Node.TEXT_NODE) {
                String nValue = n.getNodeValue();
                Text t = doc.createTextNode(nValue);
                el.appendChild(t);
	    } else if (nodeType == Node.CDATA_SECTION_NODE) {
		String nValue = n.getNodeValue();
		CDATASection s = doc.createCDATASection(nValue);
		el.appendChild(s);
            } else if (nodeType == Node.ELEMENT_NODE) {
                appendElement(doc, el, n, schema);
            }
        }
    }

    //break string with prefix into two parts part[0]:prefix , part[1]:namespace
    private static String[] getParts(String name) {
        String[] parts = new String[2];

        int index = name.indexOf(":");
        if (index > -1) {
            parts[0] = name.substring(0, index);
            parts[1] = name.substring(index + 1);
        } else {
            parts[0] = "";
            parts[1] = name;
        }
        return parts;
    }

    //Convert given string to lower case or w3c standard
    private String convertString(String convert) {
        String input = convert.trim();
        if (input.equals(Constants.BlockConstants.ALL)) {
            return "#all";
        } else
            return input.toLowerCase();
    }

    //Create new element with given local name and namespaces check whether
    //the prefix is there or not.
    private Element createNewElement(Document docs, String localName,
                                     String prefix, String namespace) {
        String elementName = ((prefix.length() > 0) ? prefix += ":" : "")
                + localName;
        return docs.createElementNS(namespace, elementName);
    }

    /**
     * will search whether the prefix is available in global hash table, if it
     * is there than append the prefix to the element name.  If not then it will
     * create new prefix corresponding to that namespace and store that in
     * hash table.  Finally add the new prefix and namespace to <schema>
     * element
     * @param names
     * @param schemaObj
     * @return resolved QName of the string
     */

    private String resolveQName(QName names,
                                XmlSchema schemaObj) {

        String namespace = names.getNamespaceURI();
        String type[] = getParts(names.getLocalPart());
        String typeName = (type.length > 1) ? type[1] : type[0];
        String prefixStr;

        // If the namespace is "" then the prefix is also ""
        Object prefix = ("".equals(namespace)) ? "" : schema_ns.get(namespace);

        if (prefix == null) {
            if (Constants.XMLNS_URI.equals(namespace)) {
                prefix = Constants.XMLNS_PREFIX;
            } else {
                int magicNumber = 0;
                Collection prefixes = schema_ns.values();
                while(prefixes.contains("ns" + magicNumber)){
                    magicNumber++;
                }
                prefix = "ns" + magicNumber;
                schema_ns.put(namespace, prefix);

                //setting xmlns in schema
                schemaElement.setAttributeNS(XMLNS_NAMESPACE_URI,
                        "xmlns:" + prefix.toString(), namespace);
            }
        }

        prefixStr = prefix.toString();
        prefixStr = (prefixStr.trim().length() > 0) ? prefixStr + ":" : "";

        return prefixStr + typeName;
    }

    //for each collection if it is an attribute serialize attribute and
    //append that child to container element.
    void setupAttr(Document doc, XmlSchemaObjectCollection collectionObj,
                   XmlSchema schema, Element container) throws XmlSchemaSerializerException {
        int collectionLength = collectionObj.getCount();
        for (int i = 0; i < collectionLength; i++) {
            XmlSchemaObject obj = collectionObj.getItem(i);
            if (obj instanceof XmlSchemaAttribute) {
                XmlSchemaAttribute attr = (XmlSchemaAttribute) obj;
                Element attrEl = serializeAttribute(doc, attr, schema);
                container.appendChild(attrEl);
            } else if (obj instanceof XmlSchemaAttributeGroupRef) {
                XmlSchemaAttributeGroupRef attr = (XmlSchemaAttributeGroupRef) obj;
                Element attrEl = serializeAttributeGroupRef(doc, attr, schema);
                container.appendChild(attrEl);
            }
        }
    }

    /**
     * Exception class used for serialization problems.
     */
    public static class XmlSchemaSerializerException extends Exception {

        /**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Standard constructor with a message.
		 * @param msg the message.
		 */
		public XmlSchemaSerializerException(String msg) {
            super(msg);
        }
    }


    /**
     * A generic method to process the extra attributes and the extra
     * elements present within the schema.
     * What are considered extensions are child elements with non schema namespace
     * and child attributes with any namespace.
     * @param schemaObject
     * @param parentElement
     */
    private void processExtensibilityComponents(XmlSchemaObject schemaObject, Element parentElement) {

        if (extReg!=null){
            Map metaInfoMap = schemaObject.getMetaInfoMap();
            if (metaInfoMap!=null && !metaInfoMap.isEmpty()) {
                //get the extra objects and call the respective deserializers
                Iterator keysIt = metaInfoMap.keySet().iterator();
                while (keysIt.hasNext()) {
                    Object key =  keysIt.next();
                    extReg.serializeExtension(schemaObject,metaInfoMap.get(key).getClass(),parentElement);

                }

            }

        }

    }

}
