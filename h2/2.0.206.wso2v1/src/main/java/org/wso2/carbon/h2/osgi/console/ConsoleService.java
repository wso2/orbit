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
package org.wso2.carbon.h2.osgi.console;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.server.ShutdownHandler;
import org.h2.tools.Console;
import org.h2.tools.Server;
import org.h2.util.Tool;

import java.sql.SQLException;

public class ConsoleService extends Tool implements ShutdownHandler {
    private static final Log log = LogFactory.getLog(ConsoleService.class);

    private Server web, tcp, pg;
    private boolean isWindows;

    /**
     * When running without options, -tcp, -web, -browser and -pg are started.
     * <br />
     * Options are case sensitive. Supported options are:
     * <table>
     * <tr><td>[-help] or [-?]</td>
     * <td>Print the list of options</td></tr>
     * <tr><td>[-web]</td>
     * <td>Start the web server with the H2 Console</td></tr>
     * <tr><td>[-browser]</td>
     * <td>Start a browser and open a page to connect to the web server</td></tr>
     * <tr><td>[-tcp]</td>
     * <td>Start the TCP server</td></tr>
     * <tr><td>[-pg]</td>
     * <td>Start the PG server</td></tr>
     * </table>
     * For each Server, additional options are available;
     * for details, see the Server tool.<br />
     * If a service can not be started, the program
     * terminates with an exit code of 1.
     *
     * @param args the command line arguments
     * @h2.resource
     */
    public static void main(String[] args) throws SQLException {
        new Console().runTool(args);
    }

    /**
     * This tool starts the H2 Console (web-) server, as well as the TCP and PG
     * server. For JDK 1.6, a system tray icon is created, for platforms that
     * support it. Otherwise, a small window opens.
     *
     * @param args the command line arguments
     */
    public void runTool(String[] args) throws SQLException {
        isWindows = System.getProperty("os.name").startsWith("Windows");
        boolean tcpStart = false, pgStart = false, webStart = false;
        boolean browserStart = false;

        for (int i = 0; args != null && i < args.length; i++) {
            String arg = args[i];
            if (arg == null) {
                continue;
            } else if ("-?".equals(arg) || "-help".equals(arg)) {
                showUsage();
                return;
            } else if ("-web".equals(arg)) {
                webStart = true;
            } else if ("-browser".equals(arg)) {
                webStart = true;
                browserStart = true;
            } else if ("-tcp".equals(arg)) {
                tcpStart = true;
            } else if ("-pg".equals(arg)) {
                pgStart = true;
            }
        }
        SQLException startException = null;
        boolean webRunning = false;
        if (webStart) {
            try {
                web = Server.createWebServer(args);
                web.setShutdownHandler(this);
                web.start();
                log.info("Starting H2 Web server...");
                webRunning = true;
            } catch (SQLException e) {
                printProblem(e, web);
                startException = e;
            }
        }
        if (tcpStart) {
            try {
                tcp = Server.createTcpServer(args);
                tcp.start();
                log.info("Starting H2 TCP server...");
            } catch (SQLException e) {
                printProblem(e, tcp);
                if (startException == null) {
                    startException = e;
                }
            }
        }
        if (pgStart) {
            try {
                pg = Server.createPgServer(args);
                pg.start();
                log.info("Starting H2 PG server...");
            } catch (SQLException e) {
                printProblem(e, pg);
                if (startException == null) {
                    startException = e;
                }
            }
        }

        // start browser anyway (even if the server is already running)
        // because some people don't look at the output,
        // but are wondering why nothing happens
        if (browserStart) {
            try {
                Server.openBrowser(web.getURL());
            } catch (Exception e) {
                throw new RuntimeException("Error while opening the Brwoser ", e);
            }
        }
        if (startException != null) {
            throw startException;
        }
    }

    private void printProblem(SQLException e, Server server) {
        log.error("H2 Error", e);
        if (server == null) {
            e.printStackTrace();
        } else {
            out.println(server.getStatus());
            out.println("Root cause: " + e.getMessage());
        }
    }

    /**
     * INTERNAL
     */
    public void shutdown() {
        stopAll();
    }

    /**
     * Stop all servers that were started using the console.
     */
    void stopAll() {
        if (web != null && web.isRunning(false)) {
            web.stop();
            web = null;
            log.info("Stopping H2 Web server...");
        }
        if (tcp != null && tcp.isRunning(false)) {
            tcp.stop();
            tcp = null;
            log.info("Stopping H2 TCP server...");
        }
        if (pg != null && pg.isRunning(false)) {
            pg.stop();
            pg = null;
            log.info("Stopping H2 PG server...");
        }
    }

    public boolean isServerRunning() {
        return ((web != null && web.isRunning(false)) || (tcp != null && tcp.isRunning(false)) ||
                (pg != null && pg.isRunning(false)));
    }
}
