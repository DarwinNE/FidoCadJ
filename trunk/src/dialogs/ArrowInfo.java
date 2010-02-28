package dialogs;

import primitives.*;

/** This class contains information about the arrow style. It is useful 
	for the automatic generation of the properties dialog.


	@author Davide Bucci
	@version 1.0 November 2009
	
	*/
public class ArrowInfo 
{
	public int style;
	
	public ArrowInfo(int i)
	{ style=i; }
	
	public int getStyle()
	{ return style; }
}
