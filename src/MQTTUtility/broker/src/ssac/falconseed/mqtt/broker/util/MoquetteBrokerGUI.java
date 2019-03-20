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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MoquetteBrokerGUI.java	0.3.2	2013/10/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MoquetteBrokerGUI.java	0.3.1	2013/07/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MoquetteBrokerGUI.java	0.3.0	2013/06/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MoquetteBrokerGUI.java	0.1.0	2013/05/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mqtt.broker.util;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.falconseed.mqtt.broker.MoquetteBrokerMain;

/**
 * <code>Moquette Broker</code> の <code>GUI</code>。
 * 
 * @version 0.3.2	2013/10/22
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MoquetteBrokerGUI extends JFrame implements ActionListener
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final int	MAX_CONSOLELINE	= 100000;
	static private final int	TIMER_INTERVAL	= 50;

	static private final String CMD_COPY	= "Copy";
	static private final String CMD_CLEAR	= "Clear";
	static private final String CMD_STOP	= "Stop";
	static private final String CMD_START	= "Start";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final CommandOutput	_stringQueue;
	
	private volatile MoquetteServer	_server = null;
	
	private JLabel		_lblServerAddr;
	private JLabel		_lblServerPort;

	private JCheckBox	_chkShowLog;
	private JButton	_btnCopy;
	private JButton	_btnClear;
	private JButton	_btnStop;
	private JButton	_btnStart;
	
	private Timer	_timer;
	private ConsoleTextPane	_console;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MoquetteBrokerGUI() {
		super(MoquetteBrokerMain.APP_NAME);
		
		// setup log appender
		ConsoleMonitorAppender.setOutputQueueing(true);
		ConsoleMonitorAppender.setOutputWriteToStream(false);
		_stringQueue = ConsoleMonitorAppender.getQueue();
		
		// initialize
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		init();
		
		// update Server socket address
		{
			InetSocketAddress saddr = MoquetteServer.getDefaultSocketAddress();
			InetAddress iaddr = saddr.getAddress();
			if (iaddr.isAnyLocalAddress()) {
				try {
					iaddr = InetAddress.getLocalHost();
				} catch (Throwable ignoreEx) {}
			}
			_lblServerAddr.setText(iaddr.getHostAddress());
			_lblServerPort.setText(String.valueOf(saddr.getPort()));
		}
		
		// create timer
		_timer = new Timer(TIMER_INTERVAL, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onTimer(ae);
			}
		});
		_timer.setCoalesce(true);

		// add window listener
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);	// exit application
			}

			@Override
			public void windowOpened(WindowEvent e) {
				startBroker();
				//--- フォーカスの移動
				_btnStart.transferFocus();
			}
		});
		
		// add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				MoquetteServer server = _server;
				if (server != null) {
					server.stopServer();
				}
			}
		});
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void startBroker() {
		if (_server != null) {
			return;		// already running
		}
		
		_btnStart.setEnabled(false);
		_console.clear();
		
		// start broker
		MoquetteServer server = null;
		try {
			server = new MoquetteServer();
			server.startServer();
		}
		catch (Throwable ex) {
			server = null;
			StringBuilder sb = new StringBuilder();
			sb.append("Failed to start Moquette Broker!\n");
			sb.append("(cause) ");
			sb.append(ex);
			JOptionPane.showMessageDialog(this, sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			_btnStart.setEnabled(true);
			return;
		}
		_server = server;
		
		// show queueing output
		showQueueingOutput();
		
		// update
		updateButtons();
		
		// start timer
		_timer.start();
	}
	
	public void stopBroker() {
		MoquetteServer server = _server;
		_server = null;
		
		if (server == null) {
			return;		// already stopped
		}
		
		_btnStop.setEnabled(false);
		
		// stop timer
		_timer.stop();
		
		// stop broker
		server.stopServer();
		
		// show queueing output
		showQueueingOutput();
		
		// update
		updateButtons();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void onTimer(ActionEvent ae) {
		// 出力更新
		long stime = System.currentTimeMillis();
		long etime = 0L;
		OutputString ostr;
		while (((ostr = _stringQueue.pop()) != null) && etime < TIMER_INTERVAL) {
			if (ostr.isError()) {
				//--- stderr
				_console.appendErrorString(ostr.getString());
			} else {
				//--- stdout
				_console.appendOutputString(ostr.getString());
			}
			etime = System.currentTimeMillis() - stime;
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String cmd = ae.getActionCommand();
		
		if (CMD_CLEAR.equals(cmd)) {
			// [Clear]
			_console.clear();
		}
		else if (CMD_STOP.equals(cmd)) {
			// [Stop]
			stopBroker();
		}
		else if (CMD_START.equals(cmd)) {
			// [Start]
			startBroker();
		}
		else if (CMD_COPY.equals(cmd)) {
			_console.selectAll();
			_console.copy();
			_console.setCaretPosition(_console.getDocument().getLength());
		}
	}

	/**
	 * [Show log] チェックボックスの状態変更イベント
	 * @since 0.3.2
	 */
	protected void onChangedShowLogCheckBox(ChangeEvent ce) {
		if (_chkShowLog.isSelected()) {
			// コンソールにログを表示
			ConsoleMonitorAppender.setOutputQueueing(true);
		} else {
			// ログを非表示
			ConsoleMonitorAppender.setOutputQueueing(false);
		}
	}

	/**
	 * キュー内の出力文字列をコンソールに出力
	 */
	protected void showQueueingOutput() {
		OutputString ostr;
		while ((ostr = _stringQueue.pop()) != null) {
			if (ostr.isError()) {
				//--- stderr
				_console.appendErrorString(ostr.getString());
			} else {
				//--- stdout
				_console.appendOutputString(ostr.getString());
			}
		}
	}
	
	protected void updateButtons()
	{
		if (_server != null) {
			// server started
			_btnStart.setEnabled(false);
			_btnStop .setEnabled(true);
		} else {
			// server stopped
			_btnStart.setEnabled(true);
			_btnStop .setEnabled(false);
		}
	}

	protected void init()
	{
		// create main panel
		JPanel pnl = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		JComponent cmp;
		
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		// broker buttons
		cmp = createBrokerPanel();
		pnl.add(cmp, gbc);
		gbc.gridy++;
		
		// console buttons
		_btnCopy = new JButton("Copy to Clipboard");
		_btnCopy.setActionCommand(CMD_COPY);
		_btnCopy.addActionListener(this);
		
		_btnClear = new JButton("Clear");
		_btnClear.setActionCommand(CMD_CLEAR);
		_btnClear.addActionListener(this);
		
		_chkShowLog = new JCheckBox("Show log");
		_chkShowLog.setSelected(true);
		_chkShowLog.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				onChangedShowLogCheckBox(e);
			}
		});
		
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("Console:"));
		box.add(Box.createHorizontalGlue());
		box.add(_chkShowLog);
		box.add(Box.createHorizontalStrut(5));
		box.add(_btnCopy);
		box.add(Box.createHorizontalStrut(5));
		box.add(_btnClear);
		
		pnl.add(box, gbc);
		gbc.gridy++;
		
		// console text area
		_console = new ConsoleTextPane();
		_console.setLineWrap(true);		// 右端で折り返す
		_console.setLineLengthLimit(0);	// 1行の文字数は制限しない
		_console.setLineCountLimit(MAX_CONSOLELINE);	// 表示行数を制限する
		
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sc.setViewportView(_console);
		
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		pnl.add(sc, gbc);
		
		// set content
		getContentPane().add(pnl, BorderLayout.CENTER);
		updateButtons();
	}
	
	private JComponent createBrokerPanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(1, 1, 1, 1),
				BorderFactory.createEtchedBorder()
		));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3,3,3,3);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		// machine info
		Box info = Box.createHorizontalBox();
		_lblServerAddr = createStaticLabel("192.168.0.1");
		_lblServerPort = createStaticLabel("1883");
		info.add(Box.createHorizontalGlue());
		info.add(new JLabel("Broker TCP/IP address : "));
		info.add(_lblServerAddr);
		info.add(Box.createHorizontalStrut(5));
		info.add(new JLabel("Port No : "));
		info.add(_lblServerPort);
		info.add(Box.createHorizontalGlue());
		pnl.add(info, gbc);
		gbc.gridy++;
		
		// buttons
		_btnStart = new JButton("Start");
		_btnStart.setActionCommand(CMD_START);
		_btnStart.addActionListener(this);
		_btnStop = new JButton("Stop");
		_btnStop.setActionCommand(CMD_STOP);
		_btnStop.addActionListener(this);
		Box buttons = Box.createHorizontalBox();
		buttons.add(Box.createHorizontalGlue());
		buttons.add(_btnStart);
		buttons.add(Box.createHorizontalStrut(10));
		buttons.add(_btnStop);
		buttons.add(Box.createHorizontalGlue());
		pnl.add(buttons, gbc);
		
		return pnl;
	}
	
	private JLabel createStaticLabel(String text) {
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createBevelBorder(BevelBorder.LOWERED),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)
		));
		return label;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
