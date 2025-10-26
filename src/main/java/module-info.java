module vinhtt.emby.sdkv4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens vinhtt.emby.sdkv4 to javafx.fxml;
    exports vinhtt.emby.sdkv4;
}