package fidocadj.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Toolkit;
import java.io.*;

import fidocadj.globals.ProvidesCopyPasteInterface;

/**
    Clipboard handling class.

    <pre>
    This file is part of FidoCadJ.

    FidoCadJ is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FidoCadJ is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2015-2023 by Davide Bucci
   </pre>

    @author Davide Bucci
*/

public final class TextTransfer implements ClipboardOwner,
    ProvidesCopyPasteInterface
{
    /** Perform a copy operation of the text specified as an argument.
        @param s the text to be copied
    */
    public void copyText(String s)
    {
         // get the system clipboard
        Clipboard systemClipboard =Toolkit.getDefaultToolkit()
            .getSystemClipboard();

        Transferable transferableText = new StringSelection(s);
        systemClipboard.setContents(transferableText,null);
    }

    /** Perform a paste operation of the copied test. The pasted text is
        returned. In other words, do a read in the clipboard.
        @return the pasted test.
    */
    public String pasteText()
    {
        // TODO: review a little...
        // HASDONE: it seems to work quite reliably. No problems so far...
        TextTransfer textTransfer = new TextTransfer();
        return textTransfer.getClipboardContents();
    }

    /**
        Empty implementation of the ClipboardOwner interface.
        @param aClipboard handle to the clipboard to use.
        @param aContents handle to the contents.
    */
    @Override public void lostOwnership(Clipboard aClipboard,
        Transferable aContents)
    {
        // does nothing
    }

    /**
        Place a {@link String} on the clipboard, and make this class the
        owner of the Clipboard's contents.
        @param aString the {@link String} to be employed.
    */
    public void setClipboardContents(String aString)
    {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    /**
        Get the String residing on the clipboard.
        @return any text found on the Clipboard; if none found, return an
        empty {@link String}.
    */
    public String getClipboardContents()
    {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = contents != null &&
            contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String)contents.getTransferData(
                    DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException ex){
                //highly unlikely since we are using a standard DataFlavor
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
        return result;
    }
}