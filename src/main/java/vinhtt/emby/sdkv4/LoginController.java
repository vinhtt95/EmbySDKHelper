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

    @FXML
    protected void onConnectButtonClick() {
        lblStatus.setText("Đang kết nối...");
        btnConnect.setDisable(true);

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
                    // LẤY USER ID SAU KHI LOGIN
                    String loggedInUserId = authenUserService.getUserId();

                    Platform.runLater(() -> {
                        lblStatus.setText("Kết nối thành công!");
                        // TRUYỀN USER ID QUA HÀM NÀY
                        openMainWindow(loggedInUserId);
                        // Đóng cửa sổ login
                        ((Stage) btnConnect.getScene().getWindow()).close();
                    });
                } else {
                    // Thất bại (login() trả về false)
                    Platform.runLater(() -> {
                        lblStatus.setText("Lỗi: Đăng nhập thất bại. Kiểm tra Username/Password.");
                        btnConnect.setDisable(false);
                    });
                }
            } catch (Exception e) {
                // Thất bại (Exception)
                Platform.runLater(() -> {
                    lblStatus.setText("Lỗi: " + e.getMessage());
                    btnConnect.setDisable(false);
                });
            }
        }).start();
    }

    // SỬA HÀM NÀY ĐỂ NHẬN USER ID
    private void openMainWindow(String userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));

            // THAY ĐỔI CHIỀU CAO VÀ RỘNG Ở ĐÂY
            Scene scene = new Scene(fxmlLoader.load(), 900, 750);

            // *** ĐOẠN QUAN TRỌNG ***
            // LẤY MAIN CONTROLLER SAU KHI LOAD FXML
            MainController mainController = fxmlLoader.getController();
            // GỌI HÀM setUserId TRÊN MAIN CONTROLLER ĐỂ TRUYỀN ID QUA
            mainController.setUserId(userId);

            // Áp dụng CSS
            String css = HelloApplication.class.getResource("style.css").toExternalForm();
            scene.getStylesheets().add(css);

            Stage stage = new Stage();
            stage.setTitle("Emby Helper Dashboard");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            lblStatus.setText("Lỗi: Không thể mở màn hình chính.");
        }
    }
}