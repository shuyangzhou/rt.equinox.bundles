/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.equinox.bidi.tests;

import org.eclipse.equinox.bidi.internal.tests.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({StructuredTextExtensibilityTest.class, StructuredTextMethodsTest.class, StructuredTextFullToLeanTest.class, StructuredTextExtensionsTest.class, StructuredTextMathTest.class, StructuredTextSomeMoreTest.class, StructuredTextProcessorTest.class, StructuredTextStringRecordTest.class})
public class StructuredTextTestSuite {
	//intentionally left blank
}