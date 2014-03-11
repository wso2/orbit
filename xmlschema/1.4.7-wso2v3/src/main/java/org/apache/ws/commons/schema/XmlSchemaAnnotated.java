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

import org.w3c.dom.Attr;

/**
 * The base class for any element that can contain annotation elements.
 */

public class XmlSchemaAnnotated extends XmlSchemaObject {
    /**
     * Defines an annotation.
     * Creates an annotation element.
     * Represents the W3C annotation element.
     */
    XmlSchemaAnnotation annotation;
    String id;

    // Stores qualified attributes that do not belong to the schema target namespace.
    public Attr[] unhandledAttributes;


    /**
     * Creates new XmlSchemaAnnotated
     */
    public XmlSchemaAnnotated() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public XmlSchemaAnnotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(XmlSchemaAnnotation annotation) {
        this.annotation = annotation;
    }

    public Attr[] getUnhandledAttributes() {
        return unhandledAttributes;
    }

    public void setUnhandledAttributes(Attr[] unhandledAttributes) {
        this.unhandledAttributes = unhandledAttributes;
    }
    
    public String toString() {
    	if (id == null)
    		return super.toString();
    	else
    		return super.toString() + " [id:" + id + "]";
    }

}

