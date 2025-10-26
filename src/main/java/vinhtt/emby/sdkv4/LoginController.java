package vinhtt.emby.sdkv4;

import embyclient.ApiClient;
import embyclient.ApiException;
import embyclient.Configuration;
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

    @FXML
    public void initialize() {
        // TẢI thông tin đăng nhập đã lưu
        loadLoginInfo();
    }

    private void loadLoginInfo() {
        txtServerAddress.setText(configService.getServerAddress());
        txtApiKey.setText(configService.getApiKey());
        txtUsername.setText(configService.getUsername());
    }

    @FXML
    protected void onConnectButtonClick() {
        lblStatus.setText("Đang kết nối...");
        btnConnect.setDisable(true);

        // LƯU thông tin đăng nhập
        try {
            configService.saveLoginInfo(
                    txtServerAddress.getText().trim(),
                    txtApiKey.getText().trim(),
                    txtUsername.getText().trim()
            );
        } catch (Exception e) {
            System.out.println("Lỗi khi lưu cài đặt: " + e.getMessage());
        }

        // Chạy trên một luồng riêng để không làm treo GUI
        new Thread(() -> {
            try {
                ApiClient apiClient = new ApiClient();
                apiClient.setBasePath(txtServerAddress.getText().trim());

                embyclient.auth.ApiKeyAuth apikeyauth = (embyclient.auth.ApiKeyAuth) apiClient.getAuthentication("apikeyauth");
                apikeyauth.setApiKey(txtApiKey.getText().trim());

                // Sử dụng password từ text field
                AuthenUserService authenUserService = new AuthenUserService(apiClient, txtUsername.getText(), txtPassword.getText());

                if (authenUserService.login()) {
                    // Thành công!
                    Configuration.setDefaultApiClient(apiClient);
                    String loggedInUserId = authenUserService.getUserId();

                    Platform.runLater(() -> {
                        lblStatus.setText("Kết nối thành công!");
                        openMainWindow(loggedInUserId);
                        ((Stage) btnConnect.getScene().getWindow()).close();
                    });
                } else {
                    Platform.runLater(() -> {
                        lblStatus.setText("Lỗi: Đăng nhập thất bại. Kiểm tra Username/Password.");
                        btnConnect.setDisable(false);
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblStatus.setText("Lỗi: " + e.getMessage());
                    btnConnect.setDisable(false);
                });
            }
        }).start();
    }

    private void openMainWindow(String userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));

            // TẢI kích thước cửa sổ chính
            double width = configService.getMainWindowWidth();
            double height = configService.getMainWindowHeight();

            Scene scene = new Scene(fxmlLoader.load(), width, height);

            MainController mainController = fxmlLoader.getController();
            mainController.setUserId(userId);

            String css = HelloApplication.class.getResource("style.css").toExternalForm();
            scene.getStylesheets().add(css);

            Stage stage = new Stage();
            stage.setTitle("Emby Helper Dashboard");
            stage.setScene(scene);
            stage.show();

            // LƯU kích thước cửa sổ chính khi thay đổi
            stage.widthProperty().addListener((obs, oldVal, newVal) -> {
                configService.saveMainWindowSize(stage.getWidth(), stage.getHeight());
            });
            stage.heightProperty().addListener((obs, oldVal, newVal) -> {
                configService.saveMainWindowSize(stage.getWidth(), stage.getHeight());
            });
            stage.setOnCloseRequest(e -> {
                configService.saveMainWindowSize(stage.getWidth(), stage.getHeight());
            });

        } catch (IOException e) {
            e.printStackTrace();
            lblStatus.setText("Lỗi: Không thể mở màn hình chính.");
        }
    }
}