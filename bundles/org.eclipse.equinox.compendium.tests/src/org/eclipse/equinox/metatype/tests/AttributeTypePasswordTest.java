/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.metatype.tests;

import org.junit.*;
import org.osgi.framework.Bundle;
import org.osgi.service.metatype.*;

public class AttributeTypePasswordTest extends AbstractTest {
	private Bundle bundle;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		bundle = bundleInstaller.installBundle("tb1"); //$NON-NLS-1$
		bundle.start();
	}

	@After
	public void tearDown() throws Exception {
		bundle.stop();
		super.tearDown();
	}

	/*
	 * Ensures the PASSWORD type is recognized.
	 */
	@Test
	public void testAttributeTypePassword1() {
		MetaTypeInformation mti = metatype.getMetaTypeInformation(bundle);
		ObjectClassDefinition ocd = mti.getObjectClassDefinition("org.eclipse.equinox.metatype.tests.tb1", null); //$NON-NLS-1$
		AttributeDefinition[] ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
		for (int i = 0; i < ads.length; i++) {
			if (ads[i].getID().equals("password1")) { //$NON-NLS-1$
				Assert.assertEquals("Attribute type is not PASSWORD", AttributeDefinition.PASSWORD, ads[i].getType()); //$NON-NLS-1$
			}
		}
	}

	/*
	 * Ensures the PASSWORD type is treated the same as the STRING type.
	 */
	@Test
	public void testAttributeTypePassword2() {
		MetaTypeInformation mti = metatype.getMetaTypeInformation(bundle);
		ObjectClassDefinition ocd = mti.getObjectClassDefinition("org.eclipse.equinox.metatype.tests.tb1", null); //$NON-NLS-1$
		AttributeDefinition[] ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
		AttributeDefinition ad = findAttributeDefinitionById("password1", ads); //$NON-NLS-1$
		Assert.assertNotNull("Attribute definition not found", ad); //$NON-NLS-1$
		assertValidationPass("1234abcd", ad); //$NON-NLS-1$
	}

	/*
	 * Ensures the PASSWORD type is treated the same as the STRING type.
	 */
	@Test
	public void testAttributeTypePassword3() {
		MetaTypeInformation mti = metatype.getMetaTypeInformation(bundle);
		ObjectClassDefinition ocd = mti.getObjectClassDefinition("org.eclipse.equinox.metatype.tests.tb1", null); //$NON-NLS-1$
		AttributeDefinition[] ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
		AttributeDefinition ad = findAttributeDefinitionById("password2", ads); //$NON-NLS-1$
		assertValidationPass("password", ad); //$NON-NLS-1$
		assertValidationFail("1234abcd", ad); //$NON-NLS-1$
	}

	/*
	 * Ensures the PASSWORD type is treated the same as the STRING type.
	 * PASSWORD length should be no less than min.
	 */
	@Test
	public void testAttributeTypePassword4() {
		MetaTypeInformation mti = metatype.getMetaTypeInformation(bundle);
		ObjectClassDefinition ocd = mti.getObjectClassDefinition("org.eclipse.equinox.metatype.tests.tb1", null); //$NON-NLS-1$
		AttributeDefinition[] ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
		AttributeDefinition ad = findAttributeDefinitionById("password3", ads); //$NON-NLS-1$
		assertValidationPass("12345678", ad); //$NON-NLS-1$
		assertValidationPass("123456789", ad); //$NON-NLS-1$
		assertValidationFail("1234567", ad); //$NON-NLS-1$
	}

	/*
	 * Ensures the PASSWORD type is treated the same as the STRING type.
	 * PASSWORD length should be no greater than max.
	 */
	@Test
	public void testAttributeTypePassword5() {
		MetaTypeInformation mti = metatype.getMetaTypeInformation(bundle);
		ObjectClassDefinition ocd = mti.getObjectClassDefinition("org.eclipse.equinox.metatype.tests.tb1", null); //$NON-NLS-1$
		AttributeDefinition[] ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
		AttributeDefinition ad = findAttributeDefinitionById("password4", ads); //$NON-NLS-1$
		assertValidationPass("12345", ad); //$NON-NLS-1$
		assertValidationPass("1234", ad); //$NON-NLS-1$
		assertValidationFail("123456", ad); //$NON-NLS-1$
	}

	/*
	 * Ensures the PASSWORD type is treated the same as the STRING type.
	 * PASSWORD length should be no less than min and no greater than max.
	 */
	@Test
	public void testAttributeTypePassword6() {
		MetaTypeInformation mti = metatype.getMetaTypeInformation(bundle);
		ObjectClassDefinition ocd = mti.getObjectClassDefinition("org.eclipse.equinox.metatype.tests.tb1", null); //$NON-NLS-1$
		AttributeDefinition[] ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
		AttributeDefinition ad = findAttributeDefinitionById("password5", ads); //$NON-NLS-1$
		assertValidationPass("123", ad); //$NON-NLS-1$
		assertValidationFail("12", ad); //$NON-NLS-1$
		assertValidationPass("123456", ad); //$NON-NLS-1$
		assertValidationFail("1234567", ad); //$NON-NLS-1$
	}
}
