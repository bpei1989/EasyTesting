package com.easytesting.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.InteractiveCallback;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * SSHUtil encapsulates general SSH related utility methods.
 */
public class SSHUtil {

	private static final Logger log = LoggerFactory.getLogger(SSHUtil.class);
	private static final long SSHCOMMAND_TIMEOUT = 300;

	/**
	 * Connects to the remote host using SSH
	 * 
	 * @param hostName
	 *            host to connect
	 * @param userName
	 *            username to authenticate
	 * @param password
	 *            password to authenticate
	 * @return SSH Connection
	 * @throws IOException
	 *             , Exception
	 */
	public static Connection getSSHConnection(String hostName, String userName,
			final String password) throws Exception {
		Connection conn = new Connection(hostName);
		String[] strArray;
		/* Now connect */
		conn.connect();

		try {
			strArray = conn.getRemainingAuthMethods(userName);
		} catch (IOException e) {
			throw new Exception(
					"Getting Remaining AuthMethods failed with IOException: "
							+ e.getMessage());
		}
		if (strArray == null) {
			log.info("conn.getRemainingAuthMethods returns null");
			try {
				conn.authenticateWithPassword(userName, password);
			} catch (Exception e) {
				String warning = "";
				if (password.equals("")) {
					warning += " : "
							+ "Warning: Implementation of this package "
							+ "does not allow empty passwords for authentication";
				}
				throw new Exception("Authentication with password failed: "
						+ e.getMessage() + warning);
			}
		} else {
			List<String> authMethods = Arrays.asList(strArray);
			/* Authenticate */
			if (authMethods.contains("password")) {
				if (!conn.authenticateWithPassword(userName, password)) {
					throw new Exception("Password based authentication failed.");
				}
			} else if (authMethods.contains("keyboard-interactive")) {
				InteractiveCallback cb = new InteractiveCallback() {
					// @Override
					public String[] replyToChallenge(String name,
							String instruction, int numPrompts,
							String[] prompt, boolean[] echo) throws Exception {
						/*
						 * Going with the assumption that the only thing servers
						 * asks for is password
						 */
						String[] response = new String[numPrompts];
						for (int i = 0; i < response.length; i++) {
							response[i] = password;
						}
						return response;
					}
				};
				if (!conn.authenticateWithKeyboardInteractive(userName, cb)) {
					throw new Exception(
							"Keyboard-interactive based authentication failed.");
				}
			} else {
				throw new Exception(
						"SSH Server doesnt support password or keyboard-interactive logins");
			}
		}
		log.info("Successfully connected to the remote ssh host: " + hostName);
		return conn;
	}

	/**
	 * Closes the SSH Connection to the remote host
	 * 
	 * @param conn
	 *            SSH Connection
	 * @return true if successful, exception raised otherwise
	 * @throws Exception
	 */
	public static boolean closeSSHConnection(Connection conn) throws Exception {
		boolean success = true;
		if (conn != null) {
			conn.close();
			log.info("SSH Connection closed");
		}
		return success;
	}

	/**
	 * Executes the given command on the remote host using ssh
	 * 
	 * @param conn
	 *            SSH Connection
	 * @param command
	 *            Command to be executed
	 * @param timeout
	 *            Timeout in seconds
	 * @return HashMap with both error and output stream contents
	 * @throws IOException
	 *             , Exception
	 */
	public static Map<String, String> getRemoteSSHCmdOutput(Connection conn,
			String command, long timeout) throws Exception {
		Session session = null;
		InputStream stderr = null;
		InputStream stdout = null;
		Map<String, String> returnData = new HashMap<String, String>();
		try {
			session = conn.openSession();
			log.info("Running command '" + command + "' with timeout of "
					+ timeout + " seconds");
			session.execCommand(command);
			// Wait until command completes or times out
			int result = session.waitForCondition(ChannelCondition.EOF,
					timeout * 1000);
			if ((result & ChannelCondition.TIMEOUT) != 0) {
				log.warn("A timeout occured while waiting for data from the "
						+ "server");
				if (session != null) {
					session.close();
				}
				return returnData;
			}
			stderr = new StreamGobbler(session.getStderr());
			stdout = new StreamGobbler(session.getStdout());
			// populate output stream
			StringBuffer outputDataStream = getInputStreamString(stdout);
			returnData.put("SSHOutputStream", outputDataStream.toString());
			// populate error stream
			StringBuffer errorDataStream = getInputStreamString(stderr);
			returnData.put("SSHErrorStream", errorDataStream.toString());
			Integer exitStatus = session.getExitStatus();
			if (errorDataStream.length() != 0) {
				// command execution failed ( even if execution of one command
				// fails
				// )
				log.error("SSH session ExitCode: " + exitStatus);
				log.error("Error while executing '" + command
						+ "' command on remote ssh host");
				log.error("Error Stream: \n" + errorDataStream);
				log.info("Output Stream: \n" + outputDataStream);
			} else {
				// command executed successfully , populate the output stream
				log.info("SSH session ExitCode: " + exitStatus);
				log.info("Successfully executed '" + command
						+ "' command on remote ssh host");
			}
		} finally {
			if (session != null) {
				session.close();
			}
			if (stderr != null) {
				stderr.close();
			}
			if (stdout != null) {
				stdout.close();
			}
		}
		// returnData must contain Error as well as output stream
		// and the test cases would decide accordingly
		return returnData;
	}

	public static Map<String, String> getRemoteSSHCmdOutput(Connection conn,
			String command) throws Exception {
		return getRemoteSSHCmdOutput(conn, command, SSHCOMMAND_TIMEOUT);
	}

	/**
	 * Populate a StringBuffer with the contents of an InputStream
	 * 
	 * @param out
	 *            InputStream to process
	 * @return StringBuffer object containing InputStream contents
	 * @throws IOException
	 */
	public static StringBuffer getInputStreamString(final InputStream in)
			throws Exception {

		StringBuffer out = null;
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				in));
		try {
			out = new StringBuffer();
			String tmp = "";
			while ((tmp = reader.readLine()) != null) {
				out.append(tmp + "\n");
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return out;
	}
	
	
	/**
	 * Asynchronously executes the given command on the remote host using ssh.
	 * It doesn't waits for command to complete on the remote host.
	 *
	 * @param Connection
	 *            SSH Connection
	 * @param command
	 *            Command to be executed
	 * @throws IOException
	 *             , Exception
	 */
	public static void executeAsyncRemoteSSHCommand(Connection conn,
			String command) throws Exception {
		Session session = null;
		try {
			session = conn.openSession();
			log.info("Running command '"
					+ command
					+ "' asynchronously. "
					+ " It doesn't wait for command to complete on remote host.");
			session.execCommand(command);
			int sleep = 10;
			log.info("Sleep for " + sleep + " seconds for command to kick in.");
			Thread.sleep(sleep * 1000);

		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	
	public static void main(String args[]) {
		Connection con = null;
		try {
			con = SSHUtil.getSSHConnection("10.144.137.53", "root", "ca$hc0w");
			Map<String, String> ret = SSHUtil.getRemoteSSHCmdOutput(con, "cat /abcd");
			for(Map.Entry<String, String> t : ret.entrySet()) {
				log.info("Result: " + t.getKey() + "+" + t.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				SSHUtil.closeSSHConnection(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}