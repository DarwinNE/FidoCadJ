package fidocadj.globals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.ArrayList;
import java.util.prefs.Preferences;


/**
 The SettingsManager class is responsible for managing application
 settings in a centralized and thread-safe manner.
 This class is designed as a Singleton to ensure that there is only one
 instance managing the settings throughout the application.
 The settings can be stored and retrieved as various data types (e.g.,
 integers, doubles, booleans, strings) and are persisted using the
 Java Preferences API.

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

    // Singleton instance
    private static final SettingsManager INSTANCE = new SettingsManager();

    private final Preferences preferences;
    private final Map<String, Object> settings;
    private static final Stack<Map<String, Object>> backupStack = new Stack<>();
    private static final List<SettingsChangeListener> listeners =
        new ArrayList<>();

    /**
     Private constructor to prevent instantiation from outside the class.
     */
    private SettingsManager()
    {
        this.preferences = Preferences.userNodeForPackage(this.getClass());
        this.settings = new HashMap<>();
    }

    /**
     Returns the singleton instance of the {@code SettingsManager}.

     @return the singleton instance of the {@code SettingsManager}.
     */
    public static SettingsManager getInstance()
    {
        return INSTANCE;
    }

    /**
     Sets a setting in the manager and saves it to the Preferences store.

     @param key the key identifying the setting.
     @param value the value of the setting to store.
     */
    public static void set(String key, Object value)
    {
        INSTANCE.settings.put(key, value);
        INSTANCE.saveToPreferences(key, value);
        notifyListeners(key, value);
    }

    /**
     Gets a setting from the manager.
     If the setting is not found, returns null.

     @param key the key identifying the setting.

     @return the value of the setting, or null if not found.
     */
    public static Object get(String key)
    {
        return INSTANCE.settings.get(key);
    }

    /**
     Retrieves a setting from the manager or Preferences store with ..
     a specified default value.
     This method overloads the existing get method to match the signature ..
     used in older code.

     @param key the key identifying the setting.
     @param defaultValue default value to return if the setting is not found.

     @return the value of the setting as a String.
     */
    public static String get(String key, String defaultValue)
    {
        if (INSTANCE.settings.containsKey(key)) {
            Object value = INSTANCE.settings.get(key);
            return value != null ? value.toString() : defaultValue;
        }
        return INSTANCE.preferences.get(key, defaultValue);
    }

    /**
     Stores a setting in the manager and Preferences store as a String.
     This method overloads the set method to allow storing settings as strings.

     @param key the key identifying the setting.
     @param value the value of the setting to store as a String.
     */
    public static void put(String key, String value)
    {
        INSTANCE.settings.put(key, value);
        INSTANCE.preferences.put(key, value);
        notifyListeners(key, value);
    }

    /**
     Gets an integer setting from the manager or Preferences store.

     @param key the key identifying the setting.
     @param defaultValue default value to return if the setting is not found.

     @return the integer value of the setting.
     */
    public static int getInt(String key, int defaultValue)
    {
        String value = get(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     Gets a double setting from the manager or Preferences store.

     @param key the key identifying the setting.
     @param defaultValue default value to return if the setting is not found.

     @return the double value of the setting.
     */
    public static double getDouble(String key, double defaultValue)
    {
        String value = get(key, String.valueOf(defaultValue));
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     Gets a boolean setting from the manager or Preferences store.

     @param key the key identifying the setting.
     @param defaultValue default value to return if the setting is not found.

     @return the boolean value of the setting.
     */
    public static boolean getBoolean(String key, boolean defaultValue)
    {
        String value = get(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    /**
     Gets a string setting from the manager or Preferences store.

     @param key the key identifying the setting.
     @param defaultValue default value to return if the setting is not found.

     @return the string value of the setting.
     */
    public static String getString(String key, String defaultValue)
    {
        if (INSTANCE.settings.containsKey(key)) {
            return (String) INSTANCE.settings.get(key);
        }
        return INSTANCE.preferences.get(key, defaultValue);
    }

    /**
     Loads a setting from the Preferences store into the ..
     SettingsManager's internal map.

     @param key the key identifying the setting.
     @param defaultValue the default value to use if the setting ..
     is not found in Preferences.
     */
    public static void loadFromPreferences(String key, Object defaultValue)
    {
        if (defaultValue instanceof Integer) {
            INSTANCE.settings.put(key, INSTANCE.preferences.getInt(key,
                    (Integer) defaultValue));
        } else {
            if (defaultValue instanceof Double) {
                INSTANCE.settings.put(key, INSTANCE.preferences.getDouble(key,
                        (Double) defaultValue));
            } else {
                if (defaultValue instanceof Boolean) {
                    INSTANCE.settings.put(key, INSTANCE.preferences.getBoolean(
                            key, (Boolean) defaultValue));
                } else {
                    if (defaultValue instanceof String) {
                        INSTANCE.settings.put(key, INSTANCE.preferences.get(key,
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
    }

    /**
     Notifies all registered listeners that a setting has changed.

     @param key the key of the setting that changed.
     @param newValue the new value of the setting.
     */
    private static void notifyListeners(String key, Object newValue)
    {
        for (SettingsChangeListener listener : listeners) {
            listener.onSettingChanged(key, newValue);
        }
    }

    /**
     Removes a setting from the manager and the Preferences store.

     @param key the key identifying the setting to remove.
     */
    public static void remove(String key)
    {
        INSTANCE.settings.remove(key);
        INSTANCE.preferences.remove(key);
        notifyListeners(key, null);
    }

    /**
     Clears all settings from the manager and the Preferences store.
     */
    public static void clear()
    {
        INSTANCE.settings.clear();
        try {
            INSTANCE.preferences.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     Resets a specific setting to its default value.

     @param key the name of the setting to reset.
     @param defaultValue the default value to reset the setting to.
     */
    public static void resetSetting(String key, Object defaultValue)
    {
        set(key, defaultValue);
        INSTANCE.saveToPreferences(key, defaultValue);
    }

    /**
     Checks if a specific setting exists.

     @param key the name of the setting to check.

     @return true if the setting exists, otherwise false.
     */
    public static boolean containsSetting(String key)
    {
        return INSTANCE.settings.containsKey(key);
    }

    /**
     Returns all saved setting keys.

     @return a set containing all keys.
     */
    public static Set<String> getAllKeys()
    {
        return INSTANCE.settings.keySet();
    }

    /**
     Returns all saved settings as a map.

     @return a map containing all key-value pairs of settings.
     */
    public static Map<String, Object> getAllSettings()
    {
        return new HashMap<>(INSTANCE.settings);
    }

    /**
     Adds a listener that will be notified when a setting changes.

     @param listener the listener to add.
     */
    public static void addSettingsChangeListener(
            SettingsChangeListener listener)
    {
        listeners.add(listener);
    }

    /**
     Removes a previously added listener.

     @param listener the listener to remove.
     */
    public static void removeSettingsChangeListener(
            SettingsChangeListener listener)
    {
        listeners.remove(listener);
    }

    /**
     Performs a backup of the current settings.
     */
    public static void backupSettings()
    {
        backupStack.push(new HashMap<>(INSTANCE.settings));
    }

    /**
     Restores the settings from the last backup.
     */
    public static void restoreSettings()
    {
        if (!backupStack.isEmpty()) {
            INSTANCE.settings.clear();
            INSTANCE.settings.putAll(backupStack.pop());
        }
    }

    /**
     Interface for listening to settings changes.
     */
    public interface SettingsChangeListener
    {

        /**
         Called when a setting changes.

         @param key the key of the setting that changed.
         @param newValue the new value of the setting.
         */
        void onSettingChanged(String key, Object newValue);
    }
}
