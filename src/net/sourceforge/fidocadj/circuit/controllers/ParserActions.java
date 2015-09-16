package net.sourceforge.fidocadj.circuit.controllers;

import java.io.*;
import java.util.*;
import java.net.*;

import net.sourceforge.fidocadj.circuit.*;
import net.sourceforge.fidocadj.circuit.model.*;
import net.sourceforge.fidocadj.export.*;
import net.sourceforge.fidocadj.globals.*;
import net.sourceforge.fidocadj.layers.*;
import net.sourceforge.fidocadj.primitives.*;
import net.sourceforge.fidocadj.graphic.*;

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

    Copyright 2007-2015 by Davide Bucci
</pre>
*/

public class ParserActions
{
    private final DrawingModel model;

    // This is the maximum number of tokens which will be considered in a line
    static final int MAX_TOKENS=512;

    // True if FidoCadJ should use Windows style line feeds (appending \r
    // to the text generated).
    static final boolean useWindowsLineFeed=false;

    // Name of the last file opened
    public String openFileName = null;

    /** Standard constructor: provide the database class.
        @param pp the drawing model (database of the circuit).
    */
    public ParserActions (DrawingModel pp)
    {
        model=pp;
    }

    /** Parse the circuit contained in the StringBuffer specified.
        This function resets the primitive database and then parses the circuit.

        @param s the string containing the circuit
    */
    public void parseString(StringBuffer s)
    {
        model.getPrimitiveVector().clear();
        addString(s, false);
        model.setChanged(true);
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
        Q.setLibrary(model.getLibrary());  // Inherit the library
        Q.setLayers(model.getLayers());    // Inherit the layers

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

            ExportGraphic.export(temp,  Q, frm, 1,true,false, true,false);

            FileInputStream input = new FileInputStream(temp);
            BufferedReader bufRead = new BufferedReader(
                new InputStreamReader(input, Globals.encoding));

            String line=bufRead.readLine();
            if (line==null) {
                bufRead.close();
                return new StringBuffer("");
            }

            txt = new StringBuffer();

            do {
                txt.append(line);
                txt.append("\n");
                line =bufRead.readLine();
            } while (line != null);

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

        for (GraphicPrimitive g:model.getPrimitiveVector()){
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
        @return a StringBuffer containing the configuration settings in the
            FidoCadJ file format.
    */
    public StringBuffer registerConfiguration(boolean extensions)
    {
        StringBuffer s = new StringBuffer();

        // This is something which is not contemplated by the original
        // FidoCAD for Windows. If extensions are not activated, just exit.
        if(!extensions) {
            return s;
        }

        // Here is the beginning of the output. We can eventually provide
        // some hints about the configuration of the software (if needed).

        // We start by checking if the diameter of the electrical connection
        // should be written.

        // We consider that a difference of 1e-5 is small enough

        if(Math.abs(Globals.diameterConnectionDefault-
            Globals.diameterConnection)>1e-5)
        {
            s.append("FJC C "+Globals.diameterConnection+"\n");
        }

        s.append(checkAndRegisterLayers());

        // Check if the line widths should be indicated
        if(Math.abs(Globals.lineWidth -
            Globals.lineWidthDefault)>1e-5)
        {
            s.append("FJC A "+Globals.lineWidth+"\n");
        }
        if(Math.abs(Globals.lineWidthCircles -
            Globals.lineWidthCirclesDefault)>1e-5)
        {
            s.append("FJC B "+Globals.lineWidthCircles+"\n");
        }

        return s;
    }

    /** Check if the layers should be indicated.
    */
    private StringBuffer checkAndRegisterLayers()
    {
        StringBuffer s=new StringBuffer();
        Vector<LayerDesc> layerV=model.getLayers();
        Vector<LayerDesc> standardLayers =
            StandardLayers.createStandardLayers();

        for(int i=0; i<layerV.size();++i) {
            LayerDesc l = (LayerDesc)layerV.get(i);

            if (l.getModified()) {
                int rgb=l.getColor().getRGB();
                float alpha=l.getAlpha();
                s.append("FJC L "+i+" "+rgb+" "+alpha+"\n");
                // We compare the layers to the standard configuration.
                // If the name has been modified, the name configuration
                // is also saved.
                String defaultName=
                    ((LayerDesc)standardLayers.get(i)).getDescription();
                if (!l.getDescription().equals(defaultName)) {
                    s.append("FJC N "+i+" "+l.getDescription()+"\n");
                }
            }
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
        int j; // Token counter within the string
        boolean hasFCJ=false; // The last primitive had FCJ extensions
        StringBuffer token=new StringBuffer();

        String macroFont = model.getTextFont();
        int macroFontSize = model.getTextFontSize();

        GraphicPrimitive g = new PrimitiveLine(macroFont, macroFontSize);

        // The tokenized command string.
        String[] tokens=new String[MAX_TOKENS];

        // Name and value fields for a primitive. Those arrays will contain
        // the tokenized TJ commands which follow an appropriate FCJ modifier.
        String[] name=null;
        String[] value=null;

        int vn=0, vv=0;

        // Since the modifier FCJ follow the command, we need to save the
        // tokens of the line previously read, as well as the number of
        // tokens found in it.
        String[] old_tokens=new String[MAX_TOKENS];
        int old_j=0;


        int macro_counter=0;
        int l;

        token.ensureCapacity(256);

        /*  This code is not very easy to read. If more extensions of the
            original FidoCAD format (performed with the FCJ tag) are to be
            implemented, it can be interesting to rewrite the parser as a
            state machine.
        */
        synchronized(this) {
            Vector<LayerDesc> layerV=model.getLayers();

            int k;
            char c='\n';
            int len;

            // Actual line number. This is useful to indicate where errors are.
            int lineNum=1;


            j=0;
            token.setLength(0);
            len=s.length();

            // The purpose of this code is to tokenize the lines. Things are
            // made more complicated by the FCJ mechanism which acts as a
            // modifier for the previous command.

            for(i=0; i<len;++i){
                c=s.charAt(i);
                /*
                System.out.print("\u001B[31m");
                System.out.print(c);
                System.out.print("\u001B[0m");
                */

                if(c=='\n' || c=='\r'|| i==len-1) { //The string is finished
                    if(i==len-1 && c!='\n' && c!=' '){
                        token.append(c);
                    }
                    ++lineNum;
                    tokens[j]=token.toString();
                    if (token.length()==0)  // Avoids trailing spaces
                        j--;

                    try{
                        // When we enter here, we have tokenized the current
                        // line
                        // and we kept in memory also the previous one.

                        // The first possibility is that the current line does
                        // not
                        // contain a FCJ modifier. In this case, process the
                        // previous line since we have all the information
                        // needed
                        // for doing that.

                        if(hasFCJ && !tokens[0].equals("FCJ")) {
                            hasFCJ = registerPrimitivesWithFCJ(hasFCJ,
                                tokens, g, old_tokens, old_j, selectNew);
                        }

                        if(tokens[0].equals("FCJ")) {
                            // FidoCadJ extension!
                            // Here the FCJ modifier changes something on the
                            // previous command. So ve check case by case what
                            // has to be modified.

                            if(hasFCJ && old_tokens[0].equals("MC")) {
                                macro_counter=2;
                                g=new PrimitiveMacro(model.getLibrary(),layerV,
                                    macroFont, macroFontSize);
                                g.parseTokens(old_tokens, old_j+1);
                            } else if (hasFCJ && old_tokens[0].equals("LI")) {
                                g=new PrimitiveLine(macroFont, macroFontSize);

                                // We concatenate the two lines in a single
                                // array
                                // of tokens (the same code will be repeated
                                // several
                                // times for other commands also).

                                for(l=0; l<j+1; ++l)
                                    old_tokens[l+old_j+1]=tokens[l];

                                // Update the number of tokens
                                old_j+=j+1;

                                // The actual parsing of the tokens is
                                // relegated
                                // to the primitive.
                                g.parseTokens(old_tokens, old_j+1);
                                g.setSelected(selectNew);

                                if(old_j>5 && old_tokens[old_j].equals("1")) {
                                    macro_counter = 2;
                                } else {
                                    model.addPrimitive(g,false,null);
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
                                    model.addPrimitive(g,false,null);
                                }

                            } else if (hasFCJ && (old_tokens[0].equals("RV")||
                                old_tokens[0].equals("RP")))
                            {
                                g=new PrimitiveRectangle(macroFont,
                                    macroFontSize);

                                for(l=0; l<j+1; ++l)
                                    old_tokens[l+old_j+1]=tokens[l];

                                old_j+=j+1;
                                g.parseTokens(old_tokens, old_j+1);
                                g.setSelected(selectNew);
                                if(old_j>2 && old_tokens[old_j].equals("1")) {
                                    macro_counter = 2;
                                } else {
                                    model.addPrimitive(g,false,null);
                                }
                            } else if (hasFCJ && (old_tokens[0].equals("EV")||
                                old_tokens[0].equals("EP")))
                            {
                                g=new PrimitiveOval(macroFont, macroFontSize);

                                for(l=0; l<j+1; ++l)
                                    old_tokens[l+old_j+1]=tokens[l];

                                old_j+=j+1;
                                g.parseTokens(old_tokens, old_j+1);
                                g.setSelected(selectNew);
                                if(old_j>2 && old_tokens[old_j].equals("1")) {
                                    macro_counter = 2;
                                } else {
                                    model.addPrimitive(g,false,null);
                                }
                            } else if (hasFCJ && (old_tokens[0].equals("PV")||
                                old_tokens[0].equals("PP")))
                            {
                                g=new PrimitivePolygon(macroFont,
                                    macroFontSize);

                                for(l=0; l<j+1; ++l)
                                    old_tokens[l+old_j+1]=tokens[l];

                                old_j+=j+1;
                                g.parseTokens(old_tokens, old_j+1);
                                g.setSelected(selectNew);
                                if(old_j>2 && old_tokens[old_j].equals("1")) {
                                    macro_counter = 2;
                                } else {
                                    model.addPrimitive(g,false,null);
                                }
                            } else if (hasFCJ && (old_tokens[0].equals("CV")||
                                old_tokens[0].equals("CP")))
                            {
                                g=new PrimitiveComplexCurve(macroFont,
                                    macroFontSize);

                                for(l=0; l<j+1; ++l)
                                    old_tokens[l+old_j+1]=tokens[l];

                                old_j+=j+1;
                                g.parseTokens(old_tokens, old_j+1);
                                g.setSelected(selectNew);
                                // If we have a name/value following, we
                                // put macro_counter (successively used by
                                // TY to determine that we are in a case in
                                // which
                                // TY commands must not be considered as
                                // separate).
                                if(old_j>2 && old_tokens[old_j].equals("1")) {
                                    macro_counter = 2;
                                } else {
                                    model.addPrimitive(g,false,null);
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
                            fidoConfig(tokens, j, layerV);
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
                            model.addPrimitive(g,false,null);
                        } else if(tokens[0].equals("TY")) {
                            // The TY command is somewhat special, because
                            // it can be used to specify the name and the value
                            // of a primitive or a macro. Therefore, we try
                            // to understand in which case we are
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
                                model.addPrimitive(g, false,null);
                                macro_counter=0;
                            } else {
                                // If we are in the classical case of a simple
                                // isolated TY command, we process it.
                                g=new PrimitiveAdvText();
                                g.parseTokens(tokens, j+1);
                                g.setSelected(selectNew);
                                model.addPrimitive(g,false,null);
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
                        }  else if(tokens[0].equals("EV")
                            ||tokens[0].equals("EP"))
                        {
                            macro_counter=0;
                            for(l=0; l<j+1; ++l)
                                old_tokens[l]=tokens[l];
                            old_j=j;
                            hasFCJ=true;
                        } else if(tokens[0].equals("RV")
                            ||tokens[0].equals("RP"))
                        {
                            macro_counter=0;

                            for(l=0; l<j+1; ++l)
                                old_tokens[l]=tokens[l];
                            old_j=j;
                            hasFCJ=true;
                        } else if(tokens[0].equals("PV")
                            ||tokens[0].equals("PP"))
                        {
                            macro_counter=0;

                            for(l=0; l<j+1; ++l)
                                old_tokens[l]=tokens[l];
                            old_j=j;
                            hasFCJ=true;
                        } else if(tokens[0].equals("CV")
                            ||tokens[0].equals("CP"))
                        {
                            macro_counter=0;
                            for(l=0; l<j+1; ++l)
                                old_tokens[l]=tokens[l];
                            old_j=j;
                            hasFCJ=true;
                        }
                    } catch(IOException E) {
                        System.out.println("Error encountered: "+E.toString());
                        System.out.println("string parsing line: "+lineNum);
                        hasFCJ = true;
                        macro_counter = 0;

                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];

                        old_j=j;
                    } catch(NumberFormatException F) {
                        System.out.println(
                            "I could not read a number at line: "
                            +lineNum);
                        hasFCJ = true;
                        macro_counter = 0;
                        for(l=0; l<j+1; ++l)
                            old_tokens[l]=tokens[l];
                        old_j=j;
                    }
                    j=0;
                    token.setLength(0);
                } else if (c==' '){ // Ready for next token
                    tokens[j]=token.toString();
                    ++j;
                    if (j>=MAX_TOKENS) {
                        System.out.println("Too much tokens!");
                        System.out.println("string parsing line: "+lineNum);
                    }
                    token.setLength(0);
                } else {
                    token.append(c);
                }
            }

            // We need to process the very last line, which is contained in
            // the tokens currently read.
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


            model.sortPrimitiveLayers();
        }
    }

    /** Handle the FCJ command for the program configuration.

    */
    private void fidoConfig(String[] tokens, int ntokens,
        Vector<LayerDesc> layerV)
    {
        double newConnectionSize = -1.0;
        double newLineWidth = -1.0;
        double newLineWidthCircles = -1.0;

        // FidoCadJ Configuration

        if(tokens[1].equals("C")) {
            // Connection size
            newConnectionSize = Double.parseDouble(tokens[2]);
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
                for(int t=3; t<ntokens+1; ++t) {
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
            newLineWidth = Double.parseDouble(tokens[2]);
        } else if(tokens[1].equals("B")) {
            // Connection size
            newLineWidthCircles = Double.parseDouble(tokens[2]);
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
    }

    /** This method checks if a primitive may have FCJ  modifiers following.
        If no further FCJ tokens are present, the primitive is created
        immediately. If a FCJ token follows, we proceed to further parsing
        what follows.
    */
    private boolean registerPrimitivesWithFCJ(boolean hasFCJ_t,
        String[] tokens,
        GraphicPrimitive gg, String[] old_tokens, int old_j,
        boolean selectNew)
        throws IOException
    {
        String macroFont = model.getTextFont();
        int macroFontSize = model.getTextFontSize();
        Vector<GraphicPrimitive> primitiveVector=model.getPrimitiveVector();
        Vector<LayerDesc> layerV=model.getLayers();

        GraphicPrimitive g=gg;
        boolean hasFCJ=hasFCJ_t;
        boolean addPrimitive = false;
        if(hasFCJ && !tokens[0].equals("FCJ")) {
            if (old_tokens[0].equals("MC")) {
                g=new PrimitiveMacro(model.getLibrary(),
                    layerV, macroFont, macroFontSize);
                addPrimitive = true;
            } else if (old_tokens[0].equals("LI")) {
                g=new PrimitiveLine(macroFont, macroFontSize);
                addPrimitive = true;
            } else if (old_tokens[0].equals("BE")) {
                g=new PrimitiveBezier(macroFont, macroFontSize);
                addPrimitive = true;
            } else if (old_tokens[0].equals("RP")||
                old_tokens[0].equals("RV"))
            {
                g=new PrimitiveRectangle(macroFont, macroFontSize);
                addPrimitive = true;
            } else if (old_tokens[0].equals("EP")||
                old_tokens[0].equals("EV"))
            {
                g=new PrimitiveOval(macroFont, macroFontSize);
                addPrimitive = true;
            } else if (old_tokens[0].equals("PP")
                ||old_tokens[0].equals("PV"))
            {
                g=new PrimitivePolygon(macroFont, macroFontSize);
                addPrimitive = true;
            } else if(old_tokens[0].equals("PL")) {
                g=new PrimitivePCBLine(macroFont, macroFontSize);
                addPrimitive = true;
            } else if (old_tokens[0].equals("CP")
                ||old_tokens[0].equals("CV"))
            {
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
            model.addPrimitive(g,false,null);
            hasFCJ = false;
        }
        return hasFCJ;
    }

    /** Read all librairies contained in the given URL at the given prefix.
        This is particularly useful to read librairies shipped in a jar
        file.
        @param s the URL containing the libraries.
        @param prefix_s the prefix to be adopted for the keys of all elements
            in the library. Most of the times it is the filename, except for
            standard or internal libraries.
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
        @throws IOException when something goes horribly wrong. Most of the
            times the filename is not found.
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
        @throws IOException when something goes horribly wrong.
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
                    model.getLibrary().put(macroName, new
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
                macroDesc = model.getLibrary().get(macroName);
                if(macroDesc==null)
                    return;
                macroDesc.name = longName;
                macroDesc.key = macroName;
                macroDesc.category = categoryName;
                macroDesc.library = libraryName;
                macroDesc.filename = prefix;

                macroDesc.description = macroDesc.description + "\n" +
                        line;
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
