package vinhtt.emby.sdkv4;

import java.util.prefs.Preferences;

public class ConfigService {

    private static final String PREF_NODE = "vinhtt/emby/sdkv4";
    private static final String KEY_SERVER = "serverAddress";
    private static final String KEY_APIKEY = "apiKey";
    private static final String KEY_USERNAME = "username";

    private static final String KEY_LOGIN_WIDTH = "loginWidth";
    private static final String KEY_LOGIN_HEIGHT = "loginHeight";
    private static final String KEY_MAIN_WIDTH = "mainWidth";
    private static final String KEY_MAIN_HEIGHT = "mainHeight";

    private Preferences prefs;

    public ConfigService() {
        prefs = Preferences.userRoot().node(PREF_NODE);
    }

    // --- Login Info ---

    public void saveLoginInfo(String server, String apiKey, String username) {
        prefs.put(KEY_SERVER, server);
        prefs.put(KEY_APIKEY, apiKey);
        prefs.put(KEY_USERNAME, username);
    }

    public String getServerAddress() {
        return prefs.get(KEY_SERVER, "http://localhost:8096/emby");
    }

    public String getApiKey() {
        return prefs.get(KEY_APIKEY, "");
    }

    public String getUsername() {
        return prefs.get(KEY_USERNAME, "admin");
    }

    // --- Login Window Size ---

    public void saveLoginWindowSize(double width, double height) {
        prefs.putDouble(KEY_LOGIN_WIDTH, width);
        prefs.putDouble(KEY_LOGIN_HEIGHT, height);
    }

    public double getLoginWindowWidth() {
        return prefs.getDouble(KEY_LOGIN_WIDTH, 450); // Default
    }

    public double getLoginWindowHeight() {
        return prefs.getDouble(KEY_LOGIN_HEIGHT, 500); // Default
    }

    // --- Main Window Size ---

    public void saveMainWindowSize(double width, double height) {
        prefs.putDouble(KEY_MAIN_WIDTH, width);
        prefs.putDouble(KEY_MAIN_HEIGHT, height);
    }

    public double getMainWindowWidth() {
        return prefs.getDouble(KEY_MAIN_WIDTH, 900); // Default
    }

    public double getMainWindowHeight() {
        return prefs.getDouble(KEY_MAIN_HEIGHT, 750); // Default
    }
}