package circuit;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

import globals.*;
import geom.*;
import dialogs.*;
import primitives.*;
import timer.*;
import toolbars.*;

/** 

    Circuit panel: draw the circuit inside this panel. This is one of the most 
    important components, as it is responsible of all editing actions.

<pre>
   ****************************************************************************
   Version History 

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     March 2007          D. Bucci     First working version
1.1     December 2007       D. Bucci
1.2     January 2008        D. Bucci     Internationalized
2.0     May 2008            D. Bucci     Editing possibilities

  
    
    
  

   Written by Davide Bucci, Dec. 2007-Jan. 2008, davbucci at tiscali dot it
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

</pre>
   The circuit panel will contain the whole drawing.
    This class is able to perform its profiling, which is in particular
    the measurement of the time needed to draw the circuit.
    
    @author Davide Bucci
    @version 1.2, January 2008
*/
public class CircuitPanel extends JPanel implements MouseMotionListener,
                                             MouseListener,
                                             ChangeSelectedLayer,
                                             ChangeGridState,
                                             ChangeZoomListener,
                                             ChangeSelectionListener
{ //KeyEventDispatcher,
    // This parsing object is used for normal graphic objects.
    // I should made it private, a day or another
    public ParseSchem P;
    

    static final int SEL_TOLERANCE = 10; // Tolerance in pixel to select an 
                                        // object
    public boolean isFilled;
    public boolean antiAlias;  // Anti alias flag
    public boolean profileTime;
    private Color backgroundColor;
    private double average;
    private double runs;
    
    private int oldx;
    private int oldy;
    private boolean successiveMove;
    
    private boolean isGridVisible;
    
    private int PCB_pad_sizex;
    private int PCB_pad_sizey;
    private int PCB_pad_style;  
    private int PCB_pad_drill;
    private int PCB_thickness;
    private boolean extStrict; 	// Strict FidoCad compatibility

    
    private Rectangle evidenceRect;
    
      
    public static final int MARGIN=20;     // Margin size in pixel when calculating
                                    // component size.

    static final Color editingColor=Color.magenta;   
                                    // Color of elements during editing
    private int clickNumber;
    
    public static final int NPOLY=20;     // Maximum number of polygon vertices
    private int[] xpoly;
    private int[] ypoly;
    private StringBuffer selected;
    
    private ChangeZoomListener zoomListener;
    private ChangeSelectionListener selectionListener;
    private ChangeSelectionListener scrollGestureSelectionListener;
    private ChangeCoordinatesListener coordinatesListener;
  
    private int currentLayer;

    int actionSelected;                 // editing action being done
    String macroKey;                    // used when entering a macro

    public static final int NONE = 0;
    public static final int SELECTION = 1;
    public static final int ZOOM = 2;
    public static final int HAND = 3;
    public static final int LINE = 4;
    public static final int TEXT = 5;
    public static final int BEZIER = 6;
    public static final int POLYGON = 7;
    public static final int ELLIPSE = 8;
    public static final int RECTANGLE = 9;
    public static final int CONNECTION = 10;
    public static final int PCB_LINE = 11;
    public static final int PCB_PAD = 12;
    public static final int MACRO = 13;
    


    /** Standard constructor
        @param isEditable indicates wheter the panel should be responsible
               to keyboard and mouse inputs.
               
    */
    public CircuitPanel (boolean isEditable) {

        backgroundColor=Color.white; 
        P=new ParseSchem();
        isGridVisible=true;
        zoomListener=null;
        antiAlias = true;
        
        evidenceRect = new Rectangle(0,0,-1,-1);

       
        setOpaque(true);
        runs = 0;
        average = 0;
        currentLayer = 0;
        PCB_thickness = 5;
        PCB_pad_sizex=5;
        PCB_pad_sizey=5;
        PCB_pad_drill=2;
        selected=new StringBuffer();
        
        xpoly = new int[NPOLY];
        ypoly = new int[NPOLY];
        
        
        if (isEditable) {
            addMouseListener(this);
        
            addMouseMotionListener(this);
            setFocusable(true);
            //requestFocusInWindow(); 
            
            /******************************************************************
                Begin of key shortcut definition
            ******************************************************************/
        
            final String selection = "selection";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('a'), selection);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('A'), selection);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0,false), 
                selection);
            
            getActionMap().put(selection, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    setSelectionState(SELECTION,"");
                    repaint();
                }
            });
    
            final String line = "line";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('l'), line);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('L'), line);
            
            getActionMap().put(line, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    setSelectionState(LINE,"");
                    repaint();
                }
            });

            final String text= "text";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('t'), text);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('T'), text);
            
            getActionMap().put(text, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    setSelectionState(TEXT,"");
                    repaint();
                }
            });
        
            final String bezier = "bezier";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('b'), bezier);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('B'), bezier);
            
            getActionMap().put(bezier, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    setSelectionState(BEZIER,"");
                    repaint();
                }
            });

            final String polygon = "polygon";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('p'), polygon);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('P'), polygon);
            
            getActionMap().put(polygon, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    setSelectionState(POLYGON,"");
                    repaint();
                }
            });
            
            
            final String ellipse = "ellipse";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('e'), ellipse);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('E'), ellipse);
            
            getActionMap().put(ellipse, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    setSelectionState(ELLIPSE,"");
                    repaint();
                }
            });
            
            final String rectangle = "rectangle";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('g'), rectangle);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('G'), rectangle);
            
            getActionMap().put(rectangle, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    setSelectionState(RECTANGLE,"");
                    repaint();
                }
            });
            
            final String connection = "connection";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('c'), connection);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('C'), connection);
                
            getActionMap().put(connection, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    setSelectionState(CONNECTION,"");
                    repaint();
                }
            });

            final String pcbline = "pcbline";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('i'), pcbline);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('I'), pcbline);
            
            
            getActionMap().put(pcbline, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    setSelectionState(PCB_LINE,"");
                    repaint();
                }
            });
            final String pcbpad = "pcbpad";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('z'), pcbpad);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('Z'), pcbpad);
            
            getActionMap().put(pcbpad, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    setSelectionState(PCB_PAD,"");
                    repaint();
                }
            });

            final String rotation = "rotation";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('r'), rotation);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('R'), rotation);
            
            getActionMap().put(rotation, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    P.rotateAllSelected();
                    repaint();
        
                }
            });
            
            final String mirror = "mirror";
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('s'), mirror);
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('S'), mirror);   
             
            
            getActionMap().put(mirror, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    //System.out.println(mirror);
                    P.mirrorAllSelected();
                    repaint();
        
                }
            });
        
            final String delete = "delete"; 
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("DELETE"), delete);
        
            getActionMap().put(delete, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    P.deleteAllSelected();
                    repaint();
                }
            });
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("BACK_SPACE"), delete);
        
            getActionMap().put(delete, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    P.deleteAllSelected();
                    repaint();
                }
            });
            final String escape = "escape"; 
        
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), escape);
        
            getActionMap().put(escape, new AbstractAction() {
                public void actionPerformed(ActionEvent ignored) {
                    if(clickNumber>0){
                        successiveMove = false;
                        clickNumber = 0;
                        repaint();
                    }
                }
            });
            /******************************************************************
                End of key shortcut definition
            ******************************************************************/
        }
    }


    /** ChangeSelectionListener interface implementation */    
    public void setSelectionState(int s, String macro)
    {
        if (selectionListener!=null && s!=actionSelected)
            selectionListener.setSelectionState(s, macro);
            
        actionSelected=s;
        
        if (scrollGestureSelectionListener!=null)
        	scrollGestureSelectionListener.setSelectionState(s, macro);
        
        clickNumber=0;
        
        macroKey=macro;
    }
    
    /** Get the current editing action (see the constants defined in this class)
    
        @return the current editing action
    
    */
    public int getSelectionState()
    {
        return actionSelected;
    }   
    
    /***********************************************************************/
    
    /** Define the listener to be called when the zoom is changed
    
        @param c the new zoom listener
    
    */
    public void addChangeZoomListener(ChangeZoomListener c)
    {
        zoomListener=c;
    }
    /** Define the listener to be called when the selected action is changed
    
        @param c the new selection listener
    
    */
    public void addChangeSelectionListener(ChangeSelectionListener c)
    {
        selectionListener=c;
    }
    
    /** Define the listener to be called when the selected action is changed
		(this is explicitly done for the ScrollGestureSelection)
		
        @param c the new selection listener
    
    */
    public void addScrollGestureSelectionListener(ChangeSelectionListener c)
    {
        scrollGestureSelectionListener=c;
    }
    
    
    
    /** Define the listener to be called when the coordinates of the mouse 
        cursor are changed
    
        @param c the new coordinates listener
    
    */
    public void addChangeCoordinatesListener(ChangeCoordinatesListener c)
    {
        coordinatesListener=c;
    }

    /** Return the current editing layer
        @return the index of the layer
    */
    public int getCurrentLayer()
    {
        return currentLayer;
    }
    
    /** Set the current editing layer
        @param l the wanted layer
    */
    public void setCurrentLayer(int l)
    {
        /* two little checks... */
        if (l<0)
            l=0;
        if (l>=P.getLayers().size())
            l=P.getLayers().size()-1;
            
        currentLayer=l;
    }
    
    /** Sets the circuit.
        @param c the circuit string
    */
    
    public void setCirc(StringBuffer c)
        throws IOException
    {
        //circ=c;
        P.parseString(c);
    }
    
    /** Get the circuit in the Fidocad format, without the [FIDOCAD] header
		@param extensions allow for FCJ extensions
        @return the circuit in the Fidocad format
    */
    public StringBuffer getCirc(boolean extensions)
    {
        return P.getText(extensions);
    }
    
    /** Change the current layer state
        @param s the layer to be selected
    */
    public void changeSelectedLayer(int s)
    {
        currentLayer=s;
    }
    
    public void cleanAll()
    {
        
    }
    /** The callback method which is called when the current grid visibility has 
        changed. 
        @param v is the wanted grid visibility state
    */
    public void setGridVisibility(boolean v)
    {
        isGridVisible=v;
        repaint();
    }
    
    /** The callback method which is called when the current snap visibility has 
        changed. 
        @param v is the wanted snap state
    */
    public void setSnapState(boolean v)
    {
        P.getMapCoordinates().setSnap(v);
    }
    

    /** Called when the mouse is clicked inside the control
    
    */
    public void mouseClicked(MouseEvent evt)
    {
        int x = evt.getX();
        int y = evt.getY();
        requestFocusInWindow(); 
        
        String cmd;
        int i;
        MapCoordinates sc=P.getMapCoordinates();

        if(clickNumber>NPOLY-1)
            clickNumber=NPOLY-1;
            
        selected.setLength(0);
              
        switch(actionSelected) {
        case NONE:
            clickNumber = 0;
            break;
                    
        case SELECTION:
            clickNumber = 0;
            
            // Double click shows the Parameters dialog.
            if(evt.getClickCount() >= 2) {
				setPropertiesForPrimitive();
				break;
            }
            
            
            if(Globals.useMetaForMultipleSelection) {
                    // I do not know if a Windows user would approve the key
                    // used for multiple selections...
                if(!evt.isMetaDown()) 
                    P.deselectAll();
            } else {
                    // Indeed, they not! (F. Bertolazzi, from 0.20.4)
                if(!evt.isControlDown()) 
                    P.deselectAll();
            }
                
            
        
            if(Globals.useMetaForMultipleSelection) {
                P.selectPrimitive(sc.unmapXnosnap(x), sc.unmapYnosnap(y),
                              sc.unmapXnosnap(x+SEL_TOLERANCE)-
                              sc.unmapXnosnap(x),evt.isMetaDown());
            } else {
                P.selectPrimitive(sc.unmapXnosnap(x), sc.unmapYnosnap(y),
                              sc.unmapXnosnap(x+SEL_TOLERANCE)-
                              sc.unmapXnosnap(x),evt.isControlDown());
            }
                
           
            repaint();
            break;
        
        case ZOOM:      //////// TO IMPROVE: should center the viewport
            sc.unmapXsnap(x);
            sc.unmapYsnap(y);
            
            double z=sc.getXMagnitude();
            
            // Click+Meta reduces the zoom
            // Click raises the zoom
            if((evt.getModifiers() & InputEvent.BUTTON3_MASK)==0) 
                z=z*3.0/2.0;
            else
                z=z*2.0/3.0;
            
            if(z>10) z=10;
            if(z<.25) z=.25;
            
            z=Math.round(z*100.0)/100.0;
            
            sc.setMagnitudes(z,z);
            repaint();
            
            break;
        
        case CONNECTION:
        	if((evt.getModifiers() & InputEvent.BUTTON3_MASK)==1) {
        		selectAndSetProperties(x,y);
				break;
        	}
        	
            P.addPrimitive(new PrimitiveConnection(sc.unmapXsnap(x),
                                        sc.unmapYsnap(y), currentLayer));
                    
            repaint();
            break;
                    
               
        case PCB_PAD:
        	if((evt.getModifiers() & InputEvent.BUTTON3_MASK)==1) {
        		selectAndSetProperties(x,y);
				break;
        	}
            P.addPrimitive(new PrimitivePCBPad(sc.unmapXsnap(x),
                                  sc.unmapYsnap(y), 
                                  PCB_pad_sizex,
                                  PCB_pad_sizey,                                                                                                                
                                  PCB_pad_drill,
                                  PCB_pad_style,
                                  currentLayer));
                    
                    
            repaint();
            break;     
        case LINE:
        	if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
        		clickNumber == 0) {
				selectAndSetProperties(x,y);
				break;
        	}
            
            ++ clickNumber;
            if (evt.getClickCount() >= 2) {
                clickNumber = 0;
                break;
            }
            
            /* changed in 0.20.5
            // A right click exits the line introduction and activates the
            // selection state.
            if( (evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0) {
                clickNumber = 0;
                repaint();
                setSelectionState(SELECTION,"");
                break;
            }*/
            successiveMove=false;
            // clickNumber == 0 means that no line is being drawn
                    
            xpoly[clickNumber] = sc.unmapXsnap(x);
            ypoly[clickNumber] = sc.unmapYsnap(y);
            if (clickNumber == 2) {
                P.addPrimitive(new PrimitiveLine(xpoly[1],
                                                         ypoly[1],
                                                         xpoly[2],
                                                         ypoly[2],
                                                         currentLayer));
                        
                clickNumber = 1;
                xpoly[1] = xpoly[2];
                ypoly[1] = ypoly[2];
                repaint();
                              
            }
            break; 
        case TEXT:
        	if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0) {
        		selectAndSetProperties(x,y);
				break;
        	}
            P.addPrimitive(new PrimitiveAdvText(sc.unmapXsnap(x),
                                        sc.unmapYsnap(y), 
                                        3,4,0,0,
                                        "String", currentLayer));
                    
            repaint();
            break;
                    
        case BEZIER:
        	if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
        		clickNumber == 0) {
        		selectAndSetProperties(x,y);
				break;
        	}
            ++ clickNumber;
            if(clickNumber<=2) successiveMove=false;
                    
            // clickNumber == 0 means that no bezier is being drawn
                    
            xpoly[clickNumber] = sc.unmapXsnap(x);
            ypoly[clickNumber] = sc.unmapYsnap(y);
            // a polygon definition is ended with a double click
            if (clickNumber == 4) {
                P.addPrimitive(new PrimitiveBezier(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         xpoly[3],
                                         ypoly[3],
                                         xpoly[4],
                                         ypoly[4],
                                         currentLayer));
        
                clickNumber = 0;
                repaint();
              
            }
            break;   
            
        case POLYGON:
        	if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
        		clickNumber == 0) {
        		selectAndSetProperties(x,y);
				break;
        	}      
            
            
            
            // a polygon definition is ended with a double click
            if (evt.getClickCount() >= 2) {
         
                PrimitivePolygon poly=new PrimitivePolygon(isFilled,
                                         currentLayer);
                for(i=1; i<=clickNumber; ++i) 
                    poly.addPoint(xpoly[i],ypoly[i]);
        
                P.addPrimitive(poly);
                clickNumber = 0;
                repaint();
                break;
            }
            ++ clickNumber;
            if(clickNumber<=2) successiveMove=false;
            // clickNumber == 0 means that no polygon is being drawn
            xpoly[clickNumber] = sc.unmapXsnap(x);
            ypoly[clickNumber] = sc.unmapYsnap(y);
            break;   
            
        case ELLIPSE:
        
        	if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
        		clickNumber == 0) {
        		selectAndSetProperties(x,y);
				break;
        	}
            
            // If control is hold, trace a circle
            if(evt.isControlDown()) {
            	y=sc.mapY(xpoly[1],ypoly[1])+(x-sc.mapX(xpoly[1],ypoly[1]));
            }
            ++ clickNumber;
            successiveMove=false;
            // clickNumber == 0 means that no ellipse is being drawn
            
            xpoly[clickNumber] = sc.unmapXsnap(x);
            ypoly[clickNumber] = sc.unmapYsnap(y);
            if (clickNumber == 2) {
                P.addPrimitive(new PrimitiveOval(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         isFilled,
                                         currentLayer));
        
        
                clickNumber = 0;
                repaint();
              
            }
            break;   
            
        case RECTANGLE:
        	if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
        		clickNumber == 0) {
        		selectAndSetProperties(x,y);
				break;
        	}
            ++ clickNumber;
            successiveMove=false;
            // clickNumber == 0 means that no rectangle is being drawn
           
            xpoly[clickNumber] = sc.unmapXsnap(x);
            ypoly[clickNumber] = sc.unmapYsnap(y);
            if (clickNumber == 2) {
                P.addPrimitive(new PrimitiveRectangle(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         isFilled,
                                         currentLayer));
                clickNumber = 0;
                repaint();
              
            }
            if (clickNumber>=2) clickNumber = 0;
            break;   
            
               
        case PCB_LINE:
        	if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0 && 
        		clickNumber == 0) {
        		selectAndSetProperties(x,y);
				break;
        	}
            ++ clickNumber;
            if (evt.getClickCount() >= 2) {
                clickNumber = 0;
                break;
            }
            successiveMove=false;
            // clickNumber == 0 means that no pcb line is being drawn
            
            xpoly[clickNumber] = sc.unmapXsnap(x);
            ypoly[clickNumber] = sc.unmapYsnap(y);
            if (clickNumber == 2) {
                P.addPrimitive(new PrimitivePCBLine(xpoly[1],
                                         ypoly[1],
                                         xpoly[2],
                                         ypoly[2],
                                         PCB_thickness,
                                         currentLayer));
                clickNumber = 1;
                xpoly[1] = xpoly[2];
                ypoly[1] = ypoly[2];
                repaint();
              
            }
            
            break;  
        case MACRO:
        	if((evt.getModifiers() & InputEvent.BUTTON3_MASK)!=0) {
        		selectAndSetProperties(x,y);
				break;
        	}
            try {
                P.addPrimitive(new PrimitiveMacro(P.getLibrary(), 
                    P.getLayers(), sc.unmapXsnap(x),
                    sc.unmapYsnap(y),macroKey, P.getMacroFont()));
            } catch (IOException G) {
                System.out.println(G);
            }
                    
            repaint();
            break;
        }    
    }
    
    
    /** Handle the mouse movements when editing a graphic primitive.
    
    */
    public void mouseMoved(MouseEvent evt)
    {
        int xa=evt.getX();
        int ya=evt.getY();
        
        // This transformation/antitrasformation is useful to take care
        // of the snapping
        MapCoordinates cs=P.getMapCoordinates();
        int x=cs.mapX(P.getMapCoordinates().unmapXsnap(xa),0);
        int y=cs.mapY(0,P.getMapCoordinates().unmapYsnap(ya));
        if (coordinatesListener!=null)
            coordinatesListener.changeCoordinates(
                P.getMapCoordinates().unmapXsnap(xa),
                P.getMapCoordinates().unmapYsnap(ya));
        
        if(x==oldx && y==oldy)
            return;
        
        if (clickNumber == 0)
            return;

        Graphics g = getGraphics();
        
        //Globals.doNotUseXOR
        if(false && successiveMove)
        	paintComponent(g);
        	
        
        //Globals.doNotUseXOR
        if (!false) g.setXORMode(editingColor);
        
        //Globals.doNotUseXOR
        if(false) 
        	g.setColor(Color.green);
        
        
        Graphics2D g2d = (Graphics2D)g;


        
        /*  LINE **************************************************************
                
               ++
                 ** 
                   **
                     **
                       **
                         ++
                        
        */
        if (actionSelected == LINE ||
            (actionSelected == POLYGON && clickNumber>1)) {
    
            if(successiveMove) {
            	// Globals.doNotUseXOR
            	if(!false)
                	g.drawLine(cs.mapX(xpoly[1],ypoly[1]),
                    	cs.mapY(xpoly[1],ypoly[1]), oldx, oldy);
            }
              
            successiveMove=true;
            g.drawLine(cs.mapX(xpoly[1],ypoly[1]),
               cs.mapY(xpoly[1],ypoly[1]),
               x,y);
               
        /*    g.setClip(Math.min(x,cs.mapX(xpoly[1],ypoly[1])),
               Math.min(y,cs.mapY(xpoly[1],ypoly[1])),
               Math.abs(x-cs.mapX(xpoly[1],ypoly[1])),
               Math.abs(y-cs.mapY(xpoly[1],ypoly[1])));
    */
        }
        /*  PCBLINE ***********************************************************
            
               ++
                *** 
                  ***
                    ***
                      ***
                        ++
                        
        */
        if (actionSelected == PCB_LINE) {
            Stroke ss=g2d.getStroke();
            int wi_pix=Math.abs(cs.mapX(0,0)-
                        cs.mapX(PCB_thickness,PCB_thickness));

            BasicStroke UseStroke= new BasicStroke(wi_pix,
                java.awt.BasicStroke.CAP_ROUND,
                java.awt.BasicStroke.JOIN_ROUND);
        
            g2d.setStroke(UseStroke);
    
    		// Globals.doNotUseXOR
            if(!false && successiveMove) {
                g.drawLine(cs.mapX(xpoly[1],ypoly[1]),
                    cs.mapY(xpoly[1],ypoly[1]),
                    oldx,
                    oldy);
            }          
            successiveMove=true;
            g.drawLine(cs.mapX(xpoly[1],ypoly[1]),
               cs.mapY(xpoly[1],ypoly[1]),
               x,y);
    
                 
            g.setClip(Math.min(x,cs.mapX(xpoly[1],ypoly[1]))-wi_pix,
               Math.min(y,cs.mapY(xpoly[1],ypoly[1]))-wi_pix,
               Math.abs(x-cs.mapX(xpoly[1],ypoly[1]))+wi_pix,
               Math.abs(y-cs.mapY(xpoly[1],ypoly[1]))+wi_pix);
               
            g2d.setStroke(ss);
        }
        /*  BEZIER ************************************************************
            
               ++    ******
                 ****      **
                             **
                             **
                           **
                        ++
                        
        */
        if (actionSelected == BEZIER) {
    
    		// Globals.doNotUseXOR
        	if(!false             && successiveMove) {
            	g.drawLine(cs.mapX(xpoly[clickNumber],
                       ypoly[clickNumber]),
                   cs.mapY(xpoly[clickNumber],
                   ypoly[clickNumber]),
                   oldx,
                   oldy);
        	} else {
            	for(int i=2; i<=clickNumber; ++i) 
                 	g.drawLine(cs.mapX(xpoly[i-1], ypoly[i-1]),
                 		cs.mapY(xpoly[i-1], ypoly[i-1]),
                 		cs.mapX(xpoly[i], ypoly[i]),
                 		cs.mapY(xpoly[i], ypoly[i]));
            }
        	successiveMove=true;           
            g.drawLine(cs.mapX(xpoly[clickNumber],
                  ypoly[clickNumber]),
                  cs.mapY(xpoly[clickNumber],
                  ypoly[clickNumber]),
                  x,y);
    
        }
        /*  POLYGON ***********************************************************
            
               ++             ++
                 ************** 
                   ***********
                     ********
                       *****
                         ++
                        
        */
        if (actionSelected == POLYGON) {
    
        	// Globals.doNotUseXOR
        	if(!false             && successiveMove) {
                g.drawLine(cs.mapX(xpoly[clickNumber],
                   ypoly[clickNumber]),
                  cs.mapY(xpoly[clickNumber],
                  ypoly[clickNumber]),
                  oldx,
                  oldy);
            } else {
            	for(int i=2; i<=clickNumber; ++i) 
                 	g.drawLine(cs.mapX(xpoly[i-1], ypoly[i-1]),
                 		cs.mapY(xpoly[i-1], ypoly[i-1]),
                 		cs.mapX(xpoly[i], ypoly[i]),
                 		cs.mapY(xpoly[i], ypoly[i]));
            }
            successiveMove=true;          
            
            
            g.drawLine(cs.mapX(xpoly[clickNumber],
               ypoly[clickNumber]),
               cs.mapY(xpoly[clickNumber],
               ypoly[clickNumber]),
               x,y);
         /*   g.setClip(Math.min(x,cs.mapX(xpoly[clickNumber],
            	ypoly[clickNumber])),
               Math.min(y,cs.mapY(xpoly[clickNumber],ypoly[clickNumber])),
               Math.abs(x-cs.mapX(xpoly[clickNumber],ypoly[clickNumber])),
               Math.abs(y-cs.mapY(xpoly[clickNumber],ypoly[clickNumber])));
    */
        }
        /*  RECTANGLE *********************************************************
            
               +          +
                **********
                **********
                **********
                **********
               +          +
                        
       */
        if (actionSelected == RECTANGLE) {
            // Globals.doNotUseXOR
        	if(!false             && successiveMove) {
                rectangle(g,cs.mapX(xpoly[1],ypoly[1]),
                    cs.mapY(xpoly[1],ypoly[1]),
                    oldx,
                    oldy);
            }          
            successiveMove=true;
            rectangle(g,cs.mapX(xpoly[1],ypoly[1]),
               cs.mapY(xpoly[1],ypoly[1]),
               x, y);
            g.setClip(Math.min(x,cs.mapX(xpoly[1],ypoly[1])),
               Math.min(y,cs.mapY(xpoly[1],ypoly[1])),
               Math.abs(x-cs.mapX(xpoly[1],ypoly[1])),
               Math.abs(y-cs.mapY(xpoly[1],ypoly[1])));
        }
        /*  ELLIPSE ***********************************************************
            
               +
                    ***
                  *******
                 *********
                 *********
                  *******
                    *** 
                           +
                        
        */
        if (actionSelected == ELLIPSE) {
            // If control is hold, trace a circle
            if(evt.isControlDown()) {
            	y=cs.mapY(xpoly[1],ypoly[1])+(x-cs.mapX(xpoly[1],ypoly[1]));
            }
            
            // Globals.doNotUseXOR
        	if(!false             && successiveMove) {
                ellipse(g,cs.mapX(xpoly[1],ypoly[1]),
                   cs.mapY(xpoly[1],ypoly[1]),
                   oldx,
                   oldy);
            }
            
            successiveMove=true;
            ellipse(g,cs.mapX(xpoly[1],ypoly[1]),
                 cs.mapY(xpoly[1],ypoly[1]),
                  x, y);
        	g.setClip(Math.min(x,cs.mapX(xpoly[1],ypoly[1])),
               Math.min(y,cs.mapY(xpoly[1],ypoly[1])),
               Math.abs(x-cs.mapX(xpoly[1],ypoly[1])),
               Math.abs(y-cs.mapY(xpoly[1],ypoly[1])));
       	}    

       	oldx=x;
       	oldy=y;
       	g.dispose();
    }
    
    /** Mouse interface: dragging operations.
    
    */
    public void mousePressed(MouseEvent evt)
    {
        int px=evt.getX();
        int py=evt.getY();
        

        boolean multiple=evt.isControlDown();
        
        if(Globals.useMetaForMultipleSelection)
            multiple=evt.isMetaDown();
            
        if(actionSelected == SELECTION) 
            P.dragHandleStart(px, py, SEL_TOLERANCE,multiple);
        
       
        
        //Globals.actualG.dispose();

    }
    public void mouseDragged(MouseEvent evt)
    {
        int px=evt.getX();
        int py=evt.getY();
        Graphics g = getGraphics();
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, 
                RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, 
                RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, 
                RenderingHints.VALUE_DITHER_DISABLE);
        if(!Globals.doNotUseXOR) g2d.setXORMode(editingColor);
        
        P.dragHandleDrag(this, g2d, px, py);
        
    }
    public void mouseReleased(MouseEvent evt)
    {
        int px=evt.getX();
        int py=evt.getY();
        //Globals.actualG=(Graphics2D)getGraphics();
        //Globals.actualMap=P.getMapCoordinates();
        
        boolean multiple=evt.isControlDown();
        
        if(Globals.useMetaForMultipleSelection)
            multiple=evt.isMetaDown();
        
        if(actionSelected==SELECTION) {
            
            P.dragHandleEnd(this,px, py, multiple);
            repaint();
        }
        //Globals.actualG.dispose();
    }

    public void mouseEntered(MouseEvent evt)
    {
        // Define the current cursor, depending on the action being done.
        switch(actionSelected) {
            case NONE:
            case SELECTION:
            default:
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
                
            case ZOOM:
            case HAND:
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                break;
            case LINE:
            case TEXT:
            case BEZIER:
            case POLYGON:
            case ELLIPSE:
            case RECTANGLE:
            case CONNECTION:
            case PCB_LINE:
            case PCB_PAD:       
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        }
    }
    
    public void mouseExited(MouseEvent evt)
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /** The zoom listener
    
        @param z the zoom factor to be used
    */
    
    public void changeZoom(double z) 
    {
        z=Math.round(z*100.0)/100.0;
        P.getMapCoordinates().setMagnitudes(z,z);
        repaint();
    }
    
    
    /** Constructor which allows to specify the background color
        @param sfondo the background color to be used.
    */
    public CircuitPanel (Color sfondo) {

        backgroundColor=sfondo; 
        P=new ParseSchem();
        //circ=new StringBuffer();
        setOpaque(true);

    }
    
    /** Sets the background color.
        @param sfondo the background color to be used.
    */
    public void setBackground(Color sfondo)
    {
        backgroundColor=sfondo;
    }
    
    public void setEvidenceRect(int lx, int ly, int w, int h)
    {
    	evidenceRect.x=lx;
    	evidenceRect.y=ly;
    	evidenceRect.height=h;
    	evidenceRect.width=w;
    	
    }
    

    /** Repaint the panel */
    public void paintComponent(Graphics g) 
    {

        MyTimer mt;
        mt = new MyTimer();
        
        
        Graphics2D g2 = (Graphics2D)g; 
    
        if (antiAlias) g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
             RenderingHints.VALUE_ANTIALIAS_ON);
        else {
          // Faster graphic 
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, 
                RenderingHints.VALUE_COLOR_RENDER_SPEED);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, 
                RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, 
                RenderingHints.VALUE_DITHER_DISABLE);
         }
     
        // Draw all the primitives
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        if(isGridVisible){  
            P.drawGrid(g2,0,0,getWidth(),
                              getHeight());
        }
        g.setColor(Color.black);

        P.draw(g2);
        if (zoomListener!=null) 
            zoomListener.changeZoom(P.getMapCoordinates().getXMagnitude());
        
        // Draw the handles of all selected primitives
        P.drawSelectedHandles(g2);
    
    	if(Globals.doNotUseXOR) 
        	g.setColor(Color.green);

   		g2.setStroke(new BasicStroke(1));

        g.drawRect(evidenceRect.x,evidenceRect.y, evidenceRect.width,	
        	evidenceRect.height);
  
        if(profileTime) {
            double elapsed=mt.getElapsed();
            g2.drawString("Version: "+
                Globals.version, 0,100);
            g.drawString("Time elapsed: " +
                elapsed+" ms" ,0,50);
            ++runs;
            average += elapsed;
            System.out.println("Time elapsed: "+
                elapsed+
                " averaging "+
                average/runs+
                "ms in "+runs+
                " redraws");
        }
        
        if (P.getMapCoordinates().getXMax()>0 && 
            P.getMapCoordinates().getYMax()>0){
            setPreferredSize(new Dimension(P.getMapCoordinates().getXMax()
                +MARGIN,
                P.getMapCoordinates().getYMax()+MARGIN));
            revalidate();
        }

    
    }
 
 	
    public void setPCB_pad_sizex(int s)
    {
        PCB_pad_sizex=s;
    }
    
    public int getPCB_pad_sizex()
    {
        return PCB_pad_sizex;
    }
    
    public void setPCB_pad_sizey(int s)
    {
    	PCB_pad_sizey=s;
    }
    
    public int getPCB_pad_sizey()
    {
    	return PCB_pad_sizey;
    }
    
   	public void setPCB_pad_style(int s)
    {
    	PCB_pad_style=s;  
   	}
    public int getPCB_pad_style()
    {
    	return PCB_pad_style;  
   	}
   	
	public void setPCB_pad_drill(int s)
   	{
    	PCB_pad_drill=s;
    }
   	
   	public int getPCB_pad_drill()
   	{
    	return PCB_pad_drill;
    }
    
    public void setPCB_thickness(int s)
    {
    	PCB_thickness=s;
    }
    
    public int getPCB_thickness()
    {
    	return PCB_thickness;
    }
    
    private void rectangle(Graphics g, int x1, int y1, int x2, int y2)
    {
        g.drawLine(x1, y1, x1, y2);
        g.drawLine(x1, y2, x2, y2);
        g.drawLine(x2, y2, x2, y1);
        g.drawLine(x2, y1, x1, y1);
        
    }
    
    private void ellipse(Graphics g, int x1, int y1, int x2, int y2)
    {
        int xa=Math.min(x1,x2);
        int ya=Math.min(y1,y2);
        int xb=Math.max(x1,x2);
        int yb=Math.max(y1,y2);
        
        g.drawOval(xa,ya,(xb-xa),(yb-ya));
        
    }
    
    private void setPropertiesForPrimitive()
    {
        GraphicPrimitive gp=P.getFirstSelectedPrimitive();
        if (gp!=null) {
            Vector v=gp.getControls();
            DialogParameters dp = new DialogParameters(null,v, extStrict, 
            	P.getLayers());
            dp.setVisible(true);
            if(dp.active) {
                gp.setControls(dp.getCharacteristics());
                P.saveUndoState();
                repaint();
            }
        }
    }
    
    private void selectAndSetProperties(int x, int y)
    {
        MapCoordinates sc=P.getMapCoordinates();

		P.deselectAll();
    	P.selectPrimitive(sc.unmapXnosnap(x), sc.unmapYnosnap(y),
                              sc.unmapXnosnap(x+SEL_TOLERANCE)-
                              sc.unmapXnosnap(x),false);
        repaint();
        setPropertiesForPrimitive();
    }
    
    public void setStrict (boolean v)
    {
    	extStrict = v;
    }
    
    public boolean getStrict ()
    {
		return extStrict;
	}

    

 
}