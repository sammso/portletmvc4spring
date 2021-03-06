/**
 * Copyright (c) 2000-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.portletmvc4spring.context;

import java.io.Serializable;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.mockito.BDDMockito.*;

import org.springframework.web.context.request.RequestAttributes;

import com.liferay.spring.mock.web.portlet.MockPortletRequest;
import com.liferay.spring.mock.web.portlet.MockPortletSession;


/**
 * @author  Rick Evans
 * @author  Juergen Hoeller
 * @author  Chris Beams
 */
public class PortletRequestAttributesTests {

	private static final String KEY = "ThatThingThatThing";

	@SuppressWarnings("serial")
	private static final Serializable VALUE = new Serializable() {
		};

	@Test(expected = IllegalArgumentException.class)
	public void testCtorRejectsNullArg() throws Exception {
		new PortletRequestAttributes(null);
	}

	@Test
	public void testGetSessionScopedAttributeDoesNotForceCreationOfSession() throws Exception {
		PortletRequest request = mock(PortletRequest.class);

		PortletRequestAttributes attrs = new PortletRequestAttributes(request);
		Object value = attrs.getAttribute(KEY, RequestAttributes.SCOPE_SESSION);
		assertNull(value);

		verify(request).getPortletSession(false);
	}

	@Test
	public void testRemoveSessionScopedAttribute() throws Exception {
		MockPortletSession session = new MockPortletSession();
		session.setAttribute(KEY, VALUE);

		MockPortletRequest request = new MockPortletRequest();
		request.setSession(session);

		PortletRequestAttributes attrs = new PortletRequestAttributes(request);
		attrs.removeAttribute(KEY, RequestAttributes.SCOPE_SESSION);

		Object value = session.getAttribute(KEY);
		assertNull(value);
	}

	@Test
	public void testRemoveSessionScopedAttributeDoesNotForceCreationOfSession() throws Exception {
		PortletRequest request = mock(PortletRequest.class);

		PortletRequestAttributes attrs = new PortletRequestAttributes(request);
		attrs.removeAttribute(KEY, RequestAttributes.SCOPE_SESSION);

		verify(request).getPortletSession(false);
	}

	@Test
	public void testSetGlobalSessionScopedAttribute() throws Exception {
		MockPortletSession session = new MockPortletSession();
		session.setAttribute(KEY, VALUE, PortletSession.APPLICATION_SCOPE);

		MockPortletRequest request = new MockPortletRequest();
		request.setSession(session);

		PortletRequestAttributes attrs = new PortletRequestAttributes(request);
		attrs.setAttribute(KEY, VALUE, PortletRequestAttributes.SCOPE_GLOBAL_SESSION);
		assertSame(VALUE, session.getAttribute(KEY, PortletSession.APPLICATION_SCOPE));
	}

	@Test
	public void testSetGlobalSessionScopedAttributeAfterCompletion() throws Exception {
		MockPortletSession session = new MockPortletSession();
		session.setAttribute(KEY, VALUE, PortletSession.APPLICATION_SCOPE);

		MockPortletRequest request = new MockPortletRequest();
		request.setSession(session);

		PortletRequestAttributes attrs = new PortletRequestAttributes(request);
		assertSame(VALUE, attrs.getAttribute(KEY, PortletRequestAttributes.SCOPE_GLOBAL_SESSION));
		attrs.requestCompleted();
		request.close();
		attrs.setAttribute(KEY, VALUE, PortletRequestAttributes.SCOPE_GLOBAL_SESSION);
		assertSame(VALUE, session.getAttribute(KEY, PortletSession.APPLICATION_SCOPE));
	}

	@Test
	public void testSetRequestScopedAttribute() throws Exception {
		MockPortletRequest request = new MockPortletRequest();
		PortletRequestAttributes attrs = new PortletRequestAttributes(request);
		attrs.setAttribute(KEY, VALUE, RequestAttributes.SCOPE_REQUEST);

		Object value = request.getAttribute(KEY);
		assertSame(VALUE, value);
	}

	@Test
	public void testSetRequestScopedAttributeAfterCompletion() throws Exception {
		MockPortletRequest request = new MockPortletRequest();
		PortletRequestAttributes attrs = new PortletRequestAttributes(request);
		request.close();

		try {
			attrs.setAttribute(KEY, VALUE, RequestAttributes.SCOPE_REQUEST);
			fail("Should have thrown IllegalStateException");
		}
		catch (IllegalStateException ex) {
			// expected
		}
	}

	@Test
	public void testSetSessionScopedAttribute() throws Exception {
		MockPortletSession session = new MockPortletSession();
		session.setAttribute(KEY, VALUE, PortletSession.PORTLET_SCOPE);

		MockPortletRequest request = new MockPortletRequest();
		request.setSession(session);

		PortletRequestAttributes attrs = new PortletRequestAttributes(request);
		attrs.setAttribute(KEY, VALUE, RequestAttributes.SCOPE_SESSION);
		assertSame(VALUE, session.getAttribute(KEY, PortletSession.PORTLET_SCOPE));
	}

	@Test
	public void testSetSessionScopedAttributeAfterCompletion() throws Exception {
		MockPortletSession session = new MockPortletSession();
		session.setAttribute(KEY, VALUE, PortletSession.PORTLET_SCOPE);

		MockPortletRequest request = new MockPortletRequest();
		request.setSession(session);

		PortletRequestAttributes attrs = new PortletRequestAttributes(request);
		assertSame(VALUE, attrs.getAttribute(KEY, RequestAttributes.SCOPE_SESSION));
		attrs.requestCompleted();
		request.close();
		attrs.setAttribute(KEY, VALUE, RequestAttributes.SCOPE_SESSION);
		assertSame(VALUE, session.getAttribute(KEY, PortletSession.PORTLET_SCOPE));
	}

	@Test
	public void testUpdateAccessedAttributes() throws Exception {
		MockPortletSession session = new MockPortletSession();
		session.setAttribute(KEY, VALUE);

		MockPortletRequest request = new MockPortletRequest();
		request.setSession(session);

		PortletRequestAttributes attrs = new PortletRequestAttributes(request);
		Object value = attrs.getAttribute(KEY, RequestAttributes.SCOPE_SESSION);
		assertSame(VALUE, value);
		attrs.requestCompleted();
	}

}
