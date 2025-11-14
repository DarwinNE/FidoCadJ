package fidocadj;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** UpdateChecker .java
    Background checker for software releases.

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

    Copyright 2015-2025 by Davide Bucci, Manuel Finessi
    </pre>

    @author Manuel Finessi
*/

public class UpdateChecker 
{
    
    private static final String RELEASES_URL = 
            "https://github.com/FidoCadJ/FidoCadJ/releases/";
    
    private static final int TIMEOUT_MS = 10000;
    
    private final String currentVersion;
    private final UpdateListener listener;
    private Thread checkerThread;
    
    /**
     * Listener interface for receiving update notifications.
     */
    public interface UpdateListener 
    {
        /**
         * Called when a newer version is available.
         * 
         * @param latestVersion the version string of the latest release
         */
        void onUpdateAvailable(String latestVersion);
        
        /**
         * Called when the current version is up to date.
         */
        void onNoUpdateAvailable();
        
        /**
         * Called when the update check fails for any reason.
         * 
         * @param reason a description of why the check failed
         */
        void onCheckFailed(String reason);
    }
    
    /**
     * Constructs a new UpdateChecker.
     * 
     * @param currentVersion the current version of the application 
     * @param listener the listener to receive update notifications 
     */
    public UpdateChecker(String currentVersion, UpdateListener listener) 
    {
        this.currentVersion = currentVersion;
        this.listener = listener;
    }
    
    /**
     * Starts the update check in a separate background thread.
     * If a check is already running, this method does nothing.
     * The thread is configured as a daemon thread to avoid
     * blocking application shutdown.
     */
    public void startCheck() 
    {
        if (checkerThread != null && checkerThread.isAlive()) {
            return;
        }
        
        checkerThread = new Thread(() -> {
            try {
                String latestVersion = fetchLatestVersion();
                
                if (latestVersion != null) {
                    if (isNewerVersion(latestVersion, currentVersion)) {
                        if (listener != null) {
                            listener.onUpdateAvailable(latestVersion);
                        }
                    } else {
                        if (listener != null) {
                            listener.onNoUpdateAvailable();
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.onCheckFailed(
                                "Unable to determine latest version");
                    }
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onCheckFailed(
                            "Error during check: " + e.getMessage());
                }
            }
        }, "UpdateCheckerThread");
        
        checkerThread.setDaemon(true);
        checkerThread.start();
    }
    
    /**
     * Fetches the releases page and extracts the latest version.
     * 
     * @return the latest version string, or null if it cannot be determined
     */
    private String fetchLatestVersion() 
    {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        
        try {
            URL url = new URL(RELEASES_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty(
                    "User-Agent", "FidoCadJ-UpdateChecker");
            
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return null;
            }
            
            reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            
            StringBuilder content = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            
            return parseLatestVersion(content.toString());
            
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (reader != null) reader.close();
                if (connection != null) connection.disconnect();
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }
    
    /**
     * Parses the HTML content to extract the latest version number.
     * Uses multiple regex patterns to handle different 
     * GitHub release page formats.
     * 
     * @param html the HTML content of the releases page
     * @return the version string if found, null otherwise
     */
    private String parseLatestVersion(String html) 
    {
        try {
            Pattern pattern = Pattern.compile(
                    "/releases/tag/v?([0-9]+\\.[0-9]+\\.[0-9]+(?:[\\s_-]?(?" +
                    ":alpha|beta|gamma|delta|epsilon|rc|release))" +
                    "?[a-zA-Z0-9\\-]*)");
            
            Matcher matcher = pattern.matcher(html);

            if (matcher.find()) {
                return matcher.group(1);
            }

            pattern = Pattern.compile(
                    "releases/tag/([0-9]+\\.[0-9]+\\.[0-9]+(?:[\\s_-]?(?" + 
                    ":alpha|beta|gamma|delta|epsilon|rc|release))" + 
                    "?[a-zA-Z0-9\\-]*)");
            
            matcher = pattern.matcher(html);

            if (matcher.find()) {
                return matcher.group(1);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
    * Compares two version strings to determine if the first 
    * is newer than the second.
    * Supports semantic versioning format 
    * (major.minor.patch) with optional Greek suffixes.
    * Only suggests stable versions (without suffixes) as updates, 
    * unless the current version is also a pre-release.
    * 
    * @param v1 the first version string (e.g., "0.24.9", "0.24.8 beta")
    * @param v2 the second version string (e.g., "0.24.8", "0.24.8 alpha")
    * @return true if v1 is newer than v2 and should be suggested as an update
    */
    private boolean isNewerVersion(String v1, String v2) {
        try {
            // Remove any 'v' prefix
            v1 = v1.replaceAll("^v", "").trim();
            v2 = v2.replaceAll("^v", "").trim();

            // Extract numeric parts and suffix
            Pattern versionPattern = Pattern.compile(
                    "^([0-9]+)\\.([0-9]+)\\.([0-9]+)(?:[\\s_-]?" +
                    "(alpha|beta|gamma|delta|epsilon|rc|release)?)?");

            Matcher m1 = versionPattern.matcher(v1);
            Matcher m2 = versionPattern.matcher(v2);

            if (!m1.find() || !m2.find()) {
                return false;
            }

            int major1 = Integer.parseInt(m1.group(1));
            int minor1 = Integer.parseInt(m1.group(2));
            int patch1 = Integer.parseInt(m1.group(3));
            String suffix1 = m1.group(4);

            int major2 = Integer.parseInt(m2.group(1));
            int minor2 = Integer.parseInt(m2.group(2));
            int patch2 = Integer.parseInt(m2.group(3));
            String suffix2 = m2.group(4);

            boolean isVersion1Stable = (suffix1 == null || 
                    suffix1.isEmpty() || suffix1.equalsIgnoreCase("release"));
            
            boolean isVersion2Stable = (suffix2 == null || 
                    suffix2.isEmpty() || suffix2.equalsIgnoreCase("release"));

            // If v1 is a pre-release and v2 is stable, never suggest the update
            if (!isVersion1Stable && isVersion2Stable) {
                return false;
            }

            // If version1 is a pre-release and version2 is also a pre-release,
            // only suggest if they are on the same numeric version
            if (!isVersion1Stable && !isVersion2Stable) {
                // Only compare pre-releases of the same numeric version
                if (major1 == major2 && minor1 == minor2 && patch1 == patch2) {
                    return getSuffixPriority(suffix1) > 
                            getSuffixPriority(suffix2);
                }
                return false; // Don't suggest different 
                              // numeric versions of pre-releases
            }

            // Compare numeric versions
            if (major1 != major2) return major1 > major2;
            if (minor1 != minor2) return minor1 > minor2;
            if (patch1 != patch2) return patch1 > patch2;

            // If we reach here, numeric versions are equal
            // version1 is stable, v2 might be pre-release
            return isVersion1Stable && !isVersion2Stable;

        } catch (Exception e) {
            return false;
        }
    }
   
   /**
    * Returns the priority value for a version suffix.
    * Higher values indicate more stable/recent versions.
    * 
    * @param suffix the version suffix (alpha, beta, etc.) or null for stable
    * @return the priority value
    */
   private int getSuffixPriority(String suffix) 
   {
       if (suffix == null || suffix.isEmpty() || 
               suffix.equalsIgnoreCase("release")) 
       {
           return 100; // Stable version (no suffix)
       }

       switch (suffix.toLowerCase()) {
           case "rc":
               return 90;
           case "epsilon":
               return 50;
           case "delta":
               return 40;
           case "gamma":
               return 30;
           case "beta":
               return 20;
           case "alpha":
               return 10;
           default:
               return 0;
       }
   }
}
