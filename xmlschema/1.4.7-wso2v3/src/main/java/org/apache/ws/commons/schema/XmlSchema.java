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

import org.apache.ws.commons.schema.XmlSchemaSerializer.XmlSchemaSerializerException;
import org.apache.ws.commons.schema.constants.Constants;
import org.apache.ws.commons.schema.utils.NamespaceContextOwner;
import org.apache.ws.commons.schema.utils.NamespacePrefixList;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

// Ancient history, year unknown:-)
//Oct 15th - momo - initial impl
//Oct 17th - vidyanand - add SimpleType + element
//Oct 18th - momo - add ComplexType
//Oct 19th - vidyanand - handle external
//Dec 6th - Vidyanand - changed RuntimeExceptions thrown to XmlSchemaExceptions
//Jan 15th - Vidyanand - made changes to SchemaBuilder.handleElement to look for an element ref.
//Feb 20th - Joni - Change the getXmlSchemaFromLocation schema
//         variable to name s.
//Feb 21th - Joni - Port to XMLDomUtil and Tranformation.
/**
 * Contains the definition of a schema. All XML Schema definition language (XSD)
 * elements are children of the schema element. Represents the World Wide Web
 * Consortium (W3C) schema element
 */
public class XmlSchema extends XmlSchemaAnnotated implements NamespaceContextOwner {
    private static final String UTF_8_ENCODING = "UTF-8";
	static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
    XmlSchemaForm attributeFormDefault, elementFormDefault;

    XmlSchemaObjectTable attributeGroups,
            attributes, elements, groups,
            notations, schemaTypes;
    XmlSchemaDerivationMethod blockDefault, finalDefault;
    XmlSchemaObjectCollection includes, items;
    boolean isCompiled;
    String syntacticalTargetNamespace, logicalTargetNamespace, version;
    String schema_ns_prefix = "";
    XmlSchemaCollection parent;

    private NamespacePrefixList namespaceContext;
    //keep the encoding of the input
    private String inputEncoding;

    public void setInputEncoding(String encoding){
        this.inputEncoding = encoding;
    }
    /**
     * Creates new XmlSchema
     * Create a new XmlSchema. The schema is <i>not</i> added to the parent collection,
     * since it has no target namespace when created through this constructor.
     * Call {@link XmlSchema#XmlSchema(String, XmlSchemaCollection)} instead.
      *
      * @param parent the parent XmlSchemaCollection
     * @deprecated
      */
     public XmlSchema(XmlSchemaCollection parent) {
    	this(null, null, parent);
    }

    /**
     * Create a schema that is not a member of a collection.
     */
    public XmlSchema() {
    	this(null, null, null);
    }

    /**
     * Create a new schema and record it as a member of a schema collection.
     * @param namespace the target namespace.
     * @param systemId the system ID for the schema.
     * @param parent the parent collection.
     */
    public XmlSchema(String namespace, String systemId, XmlSchemaCollection parent) {
        this.parent = parent;
        attributeFormDefault = new XmlSchemaForm(XmlSchemaForm.UNQUALIFIED);
        elementFormDefault = new XmlSchemaForm(XmlSchemaForm.UNQUALIFIED);
        blockDefault = new XmlSchemaDerivationMethod(Constants.BlockConstants.NONE);
        finalDefault = new XmlSchemaDerivationMethod(Constants.BlockConstants.NONE);
        items = new XmlSchemaObjectCollection();
        includes = new XmlSchemaObjectCollection();
        elements = new XmlSchemaObjectTable();
        attributeGroups = new XmlSchemaObjectTable();
        attributes = new XmlSchemaObjectTable();
        groups = new XmlSchemaObjectTable();
        notations = new XmlSchemaObjectTable();
        schemaTypes = new XmlSchemaObjectTable();

        syntacticalTargetNamespace = logicalTargetNamespace = namespace;
        if (logicalTargetNamespace == null) {
             logicalTargetNamespace = "";
         }
        if(parent != null) {
        	XmlSchemaCollection.SchemaKey schemaKey =
        		new XmlSchemaCollection.SchemaKey(this.logicalTargetNamespace, systemId);
        	if (parent.containsSchema(schemaKey)) {
        		throw new XmlSchemaException("Schema name conflict in collection");
        	} else {
        		parent.addSchema(schemaKey, this);
        	}
        }
    }

    public XmlSchema(String namespace, XmlSchemaCollection parent) {
        this(namespace, namespace, parent);
       
    }

    public XmlSchemaForm getAttributeFormDefault() {
        return attributeFormDefault;
    }

    public void setAttributeFormDefault(XmlSchemaForm value) {
        attributeFormDefault = value;
    }

    public XmlSchemaObjectTable getAttributeGroups() {
        return attributeGroups;
    }

    public XmlSchemaObjectTable getAttributes() {
        return attributes;
    }

    public XmlSchemaDerivationMethod getBlockDefault() {
        return blockDefault;
    }

    public void setBlockDefault(XmlSchemaDerivationMethod blockDefault) {
        this.blockDefault = blockDefault;
    }

    public XmlSchemaForm getElementFormDefault() {
        return elementFormDefault;
    }

    public void setElementFormDefault(XmlSchemaForm elementFormDefault) {
        this.elementFormDefault = elementFormDefault;
    }

    public XmlSchemaObjectTable getElements() {
        return elements;
    }

    
    protected XmlSchemaElement getElementByName(QName name, boolean deep,
			Stack schemaStack) {
		if (schemaStack != null && schemaStack.contains(this)) {
			// recursive schema - just return null
			return null;
		} else {
			XmlSchemaElement element = (XmlSchemaElement) elements
					.getItem(name);
			if (deep) {
				if (element == null) {
					// search the imports
					for (Iterator includedItems = includes.getIterator(); includedItems
							.hasNext();) {
						
						XmlSchema schema = getSchema(includedItems.next());
						
						if (schema != null) {
						// create an empty stack - push the current parent in
						// and
						// use the protected method to process the schema
						if (schemaStack == null) {
							schemaStack = new Stack();
						}
						schemaStack.push(this);
						element = schema.getElementByName(name, deep,
								schemaStack);
						if (element != null) {
							return element;
						}
						}
					}
				} else {
					return element;
				}
			}

			return element;
		}
	}
    
    protected XmlSchemaAttribute getAttributeByName(QName name, boolean deep, Stack schemaStack) {
                                        if (schemaStack != null && schemaStack.contains(this)) {
                                                // recursive schema - just return null
                                                return null;
                                        } else {
                                                XmlSchemaAttribute attribute = (XmlSchemaAttribute) attributes
                                                                .getItem(name);
                                                if (deep) {
                                                        if (attribute == null) {
                                                                // search the imports
                                                                for (Iterator includedItems = includes.getIterator(); includedItems
                                                                                .hasNext();) {
                                                                        
                                                                        XmlSchema schema = getSchema(includedItems.next());
                                                                        
                                                                        if (schema != null) {
                                                                        // create an empty stack - push the current parent in
                                                                        // and
                                                                        // use the protected method to process the schema
                                                                        if (schemaStack == null) {
                                                                                schemaStack = new Stack();
                                                                        }
                                                                        schemaStack.push(this);
                                                                        attribute = schema.getAttributeByName(name, deep,
                                                                                        schemaStack);
                                                                        if (attribute != null) {
                                                                                return attribute;
                                                                        }
                                                                        }
                                                                }
                                                        } else {
                                                                return attribute;
                                                        }
                                                }

                                                return attribute;
                                        }
                                }

	/**
	 * get an element by the name in the local schema
	 * 
	 * @param name
	 * @return the element.
	 */
	public XmlSchemaElement getElementByName(String name) {
        QName nameToSearchFor = new QName(this.getTargetNamespace(),name);
        return this.getElementByName(nameToSearchFor, false, null);
	}

	/**
	 * Look for a element by its qname. Searches through all the schemas
	 * @param name
	 * @return the element.
	 */
	public XmlSchemaElement getElementByName(QName name) {
		return this.getElementByName(name, true, null);
	}
	
	/**
	 * Look for a global attribute by its QName. Searches through all schemas.
	 * @param name
	 * @return the attribute.
	 */
	public XmlSchemaAttribute getAttributeByName(QName name) {
	    return this.getAttributeByName(name, true, null);
	}

	/**
	 * Protected method that allows safe (non-recursive schema loading). It looks for a type
	 * with constraints.
         * 
	 * @param name
	 * @param deep
	 * @param schemaStack
	 * @return the type.
	 */
	protected XmlSchemaType getTypeByName(QName name, boolean deep,
			Stack schemaStack) {
		if (schemaStack != null && schemaStack.contains(this)) {
			// recursive schema - just return null
			return null;
		} else {
			XmlSchemaType type = (XmlSchemaType) schemaTypes.getItem(name);

			if (deep) {
				if (type == null) {
					// search the imports
					for (Iterator includedItems = includes.getIterator(); includedItems
							.hasNext();) {

						XmlSchema schema = getSchema(includedItems.next());
						
						if (schema != null) {
							// create an empty stack - push the current parent
							// use the protected method to process the schema
							if (schemaStack == null) {
								schemaStack = new Stack();
							}
							schemaStack.push(this);
							type = schema
									.getTypeByName(name, deep, schemaStack);
							if (type != null) {
								return type;
							}
						}
					}
				} else {
					return type;
				}
			}

			return type;
		}
	}

	/**
	 * Search this schema and all the imported/included ones
         * for the given Qname
	 * @param name
	 * @return the type.
	 */
	public XmlSchemaType getTypeByName(QName name) {
		return getTypeByName(name, true, null);
	}

	/**
	 * Search this schema for a type by qname.
	 * @param name
	 * @return the type.
	 */
	public XmlSchemaType getTypeByName(String name) {
        QName nameToSearchFor = new QName(this.getTargetNamespace(),name);
        return getTypeByName(nameToSearchFor, false, null);
	}

	/**
	 * Get a schema from an import
	 * 
	 * @param includeOrImport
	 * @return return the schema object.
	 */
	private XmlSchema getSchema(Object includeOrImport) {
		XmlSchema schema;
		if (includeOrImport instanceof XmlSchemaImport) {
			schema = ((XmlSchemaImport) includeOrImport).getSchema();
		} else if (includeOrImport instanceof XmlSchemaInclude) {
			schema = ((XmlSchemaInclude) includeOrImport).getSchema();
		} else {
			// skip ?
			schema = null;
		}

		return schema;
	}

    

    public XmlSchemaDerivationMethod getFinalDefault() {
        return finalDefault;
    }

    public void setFinalDefault(XmlSchemaDerivationMethod finalDefault) {
        this.finalDefault = finalDefault;
    }

    public XmlSchemaObjectTable getGroups() {
        return groups;
    }

    public XmlSchemaObjectCollection getIncludes() {
        return includes;
    }

    public boolean isCompiled() {
        return isCompiled;
    }

    public XmlSchemaObjectCollection getItems() {
        return items;
    }

    public XmlSchemaObjectTable getNotations() {
        return notations;
    }

    public XmlSchemaObjectTable getSchemaTypes() {
        return schemaTypes;
    }

    public String getTargetNamespace() {
        return syntacticalTargetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        if (!targetNamespace.equals("")) {
            syntacticalTargetNamespace = logicalTargetNamespace = targetNamespace;
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public void compile(ValidationEventHandler eh) {

    }

    /**
     * Serialize the schema into the given output stream
     * @param out - the output stream to write to
     */
    public void write(OutputStream out) {
    try {
        if (this.inputEncoding!= null &&
                !"".equals(this.inputEncoding)){
                write(new OutputStreamWriter(out,this.inputEncoding));
        }else{
        	//As per the XML spec the default is taken to be UTF 8
            write(new OutputStreamWriter(out,UTF_8_ENCODING));
        }
    } catch (UnsupportedEncodingException e) {
        //log the error and just write it without the encoding
        write(new OutputStreamWriter(out));
    }

    }

    /**
     * Serialize the schema into the given output stream
     * @param out - the output stream to write to
     * @param options -  a map of options
     */
    public void write(OutputStream out, Map options) {
    	try {
	        if (this.inputEncoding!= null &&
	                !"".equals(this.inputEncoding)){
	                write(new OutputStreamWriter(out,this.inputEncoding),options);
	        }else{
	            write(new OutputStreamWriter(out,UTF_8_ENCODING),options);
	        }
    	} catch (UnsupportedEncodingException e) {
            //log the error and just write it without the encoding
            write(new OutputStreamWriter(out));
        }

    }

    /**
     * Serialie the schema to a given writer
     * @param writer - the writer to write this
     */
    public void write(Writer writer,Map options) {
        serialize_internal(this, writer,options);
    }
    /**
     * Serialie the schema to a given writer
     * @param writer - the writer to write this
     */
    public void write(Writer writer) {
        serialize_internal(this, writer,null);
    }

    public Document[] getAllSchemas() {
        try {

            XmlSchemaSerializer xser = new XmlSchemaSerializer();
            xser.setExtReg(this.parent.getExtReg());
            return xser.serializeSchema(this, true);

        } catch (XmlSchemaSerializer.XmlSchemaSerializerException e) {
            throw new XmlSchemaException(e.getMessage());
        }
    }

    /**
     * serialize the schema - this is the method tht does to work
     * @param schema
     * @param out
     * @param options
     */
    private  void serialize_internal(XmlSchema schema, Writer out, Map options) {

        try {
            XmlSchemaSerializer xser = new XmlSchemaSerializer();
            xser.setExtReg(this.parent.getExtReg());
            Document[] serializedSchemas = xser.serializeSchema(schema, false);
            TransformerFactory trFac = TransformerFactory.newInstance();

            try {
                trFac.setAttribute("indent-number", "4");
            } catch (IllegalArgumentException e) {
                //do nothing - we'll just silently let this pass if it
                //was not compatible
            }

            Source source = new DOMSource(serializedSchemas[0]);
            Result result = new StreamResult(out);
            javax.xml.transform.Transformer tr = trFac.newTransformer();

            //use the input encoding if there is one
            if (schema.inputEncoding!= null &&
                    !"".equals(schema.inputEncoding)){
                tr.setOutputProperty(OutputKeys.ENCODING,schema.inputEncoding);
            }

            //let these be configured from outside  if any is present
            //Note that one can enforce the encoding by passing the necessary
            //property in options

            if (options==null){
                options = new HashMap();
                loadDefaultOptions(options);
            }
            Iterator keys = options.keySet().iterator();
            while (keys.hasNext()) {
                Object key = keys.next();
                tr.setOutputProperty((String)key, (String)options.get(key));
            }

            tr.transform(source, result);
            out.flush();
        } catch (TransformerConfigurationException e) {
            throw new XmlSchemaException(e.getMessage());
        } catch (TransformerException e) {
            throw new XmlSchemaException(e.getMessage());
        } catch (XmlSchemaSerializer.XmlSchemaSerializerException e) {
            throw new XmlSchemaException(e.getMessage());
        } catch (IOException e) {
            throw new XmlSchemaException(e.getMessage());
        }
    }

    /**
     * Load the default options
     * @param options  - the map of
     */
    private void loadDefaultOptions(Map options) {
        options.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
        options.put(OutputKeys.INDENT, "yes");
    }

    public void addType(XmlSchemaType type) {
        QName qname = type.getQName();
        if (schemaTypes.contains(qname)) {
            throw new XmlSchemaException(" Schema for namespace '" +
                    syntacticalTargetNamespace + "' already contains type '" +
                    qname.getLocalPart() + "'");
        }
        schemaTypes.add(qname, type);
    }

    public NamespacePrefixList getNamespaceContext() {
        return namespaceContext;
    }

    /**
     * Sets the schema elements namespace context. This may be used for schema
     * serialization, until a better mechanism was found.
     */
    public void setNamespaceContext(NamespacePrefixList namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    /**
     * Override the equals(Object) method with equivalence checking
     * that is specific to this class.
     */
    public boolean equals(Object what) {

        //Note: this method may no longer be required when line number/position are used correctly in XmlSchemaObject.
        //Currently they are simply initialized to zero, but they are used in XmlSchemaObject.equals 
        //which can result in a false positive (e.g. if a WSDL contains 2 inlined schemas).

        if (what == this) {
            return true;
        }

        //If the inherited behaviour determines that the objects are NOT equal, return false. 
        //Otherwise, do some further equivalence checking.

        if(!super.equals(what)) {
            return false;
        }

        if (!(what instanceof XmlSchema)) {
            return false;
        }

        XmlSchema xs = (XmlSchema) what;

        if (this.id != null) {
            if (!this.id.equals(xs.id)) {
                return false;
            }
        } else {
            if (xs.id != null) {
                return false;
            }
        }

        if (this.syntacticalTargetNamespace != null) {
            if (!this.syntacticalTargetNamespace.equals(xs.syntacticalTargetNamespace)) {
                return false;
            }
        } else {
            if (xs.syntacticalTargetNamespace != null) {
                return false;
            }
        }

        //TODO decide if further schema content should be checked for equivalence.

        return true;
    }
    
    /**
     * Retrieve a DOM tree for this one schema, independent of any included or 
     * related schemas.
     * @return The DOM document.
     * @throws XmlSchemaSerializerException
     */
    public Document getSchemaDocument() throws XmlSchemaSerializerException {
        XmlSchemaSerializer xser = new XmlSchemaSerializer();
        xser.setExtReg(this.parent.getExtReg());
        return xser.serializeSchema(this, false)[0];
    }
    
    public String getInputEncoding() {
        return inputEncoding;
    }
    
    public String toString() {
        return super.toString() + "[" + logicalTargetNamespace + "]";
    }
}
