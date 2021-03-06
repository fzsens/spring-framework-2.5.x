/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.mock.web.portlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.portlet.ActionRequest;
import javax.portlet.PortalContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;

/**
 * Mock implementation of the {@link javax.portlet.ActionRequest} interface.
 *
 * @author John A. Lewis
 * @author Juergen Hoeller
 * @since 2.0
 */
public class MockActionRequest extends MockPortletRequest implements ActionRequest {

	private String characterEncoding;

	private byte[] content;

	private String contentType;


	/**
	 * Create a new MockActionRequest with a default {@link MockPortalContext}
	 * and a default {@link MockPortletContext}.
	 * @see MockPortalContext
	 * @see MockPortletContext
	 */
	public MockActionRequest() {
		super();
	}

	/**
	 * Create a new MockActionRequest with a default {@link MockPortalContext}
	 * and a default {@link MockPortletContext}.
	 * @param portletMode the mode that the portlet runs in
	 */
	public MockActionRequest(PortletMode portletMode) {
		super();
		setPortletMode(portletMode);
	}

	/**
	 * Create a new MockActionRequest with a default {@link MockPortalContext}.
	 * @param portletContext the PortletContext that the request runs in
	 */
	public MockActionRequest(PortletContext portletContext) {
		super(portletContext);
	}

	/**
	 * Create a new MockActionRequest.
	 * @param portalContext the PortalContext that the request runs in
	 * @param portletContext the PortletContext that the request runs in
	 */
	public MockActionRequest(PortalContext portalContext, PortletContext portletContext) {
		super(portalContext, portletContext);
	}


	public void setContent(byte[] content) {
		this.content = content;
	}

	public InputStream getPortletInputStream() throws IOException {
		if (this.content != null) {
			return new ByteArrayInputStream(this.content);
		}
		else {
			return null;
        }
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public BufferedReader getReader() throws UnsupportedEncodingException {
		if (this.content != null) {
			InputStream sourceStream = new ByteArrayInputStream(this.content);
			Reader sourceReader = (this.characterEncoding != null) ?
			    new InputStreamReader(sourceStream, this.characterEncoding) : new InputStreamReader(sourceStream);
			return new BufferedReader(sourceReader);
		}
		else {
			return null;
		}
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public int getContentLength() {
		return (this.content != null ? content.length : -1);
	}

}
