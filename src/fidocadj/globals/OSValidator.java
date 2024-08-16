package fidocadj.globals;


/** OSValidator.java
 * Utility class for detecting the current operating system.
 *
 * This class provides static methods to determine if the operating system
 * is Windows, macOS, Unix/Linux, or Solaris. The OS name is retrieved
 * from the system properties when the class is loaded.
 *
 * <pre>
 * This file is part of FidoCadJ.
 *
 * FidoCadJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FidoCadJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FidoCadJ. If not,
 * @see <a href=http://www.gnu.org/licenses/>http://www.gnu.org/licenses/</a>.
 *
 * Copyright 2008-2023 by Davide Bucci
 * </pre>
 *
 * @author Manuel Finessi
 */
public final class OSValidator
{

    /**
     * The name of the operating system,
     * converted to lowercase for easy comparison.
     *
     * <p>
     * This variable is initialized once when the class is loaded, and
     * it is used by all the static methods to determine the OS type.</p>
     */
    private static final String OS=System.getProperty("os.name").toLowerCase();

    /**
     * Private constructor to prevent instantiation.
     *
     * <p>
     * This class should not be instantiated because it only contains
     * static utility methods.</p>
     */
    private OSValidator()
    {
        // Prevent instantiation
    }

    /**
     * Checks if the current operating system is Windows.
     *
     * @return {@code true} if the OS is Windows, {@code false} otherwise.
     */
    public static boolean isWindows()
    {
        return OS.contains("win");
    }

    /**
     * Checks if the current operating system is macOS.
     *
     * @return {@code true} if the OS is macOS, {@code false} otherwise.
     */
    public static boolean isMac()
    {
        return OS.contains("mac");
    }

    /**
     * Checks if the current operating system is Unix/Linux.
     *
     * <p>
     * This method checks for several common Unix-based OS names
     * including "nix", "nux", and "aix".</p>
     *
     * @return {@code true} if the OS is Unix or Linux, {@code false} otherwise.
     */
    public static boolean isUnix()
    {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    /**
     * Checks if the current operating system is Solaris.
     *
     * @return {@code true} if the OS is Solaris, {@code false} otherwise.
     */
    public static boolean isSolaris()
    {
        return OS.contains("sunos");
    }
}
