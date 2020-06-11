package net.sourceforge.fidocadj.globals;

/** ProvidesCopyPasteInterface is an interface describing a minimalistic
    set of methods which may be used during copy/paste operations.

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

    Copyright 2008-2015 by Davide Bucci

    </pre>
*/
public interface ProvidesCopyPasteInterface
{
    /** Copy a text into the clipboard.
        @param s the text to be copied.
    */
    public void copyText(String s);

    /** Paste a text from the clipboard.
        @return the text retrieved from the clipboard.
    */
    public String pasteText();
}