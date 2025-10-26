module vinhtt.emby.sdkv4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires eemby.sdk.java;
    requires java.sql;
    requires java.prefs;
    requires com.google.gson; // <--- THÊM DÒNG NÀY VÀO ĐÂY


    opens vinhtt.emby.sdkv4 to javafx.fxml;
    exports vinhtt.emby.sdkv4;
}