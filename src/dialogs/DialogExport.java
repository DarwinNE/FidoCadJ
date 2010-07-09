package dialogs;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;

import globals.*;


/** DialogExport.java v.1.2

   Choose file format, size and options of the graphic exporting.

   ****************************************************************************
   Version History 

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     December 2007       D. Bucci    First working version
1.1     January 2008        D. Bucci    Internazionalized version
1.2     June 2009           D. Bucci    Capitalize the first letters                                     

                                     

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

    Copyright 2007-2009 by Davide Bucci
</pre>
    @author Davide Bucci
    @version 1.2 June 2009
    
    */
    
public class DialogExport extends JDialog implements ComponentListener 
{
    private static final int MIN_WIDTH=400;
    private static final int MIN_HEIGHT=350;
    
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
    
    /** Standard constructor: it needs the parent frame.
        @param p the dialog's parent
    */
    public DialogExport (JFrame p)
    {
        super(p,Globals.messages.getString("Circ_exp_t"), true);
        addComponentListener(this); 
        export=false;
        parent=p;
        
        
        
        
        GridBagLayout bgl=new GridBagLayout();
        GridBagConstraints constraints=new GridBagConstraints();
        Container contentPane=getContentPane();
        contentPane.setLayout(bgl);
        //constraints.insets.right=30;

        JLabel resolutionLabel=new 
            JLabel(Globals.messages.getString("Resolution"));
        constraints.weightx=0;
        constraints.weighty=0;
        constraints.gridx=1;
        constraints.gridy=0;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.anchor=GridBagConstraints.EAST;
        constraints.insets=new Insets(20,40,6,0);

        contentPane.add(resolutionLabel, constraints);
        
        resolution=new JComboBox();
        resolution.addItem("72x72 dpi");
        resolution.addItem("150x150 dpi");
        resolution.addItem("300x300 dpi");
        resolution.addItem("600x600 dpi");
        resolution.addItem("1200x1200 dpi");
        
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=2;
        constraints.gridy=0;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        constraints.insets=new Insets(20,0,0,40);
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.CENTER;
        contentPane.add(resolution, constraints);
        
        antiAlias_CB=new JCheckBox(Globals.messages.getString("Anti_aliasing"));
        //antiAlias_CB.setSelected(antiAlias);
        constraints.gridx=2;
        constraints.gridy=1;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.WEST;
        constraints.insets=new Insets(0,0,0,0);

        contentPane.add(antiAlias_CB, constraints);     // Add antialias cb
        
        blackWhite_CB=new JCheckBox(Globals.messages.getString("B_W"));
        //blackWhite_CB.setSelected(antiAlias);
        constraints.gridx=2;
        constraints.gridy=2;
        constraints.gridwidth=1;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.WEST;
        contentPane.add(blackWhite_CB, constraints);        // Add antialias cb
        
        JLabel fileFormatLabel=new 
            JLabel(Globals.messages.getString("File_format"));
        constraints.weightx=0;
        constraints.weighty=0;
        constraints.gridx=1;
        constraints.gridy=3;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.insets=new Insets(0,40,0,0);
        constraints.anchor=GridBagConstraints.EAST;
        constraints.fill=GridBagConstraints.NONE;

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
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=2;
        constraints.gridy=3;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.insets=new Insets(0,0,0,40);
        constraints.anchor=GridBagConstraints.WEST;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        contentPane.add(fileFormat, constraints);
        
        JLabel fileNameLabel=new 
            JLabel(Globals.messages.getString("File_name"));
        constraints.weightx=0;
        constraints.weighty=0;
        constraints.gridx=1;
        constraints.gridy=4;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.insets=new Insets(0,40,0,0);
        constraints.anchor=GridBagConstraints.EAST;
        constraints.fill=GridBagConstraints.NONE;
        contentPane.add(fileNameLabel, constraints);
        
        fileName=new JTextField();
        constraints.weightx=100;
        constraints.weighty=100;
        constraints.gridx=2;
        constraints.gridy=4;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.insets=new Insets(0,0,0,12);
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.anchor=GridBagConstraints.CENTER;
        contentPane.add(fileName, constraints);
        
        JButton browse=new JButton(Globals.messages.getString("Browse"));
        constraints.weightx=0;
        constraints.weighty=0;
        constraints.gridx=3;
        constraints.gridy=4;
        constraints.gridwidth=1;
        constraints.gridheight=1;   
        constraints.insets=new Insets(0,0,0,20);
        constraints.fill=GridBagConstraints.NONE;
        constraints.anchor=GridBagConstraints.CENTER;
        contentPane.add(browse, constraints);

        browse.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {

                
                if(Globals.useNativeFileDialogs) {
                    FileDialog fd = new FileDialog(parent, 
                    Globals.messages.getString("Select_file_export"),
                                               FileDialog.SAVE);
                    String filen;
                
                    fd.setDirectory(new File(fileName.getText()).getPath());
                    fd.setVisible(true);
                
                        
                    if(fd.getFile() != null) {
                        filen=Globals.createCompleteFileName(
                            fd.getDirectory(),
                            fd.getFile());          
                        fileName.setText(filen);
                    }
            
                } else {
                    JFileChooser fc = new JFileChooser(
                        new File(fileName.getText()).getPath());
                    int r = fc.showSaveDialog(null);
                    if (r == JFileChooser.APPROVE_OPTION) {
                        fileName.setText(fc.getSelectedFile().toString());
                    }   
                
                }
                
                        
                
            
                
                
                
            }
        });
        
        JButton ok=new JButton(Globals.messages.getString("Ok_btn"));
        JButton cancel=new JButton(Globals.messages.getString("Cancel_btn"));
    
        constraints.gridx=0;
        constraints.gridy=6;
        constraints.gridwidth=4;
        constraints.gridheight=1;
        constraints.anchor=GridBagConstraints.EAST;
        constraints.insets=new Insets(20,20,20,20);

        Box b=Box.createHorizontalBox();
        b.add(Box.createHorizontalGlue());
		b.add(cancel);
		b.add(Box.createHorizontalStrut(12));
		ok.setPreferredSize(cancel.getPreferredSize());
		b.add(ok);
        contentPane.add(b, constraints);            // Add cancel button    
        
        
        
        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                int selection;
                
                if(fileName.getText().trim().equals("")){
                    export=false;
                    setVisible(false);
                }
                    
                
                if((new File(fileName.getText())).exists())
                    selection = JOptionPane.showConfirmDialog(null, 
                        Globals.messages.getString("Warning_overwrite"),
                        Globals.messages.getString("Warning"),
                        JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.WARNING_MESSAGE);
                else
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