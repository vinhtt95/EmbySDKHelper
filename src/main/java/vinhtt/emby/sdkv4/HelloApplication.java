package vinhtt.emby.sdkv4;

import embyclient.ApiClient;
import embyclient.Configuration;
import embyclient.api.SystemServiceApi;
import embyclient.api.UserServiceApi;
import embyclient.auth.ApiKeyAuth;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private ConfigService configService = new ConfigService();
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

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

    // *** THAY THẾ HÀM NÀY ***
    private void validateSavedSession(String serverUrl, String apiKey, String accessToken, String userId) {
        new Thread(() -> {
            boolean sessionValid = false;
            ApiClient tempApiClient = new ApiClient(); // Tạo client tạm thời chỉ để kiểm tra
            try {
                // 1. Cấu hình client tạm thời
                tempApiClient.setBasePath(serverUrl);
                ApiKeyAuth apikeyauth = (ApiKeyAuth) tempApiClient.getAuthentication("apikeyauth");
                apikeyauth.setApiKey(apiKey);
                // *** QUAN TRỌNG: Đặt Access Token trực tiếp vào client TẠM THỜI này ***
                tempApiClient.setAccessToken(accessToken);

                // 2. Gọi API UserService để lấy thông tin user bằng ID đã lưu
                //    Sử dụng client TẠM THỜI đã cấu hình token
                UserServiceApi userServiceApi = new UserServiceApi(tempApiClient);
                System.out.println("Đang thử lấy thông tin User ID: " + userId + " để xác thực session...");
                embyclient.model.UserDto userDto = userServiceApi.getUsersById(userId);

                // 3. Nếu gọi API thành công và lấy được thông tin user -> Session hợp lệ
                if (userDto != null) {
                    System.out.println("Xác thực thành công cho user: " + userDto.getName());
                    sessionValid = true;
                } else {
                    // Trường hợp hiếm: API không lỗi nhưng không trả về user Dto
                    System.err.println("Xác thực session thất bại: API thành công nhưng không lấy được UserDto.");
                    configService.clearSession(); // Xóa session không hợp lệ
                }

            } catch (embyclient.ApiException apiEx) {
                // Lỗi API (401, 403, 404, 0...) -> Session không hợp lệ
                System.err.println("Xác thực session thất bại (API Error " + apiEx.getCode() + "): " + apiEx.getMessage());
                // In thêm body nếu có để debug
                if (apiEx.getResponseBody() != null) {
                    System.err.println("Response Body: " + apiEx.getResponseBody());
                }
                configService.clearSession(); // Xóa session không hợp lệ
            } catch (Exception e) {
                // Lỗi khác (mạng, etc.) -> Session không hợp lệ
                System.err.println("Xác thực session thất bại (Lỗi khác): " + e.getMessage());
                e.printStackTrace(); // In stack trace để debug
                configService.clearSession(); // Xóa session không hợp lệ
            }

            // --- Cập nhật UI ---
            final boolean finalSessionValid = sessionValid;
            Platform.runLater(() -> {
                if (finalSessionValid) {
                    System.out.println("Session hợp lệ. Hiển thị màn hình chính.");
                    // Cấu hình ApiClient CHÍNH THỨC MẶC ĐỊNH cho toàn ứng dụng
                    // (Lưu ý: Không dùng lại tempApiClient)
                    ApiClient mainApiClient = new ApiClient();
                    mainApiClient.setBasePath(serverUrl);
                    ApiKeyAuth mainApiKeyAuth = (ApiKeyAuth) mainApiClient.getAuthentication("apikeyauth");
                    mainApiKeyAuth.setApiKey(apiKey);
                    mainApiClient.setAccessToken(accessToken); // Đặt token cho client chính
                    Configuration.setDefaultApiClient(mainApiClient); // Đặt làm client mặc định

                    openMainWindow(userId); // Mở thẳng màn hình chính
                } else {
                    System.out.println("Session không hợp lệ hoặc có lỗi. Hiển thị màn hình Login.");
                    showLoginView(); // Nếu session không hợp lệ, quay lại Login
                }
            });
        }).start();
    }


    private void showLoginView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
            double width = configService.getLoginWindowWidth();
            double height = configService.getLoginWindowHeight();
            Scene scene = new Scene(fxmlLoader.load(), width, height);

            // *** TRUYỀN HelloApplication VÀO LoginController ***
            LoginController loginController = fxmlLoader.getController();
            loginController.setApp(this); // Thêm dòng này

            applyStylesAndShow(scene, "Emby Helper - Đăng nhập", primaryStage);

            primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> configService.saveLoginWindowSize(primaryStage.getWidth(), primaryStage.getHeight()));
            primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> configService.saveLoginWindowSize(primaryStage.getWidth(), primaryStage.getHeight()));
            primaryStage.setOnCloseRequest(e -> configService.saveLoginWindowSize(primaryStage.getWidth(), primaryStage.getHeight()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // *** THAY ĐỔI private thành public Ở ĐÂY ***
    public void openMainWindow(String userId) { // <-- Sửa thành public
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
            double width = configService.getMainWindowWidth();
            double height = configService.getMainWindowHeight();
            Scene scene = new Scene(fxmlLoader.load(), width, height);

            MainController mainController = fxmlLoader.getController();
            mainController.setUserId(userId);
            mainController.setApp(this); // Truyền app cho logout

            applyStylesAndShow(scene, "Emby Helper Dashboard", primaryStage);

            primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> configService.saveMainWindowSize(primaryStage.getWidth(), primaryStage.getHeight()));
            primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> configService.saveMainWindowSize(primaryStage.getWidth(), primaryStage.getHeight()));
            primaryStage.setOnCloseRequest(e -> {
                configService.saveMainWindowSize(primaryStage.getWidth(), primaryStage.getHeight());
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

}