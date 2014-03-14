import java.util.*;

public class Library
{
    String libraryName;
    String filename;
    boolean isStd;
    ArrayList<Category> categories;

    Library(String libraryName,String filename,boolean isStd)
    {
        this.libraryName = libraryName;
        this.filename = filename;
        this.isStd = isStd;
        categories = new ArrayList<Category>();
    }

    public String getName()
    {
        return libraryName;
    }

    public void setName(String name)
    {
        this.libraryName = name;
    }


    public String getFileName()
    {
        return filename;
    }

    public List<Category> getAllCategories()
    {
        return categories;
    }

    public Category getCategory(String name)
    {
        Category result=null;
        for(Category c:categories) {
            if(c.getName().equals(name)) {
                result=c;
                break;
            }
        }
        return result;
    }

    public void addCategory(Category category)
    {
        categories.add(category);
    }

    public void removeCategory(Category category)
    {
        categories.remove(category);
    }

    public boolean isStdLib()
    {
        return isStd;
    }

    public boolean isHidden()
    {
        return false;
    }

    public static boolean isValidName(String name)
    {
        return true;
    }

    public boolean containsMacroKey(String key)
    {
        if(key==null) {
            return true;
        }

        for(Category category:categories) {
            if(category.containsMacroKey(key)) {
                return true;
            }
        }
        return false;
    }

}
