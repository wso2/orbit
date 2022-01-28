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

import java.util.ArrayList;
import java.util.List;

public class H2Constants {
    public static final String PARA_WEB = "web"; 							//Start the web server with the H2 Console
    public static final String PARA_WEB_ALLOW_OTHERS = "webAllowOthers"; 	//Allow other computers to connect
    public static final String PARA_WEB_PORT = "webPort"; 					//The port (default: 8082)
    public static final String PARA_WEB_SSL = "webSSL"; 					//Use encrypted (HTTPS) connections
    public static final String PARA_BROWSER = "browser"; 					//Start a browser and open a page to connect to the web server
    public static final String PARA_TCP = "tcp"; 							//Start the TCP server
    public static final String PARA_TCP_ALLOW_OTHERS = "tcpAllowOthers"; 	//Allow other computers to connect
    public static final String PARA_TCP_PORT = "tcpPort";			 		//The port (default: 9092)
    public static final String PARA_TCP_SSL = "tcpSSL"; 					//Use encrypted (SSL) connections
    public static final String PARA_TCP_PASSWORD = "tcpPassword"; 			//The password for shutting down a TCP server
    public static final String PARA_TCP_SHUTDOWN = "tcpShutdown"; 			//Stop the TCP server; example: tcp://localhost:9094
    public static final String PARA_TCP_SHUTDOWN_FORCE = "tcpShutdownForce";//Do not wait until all connections are closed
    public static final String PARA_PG = "pg"; 								//Start the PG server
    public static final String PARA_PG_ALLOW_OTHERS = "pgAllowOthers"; 		//Allow other computers to connect
    public static final String PARA_PG_PORT = "pgPort"; 					//The port (default: 5435)
    public static final String PARA_BASE_DIR = "baseDir"; 					//The base directory for H2 databases; for all servers
    public static final String PARA_IF_EXISTS = "ifExists"; 				//Only existing databases may be opened; for all servers
    public static final String PARA_TRACE = "trace"; 						//Print additional trace information; for all servers


    public static final String ENGINE_VAR_DATABASES="DATABASES";
    public static final String ENGINE_METHOD_CLOSE="close";
    public static String[] getParameterStringList(){
    	return (String[])getParameterList().toArray(new String[]{});
    }

    public static List getParameterList(){
    	List paraList=new ArrayList();
    	paraList.add(PARA_WEB);
    	paraList.add(PARA_WEB_ALLOW_OTHERS);
    	paraList.add(PARA_WEB_PORT);
    	paraList.add(PARA_WEB_SSL);
    	paraList.add(PARA_BROWSER);
    	paraList.add(PARA_TCP);
    	paraList.add(PARA_TCP_ALLOW_OTHERS);
    	paraList.add(PARA_TCP_PORT);
    	paraList.add(PARA_TCP_SSL);
    	paraList.add(PARA_PG);
    	paraList.add(PARA_PG_ALLOW_OTHERS);
    	paraList.add(PARA_PG_PORT);
    	paraList.add(PARA_BASE_DIR);
    	paraList.add(PARA_IF_EXISTS);
    	paraList.add(PARA_TRACE);
    	return paraList;
    }
}
