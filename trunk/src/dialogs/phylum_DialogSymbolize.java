package dialogs;

import export.ExportGraphic;
import geom.MapCoordinates;
import globals.Globals;
import globals.phylum_LibUtils;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
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

import layers.LayerDesc;

import primitives.GraphicPrimitive;
import primitives.MacroDesc;
import primitives.PrimitiveMacro;
import circuit.CircuitPanel;
import circuit.ParseSchem;

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

    Copyright 2012-2013 Phylum2, Davide Bucci
</pre>
    @author Phylum2, Davide Bucci
    
    */

public class phylum_DialogSymbolize extends JDialog 
			implements 	ComponentListener, 
						ActionListener
{
    	// Miniumum size for the window.

    private static final int MIN_WIDTH=350;
    private static final int MIN_HEIGHT=250;
 
    private JPanel parent;    
    private ParseSchem cp;
    
    // Swing elements
    private JComboBox library;       
    private JTextField name;
    private JTextField key;    
    private JComboBox group;
    private JCheckBox snapToGrid;
    
	/** The class myCircuitPanel extends the CircuitPanel class by adding
	    coordinate axis which can be moved.
	*/
	class myCircuitPanel extends CircuitPanel
	{

		/**
		 * 
		 */
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
			g.setColor(c);
			g2.setStroke(t);
		}
	}
	
	myCircuitPanel jj = new myCircuitPanel(false);
    
    /** Gets the library to be created or modified.
    	@return the given library (string description).
    */
    public String getLibrary() 
    { 
    	// DB: I do not understand why splitting with the regex.
    	String s=library.getSelectedItem().toString();//.split("::")[0]
    	return s.trim(); 
    }
    /** Gets the name of the macro to be created
    	@return the name
    */
    public String getName() 
    { 
    	return name.getText(); 
    }
    /**	Gets the group of the macro to be created
    	@return the name
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
    }
    public void componentShown(ComponentEvent e) 
    {
    }
    public void componentHidden(ComponentEvent e) 
    {
    }    
    
    /** List all the libraries available which are not standard in the library
    	combo box.
    	If there are no non standard libraries, suggest a default name.
    */
	private void enumLibs() 
	{
		library.removeAllItems();
		List lst = new LinkedList();
		Map<String,MacroDesc> m=jj.P.getLibrary();
		for (Entry<String,MacroDesc> e : m.entrySet()) {
			MacroDesc md = e.getValue();
			// Add only non standard libs.
			if(!lst.contains(md.library) && 
				!phylum_LibUtils.isStdLib(md.library)) {
				library.addItem(md.library);
				lst.add(md.library);
			}
		}

		if (((DefaultComboBoxModel) library.getModel()).getSize() == 0)
			library.addItem("User lib");
		library.setEditable(true);
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
            JLabel(Globals.messages.getString("Library"));
            
   		constraints = DialogUtil.createConst(1,0,1,1,0,0,
			GridBagConstraints.EAST, GridBagConstraints.BOTH, 
			new Insets(6,0,0,0));

        panel.add(libraryLabel, constraints);
        
        library=new JComboBox();      

        String e = null;
                

   		constraints = DialogUtil.createConst(2,0,1,1,100,100,
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
			new Insets(6,0,0,0));
        
        panel.add(library, constraints);     	
    	    	        
    	jj.addMouseListener(new MouseAdapter() {
    	   	boolean grid = false;
		   	public void mousePressed(MouseEvent e)
		   	{      
		      	// Toggle grid visibility, via the secondary mouse button
		      	if (e.getButton()==e.BUTTON3) {
		    	  	grid = !grid;
		    	  	jj.setGridVisibility(grid);
		    	  	jj.repaint();
		      	}
		   	}
		});
    	jj.addMouseMotionListener(new MouseAdapter() {
			/** Drag the origin of axes using the mouse.
			*/
		    public void mouseDragged(MouseEvent evt)
		    {
		    	int x=evt.getX();
		    	int y=evt.getY();
		    	
		    	if(snapToGrid.isSelected()) {
		    		jj.xl=jj.getMapCoordinates().unmapXsnap(x);
		    		jj.yl=jj.getMapCoordinates().unmapYsnap(y);
		    	} else {
		    		jj.xl=jj.getMapCoordinates().unmapXnosnap(x);
		    		jj.yl=jj.getMapCoordinates().unmapYnosnap(y);
		    	}
		    	x=jj.getMapCoordinates().mapXi(jj.xl,jj.yl,false);
		    	y=jj.getMapCoordinates().mapYi(jj.xl,jj.yl,false);
		    	jj.setDx(x);
		    	jj.setDy(y);
		    	jj.repaint();		    	 
		    }
		});    	
    	jj.setSize(256, 256);	
    	jj.setPreferredSize(new Dimension(256, 256));
    	jj.add(Box.createVerticalStrut(256));
    	jj.add(Box.createHorizontalStrut(256));
    	
        jj.P.setLayers(cp.getLayers());
        jj.P.setLibrary(cp.getLibrary()); 
        enumLibs();
        jj.antiAlias = true;
        jj.profileTime = false; 
        MacroDesc macro = BuildMacro("temp","temp","temp","temp",
        	new Point(100,100));
        	
        jj.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Set the current objects in the preview panel.
        try {
			jj.P.addString(new StringBuffer(macro.description),	false);
		} catch (IOException e1) {}
		// Calculate an optimum preview size in order to show all elements.
		MapCoordinates m = 
				ExportGraphic.calculateZoomToFit(jj.P, 
				jj.getSize().width*80/100, jj.getSize().height*80/100, 
				true);
		m.setXCenter(m.getXCenter()+10);
		m.setYCenter(m.getYCenter()+10);
		jj.setMapCoordinates(m);
		jj.resetOrigin();
		
        constraints = DialogUtil.createConst(3,0,8,8,100,100,
    		GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
    		new Insets(6,6,6,6)); 
        panel.add(jj, constraints);          
     
     	JLabel groupLabel=new 
            JLabel(Globals.messages.getString("Group")); // 
            
		constraints = DialogUtil.createConst(1,3,1,1,0,0,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,40,0,0));
        panel.add(groupLabel, constraints);
        
        group=new JComboBox();                
        listGroups();
        if (group.getItemCount()==0)
        	group.addItem((Globals.messages.getString("Group").toLowerCase()));
        group.setEditable(true);   
        
        library.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				listGroups();
			}
		});
        
        library.addActionListener(new ActionListener() {
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
        name.setText(Globals.messages.getString("Name").toLowerCase());
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
        key.setText(String.valueOf(System.nanoTime()));
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
        
        if (Globals.lastCLib!=null) 
        	library.setSelectedItem(Globals.lastCLib);
        if (Globals.lastCGrp!=null) 
        	group.setSelectedItem(Globals.lastCGrp);
        
        library.getEditor().selectAll();  
        
        return panel;
    }
    
    /** Obtain all the groups in a given library and put them in the
    	group list.
    
    */
	protected void listGroups() 
	{
		// Obtain all the groups in a given library.
		List<String> l = phylum_LibUtils.enumGroups(cp.getLibrary(),
			library.getEditor().getItem().toString());
		
		// Update the group list.
		group.removeAllItems();
        for (String s : l)
        	group.addItem(s);	
	}
    
	public void actionPerformed(ActionEvent evt)
    {
		JComboBox source = (JComboBox)evt.getSource();
		int idx=source.getSelectedIndex();
	}
        
    
    /** Standard constructor        
    */
    public phylum_DialogSymbolize (CircuitPanel circuitPanel,ParseSchem p)
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
            		key.requestFocus(); 
            		return; 
            	}
            	Point p = new Point(200-jj.xl, 200-jj.yl);
            	MacroDesc macro = BuildMacro(getName().trim(),
            		key.getText().trim(),getLibrary().trim(),getGroup().trim(),
            		p);            	
            	
            	cp.getLibrary().put(key.getText(), macro); // add to lib	
				
				// Save the new symbol in the current library
				phylum_LibUtils.save(cp.getLibrary(), 
					phylum_LibUtils.getLibPath(getLibrary()).trim(), 
					getLibrary());							
				
            	setVisible(false);   
            	Globals.activeWindow.repaint();
            	
				// Update libs
            	updateTreeLib();
            	
            	Globals.lastCLib = getLibrary();
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
      
	protected MacroDesc BuildMacro(String myname, String mykey, String mylib, 
			String mygrp, Point origin) 
	{
       	StringBuilder ss = new StringBuilder();
       	
		// Check if there is anything selected.
		if (cp.getFirstSelectedPrimitive() == null) 
			return null;
										
		// Move the selected primitives around the origin just
		// determined and add them to the macro description contained
		// in ss.
		
		ParseSchem ps = new ParseSchem();
		try {				
			ps.setLibrary(cp.getLibrary());
			for (GraphicPrimitive p : cp.getPrimitiveVector()) {
				if (p.getSelected()) 
					ps.addString(new StringBuffer(p.toString(true)), true);						
			}		
			ps.selectAll();
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
		// Create the symbol key from the date and hour				
		String k = mykey;
		String desc = ss.toString();
		MacroDesc md = new MacroDesc(k, myname, desc, mygrp, mylib);
		return md;	
	}
    
    
	/** Update all the libs shown in the tree.
    */
	protected void updateTreeLib() 
	{
        // This is a dangerous code. What if a new menu option is added?
        // This would be better, but there is something to solve about access.
        // ((FidoFrame)Globals.activeWindow).loadLibraries();
        
		Container cc;
		cc = Globals.activeWindow;
		
		((AbstractButton) ((JFrame) cc).getJMenuBar()
			.getMenu(3).getSubElements()[0].getSubElements()[1]).doClick();
		
	}
	
	/** Sets the drawing database on which to work
		@param p the database
	*/
	public void setCircuit(ParseSchem p) {
		this.cp = p;
	}        

}