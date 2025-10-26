package vinhtt.emby.sdkv4;

import embyclient.ApiClient;
import embyclient.ApiException;
import embyclient.Configuration;
import embyclient.auth.ApiKeyAuth; // Thêm import này
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtServerAddress;
    @FXML
    private TextField txtApiKey;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnConnect;
    @FXML
    private Label lblStatus;

    // Khởi tạo ConfigService
    private ConfigService configService = new ConfigService();
    // Thêm tham chiếu đến HelloApplication để gọi openMainWindow
    private HelloApplication app;

    // Thêm hàm setter này
    public void setApp(HelloApplication app) {
        this.app = app;
    }


    @FXML
    public void initialize() {
        // TẢI thông tin đăng nhập đã lưu khi khởi tạo controller
        loadLoginInfo();
    }

    private void loadLoginInfo() {
        txtServerAddress.setText(configService.getServerAddress());
        txtApiKey.setText(configService.getApiKey());
        txtUsername.setText(configService.getUsername());
        // Không tải lại password vì lý do bảo mật
    }

    @FXML
    protected void onConnectButtonClick() {
        lblStatus.setText("Đang kết nối...");
        btnConnect.setDisable(true);

        // LƯU thông tin đăng nhập (trừ password) vào config khi nhấn nút
        // (Lưu ngay cả trước khi thử kết nối)
        try {
            configService.saveLoginInfo(
                    txtServerAddress.getText().trim(),
                    txtApiKey.getText().trim(),
                    txtUsername.getText().trim()
            );
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu thông tin đăng nhập vào config: " + e.getMessage());
        }

        // Chạy trên một luồng riêng để không làm treo GUI
        new Thread(() -> {
            try {
                // Lấy thông tin từ các trường nhập liệu
                String serverUrl = txtServerAddress.getText().trim();
                String apiKey = txtApiKey.getText().trim();
                String username = txtUsername.getText().trim();
                String password = txtPassword.getText(); // Lấy password trực tiếp

                // 1. Tạo ApiClient và cấu hình cơ bản
                ApiClient apiClient = new ApiClient();
                apiClient.setBasePath(serverUrl);

                // 2. Cấu hình ApiKeyAuth
                ApiKeyAuth apikeyauth = (ApiKeyAuth) apiClient.getAuthentication("apikeyauth");
                apikeyauth.setApiKey(apiKey);

                // 3. Gọi AuthenUserService để đăng nhập
                AuthenUserService authenUserService = new AuthenUserService(apiClient, username, password);

                if (authenUserService.login()) {
                    // --- Đăng nhập Thành Công ---
                    // 4. Lấy thông tin session
                    String loggedInUserId = authenUserService.getUserId();
                    String accessToken = authenUserService.getAuthenticateUser().getAccessToken();

                    // 5. LƯU SESSION (Token và UserID) vào ConfigService
                    configService.saveSession(accessToken, loggedInUserId);
                    System.out.println("Đã lưu session (Token: ..." + (accessToken != null ? accessToken.substring(accessToken.length() - 5) : "null") + ", UserID: " + loggedInUserId + ")");


                    // 6. Cấu hình ApiClient MẶC ĐỊNH cho toàn ứng dụng
                    //    Quan trọng: Phải tạo client mới hoặc cấu hình lại client dùng chung
                    //    Ở đây ta cấu hình client mặc định
                    Configuration.setDefaultApiClient(apiClient);
                    // Đặt Access Token vào client mặc định này để các API call sau dùng được
                    Configuration.getDefaultApiClient().setAccessToken(accessToken);

                    // 7. Mở cửa sổ chính trên luồng JavaFX
                    Platform.runLater(() -> {
                        lblStatus.setText("Kết nối thành công!");
                        // Gọi hàm openMainWindow của HelloApplication (đã tách ra)
                        if (app != null) {
                            app.openMainWindow(loggedInUserId);
                            // Đóng cửa sổ login hiện tại
                            ((Stage) btnConnect.getScene().getWindow()).close();
                        } else {
                            // Xử lý trường hợp app bị null (không mong muốn)
                            lblStatus.setText("Lỗi: Không thể mở cửa sổ chính (app is null).");
                            configService.clearSession(); // Xóa session nếu không mở được main window
                            btnConnect.setDisable(false);
                        }
                    });
                } else {
                    // --- Đăng nhập Thất Bại ---
                    // XÓA SESSION đã lưu (nếu có)
                    configService.clearSession();
                    Platform.runLater(() -> {
                        lblStatus.setText("Lỗi: Đăng nhập thất bại. Kiểm tra Username/Password/API Key.");
                        btnConnect.setDisable(false);
                    });
                }
            } catch (Exception e) {
                // --- Lỗi khác (vd: mạng, cấu hình sai...) ---
                configService.clearSession(); // Xóa session khi có lỗi khác
                System.err.println("Lỗi không xác định khi đăng nhập: " + e.getMessage());
                e.printStackTrace();
                Platform.runLater(() -> {
                    lblStatus.setText("Lỗi: " + e.getMessage());
                    btnConnect.setDisable(false);
                });
            }
        }).start();
    }

    /*
     * Hàm openMainWindow đã được chuyển sang HelloApplication.java
     * để xử lý cả trường hợp mở trực tiếp từ session đã lưu.
     */
    // private void openMainWindow(String userId) { ... }
}