package primitives;
/** Class MacroDesc provides a standard description of the macro. It provides
	its name, its description and its category */

public class MacroDesc {
	public String name;
	public String key;
	public String description;
	public String category;
	public String library;
	
	/** Standard constructor. Give the macro's name, description and cathegory 
		
	*/
	public MacroDesc(String ke, String na, String de, String cat, String lib)
	{
		name = na;
		key=ke;
		description = de;
		category = cat;
		library = lib;
		
	}
	
	public String toString() 
	{
		return name.trim();//+", "+key+", "+category+", "+library;
	}
}