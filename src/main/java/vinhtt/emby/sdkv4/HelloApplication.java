package vinhtt.emby.sdkv4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    // Khởi tạo ConfigService
    private ConfigService configService = new ConfigService();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));

        // Lấy kích thước đã lưu
        double width = configService.getLoginWindowWidth();
        double height = configService.getLoginWindowHeight();

        Scene scene = new Scene(fxmlLoader.load(), width, height);

        // Áp dụng file CSS
        String css = HelloApplication.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Emby Helper - Đăng nhập");
        stage.setScene(scene);
        stage.show();

        // Thêm listener để LƯU kích thước cửa sổ khi thay đổi
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            configService.saveLoginWindowSize(stage.getWidth(), stage.getHeight());
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            configService.saveLoginWindowSize(stage.getWidth(), stage.getHeight());
        });
        stage.setOnCloseRequest(e -> {
            configService.saveLoginWindowSize(stage.getWidth(), stage.getHeight());
        });
    }
}