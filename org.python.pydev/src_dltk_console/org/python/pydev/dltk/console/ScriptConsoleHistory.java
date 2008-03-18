/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.python.pydev.dltk.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.python.pydev.plugin.PydevPlugin;

/**
 * Handles the history so that the user can do Ctrl+up / Ctrl+down
 */
public class ScriptConsoleHistory {
    
    /**
     * Holds the history in an easy way to handle it.
     */
    private final List<String> lines;

    /**
     * Holds the position of the current line in the history.
     */
    private int currLine;

    /**
     * Holds the history as a document (for requests such as the indent)
     */
    private Document historyAsDoc;

    public ScriptConsoleHistory() {
        this.lines = new ArrayList<String>();
        this.lines.add(""); //$NON-NLS-1$
        this.currLine = 0;
        this.historyAsDoc = new Document();
    }

    /**
     * Updates the current line in the buffer for the history (but it can still be changed later)
     * 
     * @param line contents to be added to the top of the command history.
     */
    public void update(String line) {
        lines.set(lines.size() - 1, line);
    }

    /**
     * Commits the currently added line (last called in update) to the history and keeps it there.
     */
    public void commit() {
        String lineToAddToHistory = getBufferLine();
        try {
            historyAsDoc.replace(historyAsDoc.getLength(), 0, lineToAddToHistory+"\n");
        } catch (BadLocationException e) {
            PydevPlugin.log(e);
        }

        if (lineToAddToHistory.length() == 0) {
            currLine = lines.size()-1;
            return;
        }
        
        lines.set(lines.size() - 1, lineToAddToHistory);

        lines.add(""); //$NON-NLS-1$
        currLine = lines.size() - 1;
    }

    /**
     * @return true if we've been able to go to a previous line (and false if there's no previous command in the history).
     */
    public boolean prev() {
        if (currLine > 0) {
            --currLine;
            return true;
        }

        return false;
    }

    /**
     * @return true if we've been able to go to a next line (and false if there's no next command in the history).
     */
    public boolean next() {
        if (currLine < lines.size() - 1) {
            ++currLine;
            return true;
        }

        return false;
    }

    /**
     * @return the document with the contents of this history. Should not be changed externally.
     */
    public IDocument getAsDoc() {
        return historyAsDoc;
    }
    
    /**
     * @return the contents of the line that's currently in the buffer but still wasn't added to the history.
     */
    public String getBufferLine() {
        return lines.get(lines.size()-1);
    }
    
    /**
     * @return the contents of the current line in the history.
     */
    public String get() {
        if (lines.isEmpty()) {
            return "";
        }

        return (String) lines.get(currLine);
    }
}