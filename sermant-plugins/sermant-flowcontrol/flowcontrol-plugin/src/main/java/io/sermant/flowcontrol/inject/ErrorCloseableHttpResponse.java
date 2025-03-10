/*
 * Copyright (C) 2024-2024 Sermant Authors. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.sermant.flowcontrol.inject;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderIterator;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.Locale;

/**
 * httpclient response
 *
 * @author zhouss
 * @since 2024-12-20
 */
public class ErrorCloseableHttpResponse implements CloseableHttpResponse {
    private final int statusCode;

    private final String message;

    private final ProtocolVersion protocolVersion;

    /**
     * Constructor
     *
     * @param statusCode Response code
     * @param message error msg
     * @param protocolVersion Request an agreement version
     */
    public ErrorCloseableHttpResponse(int statusCode, String message, ProtocolVersion protocolVersion) {
        this.statusCode = statusCode;
        this.message = message;
        this.protocolVersion = protocolVersion;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public StatusLine getStatusLine() {
        return new BasicStatusLine(HttpVersion.HTTP_1_1, this.statusCode, this.message);
    }

    @Override
    public void setStatusLine(StatusLine statusline) {

    }

    @Override
    public void setStatusLine(ProtocolVersion ver, int code) {

    }

    @Override
    public void setStatusLine(ProtocolVersion ver, int code, String reason) {

    }

    @Override
    public void setStatusCode(int code) throws IllegalStateException {

    }

    @Override
    public void setReasonPhrase(String reason) throws IllegalStateException {

    }

    @Override
    public HttpEntity getEntity() {
        return new StringEntity(message == null ? "unKnow error" : message, ContentType.APPLICATION_JSON);
    }

    @Override
    public void setEntity(HttpEntity entity) {
    }

    @Override
    public Locale getLocale() {
        return Locale.ENGLISH;
    }

    @Override
    public void setLocale(Locale loc) {
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public Header[] getHeaders(String name) {
        return new Header[0];
    }

    @Override
    public Header getFirstHeader(String name) {
        return new BasicHeader("type", "SermantErrorResponse");
    }

    @Override
    public Header getLastHeader(String name) {
        return getFirstHeader(name);
    }

    @Override
    public Header[] getAllHeaders() {
        return new Header[0];
    }

    @Override
    public void addHeader(Header header) {

    }

    @Override
    public void addHeader(String name, String value) {

    }

    @Override
    public void setHeader(Header header) {

    }

    @Override
    public void setHeader(String name, String value) {

    }

    @Override
    public void setHeaders(Header[] headers) {

    }

    @Override
    public void removeHeader(Header header) {

    }

    @Override
    public void removeHeaders(String name) {

    }

    @Override
    public HeaderIterator headerIterator() {
        return this.headerIterator("errorHeaders");
    }

    @Override
    public HeaderIterator headerIterator(String name) {
        return new BasicHeaderIterator(new Header[0], name);
    }

    @Override
    public HttpParams getParams() {
        return new BasicHttpParams();
    }

    @Override
    public void setParams(HttpParams params) {
    }
}
