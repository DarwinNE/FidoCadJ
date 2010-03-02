package dialogs;

import primitives.*;

/** This class contains some settings about the actual dashing style. It is used
	in the automatic primitive characteristics dialog.
	
	@author Davide Bucci
	@version 1.0 November 2009
	
	*/
public class DashInfo 
{
	public int style;
	
	public DashInfo(int i)
	{ style=i; }
	
	public int getStyle()
	{ return style; }
}
