/**
Copyright (c) 2012, University of Texas at El Paso
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
DAMAGE.
 */

package edu.utep.cybershare.util;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.URIUtil;

public class AlfrescoClient extends JDialog {
	// static vars
	private static final long serialVersionUID = 1L;
	private static final String PATH_SERVICE = "/service/";
	private static final int MAX_LOGIN_ATTEMPTS = 3;
	private static final String SLASH = "/";
	private static final String QUESTION = "?";
	private static final String EQUALS = "=";
	private static final String AMPERSAND = "&";
	
	private HttpConnectionManager connectionManager;
	private String username;
	private String password;
	private String project;
	private static String alfrescoBaseUrl;
	private boolean loggedIn;
	private boolean loginCanceled;
	
	//Directory where all deriva internal files get saved
	public static final String PREFERENCE_FOLDER_NAME = ".Wdoit";
	public static final String PROP_FILE_NAME = "auth.properties";
	public static final String PROP_SERVER = "server";
	public static final String PROP_USERNAME = "username";
	public static final String PROP_PROJECT = "project";
	
	// GUI variables declaration - do not modify
	private javax.swing.JLabel Label;
	private javax.swing.JLabel Label2;
	private javax.swing.JButton cancelButton;
	private javax.swing.JLabel passwordLabel;
	private javax.swing.JPasswordField passwordText;
	private javax.swing.JLabel projectLabel;
	private javax.swing.JTextField projectText;
	private javax.swing.JSeparator separator;
	private javax.swing.JLabel serverLabel;
	private javax.swing.JTextField serverText;
	private javax.swing.JButton submitButton;
	private javax.swing.JLabel usernameLabel;
	private javax.swing.JTextField usernameText;
	// End of variables declaration
	
	/**
	 * Initialize class variables.
	 */
	private void initVariables() {
		username = null;
		password = null;
		project = null;
		alfrescoBaseUrl = null;
		loggedIn = false;
		
		// Use the example from CommonsHTTPSender - we need to make sure connections are freed properly
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setDefaultMaxConnectionsPerHost(5);
		connectionManager.getParams().setMaxTotalConnections(5);
		connectionManager.getParams().setConnectionTimeout(8000);
		
		this.setLocationRelativeTo(this.getOwner());
		this.setModal(true);
	}

	/**
	 * Constructor: initialize GUI with location relative to calling frame.
	 * @param parent
	 */
	public AlfrescoClient(Frame parent) {
		super(parent);
		initVariables();
		initComponents();
		restoreState();
	}
	
	/**
	 * Upload a given file to an already created node in Alfresco
	 */
	public boolean updateFile(String nodeURL, File file) {
		if (!attemptLogin()) {
			return false;
		}

		PostMethod postMethod = new PostMethod();
		StringBuilder uploadFileURL = getDerivAWebScriptUrl();
		uploadFileURL.append("fileUploader");
		String[] uuid = nodeURL.split("/");
		appendParameter(uploadFileURL, "uuid", uuid[uuid.length-2]);

		try {
			String encodedUrl = encode(uploadFileURL);
			String charset = postMethod.getParams().getUriCharset();
			URI uri = new URI(encodedUrl, true, charset);

			postMethod.setURI(uri);
			postMethod.setContentChunked(true);
			FileRequestEntity fre = new FileRequestEntity(file, null);
			postMethod.setRequestEntity(fre);

			executeMethod(postMethod);
			return true;

		} catch (IOException e) {
			throw new RuntimeException(e);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();
		}   
		return false;
	}
	
	/**
	 * create a blank node and return the URI in order to 
	 * later add content to it.
	 * @return URI of node
	 */
	public String createNode() {
		if (!attemptLogin()) {
			return null;
		}
		
		String filename = null;
		boolean filenameExists;
		do {
			filename = (String) JOptionPane.showInputDialog(this.getFocusOwner(),
					"Name of new Ontology?",
					"New Ontology",
					JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (filename == null) {
				return null;
			}
			
			filenameExists = getObjectUuid("Projects/" + project + "/" + filename) != null;
			if (filenameExists) {
				JOptionPane.showMessageDialog(this.getFocusOwner(), "File already exists. Please choose another name or cancel.");	
			}
		} while (filenameExists);
		
		StringBuilder createNodeURL = getDerivAWebScriptUrl();
		createNodeURL.append("createNode");
		appendParameter(createNodeURL, "project", project);
		appendParameter(createNodeURL, "filename", filename);	

		GetMethod getMethod = new GetMethod();

		try {
			String encodedUrl = encode(createNodeURL);
			String charset = getMethod.getParams().getUriCharset();
			URI uri = new URI(encodedUrl, true, charset);

			getMethod.setURI(uri);
			executeMethod(getMethod);

			return alfrescoBaseUrl + "/d/a/workspace/SpacesStore/" + getObjectUuid("Projects/" + project + "/" + filename) + "/" + filename;
//			return getMethod.getResponseBodyAsString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			getMethod.releaseConnection();
		} 
	}
	
	private boolean attemptLogin() {
		int intents = 0;
		loginCanceled = false;
		while (!loggedIn && intents < MAX_LOGIN_ATTEMPTS && !loginCanceled) {		
			this.setVisible(true);
			intents++;
		}
		return loggedIn;
	}
	
	/**
	 * Cancel button pressed
	 * @param evt
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		loginCanceled = true;
		setVisible(false);
	}

	/**
	 * OK button pressed
	 * Start login process
	 * @param evt
	 */
	private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {
		setBaseUrl(serverText.getText());
		project = projectText.getText();
		username = usernameText.getText();
		char[] tempPassword = passwordText.getPassword();
		password = "";
		for(int i = 0; i < tempPassword.length; i++) {
			password = password + tempPassword[i];
			tempPassword[i] = 0;
		}

		// Try to access webdav URL to see if credentials are valid
		StringBuilder url = getWebdavUrl();
		GetMethod getMethod = new GetMethod();

		try {
			String encodedUrl = encode(url);
			String charset = getMethod.getParams().getUriCharset();
			URI uri = new URI(encodedUrl, true, charset);

			getMethod.setURI(uri);

			/////////////////////////////
			HttpClient httpclient = getHttpClient();
			httpclient.executeMethod(getMethod);

			if (getMethod.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
				JOptionPane.showMessageDialog(null, "User credentials not valid.");
				loggedIn = false;
			}
			else {
				loggedIn = true;
			}
			setVisible(false);
			saveState();
			/////////////////////////////
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			getMethod.releaseConnection();
		}
	}

	/**
	 * ALFRESCO USER INTERFACE CODE
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents() {
		Label = new javax.swing.JLabel();
		usernameLabel = new javax.swing.JLabel();
		passwordLabel = new javax.swing.JLabel();
		usernameText = new javax.swing.JTextField();
		cancelButton = new javax.swing.JButton();
		submitButton = new javax.swing.JButton();
		passwordText = new javax.swing.JPasswordField();
		separator = new javax.swing.JSeparator();
		serverLabel = new javax.swing.JLabel();
		serverText = new javax.swing.JTextField();
		projectLabel = new javax.swing.JLabel();
		projectText = new javax.swing.JTextField();
		Label2 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

		this.setTitle("Alfresco Server Login");

		Label.setText("Set Authentication ");

		usernameLabel.setText("Username:");

		passwordLabel.setText("Password:");

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		submitButton.setText("Submit");
		submitButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				submitButtonActionPerformed(evt);
			}
		});
		
		// make the submit button the default button when enter key is pressed
		getRootPane().setDefaultButton(submitButton);

		serverLabel.setText("Server: ");

		projectLabel.setText("Project: ");

		Label2.setText("User Credentials");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
										.addGap(0, 0, Short.MAX_VALUE)
										.addComponent(submitButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(cancelButton))
										.addComponent(separator)
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(serverLabel)
														.addComponent(projectLabel))
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(serverText)
																.addComponent(projectText)))
																.addGroup(layout.createSequentialGroup()
																		.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(Label)
																				.addComponent(Label2))
																				.addGap(0, 0, Short.MAX_VALUE))
																				.addGroup(layout.createSequentialGroup()
																						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																								.addComponent(passwordLabel)
																								.addComponent(usernameLabel))
																								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																										.addComponent(usernameText)
																										.addComponent(passwordText, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))))
																										.addContainerGap())
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(Label)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(serverLabel)
								.addComponent(serverText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(projectLabel)
										.addComponent(projectText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(Label2)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(usernameLabel)
												.addComponent(usernameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(passwordLabel)
														.addComponent(passwordText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGap(18, 18, 18)
														.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(cancelButton)
																.addComponent(submitButton))
																.addContainerGap())
				);
		pack();
	}// </editor-fold>
	
	private File getPreferenceFolder() {
		String userHome = System.getProperty("user.home");
		File homeFolder = new File(userHome);
		File derivaFolder = new File(homeFolder, PREFERENCE_FOLDER_NAME);
		if(!derivaFolder.exists()) {
			derivaFolder.mkdir();
		}
		return derivaFolder;
	}

	/**
	 * Load the authentication dialog with cached values so the user doesn't have to retype them every time.
	 */
	private void restoreState() {
		File derivaFolder = getPreferenceFolder();
		File propFile = new File(derivaFolder, PROP_FILE_NAME);
		if(propFile.exists()) {
			// load properties file
			try {
				Properties props = new Properties();
				props.load(new FileInputStream(propFile));
				if(props.getProperty(PROP_SERVER) != null) {
					serverText.setText(props.getProperty(PROP_SERVER));
				}
				if(props.getProperty(PROP_PROJECT) != null) {
					projectText.setText(props.getProperty(PROP_PROJECT));
				}
				if(props.getProperty(PROP_USERNAME) != null) {
					usernameText.setText(props.getProperty(PROP_USERNAME));
				}

				// default the cursor to the password box
				passwordText.requestFocusInWindow();

			} catch (Throwable e) {
				e.printStackTrace();
			}

		} else {
			serverText.setText("http://localhost:8080/alfresco");
		}
	}

	private void saveState() {
		File derivaFolder = getPreferenceFolder();
		File propFile = new File(derivaFolder, PROP_FILE_NAME);

		try {
			Properties props = new Properties();
			props.put(PROP_SERVER, serverText.getText());
			props.put(PROP_PROJECT, projectText.getText());
			props.put(PROP_USERNAME, usernameText.getText());
			props.store(new FileOutputStream(propFile), "");

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void setBaseUrl(String alfrescoBaseUrl) {
		if(alfrescoBaseUrl.endsWith("/")){
			alfrescoBaseUrl = alfrescoBaseUrl.substring(0,alfrescoBaseUrl.length()-1);
		}
		AlfrescoClient.alfrescoBaseUrl = alfrescoBaseUrl;
	}

	protected StringBuilder getWebdavUrl() {
		StringBuilder url = new StringBuilder(alfrescoBaseUrl);
		url.append("/webdav/");
		return url;
	}

	protected void executeMethod(HttpMethod method) throws Exception {
		try {
			HttpClient httpclient = getHttpClient();
			httpclient.executeMethod(method);

			if (method.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
				throw new Exception("The user is not authorized to perform this operation.");

			if (method.getStatusCode() >= 400) {
				if(method.getStatusCode() == 511) {
					throw new RuntimeException("A resource with this name already exists!");

				} else {
					String response = method.getResponseBodyAsString();
					throw new RuntimeException("Error sending content: " + method.getStatusLine().toString() + "\n" + response);
				}
			}
		} catch (HttpException e) {
			throw new RuntimeException("Error sending content.", e);
		} catch (IOException e) {
			throw new RuntimeException("Error sending content.", e);
		}
	}

	protected HttpClient getHttpClient() {
		// Instantiating the way CommonsHTTPSender does
		HttpClient httpclient = new HttpClient(this.connectionManager);

		// the timeout value for allocation of connections from the pool
		// 0 means infinite timeout
		httpclient.getParams().setConnectionManagerTimeout(0);

		// we're hoping this speeds things up by eliminating the handshake
		//httpclient.getParams().setBooleanParameter(HttpClientParams.PREEMPTIVE_AUTHENTICATION, true);

		if(username != null) {
			Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
			httpclient.getState().setCredentials(AuthScope.ANY, defaultcreds);
		}

		// Set the proxy server
		String proxyHost = System.getProperties().getProperty("http.proxyHost");
		String proxyPort = System.getProperties().getProperty("http.proxyPort");

		if(proxyHost != null && proxyPort != null) {
			System.out.println("HttpClient configuring proxy " + proxyHost + ":" + proxyPort);
			httpclient.getHostConfiguration().setProxy(proxyHost, Integer.valueOf(proxyPort));
		}

		return httpclient;

	}

	protected StringBuilder getCMISWebScriptUrl() {
		StringBuilder url = new StringBuilder(alfrescoBaseUrl);
		url.append(PATH_SERVICE);
		url.append("cmis");
		return url;
	}
	protected StringBuilder getAPIWebScriptUrl() {
		StringBuilder url = new StringBuilder(alfrescoBaseUrl);
		url.append(PATH_SERVICE);
		url.append("api");
		return url;
	}

	public static StringBuilder getDerivAWebScriptUrl() {
		StringBuilder url = new StringBuilder(alfrescoBaseUrl);
		url.append(PATH_SERVICE);
		url.append("derivA/");
		return url;
	}

	/**
	 * Append the given String paths to the {@link StringBuilder} URL, separating with {@link #SLASH}. The {@link StringBuilder} when finished will not end in a {@link #SLASH}.
	 *
	 * @param url
	 *          {@link StringBuilder} to append to
	 * @param paths
	 *          String array (varargs) of paths to append
	 */
	public static void appendPaths(StringBuilder url, String... paths) {
		if (!endsWith(url, SLASH)) {
			url.append(SLASH);
		}

		for (String path : paths) {
			if (!path.isEmpty()) {
				url.append(path);
			}

			if (!endsWith(url, SLASH)) {
				url.append(SLASH);
			}
		}

		if (endsWith(url, SLASH)) {
			url.deleteCharAt(url.length() - 1);
		}
	}

	/**
	 * Test if the given {@link StringBuilder} ends with the given String.
	 *
	 * @param stringBuilder
	 *          {@link StringBuilder} to test
	 * @param value
	 *          String to test if at the end of the {@link StringBuilder}
	 * @return boolean true if the {@link StringBuilder} ends with the String
	 */
	private static boolean endsWith(StringBuilder stringBuilder, String value) {
		int index = stringBuilder.lastIndexOf(value);

		return index == (stringBuilder.length() - 1);
	}

	/**
	 * Append the given request parameter name and value to the {@link StringBuilder} URL.
	 *
	 * @param url
	 *          {@link StringBuilder} to append parameter to
	 * @param name
	 *          String name of the parameter
	 * @param value
	 *          String value of the parameter
	 */
	public static void appendParameter(StringBuilder url, String name, String value) {
		if (!name.isEmpty()) {
			if (!contains(url, QUESTION)) {
				url.append(QUESTION);
			}

			if (!endsWith(url, QUESTION) && !endsWith(url, AMPERSAND)) {
				url.append(AMPERSAND);
			}

			url.append(name);
			url.append(EQUALS);

			if (!value.isEmpty()) {
				url.append(value);
			}
		}
	}

	/**
	 * Test if the given {@link StringBuilder} contains the given String.
	 *
	 * @param stringBuilder
	 *          {@link StringBuilder} to search within
	 * @param value
	 *          String to search for
	 * @return boolean true if the {@link StringBuilder} contains the given String
	 */
	private static boolean contains(StringBuilder stringBuilder, String value) {
		int index = stringBuilder.indexOf(value);

		return index != -1;
	}

	/**
	 * Encode the path and query segments of the given {@link StringBuilder} URL.
	 *
	 * @param url
	 *          {@link StringBuilder} URL with path and query segments
	 * @return String encoded URL
	 */
	public static String encode(StringBuilder url) {
		try {
			return URIUtil.encodePathQuery(url.toString());
		} catch (URIException e) {
			throw new RuntimeException(e);
		}
	}

	public String getObjectUuid(String path){
		String atomXml = getObject(path);
		if (atomXml == null || atomXml.isEmpty()) {
			return null;
		}
		//find the <id> tag, should be only one
		String idTag = atomXml.substring(atomXml.indexOf("<id>") + "<id>".length(), atomXml.indexOf("</id>"));
		//this UUID is everything after the last colon:
		return idTag.substring(idTag.lastIndexOf(":") + ":".length());
	}

	public String getObject(String path){
		//we're using this alfresco webscript - but it returns an atom feed xml document, so it will need to be parsed
		//GET /api/path/{store_type}/{store_id}/{nodepath}?filter={filter?}&returnVersion={returnVersion?}
		//&includeAllowableActions={includeAllowableActions?}&includeRelationships={includeRelationships?}
		//&includeACL={includeACL?}&renditionFilter={renditionFilter?}

		try{
			StringBuilder url = getAPIWebScriptUrl();
			appendPaths(url, "path");
			appendPaths(url, "Workspace");
			appendPaths(url, "SpacesStore");
			appendPaths(url, "company_home");
			appendPaths(url, path);

			GetMethod getMethod = new GetMethod();
			String encodedUrl = encode(url);
			String charset = getMethod.getParams().getUriCharset();
			URI uri = new URI(encodedUrl, true, charset);

			getMethod.setURI(uri);
			executeMethod(getMethod);
			return getMethod.getResponseBodyAsString();
		}catch (Exception e) {
			return "";
		}
	}
}
