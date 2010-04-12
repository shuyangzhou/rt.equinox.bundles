/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.equinox.bidi.internal.complexp.consumable;

import org.eclipse.equinox.bidi.complexp.IComplExpProcessor;
import org.eclipse.equinox.bidi.internal.complexp.ComplExpBasic;

/**
 *  <code>ComplExpJava</code> is a processor for complex expressions
 *  composed of Java statements. Such a complex expression may span
 *  multiple lines.
 *  <p>
 *  In applications like an editor where parts of the text might be modified
 *  while other parts are not, the user may want to call
 *  {@link IComplExpProcessor#leanToFullText leanToFullText}
 *  separately on each line and save the initial state of each line (this is
 *  the final state of the previous line which can be retrieved using
 *  {@link IComplExpProcessor#getFinalState getFinalState}. If both the content
 *  of a line and its initial state have not changed, the user can be sure that
 *  the last <i>full</i> text computed for this line has not changed either.
 *
 *  @see IComplExpProcessor
 *  @author Matitiahu Allouche
 */
public class ComplExpJava extends ComplExpBasic {
	private static final byte WS = Character.DIRECTIONALITY_WHITESPACE;
	static final String operators = "[](){}.+-<>=~!&*/%^|?:,;\t"; //$NON-NLS-1$
	static String lineSep;

	/**
	 *  Constructor for a complex expressions processor with support for
	 *  Java statements.
	 */
	public ComplExpJava() {
		super(operators, 4);
		// TBD use bundle properties
		if (lineSep == null)
			lineSep = System.getProperty("line.separator", "\n"); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 *  This method is not supposed to be invoked directly by users of this
	 *  class. It may  be overridden by subclasses of this class.
	 */
	protected int indexOfSpecial(int whichSpecial, String srcText, int fromIndex) {
		switch (whichSpecial) {
			case 0 : /* space */
				return srcText.indexOf(' ', fromIndex);
			case 1 : /* literal */
				return srcText.indexOf('"', fromIndex);
			case 2 : /* slash-aster comment */
				return srcText.indexOf("/*", fromIndex); //$NON-NLS-1$
			case 3 : /* slash-slash comment */
				return srcText.indexOf("//", fromIndex); //$NON-NLS-1$
		}
		// we should never get here
		return -1;
	}

	/**
	 *  This method is not supposed to be invoked directly by users of this
	 *  class. It may  be overridden by subclasses of this class.
	 */
	protected int processSpecial(int whichSpecial, String srcText, int operLocation) {
		int loc, cnt, i;

		processOperator(operLocation);
		switch (whichSpecial) {
			case 0 : /* space */
				operLocation++;
				while (operLocation < srcText.length() && srcText.charAt(operLocation) == ' ') {
					setDirProp(operLocation, WS);
					operLocation++;
				}
				return operLocation;
			case 1 : /* literal */
				loc = operLocation + 1;
				while (true) {
					loc = srcText.indexOf('"', loc);
					if (loc < 0)
						return srcText.length();
					for (cnt = 0, i = loc - 1; srcText.charAt(i) == '\\'; i--) {
						cnt++;
					}
					loc++;
					if ((cnt & 1) == 0)
						return loc;
				}
			case 2 : /* slash-aster comment */
				if (operLocation < 0)
					loc = 0; // initial state from previous line
				else
					loc = operLocation + 2; // skip the opening slash-aster
				loc = srcText.indexOf("*/", loc); //$NON-NLS-1$
				if (loc < 0) {
					state = 2;
					return srcText.length();
				}
				return loc + 2;
			case 3 : /* slash-slash comment */
				loc = srcText.indexOf(lineSep, operLocation + 2);
				if (loc < 0)
					return srcText.length();
				return loc + lineSep.length();
		}
		// we should never get here
		return operLocation + 1;
	}
}