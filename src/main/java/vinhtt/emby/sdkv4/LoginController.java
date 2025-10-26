package vinhtt.emby.sdkv4;

import embyclient.ApiClient;
import embyclient.Configuration; // <-- DÒNG BỊ THIẾU ĐÃ ĐƯỢC THÊM VÀO
import embyclient.auth.ApiKeyAuth;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class LoginController {

    @FXML private TextField txtServerAddress;
    @FXML private TextField txtApiKey;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnConnect;
    @FXML private Label lblStatus;

    @FXML
    private ResourceBundle resources;

    private ConfigService configService = new ConfigService();
    private HelloApplication app;

    public void setApp(HelloApplication app) {
        this.app = app;
    }

    @FXML
    public void initialize() {
        loadLoginInfo();
    }

    private void loadLoginInfo() {
        txtServerAddress.setText(configService.getServerAddress());
        txtApiKey.setText(configService.getApiKey());
        txtUsername.setText(configService.getUsername());
    }

    @FXML
    protected void onConnectButtonClick() {
        lblStatus.setText(resources.getString("login.status.connecting"));
        btnConnect.setDisable(true);

        try {
            configService.saveLoginInfo(
                    txtServerAddress.getText().trim(),
                    txtApiKey.getText().trim(),
                    txtUsername.getText().trim()
            );
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu thông tin đăng nhập vào config: " + e.getMessage());
        }

        new Thread(() -> {
            try {
                String serverUrl = txtServerAddress.getText().trim();
                String apiKey = txtApiKey.getText().trim();
                String username = txtUsername.getText().trim();
                String password = txtPassword.getText();

                ApiClient apiClient = new ApiClient();
                apiClient.setBasePath(serverUrl);

                ApiKeyAuth apikeyauth = (ApiKeyAuth) apiClient.getAuthentication("apikeyauth");
                apikeyauth.setApiKey(apiKey);

                AuthenUserService authenUserService = new AuthenUserService(apiClient, username, password);

                if (authenUserService.login()) {
                    // --- Đăng nhập Thành Công ---
                    String loggedInUserId = authenUserService.getUserId();
                    String accessToken = authenUserService.getAuthenticateUser().getAccessToken();

                    configService.saveSession(accessToken, loggedInUserId);
                    System.out.println("Đã lưu session (Token: ..." + (accessToken != null ? accessToken.substring(accessToken.length() - 5) : "null") + ", UserID: " + loggedInUserId + ")");

                    // ĐÂY LÀ 2 DÒNG GÂY LỖI - ĐÃ SỬA
                    Configuration.setDefaultApiClient(apiClient);
                    Configuration.getDefaultApiClient().setAccessToken(accessToken);

                    Platform.runLater(() -> {
                        lblStatus.setText(resources.getString("login.status.success"));

                        if (app != null) {
                            app.openMainWindow(loggedInUserId);
                            ((Stage) btnConnect.getScene().getWindow()).close();
                        } else {
                            lblStatus.setText("Lỗi: Không thể mở cửa sổ chính (app is null).");
                            configService.clearSession();
                            btnConnect.setDisable(false);
                        }
                    });
                } else {
                    // --- Đăng nhập Thất Bại ---
                    configService.clearSession();
                    Platform.runLater(() -> {
                        lblStatus.setText(resources.getString("login.status.failure"));
                        btnConnect.setDisable(false);
                    });
                }
            } catch (Exception e) {
                // --- Lỗi khác ---
                configService.clearSession();
                System.err.println("Lỗi không xác định khi đăng nhập: " + e.getMessage());
                e.printStackTrace();
                Platform.runLater(() -> {
                    lblStatus.setText(resources.getString("login.status.failure") + ": " + e.getMessage());
                    btnConnect.setDisable(false);
                });
            }
        }).start();
    }
}