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
package org.apache.ws.commons.schema.extensions;

import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.constants.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import java.util.Iterator;
import java.util.Map;

/**

 */
public class DefaultExtensionSerializer implements ExtensionSerializer{

    /**
     * serialize the given element
     *
     * @param schemaObject - Parent schema element
     * @param classOfType  - the class of the object to be serialized
     * @param node - The DOM Node that is the parent of the serialzation
     */
    public void serialize(XmlSchemaObject schemaObject, Class classOfType, Node node) {
        // serialization is somewhat tricky in most cases hence this default serializer will
        // do the exact reverse of the deserializer - look for any plain 'as is' items
        // and attach them to the parent node.
        // we just attach the raw node either to the meta map of
        // elements or the attributes
        Map metaInfoMap = schemaObject.getMetaInfoMap();
        Document parentDoc = node.getOwnerDocument();
        if (metaInfoMap.containsKey(Constants.MetaDataConstants.EXTERNAL_ATTRIBUTES)){
            Map attribMap  = (Map)metaInfoMap.get(Constants.MetaDataConstants.EXTERNAL_ATTRIBUTES);
           for(Iterator it = attribMap.values().iterator();it.hasNext();){
                if (node.getNodeType()==Node.ELEMENT_NODE){
                    ((Element)node).setAttributeNodeNS((Attr)parentDoc.importNode((Node)it.next(),true));
                }

            }
        }

        if (metaInfoMap.containsKey(Constants.MetaDataConstants.EXTERNAL_ELEMENTS)){
            Map elementMap  = (Map)metaInfoMap.get(Constants.MetaDataConstants.EXTERNAL_ELEMENTS);
            for(Iterator it = elementMap.values().iterator();it.hasNext();){
                node.appendChild(
                        parentDoc.importNode((Node)it.next(),true));
            }
        }

    }
}
