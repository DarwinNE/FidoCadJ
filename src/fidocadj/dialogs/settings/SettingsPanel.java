package fidocadj.dialogs.settings;

/**
 SettingsPanel is an interface that defines the contract for panels
 used in the DialogOptions dialog. Each panel should be able to set up
 its UI, load settings from the SettingsManager, and save settings back to it.

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

 Copyright 2008-2024 by Davide Bucci, Manuel Finessi
 </pre>
 */
public interface SettingsPanel
{

    /**
     Set up the UI components and layout.
     */
    void setupUI();

    /**
     Load settings from the SettingsManager and populate the UI components.
     */
    void loadSettings();

    /**
     Save the settings from the UI components into SettingsManager.
     */
    void saveSettings();
}
