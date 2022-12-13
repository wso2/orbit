/*
* Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.h2.osgi.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class CarbonUtils {
    public static String getCarbonXmlFilePath() {
        String carbonXmlPath =
                System.getProperty(CarbonConstants.CARBON_HOME) + File.separator + "repository" + File.separator +
                CarbonConstants.CONF_FOLDER + File.separator + CarbonConstants.CARBON_XML_FILE;
        try {
            new File(carbonXmlPath);
            return carbonXmlPath;
        } catch (Exception e) {
            //the property is invalid does not exist keep using the default file path
        }
        return null;
    }

    public static Map getH2Parameters() {
        Map h2Properties = new HashMap();
        H2Utils.resetToDefaultParameters(h2Properties);
        String carbonXmlFile = getCarbonXmlFilePath();
        try {
            File file = new File(carbonXmlFile);
            if (!file.exists()) {
                return h2Properties;
            }
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            Element rootEle = doc.getDocumentElement();
            NodeList childNodes2 = rootEle.getChildNodes();
            Node dbConfigurationNode = null;
            Map h2PropertiesConfigured = new HashMap();
            for (int i = 0; i < childNodes2.getLength(); i++) {
                Node propertyEle = (Node) childNodes2.item(i);
                if (propertyEle.getNodeName().equals(CarbonConstants.TAG_DB_CONFIGURATION)) {
                    dbConfigurationNode = propertyEle;
                }
            }
            if (dbConfigurationNode == null)
                return h2Properties;

            NodeList childNodes = dbConfigurationNode.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node propertyEle = (Node) childNodes.item(i);
                if (!propertyEle.getNodeName().equals(CarbonConstants.TAG_PROPERTY))
                    continue;
                NamedNodeMap propertyAttribs = propertyEle.getAttributes();
                Node nameAttrib = propertyAttribs.getNamedItem(CarbonConstants.ATTRIBUTE_NAME);
                String transport;
                if (nameAttrib == null) {
                    continue;
                }
                String paraName = nameAttrib.getNodeValue();
                String value = propertyEle.getTextContent();
                if (H2Constants.getParameterList().contains(paraName)) {
                    if (value != null)
                        value = value.replaceAll(Pattern.quote(CarbonConstants.CARBON_HOME_PARAMETER),
                                                 CarbonUtils.getCarbonHome());
                    h2PropertiesConfigured.put(paraName, value);
                }
            }
            if (h2PropertiesConfigured.containsKey(H2Constants.PARA_WEB) ||
                h2PropertiesConfigured.containsKey(H2Constants.PARA_TCP) ||
                h2PropertiesConfigured.containsKey(H2Constants.PARA_PG)) {
                h2Properties = h2PropertiesConfigured;
            } else {
                for (Iterator iterator = h2PropertiesConfigured.keySet().iterator(); iterator.hasNext(); ) {
                    String paraName = (String) iterator.next();
                    h2Properties.put(paraName, h2PropertiesConfigured.get(paraName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return h2Properties;
    }

    public static String getCarbonHome() {
        String carbonHome = System.getProperty(CarbonConstants.CARBON_HOME);
        if (carbonHome == null) {
            carbonHome = System.getenv(CarbonConstants.CARBON_HOME_ENV);
        }
        return carbonHome;
    }
}
