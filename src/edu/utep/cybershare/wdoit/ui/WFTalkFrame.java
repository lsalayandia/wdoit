/* 
Copyright (c) 2005,
The Board of Trustees of the  Leland Stanford Junior University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

Neither the name of Stanford University nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, ANY IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.utep.cybershare.wdoit.ui;

import java.util.ArrayList;
import java.util.Date;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JFrame;

import com.hp.hpl.jena.ontology.OntModel;

import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.ciclient.ciui.CIGetUP;
import edu.utep.cybershare.ciclient.CIGet;
import edu.utep.cybershare.ciclient.CIPut;
import edu.utep.cybershare.ciclient.CIReturnObject;
import edu.utep.cybershare.ciclient.ciconnect.CIKnownServerTable;

public class WFTalkFrame extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CIKnownServerTable servers = CIKnownServerTable.getInstance();

	private static WFTalkFrame wfTalkFrameWindow = null;
	private JButton updateButton;
	private JButton cancelButton;
	private JButton sendButton;
	private JButton setupButton;
	private JPanel mainPanel;
	JTextArea message;
	JTextArea msgContents;
	JTextField name;
	JTextField serverName;
	JTextField wfName;

	public static void showWindow() {
		// first try to connect to a related server
		// NOTE:
		// then look for the workflow
		// if not a valid workflow - give an error message and do NOT
		// show the window
		if (wfTalkFrameWindow == null) {
			wfTalkFrameWindow = new WFTalkFrame();
		}
		wfTalkFrameWindow.setVisible(true);
	}

	protected WFTalkFrame() {
		this.setTitle("WFTalk");
		this.setSize(800, 400);
		// /
		// right side - send message
		JPanel sendPanel = new JPanel();
		sendPanel.setLayout(new BoxLayout(sendPanel, BoxLayout.PAGE_AXIS));

		JLabel topLabel = new JLabel("SEND A MESSAGE");
		topLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel sendSubPanel1 = new JPanel();
		name = new JTextField(20);
		name.setToolTipText("The user name of the sender");
		name.setEditable(false);
		name.setBackground(Color.white);

		JLabel senderLabel = new JLabel("Sender: ");
		senderLabel.setLabelFor(name);
		sendSubPanel1.add(senderLabel);
		sendSubPanel1.add(name);

		message = new JTextArea();
		message.setColumns(50);
		message.setLineWrap(true);
		message.setWrapStyleWord(true);
		message.setEditable(true);
		message.setRows(15);
		message.setLineWrap(true);

		message.setToolTipText("Enter the message to send");
		JLabel messageLabel = new JLabel("Message: ");
		messageLabel.setAlignmentX(CENTER_ALIGNMENT);
		// messageLabel.setLabelFor(message);
		JScrollPane msgScroll = new JScrollPane(message);
		msgScroll.setMinimumSize(new Dimension(300, 180));
		msgScroll.setPreferredSize(new Dimension(300, 180));
		msgScroll.setAlignmentX(CENTER_ALIGNMENT);

		JPanel sButtonPanel = new JPanel();
		setupButton = new JButton("Setttings");
		setupButton.setActionCommand("Settings");
		setupButton.addActionListener(this);
		sendButton = new JButton("Send");
		sendButton.setActionCommand("Send");
		sendButton.addActionListener(this);
		// TODO: add this button to fix login issues
		// sButtonPanel.add(setupButton);
		// setupButton.setFocusable(false);
		sButtonPanel.add(sendButton);
		getRootPane().setDefaultButton(sendButton);

		sendPanel.add(topLabel);
		sendPanel.add(sendSubPanel1);
		sendPanel.add(messageLabel);
		sendPanel.add(msgScroll);
		sendPanel.add(sButtonPanel);
		sendPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		// Left side, view messages

		JPanel messagePanel = new JPanel();
		messagePanel
				.setLayout(new BoxLayout(messagePanel, BoxLayout.PAGE_AXIS));

		msgContents = new JTextArea();
		msgContents.setColumns(50);
		msgContents.setLineWrap(true);
		msgContents.setRows(15);
		msgContents.setWrapStyleWord(true);
		msgContents.setEditable(false);

		JPanel topPanel = new JPanel();
		JPanel topPanels1 = new JPanel();
		JPanel topPanels2 = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));
		JLabel listLabel = new JLabel("Messages:");
		JLabel serverLabel = new JLabel("Server : ");
		JLabel workflowLabel = new JLabel("Workflow:");
		serverName = new JTextField(40);
		wfName = new JTextField(40);
		serverName.setAlignmentX(CENTER_ALIGNMENT);
		wfName.setAlignmentX(CENTER_ALIGNMENT);
		serverLabel.setLabelFor(serverName);
		serverName.setEditable(false);
		workflowLabel.setLabelFor(wfName);
		wfName.setEditable(false);
		serverName.setBackground(Color.white);
		wfName.setBackground(Color.white);
		topPanels1.add(serverLabel);
		topPanels1.add(serverName);
		topPanels2.add(workflowLabel);
		topPanels2.add(wfName);
		listLabel.setAlignmentX(CENTER_ALIGNMENT);
		topPanel.add(listLabel);
		topPanel.add(topPanels1);
		topPanel.add(topPanels2);

		JScrollPane msgListScroll = new JScrollPane(msgContents);
		msgListScroll.setMinimumSize(new Dimension(450, 300));
		msgListScroll.setPreferredSize(new Dimension(450, 300));
		msgListScroll.setAlignmentX(CENTER_ALIGNMENT);

		JPanel bottomBPanel = new JPanel();
		// Create and initialize the buttons.
		updateButton = new JButton("Update");
		updateButton.setActionCommand("Update");
		updateButton.addActionListener(this);
		cancelButton = new JButton("Close");
		cancelButton.setActionCommand("Close");
		cancelButton.addActionListener(this);
		bottomBPanel.add(updateButton);
		bottomBPanel.add(cancelButton);

		messagePanel.add(topPanel);
		messagePanel.add(msgListScroll);
		messagePanel.add(bottomBPanel);
		messagePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				sendPanel, messagePanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(300);
		splitPane.setResizeWeight(1);

		// Create a container so that we can add a title around
		// the scroll pane. Can't add a title directly to the
		// scroll pane because its background would be white.
		// Lay out the label and scroll pane from top to bottom.
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setBackground(Color.lightGray);
		mainPanel.add(splitPane);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// some setup based on the workflow selected
		State state = State.getInstance();
		OntModel wf = state.getSelectedWorkflow();
		String uname = null;
		String wfS = null; // the url of the server
		String wfN = null; // the path of the workflow on the server
		int serverId = -1;
		if (wf != null) {
			String wfURL = state.getOWLDocumentURL(wf);
			serverId = servers.ciGetServerEntryFromURL(wfURL);
			if (serverId > -1) {
				wfS = servers.ciGetServerURL(serverId);
				String stmp[] = wfURL.split(wfS + "/");
				if (stmp[1] != null)
					wfN = stmp[1];
				if (servers.ciIsUsernamePasswordSet(serverId) == false) {
					// try to setup a valid username/password
					if (CIGetUP.showDialog(this, serverId) == false) {
						JOptionPane
								.showMessageDialog(
										this,
										"Username and password needed - command failed!",
										"Error Message",
										JOptionPane.ERROR_MESSAGE);
					} else
						uname = servers.ciGetServerUsername(serverId);
				} else
					uname = servers.ciGetServerUsername(serverId);
			}
		}
		if (uname != null) {
			name.setText(uname);
			wfName.setText(wfN);
			serverName.setText(wfS);
		} else {
			name.setText("");
			wfName.setText("");
			serverName.setText("");
		}

		this.setContentPane(mainPanel);

		pack();

	}

	// Handle clicks on the Set and Cancel buttons.
	public void actionPerformed(ActionEvent e) {
		if ("Close".equals(e.getActionCommand())) {
			setVisible(false);
		} else if ("Send".equals(e.getActionCommand())) {
			String msg = message.getText();
			String sender = name.getText();
			Date now = new Date();
			msg = "SENT BY: " + sender + ".  ON: " + now + ".\n\t " + msg;
			if (sendMessage(msg)) {
				message.setText("");
				msgContents.append("YOU JUST SENT => " + msg);
			} else {
				JOptionPane.showMessageDialog(this,
						"Message could not be sent.", "Error Message",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			// run update
		} else if ("Update".equals(e.getActionCommand())) {
			updateMessageList();
		}
		if ("Settings".equals(e.getActionCommand())) {
			State state = State.getInstance();
			OntModel wf = state.getSelectedWorkflow();
			if (wf != null) {
				String wfURL = state.getOWLDocumentURL(wf);
				int serverId = servers.ciGetServerEntryFromURL(wfURL);
				if (serverId == -1) {
					JOptionPane.showMessageDialog(this,
							"Workflow not associated with a known CI Server",
							"Error Message", JOptionPane.ERROR_MESSAGE);
					name.setText("");
					msgContents.setText("");
					wfName.setText("");
					serverName.setText("");
				} else {
					if (CIGetUP.showDialog(this, serverId) == false) {
						JOptionPane
								.showMessageDialog(
										this,
										"Username and password needed - command failed!",
										"Error Message",
										JOptionPane.ERROR_MESSAGE);
						name.setText("");
						msgContents.setText("");
					}
				}
			}
		}

	}

	private void updateMessageList() {
		State state = State.getInstance();
		OntModel wf = state.getSelectedWorkflow();
		if (wf != null) {
			String wfURL = state.getOWLDocumentURL(wf);
			int serverId = servers.ciGetServerEntryFromURL(wfURL);
			if (serverId != -1) {
				// assure password is setup
				if (servers.ciIsUsernamePasswordSet(serverId) == false) {
					// try to setup a valid username/password
					if (CIGetUP.showDialog(this, serverId) == false) {
						JOptionPane
								.showMessageDialog(
										this,
										"Username and password needed - command failed!",
										"Error Message",
										JOptionPane.ERROR_MESSAGE);
						msgContents.setText("");
						name.setText("");
						return;
					}
				}
				// valid login for this server - proceed with update
				name.setText(servers.ciGetServerUsername(serverId));
				// valid login for this message, proceed with getting the
				// messages
				// for the url on the server
				CIReturnObject messageObject = CIGet.ciGetMessageList(wfURL);
				if (messageObject.gStatus.equals("-1")) {
					msgContents
							.setText("Could not obtain a message list from server.");
				} else {
					ArrayList<String> messagelist = messageObject.gMessageList;
					if (messagelist == null || messagelist.size() == 0) {
						msgContents
								.setText("no messages on server for this workflow");
					} else {
						String allMessages = "";
						int farIndex = messagelist.size();
						// want the reverse order of how the messages are
						// retrieved - for now. Returned blog style and
						// text area does not work like that
						for (int index = farIndex - 1; index >= 0; index--) {
							String oneMsg = messagelist.get(index) + "\n";
							allMessages += oneMsg;
						}
						msgContents.setText(allMessages);
					}
					String wfS;
					String wfN;
					wfS = servers.ciGetServerURL(serverId);
					serverName.setText(wfS);
					String stmp[] = wfURL.split(wfS + "/");
					if (stmp[1] != null) {
						wfN = stmp[1];
						wfName.setText(wfN);
					}
				}
			} else {
				msgContents.setText("");
				name.setText("");
			}
		} else {
			msgContents.setText("");
			name.setText("");
		}
	}

	private boolean sendMessage(String msg) {
		boolean returnValue = false;

		State state = State.getInstance();
		OntModel wf = state.getSelectedWorkflow();
		if (wf != null) {
			String wfURL = state.getOWLDocumentURL(wf);
			int serverId = servers.ciGetServerEntryFromURL(wfURL);
			if (serverId > -1) {
				// assure password is setup
				if (servers.ciGetServerAuthSession(serverId) == null) {
					if (CIGetUP.showDialog(this, serverId) == false) {
						JOptionPane.showMessageDialog(this,
								"Could not authenticate with server!",
								"Error Message", JOptionPane.ERROR_MESSAGE);
						msgContents.setText("");
						name.setText("");
						return returnValue;
					}
				}
				// valid login for this server - proceed with sending
				name.setText(servers.ciGetServerUsername(serverId));
				// message for this url on the server
				CIReturnObject ro = CIPut.ciSendComment(wfURL, msg);
				if (ro.gStatus.equals("-1"))
					return false;
				else
					returnValue = true;
				String wfS;
				String wfN;
				wfS = servers.ciGetServerURL(serverId);
				serverName.setText(wfS);
				String stmp[] = wfURL.split(wfS + "/");
				if (stmp[1] != null) {
					wfN = stmp[1];
					wfName.setText(wfN);
				}
			} else {
				msgContents.setText("");
				name.setText("");
			}
		} else {
			msgContents.setText("");
			name.setText("");
		}
		// only reach here if no workflow selected
		return returnValue;
	}
}
