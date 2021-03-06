/*
 [The "BSD licence"]
 Copyright (c) 2005-2006 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.antlr.runtime.debug;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.tree.TreeAdaptor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/** A proxy debug event listener that forwards events over a socket to
 *  a debugger (or any other listener) using a simple text-based protocol;
 *  one event per line.  ANTLRWorks listens on server socket with a
 *  RemoteDebugEventSocketListener instance.  These two objects must therefore
 *  be kept in sync.  New events must be handled on both sides of socket.
 */
public class DebugEventSocketProxy extends BlankDebugEventListener {
	public static final int DEFAULT_DEBUGGER_PORT = 0xC001;
	protected int port = DEFAULT_DEBUGGER_PORT;
	protected ServerSocket serverSocket;
	protected Socket socket;
	protected String grammarFileName;
	protected PrintWriter out;
	protected BufferedReader in;

	/** Who am i debugging? */
	protected BaseRecognizer recognizer;

	/** Almost certainly the recognizer will have adaptor set, but
	 *  we don't know how to cast it (Parser or TreeParser) to get
	 *  the adaptor field.  Must be set with a constructor. :(
	 */
	protected TreeAdaptor adaptor;

	public DebugEventSocketProxy(BaseRecognizer recognizer, TreeAdaptor adaptor) {
		this(recognizer, DEFAULT_DEBUGGER_PORT, adaptor);
	}

	public DebugEventSocketProxy(BaseRecognizer recognizer, int port, TreeAdaptor adaptor) {
		this.grammarFileName = recognizer.getGrammarFileName();
		this.adaptor = adaptor;
		this.port = port;
	}

	public void handshake() throws IOException {
		if ( serverSocket==null ) {
			serverSocket = new ServerSocket(port);
			socket = serverSocket.accept();
			socket.setTcpNoDelay(true);
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF8");
			out = new PrintWriter(new BufferedWriter(osw));
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF8");
			in = new BufferedReader(isr);
			out.println("ANTLR "+ DebugEventListener.PROTOCOL_VERSION);
			out.println("grammar \""+ grammarFileName);
			out.flush();
		}
	}

	public void commence() {
		// don't bother sending event; listener will trigger upon connection
	}

	public void terminate() {
		transmit("terminate");
		out.close();
		try {
			socket.close();
		}
		catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}
	}

	protected void ack() {
		try {
			in.readLine();
		}
		catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}

	}

	protected void transmit(String event) {
		out.println(event);
		out.flush();
		ack();
	}

	public void enterRule(String ruleName) {
		transmit("enterRule "+ruleName);
	}

	public void enterAlt(int alt) {
		transmit("enterAlt "+alt);
	}

	public void exitRule(String ruleName) {
		transmit("exitRule "+ruleName);
	}

	public void enterSubRule(int decisionNumber) {
		transmit("enterSubRule "+decisionNumber);
	}

	public void exitSubRule(int decisionNumber) {
		transmit("exitSubRule "+decisionNumber);
	}

	public void enterDecision(int decisionNumber) {
		transmit("enterDecision "+decisionNumber);
	}

	public void exitDecision(int decisionNumber) {
		transmit("exitDecision "+decisionNumber);
	}

	public void consumeToken(Token t) {
		String buf = serializeToken(t);
		transmit("consumeToken "+buf);
	}

	public void consumeHiddenToken(Token t) {
		String buf = serializeToken(t);
		transmit("consumeHiddenToken "+buf);
	}

	public void LT(int i, Token t) {
        if(t != null)
            transmit("LT "+i+" "+serializeToken(t));
	}

	public void mark(int i) {
		transmit("mark "+i);
	}

	public void rewind(int i) {
		transmit("rewind "+i);
	}

	public void rewind() {
		transmit("rewind");
	}

	public void beginBacktrack(int level) {
		transmit("beginBacktrack "+level);
	}

	public void endBacktrack(int level, boolean successful) {
		transmit("endBacktrack "+level+" "+(successful?TRUE:FALSE));
	}

	public void location(int line, int pos) {
		transmit("location "+line+" "+pos);
	}

	public void recognitionException(RecognitionException e) {
		StringBuffer buf = new StringBuffer(50);
		buf.append("exception ");
		buf.append(e.getClass().getName());
		// dump only the data common to all exceptions for now
		buf.append(" ");
		buf.append(e.index);
		buf.append(" ");
		buf.append(e.line);
		buf.append(" ");
		buf.append(e.charPositionInLine);
		transmit(buf.toString());
	}

	public void beginResync() {
		transmit("beginResync");
	}

	public void endResync() {
		transmit("endResync");
	}

	public void semanticPredicate(boolean result, String predicate) {
		StringBuffer buf = new StringBuffer(50);
		buf.append("semanticPredicate ");
		buf.append(result);
		serializeText(buf, predicate);
		transmit(buf.toString());
	}

	// A S T  P a r s i n g  E v e n t s

	public void consumeNode(Object t) {
		StringBuffer buf = new StringBuffer(50);
		buf.append("consumeNode");
		serializeNode(buf, t);
		transmit(buf.toString());
	}

	public void LT(int i, Object t) {
		int ID = adaptor.getUniqueID(t);
		String text = adaptor.getText(t);
		int type = adaptor.getType(t);
		StringBuffer buf = new StringBuffer(50);
		buf.append("LN "); // lookahead node; distinguish from LT in protocol
		buf.append(i);
		serializeNode(buf, t);
		transmit(buf.toString());
	}

	protected void serializeNode(StringBuffer buf, Object t) {
		int ID = adaptor.getUniqueID(t);
		String text = adaptor.getText(t);
		int type = adaptor.getType(t);
		buf.append(" ");
		buf.append(ID);
		buf.append(" ");
		buf.append(type);
		Token token = adaptor.getToken(t);
		int line = -1;
		int pos = -1;
		if ( token!=null ) {
			line = token.getLine();
			pos = token.getCharPositionInLine();
		}
		buf.append(" ");
		buf.append(line);
		buf.append(" ");
		buf.append(pos);
		int tokenIndex = adaptor.getTokenStartIndex(t);
		buf.append(" ");
		buf.append(tokenIndex);
		serializeText(buf, text);
	}

	
	// A S T  E v e n t s

	public void nilNode(Object t) {
		int ID = adaptor.getUniqueID(t);
		transmit("nilNode "+ID);
	}

	public void createNode(Object t) {
		int ID = adaptor.getUniqueID(t);
		String text = adaptor.getText(t);
		int type = adaptor.getType(t);
		StringBuffer buf = new StringBuffer(50);
		buf.append("createNodeFromTokenElements ");
		buf.append(ID);
		buf.append(" ");
		buf.append(type);
		serializeText(buf, text);
		transmit(buf.toString());
	}

	public void createNode(Object node, Token token) {
		int ID = adaptor.getUniqueID(node);
		int tokenIndex = token.getTokenIndex();
		transmit("createNode "+ID+" "+tokenIndex);
	}

	public void becomeRoot(Object newRoot, Object oldRoot) {
		int newRootID = adaptor.getUniqueID(newRoot);
		int oldRootID = adaptor.getUniqueID(oldRoot);
		transmit("becomeRoot "+newRootID+" "+oldRootID);
	}

	public void addChild(Object root, Object child) {
		int rootID = adaptor.getUniqueID(root);
		int childID = adaptor.getUniqueID(child);
		transmit("addChild "+rootID+" "+childID);
	}

	public void setTokenBoundaries(Object t, int tokenStartIndex, int tokenStopIndex) {
		int ID = adaptor.getUniqueID(t);
		transmit("setTokenBoundaries "+ID+" "+tokenStartIndex+" "+tokenStopIndex);
	}

	// support

	protected String serializeToken(Token t) {
		StringBuffer buf = new StringBuffer(50);
		buf.append(t.getTokenIndex()); buf.append(' ');
		buf.append(t.getType()); buf.append(' ');
		buf.append(t.getChannel()); buf.append(' ');
		buf.append(t.getLine()); buf.append(' ');
		buf.append(t.getCharPositionInLine());
		serializeText(buf, t.getText());
		return buf.toString();
	}

	protected void serializeText(StringBuffer buf, String text) {
		buf.append(" \"");
		if ( text==null ) {
			text = "";
		}
		// escape \n and \r all text for token appears to exist on one line
		// this escape is slow but easy to understand
		text = escapeNewlines(text);
		buf.append(text);
	}

	protected String escapeNewlines(String txt) {
		txt = txt.replaceAll("%","%25");   // escape all escape char ;)
		txt = txt.replaceAll("\n","%0A");  // escape \n
		txt = txt.replaceAll("\r","%0D");  // escape \r
		return txt;
	}
}

