/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)LanguageSelectorDlg.java	2.0.0	2012/11/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.launcher;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * FALCON-SEED ランチャー
 * 
 * @version 2.0.0	2012/11/06
 */
public class LanguageSelectorDlg extends JDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String DLG_TITLE	= "Language";
	static protected final String CMB_TITLE	= "Language";
	
	static protected final String[][] _languages = {
		{ "en", "English"},
		{ "ja", "Japanese"},
//		{ "zh", "chinese"},
//		{ "en", "english"},
//		{ "in", "indonesian"},
//		{ "ja", "japanese"},
//		{ "kr", "korean"},
//		{ "es", "spanish"},
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected boolean		_result;
	protected String		_lang;
	private Map<String,String> _language_name_map = new HashMap<String,String>();
	private Map<String,String> _language_map = new TreeMap<String,String>();
	private JComboBox _language_comboBox = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public LanguageSelectorDlg(Dialog owner) {
		super(owner, DLG_TITLE, true);
		commonConstruct();
	}

	public LanguageSelectorDlg(Frame owner) {
		super(owner, DLG_TITLE, true);
		commonConstruct();
	}
	
	private void commonConstruct() {
		for (int i = 0; i < _languages.length; ++i) {
			_language_name_map.put( _languages[ i][ 0], _languages[ i][ 1]);
			_language_map.put( _languages[ i][ 1], _languages[ i][ 0]);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getSelectedLanguage() {
		return _lang;
	}
	
	public boolean doModal(Component component, String defaultLanguage) {
		// setup window listener
		addWindowListener( new WindowListener() {
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				on_cancel(null);
			}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		});
		
		// setup dialog
		setResizable(false);
		getContentPane().setLayout( new BorderLayout());
		
		// create main panel
		JPanel center_panel = new JPanel();
		center_panel.setLayout(new BoxLayout(center_panel, BoxLayout.Y_AXIS));
		insert_horizontal_glue( center_panel );
		//--- setup
		setup( center_panel, defaultLanguage );
		getContentPane().add( center_panel );
		
		JPanel south_panel = new JPanel();
		south_panel.setLayout( new BoxLayout( south_panel, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setLayout( new FlowLayout( FlowLayout.RIGHT, 5, 0));

		insert_horizontal_glue( south_panel);

		setup_ok_and_cancel_button(
			panel,
			"OK",	// ResourceManager.get( "dialog.ok"),
			"Cancel",	//ResourceManager.get( "dialog.cancel"),
			true, true);
		south_panel.add( panel);

		insert_horizontal_glue( south_panel);

		getContentPane().add( south_panel, "South");
		
		pack();
		setLocationRelativeTo( component);
		setVisible( true);

		return _result;
	}
	
	public void link_to_cancel(JComponent component) {
		AbstractAction cancelAction = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent){
				on_cancel( actionEvent);
		  }
		};
		component.getInputMap().put(
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0), "cancel");
		component.getActionMap().put( "cancel", cancelAction);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void on_ok(ActionEvent actionEvent) {
		_lang = _language_map.get(_language_comboBox.getSelectedItem());
		_result = true;
		dispose();
	}

	protected void on_cancel(ActionEvent actionEvent) {
		_result = false;
		dispose();
	}

	public void insert_horizontal_glue() {
		insert_horizontal_glue( getContentPane() );
	}

	public void insert_horizontal_glue(Container container) {
		JPanel glue_panel = new JPanel();
		glue_panel.add( Box.createHorizontalGlue());
		container.add( glue_panel);
	}
	
	private void setup(JPanel parent, String defaultLanguage) {
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS));

		panel.add( Box.createHorizontalStrut( 5));

		JLabel label = new JLabel(CMB_TITLE);
		link_to_cancel( label);
		panel.add( label);

		panel.add( Box.createHorizontalStrut( 5));

		_language_comboBox = new JComboBox( ( String[])_language_map.keySet().toArray( new String[ 0]));
		_language_comboBox.setPreferredSize( new Dimension( 200, _language_comboBox.getPreferredSize().height));
		String lang = _language_name_map.get(defaultLanguage);
		if (lang == null) {
			lang = _language_name_map.get("en");
		}
		_language_comboBox.setSelectedItem(lang);
		link_to_cancel( _language_comboBox);
		panel.add( _language_comboBox);

		panel.add( Box.createHorizontalStrut( 5));

		parent.add( panel);
	}
	
	protected void setup_ok_and_cancel_button(JPanel panel, String ok, String cancel, boolean enter, boolean escape) {
		JButton okButton = new JButton( ok);

		if ( enter)
			getRootPane().setDefaultButton( okButton);

		okButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				on_ok( arg0);
			}
		});

		if ( escape)
			link_to_cancel( okButton);

		panel.add( okButton);


		JButton cancelButton = new JButton( cancel);
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				on_cancel( arg0);
			}
		});

		if ( escape)
			link_to_cancel( cancelButton);

		panel.add( cancelButton);


		int width = Math.max( okButton.getPreferredSize().width, cancelButton.getPreferredSize().width);

		okButton.setPreferredSize( new Dimension( width, okButton.getPreferredSize().height));
		cancelButton.setPreferredSize( new Dimension( width, cancelButton.getPreferredSize().height));
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
