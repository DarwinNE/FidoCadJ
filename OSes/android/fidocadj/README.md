FidoCadJ for Android README file

By Davide Bucci, Giuseppe Amato 2014

This folder contains the complete sources of the FidoCadJ for Android
application.
This app shares a lot of files with the main program running on PC's using
Swing.
Those files are present in the source arborescence by means of symbolic links.
So, please use a decent operating system allowing you to use symbolic links for
files, if you want to contribute to the coding.
You may also notice that in some cases different files with the same name and
package are present for the Swing version and the Android version.
This is because those files need some version-specific features.

IMPORTANT NOTE: there are some strict coding conventions to be respected.
They include the indenting and the curly brackets positioning rules.
Please refer to the "3.3 Coding conventions" section, in the `README.md` file
of the Swing application.


1 - Files and directories
=========================

| File                        | DIR | Comments                                 |
| --------------------------- |:---:| ---------------------------------------  |
| `AndroidManifest.xml`       |     |                                          |
| `ant.properties`            |     |                                          |
| `build.xml`                 |     |                                          |
| `count`                     |     |                                          |
| `dimen.sh`                  |     |                                          |
| `fidocadj_android_pmd.html` |     |                                          |
| `libs/`                     |  X  |                                          |
| `linkedResource.py`         |     | Create the XML string for Eclipse on Win |
| `local.properties`          |     |                                          |
| `pmd.sh`                    |     |                                          |
| `proguard-project.txt`      |     |                                          |
| `project.properties`        |     |                                          |
| `proTOxml.py`               |     | Create the XML string resource files     |
| `README.md`                 |     | This file                                |
| `res/`                      |  X  | Contains the Android project resources   |
| `res.sh`                    |     | Create the Android project resources     |
| `dimen.sh`                  |     | Generate resource files for size conf.   |
| `src/`                      |  X  | Contains all the project sources         |


2 - Resources
=============

The primary resources are always those for the Swing application. For this
reason, if you need to add a string, modify the resource files of the
Swing application (`/bin/*.resources`) and then run the `res.sh` script.


3 - Building on Windows with Eclipse
====================================

1. Import the existing project in Eclipse.
2. Execute `linkedResource.py` file which is in the FidoCadJ for Android root
   directory.
3. It will create a file named `linkedResources.xml`.
4. Copy the content of file `linkedResources.xml` and paste in the file
   `.project` created by Eclipse, between tags
   `<projectDescription></projectDescription>`.
5. Refresh the project in Eclipse.

Be sure all the dependencies of Android SDK are fulfilled.


4 - Differences with the Swing application
==========================================

The Android code is based on the same low level code of the Swing application.
More or less 65% of the code is the same for the two applications, the main
difference being the adoption of the Swing GUI code or the Android one.

However, there are some notable differences between the two applications:

- In Swing you can choose the user library directory, with Android it will
  always be the `FidoCadJ/Libs` dir.
- You can not customize symbols and edit user libraries with the Android app.
- You can not change the color of the layers with the Android app.
- You can not export drawings with the Android app.


5 - Acknowledgments
===================

See the `README.md` file for the Swing application for the complete list of
acknowledgments. Here we just deal with the specific Android coding.

Code
:   Davide Bucci, Dante Loi, Giuseppe Amato

When possible, the authors of the snippets have been contacted to gain
explicit permission of using the code in an open source project. If you own
the copyright of some of the reused code and you do not agree on its
inclusion in the FidoCadJ project, open an Issue on GitHub and we will remove
the offending code as fast as we can.


6 - Licensing
=============

FidoCadJ is distributed with the GPL v. 3 license:

    FidoCadJ is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FidoCadJ is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.
