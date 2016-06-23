/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.security.tests.storage;

import java.util.Map;
import org.eclipse.equinox.internal.security.tests.SecurityTestsActivator;
import org.junit.Before;
import org.osgi.framework.*;

/**
 * Tests Windows module, if available.
 *
 */
public class WinPreferencesTest extends SecurePreferencesTest {

	final private static String WIN_BUNDLE = "org.eclipse.equinox.security.win32.x86";
	final private static String WIN_64BIT_BUNDLE = "org.eclipse.equinox.security.win32.x86_64";
	private boolean is64Bit = "x86-64".equals(SecurityTestsActivator.getDefault().getBundleContext().getProperty(Constants.FRAMEWORK_PROCESSOR));

	@Before
	public void setUp() {
		org.junit.Assume.assumeTrue(hasBundle(is64Bit ? WIN_64BIT_BUNDLE : WIN_BUNDLE));
	}

	/**
	 * Unique ID of the Windows module.
	 */
	static private final String WIN_MODULE_ID = "org.eclipse.equinox.security.WindowsPasswordProvider"; //$NON-NLS-1$
	static private final String WIN_64BIT_MODULE_ID = "org.eclipse.equinox.security.WindowsPasswordProvider64bit"; //$NON-NLS-1$

	protected String getModuleID() {
		return is64Bit ? WIN_64BIT_MODULE_ID : WIN_MODULE_ID;
	}

	protected Map<String, Object> getOptions() {
		// Don't specify default password when testing specific password provider
		return getOptions(null);
	}

	static private boolean hasBundle(String symbolicID) {
		BundleContext context = SecurityTestsActivator.getDefault().getBundleContext();
		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			String bundleName = bundles[i].getSymbolicName();
			if (!symbolicID.equals(bundleName))
				continue;
			int bundleState = bundles[i].getState();
			return (bundleState != Bundle.INSTALLED) && (bundleState != Bundle.UNINSTALLED);
		}
		return false;
	}
}
