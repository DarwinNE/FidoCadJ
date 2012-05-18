package dialogs;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.border.*;

import globals.*;


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

    Copyright 2007-2010 by Davide Bucci
</pre>
    @author Davide Bucci
    
    */
    
public class DialogExport extends JDialog implements ComponentListener, 
	ActionListener
{
    private static final int MIN_WIDTH=450;
    private static final int MIN_HEIGHT=400;
    
    private JFrame parent;
    
    private boolean export;     // Indicates that the export should be done
    
    private static final int PNG_INDEX=0;       // Combo list index: png format
    private static final int JPG_INDEX=1;       //  "      "    "  : jpg format
    private static final int SVG_INDEX=2;       // Combo list index: svg format
    private static final int EPS_INDEX=3;       // Combo list index: eps format
    private static final int PGF_INDEX=4;       // Combo list index: pgf format
    private static final int PDF_INDEX=5;       // Combo list index: pgf format
    private static final int SCR_INDEX=6;       // idem: Eagle scr format

    private final double EPS=1E-5;      // Resolution comparison precision
    
    // Swing elements
    private JComboBox resolution;       // Resolution combo box
    private JCheckBox antiAlias_CB;     // AntiAlias checkbox
    private JCheckBox blackWhite_CB;    // Black and white checkbox
    private JComboBox fileFormat;       // File format combo box
    private JTextField fileName;        // File name text field
    private JTextField multiplySizes;   // Size multiplications for vector exp.
    
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
    
    
    /** Indicates that the export should be done: the user selected the "ok"
        button 
        
        @return a boolean value which indicates if the export should be done
    */
    public boolean shouldExport()
    {
        return export;
    }
    
    /** Indicates that the antiAlias should be activated 
    
        @return a boolean value which indicates if the anti alias should be
                activated */
    public boolean getAntiAlias()
    {
        return antiAlias_CB.isSelected();
    }
    
    /** Indicates that the black and white export should be activated 
    
        @return a boolean value which indicates if the export should be
            done in black and white.
    */
    public boolean getBlackWhite()
    {
        return blackWhite_CB.isSelected();
    }
    
    /** Indicates which export format has been selected 
    
        @return a string describing the image format (e.g. "png", "jpg")
    */
    public String getFormat()
    {
        switch(fileFormat.getSelectedIndex()){
            case PNG_INDEX:
                return "png";
            case JPG_INDEX:
                return "jpg";
            case SVG_INDEX:
                return "svg";
            case EPS_INDEX:
                return "eps";
            case PGF_INDEX:
                return "pgf";
            case PDF_INDEX:
                return "pdf";
            
            case SCR_INDEX:
                return "scr";
            
            default:
                System.out.println (
                "dialogExport.getExportFormat Warning: file format set to png");
                return "png";
        }
    }
    
    /** @return a string containing the file name given by the user 
    */
    public String getFileName()
    {
        return fileName.getText();
    }
    
    /** Sets the actual file name 
    
        @param f a string containing the default file name
    */
    public void setFileName(String f)
    {
        fileName.setText(f);
    }
    
    /** Sets the actual anti alias state
        @param a a boolean which indicates the default anti alias state
    */
    public void setAntiAlias(boolean a)
    {
        antiAlias_CB.setSelected(a);

    }

    /** Sets the actual black and white state
        @param a a boolean which indicates the default black and white state
    */
    public void setBlackWhite(boolean a)
    {
        blackWhite_CB.setSelected(a);

    }
    
    /** Set the default unit per pixel value
        @param d the default unit per pixel value
    */
    public void setUnitPerPixel(double d)
    {
        int index=0;

        if (Math.abs(d-0.36)<EPS) index=0;
        if (Math.abs(d-0.75)<EPS) index=1;
        if (Math.abs(d-1.50)<EPS) index=2;
        if (Math.abs(d-3.00)<EPS) index=3;
        if (Math.abs(d-6.00)<EPS) index=4;
        
        resolution.setSelectedIndex(index);
    }
    
    /** Set the magnification factor for vector format export
        @param d the default unit per pixel value
    */
    public void setMagnification(double d)
    {      
        multiplySizes.setText(""+d);
    }
    /** Get the magnification factor for vector format export
        @param d the default unit per pixel value
    */
    public double getMagnification()
    {      
        return Double.parseDouble(multiplySizes.getText());
    }
    /** Get the default unit per pixel value 
        @return the unit per pixel value
    */
    public double getUnitPerPixel()
    {
        int index=resolution.getSelectedIndex();
        if(index==0) return 0.36;
        if(index==1) return 0.75;
        if(index==2) return 1.50;
        if(index==3) return 3.00;
        if(index==4) return 6.00;
        
        return 0.36;
        
    }
    
    /** Sets the default export format. 
    
        @param s The default expor format. If the format string is not
            recognized (valid strings are {"png"|"jpg"}), prints on System.out
            a warning message and use the png format. 
    */
    public void setFormat(String s)
    {
        if (s.equals("png")) {
            fileFormat.setSelectedIndex(PNG_INDEX);
        } else if (s.equals("jpg")) {
            fileFormat.setSelectedIndex(JPG_INDEX);
        } else if (s.equals("svg")) {
            fileFormat.setSelectedIndex(SVG_INDEX);
        } else if (s.equals("eps")) {
            fileFormat.setSelectedIndex(EPS_INDEX);
        } else if (s.equals("pgf")) {
            fileFormat.setSelectedIndex(PGF_INDEX);
        } else if (s.equals("pdf")) {
            fileFormat.setSelectedIndex(PDF_INDEX);
        } else if (s.equals("scr")) {
            fileFormat.setSelectedIndex(SCR_INDEX);
        } else {
            System.out.println(
              "dialogExport.setExportFormat: format set to png");
            fileFormat.setSelectedIndex(PNG_INDEX);
        }
    }
    
    private JPanel createInterfacePanel()
    {
    	JPanel panel = new JPanel();

        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints=new GridBagConstraints();
        panel.setLayout(bgl);

        JLabel resolutionLabel=new 
            JLabel(Globals.messages.getString("Resolution"));
            
   		constraints = DialogUtil.createConst(1,0,1,1,0,0,
			GridBagConstraints.EAST, GridBagConstraints.BOTH, 
			new Insets(6,40,6,6));

        panel.add(resolutionLabel, constraints);
        
        resolution=new JComboBox();
        resolution.addItem("72x72 dpi");
        resolution.addItem("150x150 dpi");
        resolution.addItem("300x300 dpi");
        resolution.addItem("600x600 dpi");
        resolution.addItem("1200x1200 dpi");
        
   		constraints = DialogUtil.createConst(2,0,1,1,100,100,
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
			new Insets(6,0,0,0));
        
        panel.add(resolution, constraints);
        
        antiAlias_CB=new JCheckBox(Globals.messages.getString("Anti_aliasing"));

		constraints = DialogUtil.createConst(2,1,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
			new Insets(6,0,0,0));

        panel.add(antiAlias_CB, constraints);     // Add antialias cb
        
        blackWhite_CB=new JCheckBox(Globals.messages.getString("B_W"));
       
        constraints = DialogUtil.createConst(2,2,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.NONE, 
			new Insets(6,0,0,0));
       
        panel.add(blackWhite_CB, constraints);        // Add antialias cb
        
        JLabel multiplySizesLabel=new 
            JLabel(Globals.messages.getString("Multiply_sizes"));
            
		constraints = DialogUtil.createConst(1,3,1,1,0,0,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,40,0,0));
        panel.add(multiplySizesLabel, constraints);
        
        multiplySizes=new JTextField();
        constraints = DialogUtil.createConst(2,3,1,1,100,100,
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
			new Insets(6,0,0,0));
		panel.add(multiplySizes, constraints);

        JLabel fileNameLabel=new 
            JLabel(Globals.messages.getString("File_name"));
            
		constraints = DialogUtil.createConst(1,4,1,1,0,0,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(6,40,12,0));
        panel.add(fileNameLabel, constraints);
        
        fileName=new JTextField();
        constraints = DialogUtil.createConst(2,4,1,1,100,100,
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
			new Insets(6,0,12,0));
			
        panel.add(fileName, constraints);
        
        // See request #3526600
        // fileName.setEditable(false);
        
        JButton browse=new JButton(Globals.messages.getString("Browse"));
		constraints = DialogUtil.createConst(3,4,1,1,0,0,
			GridBagConstraints.CENTER, GridBagConstraints.NONE, 
			new Insets(6,0,12,12));

        panel.add(browse, constraints);

        browse.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
				// Open the browser in order to let the user select the file
				// name on which export
                
                if(Globals.useNativeFileDialogs) {
                
                	// Native file dialog
                    FileDialog fd = new FileDialog(parent, 
                    Globals.messages.getString("Select_file_export"),
                                               FileDialog.SAVE);
                    String filen;
                
                	// Set defaults and make visible.
                    fd.setDirectory(new File(fileName.getText()).getPath());
                    fd.setVisible(true);
                
                    // The user has selected a file.
                    if(fd.getFile() != null) {
                        filen=Globals.createCompleteFileName(
                            fd.getDirectory(),
                            fd.getFile());          
                        fileName.setText(filen);
                    }
            
                } else {
                	// Swing file dialog
                    JFileChooser fc = new JFileChooser(
                        new File(fileName.getText()).getPath());
                    int r = fc.showSaveDialog(null);
                    if (r == JFileChooser.APPROVE_OPTION) {
                        fileName.setText(fc.getSelectedFile().toString());
                    }   
                } 
            }
        });  
        
        return panel;
    }
    
    
    public void actionPerformed(ActionEvent evt)
    {
		JComboBox source = (JComboBox)evt.getSource();
		int idx=source.getSelectedIndex();
				
		// Once the index of the selected item is obtained, we proceed
		// by checking if it is a bitmap format
		if(idx==0 || idx == 1) {
			// It is a bitmap based export
			resolution.setEnabled(true);     // Resolution combo box
    		antiAlias_CB.setEnabled(true);   // AntiAlias checkbox
    		blackWhite_CB.setEnabled(true);  // Black and white checkbox
    		multiplySizes.setEnabled(false); // Size multiplications
    		multiplySizes.setText("1.0");
    	} else {
			// It is a vector based export
			resolution.setEnabled(false);     // Resolution combo box
    		antiAlias_CB.setEnabled(false);   // AntiAlias checkbox
    		blackWhite_CB.setEnabled(true);  // Black and white checkbox
    		multiplySizes.setEnabled(true); // Size multiplications
		}
	}
        
    
    /** Standard constructor: it needs the parent frame.
        @param p the dialog's parent
    */
    public DialogExport (JFrame p)
    {
    
        super(p,Globals.messages.getString("Circ_exp_t"), true);
 		// Ensure that under MacOSX >= 10.5 Leopard, this dialog will appear
  		// as a document modal sheet
  		
  		getRootPane().putClientProperty("apple.awt.documentModalSheet", 
				Boolean.TRUE);
        
        addComponentListener(this); 
        export=false;
        parent=p;
              
        // Obtain the current content pane and create the grid layout manager
        // which will be used for putting the elements of the interface.
        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints=new GridBagConstraints();
        Container contentPane=getContentPane();
        
        contentPane.setLayout(bgl);

		// The first thing we need to put is the combobox describing the file
		// format to be used. This is important, since part of the remaining
		// dialog should be changed depending on the format chosen.
        JLabel fileFormatLabel=new 
            JLabel(Globals.messages.getString("File_format"));
            
   		constraints = DialogUtil.createConst(1,0,1,1,0,0,
			GridBagConstraints.EAST, GridBagConstraints.NONE, 
			new Insets(12,40,0,0));

        contentPane.add(fileFormatLabel, constraints);
        
        fileFormat=new JComboBox();
        fileFormat.addItem("PNG (Bitmap)");
        fileFormat.addItem("JPG (Bitmap)");
        fileFormat.addItem("SVG (Vector, Scalable Vector Graphic)");
        fileFormat.addItem("EPS (Vector, Encapsulated Postscript)");
        fileFormat.addItem("PGF (Vector, PGF packet for LaTeX)");
        fileFormat.addItem("PDF (Vector, Portable Document File)");
        fileFormat.addItem("CadSoft Eagle SCR (Script)");

        fileFormat.setSelectedIndex(0);
              
   		constraints = DialogUtil.createConst(2,0,1,1,100,100,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
			new Insets(12,0,0,20));
        contentPane.add(fileFormat, constraints);
        
        // We need to track when the user changes the file format since some
        // options will be made available or not depending if the file format
        // chosen is vector based or bitmap based.
        
        fileFormat.addActionListener(this);
        
        JPanel panel = createInterfacePanel();   
        
        // Put the panel containing the characteristics of the export inside a
        // border.
        Border etched = BorderFactory.createEtchedBorder();
        Border titled = BorderFactory.createTitledBorder(etched, 
        	Globals.messages.getString("ExportOptions"));
        	
        panel.setBorder(titled);

        
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
        contentPane.add(b, constraints);            // Add cancel button    

        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                int selection;
                
                // Check if the magnification factor is correct.
                double mult = Double.parseDouble(multiplySizes.getText());
                if(multiplySizes.isEnabled()==true && (mult<0.01 || mult>100)) {
                	export=false;
                    JOptionPane.showMessageDialog(null,
						Globals.messages.getString("Warning_mul"),
                        Globals.messages.getString("Warning"),
    					JOptionPane.WARNING_MESSAGE);
					return;
                
                }
                
                if(fileName.getText().trim().equals("")){
                    export=false;
                    JOptionPane.showMessageDialog(null,
						Globals.messages.getString("Warning_noname"),
                        Globals.messages.getString("Warning"),
    					JOptionPane.WARNING_MESSAGE);
					return;
                }
                selection=JOptionPane.OK_OPTION;
                    
                if (selection==JOptionPane.OK_OPTION) {
                    export=true;
                    setVisible(false);
                }
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
    
    


}