/*
Copyright (c) 2012, University of Texas at El Paso
All rights reserved.
Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:
- Redistributions of source code must retain the above copyright notice, this 
  list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
 */
package edu.utep.cybershare.wdoapi.util;

/**
 * Utility class to verify namespaces assigned to OWL documents.
 * 
 * namespace :- protocol://body 
 * protocol :- http | https | ftp | ftps | file
 * body :- nspart | nspart/body nspart :- word | word-nspart | word.nspart word
 * :- sequence of letters and numbers
 * 
 * Exception: When the protocol file: is used, the body of the namespace can
 * start with a /, e.g., file:///home/test.txt
 * 
 * @author Leonardo Salayandia
 */
public class Namespace {
	public enum NS_FORMAT {
		FULL, BODY_ONLY
	}; // whether to expect namespace in the form protocol://body or just the
		// body portion

	public static final String PROTOCOL_BODY_SEPARATOR = "://";
	public static final String NS_SEPARATOR = "/";

	public enum NS_PROTOCOLS {
		http, https, ftp, ftps, file
	};

	private static final String protocolRegEx = "http|https|ftp|ftps|file";
	private static final String nsPartRegEx = "[a-zA-Z0-9]+((\\-|\\.|\\_|\\%20|\\:)[a-zA-Z0-9]+)*";

	/**
	 * Verify that the given namespace is valid. Namespace is expected to be of
	 * the form: protocol://body
	 * 
	 * @param ns
	 * @return
	 */
	public static boolean isValid(String ns) {
		return isValid(ns, NS_FORMAT.FULL);
	}

	/**
	 * Verify that the given namespace is valid. If format is FULL, then the
	 * namespace should be of the form: protocol://body If format is BODY_ONLY,
	 * then the namespace should be of the form: body
	 * 
	 * @param ns
	 * @param format
	 * @return
	 */
	public static boolean isValid(String ns, NS_FORMAT format) {
		if (ns == null) {
			return false;
		}

		String protocol = null;
		String body = null;
		if (format == NS_FORMAT.FULL) {
			String[] nsSplit = ns.split(PROTOCOL_BODY_SEPARATOR, 2);
			if (nsSplit.length != 2) {
				return false;
			}
			protocol = nsSplit[0];

			if (!protocol.toLowerCase().matches(protocolRegEx)) {
				return false;
			}

			body = nsSplit[1];
		} else if (format == NS_FORMAT.BODY_ONLY) {
			body = ns;
		}

		// namespace should not end with separator character
		if (body.endsWith(NS_SEPARATOR)) {
			return false;
		}

		// divide namespace body into parts and evaluate each part individually
		String[] nsParts = body.split(NS_SEPARATOR);

		// for the case of file: protocol, the first nsPart can be null, i.e.,
		// the ns body can start with /
		int i = 0;
		if (format == NS_FORMAT.FULL) {
			if (protocol.equalsIgnoreCase(NS_PROTOCOLS.file.toString())) {
				if (nsParts[0].isEmpty()) {
					i = 1;
				}
			}
		}
		// if protocol not provided, need to be flexible. Allow / at the
		// beginning.
		else {
			if (nsParts[0].isEmpty()) {
				i = 1;
			}
		}
		for (; i < nsParts.length; i++) {
			if (!nsParts[i].matches(nsPartRegEx)) {
				return false;
			}
		}

		// passed tests
		return true;
	}
}
