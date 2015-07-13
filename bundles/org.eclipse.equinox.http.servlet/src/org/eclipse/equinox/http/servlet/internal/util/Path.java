/*******************************************************************************
 * Copyright (c) Jul 3, 2015 Liferay, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liferay, Inc. - initial API and implementation and/or initial
 *                    documentation
 ******************************************************************************/

package org.eclipse.equinox.http.servlet.internal.util;

/**
 * @author Raymond Augé
 */
public class Path {

	public static String findExtension(String path) {
		String lastSegment = path.substring(path.lastIndexOf('/') + 1);

		int dot = lastSegment.lastIndexOf('.');

		if (dot == -1) {
			return null;
		}

		return lastSegment.substring(dot + 1);
	}

}