package fidocadj;

import java.io.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

/** DragDropTools.java

    Class handling the drag and drop operations.

    TODO: improve the descriptions in the Javadoc comments. Sometimes are
    cryptical or uninformative.

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
public class DragDropTools implements DropTargetListener
{
    FidoFrame fidoFrame;

    /** Constructor.
      @param f the frame to which the drag and drop tools should be associated.
    */
    public DragDropTools(FidoFrame f)
    {
        fidoFrame=f;
    }

    /**  This implementation of the DropTargetListener interface is heavily
        inspired on the example given here:
        http://www.java-tips.org/java-se-tips/javax.swing/how-to-implement-drag-
            drop-functionality-in-your-applic.html
        @param dtde the drop target drag event
    */
    @Override public void dragEnter(DropTargetDragEvent dtde)
    {
        // does nothing
    }

    /** Exit from the drag target.
        @param dte the drop target event.
    */
    @Override public void dragExit(DropTargetEvent dte)
    {
        // does nothing
    }

    /** Drag over the target.
        @param dtde the drop target drag event.
    */
    @Override public void dragOver(DropTargetDragEvent dtde)
    {
        // does nothing
    }

    /** Drop action changed.
        @param dtde the drop target drag event.
    */
    @Override public void dropActionChanged(DropTargetDragEvent dtde)
    {
        // does nothing
    }

    /** This routine is called when a drag and drop of an useful file is done
        on an open instance of FidoCadJ. The difficulty is that depending on
        the operating system flavor, the files are handled differently.
        For that reason, we check a few things and we need to differentiate
        several cases.
        @param dtde the drop target event.
    */
    @Override public void drop(DropTargetDropEvent dtde)
    {
        try {
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            if (flavors==null) {
                return;
            }

            for (DataFlavor df : flavors) {
                // try to avoid problematic situations
                if(df==null) {
                    return;
                }
                // check the correct type of the drop flavor
                if (df.isFlavorJavaFileListType()) {
                    // Great!  Accept copy drops...
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    // And add the list of file names to our text area
                    java.util.List list =
                        (java.util.List)tr.getTransferData(df);

                    FidoFrame popFrame;

                    if(fidoFrame.getCircuitPanel().getUndoActions()
                                                        .getModified()) {
                        popFrame = fidoFrame.createNewInstance();
                    } else {
                        popFrame=fidoFrame;
                    }

                    // Only the first file of the list will be opened
                    popFrame.getCircuitPanel().getParserActions().openFileName=
                        ((File)list.get(0)).getAbsolutePath();
                    popFrame.getFileTools().openFile();
                    // If we made it this far, everything worked.
                    dtde.dropComplete(true);
                    return;
                }
                // Ok, is it another Java object?
                else if (df.isFlavorSerializedObjectType()) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Object o = tr.getTransferData(df);
                    // If there is a valid FidoCad code, try to draw it.
                    FidoFrame popFrame;

                    if(fidoFrame.getCircuitPanel().getUndoActions().getModified()) {
                        popFrame = fidoFrame.createNewInstance();
                    } else {
                        popFrame=fidoFrame;
                    }

                    popFrame.getCircuitPanel().getParserActions().parseString(
                        new StringBuffer(o.toString()));
                    popFrame.getCircuitPanel().getUndoActions().saveUndoState();
                    popFrame.getCircuitPanel().getUndoActions().setModified(false);

                    dtde.dropComplete(true);
                    popFrame.getCircuitPanel().repaint();
                    return;
                }
                // How about an input stream? In some Linux flavors, it contains
                // the file name, with a few substitutions.

                else if (df.isRepresentationClassInputStream()) {
                    // Everything seems to be ok here, so we proceed handling
                    // the file
                    InputStreamReader reader=null;
                    BufferedReader in=null;
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    try {
                        reader=new InputStreamReader(
                            (InputStream)tr.getTransferData(df));
                        in=new BufferedReader(reader);

                        String line="";
                        int k;
                        line = in.readLine();

                        while (line != null) {
                            k=line.indexOf("file://");
                            if (k>=0) {
                                FidoFrame popFrame;

                                if(fidoFrame.getCircuitPanel().getUndoActions().
                                    getModified())
                                {
                                    popFrame=fidoFrame.createNewInstance();
                                } else {
                                    popFrame=fidoFrame;
                                }

                                popFrame.getCircuitPanel().getParserActions().
                                    openFileName = line.substring(k+7);

                                // Deprecated! It should indicate the encoding,
                                // but WE WANT the encoding using being the
                                // same of the host system. It may be
                                // deprecated, but it is the correct behaviour,
                                // here.

                                popFrame.getCircuitPanel().getParserActions().
                                    openFileName =
                                    java.net.URLDecoder.decode(
                                            popFrame.getCircuitPanel()
                                                    .getParserActions()
                                                    .openFileName, "UTF-8");

                                // After we set the current file name, we just
                                // open it.
                                popFrame.getFileTools().openFile();
                                popFrame.getCircuitPanel().getUndoActions().
                                    saveUndoState();
                                popFrame.getCircuitPanel().getUndoActions().
                                    setModified(false);
                                break;
                            }
                            line = in.readLine();
                        }
                    } finally {
                        if(in!=null) { in.close(); }
                        if(reader!=null) { reader.close(); }
                    }
                    fidoFrame.getCircuitPanel().repaint();

                    dtde.dropComplete(true);
                    return;
                }
            }
            // Hmm, the user must not have dropped a file list
            System.out.println("Drop failed: " + dtde);
            dtde.rejectDrop();
        } catch (Exception e) {
            e.printStackTrace();
            dtde.rejectDrop();
        }
    }
}
