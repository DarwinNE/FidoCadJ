package fidocadj.dialogs.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 The SettingsManager class is responsible for managing application settings
 in a centralized way. It provides methods to store and retrieve various
 types of settings (e.g., integers, doubles, booleans, strings) and handles
 saving/loading these settings using the Java Preferences API.

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
public class SettingsManager
{

    private final Preferences preferences;
    private final Map<String, Object> settings;

    /**
     Constructs a new SettingsManager associated with a specific class.
     The Preferences API uses this class for storing and retrieving settings.

     @param clazz the class for which the preferences node is created.
     */
    public SettingsManager(Class<?> clazz)
    {
        this.preferences = Preferences.userNodeForPackage(clazz);
        this.settings = new HashMap<>();
    }

    /**
     Sets a setting in the manager and saves it to the Preferences store.

     @param key the key identifying the setting.
     @param value the value of the setting to store.
     */
    public void set(String key, Object value)
    {
        settings.put(key, value);
        saveToPreferences(key, value);
    }

    /**
     Gets a setting from the manager.
     If the setting is not found, returns null.

     @param key the key identifying the setting.

     @return the value of the setting, or null if not found.
     */
    public Object get(String key)
    {
        return settings.get(key);
    }

    /**
     Retrieves a setting from the Preferences store with a ..
     specified default value.
     This method overloads the existing get method to match the ..
     signature used in older code.

     @param key the key identifying the setting.
     @param defaultValue default value to return if the setting is not found.

     @return the value of the setting as a String.
     */
    public String get(String key, String defaultValue)
    {
        if (settings.containsKey(key)) {
            Object value = settings.get(key);
            return value != null ? value.toString() : defaultValue;
        }
        return preferences.get(key, defaultValue);
    }

    /**
     Stores a setting in the Preferences store. 
     This method overloads the existing set method to allow storing ..
     settings as strings to maintain compatibility with older code.

     @param key the key identifying the setting.
     @param value the value of the setting to store as a String.
     */
    public void put(String key, String value)
    {
        settings.put(key, value);
        preferences.put(key, value);
    }

    /**
     Gets an integer setting from the manager or preferences store.

     @param key the key identifying the setting.
     @param defaultValue default value to return if the setting is not found.

     @return the integer value of the setting.
     */
    public int getInt(String key, int defaultValue)
    {
        String value = get(key, String.valueOf(defaultValue)); 
        try {
            return Integer.parseInt(value); 
        } catch (NumberFormatException e) {
            return defaultValue; 
        }
    }

    /**
     Gets a double setting from the manager or preferences store.

     @param key the key identifying the setting.
     @param defaultValue default value to return if the setting is not found.

     @return the double value of the setting.
     */
    public double getDouble(String key, double defaultValue)
    {
        String value = get(key, String.valueOf(defaultValue));
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // If the conversion fails, it returns the default value.
            return defaultValue;
        }
    }
    
    /**
     Gets a boolean setting from the manager or preferences store.

     @param key the key identifying the setting.
     @param defaultValue default value to return if the setting is not found.

     @return the boolean value of the setting.
     */
    public boolean getBoolean(String key, boolean defaultValue)
    {
        String value = get(key, String.valueOf(defaultValue));  
        return Boolean.parseBoolean(value); 
    }

    /**
     Gets a string setting from the manager or preferences store.

     @param key the key identifying the setting.
     @param defaultValue default value to return if the setting is not found.

     @return the string value of the setting.
     */
    public String getString(String key, String defaultValue)
    {
        if (settings.containsKey(key)) {
            return (String) settings.get(key);
        }
        return preferences.get(key, defaultValue);
    }

    /**
     Loads a setting from the Preferences store into the SettingsManager's
     internal map.

     @param key the key identifying the setting.
     @param defaultValue the default value to use if the setting is not..
     found in Preferences.
     */
    public void loadFromPreferences(String key, Object defaultValue)
    {
        if (defaultValue instanceof Integer) {
            settings.put(key, preferences.getInt(key, (Integer) defaultValue));
        } else {
            if (defaultValue instanceof Double) {
                settings.put(key, preferences.getDouble(key,
                        (Double) defaultValue));
            } else {
                if (defaultValue instanceof Boolean) {
                    settings.put(key, preferences.getBoolean(key,
                            (Boolean) defaultValue));
                } else {
                    if (defaultValue instanceof String) {
                        settings.put(key, preferences.get(key,
                                (String) defaultValue));
                    }
                }
            }
        }
    }

    /**
     Saves a setting value to the Preferences store based on its type.

     @param key the key identifying the setting.
     @param value the value of the setting to save.
     */
    private void saveToPreferences(String key, Object value)
    {
        if (value instanceof Integer) {
            preferences.putInt(key, (Integer) value);
        } else {
            if (value instanceof Double) {
                preferences.putDouble(key, (Double) value);
            } else {
                if (value instanceof Boolean) {
                    preferences.putBoolean(key, (Boolean) value);
                } else {
                    if (value instanceof String) {
                        preferences.put(key, (String) value);
                    }
                }
            }
        }
        // Additional types can be added as needed.
    }

    /**
     Removes a setting from the manager and the Preferences store.

     @param key the key identifying the setting to remove.
     */
    public void remove(String key)
    {
        settings.remove(key);
        preferences.remove(key);
    }

    /**
     Clears all settings from the manager and the Preferences store.
     */
    public void clear()
    {
        settings.clear();
        try {
            preferences.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
