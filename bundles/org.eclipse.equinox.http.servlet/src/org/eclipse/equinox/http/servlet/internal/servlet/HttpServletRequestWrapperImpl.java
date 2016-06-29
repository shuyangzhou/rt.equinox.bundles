/*******************************************************************************
 * Copyright (c) 2005, 2016 Cognos Incorporated, IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Cognos Incorporated - initial API and implementation
 *     IBM Corporation - bug fixes and enhancements
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 *******************************************************************************/
package org.eclipse.equinox.http.servlet.internal.servlet;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.context.DispatchTargets;
import org.eclipse.equinox.http.servlet.internal.util.Const;
import org.eclipse.equinox.http.servlet.internal.util.EventListeners;
import org.osgi.service.http.HttpContext;

public class HttpServletRequestWrapperImpl extends HttpServletRequestWrapper {

	private final Stack<DispatchTargets> dispatchTargets = new Stack<DispatchTargets>();
	private final HttpServletRequest request;

	private static final Set<String> dispatcherAttributes =	new HashSet<String>();

	static {
		dispatcherAttributes.add(RequestDispatcher.ERROR_EXCEPTION);
		dispatcherAttributes.add(RequestDispatcher.ERROR_EXCEPTION_TYPE);
		dispatcherAttributes.add(RequestDispatcher.ERROR_MESSAGE);
		dispatcherAttributes.add(RequestDispatcher.ERROR_REQUEST_URI);
		dispatcherAttributes.add(RequestDispatcher.ERROR_SERVLET_NAME);
		dispatcherAttributes.add(RequestDispatcher.ERROR_STATUS_CODE);
		dispatcherAttributes.add(RequestDispatcher.FORWARD_CONTEXT_PATH);
		dispatcherAttributes.add(RequestDispatcher.FORWARD_PATH_INFO);
		dispatcherAttributes.add(RequestDispatcher.FORWARD_QUERY_STRING);
		dispatcherAttributes.add(RequestDispatcher.FORWARD_REQUEST_URI);
		dispatcherAttributes.add(RequestDispatcher.FORWARD_SERVLET_PATH);
		dispatcherAttributes.add(RequestDispatcher.INCLUDE_CONTEXT_PATH);
		dispatcherAttributes.add(RequestDispatcher.INCLUDE_PATH_INFO);
		dispatcherAttributes.add(RequestDispatcher.INCLUDE_QUERY_STRING);
		dispatcherAttributes.add(RequestDispatcher.INCLUDE_REQUEST_URI);
		dispatcherAttributes.add(RequestDispatcher.INCLUDE_SERVLET_PATH);
	}

	public static HttpServletRequestWrapperImpl findHttpRuntimeRequest(
		HttpServletRequest request) {

		while (request instanceof HttpServletRequestWrapper) {
			if (request instanceof HttpServletRequestWrapperImpl) {
				return (HttpServletRequestWrapperImpl)request;
			}

			request = (HttpServletRequest)((HttpServletRequestWrapper)request).getRequest();
		}

		return null;
	}

	public HttpServletRequestWrapperImpl(HttpServletRequest request) {
		super(request);
		this.request = request;
	}

	public String getAuthType() {
		String authType = (String) this.getAttribute(HttpContext.AUTHENTICATION_TYPE);
		if (authType != null)
			return authType;

		return request.getAuthType();
	}

	public String getRemoteUser() {
		String remoteUser = (String) this.getAttribute(HttpContext.REMOTE_USER);
		if (remoteUser != null)
			return remoteUser;

		return request.getRemoteUser();
	}

	public String getPathInfo() {
		if ((dispatchTargets.peek().getServletName() != null) ||
			(dispatchTargets.peek().getDispatcherType() == DispatcherType.INCLUDE)) {
			return this.dispatchTargets.get(0).getPathInfo();
		}
		return this.dispatchTargets.peek().getPathInfo();
	}

	public DispatcherType getDispatcherType() {
		return dispatchTargets.peek().getDispatcherType();
	}

	public String getParameter(String name) {
		String[] values = getParameterValues(name);
		if ((values == null) || (values.length == 0)) {
			return null;
		}
		return values[0];
	}

	public Map<String, String[]> getParameterMap() {
		return dispatchTargets.peek().getParameterMap();
	}

	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(getParameterMap().keySet());
	}

	public String[] getParameterValues(String name) {
		return getParameterMap().get(name);
	}

	@Override
	public String getQueryString() {
		if ((dispatchTargets.peek().getServletName() != null) ||
			(dispatchTargets.peek().getDispatcherType() == DispatcherType.INCLUDE)) {
			return request.getQueryString();
		}
		return this.dispatchTargets.peek().getQueryString();
	}

	@Override
	public String getRequestURI() {
		if ((dispatchTargets.peek().getServletName() != null) ||
			(dispatchTargets.peek().getDispatcherType() == DispatcherType.INCLUDE)) {
			return request.getRequestURI();
		}
		return this.dispatchTargets.peek().getRequestURI();
	}

	public ServletContext getServletContext() {
		return dispatchTargets.peek().getServletRegistration().getServletContext();
	}

	public String getServletPath() {
		if ((dispatchTargets.peek().getServletName() != null) ||
			(dispatchTargets.peek().getDispatcherType() == DispatcherType.INCLUDE)) {
			return this.dispatchTargets.get(0).getServletPath();
		}
		if (dispatchTargets.peek().getServletPath().equals(Const.SLASH)) {
			return Const.BLANK;
		}
		return this.dispatchTargets.peek().getServletPath();
	}

	public String getContextPath() {
		return dispatchTargets.peek().getContextController().getFullContextPath();
	}

	public Object getAttribute(String attributeName) {
		DispatchTargets current = dispatchTargets.peek();

		DispatcherType dispatcherType = current.getDispatcherType();

		boolean hasServletName = (current.getServletName() != null);

		if (dispatcherType == DispatcherType.ERROR) {
			if (dispatcherAttributes.contains(attributeName) &&
				!attributeName.startsWith("javax.servlet.error.")) { //$NON-NLS-1$

				return null;
			}
		}
		else if (dispatcherType == DispatcherType.INCLUDE) {
			if (attributeName.equals(RequestDispatcher.INCLUDE_CONTEXT_PATH)) {
				if (hasServletName) {
					return null;
				}

				Object attributeValue = super.getAttribute(attributeName);

				if (attributeValue != null) {
					return attributeValue;
				}

				return current.getContextController().getContextPath();
			}
			else if (attributeName.equals(RequestDispatcher.INCLUDE_PATH_INFO)) {
				if (hasServletName) {
					return null;
				}

				Object attributeValue = super.getAttribute(attributeName);

				if (attributeValue != null) {
					return attributeValue;
				}

				return current.getPathInfo();
			}
			else if (attributeName.equals(RequestDispatcher.INCLUDE_QUERY_STRING)) {
				if (hasServletName) {
					return null;
				}

				Object attributeValue = super.getAttribute(attributeName);

				if (attributeValue != null) {
					return attributeValue;
				}

				return current.getQueryString();
			}
			else if (attributeName.equals(RequestDispatcher.INCLUDE_REQUEST_URI)) {
				if (hasServletName) {
					return null;
				}

				Object attributeValue = super.getAttribute(attributeName);

				if (attributeValue != null) {
					return attributeValue;
				}

				return current.getRequestURI();
			}
			else if (attributeName.equals(RequestDispatcher.INCLUDE_SERVLET_PATH)) {
				if (hasServletName) {
					return null;
				}

				Object attributeValue = super.getAttribute(attributeName);

				if (attributeValue != null) {
					return attributeValue;
				}

				return current.getServletPath();
			}

			if (dispatcherAttributes.contains(attributeName)) {
				return null;
			}
		}
		else if (dispatcherType == DispatcherType.FORWARD) {
			if (hasServletName && attributeName.startsWith("javax.servlet.forward")) {
				return null;
			}

			DispatchTargets original = dispatchTargets.get(0);

			if (attributeName.equals(RequestDispatcher.FORWARD_CONTEXT_PATH)) {
				return original.getContextController().getContextPath();
			}
			else if (attributeName.equals(RequestDispatcher.FORWARD_PATH_INFO)) {
				return original.getPathInfo();
			}
			else if (attributeName.equals(RequestDispatcher.FORWARD_QUERY_STRING)) {
				return original.getQueryString();
			}
			else if (attributeName.equals(RequestDispatcher.FORWARD_REQUEST_URI)) {
				return original.getRequestURI();
			}
			else if (attributeName.equals(RequestDispatcher.FORWARD_SERVLET_PATH)) {
				return original.getServletPath();
			}

			if (dispatcherAttributes.contains(attributeName)) {
				return null;
			}
		}

		return request.getAttribute(attributeName);
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		ContextController contextController =
			this.dispatchTargets.peek().getContextController();

		// support relative paths
		if (!path.startsWith(Const.SLASH)) {
			path = this.dispatchTargets.peek().getServletPath() + Const.SLASH + path;
		}
		// if the path starts with the full context path strip it
		else if (path.startsWith(contextController.getFullContextPath())) {
			path = path.substring(contextController.getFullContextPath().length());
		}

		DispatchTargets requestedDispatchTargets = contextController.getDispatchTargets(path, null);

		if (requestedDispatchTargets == null) {
			return null;
		}

		return new RequestDispatcherAdaptor(requestedDispatchTargets, path);
	}

	public static String getDispatchPathInfo(HttpServletRequest req) {
		if (req.getDispatcherType() == DispatcherType.INCLUDE)
			return (String) req.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);

		return req.getPathInfo();
	}

	public HttpSession getSession() {
		HttpSession session = request.getSession();
		if (session != null) {
			return dispatchTargets.peek().getContextController().getSessionAdaptor(
				session, dispatchTargets.peek().getServletRegistration().getT().getServletConfig().getServletContext());
		}

		return null;
	}

	public HttpSession getSession(boolean create) {
		HttpSession session = request.getSession(create);
		if (session != null) {
			return dispatchTargets.peek().getContextController().getSessionAdaptor(
				session, dispatchTargets.peek().getServletRegistration().getT().getServletConfig().getServletContext());
		}

		return null;
	}

	public synchronized void pop() {
		if (dispatchTargets.size() > 1) {
			this.dispatchTargets.pop();
		}
	}

	public synchronized void push(DispatchTargets toPush) {
		toPush.addRequestParameters(request);
		this.dispatchTargets.push(toPush);
	}

	public void removeAttribute(String name) {
		request.removeAttribute(name);

		EventListeners eventListeners = dispatchTargets.peek().getContextController().getEventListeners();

		List<ServletRequestAttributeListener> listeners = eventListeners.get(
			ServletRequestAttributeListener.class);

		if (listeners.isEmpty()) {
			return;
		}

		ServletRequestAttributeEvent servletRequestAttributeEvent =
			new ServletRequestAttributeEvent(
				dispatchTargets.peek().getServletRegistration().getServletContext(), this, name, null);

		for (ServletRequestAttributeListener servletRequestAttributeListener : listeners) {
			servletRequestAttributeListener.attributeRemoved(
				servletRequestAttributeEvent);
		}
	}

	public void setAttribute(String name, Object value) {
		boolean added = (request.getAttribute(name) == null);
		request.setAttribute(name, value);

		EventListeners eventListeners = dispatchTargets.peek().getContextController().getEventListeners();

		List<ServletRequestAttributeListener> listeners = eventListeners.get(
			ServletRequestAttributeListener.class);

		if (listeners.isEmpty()) {
			return;
		}

		ServletRequestAttributeEvent servletRequestAttributeEvent =
			new ServletRequestAttributeEvent(
				dispatchTargets.peek().getServletRegistration().getServletContext(), this, name, value);

		for (ServletRequestAttributeListener servletRequestAttributeListener : listeners) {
			if (added) {
				servletRequestAttributeListener.attributeAdded(
					servletRequestAttributeEvent);
			}
			else {
				servletRequestAttributeListener.attributeReplaced(
					servletRequestAttributeEvent);
			}
		}
	}

}