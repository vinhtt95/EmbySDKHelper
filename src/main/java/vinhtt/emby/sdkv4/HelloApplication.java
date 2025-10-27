package vinhtt.emby.sdkv4;

import embyclient.ApiClient;
import embyclient.Configuration;
import embyclient.api.UserServiceApi;
import embyclient.auth.ApiKeyAuth;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream; // <-- Cần
import java.io.InputStreamReader; // <-- Cần
import java.nio.charset.StandardCharsets; // <-- Cần
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle; // <-- Cần

public class HelloApplication extends Application {

    private ConfigService configService = new ConfigService();
    private Stage primaryStage;
    private ResourceBundle bundle;

    // ĐÃ XÓA LỚP Utf8Control KHỎI ĐÂY

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        // --- TẢI RESOURCE BUNDLE (FIXED FOR NAMED MODULES) ---
        String lang = configService.getLanguage();
        Locale currentLocale = Locale.of(lang);
        Locale.setDefault(currentLocale);

        // Phương thức mới: Tải ResourceBundle thủ công với UTF-8
        String preferredBundlePath = "vinhtt/emby/sdkv4/messages_" + currentLocale.getLanguage() + ".properties";
        ResourceBundle tempBundle = null;

        // 1. Thử tải gói ngôn ngữ đã lưu (ví dụ: messages_vi.properties)
        try (InputStream is = HelloApplication.class.getResourceAsStream(preferredBundlePath)) {
            if (is != null) {
                try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    tempBundle = new PropertyResourceBundle(reader);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file properties cho locale " + lang + ": " + e.getMessage());
        }

        // 2. Fallback sang tiếng Anh nếu ngôn ngữ đã lưu không tồn tại hoặc lỗi
        if (tempBundle == null && !currentLocale.getLanguage().equals("en")) {
            System.out.println("Không tìm thấy file locale " + lang + ". Đang thử tải tiếng Anh.");
            String englishBundlePath = "vinhtt/emby/sdkv4/messages_en.properties";
            try (InputStream is = HelloApplication.class.getResourceAsStream(englishBundlePath)) {
                if (is != null) {
                    try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        tempBundle = new PropertyResourceBundle(reader);
                    }
                }
            } catch (IOException e) {
                System.err.println("Lỗi đọc file properties cho locale English: " + e.getMessage());
            }
        }

        // 3. Gán gói ngôn ngữ (Đảm bảo this.bundle không null)
        if (tempBundle != null) {
            this.bundle = tempBundle;
        } else {
            // Fallback cuối cùng nếu không có file nào đọc được (chỉ nên xảy ra khi project không có file properties)
            this.bundle = ResourceBundle.getBundle("vinhtt.emby.sdkv4.messages", currentLocale);
        }
        // --- KẾT THÚC TẢI ---

        String savedToken = configService.getAccessToken();
        String savedUserId = configService.getUserId();
        String savedServer = configService.getServerAddress();
        String savedApiKey = configService.getApiKey();

        if (savedToken != null && !savedToken.isEmpty() &&
                savedUserId != null && !savedUserId.isEmpty() &&
                savedServer != null && !savedServer.isEmpty() &&
                savedApiKey != null && !savedApiKey.isEmpty())
        {
            System.out.println("Tìm thấy session đã lưu. Đang thử xác thực...");
            validateSavedSession(savedServer, savedApiKey, savedToken, savedUserId);
        } else {
            System.out.println("Không tìm thấy session hợp lệ. Hiển thị màn hình Login.");
            showLoginView();
        }
    }

    // ... (validateSavedSession giữ nguyên)
    private void validateSavedSession(String serverUrl, String apiKey, String accessToken, String userId) {
        new Thread(() -> {
            boolean sessionValid = false;
            ApiClient tempApiClient = new ApiClient();
            try {
                // 1. Cấu hình client tạm thời
                tempApiClient.setBasePath(serverUrl);
                ApiKeyAuth apikeyauth = (ApiKeyAuth) tempApiClient.getAuthentication("apikeyauth");
                apikeyauth.setApiKey(apiKey);
                tempApiClient.setAccessToken(accessToken);

                // 2. Gọi API UserService
                UserServiceApi userServiceApi = new UserServiceApi(tempApiClient);
                System.out.println("Đang thử lấy thông tin User ID: " + userId + " để xác thực session...");
                embyclient.model.UserDto userDto = userServiceApi.getUsersById(userId);

                // 3. Nếu gọi API thành công -> Session hợp lệ
                if (userDto != null) {
                    System.out.println("Xác thực thành công cho user: " + userDto.getName());
                    sessionValid = true;
                } else {
                    System.err.println("Xác thực session thất bại: API thành công nhưng không lấy được UserDto.");
                    configService.clearSession();
                }

            } catch (embyclient.ApiException apiEx) {
                System.err.println("Xác thực session thất bại (API Error " + apiEx.getCode() + "): " + apiEx.getMessage());
                if (apiEx.getResponseBody() != null) {
                    System.err.println("Response Body: " + apiEx.getResponseBody());
                }
                configService.clearSession();
            } catch (Exception e) {
                System.err.println("Xác thực session thất bại (Lỗi khác): " + e.getMessage());
                e.printStackTrace();
                configService.clearSession();
            }

            // --- Cập nhật UI ---
            final boolean finalSessionValid = sessionValid;
            Platform.runLater(() -> {
                if (finalSessionValid) {
                    System.out.println("Session hợp lệ. Hiển thị màn hình chính.");
                    // Cấu hình ApiClient CHÍNH THỨC MẶC ĐỊNH
                    ApiClient mainApiClient = new ApiClient();
                    mainApiClient.setBasePath(serverUrl);
                    ApiKeyAuth mainApiKeyAuth = (ApiKeyAuth) mainApiClient.getAuthentication("apikeyauth");
                    mainApiKeyAuth.setApiKey(apiKey);
                    mainApiClient.setAccessToken(accessToken);
                    Configuration.setDefaultApiClient(mainApiClient);

                    openMainWindow(userId); // Mở thẳng màn hình chính
                } else {
                    System.out.println("Session không hợp lệ hoặc có lỗi. Hiển thị màn hình Login.");
                    showLoginView(); // Quay lại Login
                }
            });
        }).start();
    }


    private void showLoginView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
            fxmlLoader.setResources(this.bundle);

            double width = configService.getLoginWindowWidth();
            double height = configService.getLoginWindowHeight();
            Scene scene = new Scene(fxmlLoader.load(), width, height);

            LoginController loginController = fxmlLoader.getController();
            loginController.setApp(this);

            applyStylesAndShow(scene, "Emby Helper - Đăng nhập", primaryStage);

            primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> configService.saveLoginWindowSize(primaryStage.getWidth(), primaryStage.getHeight()));
            primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> configService.saveLoginWindowSize(primaryStage.getWidth(), primaryStage.getHeight()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openMainWindow(String userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
            fxmlLoader.setResources(this.bundle);

            double width = configService.getMainWindowWidth();
            double height = configService.getMainWindowHeight();
            Scene scene = new Scene(fxmlLoader.load(), width, height);

            MainController mainController = fxmlLoader.getController();
            mainController.setUserId(userId);
            mainController.setApp(this);

            applyStylesAndShow(scene, "Emby Helper Dashboard", primaryStage);

            primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> configService.saveMainWindowSize(primaryStage.getWidth(), primaryStage.getHeight()));
            primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> configService.saveMainWindowSize(primaryStage.getWidth(), primaryStage.getHeight()));
            primaryStage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyStylesAndShow(Scene scene, String title, Stage stage) {
        String css = HelloApplication.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public void handleLogout() {
        configService.clearSession();
        if (primaryStage != null) {
            primaryStage.close();
        }
        Platform.runLater(() -> {
            try {
                Stage loginStage = new Stage();
                start(loginStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void switchLanguage(String langCode) {
        configService.saveLanguage(langCode);

        if (primaryStage != null) {
            primaryStage.close();
        }

        Platform.runLater(() -> {
            try {
                Stage newStage = new Stage();
                start(newStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}