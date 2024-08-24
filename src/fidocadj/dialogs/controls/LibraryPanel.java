package fidocadj.dialogs.controls;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;

import fidocadj.globals.Globals;

import java.awt.*;
import java.io.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.*;

/**
* JFileChooser accessory panel for listing libraries description.

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

    Copyright 2014-2023 by Kohta Ozaki, Davide Bucci
    </pre>
    @author Kohta Ozaki, Davide Bucci
*/

public final class LibraryPanel extends JPanel
    implements PropertyChangeListener
{
    final private static int PREFERRED_PANEL_WIDTH = 250;

    final private JFileChooser fc;
    final private LibraryListModel listModel;

    /**
    * Creates UI.
    * This LibraryPanel register as accessory panel to JFileChooser.
    * And adds as listener for receiving selection change event.
    * @param fc JFileChooser instance.
    */
    public LibraryPanel(JFileChooser fc)
    {
        this.fc = fc;
        fc.addPropertyChangeListener(this);
        fc.setAccessory(this);
        listModel = new LibraryListModel();

        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run()
                {
                    initGUI();
                }
        } );

        listModel.setDirectory(fc.getCurrentDirectory());
    }

    /** By implementing writeObject method,
    // we can prevent
    // subclass from serialization
    */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        throw new NotSerializableException();
    }

    /* By implementing readObject method,
    // we can prevent
    // subclass from de-serialization
    */
    private void readObject(ObjectInputStream in) throws IOException
    {
        throw new NotSerializableException();
    }

    /**
    * Creates UI.
    */
    private void initGUI()
    {
        JList<LibraryDesc> fileList;
        JScrollPane sp;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(PREFERRED_PANEL_WIDTH,1));

        // If this class is run as a standalone program, the Globals.messages
        // resource handler might not be initizalized. In this case,
        // an english tag will do the job.
        if(Globals.messages==null) {
            add(BorderLayout.NORTH, new JLabel("Libraries in directory:"));
        } else {
            add(BorderLayout.NORTH,new JLabel(
                Globals.messages.getString("lib_in_dir")));
        }

        fileList = new JList<LibraryDesc>(listModel);
        fileList.setCellRenderer(new ListCellRenderer<LibraryDesc>() {
            @Override
            public Component getListCellRendererComponent(JList<?
                extends LibraryPanel.LibraryDesc> list,
                LibraryPanel.LibraryDesc value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                LibraryDesc desc = (LibraryDesc) value;
                String libraryName;
                JPanel p;
                Icon icon = MetalIconFactory.getTreeFloppyDriveIcon();
                Icon spaceIcon = new SpaceIcon(icon.getIconWidth(),
                    icon.getIconHeight());

                if (desc.libraryName == null) {
                    libraryName = "---";
                } else {
                    libraryName = "(" + desc.libraryName + ")";
                }

                p = new JPanel();
                p.setBorder(new EmptyBorder(2,0,3,0));
                p.setOpaque(false);
                p.setLayout(new BorderLayout());
                p.add(BorderLayout.NORTH, new JLabel(desc.filename, icon,
                    SwingConstants.LEFT));
                p.add(BorderLayout.SOUTH, new JLabel(libraryName, spaceIcon,
                    SwingConstants.LEFT));
                return p;
            }
        });

        sp = new JScrollPane(fileList);
        sp.setVerticalScrollBarPolicy(
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(BorderLayout.CENTER, sp);

        // Disable focus.
        // The list is never selected.
        fileList.setFocusable(false);
    }

    @Override public void propertyChange(PropertyChangeEvent evt)
    {

        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(
            evt.getPropertyName()))
        {
            listModel.setDirectory(fc.getSelectedFile());
        }
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(
            evt.getPropertyName()))
        {
            listModel.setDirectory(fc.getCurrentDirectory());
        }
    }

    /** For test method.
        Shows only JFileChooser with this LibraryPanel.
        @param args the input parameters on the command line.
    */
    public static void main(String... args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run()
            {
                JFileChooser fc;
                fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setPreferredSize(new Dimension(800,400));
                new LibraryPanel(fc);

                fc.showOpenDialog(null);
            }
        });
    }

    /**
    * ListModel to provide libraries list.
    * This model searches libraries in selected directory.
    * And provide library name and filename to JList component.
    */
    public static class LibraryListModel implements ListModel<LibraryDesc>
    {
        final private java.util.List<ListDataListener> listeners;
        final private java.util.List<LibraryDesc> libraryList;
        private File currentDir=null;

    /** Constructs model.
    */
        LibraryListModel()
        {
            listeners = new ArrayList<ListDataListener>();
            libraryList = new ArrayList<LibraryDesc>();
        }

        /**
        * Sets directory.
        * And updates libraries list in directory.
        * @param dir selected directory as File
        */
        public void setDirectory(File dir)
        {
            currentDir = dir;

            clearList();
            if (currentDir != null && currentDir.canRead() &&
                 currentDir.isDirectory())
            {
                // Permission check
                refreshList();
            }
            // DB -> KO check if it is correct. I removed a "}"
            fireChanged();
        }

        private void refreshList()
        {
            File[] files=null;
            LibraryDesc desc=null;

            files = currentDir.listFiles(new FileFilter() {
                @Override public boolean accept(File f)
                {
                    return f.isFile() &&
                        f.getName().toLowerCase(Locale.US).
                        matches("^.*\\.fcl$");
                }
            });

            if(files==null) {
                return;
            }
            for (File f : files) {
                desc = new LibraryDesc();
                desc.filename = f.getName();
                desc.libraryName = getLibraryName(f);
                libraryList.add(desc);
            }

            // Sort list by filename.
            Collections.sort(libraryList, new Comparator<LibraryDesc>() {
                @Override
                public int compare(LibraryDesc ld1, LibraryDesc ld2)
                {
                    // Sort with case sensitive.
                    // This is usually made in UNIX file systems.
                    // If case sensitive is not needed, use
                    // String.compareToIgnoreCase
                    return ld1.filename.compareTo(ld2.filename);
                }
                /*@Override
                public boolean equals(Object obj)
                {   // DB. FindBugs complains that this methods always
                    // returns "false". It considers it a quite high priority
                    // issue to be solved. Is there any particular reason why
                    // this must return false?
                    // return false;
                    return this == obj;
                }*/
            });
        }

        private void clearList()
        {
            libraryList.clear();
        }

        private void fireChanged()
        {
            for (ListDataListener l : listeners) {
                l.contentsChanged(new ListDataEvent(this,
                    ListDataEvent.CONTENTS_CHANGED, 0, 0));
            }
        }

        private String getLibraryName(File f)
        {
            // if there is a public api for reading library name direct,
            // rewrite this section.

            // maxReadLine = -1  : reads to EOF.
            //             = num : reads to num line.
            int maxReadline = -1;
            int pt = 0;

            FileReader fr = null;
            BufferedReader br = null;

            String buf;
            String libraryName = null;

            try {
                fr = new FileReader(f);
                br = new BufferedReader(fr);

                while (true) {
                    buf = br.readLine();

                    // check EOF
                    if (buf == null) {
                        break;
                    }

                    if (buf.matches("^\\[FIDOLIB .*\\]\\s*")) {
                        buf = buf.trim();
                        libraryName = buf.substring(9, buf.length() - 1);
                        break;
                    }

                    if (maxReadline != -1 && maxReadline <= pt) {
                        break;
                    }
                    pt++;
                }

                // DB: it seems to me that catching an Exception is a
                // little bit too general. Which kind of reasonable
                // problems have we got to handle here?
            } catch (Exception e) {
                // return null for libraryName
                libraryName=null;
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (fr != null) {
                        fr.close();
                    }
                    // DB: it seems to me that catching an Exception is a
                    // little bit too general. Which kind of reasonable
                    // problems have we got to handle here?
                } catch (Exception e) {
                    System.out.println("Problems while closing streams.");
                }
            }

            return libraryName;
        }

        @Override
        public void addListDataListener(ListDataListener l)
        {
            listeners.add(l);
        }

        @Override
        public void removeListDataListener(ListDataListener l)
        {
            listeners.remove(l);
        }

        @Override
        public int getSize()
        {
            return libraryList.size();
        }

        @Override
        public LibraryDesc getElementAt(int index)
        {
            return libraryList.get(index);
        }
    }

    /**
    * Library description class.
    */
    private static final class LibraryDesc
    {
        public String filename;
        public String libraryName;

        @Override public String toString()
        {
            return String.format("%s (%s)", filename, libraryName);
        }
    }

    /**
    * Dummy icon class for spacing.
    */
    private static class SpaceIcon implements Icon
    {

        final private int width;
        final private int height;

        /** Constructor. Creates a dummy icon with the given size.
        */
        SpaceIcon(int width, int height)
        {
            this.width = width;
            this.height = height;
        }

        @Override public int getIconHeight()
        {
            return height;
        }

        @Override public int getIconWidth()
        {
            return width;
        }

        @Override public void paintIcon(Component c, Graphics g, int x, int y)
        {
            // NOP
        }
    }
}

