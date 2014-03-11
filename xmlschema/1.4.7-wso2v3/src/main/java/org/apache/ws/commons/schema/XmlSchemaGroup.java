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

import javax.xml.namespace.QName;

/**
 * Class that defines groups at the schema level that are referenced
 * from the complex types. Groups a set of element declarations so that
 * they can be incorporated as a group into complex type definitions.
 * Represents the World Wide Web Consortium (W3C) group element.
 */

public class XmlSchemaGroup extends XmlSchemaAnnotated {

    /**
     * Creates new XmlSchemaGroup
     */
    public XmlSchemaGroup() {
    }

    QName name;
    XmlSchemaGroupBase particle;

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public XmlSchemaGroupBase getParticle() {
        return particle;
    }

    public void setParticle(XmlSchemaGroupBase particle) {
        this.particle = particle;
    }

}
