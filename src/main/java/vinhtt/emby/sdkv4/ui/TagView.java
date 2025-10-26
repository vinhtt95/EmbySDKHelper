package vinhtt.emby.sdkv4.ui; // Hoặc package bạn muốn

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;

public class TagView extends HBox {

    private final TagModel tagModel;
    private final Button deleteButton; // Giữ tham chiếu để có thể ẩn/hiện

    public TagView(TagModel tagModel) {
        this.tagModel = tagModel;

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5); // Giảm khoảng cách
        setPadding(new Insets(3, 5, 3, 8)); // Giảm padding
        getStyleClass().add("tag-view");
        if (tagModel.isJson()) {
            getStyleClass().add("tag-view-json");
        } else {
            getStyleClass().add("tag-view-simple");
        }

        if (tagModel.isJson()) {
            Label keyLabel = new Label(tagModel.getKey());
            keyLabel.getStyleClass().add("tag-label-key");

            Separator separator = new Separator(Orientation.VERTICAL);
            separator.getStyleClass().add("tag-separator");

            Label valueLabel = new Label(tagModel.getValue());
            valueLabel.getStyleClass().add("tag-label-value");

            getChildren().addAll(keyLabel, separator, valueLabel);
        } else {
            Label label = new Label(tagModel.getDisplayName());
            label.getStyleClass().add("tag-label");
            getChildren().add(label);
        }

        // Vẫn tạo nút xóa nhưng có thể ẩn đi
        deleteButton = new Button("✕");
        deleteButton.getStyleClass().add("tag-delete-button");
        // Không gán action ở đây, để controller bên ngoài xử lý nếu cần

        getChildren().add(deleteButton);
    }

    public TagModel getTagModel() {
        return tagModel;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }
}