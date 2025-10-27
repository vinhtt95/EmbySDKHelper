package vinhtt.emby.sdkv4;

import java.util.prefs.Preferences;

public class ConfigService {

    private static final String PREF_NODE = "vinhtt/emby/sdkv4";
    private static final String KEY_SERVER = "serverAddress";
    private static final String KEY_APIKEY = "apiKey";
    private static final String KEY_USERNAME = "username";

    // --- THÊM CÁC KEY MỚI ---
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_LANGUAGE = "language"; // <-- THÊM KEY NGÔN NGỮ
    // --- (Kết thúc thêm key) ---


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

    // --- THÊM CÁC HÀM LƯU/TẢI SESSION ---
    public void saveSession(String accessToken, String userId) {
        prefs.put(KEY_ACCESS_TOKEN, accessToken != null ? accessToken : "");
        prefs.put(KEY_USER_ID, userId != null ? userId : "");
        try {
            prefs.flush(); // Đảm bảo lưu ngay lập tức
        } catch (Exception e) {
            System.err.println("Lỗi flush preferences khi lưu session: " + e.getMessage());
        }
    }

    public String getAccessToken() {
        return prefs.get(KEY_ACCESS_TOKEN, null); // Trả về null nếu không có
    }

    public String getUserId() {
        return prefs.get(KEY_USER_ID, null); // Trả về null nếu không có
    }

    public void clearSession() {
        prefs.remove(KEY_ACCESS_TOKEN);
        prefs.remove(KEY_USER_ID);
        try {
            prefs.flush();
        } catch (Exception e) {
            System.err.println("Lỗi flush preferences khi xóa session: " + e.getMessage());
        }
        System.out.println("Đã xóa session đã lưu.");
    }
    // --- (Kết thúc thêm hàm session) ---

    // --- THÊM HÀM LƯU/TẢI NGÔN NGỮ ---
    public void saveLanguage(String langCode) {
        prefs.put(KEY_LANGUAGE, langCode);
        try {
            prefs.flush();
        } catch (Exception e) {
            System.err.println("Lỗi flush preferences khi lưu ngôn ngữ: " + e.getMessage());
        }
    }

    public String getLanguage() {
        return prefs.get(KEY_LANGUAGE, "vi"); // Mặc định là 'vi'
    }
    // --- (Kết thúc) ---


    // Các hàm getServerAddress, getApiKey, getUsername không đổi...
    public String getServerAddress() { return prefs.get(KEY_SERVER, "http://localhost:8096/emby"); }
    public String getApiKey() { return prefs.get(KEY_APIKEY, ""); }
    public String getUsername() { return prefs.get(KEY_USERNAME, "admin"); }

    // Các hàm lưu/tải kích thước cửa sổ (ĐÃ SỬA)
    public void saveLoginWindowSize(double width, double height) {
        prefs.putDouble(KEY_LOGIN_WIDTH, width);
        prefs.putDouble(KEY_LOGIN_HEIGHT, height);
    }
    public double getLoginWindowWidth() { return prefs.getDouble(KEY_LOGIN_WIDTH, 450); }
    public double getLoginWindowHeight() { return prefs.getDouble(KEY_LOGIN_HEIGHT, 500); }

    public void saveMainWindowSize(double width, double height) {
        prefs.putDouble(KEY_MAIN_WIDTH, width);
        prefs.putDouble(KEY_MAIN_HEIGHT, height);
    }
    public double getMainWindowWidth() { return prefs.getDouble(KEY_MAIN_WIDTH, 900); }
    public double getMainWindowHeight() { return prefs.getDouble(KEY_MAIN_HEIGHT, 850); } // <-- TĂNG CHIỀU CAO MẶC ĐỊNH
}