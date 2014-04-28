package circuit.controllers;

import java.io.*;
import java.util.*;
import java.net.*;

import primitives.*;
import export.*;
import globals.*;
import layers.*;
import circuit.*;
import circuit.model.*;
import graphic.*;

/** ParserActions: perform parsing of FidoCadJ code.
	In general, those routines are constructed such as they are relatively 
	fault-tolerant. If an error is detected in the file, the parsing is 
	continued anyway and just an error message is sent to the console. Most
	of the times, the user will not see the error and this is probably OK
	since he/she will not be interested in it.
    
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

    Copyright 2007-2014 by Davide Bucci
</pre>
*/

public class ParserActions 
{
	private final DrawingModel P;

    // This is the maximum number of tokens which will be considered in a line
    static final int MAX_TOKENS=512;
  
    // True if FidoCadJ should use Windows style line feeds (appending \r
    // to the text generated).
    static final boolean useWindowsLineFeed=false;
    
    // Name of the last file opened
    public String openFileName;

	/** Standard constructor: provide the database class.
	*/
	public ParserActions (DrawingModel pp)
	{
		P=pp;
	}
	
    /** Parse the circuit contained in the StringBuffer specified.
        This function resets the primitive database and then parses the circuit.
        
        @param s the string containing the circuit
    */
    public void parseString(StringBuffer s) 
    {
        P.getPrimitiveVector().clear();
        addString(s, false);
        P.setChanged(true);
    }
    
    /** Renders a split version of the macros contained in the given string.
    	@param s a string containing macros to be splitted.
    	@param splitStandardMacros if it is true, even the standard macros 
    		will be split.
    	@return the split macros.
    */
    public StringBuffer splitMacros(StringBuffer s, 
    	boolean splitStandardMacros)
    {
        StringBuffer txt= new StringBuffer("");    
		
        DrawingModel Q=new DrawingModel();
        Q.setLibrary(P.getLibrary());  // Inherit the library
        Q.setLayers(P.getLayers());    // Inherit the layers
            
        // from the obtained string, obtain the new Q object which will
        // be exported and then loaded into the clipboard.
            
        try {
            ParserActions pas=new ParserActions(Q);
            pas.parseString(s); 
            
            File temp= File.createTempFile("copy", ".fcd");
            temp.deleteOnExit();
            String frm="";
            if(splitStandardMacros) 
            	frm = "fcda";
            else
            	frm = "fcd";
                  
            ExportGraphic.export(temp,  Q, frm, 1,true,false, 
                true,false);
                
            FileInputStream input = new FileInputStream(temp);
            BufferedReader bufRead = new BufferedReader(
                new InputStreamReader(input, Globals.encoding));
                
            String line="";
                        
            txt = new StringBuffer(bufRead.readLine());
                        
            txt.append("\n");
                        
            while (line != null){
                line =bufRead.readLine();
                if (line==null)
                 	break;
                txt.append(line);
                txt.append("\n");
            }
            
            bufRead.close();
                
        } catch(IOException e) {
            System.out.println("Error: "+e); 
        }
        
        return txt;
    }
    
    /** Get the FidoCadJ text file.
    
        @param extensions specify if FCJ extensions should be used
        @return the sketch in the text FidoCadJ format
    */
    public StringBuffer getText(boolean extensions)
    {
        int i;
        StringBuffer s=registerConfiguration(extensions);
        
        for (GraphicPrimitive g:P.getPrimitiveVector()){
            s.append(g.toString(extensions));
            if(useWindowsLineFeed) 
                s.append("\r");
        }        
        return s;
    }
    /** If it is needed, provides all the configurations settings at
        the beginning of the FidoCadJ file.
        @param extensions it is true when FidoCadJ should export using 
            its extensions.
    
    */
    public StringBuffer registerConfiguration(boolean extensions)
    {
    	Vector<LayerDesc> layerV=P.getLayers();
    	
    	// This is something which is not contemplated by the original
    	// FidoCAD for Windows. If extensions are not activated, just exit.
        if(!extensions) {
        	return new StringBuffer();
		}
		
        StringBuffer s = new StringBuffer();
        // Here is the beginning of the output. We can eventually provide
        // some hints about the configuration of the software (if needed).
        
        // We start by checking if the diameter of the electrical connection
        // should be written.
        
        // We consider that a difference of 1e-5 is small enough 

        if(Math.abs(Globals.diameterConnectionDefault-
            Globals.diameterConnection)>1e-5) {           
            s.append("FJC C "+Globals.diameterConnection+"\n");
        }
        
        
        // Check if the layers should be indicated    
        Vector<LayerDesc> standardLayers = 
        	StandardLayers.createStandardLayers();
        
        
        for(int i=0; i<layerV.size();++i) {
            LayerDesc l = (LayerDesc)layerV.get(i);
            String defaultName=
        	   	((LayerDesc)standardLayers.get(i)).getDescription();
            if (l.getModified()) {
                int rgb=l.getColor().getRGB();
                float alpha=l.getAlpha();
                s.append("FJC L "+i+" "+rgb+" "+alpha+"\n");
                // We compare the layers to the standard configuration.
              	// If the name has been modified, the layer configuration 
               	// is saved.
                if (!l.getDescription().equals(defaultName)) {
                  	s.append("FJC N "+i+" "+l.getDescription()+"\n");
                }
            }
        }
        
        // Check if the line widths should be indicated
        if(Math.abs(Globals.lineWidth -
           	Globals.lineWidthDefault)>1e-5) {         
           	s.append("FJC A "+Globals.lineWidth+"\n");
        }
        if(Math.abs(Globals.lineWidthCircles -
           	Globals.lineWidthCirclesDefault)>1e-5) {          
           	s.append("FJC B "+Globals.lineWidthCircles+"\n");
       	}
       
        return s;
    }    
    
    /** Parse the circuit contained in the StringBuffer specified.
        this funcion add the circuit to the current primitive database.
        
        @param s the string containing the circuit
        @param selectNew specify that the added primitives should be selected.
    */
    public void addString(StringBuffer s, boolean selectNew) 
        //throws IOException
    {
        int i; // Character pointer within the string 
        int j; // token counter within the string
        boolean hasFCJ=false; // the last primitive has FCJ extensions
        StringBuffer token=new StringBuffer(); 
        
        String macroFont = P.getTextFont();
        int macroFontSize = P.getTextFontSize();

        GraphicPrimitive g = new PrimitiveLine(macroFont, macroFontSize);

        String[] tokens=new String[MAX_TOKENS];
        String[] old_tokens=new String[MAX_TOKENS];
        String[] name=null;
        String[] value=null;
        double newConnectionSize = -1.0;
        double newLineWidth = -1.0;
        double newLineWidthCircles = -1.0;
        
        int vn=0, vv=0;
        int old_j=0;
        int macro_counter=0;
        int l;
        
        token.ensureCapacity(256);
        
        /*  This code is not very easy to read. If more extension of the
            original FidoCAD format (performed with the FCJ tag) are to be 
            implemented, it can be interesting to rewrite the parser as a
            state machine.
        */
        synchronized(this) {
    	Vector<LayerDesc> layerV=P.getLayers();

        int k;      
        char c='\n';
        int len;
		
		// Actual line number. This is useful to indicate errors.
    	int lineNum=1;
        j=0;    // A fairy simple tokenizer
        token.setLength(0);
        len=s.length();
        
        for(i=0; i<len;++i){
            c=s.charAt(i);
            if(c=='\n' || c=='\r'|| i==len-1) { //The string finished
                if(i==len-1 && c!='\n' && c!=' '){
                    token.append(c);
                }
                ++lineNum;
                tokens[j]=token.toString();
                if (token.length()==0)  // Avoids trailing spaces
                    j--;
                
                try{
                    if(hasFCJ && !tokens[0].equals("FCJ")) {
                        hasFCJ = registerPrimitivesWithFCJ(hasFCJ, tokens, g, 
                            old_tokens, old_j, selectNew);
                    }
                    
                    if(tokens[0].equals("FCJ")) {   // FidoCadJ extension!
                        if(hasFCJ && old_tokens[0].equals("MC")) {
                            macro_counter=2;
                            g=new PrimitiveMacro(P.getLibrary(),layerV,
                            	macroFont, macroFontSize);
                            g.parseTokens(old_tokens, old_j+1);
                        } else if (hasFCJ && old_tokens[0].equals("LI")) {
                            g=new PrimitiveLine(macroFont, macroFontSize);
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);

                            if(old_j>5 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                P.addPrimitive(g,false,null);
                            }
                     
                        } else if (hasFCJ && old_tokens[0].equals("BE")) {
                            g=new PrimitiveBezier(macroFont, macroFontSize);
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);
                            if(old_j>5 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                P.addPrimitive(g,false,null);
                            }
                            
                        } else if (hasFCJ && (old_tokens[0].equals("RV")||
                            old_tokens[0].equals("RP"))) {
                            g=new PrimitiveRectangle(macroFont, macroFontSize);
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);
                            if(old_j>2 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                P.addPrimitive(g,false,null);
                            }                        
                        } else if (hasFCJ && (old_tokens[0].equals("EV")||
                            old_tokens[0].equals("EP"))) {
                            g=new PrimitiveOval(macroFont, macroFontSize);
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);
                            if(old_j>2 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                P.addPrimitive(g,false,null);
                            }                        
                        } else if (hasFCJ && (old_tokens[0].equals("PV")||
                            old_tokens[0].equals("PP"))) {
                            g=new PrimitivePolygon(macroFont, macroFontSize);
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);
                            if(old_j>2 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                P.addPrimitive(g,false,null);
                            }     
    					} else if (hasFCJ && (old_tokens[0].equals("CV")||
                            old_tokens[0].equals("CP"))) {
                            g=new PrimitiveComplexCurve(macroFont,
                            	macroFontSize);
                            
                            for(l=0; l<j+1; ++l)
                                old_tokens[l+old_j+1]=tokens[l];
                            
                            old_j+=j+1;
                            g.parseTokens(old_tokens, old_j+1);
                            g.setSelected(selectNew);
                            if(old_j>2 && old_tokens[old_j].equals("1")) {
                            	macro_counter = 2;
                            } else {
                                P.addPrimitive(g,false,null);
                            }     
    					} else if (hasFCJ && old_tokens[0].equals("PL")) {
                    		macro_counter = 2;
                    	} else if (hasFCJ && old_tokens[0].equals("PA")) {
                    		macro_counter = 2;
						} else if (hasFCJ && old_tokens[0].equals("SA")) {                    
                    		macro_counter = 2;                       
                        }
                        hasFCJ=false;
                    
                    } else if(tokens[0].equals("FJC")) {
                        // FidoCadJ Configuration
                    
                        if(tokens[1].equals("C")) {
                            // Connection size
                            newConnectionSize = 
                                Double.parseDouble(tokens[2]);
                        
                        } else if(tokens[1].equals("L")) {
                            // Layer configuration
                            int layerNum = Integer.parseInt(tokens[2]);
                            if (layerNum>=0&&layerNum<layerV.size()){
                                int rgb=Integer.parseInt(tokens[3]);
                                float alpha=Float.parseFloat(tokens[4]);
                                LayerDesc ll=(LayerDesc)(layerV.get(layerNum));
                                ll.getColor().setRGB(rgb);
                                ll.setAlpha(alpha);
                                ll.setModified(true);  
                            }
                                
                        } else if(tokens[1].equals("N")) {
                        	// Layer name
                        	
                        	int layerNum = Integer.parseInt(tokens[2]);
                            if (layerNum>=0&&layerNum<layerV.size()){
                                String lName="";
                                
                                StringBuffer temp=new StringBuffer(25);
                                for(int t=3; t<j+1; ++t) {
                                	temp.append(tokens[t]);
                                	temp.append(" ");
                                }
                                
                                lName=temp.toString();
                                LayerDesc ll=(LayerDesc)(layerV.get(layerNum));
                                ll.setDescription(lName);
                                ll.setModified(true);  
                            }
                        
                        } else if(tokens[1].equals("A")) {
                            // Connection size
                            newLineWidth = 
                                Double.parseDouble(tokens[2]);
                        
                        } else if(tokens[1].equals("B")) {
                            // Connection size
                            newLineWidthCircles = 
                                Double.parseDouble(tokens[2]);                      
                        }
                        
                    } else if(tokens[0].equals("LI")) {
                        // Save the tokenized line.
                        // We cannot create the macro until we parse the 
                        // following line (which can be FCJ)
                        macro_counter=0;
    
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
        
                    } else if(tokens[0].equals("BE")) {
                        macro_counter=0;
    
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    } else if(tokens[0].equals("MC")) {
                        // Save the tokenized line.
                        macro_counter=0;
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    } else if(tokens[0].equals("TE")) {
                        hasFCJ=false;
                        macro_counter=0;
                        g=new PrimitiveAdvText();
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        P.addPrimitive(g,false,null);
                    } else if(tokens[0].equals("TY")) {
                        hasFCJ=false;
                        
                        if(macro_counter==2) {
                            macro_counter--;
                            name=new String[j+1];
        					for(l=0; l<j+1;++l)
                                name[l]=tokens[l];
                            vn=j;                                
                        } else if(macro_counter==1) {
                        	value=new String[j+1];
                            for(l=0; l<j+1;++l)
                                value[l]=tokens[l];
                            vv=j;       
                            if (name!=null) g.setName(name,vn+1);
                            g.setValue(value,vv+1);

                            g.setSelected(selectNew);
                            P.addPrimitive(g, false,null);
                            macro_counter=0;
                        } else {
                            g=new PrimitiveAdvText();
                            g.parseTokens(tokens, j+1);
                            g.setSelected(selectNew);
                            P.addPrimitive(g,false,null);
                         }
                    } else if(tokens[0].equals("PL")) {
                        hasFCJ=true;
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                            
                        macro_counter=0;
                        old_j=j;
                        g=new PrimitivePCBLine(macroFont, macroFontSize);
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        //addPrimitive(g,false,false);
                    } else if(tokens[0].equals("PA")) {
                        hasFCJ=true;
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        macro_counter=0;
                        g=new PrimitivePCBPad(macroFont, macroFontSize);
                        old_j=j;
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        //addPrimitive(g,false,false);
                    } else if(tokens[0].equals("SA")) {
                        hasFCJ=true;
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        macro_counter=0;
                        g=new PrimitiveConnection(macroFont, macroFontSize);
                        g.parseTokens(tokens, j+1);
                        g.setSelected(selectNew);
                        //addPrimitive(g,false,false);
                    }  else if(tokens[0].equals("EV")||tokens[0].equals("EP")) {
                        macro_counter=0;
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                	} else if(tokens[0].equals("RV")||tokens[0].equals("RP")) {
                        macro_counter=0;
    
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    } else if(tokens[0].equals("PV")||tokens[0].equals("PP")) {
                        macro_counter=0;
    
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    } else if(tokens[0].equals("CV")||tokens[0].equals("CP")) {
                        macro_counter=0;
    
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                        hasFCJ=true;
                    }
                } catch(IOException E) {
                    System.out.println("Error encountered: "+E.toString());
                    System.out.println("string parsing line: "+lineNum);
                    hasFCJ = false;
                    macro_counter = 0;
                } catch(NumberFormatException F) {
                    System.out.println("I could not read a number at line: "
                                       +lineNum);
                    hasFCJ = false;
                    macro_counter = 0;                  
                }
                j=0;
                token.setLength(0);
            } else if (c==' '){ // Ready for next token
                tokens[j]=token.toString();
                ++j;
                if (j>=MAX_TOKENS) {
                    //IOException e=new IOException("Too much tokens!");
                    //throw e;
                    System.out.println("Too much tokens!");
                    System.out.println("string parsing line: "+lineNum);
                }
                token.setLength(0);
            } else {
                token.append(c);
            }
        }
       
        try{
            registerPrimitivesWithFCJ(hasFCJ, tokens, g, old_tokens, old_j,
                selectNew);
        } catch(IOException E) {
            System.out.println("Error encountered: "+E.toString());
            System.out.println("string parsing line: "+lineNum);
        } catch(NumberFormatException F) {
            System.out.println("I could not read a number at line: "
                                     +lineNum);
        }
        
        // If the schematics has some configuration information, we need
        // to set them up.
        if (newConnectionSize>0) {
            Globals.diameterConnection=newConnectionSize;
        }
        if (newLineWidth>0) {
            Globals.lineWidth = newLineWidth;
        }
        
        if (newLineWidthCircles>0) {
            Globals.lineWidthCircles = newLineWidthCircles;
        }
        P.sortPrimitiveLayers();
        }
        
    }
    
    private boolean registerPrimitivesWithFCJ(boolean hasFCJ_t, String[] tokens,
        GraphicPrimitive gg, String[] old_tokens, int old_j, boolean selectNew)
        throws IOException
    {
        String macroFont = P.getTextFont();
        int macroFontSize = P.getTextFontSize();
        Vector<GraphicPrimitive> primitiveVector=P.getPrimitiveVector();
    	Vector<LayerDesc> layerV=P.getLayers();

    	GraphicPrimitive g=gg;
    	boolean hasFCJ=hasFCJ_t;
    	boolean addPrimitive = false;
        if(hasFCJ && !tokens[0].equals("FCJ")) {
            if (old_tokens[0].equals("MC")) {
                g=new PrimitiveMacro(P.getLibrary(),
                	layerV, macroFont, macroFontSize);
                addPrimitive = true;
            } else if (old_tokens[0].equals("LI")) {
                g=new PrimitiveLine(macroFont, macroFontSize);
                addPrimitive = true;
            } else if (old_tokens[0].equals("BE")) {
                g=new PrimitiveBezier(macroFont, macroFontSize);
                addPrimitive = true;
            } else if (old_tokens[0].equals("RP")||
                old_tokens[0].equals("RV")) {
                g=new PrimitiveRectangle(macroFont, macroFontSize);
                addPrimitive = true;
            } else if (old_tokens[0].equals("EP")||
                old_tokens[0].equals("EV")) {
                g=new PrimitiveOval(macroFont, macroFontSize);
                addPrimitive = true;
            } else if (old_tokens[0].equals("PP")||
                old_tokens[0].equals("PV")) {
                g=new PrimitivePolygon(macroFont, macroFontSize);
                addPrimitive = true;
            } else if(old_tokens[0].equals("PL")) {
                g=new PrimitivePCBLine(macroFont, macroFontSize);
                addPrimitive = true;
             } else if (old_tokens[0].equals("CP")||
                old_tokens[0].equals("CV")) {
                g=new PrimitiveComplexCurve(macroFont, macroFontSize);
                addPrimitive = true;
            }  else if(old_tokens[0].equals("PA")) {
                g=new PrimitivePCBPad(macroFont, macroFontSize);
                addPrimitive = true;
            } else if(old_tokens[0].equals("SA")) {
                g=new PrimitiveConnection(macroFont, macroFontSize);
                addPrimitive = true;
            }
        }     
        
        if(addPrimitive) {
            g.parseTokens(old_tokens, old_j+1);
            g.setSelected(selectNew);
            P.addPrimitive(g,false,null);
            hasFCJ = false;        
        }
        return hasFCJ;
    }
	

    /** Read all librairies contained in the given URL at the given prefix.
        This is particularly useful to read librairies shipped in a jar 
        file.
    */
    public void loadLibraryInJar(URL s, String prefix_s)
    {
    	String prefix=prefix_s;
    	if(s==null) {
    		if (prefix==null)
    			prefix="";
    		System.out.println("Resource not found! "+prefix);
    		return; 
    	}
        try {
            readLibraryBufferedReader(new BufferedReader(new 
                InputStreamReader(s.openStream(), Globals.encoding)), prefix);
        } catch (IOException E) {
            System.out.println("Problems reading library: "+s.toString());
        }
           
    }
    
    /** Read the library contained in a file
        @param openFileName the name of the file to be loaded
    */
    public void readLibraryFile(String openFileName)
        throws IOException
    {
        InputStreamReader input = new InputStreamReader(new 
            FileInputStream(openFileName), Globals.encoding);
        
        BufferedReader bufRead = new BufferedReader(input);
        String prefix="";

        prefix = Globals.getFileNameOnly(openFileName);
        if ("FCDstdlib".equals(prefix))
            prefix="";
            
        readLibraryBufferedReader(bufRead, prefix);

        bufRead.close();
    }

    /** Read a library provided by a buffered reader. Adds all the macro keys
        in memory, with the given prefix.
        @param bufRead The buffered reader prepared with the stream containing
            the library we want to read.
        @param prefix The prefix which should be added to the macro key when 
            using a non standard macro.
    */
    public void readLibraryBufferedReader(BufferedReader bufRead, 
    	String prefix)
        throws IOException
    {
        String macroName="";
        String longName="";
        String categoryName="";
        String libraryName="";
        int i;
//        StringBuffer txt= new StringBuffer();    
        String line="";
        
        MacroDesc macroDesc;
        
        while(true) {
            // Read and process line by line.
            line = bufRead.readLine();
            
            if(line==null)
                break; 
            
            // Avoid trailing spaces
            line=line.trim();       
            
            // Avoid processing shorter lines
            if (line.length()<=1)    
                continue;
                
            // A category
            if(line.charAt(0)=='{') { 
                categoryName="";
                StringBuffer temp=new StringBuffer(25);
                for(i=1; i<line.length()&&line.charAt(i)!='}'; ++i){
                    temp.append(line.charAt(i));
                }
                categoryName=temp.toString().trim();
                if(i==line.length()) {
                	IOException e=new IOException(
                		"Category non terminated with }.");
                    throw e;
                }
                continue;
            }
                
            // A macro
            if(line.charAt(0)=='[') { 
                macroName="";
                    
                longName="";
                StringBuffer temp=new StringBuffer(25);
                for(i=1; line.charAt(i)!=' ' &&
                         line.charAt(i)!=']' &&
                         i<line.length(); ++i){
                    temp.append(line.charAt(i));
                }
                macroName=temp.toString().trim();
                int j;
                temp=new StringBuffer(25);
                for(j=i; j<line.length()&&line.charAt(j)!=']'; ++j){
                    temp.append(line.charAt(j));
                }
                longName=temp.toString();
                if(j==line.length()) {
                	IOException e=new IOException(
                		"Macro name non terminated with ].");
                    throw e;
                }
                
                if ("FIDOLIB".equals(macroName)) {
                    libraryName = longName.trim();
                    continue;
                } else {
                    if(!"".equals(prefix))
                        macroName=prefix+"."+macroName;
                    
                    macroName=macroName.toLowerCase(new Locale("en"));
                    P.getLibrary().put(macroName, new 
                        MacroDesc(macroName,"","","","", prefix));
					/*System.out.printf("-- macroName:%s | longName:%s | 
						categoryName:%s | libraryName:%s | prefix:%s\n",
						macroName,longName,categoryName,libraryName,prefix);*/                    
                    continue;
                }
            }
            
            // TODO rewrite this block 
            if(!"".equals(macroName)){
                // Add the macro name.
                // NOTE: in FidoCAD, the macro prefix is somewhat case 
                // insensitive, since it indicates a file name and in 
                // Windows all file names are case insensitive. Under
                // other operating systems, we need to be waaay much
                // careful, hence we convert the macro name to lower case.
                macroName = macroName.toLowerCase(new Locale("en"));
                macroDesc = P.getLibrary().get(macroName);
                if(macroDesc==null)
                	return;
                macroDesc.name = longName;
                macroDesc.key = macroName;
                macroDesc.category = categoryName;
                macroDesc.library = libraryName;
                macroDesc.filename = prefix;
                
               
                macroDesc.description = macroDesc.description + "\n" + 
                		line;
                
                // Is it OK to use prefix as the macro filename? Yes!
            }                
        } 
    }
    /** Try to load all libraries ("*.fcl") files in the given directory.
        FCDstdlib.fcl if exists will be considered as standard library.
        
        @param s the directory in which the libraries should be present.
    */
    public void loadLibraryDirectory(String s)
    {
        String[] files;  // The names of the files in the directory.
        File dir = new File(s);
        
        // Obtain the list of files in the specified directory.
        files = dir.list(new FilenameFilter()
        { 
            public boolean accept(File dir, String name)
            {
                // This filter allows to obtain all files with the fcd 
                // file extension
                return name.toLowerCase(new Locale("en")).endsWith(".fcl");
            }
        });
        
        // We first check if the directory is existing or is not empty.        
        if(!dir.exists() || files==null) {
            if (!"".equals(s)){
                System.out.println("Warning! Library directory is incorrect:");
                System.out.println(s);
            }
            System.out.println(
            	"Activated FidoCadJ internal libraries and symbols.");
            return;
        }
        // We read all the directory content, file by file 
        for (int i = 0; i < files.length; i++) {
            File f;  // One of the files in the directory.
            f = new File(dir, files[i]);
            try {
                // Here we have a hopefully valid file in f, so we may read its
                // contents
                readLibraryFile(f.getPath());
            } catch (IOException E) {
                System.out.println("Problems reading library "+
                	f.getName()+" "+E);
            }
        } 
           
    }

}