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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.equinox.compendium.tests.Activator;
import org.eclipse.osgi.tests.bundles.BundleInstaller;
import org.junit.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.*;

public class NoADTest {
	private Bundle bundle;
	private BundleInstaller bundleInstaller;
	private MetaTypeService metaType;
	private ServiceReference<MetaTypeService> metaTypeReference;

	@Before
	public void setUp() throws Exception {
		Activator.getBundle(Activator.BUNDLE_METATYPE).start();
		metaTypeReference = Activator.getBundleContext().getServiceReference(MetaTypeService.class);
		metaType = Activator.getBundleContext().getService(metaTypeReference);
		bundleInstaller = new BundleInstaller("bundle_tests/metatype", Activator.getBundleContext()); //$NON-NLS-1$
		bundleInstaller.refreshPackages(null);
		bundle = bundleInstaller.installBundle("tb10"); //$NON-NLS-1$
		bundle.start();
	}

	@After
	public void tearDown() throws Exception {
		bundleInstaller.shutdown();
		Activator.getBundleContext().ungetService(metaTypeReference);
		Activator.getBundle(Activator.BUNDLE_METATYPE).stop();
	}

	/*
	 * Ensures an OCD can exist with no ADs
	 */
	@Test
	public void testNoADs() {
		MetaTypeInformation mti = metaType.getMetaTypeInformation(bundle);
		String[] pids = mti.getPids();
		assertNotNull("The pid was not present.", pids); //$NON-NLS-1$
		assertEquals("Not the expected number of pids.", 1, pids.length); //$NON-NLS-1$
		assertEquals("Expected pid was not present.", "no.ad.designate", pids[0]); //$NON-NLS-1$ //$NON-NLS-2$
		ObjectClassDefinition ocd = mti.getObjectClassDefinition(pids[0], null);
		AttributeDefinition[] ads = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
		// seems we are not really spec compliant here
		if (ads != null) {
			// should really be null, but if not then make sure it is an empty array
			assertEquals("Found some ads: " + ads, 0, ads.length); //$NON-NLS-1$
		}
	}
}
