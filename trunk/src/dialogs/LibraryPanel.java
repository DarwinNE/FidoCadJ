package dialogs;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import java.awt.*;
import java.io.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.*;

import globals.*;

/**
* Accessory panel to show library list for JFileChooser.
* @Author Kohta Ozaki
*/
class LibraryPanel extends JPanel implements PropertyChangeListener 
{
    private int PREFERRED_PANEL_WIDTH = 250;
    
    private JFileChooser fc;
    private JList fileList;
    private LibraryListModel listModel;
    
    /**
    * Constructor.
    * This automatically docks to JFileChooser.
    * @param fc JFileChooser to docking.
    */
    LibraryPanel(JFileChooser fc) 
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
    
    private void initGUI() 
    {
        JScrollPane sp;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(PREFERRED_PANEL_WIDTH,1));
        
        // If this class is run as a standalone program, the Globals.messages
        // resource handler might not be initizalized. In this case,
        // an english tag will do.
        if(Globals.messages!=null)
        	add(BorderLayout.NORTH,new JLabel(
        		Globals.messages.getString("lib_in_dir")));
        else 
        	add(BorderLayout.NORTH, new JLabel("Libraries in directory:"));
        
        fileList = new JList(listModel);
        fileList.setCellRenderer(new ListCellRenderer() {
        	@Override
        	public Component getListCellRendererComponent(JList list, 
        		Object value, int index, 
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
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(BorderLayout.CENTER, sp);
        
        // disable focus
        fileList.setFocusable(false);
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) 
    {
        if (evt.getPropertyName().equals(
        	JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
        	listModel.setDirectory(fc.getSelectedFile());
        }
        if (evt.getPropertyName().equals(
        	JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
        	listModel.setDirectory(fc.getCurrentDirectory());
        }
    }
    
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() 
			{
				JFileChooser fc;
				LibraryPanel lp;
				fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setPreferredSize(new Dimension(800,400));
				lp = new LibraryPanel(fc);
			
				fc.showOpenDialog(null);
			}
        });
    }
    
    /**
    * ListModel for libraries.
    */
    class LibraryListModel implements ListModel 
    {
    	private ArrayList<ListDataListener> listeners;
    	private ArrayList<LibraryDesc> libraryList;
    	private File currentDir;
    	
    	LibraryListModel() 
    	{
    		listeners = new ArrayList<ListDataListener>();
    		libraryList = new ArrayList<LibraryDesc>();
    	}
    	
    	public void setDirectory(File dir) 
    	{
    		currentDir = dir;
    		
    		clearList();
    		if (currentDir != null) {
    			if (currentDir.canRead() && currentDir.isDirectory()) {
    				refreshList();
    			}
    		}
    		fireChanged();
    	}
    	
    	private void refreshList() 
    	{
    		File[] files;
    		LibraryDesc desc;
    		
    		files = currentDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) 
				{
					if (f.isFile()) {
						if (f.getName().toLowerCase().matches("^.*\\.fcl$")) {
							return true;
						}
					}
					return false;
				}
    		});
    		
    		for (File f : files) {
    			desc = new LibraryDesc();
    			desc.filename = f.getName();
    			desc.libraryName = getLibraryName(f);
    			libraryList.add(desc);
    		}

    		Collections.sort(libraryList, new Comparator<LibraryDesc>() {
				@Override
				public int compare(LibraryDesc ld1, LibraryDesc ld2) 
				{
					// Sorting with case sensitive
					// If need Windows like sorting,
					// use String.compareToIgnoreCase
					return ld1.filename.compareTo(ld2.filename);
				}
				@Override
				public boolean equals(Object obj) {
					return false;
				}
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
    	
    	private String getLibraryName(File f) {
    		// if unified api for library is implemented,replace this section
    		
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
    			
    		} catch (Exception e) {
    			// return null for libraryName
    		} finally {
    			try {
    				if (br != null) {
    					br.close();
    				}
    				if (fr != null) {
    					fr.close();
    				}
            } catch (Exception e) { }
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
    class LibraryDesc 
    {
    	public String filename;
    	public String libraryName;
    	
    	@Override
    	public String toString() {
    		return String.format("%s (%s)", filename, libraryName);
    	}
    }
    
    /**
    * Dummy icon class for spacing.
    */
    class SpaceIcon implements Icon 
    {
    	private int width;
    	private int height;
    	
    	SpaceIcon(int width, int height) 
    	{
    		this.width = width;
    		this.height = height;
    	}
    	
    	@Override
    	public int getIconHeight() 
    	{
    		return height;
    	}
    	
    	@Override
    	public int getIconWidth() 
    	{
    		return width;
    	}
    	
    	@Override
    	public void paintIcon(Component c, Graphics g, int x, int y) 
    	{
    		// NOP
    	}
    }
}

