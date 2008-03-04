/*******************************************************************************
 * 
 * Copyright (c) 2007 Thomas Holland (thomas@innot.de) and others
 * 
 * This program and the accompanying materials are made
 * available under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Thomas Holland - initial API and implementation
 *     
 * $Id$
 *     
 *******************************************************************************/
package de.innot.avreclipse.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.innot.avreclipse.core.preferences.AVRDudePreferences;

/**
 * AVRDude Preference page of the AVR Eclipse plugin.
 * 
 * <p>
 * This page contains an option to select an avrdude configuration file and
 * manages the list of known Programmer configurations.
 * </p>
 * <p>
 * The list of Programmers configurations is actually handled by the
 * {@link ProgConfigListFieldEditor} class, while editing is done in a separate
 * {@link ProgConfigEditDialog}.
 * </p>
 */

public class AVRDudePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private IPreferenceStore fPreferenceStore = null;

	private FileFieldEditor fFileEditor;
	private ProgConfigListFieldEditor fConfigEditor;

	public AVRDudePreferencePage() {
		super(GRID);

		// Get the instance scope avrdude preference store
		fPreferenceStore = AVRDudePreferences.getPreferenceStore();
		setPreferenceStore(fPreferenceStore);
		setDescription("AVRDude Global Settings");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		final Composite parent = getFieldEditorParent();

		// Add the Configuration file settings
		MyBooleanFieldEditor usecustomconfig = new MyBooleanFieldEditor(
				AVRDudePreferences.KEY_USECUSTOMCONFIG,
				"Use custom configuration file for AVRDude", parent);
		addField(usecustomconfig);

		fFileEditor = new FileFieldEditor(AVRDudePreferences.KEY_CONFIGFILE,
				"AVRDude config file", parent);
		addField(fFileEditor);

		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		separator.setLayoutData(data);

		fConfigEditor = new ProgConfigListFieldEditor(
				"Programmer configurations",
				parent);
		addField(fConfigEditor);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		// nothing to init
	}

	/**
	 * Extends the BooleanFieldEditor to set the enabled state of the custom
	 * config file FieldEditor depending on the value of this FieldEditor.
	 */
	private class MyBooleanFieldEditor extends BooleanFieldEditor {
		// This class is required, because BooleanFieldEditor does not grant
		// access to its Checkbox control (to add a listener) nor does the
		// addSelectionListener method work (the single listener is overwritten
		// by the FieldEditorPreferencePage)
		
		// So we override the some methods to always be informed about any state changes.

		private Composite fParent;

		/*
		 * @see org.eclipse.jface.preference.BooleanFieldEditor(String name, String label, Composite parent)
		 */
		public MyBooleanFieldEditor(String name, String label, Composite parent) {
			super(name, label, parent);
			fParent = parent;
		}

		@Override
		protected void valueChanged(boolean oldValue, boolean newValue) {
			super.valueChanged(oldValue, newValue);
			enableConfigFileEditor(newValue);
		}
		
		@Override
		protected void doLoad() {
			super.doLoad();
			enableConfigFileEditor(getBooleanValue());
		}

		@Override
		protected void doLoadDefault() {
			super.doLoadDefault();
			enableConfigFileEditor(getBooleanValue());
		}

		private void enableConfigFileEditor(boolean newValue) {
			if (fFileEditor != null) {
				if (newValue) {
					fFileEditor.setEnabled(true, fParent);
				} else {
					fFileEditor.setEnabled(false, fParent);
				}
			}
		}
	}

}