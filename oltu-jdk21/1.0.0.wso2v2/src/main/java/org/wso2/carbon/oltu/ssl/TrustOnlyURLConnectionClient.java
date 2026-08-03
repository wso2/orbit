/**
 * Copyright (c) 2026, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.oltu.ssl;

import org.apache.oltu.oauth2.client.HttpClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthClientResponse;
import org.apache.oltu.oauth2.client.response.OAuthClientResponseFactory;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Oltu {@link HttpClient} based on {@code URLConnection}, with per-connection trust-only SSL so
 * outbound calls do not present a TLS client certificate from the JVM default keystore (avoids an
 * unintended TLS-client-auth + {@code client_secret} dual authentication). Does not modify or
 * replace {@link org.apache.oltu.oauth2.client.URLConnectionClient} - that class and its default
 * behavior are unchanged; this is an additional, opt-in {@link HttpClient} implementation.
 * <p>
 * Otherwise matches {@code org.apache.oltu.oauth2.client.URLConnectionClient} behavior for the
 * oltu-jdk21 orbit API ({@code createCustomResponse(String, String, int, Class)}).
 */
public final class TrustOnlyURLConnectionClient implements HttpClient {

    private static final int SC_BAD_REQUEST = 400;
    private static final int SC_UNAUTHORIZED = 401;
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 10000;
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 10000;

    private final int connectTimeoutMillis;
    private final int readTimeoutMillis;

    public TrustOnlyURLConnectionClient() {

        this(DEFAULT_CONNECT_TIMEOUT_MILLIS, DEFAULT_READ_TIMEOUT_MILLIS);
    }

    public TrustOnlyURLConnectionClient(int connectTimeoutMillis, int readTimeoutMillis) {

        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
    }

    @Override
    public <T extends OAuthClientResponse> T execute(OAuthClientRequest request, Map<String, String> headers,
                                                     String requestMethod, Class<T> responseClass)
            throws OAuthSystemException, OAuthProblemException {

        String responseBody = null;
        URLConnection connection;
        int responseCode = 0;

        try {
            URL url = new URL(request.getLocationUri());
            connection = url.openConnection();
            // Matches URLConnectionClient: -1 unless overwritten by HttpURLConnection.getResponseCode() below.
            responseCode = -1;

            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
                httpURLConnection.setConnectTimeout(connectTimeoutMillis);
                httpURLConnection.setReadTimeout(readTimeoutMillis);
                TrustOnlySslUtils.applyTrustOnlySslIfHttps(httpURLConnection);

                if (headers != null && !headers.isEmpty()) {
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        httpURLConnection.addRequestProperty(header.getKey(), header.getValue());
                    }
                }

                if (request.getHeaders() != null) {
                    for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
                        httpURLConnection.addRequestProperty(header.getKey(), header.getValue());
                    }
                }

                if (OAuthUtils.isEmpty(requestMethod)) {
                    httpURLConnection.setRequestMethod(OAuth.HttpMethod.GET);
                } else {
                    httpURLConnection.setRequestMethod(requestMethod);
                    setRequestBody(request, requestMethod, httpURLConnection);
                }

                httpURLConnection.connect();

                responseCode = httpURLConnection.getResponseCode();
                InputStream inputStream;
                if (responseCode == SC_BAD_REQUEST || responseCode == SC_UNAUTHORIZED) {
                    // getErrorStream() may legitimately return null if the server sent no error body.
                    inputStream = httpURLConnection.getErrorStream();
                } else {
                    inputStream = httpURLConnection.getInputStream();
                }
                responseBody = (inputStream != null) ? OAuthUtils.saveStreamAsString(inputStream) : "";
            }
        } catch (IOException e) {
            throw new OAuthSystemException(e);
        }

        return OAuthClientResponseFactory.createCustomResponse(responseBody, connection.getContentType(),
                responseCode, responseClass);
    }

    private void setRequestBody(OAuthClientRequest request, String requestMethod,
                                HttpURLConnection httpURLConnection) throws IOException {

        String requestBody = request.getBody();
        if (OAuthUtils.isEmpty(requestBody)) {
            return;
        }

        if (OAuth.HttpMethod.POST.equals(requestMethod) || OAuth.HttpMethod.PUT.equals(requestMethod)) {
            httpURLConnection.setDoOutput(true);
            try (OutputStream outputStream = httpURLConnection.getOutputStream();
                 Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                writer.write(requestBody);
                writer.flush();
            }
        }
    }

    @Override
    public void shutdown() {
        // Nothing to shut down for URLConnection-based client.
    }
}
