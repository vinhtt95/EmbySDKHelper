module vinhtt.emby.sdkv4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires eemby.sdk.java;
    requires java.sql;


    opens vinhtt.emby.sdkv4 to javafx.fxml;
    exports vinhtt.emby.sdkv4;
}