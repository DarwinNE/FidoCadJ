package net.sourceforge.fidocadj.dialogs;

import net.sourceforge.fidocadj.circuit.CircuitPanel;
import net.sourceforge.fidocadj.circuit.controllers.EditorActions;
import net.sourceforge.fidocadj.circuit.controllers.ParserActions;
import net.sourceforge.fidocadj.circuit.controllers.SelectionActions;
import net.sourceforge.fidocadj.circuit.model.DrawingModel;
import net.sourceforge.fidocadj.export.ExportGraphic;
import net.sourceforge.fidocadj.geom.DrawingSize;
import net.sourceforge.fidocadj.geom.MapCoordinates;
import net.sourceforge.fidocadj.globals.Globals;
import net.sourceforge.fidocadj.globals.LibUtils;
import net.sourceforge.fidocadj.layers.LayerDesc;
import net.sourceforge.fidocadj.primitives.GraphicPrimitive;
import net.sourceforge.fidocadj.primitives.MacroDesc;
import net.sourceforge.fidocadj.primitives.PrimitiveMacro;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.Border;


/** Choose file format, size and options of the graphic exporting.

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
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2012-2014 Phylum2, Davide Bucci
</pre>
    @author Phylum2, Davide Bucci
    
    */

public class DialogSymbolize extends JDialog 
			implements 	ComponentListener, 
						ActionListener
{
    	// Miniumum size for the window.

    private static final int MIN_WIDTH=350;
    private static final int MIN_HEIGHT=250;
 
    final private JPanel parent;    
    private DrawingModel cp;
    
    // Swing elements
    private JComboBox<String> libFilename;
    private JTextField libName;
    private JTextField name;
    private JTextField key;    
    private JComboBox<String> group;
    private JCheckBox snapToGrid;
    
   	myCircuitPanel cpanel = new myCircuitPanel(false);

    
	/** The class myCircuitPanel extends the CircuitPanel class by adding
	    coordinate axis which can be moved.
	*/
	static class myCircuitPanel extends CircuitPanel
	{
		private static final long serialVersionUID = 1L;
		final float dash1[] = {2.0f};
	    final BasicStroke dashed =
	        new BasicStroke(1.0f,
	                        BasicStroke.CAP_BUTT,
	                        BasicStroke.JOIN_MITER,
	                        1.0f, dash1, 1.0f);
	    
		// x and y coordinates of the origin in pixel.
	    private int dx = 20,dy = 20;
	    
	    // x and y coordinates of the origin in logical units.
	    // TODO: improve data encapsulation (these should be private).
	    public int xl=5, yl=5;
	    
	    public int getDx()
	    {
	    	return dx;
	    }
	    
	    public int getDy()
	    {
	    	return dy;
	    }
	    
	    /** Put the origin in the 10,10 logical coordinates.
	    */
	    public void resetOrigin()
	    {
	    	xl=getMapCoordinates().unmapXsnap(10);
		    yl=getMapCoordinates().unmapYsnap(10);
		    	
		    dx=getMapCoordinates().mapXi(xl,yl,false);
		    dy=getMapCoordinates().mapYi(xl,yl,false);
	    }
	    
	    
	    /** Set the new x coordinate of the origin
	    */
	    public void setDx(int dx) 
	    {
		    if (dx < 0 || dx>getWidth())
		    	return;
			this.dx = dx;
		}
		
	    /** Set the new y coordinate of the origin
	    */
		public void setDy(int dy) 
		{
		    if (dy<0 || dy>getHeight())
		    	return;
			this.dy = dy;
		}
		public myCircuitPanel(boolean isEditable) {
			super(isEditable);
		}
		
		/** Show a red cross with dashed line and write "origin" near to the
		    center. This should suggest to the user that it is worth clicking
		    in the origin panel (some users reported they did not see the 
		    cross alone in a first instance).
		*/
		@Override
		public void paintComponent (Graphics g)
		{
			super.paintComponent(g);
			Color c = g.getColor();
			Graphics2D g2 = (Graphics2D) g;
			g.setColor(Color.red);
			Stroke t=g2.getStroke();
		    g2.setStroke(dashed);
		    // Show the origin of axes (red cross)
			g.drawLine(dx, 0, dx, getHeight()); // y
			g.drawLine(0, dy, getWidth(), dy); // x
			
			Font f=new Font("Helvetica",0,12);
			FontMetrics fm = g.getFontMetrics(f);
    		int h = fm.getAscent();
    		int th = h+fm.getDescent();
 				
			g.drawString(Globals.messages.getString("Origin"), 
				dx+5, dy+th+2);
			g.setColor(c);
			g2.setStroke(t);
		}
	}
	    
    /** Gets the library to be created or modified.
    	@return the given library (string description).
    */
    public String getLibraryName() 
    { 
    	String s=libName.getText();
    	return s.trim(); 
    }
    /** Gets the name of the macro to be created
    	@return the name
    */
    public String getMacroName() 
    { 
    	return name.getText(); 
    }
    
    /** Gets the prefix (filename) of the macro to be created
    	@return the filename/prefix
    */
    public String getPrefix()
    {
    	return libFilename.getEditor().getItem().toString();
    }
    
    /**	Gets the group of the macro to be created
    	@return the name of the group
    */
    public String getGroup() 
    {     	
    	return group.getEditor().getItem().toString(); 
    }
    
    /** Handle resizeing of the dialog.
    */
    public void componentResized(ComponentEvent e) 
    {
        int width = getWidth();
        int height = getHeight();
        
        boolean resize = false;
        if (width < MIN_WIDTH) {
            resize = true;
            width = MIN_WIDTH;
         }
         if (height < MIN_HEIGHT) {
            resize = true;
            height = MIN_HEIGHT;
         }
         if (resize) {
            setSize(width, height);
         }
    }
    public void componentMoved(ComponentEvent e) 
    {
    	// Nothing to do
    }
    public void componentShown(ComponentEvent e) 
    {
    	// Nothing to do
    }
    public void componentHidden(ComponentEvent e) 
    {
    	// Nothing to do
    }    
    
    /** List all the libraries available which are not standard in the 
    	libFilename combo box.
    	If there are no non standard libraries, suggest a default name.
    */
	private void enumLibs() 
	{
		libFilename.removeAllItems();
		List<String> lst = new LinkedList<String>();
		Map<String,MacroDesc> m=cpanel.P.getLibrary();
		
		for (Entry<String,MacroDesc> e : m.entrySet()) {
			MacroDesc md = e.getValue();
			// Add only non standard libs.
			if(!lst.contains(md.filename) && 
				!LibUtils.isStdLib(md)) {
				libFilename.addItem(md.filename);
				lst.add(md.filename);
			}
		}

		if (((DefaultComboBoxModel) libFilename.getModel()).getSize() == 0)
			libFilename.addItem("user_lib");
		libFilename.setEditable(true);
	}

	/** Create the GUI for the dialog.
	*/
 	private JPanel createInterfacePanel()
    { 			
    	JPanel panel = new JPanel();

        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints=new GridBagConstraints();
        panel.setLayout(bgl);

        JLabel libraryLabel=new 
            JLabel(Globals.messages.getString("Library_file"));
            
   		constraints = DialogUtil.createConst(1,0,1,1,0,0,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,0,0,0));

        panel.add(libraryLabel, constraints);
        
        libFilename=new JComboBox<String>();      

        String e = null;

   		constraints = DialogUtil.createConst(2,0,1,1,100,100,
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
			new Insets(6,0,0,0));
        
        panel.add(libFilename, constraints);    
        
        JLabel libraryNameLabel=new 
            JLabel(Globals.messages.getString("Library_name"));
            
   		constraints = DialogUtil.createConst(1,1,1,1,0,0,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,0,0,0));

        panel.add(libraryNameLabel, constraints);
    	
    	libName = new JTextField();
    	constraints = DialogUtil.createConst(2,1,1,1,0,0,
			GridBagConstraints.EAST, GridBagConstraints.BOTH, 
			new Insets(6,0,0,0));
    	
    	panel.add(libName, constraints);
    	
    	MouseAdapter ma = new MouseAdapter() {
    	   	boolean grid = false;
    	   	
    	   	/** Mouse click: either show the grid (button 3), or move the 
    	   		origin.
    	   	*/
		   	public void mouseReleased(MouseEvent e)
		   	{      
		      	// Toggle grid visibility, via the secondary mouse button
		      	if (e.getButton()==e.BUTTON3) {
		    	  	grid = !grid;
		    	  	cpanel.setGridVisibility(grid);
		    	  	cpanel.repaint();
		      	} else {
		      		mouseDragged(e);
		      	}
		   	}
    	
			/** Drag the origin of axes using the mouse.
			*/
		    public void mouseDragged(MouseEvent evt)
		    {
		    	int x=evt.getX();
		    	int y=evt.getY();
		    	
		    	if(snapToGrid.isSelected()) {
		    		cpanel.xl=cpanel.getMapCoordinates().unmapXsnap(x);
		    		cpanel.yl=cpanel.getMapCoordinates().unmapYsnap(y);
		    	} else {
		    		cpanel.xl=cpanel.getMapCoordinates().unmapXnosnap(x);
		    		cpanel.yl=cpanel.getMapCoordinates().unmapYnosnap(y);
		    	}
		    	x=cpanel.getMapCoordinates().mapXi(cpanel.xl,cpanel.yl,false);
		    	y=cpanel.getMapCoordinates().mapYi(cpanel.xl,cpanel.yl,false);
		    	cpanel.setDx(x);
		    	cpanel.setDy(y);
		    	cpanel.repaint();		    	 
		    }
		};
		
		// Make sort that the user can move the origin both by clicking
		// and by dragging the mouse pointer. 
		cpanel.addMouseListener(ma);
		cpanel.addMouseMotionListener(ma);
		
		// Reasonable size
    	cpanel.setSize(256, 256);	
    	cpanel.setPreferredSize(new Dimension(256, 256));
    	cpanel.add(Box.createVerticalStrut(256));
    	cpanel.add(Box.createHorizontalStrut(256));
    	
        cpanel.P.setLayers(cp.getLayers());
        cpanel.P.setLibrary(cp.getLibrary()); 
        enumLibs();
        cpanel.antiAlias = true;
        cpanel.profileTime = false; 
        MacroDesc macro = buildMacro("temp","temp","temp","temp", "temp",
        	new Point(100,100));
        	
        cpanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Set the current objects in the preview panel.
        cpanel.getParserActions().addString(
			new StringBuffer(macro.description), false);
		// Calculate an optimum preview size in order to show all elements.
		MapCoordinates m = 
				DrawingSize.calculateZoomToFit(cpanel.P, 
				cpanel.getSize().width*80/100, cpanel.getSize().height*80/100, 
				true);
		m.setXCenter(-m.getXCenter()+10);
		m.setYCenter(-m.getYCenter()+10);
		cpanel.setMapCoordinates(m);
		cpanel.resetOrigin();
		
        constraints = DialogUtil.createConst(3,0,8,8,100,100,
    		GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
    		new Insets(6,6,6,6)); 
        panel.add(cpanel, constraints);          
     
     	JLabel groupLabel=new 
            JLabel(Globals.messages.getString("Group")); // 
            
		constraints = DialogUtil.createConst(1,3,1,1,0,0,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,40,0,0));
        panel.add(groupLabel, constraints);
        
        group=new JComboBox<String>();                
        listGroups();
        if (group.getItemCount()==0)
        	group.addItem(Globals.messages.getString("Group").toLowerCase());
        group.setEditable(true);   
        
        libFilename.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				listGroups();
			}
		});
        
        libFilename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				listGroups();
			}
		});
        
        constraints = DialogUtil.createConst(2,3,1,1,100,100,
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
			new Insets(6,0,0,0));
		panel.add(group, constraints);

        JLabel nameLabel=new 
            JLabel(Globals.messages.getString("Name")); // 
            
		constraints = DialogUtil.createConst(1,4,1,1,0,0,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,40,12,0));
        panel.add(nameLabel, constraints);
        
        name=new JTextField();
        name.setText(
        	Globals.messages.getString("Name").toLowerCase());
        constraints = DialogUtil.createConst(2,4,1,1,100,100,
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
			new Insets(6,0,12,0));

        panel.add(name, constraints);
        
        JLabel nameLabel1=new 
                JLabel(Globals.messages.getString("Key")); // 
                
    	constraints = DialogUtil.createConst(1,5,1,1,0,0,
    		GridBagConstraints.EAST, GridBagConstraints.NONE, 
    		new Insets(6,40,12,0));
        panel.add(nameLabel1, constraints);
            
        key=new JTextField();
        
        long t=System.nanoTime();
        long h=0;
        for(int i=0; t>0; ++i) {
        	t>>=i*8;
        	h^=t & 0xFF;
        }
        
        key.setText(String.valueOf(h));
        constraints = DialogUtil.createConst(2,5,1,1,100,100,
		GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
    			new Insets(6,0,12,0));

        panel.add(key, constraints);

        snapToGrid=new JCheckBox(
        	Globals.messages.getString("SnapToGridOrigin"));
         
       	constraints = DialogUtil.createConst(2,6,1,1,0,0,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,0,0,0));
        panel.add(snapToGrid, constraints);
        
        // Keep in mind the last edited library and group
        if (Globals.lastCLib!=null) 
        	libFilename.setSelectedItem(Globals.lastCLib);
        if (Globals.lastCGrp!=null) 
        	group.setSelectedItem(Globals.lastCGrp);
        
        libFilename.getEditor().selectAll();  
        
        return panel;
    }
    
    /** Obtain all the groups in a given library and put them in the
    	group list.
    */
	private void listGroups() 
	{
		// Obtain all the groups in a given library.
		List<String> l = LibUtils.enumGroups(cp.getLibrary(),
			libFilename.getEditor().getItem().toString());
		
		// Update the group list.
		group.removeAllItems();
        for (String s : l)
        	group.addItem(s);
        	
        libName.setText(LibUtils.getLibName(cp.getLibrary(),
			libFilename.getEditor().getItem().toString()));      
	}
    
	public void actionPerformed(ActionEvent evt)
    {
		//JComboBox<String> source = (JComboBox<String>)evt.getSource();
		//int idx=source.getSelectedIndex();
	}
        
    
    /** Standard constructor        
    */
    public DialogSymbolize (CircuitPanel circuitPanel, DrawingModel p)
    {   
		super((JFrame)null, Globals.messages.getString("SaveSymbol"), true);
    	parent = circuitPanel;
        addComponentListener(this);
        
        // Obtain the current content pane and create the grid layout manager
        // which will be used for putting the elements of the interface.
        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints;
        Container contentPane=getContentPane();
        
        contentPane.setLayout(bgl);   
              
   		constraints = DialogUtil.createConst(2,0,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
			new Insets(12,0,0,20));   		
        
   		setCircuit(p);
   			
        JPanel panel = createInterfacePanel();

        JButton ok=new JButton(Globals.messages.getString("Ok_btn"));
        JButton cancel=new JButton(Globals.messages.getString("Cancel_btn"));
    
    	constraints.gridx=0;
        constraints.gridy=1;
        constraints.gridwidth=4;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.EAST;
        constraints.insets=new Insets(20,20,20,20);
        
        contentPane.add(panel, constraints);
        
        constraints.gridx=0;
        constraints.gridy=2;
        constraints.gridwidth=4;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.EAST;
        constraints.insets=new Insets(20,20,20,20);
        
		// Put the OK and Cancel buttons and make them active.
        Box b=Box.createHorizontalBox();
        b.add(Box.createHorizontalGlue());
		ok.setPreferredSize(cancel.getPreferredSize());

		if (Globals.okCancelWinOrder) {
			b.add(ok);
			b.add(Box.createHorizontalStrut(12));
			b.add(cancel);
		} else {
			b.add(cancel);
			b.add(Box.createHorizontalStrut(12));
			b.add(ok);
		}
		// Add the OK/cancel buttons in the correct order
        contentPane.add(b, constraints);               

        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
            	// Check if there is a valid key available. We can not continue
            	// without a key!
            	if (key.getText().length()<1) {
            		JOptionPane.showMessageDialog(null,
    					Globals.messages.getString("InvKey"),
    					Globals.messages.getString("Symbolize"),
    					JOptionPane.ERROR_MESSAGE);
            		key.requestFocus();
            		return;
            	
            	} else if(LibUtils.checkKey(cp.getLibrary(),
            			getPrefix().trim(),
            			getPrefix().trim()+"."+key.getText().trim())) { 
            		JOptionPane.showMessageDialog(null,
    					Globals.messages.getString("DupKey"),
    					Globals.messages.getString("Symbolize"),    
    					JOptionPane.ERROR_MESSAGE);
            		key.requestFocus(); 
            		return; 
            	} else if(key.getText().contains(" ")) {
            		JOptionPane.showMessageDialog(null,
    					Globals.messages.getString("SpaceKey"),
    					Globals.messages.getString("Symbolize"),    
    					JOptionPane.ERROR_MESSAGE);
            		key.requestFocus(); 
            		return; 
            	}
            	Point p = new Point(200-cpanel.xl, 200-cpanel.yl);
            	MacroDesc macro = buildMacro(getMacroName().trim(),
            		key.getText().trim(),getLibraryName().trim(),
            		getGroup().trim(), getPrefix().trim(),p);
            		
            	cp.getLibrary().put(macro.key, macro); // add to lib
            	
            	System.out.println("key: "+macro.key);
	
				// Save the new symbol in the current libFilename
				try {
					LibUtils.save(cp.getLibrary(), 
						LibUtils.getLibPath(getPrefix()).trim(), 
						getLibraryName(), getPrefix());
				} catch (FileNotFoundException F) {
					JOptionPane.showMessageDialog(null,
    					Globals.messages.getString("DirNotFound"),
    					Globals.messages.getString("Symbolize"),    
    					JOptionPane.ERROR_MESSAGE);
				}
				
            	setVisible(false);   
            	((JFrame)Globals.activeWindow).repaint();
            	
				// Update libs
            	updateTreeLib();
            	
            	Globals.lastCLib = getLibraryName();
                Globals.lastCGrp = getGroup();
            }
        });
        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                setVisible(false);
            }
        });
        
        // Here is an action in which the dialog is closed
        AbstractAction cancelAction = new AbstractAction ()
        {
            public void actionPerformed (ActionEvent e)
            {
                setVisible(false);
            }
        };
        DialogUtil.addCancelEscape (this, cancelAction);                        
        pack();
        DialogUtil.center(this);
        getRootPane().setDefaultButton(ok);
    }
      
	protected MacroDesc buildMacro(String myname, String mykey, String mylib, 
			String mygrp, String myprefix, Point origin) 
	{
       	StringBuilder ss = new StringBuilder();
       	SelectionActions sa = new SelectionActions(cp);
       	EditorActions edt=new EditorActions(cp, sa, null);
       	
		// Check if there is anything selected.
		if (sa.getFirstSelectedPrimitive() == null) 
			return null;
										
		// Move the selected primitives around the origin just
		// determined and add them to the macro description contained
		// in ss.
		
		DrawingModel ps = new DrawingModel();
		try {				
			ps.setLibrary(cp.getLibrary());
			ParserActions pa = new ParserActions(ps);
			
			for (GraphicPrimitive g : cp.getPrimitiveVector()) {
				if (g.getSelected()) {
					pa.addString(new StringBuffer(g.toString(true)), true);	
				}					
			}		
			sa.setSelectionAll(true);
		} catch (Exception e){ 
			e.printStackTrace(); 
		}				
	
		for (GraphicPrimitive psp : ps.getPrimitiveVector()) {
			if (!psp.getSelected()) 
				continue;											
			psp.movePrimitive(origin.x, origin.y);
			ss.append(psp.toString(true)); 						    
		}
						
		parent.repaint();	
		String desc = ss.toString();
		MacroDesc md = new MacroDesc(myprefix+"."+mykey, myname, desc, mygrp, 
			mylib, myprefix);
		return md;	
	}
    
    
	/** Update all the libs shown in the tree.
    */
	protected void updateTreeLib() 
	{
		// TODO:
        // This is a tricky code. What if a new menu option is added?
        // This would be better, but there is something to solve about access.
        // ((FidoFrame)Globals.activeWindow).loadLibraries();
        
		Container cc;
		cc = (JFrame)Globals.activeWindow;
		
		((AbstractButton) ((JFrame) cc).getJMenuBar()
			.getMenu(3).getSubElements()[0].getSubElements()[1]).doClick();
		
	}
	
	/** Sets the drawing database on which to work
		@param p the database
	*/
	public void setCircuit(DrawingModel p) {
		this.cp = p;
	}
}