package fidocadj.dialogs.controls;

/**
 This class represents a typed parameter that holds both the original value
 and the display value of a parameter. The original value is used to store
 the initial value of the parameter, while the display value can be used
 to show a modified or user-friendly version of the parameter in the UI.

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

 Copyright 2007-2024 by Davide Bucci, Manuel Finessi
 </pre>
 */
public class TypedParameter
{

    private Object originalValue;
    private Object displayValue;

    /**
     Constructs a new TypedParameter with the specified original and display
     values.
     @param originalValue The original value of the parameter.
     @param displayValue The display value of the parameter, which may be the
            same as the original value.
     */
    public TypedParameter(Object originalValue, Object displayValue)
    {
        this.originalValue = originalValue;
        this.displayValue = displayValue;
    }

    /**
     Sets the display value of the parameter.

     @param value The new display value to set.
     */
    public void setDisplayValue(Object value)
    {
        displayValue = value;
    }

    /**
     Sets the original value of the parameter.

     @param value The new original value to set.
     */
    public void setOriginalValue(Object value)
    {
        originalValue = value;
    }

    /**
     Gets the display value of the parameter.

     @return The display value of the parameter.
     */
    public Object getDisplayValue()
    {
        return displayValue;
    }

    /**
     Gets the original value of the parameter.

     @return The original value of the parameter.
     */
    public Object getOriginalValue()
    {
        return originalValue;
    }
}
