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
package tests.customext.elt;

import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.extensions.ExtensionDeserializer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;

/**
 * Custom element deserializer
 */
public class CustomElementDeserializer implements ExtensionDeserializer {
    /**
     * deserialize the given element
     *
     * @param schemaObject - Parent schema element
     * @param name         - the QName of the element/attribute to be deserialized.
     *                     in the case where a deserializer is used to handle multiple elements/attributes
     *                     this may be useful to determine the correct deserialization
     * @param domNode      - the raw DOM Node read from the source. This will be the
     *                     extension element itself if for an element or the extension attribute object if
     *                     it is an attribute
     */
    public void deserialize(XmlSchemaObject schemaObject, QName name, Node domNode) {
         if (CustomElement.CUSTOM_ELT_QNAME.equals(name)){
             Element elt = (Element)domNode;

             CustomElement customElement = new CustomElement();
             customElement.setPrefix(elt.getAttribute("prefix"));
             customElement.setSuffix(elt.getAttribute("suffix"));

             //put this in the schema object meta info map
             schemaObject.addMetaInfo(CustomElement.CUSTOM_ELT_QNAME,customElement);
         }
    }
}
