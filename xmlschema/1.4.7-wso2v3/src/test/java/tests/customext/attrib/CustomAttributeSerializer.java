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
package tests.customext.attrib;

import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.extensions.ExtensionSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * serializer for the custom attribute
 */
public class CustomAttributeSerializer  implements ExtensionSerializer {

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
        CustomAttribute att = (CustomAttribute)metaInfoMap.get(CustomAttribute.CUSTOM_ATTRIBUTE_QNAME);

        Element elt = (Element)domNode;
        Attr att1 = elt.getOwnerDocument().createAttributeNS(CustomAttribute.CUSTOM_ATTRIBUTE_QNAME.getNamespaceURI(),
                                                             CustomAttribute.CUSTOM_ATTRIBUTE_QNAME.getLocalPart());
        att1.setValue(att.getPrefix() + ":" + att.getSuffix());
        elt.setAttributeNodeNS(att1);
    }
}
