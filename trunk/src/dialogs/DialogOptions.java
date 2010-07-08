package dialogs;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.io.*;

import globals.*;
import dialogs.*;


/** DialogOptions.java v.1.8

	The dialogOptions class implements a modal dialog, which allows the user to 
	choose which circuit drawing options (size, anti aliasing, profiling) should
	be activated.

  <pre>

   ****************************************************************************
   Version History 

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     December 2007		D. Bucci    First working version
1.1		January 2008		D. Bucci	Internationalized
1.2  	May 2008			D. Bucci	Grid handling
1.3		July 2008			D. Bucci	Library dir selection
1.4	    August 2008		    D. Bucci	Icon size and text 
1.5     January 2009        D. Bucci    FCJ extensions
1.6		February 2009		D. Bucci	Tabbed view
										Quaqua options
1.7		June 2009			D. Bucci 	Capitalize the first letters 
1.8		November 2009		D. Bucci	Select macro font

                                     
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

	Copyright 2007-2009 by Davide Bucci
</pre>
*/
public class DialogOptions extends JDialog implements ComponentListener 
{
  	private static final int MIN_WIDTH=500;
  	private static final int MIN_HEIGHT=350;
  	
  	public double zoomValue;
  	public boolean profileTime;
  	public boolean antiAlias;
  	public boolean textToolbar;
  	public boolean smallIconsToolbar;
  	public int gridSize;
  	public boolean extFCJ_s;
  	public boolean extFCJ_c;
  	public boolean extStrict;
  	public boolean split_n_s;
  	public boolean split_n_c;
  	
  	public double stroke_size_straight_i;
	public double stroke_size_oval_i;
	
	public double connectionSize_i;

  	public boolean quaquaActive;
  	public String libDirectory;
  	
  	public int pcblinewidth_i;
  	public int pcbpadwidth_i;
  	public int pcbpadheight_i;
  	public int pcbpadintw_i;
  	public String macroFont;
  	public int macroSize_i;
  	
  	
 	private JFrame parent;

  	
	private JCheckBox antiAlias_CB;
	private JCheckBox profile_CB;
	private JCheckBox extFCJ_c_CB;
	private JCheckBox extFCJ_s_CB;
	private JCheckBox extStrict_CB;
	private JCheckBox split_n_s_CB;
	private JCheckBox split_n_c_CB;

	private JComboBox zoom;  	
	private JTextField gridWidth;
	private JTextField libD;
	private JCheckBox textToolbar_CB;
	private JCheckBox smallIconsToolbar_CB;
	private JTextField pcblinewidth;
	private JTextField pcbpadwidth;
	private JTextField pcbpadheight;
	private JTextField pcbpadintw;
	private JTextField connectionSize;
	private JTextField macroSize;
	
	private JTextField stroke_size_straight;
	private JTextField stroke_size_oval;
	
	private JCheckBox quaquaActive_CB;
	private JComboBox comboFont;
	
	/** Standard constructor
	
	@param pa the parent frame
	@param z the current zoom
	@param p the current profile status
	@param a the current anti aliasing state
	@param gs the current grid activation state
	@param libDir the current library directory
	@param tt the current text in the toolbar state
	@param sit the current small icon state
	@param plw the current PCB line width
	@param pw the current PCB pad width
	@param ph the current PCB pad height
	@param piw the current PCB bad internal hole diameter
	@param es the current save using FidoCadJ extensions state
	@param ec the current copy using FidoCadJ extensions state
	@param qq the current Quaqua state
	@param mf the current Macro font
	@param sn split non standard macros during save
	@param sc split non standard macros during copy
	
	
	*/
	
	public DialogOptions (JFrame pa, double z, boolean p, boolean a,
  						  int gs, String libDir, boolean tt, boolean sit,
  						  int plw, int pw, int ph, int piw, boolean es, 
  						  boolean ec, boolean qq, boolean ex, String mf,
  						  boolean sn, boolean sc, double sssi, double ssoi,
  						  double ccs, int ms)
  	{
  		super(pa, Globals.messages.getString("Cir_opt_t"), true);
  		addComponentListener(this);	

		parent=pa;
  		zoomValue=z;
  		profileTime=p;
  		antiAlias=a;
  		gridSize=gs;
  		libDirectory = libDir;
  		textToolbar=tt;
  		smallIconsToolbar=sit;
  		extFCJ_s=es;
  		extFCJ_c=ec;
  		quaquaActive=qq;
  		extStrict=ex;
  		macroFont = mf;
  		split_n_s = sn;
  		split_n_c = sc;
  		
  		pcblinewidth_i = plw;
  		pcbpadwidth_i = pw;
  		pcbpadheight_i = ph;
  		pcbpadintw_i = piw;
  		connectionSize_i=ccs;
  		macroSize_i = ms;
  		
		stroke_size_straight_i =sssi;
		stroke_size_oval_i=ssoi;

  		
  		setSize(600,500);

		GridBagConstraints constraints=new GridBagConstraints();
		Container contentPane=getContentPane();
		
		contentPane.setLayout(new GridBagLayout());
			
		JTabbedPane tabsPane = new JTabbedPane();
		


		tabsPane.addTab(Globals.messages.getString("Restart"), 
			createRestartPane());

		
		tabsPane.addTab(Globals.messages.getString("Drawing"),
			createDrawingOptPanel());

		tabsPane.addTab(Globals.messages.getString("PCBsizes"),
			createPCBsizePanel());

		
		tabsPane.addTab(Globals.messages.getString("FidoCad"), 
			createExtensionsPanel());
		
		constraints.gridx=0;
		constraints.gridy=0;
		constraints.gridwidth=3;
		constraints.gridheight=1;
		constraints.insets=new Insets(6,20,6,20);
		//constraints.anchor=GridBagConstraints.EAST;
		
		contentPane.add(tabsPane, constraints);
		
		
		
		
		
		JButton ok=new JButton(Globals.messages.getString("Ok_btn"));
		JButton cancel=new JButton(Globals.messages.getString("Cancel_btn"));
	
		constraints.gridx=0;
		constraints.gridy=1;
		constraints.gridwidth=3;
		constraints.gridheight=1;
		constraints.insets=new Insets(6,20,20,20);
		constraints.anchor=GridBagConstraints.EAST;
		
		Box b=Box.createHorizontalBox();
		b.add(Box.createHorizontalGlue());
		b.add(cancel);
		b.add(Box.createHorizontalStrut(12));
		ok.setPreferredSize(cancel.getPreferredSize());
		b.add(ok);
		contentPane.add(b, constraints);			// Add cancel button	
		
		
		
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int ng=-1;
				
				antiAlias=antiAlias_CB.isSelected();
				//zoomValue=(double)(zoom.getSelectedIndex());
				profileTime=profile_CB.isSelected();
				textToolbar=textToolbar_CB.isSelected();
  				smallIconsToolbar=smallIconsToolbar_CB.isSelected();
  				extFCJ_s =extFCJ_s_CB.isSelected();
				extFCJ_c =extFCJ_c_CB.isSelected();
				quaquaActive=quaquaActive_CB.isSelected();
				extStrict = extStrict_CB.isSelected();
				macroFont = (String)comboFont.getSelectedItem();
				split_n_s = split_n_s_CB.isSelected(); 				
				split_n_c = split_n_c_CB.isSelected(); 				
				
				try{
					ng=Integer.parseInt(gridWidth.getText().trim());
					libDirectory=libD.getText().trim();
					
					pcblinewidth_i=Integer.parseInt(
						pcblinewidth.getText().trim());
  					
  					pcbpadwidth_i=Integer.parseInt(
  						pcbpadwidth.getText().trim());
  					
  					pcbpadheight_i=Integer.parseInt(
  						pcbpadheight.getText().trim());
  					
  					pcbpadintw_i=Integer.parseInt(
  						pcbpadintw.getText().trim());
					
					stroke_size_straight_i=Double.parseDouble(
  						stroke_size_straight.getText().trim());
					
					stroke_size_oval_i=Double.parseDouble(
  						stroke_size_oval.getText().trim());
					
					connectionSize_i=Double.parseDouble(
  						connectionSize.getText().trim());
  					
  					macroSize_i=Integer.parseInt(macroSize.getText().trim());
  						
				} catch (NumberFormatException E) 
				{
					// ng will remain equal to -1, which is invalid	 
				}
				if(ng>0) 
					gridSize=ng;
				else
					JOptionPane.showMessageDialog(null,
                        Globals.messages.getString("Format_invalid"),
                        "",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                        
				setVisible(false);
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
    
    private JPanel createRestartPane()
    {
		JPanel restartOptionPanel=new JPanel();
		
		restartOptionPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints=new GridBagConstraints();
		restartOptionPanel.setOpaque(false);
		
		
		
		JLabel liblbl=new JLabel(Globals.messages.getString("lib_dir"));
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=0;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor=GridBagConstraints.WEST;
		constraints.insets=new Insets(20,40,6,0);
		restartOptionPanel.add(liblbl, constraints);			// Add lib dir label

		
		libD=new JTextField(10);
		libD.setText(libDirectory);
		constraints.gridx=0;
		constraints.gridy=1;
		constraints.insets=new Insets(6,40,6,0);


		restartOptionPanel.add(libD,constraints);		// Add lib dir tf
		
		JButton libB=new JButton(Globals.messages.getString("Browse"));
		constraints.insets=new Insets(0,0,0,40);
		libB.setOpaque(false);
		constraints.gridx=1;
		constraints.gridy=1;

		restartOptionPanel.add(libB, constraints);		// Add lib dir button
		libB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
			
        		String din;
            	
        		if(Globals.useNativeFileDialogs) {
            		FileDialog fd = new FileDialog(parent, 
					Globals.messages.getString("Select_lib_directory"),
											   FileDialog.LOAD);
					String openFileName;
				
					fd.setDirectory(libD.getText());
					System.setProperty("apple.awt.fileDialogForDirectories", 
						"true");

					fd.setVisible(true);
					System.setProperty("apple.awt.fileDialogForDirectories", 
						"false");

					din=(new File(fd.getDirectory(),fd.getFile())).getPath();
        	
        		} else {
           			JFileChooser fc = new JFileChooser(
					new File(libD.getText()).getPath());
					fc.setDialogTitle(
						Globals.messages.getString("Select_lib_directory"));
					int r = fc.showSaveDialog(null);
					if (r == JFileChooser.APPROVE_OPTION) {
						din=fc.getSelectedFile().getParentFile().getPath();
						
					} else
						din=null;
                
        		}
                
						
				if(din != null) {
					libDirectory=din;			
					libD.setText(libDirectory);
				}
			}
		});
		
		textToolbar_CB=new JCheckBox(Globals.messages.getString("TextToolbar"));
		textToolbar_CB.setSelected(textToolbar);
		textToolbar_CB.setOpaque(false);
		constraints.gridx=0;
		constraints.gridy=3;
		constraints.insets=new Insets(6,40,6,0);



		restartOptionPanel.add(textToolbar_CB, constraints);		// Add text in tb cb	
		
		quaquaActive_CB=new 
			JCheckBox(Globals.messages.getString("Quaqua"));
		quaquaActive_CB.setSelected(quaquaActive);
		quaquaActive_CB.setOpaque(false);
		constraints.gridx=0;
		constraints.gridy=4;
		
		if (Globals.weAreOnAMac) // Check the user wants Quaqua L&F
		{
			restartOptionPanel.add(quaquaActive_CB, constraints);
		}
		
		smallIconsToolbar_CB=new 
			JCheckBox(Globals.messages.getString("SmallIcons"));
		smallIconsToolbar_CB.setSelected(smallIconsToolbar);
		smallIconsToolbar_CB.setOpaque(false);
		constraints.gridx=0;
		constraints.gridy=5;
		constraints.insets=new Insets(6,40,20,0);


	
		restartOptionPanel.add(smallIconsToolbar_CB, constraints);		// Add small icons
		
		
		
		return restartOptionPanel;
	}
    
    private JPanel createDrawingOptPanel()
    {
    	JPanel drawingOptPanel = new JPanel();
    	
	
		GridBagConstraints constraints=new GridBagConstraints();
		drawingOptPanel.setLayout(new GridBagLayout());
		drawingOptPanel.setOpaque(false);
		
		constraints.weightx=100;
		constraints.weighty=100;
		
		
	
		profile_CB=new JCheckBox(Globals.messages.getString("Profile"));
		profile_CB.setOpaque(false);
		profile_CB.setSelected(profileTime);
		constraints.gridx=0;
		constraints.gridy=1;
		constraints.gridwidth=2;
		constraints.gridheight=1;
		if(Globals.isBeta)
			drawingOptPanel.add(profile_CB, constraints);	// Add profiler cb		
		
		JLabel gridlbl=new JLabel(Globals.messages.getString("Grid_width"));
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=2;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.EAST;
		constraints.insets=new Insets(6,6,6,6);

		drawingOptPanel.add(gridlbl, constraints);			// Add Grid label
		
		gridWidth=new JTextField(10);
		gridWidth.setText(""+gridSize);
		constraints.gridx=1;
		constraints.gridy=2;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;

		drawingOptPanel.add(gridWidth, constraints);		// Add grid width tf
		

		/**********************************************************************
		  Stroke sizes
		 **********************************************************************/
		JLabel connectionSizelbl=new JLabel(Globals.messages.getString(
			"connection_size"));
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=8;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		constraints.anchor=GridBagConstraints.EAST;
		drawingOptPanel.add(connectionSizelbl, constraints);
		
		connectionSize=new JTextField(10);
		connectionSize.setText(""+connectionSize_i);
		constraints.gridx=1;
		constraints.gridy=8;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		drawingOptPanel.add(connectionSize, constraints);		
		
		
		JLabel stroke_size_strlbl=new JLabel(Globals.messages.getString(
			"stroke_size_straight"));
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=9;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		constraints.anchor=GridBagConstraints.EAST;
		drawingOptPanel.add(stroke_size_strlbl, constraints);
		
		stroke_size_straight=new JTextField(10);
		stroke_size_straight.setText(""+stroke_size_straight_i);
		constraints.gridx=1;
		constraints.gridy=9;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		drawingOptPanel.add(stroke_size_straight, constraints);		
		
		JLabel stroke_size_ovlbl=new JLabel(Globals.messages.getString(
			"stroke_size_oval"));
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=10;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		constraints.anchor=GridBagConstraints.EAST;
		drawingOptPanel.add(stroke_size_ovlbl, constraints);
		
		stroke_size_oval=new JTextField(10);
		stroke_size_oval.setText(""+stroke_size_oval_i);
		constraints.gridx=1;
		constraints.gridy=10;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		drawingOptPanel.add(stroke_size_oval, constraints);		
		
		/**********************************************************************
		  Macro font
		 **********************************************************************/
		
		
		JLabel macroFontlbl=new JLabel(Globals.messages.getString(
			"macrofont"));
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=11;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		constraints.anchor=GridBagConstraints.EAST;
		drawingOptPanel.add(macroFontlbl, constraints);// Macro font selection

		// Get all installed font families
		GraphicsEnvironment gE;  

		gE = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] s = gE.getAvailableFontFamilyNames();  
		comboFont=new JComboBox();
      	
      	//System.out.println(macroFont);
    	for (int i = 0; i < s.length; ++i) {
      		comboFont.addItem(s[i]);
      		if (s[i].equals(macroFont))
      			comboFont.setSelectedIndex(i);
     	}
		constraints.gridx=1;
		constraints.gridy=11;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		drawingOptPanel.add(comboFont, constraints);	// Add primitive font combo
		
		JLabel macroSizelbl=new JLabel(Globals.messages.getString(
			"macroSize"));
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=12;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		constraints.anchor=GridBagConstraints.EAST;
		drawingOptPanel.add(macroSizelbl, constraints);
		
		macroSize=new JTextField(10);
		macroSize.setText(""+macroSize_i);
		constraints.gridx=1;
		constraints.gridy=12;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		drawingOptPanel.add(macroSize, constraints);				
		
		
		
		antiAlias_CB=new JCheckBox(Globals.messages.getString("Anti_al"));
		antiAlias_CB.setSelected(antiAlias);
		antiAlias_CB.setOpaque(false);
		constraints.gridx=1;
		constraints.gridy=13;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		constraints.insets=new Insets(6,6,6,40);

		drawingOptPanel.add(antiAlias_CB, constraints);		// Add antialias cb		
		
		return drawingOptPanel;
	
		
		/**********************************************************************
		  END of drawingOptPanel
		 **********************************************************************/
		 
	}
	private JPanel createPCBsizePanel()
	{
		/**********************************************************************
		  PCB line and pad default sizes
		 **********************************************************************/
		JPanel pcbSizePanel = new JPanel();
    	
	
		GridBagConstraints constraints=new GridBagConstraints();
		pcbSizePanel.setLayout(new GridBagLayout());
		pcbSizePanel.setOpaque(false);
		
		constraints.weightx=100;
		constraints.weighty=100;
		
		JLabel pcblinelbl=new JLabel(Globals.messages.getString(
			"pcbline_width"));
		constraints.gridx=0;
		constraints.gridy=0;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.EAST;
		pcbSizePanel.add(pcblinelbl, constraints);			// Add pcbline label
		
		pcblinewidth=new JTextField(10);
		pcblinewidth.setText(""+pcblinewidth_i);
		constraints.gridx=1;
		constraints.gridy=0;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		pcbSizePanel.add(pcblinewidth, constraints);		// Add pcbline width tf
		
		JLabel pcbpadwidthlbl=new JLabel(Globals.messages.getString(
			"pcbpad_width"));
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=1;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		constraints.anchor=GridBagConstraints.EAST;

		pcbSizePanel.add(pcbpadwidthlbl, constraints); // Add pcbpad width label
		
		pcbpadwidth=new JTextField(10);
		pcbpadwidth.setText(""+pcbpadwidth_i);
		constraints.gridx=1;
		constraints.gridy=1;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		pcbSizePanel.add(pcbpadwidth, constraints);		// Add pcbpad width tf
		
		JLabel pcbpadheightlbl=new JLabel(Globals.messages.getString(
			"pcbpad_height"));
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=2;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		constraints.anchor=GridBagConstraints.EAST;
		pcbSizePanel.add(pcbpadheightlbl, constraints);	// Add pcbline label
		
		pcbpadheight=new JTextField(10);
		pcbpadheight.setText(""+pcbpadheight_i);
		constraints.gridx=1;
		constraints.gridy=2;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		pcbSizePanel.add(pcbpadheight, constraints);		// Add pcbline height tf
		
		JLabel pcbpadintwlbl=new JLabel(Globals.messages.getString(
			"pcbpad_intw"));
		constraints.weightx=100;
		constraints.weighty=100;
		constraints.gridx=0;
		constraints.gridy=3;
		constraints.gridwidth=1;
		constraints.gridheight=1;	
		constraints.anchor=GridBagConstraints.EAST;
		pcbSizePanel.add(pcbpadintwlbl, constraints);// Add pcbpad int w label
		
		pcbpadintw=new JTextField(10);
		pcbpadintw.setText(""+pcbpadintw_i);
		constraints.gridx=1;
		constraints.gridy=3;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		pcbSizePanel.add(pcbpadintw, constraints);		// Add pcbline width tf
		
		return pcbSizePanel;
	}
	private JPanel createExtensionsPanel()
	{
		/**********************************************************************
		  FidoCadJ extensions
		 **********************************************************************/
		JPanel extensionsPanel = new JPanel();
    	
    	GridBagLayout bgl=new GridBagLayout();
		
		GridBagConstraints constraints=new GridBagConstraints();
		extensionsPanel.setLayout(new GridBagLayout());
		extensionsPanel.setOpaque(false);
		
		constraints.weightx=100;
		constraints.weighty=100;

		extStrict_CB=new JCheckBox(Globals.messages.getString("strict_FC_comp"));
		extStrict_CB.setSelected(extStrict);
		extStrict_CB.setOpaque(false);
		constraints.gridx=0;
		constraints.gridy=0;
		constraints.gridwidth=2;
		constraints.gridheight=1;
		constraints.insets=new Insets(6,40,6,40);
		constraints.anchor=GridBagConstraints.WEST;
		extensionsPanel.add(extStrict_CB, constraints);	// Strict FidoCad
														// compatibility
		
		extFCJ_s_CB=new JCheckBox(Globals.messages.getString("extFCJ_s"));
		extFCJ_s_CB.setSelected(extFCJ_s);
		extFCJ_s_CB.setOpaque(false);
		constraints.gridx=0;
		constraints.gridy=1;
		constraints.gridwidth=2;
		constraints.gridheight=1;
		constraints.insets=new Insets(6,40,6,40);
		constraints.anchor=GridBagConstraints.WEST;
		extensionsPanel.add(extFCJ_s_CB, constraints);		// FCJ extensions while
														// saving
		
		extFCJ_c_CB=new JCheckBox(Globals.messages.getString("extFCJ_c"));
		extFCJ_c_CB.setSelected(extFCJ_c);
		extFCJ_c_CB.setOpaque(false);
		constraints.gridx=0;
		constraints.gridy=2;
		constraints.gridwidth=2;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		extensionsPanel.add(extFCJ_c_CB, constraints);		// FCJ extensions while
														// copying
		
		split_n_s_CB=new JCheckBox(Globals.messages.getString("split_nonstandard"));
		split_n_s_CB.setSelected(split_n_s);
		split_n_s_CB.setOpaque(false);
		constraints.gridx=0;
		constraints.gridy=3;
		constraints.gridwidth=2;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		extensionsPanel.add(split_n_s_CB, constraints);
		
		split_n_c_CB=new JCheckBox(Globals.messages.getString("split_nonstandard_copy"));
		split_n_c_CB.setSelected(split_n_c);
		split_n_c_CB.setOpaque(false);
		constraints.gridx=0;
		constraints.gridy=4;
		constraints.gridwidth=2;
		constraints.gridheight=1;
		constraints.anchor=GridBagConstraints.WEST;
		extensionsPanel.add(split_n_c_CB, constraints);
		/**********************************************************************
		  END of FidoCadJ extensions
		 **********************************************************************/
		 
		return extensionsPanel;
	}
    
  	


}