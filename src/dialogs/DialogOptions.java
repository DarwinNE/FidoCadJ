package dialogs;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.io.*;

import globals.*;
import dialogs.*;

/** The dialogOptions class implements a modal dialog, which allows the user to 
	choose which circuit drawing options (size, anti aliasing, profiling) should
	be activated.

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

	Copyright 2007-2012 by Davide Bucci
</pre>
*/
public class DialogOptions extends JDialog implements ComponentListener 
{
  	private static final int MIN_WIDTH=600;
  	private static final int MIN_HEIGHT=450;
  	
  	public double zoomValue;
  	public boolean profileTime;
  	public boolean antiAlias;
  	public boolean textToolbar;
  	public boolean smallIconsToolbar;
  	public int gridSize;
  	//public boolean extFCJ_s;
  	//public boolean extFCJ_c;
  	public boolean extStrict;
  	public boolean split_n_s;
  	public boolean split_n_c;
  	public boolean shiftCP;
  	
  	public double stroke_size_straight_i;
	//public double stroke_size_oval_i;
	
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
	//private JCheckBox extFCJ_c_CB;
	//private JCheckBox extFCJ_s_CB;
	private JCheckBox extStrict_CB;
	private JCheckBox split_n_s_CB;
	private JCheckBox split_n_c_CB;
	
	private JCheckBox shiftCP_CB;

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
	// private JTextField stroke_size_oval;
	
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
	@param qq the current Quaqua state
	@param mf the current Macro font
	@param sn split non standard macros during save
	@param sc split non standard macros during copy
	@param sssi stroke width to be used for segments and straight lines
	@param ssoi	stroke width to be used for ovals
	@param ccs connection size
	@param ms text height for macros
	@param sdcp shift during copy and paste
	
	*/
	
	public DialogOptions (JFrame pa, double z, boolean p, boolean a,
  						  int gs, String libDir, boolean tt, boolean sit,
  						  int plw, int pw, int ph, int piw,
  						  boolean qq, boolean ex, String mf,
  						  boolean sn, boolean sc, double sssi, double ssoi,
  						  double ccs, int ms, boolean sdcp)
  	{
  		super(pa, Globals.messages.getString("Cir_opt_t"), true);
  		addComponentListener(this);	

		shiftCP=sdcp;
		parent=pa;
  		zoomValue=z;
  		profileTime=p;
  		antiAlias=a;
  		gridSize=gs;
  		libDirectory = libDir;
  		textToolbar=tt;
  		smallIconsToolbar=sit;
  		//extFCJ_s=es;
  		//extFCJ_c=ec;
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
		//stroke_size_oval_i=ssoi;

  		setSize(600,500);

		GridBagConstraints constraints;
		Container contentPane=getContentPane();
		
		contentPane.setLayout(new GridBagLayout());
			
		JTabbedPane tabsPane = new JTabbedPane();
		
		// Creates four panes and then populate them.
		tabsPane.addTab(Globals.messages.getString("Restart"), 
			createRestartPane());

		tabsPane.addTab(Globals.messages.getString("Drawing"),
			createDrawingOptPanel());

		tabsPane.addTab(Globals.messages.getString("PCBsizes"),
			createPCBsizePanel());

		tabsPane.addTab(Globals.messages.getString("FidoCad"), 
			createExtensionsPanel());
		
		constraints = DialogUtil.createConst(0,0,3,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.BOTH, 
			new Insets(6,20,6,20));

		contentPane.add(tabsPane, constraints);
		
    	// Put the OK and Cancel buttons and make them active.

		JButton ok=new JButton(Globals.messages.getString("Ok_btn"));
		JButton cancel=new JButton(Globals.messages.getString("Cancel_btn"));

		constraints = DialogUtil.createConst(0,1,3,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.BOTH, 
			new Insets(6,20,20,20));
	
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
		contentPane.add(b, constraints);			// Add cancel button	
		
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int ng=-1;
				shiftCP=shiftCP_CB.isSelected();
				antiAlias=antiAlias_CB.isSelected();
				//zoomValue=(double)(zoom.getSelectedIndex());
				profileTime=profile_CB.isSelected();
				textToolbar=textToolbar_CB.isSelected();
  				smallIconsToolbar=smallIconsToolbar_CB.isSelected();
  				//extFCJ_s =extFCJ_s_CB.isSelected();
				//extFCJ_c =extFCJ_c_CB.isSelected();
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
					
					//stroke_size_oval_i=Double.parseDouble(
  					//	stroke_size_oval.getText().trim());
					
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
  
  	/** Ensure that the dialog can not be resized to a size smaller than
  		a certain limit (see MIN_WIDTH and MIN_HEIGHT constants).
  	
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
    
    // Creates the panel dedicated to the startup options of FidoCadJ.
    private JPanel createRestartPane()
    {
		JPanel restartOptionPanel=new JPanel();
		
		restartOptionPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints=new GridBagConstraints();
		restartOptionPanel.setOpaque(false);
		
		JLabel liblbl=new JLabel(Globals.messages.getString("lib_dir"));
		
		constraints = DialogUtil.createConst(0,0,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
			new Insets(20,40,6,0));

		// Add lib dir label
		restartOptionPanel.add(liblbl, constraints);			

		libD=new JTextField(10);
		libD.setText(libDirectory);
		constraints = DialogUtil.createConst(0,1,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 
			new Insets(6,40,6,20));

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
        			// Use the native (AWT) file dialogs instead of Swing's
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
					if(fd.getDirectory()!= null && fd.getFile() != null) 
						din=(new File(fd.getDirectory(),fd.getFile())).
							getPath();
					else
						din = "";
        	
        		} else {
        			// Use Swing's file dialog.
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
		constraints = DialogUtil.createConst(0,3,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 
			new Insets(6,40,6,0));
		
		// Add text in tb cb	
		restartOptionPanel.add(textToolbar_CB, constraints);		
		
		quaquaActive_CB=new 
			JCheckBox(Globals.messages.getString("Quaqua"));
		quaquaActive_CB.setSelected(quaquaActive);
		quaquaActive_CB.setOpaque(false);
		
		constraints = DialogUtil.createConst(0,4,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 
			new Insets(6,40,6,20));
		
		if (Globals.weAreOnAMac) // Check the user wants Quaqua L&F
		{
			restartOptionPanel.add(quaquaActive_CB, constraints);
		}
		
		smallIconsToolbar_CB=new 
			JCheckBox(Globals.messages.getString("SmallIcons"));
		smallIconsToolbar_CB.setSelected(smallIconsToolbar);
		smallIconsToolbar_CB.setOpaque(false);
		
		constraints = DialogUtil.createConst(0,5,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 
			new Insets(6,40,20,20));
		
		// Add small icons
		restartOptionPanel.add(smallIconsToolbar_CB, constraints);		
		
		return restartOptionPanel;
	}

    // Creates the panel dedicated to the drawing options of FidoCadJ.    
    private JPanel createDrawingOptPanel()
    {
    	JPanel drawingOptPanel = new JPanel();
    	
	
		GridBagConstraints constraints=new GridBagConstraints();
		drawingOptPanel.setLayout(new GridBagLayout());
		drawingOptPanel.setOpaque(false);
		
		profile_CB=new JCheckBox(Globals.messages.getString("Profile"));
		profile_CB.setOpaque(false);
		profile_CB.setSelected(profileTime);
		
		constraints = DialogUtil.createConst(1,1,2,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, 
			new Insets(6,6,6,20));
			
		if(Globals.isBeta)
			drawingOptPanel.add(profile_CB, constraints);	// Add profiler cb		
		
		JLabel gridlbl=new JLabel(Globals.messages.getString("Grid_width"));
		
		constraints = DialogUtil.createConst(0,2,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		
		drawingOptPanel.add(gridlbl, constraints);			// Add Grid label
		
		gridWidth=new JTextField(10);
		gridWidth.setText(""+gridSize);
		
		constraints = DialogUtil.createConst(1,2,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		
		drawingOptPanel.add(gridWidth, constraints);		// Add grid width tf
		
		/**********************************************************************
		  Stroke sizes
		 **********************************************************************/
		JLabel connectionSizelbl=new JLabel(Globals.messages.getString(
			"connection_size"));
		constraints = DialogUtil.createConst(0,8,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		drawingOptPanel.add(connectionSizelbl, constraints);
		
		connectionSize=new JTextField(10);
		connectionSize.setText(""+connectionSize_i);
		constraints = DialogUtil.createConst(1,8,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		drawingOptPanel.add(connectionSize, constraints);		
		
		
		JLabel stroke_size_strlbl=new JLabel(Globals.messages.getString(
			"stroke_size_straight"));
		constraints = DialogUtil.createConst(0,9,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		drawingOptPanel.add(stroke_size_strlbl, constraints);
		
		stroke_size_straight=new JTextField(10);
		stroke_size_straight.setText(""+stroke_size_straight_i);
		constraints = DialogUtil.createConst(1,9,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
			
		drawingOptPanel.add(stroke_size_straight, constraints);		
		
/*		JLabel stroke_size_ovlbl=new JLabel(Globals.messages.getString(
			"stroke_size_oval"));
		constraints = DialogUtil.createConst(0,10,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		
		// drawingOptPanel.add(stroke_size_ovlbl, constraints);
		
		stroke_size_oval=new JTextField(10);
		stroke_size_oval.setText(""+stroke_size_oval_i);
		constraints = DialogUtil.createConst(1,10,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
			
		//drawingOptPanel.add(stroke_size_oval, constraints);		*/
		
		/**********************************************************************
		  Macro font
		 **********************************************************************/
		
		JLabel macroFontlbl=new JLabel(Globals.messages.getString(
			"macrofont"));
		constraints = DialogUtil.createConst(0,11,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
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
     	constraints = DialogUtil.createConst(1,11,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
			
		drawingOptPanel.add(comboFont, constraints);// Add primitive font combo
		
		JLabel macroSizelbl=new JLabel(Globals.messages.getString(
			"macroSize"));
		constraints = DialogUtil.createConst(0,12,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
			
		drawingOptPanel.add(macroSizelbl, constraints);
		
		macroSize=new JTextField(10);
		macroSize.setText(""+macroSize_i);
		constraints = DialogUtil.createConst(1,12,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		drawingOptPanel.add(macroSize, constraints);				
		
		antiAlias_CB=new JCheckBox(Globals.messages.getString("Anti_al"));
		antiAlias_CB.setSelected(antiAlias);
		antiAlias_CB.setOpaque(false);
		constraints = DialogUtil.createConst(1,13,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,40));
			
		drawingOptPanel.add(antiAlias_CB, constraints);		// Add antialias cb		
		
		shiftCP_CB=new JCheckBox(Globals.messages.getString("Shift_cp"));
		shiftCP_CB.setSelected(shiftCP);
		shiftCP_CB.setOpaque(false);
		constraints = DialogUtil.createConst(1,14,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,40));
		drawingOptPanel.add(shiftCP_CB, constraints);		// Add shift C/P cb
		
		return drawingOptPanel;
	
		
		/**********************************************************************
		  END of drawingOptPanel
		 **********************************************************************/
		 
	}
	
	
    // Creates the panel dedicated to the default PCB track and pad sizes 
    // options of FidoCadJ.
	private JPanel createPCBsizePanel()
	{
		/**********************************************************************
		  PCB line and pad default sizes
		 **********************************************************************/
		JPanel pcbSizePanel = new JPanel();
    	
	
		GridBagConstraints constraints=new GridBagConstraints();
		pcbSizePanel.setLayout(new GridBagLayout());
		pcbSizePanel.setOpaque(false);
		
		JLabel pcblinelbl=new JLabel(Globals.messages.getString(
			"pcbline_width"));
		constraints = DialogUtil.createConst(0,0,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(0,0,0,0));
		pcbSizePanel.add(pcblinelbl, constraints);			// Add pcbline label
		
		pcblinewidth=new JTextField(10);
		pcblinewidth.setText(""+pcblinewidth_i);
		constraints = DialogUtil.createConst(1,0,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
			
		pcbSizePanel.add(pcblinewidth, constraints);	// Add pcbline width tf
		
		JLabel pcbpadwidthlbl=new JLabel(Globals.messages.getString(
			"pcbpad_width"));
		constraints = DialogUtil.createConst(0,1,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		pcbSizePanel.add(pcbpadwidthlbl, constraints); // Add pcbpad width label
		
		pcbpadwidth=new JTextField(10);
		pcbpadwidth.setText(""+pcbpadwidth_i);
		constraints = DialogUtil.createConst(1,1,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		
		pcbSizePanel.add(pcbpadwidth, constraints);		// Add pcbpad width tf
		
		JLabel pcbpadheightlbl=new JLabel(Globals.messages.getString(
			"pcbpad_height"));
		constraints = DialogUtil.createConst(0,2,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		pcbSizePanel.add(pcbpadheightlbl, constraints);	// Add pcbline label
		
		pcbpadheight=new JTextField(10);
		pcbpadheight.setText(""+pcbpadheight_i);
		constraints = DialogUtil.createConst(1,2,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		pcbSizePanel.add(pcbpadheight, constraints);  // Add pcbline height tf
		
		JLabel pcbpadintwlbl=new JLabel(Globals.messages.getString(
			"pcbpad_intw"));
		constraints = DialogUtil.createConst(0,3,1,1,100,100,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		pcbSizePanel.add(pcbpadintwlbl, constraints);// Add pcbpad int w label
		
		pcbpadintw=new JTextField(10);
		pcbpadintw.setText(""+pcbpadintw_i);
		constraints = DialogUtil.createConst(1,3,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,6,6,6));
		pcbSizePanel.add(pcbpadintw, constraints);		// Add pcbline width tf
		
		return pcbSizePanel;
	}
	
    // Creates the panel dedicated to the extensions introduced by FidoCadJ on
    // the original FidoCad file format.
	private JPanel createExtensionsPanel()
	{
		/**********************************************************************
		  FidoCadJ extensions
		 **********************************************************************/
		JPanel extensionsPanel = new JPanel();
    			
		GridBagConstraints constraints=new GridBagConstraints();
		extensionsPanel.setLayout(new GridBagLayout());
		extensionsPanel.setOpaque(false);

		extStrict_CB=new JCheckBox(Globals.messages.getString(
			"strict_FC_comp"));
		extStrict_CB.setSelected(extStrict);
		extStrict_CB.setOpaque(false);
		constraints = DialogUtil.createConst(0,0,2,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,40,6,40));
		extensionsPanel.add(extStrict_CB, constraints);	// Strict FidoCad
														// compatibility
		
		/*extFCJ_s_CB=new JCheckBox(Globals.messages.getString("extFCJ_s"));
		extFCJ_s_CB.setSelected(extFCJ_s);
		extFCJ_s_CB.setOpaque(false);
		constraints = DialogUtil.createConst(0,1,2,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,40,6,40));
		extensionsPanel.add(extFCJ_s_CB, constraints);	// FCJ extensions while
														// saving
		
	
		extFCJ_c_CB=new JCheckBox(Globals.messages.getString("extFCJ_c"));
		extFCJ_c_CB.setSelected(extFCJ_c);
		extFCJ_c_CB.setOpaque(false);
		constraints = DialogUtil.createConst(0,2,2,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,40,6,40));
		extensionsPanel.add(extFCJ_c_CB, constraints);	// FCJ extensions while
														// copying
		*/
		split_n_s_CB=new JCheckBox(Globals.messages.getString(
			"split_nonstandard"));
		split_n_s_CB.setSelected(split_n_s);
		split_n_s_CB.setOpaque(false);
		constraints = DialogUtil.createConst(0,3,2,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,40,6,40));
		
		extensionsPanel.add(split_n_s_CB, constraints); // Split non standard
													// macros when saving files
		
		split_n_c_CB=new JCheckBox(Globals.messages.getString(
			"split_nonstandard_copy"));
		split_n_c_CB.setSelected(split_n_c);
		split_n_c_CB.setOpaque(false);
		constraints = DialogUtil.createConst(0,4,2,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,40,6,40));
		//extensionsPanel.add(split_n_c_CB, constraints);
		/**********************************************************************
		  END of FidoCadJ extensions
		 **********************************************************************/
		return extensionsPanel;
	}
    
}