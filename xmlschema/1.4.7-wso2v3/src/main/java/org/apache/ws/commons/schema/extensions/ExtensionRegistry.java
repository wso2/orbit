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
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

/**
 * The task of the extension serializer is to delegate the serilization of
 * the extensions. The extension serializers/deserializers are retained in seperate
 * maps and there are also two seperate references to default serializers and deserializers
 * which would jump into action when there are no specifically attached
 * serializers/deserializers
 */
public class ExtensionRegistry {

    /**
     * Maps for the storage of extension serializers /deserializers
     */
    private Map extensionSerializers = new HashMap();
    private Map extensionDeserializers = new HashMap();

    /**
     * Default serializer and serializer
     */
    private ExtensionSerializer defaultExtensionSerializer  =new DefaultExtensionSerializer();
    private ExtensionDeserializer defaultExtensionDeserializer = new DefaultExtensionDeserializer();


    public ExtensionSerializer getDefaultExtensionSerializer() {
        return defaultExtensionSerializer;
    }

    public void setDefaultExtensionSerializer(ExtensionSerializer defaultExtensionSerializer) {
        this.defaultExtensionSerializer = defaultExtensionSerializer;
    }

    public ExtensionDeserializer getDefaultExtensionDeserializer() {
        return defaultExtensionDeserializer;
    }

    public void setDefaultExtensionDeserializer(ExtensionDeserializer defaultExtensionDeserializer) {
        this.defaultExtensionDeserializer = defaultExtensionDeserializer;
    }

    /**
     * Register a deserializer with a QName
     * @param name  - the QName of the element/attribute
     * @param deserializer - an instance of the deserializer
     */
    public void registerDeserializer(QName name,ExtensionDeserializer deserializer){
        extensionDeserializers.put(name,deserializer);
    }
    /**
     * Register a serializer with a Class
     * @param classOfType  - the class of the object that would be serialized
     * @param serializer - an instance of the deserializer
     */
    public void registerSerializer(Class classOfType,ExtensionSerializer serializer){
        extensionSerializers.put(classOfType,serializer);
    }


    /**
     * remove the registration for a serializer with a QName
     * @param name  - the QName of the element/attribute the
     * serializer is associated with
     */
    public void unregisterSerializer(QName name){
        extensionSerializers.remove(name);
    }

    /**
     * remove the registration for a deserializer with a QName
     * @param classOfType  - the  the
     * deserializer is associated with
     */
    public void unregisterDeserializer(Class classOfType){
        extensionDeserializers.remove(classOfType);
    }


    /**
     * Serialize a given extension element
     * @param parentSchemaObject - the parent schema object. This is what
     * would contain the extension object, probably in side its meta information
     * map
     * @param classOfType - The class of type to be serialized
     * @param  node - the parent DOM Node that will ultimately be serialized. The XMLSchema
     * serialization mechanism is to create a DOM tree first and serialize it
     */
    public void serializeExtension(XmlSchemaObject parentSchemaObject,
                             Class classOfType,
                             Node node){
        Object serializerObject = extensionSerializers.get(classOfType);
        if (serializerObject!=null){
            //perform the serialization
            ExtensionSerializer ser = (ExtensionSerializer)serializerObject;
            ser.serialize(parentSchemaObject,classOfType,node);
        }else if (defaultExtensionSerializer!=null) {
            defaultExtensionSerializer.serialize(parentSchemaObject,classOfType,node);
        }


    }


    /**
     * Deserialize a given extension element
     * @param parentSchemaObject - the parent schema object. This is anticipated
     * to be created already and the relevant object would contain the extension
     * object, probably in side its meta information map
     * @param name - The qname of the element/attribute to be deserialized. This will be used to
     * search for the extension as well as by the deserializer if a single deserializer is
     * registered against a number of qnames
     * @param  rawNode  - the raw DOM Node read from the source. This will be the
     * extension element itself if for an element or extension attribute itself
     * in case of an attribute
     */
    public void deserializeExtension(XmlSchemaObject parentSchemaObject,
                             QName name,
                             Node rawNode){
        Object deserializerObject = extensionDeserializers.get(name);
        if (deserializerObject !=null){
            //perform the serialization
            ExtensionDeserializer deser = (ExtensionDeserializer)deserializerObject;
            deser.deserialize(parentSchemaObject,name,rawNode);
        } else if (defaultExtensionDeserializer!=null){
            defaultExtensionDeserializer.deserialize(parentSchemaObject,name,rawNode);
        }
    }

}
