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
package org.eclipse.equinox.security.tests;

import org.eclipse.equinox.internal.security.tests.storage.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * As tests use registry listeners, UI listeners might cause tests to time out and fail.
 * As such this tests should be run in a headless mode.
 */
@RunWith(Suite.class)
@SuiteClasses({Base64Test.class, SlashEncodeTest.class, DefaultPreferencesTest.class, DynamicPreferencesTest.class, WinPreferencesTest.class})
public class AllSecurityTests {
	//see @SuiteClasses
}
