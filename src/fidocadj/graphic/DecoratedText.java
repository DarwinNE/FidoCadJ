package fidocadj.graphic;

/** Decorated text is a class that provides advanced text functions.
    It is possible to do things as follows:

    I_dsat

    R^2

    V^2e

    x^2^3_-3_-4

    to indicate indices or exponents. The command _ indicates that the next
    character will be an index. The command ^ indicates that the next character
    is an exponent. If more of one character must be put, put them in braces.
    Use \_ to enter a bar and \^ to enter a caret.

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
    along with FidoCadJ. If not,
    @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.

    Copyright 2020-2023 by Davide Bucci
    </pre>
*/
public class DecoratedText
{
    private TextInterface g;
    private String btoken;
    private String bstr;
    private int currentIndex;
    private int lastIndex;
    private int exponentLevel;

    final static int CHUNK = 0;
    final static int INDEX = 1;
    final static int EXPONENT = 2;
    final static int END = 3;

    /** The creator.
        @param g the graphic object where to draw.
    */
    public DecoratedText(TextInterface g)
    {
        this.g=g;
    }

    /** Get the width of the given string with the current font.
        @param s the string to be used.
        @return the width of the string, in pixels.
    */
    public int getStringWidth(String s)
    {
        return g.getStringWidth(s);
    }

    private int getToken()
    {
        StringBuffer processToken;
        if(currentIndex >= lastIndex) {
            return END;
        }
        char c=bstr.charAt(currentIndex);
        char cp=0;
        if(currentIndex < lastIndex-1) {
            cp=bstr.charAt(currentIndex+1);
        }

        if(c=='\\') {
            c=cp;
            ++currentIndex;
        } else if(c=='_') {
            ++currentIndex;
            return INDEX;
        } else if (c=='^') {
            ++currentIndex;
            return EXPONENT;
        }
        processToken=new StringBuffer();
        while(true) {
            processToken.append(c);
            ++currentIndex;

            if(currentIndex>=lastIndex) {
                break;
            }
            c=bstr.charAt(currentIndex);
            if(c=='_' || c=='^' || c=='\\') {
                break;
            }
        }
        btoken=processToken.toString();
        return CHUNK;
    }

    private void resetTokenization(String s)
    {
        bstr=s;
        currentIndex=0;
        exponentLevel=0;
        lastIndex=s.length();
    }

    private float getSizeMultLevel()
    {
        switch((int)Math.abs(exponentLevel)){
            case 0:
                return 1f;
            case 1:
                return 0.8f;
            case 2:
                return 0.7f;
            case 3:
                return 0.6f;
            default:
                return 0.5f;
        }
    }
    /** Draw a string on the current graphic context.
        @param str the string to be drawn.
        @param x the x coordinate of the starting point.
        @param y the y coordinate of the starting point.
    */
    public void drawString(String str,
                                int x,
                                int y)
    {
        /*
            [FIDOCAD]
            FJC A 0.35
            TY 1 2 4 3 0 0 0 Helvetica 1^2^3^4^5^6^7^8^9
        */
        resetTokenization(str);
        int xc=x;
        double fontSize=g.getFontSize();
        int t;
        while((t=getToken())!=END) {
            switch(t) {
                case CHUNK:
                    g.setFontSize(fontSize*getSizeMultLevel());
                    // Font size is given in points, i.e. 1/72 of an inch.
                    // FidoCadJ has a 200 dpi internal resolution.
                    g.drawString(btoken, xc, y-(int)Math.round(
                            exponentLevel*fontSize*getSizeMultLevel()*0.5));
                    xc+=g.getStringWidth(btoken);
                    break;
                case EXPONENT:
                    ++exponentLevel;
                    break;
                case INDEX:
                    --exponentLevel;
                    break;
                case END:
                    break;
                default:
            }
        }
    }
}