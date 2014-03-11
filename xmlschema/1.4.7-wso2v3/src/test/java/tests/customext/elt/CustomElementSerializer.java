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
import org.apache.ws.commons.schema.extensions.ExtensionSerializer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Custom element serializer
 */
public class CustomElementSerializer implements ExtensionSerializer {
    /**
     * serialize the given element
     *
     * @param schemaObject - Parent schema object.contains the extension
     *                     to be serialized
     * @param classOfType  - The class of type to be serialized
     * @param domNode      - the parent DOM Node that will ultimately be serialized. The XMLSchema
     *                     serialization mechanism is to create a DOM tree first and serialize it
     */
    public void serialize(XmlSchemaObject schemaObject, Class classOfType, Node domNode) {
       Map metaInfoMap = schemaObject.getMetaInfoMap();
       CustomElement customElt = (CustomElement)metaInfoMap.get(CustomElement.CUSTOM_ELT_QNAME);

        Element elt = (Element)domNode;
        Element extElt = elt.getOwnerDocument().createElementNS(CustomElement.CUSTOM_ELT_QNAME.getNamespaceURI(),
                                                             CustomElement.CUSTOM_ELT_QNAME.getLocalPart());
        extElt.setAttribute("prefix",customElt.getPrefix());
        extElt.setAttribute("suffix",customElt.getSuffix());

        elt.appendChild(extElt);

    }
}
