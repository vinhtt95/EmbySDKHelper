package vinhtt.emby.sdkv4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import embyclient.model.BaseItemDto;
import embyclient.model.UserLibraryTagItem;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import vinhtt.emby.sdkv4.service.*;
import vinhtt.emby.sdkv4.ui.TagModel;
import vinhtt.emby.sdkv4.ui.TagView;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FilenameFilter;
// THÊM CÁC IMPORT MỚI ĐỂ QUÉT ĐỆ QUY
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList; // Cần cho khởi tạo List
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors; // Cần cho stream
import java.util.stream.Stream; // Cần cho stream
import embyclient.JSON.OffsetDateTimeTypeAdapter;

public class MainController {

    // (Enum MetadataType đã được cập nhật để dùng i18n keys)
    private enum MetadataType {
        STUDIO("metadata.studios", "metadata.studio.singular"),
        GENRE("metadata.genres", "metadata.genre.singular"),
        PEOPLE("metadata.people", "metadata.people.singular"),
        TAG("metadata.tags", "metadata.tag.singular");

        final String pluralKey;
        final String singularKey;
        MetadataType(String pluralKey, String singularKey) {
            this.pluralKey = pluralKey;
            this.singularKey = singularKey;
        }
    }

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeTypeAdapter())
            .setPrettyPrinting()
            .create();

    // --- Tham chiếu HelloApplication ---
    private HelloApplication app;

    // --- MenuBar ---
    @FXML private MenuBar menuBar;
    @FXML private MenuItem logoutMenuItem;
    @FXML private MenuItem langMenuItemVI;
    @FXML private MenuItem langMenuItemEN;

    // --- Status Bar ---
    @FXML private HBox statusBar;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator statusIndicator;

    // --- Vùng Chọn Loại (Tab 1: Metadata Tools) ---
    @FXML private ComboBox<MetadataType> typeComboBox;
    @FXML private TitledPane copyPane;
    @FXML private TextField txtCopyFromItemID;
    @FXML private TextField txtCopyToParentID;
    @FXML private Button btnRunCopy;
    @FXML private TitledPane clearParentPane;
    @FXML private Label clearParentLabel;
    @FXML private TextField txtClearByParentID;
    @FXML private Button btnRunClearParent;
    @FXML private TitledPane clearSpecificPane;
    @FXML private Label clearSpecificLabel;
    @FXML private TextField searchField;
    @FXML private Button btnReloadList;
    @FXML private ScrollPane selectionScrollPane;
    @FXML private FlowPane selectionFlowPane;
    @FXML private Button btnRunClearSpecific;
    @FXML private Button btnRunUpdateSpecific;
    private final ToggleGroup selectionToggleGroup = new ToggleGroup();

    // --- Vùng Công cụ Hàng loạt (Tab 2: Batch Tools) ---
    @FXML private TitledPane exportJsonPane;
    @FXML private TextField txtExportJsonParentID;
    @FXML private Button btnRunExportJson;

    @FXML private TitledPane batchProcessPane;
    @FXML private TextField txtBatchProcessParentID;
    @FXML private Button btnRunBatchProcess;

    // --- (THÊM MỚI) Vùng Công cụ Nhập (Tab 2) ---
    @FXML private TitledPane importJsonPane;
    @FXML private TextField txtImportJsonParentID;
    @FXML private Button btnRunImportJson;


    // --- ResourceBundle ---
    @FXML
    private ResourceBundle resources;

    // --- Services ---
    private ItemService itemService;
    private StudioService studioService;
    private GenresService genresService;
    private PeopleService peopleService;
    private TagService tagService;

    // (State và Lists không đổi)
    private String userId;
    private MetadataType currentType = MetadataType.STUDIO;
    private final ObservableList<Object> originalItemList = FXCollections.observableArrayList();
    private FilteredList<Object> filteredItemList;

    // --- Setters ---
    public void setApp(HelloApplication app) { this.app = app; }

    /**
     * Khởi tạo các Service và kích hoạt tải dữ liệu lần đầu sau khi login.
     * Đây là nơi khắc phục lỗi race condition "loadCurrentTypeList bị gọi trước khi service sẵn sàng".
     */
    public void setUserId(String userId) {
        this.userId = userId;
        // 1. KHỞI TẠO SERVICES
        this.itemService = new ItemService(this.userId);
        this.studioService = new StudioService(this.itemService);
        this.genresService = new GenresService(this.itemService);
        this.peopleService = new PeopleService(this.itemService);
        this.tagService = new TagService(this.itemService);

        updateStatus(String.format(resources.getString("status.servicesInitialized"), userId));

        // 2. KÍCH HOẠT TẢI DANH SÁCH SAU KHI SERVICES SẴN SÀNG
        Platform.runLater(() -> {
            if (typeComboBox != null) {
                // Việc set value này sẽ kích hoạt listener và gọi loadCurrentTypeList
                typeComboBox.setValue(currentType);
            } else {
                // Fallback nếu ComboBox bị ẩn hoặc chưa được tải (nên không bao giờ xảy ra trong FXML này)
                this.loadCurrentTypeList();
            }
        });
    }

    // --- Initialize ---
    @FXML
    public void initialize() {
        // (Khởi tạo ComboBox)
        if(typeComboBox != null) {
            typeComboBox.getItems().setAll(MetadataType.values());

            // Dùng StringConverter để dịch Enum
            typeComboBox.setConverter(new StringConverter<MetadataType>() {
                @Override
                public String toString(MetadataType type) {
                    if (type == null || resources == null) {
                        return "";
                    }
                    return resources.getString(type.pluralKey);
                }
                @Override
                public MetadataType fromString(String string) {
                    return null;
                }
            });

            typeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
                if (newType != null) {
                    currentType = newType;
                    updateUITexts(); // Cập nhật text động
                    clearSelectionAndSearch();
                    if(txtCopyFromItemID != null) txtCopyFromItemID.clear();
                    if(txtCopyToParentID != null) txtCopyToParentID.clear();
                    loadCurrentTypeList(); // Gọi cho các lần thay đổi tiếp theo
                }
            });
            // BỎ DÒNG typeComboBox.setValue(currentType) TẠI ĐÂY để tránh lỗi service not ready.
        }

        filteredItemList = new FilteredList<>(originalItemList, p -> true);

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));
            searchField.setOnAction(e -> applyFilter(searchField.getText()));
        }

        populateFlowPaneFromFilteredList();

        // Gán action cho Menu Ngôn ngữ và Logout
        if (logoutMenuItem != null) logoutMenuItem.setOnAction(e -> handleLogout());
        if (langMenuItemVI != null) langMenuItemVI.setOnAction(e -> switchLanguage("vi"));
        if (langMenuItemEN != null) langMenuItemEN.setOnAction(e -> switchLanguage("en"));

        updateUITexts(); // Vẫn gọi để set text động ban đầu
    }

    // --- Helper Methods ---

    private void updateStatus(String message) { updateStatus(message, false); }
    private void updateStatus(String message, boolean isLoading) {
        Platform.runLater(() -> {
            if (statusLabel != null) statusLabel.setText(message);
            if (statusIndicator != null) statusIndicator.setVisible(isLoading);
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            if (statusBar != null && statusBar.getScene() != null) {
                alert.initOwner(statusBar.getScene().getWindow());
                // THÊM: Áp dụng CSS dark mode cho Alert
                if (!statusBar.getScene().getStylesheets().isEmpty()) {
                    String css = statusBar.getScene().getStylesheets().get(0);
                    alert.getDialogPane().getStylesheets().add(css);
                }
            }
            alert.showAndWait();
        });
    }

    /**
     * Cập nhật các Label và Button có text phụ thuộc vào loại Metadata đang chọn.
     * Đã sửa lỗi MissingFormatArgumentException.
     */
    private void updateUITexts() {
        if (resources == null || currentType == null) return;

        // SỬA: Thêm btnRunUpdateSpecific
        if(clearParentLabel == null || btnRunClearParent == null || clearSpecificLabel == null || btnRunClearSpecific == null || btnRunUpdateSpecific == null) {
            return;
        }

        String singularName = resources.getString(currentType.singularKey).toLowerCase();
        String pluralName = resources.getString(currentType.pluralKey);

        clearParentLabel.setText(String.format(resources.getString("label.clearParentDynamic"), singularName));
        btnRunClearParent.setText(String.format(resources.getString("button.clearParentDynamic"), pluralName));
        // FIX LỖI: Cung cấp đủ 2 tham số (%s) cho chuỗi định dạng "label.clearSpecificDynamic"
        clearSpecificLabel.setText(String.format(resources.getString("label.clearSpecificDynamic"), singularName, singularName));
        btnRunClearSpecific.setText(String.format(resources.getString("button.clearSpecificDynamic"), singularName));
        // THÊM: Set text cho nút Update
        btnRunUpdateSpecific.setText(String.format(resources.getString("button.updateSpecificDynamic"), singularName));
    }

    private void clearSelectionAndSearch() {
        selectionToggleGroup.selectToggle(null);
        if(searchField != null) searchField.clear();
    }

    private void loadCurrentTypeList() {
        // KIỂM TRA SERVICES TRƯỚC KHI CHẠY để tránh lỗi "loadCurrentTypeList bị gọi trước khi service sẵn sàng"
        if (itemService == null) {
            System.err.println("loadCurrentTypeList bị gọi trước khi service sẵn sàng. Bỏ qua lần gọi này.");
            return;
        }
        String taskName = String.format(resources.getString("task.loading"), resources.getString(currentType.pluralKey));
        runTask(taskName, () -> {
            updateStatus(taskName + "...", true);
            List<?> rawList = Collections.emptyList();
            try {
                switch (currentType) {
                    case STUDIO: rawList = studioService.getListStudios(); break;
                    case GENRE:  rawList = genresService.getListGenres(); break;
                    case PEOPLE: rawList = peopleService.getListPeople(); break;
                    case TAG:    rawList = tagService.getListTags(); break;
                }
            } catch (Exception e) {
                System.err.println("Lỗi API khi tải danh sách " + currentType.pluralKey);
                rawList = Collections.emptyList();
            }
            final List<?> finalList = rawList != null ? rawList : Collections.emptyList();
            Platform.runLater(() -> {
                originalItemList.setAll(finalList);
                populateFlowPaneFromFilteredList();
                updateStatus(String.format(resources.getString("status.loaded"), resources.getString(currentType.pluralKey), originalItemList.size()), false);
            });
        });
    }

    private void applyFilter(String filterText) {
        String lowerCaseFilter = filterText == null ? "" : filterText.toLowerCase().trim();
        filteredItemList.setPredicate(item -> {
            if (lowerCaseFilter.isEmpty()) return true;
            String itemName = getItemName(item);
            return itemName != null && itemName.toLowerCase().contains(lowerCaseFilter);
        });
        populateFlowPaneFromFilteredList();
    }
    private String getItemName(Object item) {
        if (item instanceof BaseItemDto) return ((BaseItemDto) item).getName();
        if (item instanceof UserLibraryTagItem) return ((UserLibraryTagItem) item).getName();
        return null;
    }

    /**
     * Lấy dữ liệu định danh cho item
     * - STUDIO, PEOPLE: Trả về ID
     * - GENRE, TAG: Trả về Tên (đã serialize nếu là JSON)
     */
    private String getUserData(Object item) {
        switch (currentType) {
            case STUDIO:
            case PEOPLE:
                if (item instanceof BaseItemDto) return ((BaseItemDto) item).getId();
                break;
            case GENRE:
            case TAG:
                return getItemName(item); // Tên chính là định danh
        }
        return null;
    }

    private void populateFlowPaneFromFilteredList() {
        if (selectionFlowPane == null) return;

        selectionFlowPane.getChildren().clear();
        selectionToggleGroup.getToggles().clear();
        for (Object item : filteredItemList) {
            String rawName = getItemName(item);
            if (rawName == null || rawName.isEmpty()) continue;
            TagModel tagModel = TagModel.parse(rawName);
            String userData = getUserData(item);
            if (userData != null) {
                ToggleButton chipButton = new ToggleButton();
                chipButton.setUserData(userData); // UserData chứa ID (Studio/People) hoặc Name (Genre/Tag)
                chipButton.setToggleGroup(selectionToggleGroup);

                TagView tagView = new TagView(tagModel);
                // SỬA: Không ẩn nút xóa/sửa nữa, vì chúng không có trên chip
                tagView.getDeleteButton().setVisible(false);
                tagView.getDeleteButton().setManaged(false);

                chipButton.setGraphic(tagView);
                chipButton.getStyleClass().add("chip-toggle-button");
                if (tagModel.isJson()) chipButton.getStyleClass().add("tag-view-json");
                else chipButton.getStyleClass().add("tag-view-simple");
                selectionFlowPane.getChildren().add(chipButton);
            }
        }
    }
    private String getDisplayNameFromToggle(Toggle toggle) {
        if (toggle instanceof ToggleButton) {
            ToggleButton button = (ToggleButton) toggle;
            if (button.getGraphic() instanceof TagView) {
                TagView tagView = (TagView) button.getGraphic();
                return tagView.getTagModel().getDisplayName();
            }
            return button.getText();
        }
        return "Không xác định";
    }
    private void runTask(String taskName, Runnable task) {
        if (studioService == null || genresService == null || peopleService == null || tagService == null || itemService == null) {
            updateStatus(resources.getString("error.service.notReady"), false);
            showAlert(Alert.AlertType.ERROR, resources.getString("error.service.title"), resources.getString("error.service.notReady"));
            return;
        }
        updateStatus(String.format(resources.getString("status.executing"), taskName) + "...", true);
        Thread thread = new Thread(() -> {
            try {
                task.run();
            } catch (Exception e) {
                String errorMsg = String.format(resources.getString("error.task.failed"), taskName, e.getMessage());
                System.err.println("\n!!! ĐÃ XẢY RA LỖI !!!");
                e.printStackTrace();
                updateStatus(errorMsg, false);
                showAlert(Alert.AlertType.ERROR, resources.getString("error.task.title"), errorMsg);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // --- Event Handlers ---

    @FXML private void handleLogout() {
        if (app != null) app.handleLogout();
        else {
            updateStatus(resources.getString("error.logout.appNull"), false);
            showAlert(Alert.AlertType.ERROR, resources.getString("error.logout.title"), resources.getString("error.logout.appNull"));
        }
    }

    private void switchLanguage(String langCode) {
        if (app != null) {
            app.switchLanguage(langCode);
        }
    }

    @FXML private void btnReloadListClick() {
        searchField.clear();
        loadCurrentTypeList();
    }

    // --- Handlers cho Copy (Tab 1) ---
    @FXML private void btnRunCopyClick() {
        String copyFromId = txtCopyFromItemID.getText();
        String copyToId = txtCopyToParentID.getText();

        if (copyFromId == null || copyFromId.trim().isEmpty() || copyToId == null || copyToId.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    resources.getString("alert.missingInfo.title"),
                    resources.getString("alert.missingInfo.contentCopy"));
            return;
        }
        final String finalCopyFromId = copyFromId.trim();
        final String finalCopyToId = copyToId.trim();
        String taskName = String.format(resources.getString("task.copy"), resources.getString(currentType.pluralKey));
        runTask(taskName, () -> {
            boolean success = false;
            try {
                switch (currentType) {
                    case STUDIO: studioService.copyStudio(finalCopyFromId, finalCopyToId); success = true; break;
                    case GENRE:  genresService.copyGenres(finalCopyFromId, finalCopyToId); success = true; break;
                    case PEOPLE: peopleService.copyPeople(finalCopyFromId, finalCopyToId); success = true; break;
                    case TAG:    tagService.copyTags(finalCopyFromId, finalCopyToId); success = true; break;
                }
                // BỎ ALERT - CHỈ UPDATE STATUS
                if(success) updateStatus(String.format(resources.getString("status.task.success"), taskName), false);
                else updateStatus(String.format(resources.getString("error.task.unknown"), taskName), false);
            } catch (Exception e) {
                updateStatus(String.format(resources.getString("error.task.failed"), taskName, e.getMessage()), false);
            }
        });
    }


    // --- Handlers cho Xóa Theo Thư Mục (Tab 1) ---
    @FXML private void btnRunClearParentClick() {
        String parentId = txtClearByParentID.getText();
        if (parentId == null || parentId.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    resources.getString("alert.missingInfo.title"),
                    resources.getString("alert.missingInfo.contentClearParent"));
            return;
        }
        final String finalParentId = parentId.trim();
        String taskName = String.format(resources.getString("task.clearParent"), resources.getString(currentType.pluralKey));
        runTask(taskName, () -> {
            boolean success = false;
            try {
                switch (currentType) {
                    case STUDIO: studioService.clearStudioByParentID(finalParentId); success = true; break;
                    case GENRE:  genresService.clearGenresByParentID(finalParentId); success = true; break;
                    case PEOPLE: peopleService.clearPeopleByParentID(finalParentId); success = true; break;
                    case TAG:    tagService.clearTagsByParentID(finalParentId); success = true; break;
                }
                // BỎ ALERT - CHỈ UPDATE STATUS
                if(success) updateStatus(String.format(resources.getString("status.task.success"), taskName), false);
                else updateStatus(String.format(resources.getString("error.task.unknown"), taskName), false);
            } catch (Exception e) {
                updateStatus(String.format(resources.getString("error.task.failed"), taskName, e.getMessage()), false);
            }
        });
    }

    // --- Handlers cho Xóa Cụ Thể (Tab 1) ---
    @FXML private void btnRunClearSpecificClick() {
        Toggle selectedToggle = selectionToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            showAlert(Alert.AlertType.WARNING,
                    resources.getString("alert.notSelected.title"),
                    resources.getString("alert.notSelected.content"));
            return;
        }
        String idOrNameToClear = (String) selectedToggle.getUserData();
        String nameSelected = getDisplayNameFromToggle(selectedToggle);
        String taskName = String.format(resources.getString("task.clearSpecific"), resources.getString(currentType.singularKey), nameSelected);
        runTask(taskName, () -> {
            boolean success = false;
            try {
                switch (currentType) {
                    case STUDIO: studioService.clearStudio(idOrNameToClear); success = true; break;
                    case GENRE:  genresService.clearGenres(idOrNameToClear); success = true; break;
                    case PEOPLE: peopleService.clearPeople(idOrNameToClear); success = true; break;
                    case TAG:    tagService.clearTags(idOrNameToClear); success = true; break;
                }
                if(success) {
                    // BỎ ALERT - CHỈ UPDATE STATUS
                    updateStatus(String.format(resources.getString("status.task.success"), taskName), false);
                    Platform.runLater(this::loadCurrentTypeList);
                } else {
                    updateStatus(String.format(resources.getString("error.task.unknown"), taskName), false);
                }
            } catch (Exception e) {
                updateStatus(String.format(resources.getString("error.task.failed"), taskName, e.getMessage()), false);
            }
        });
    }

    // --- (SỬA) Handlers cho SỬA Cụ Thể (Tab 1) ---
    @FXML
    private void btnRunUpdateSpecificClick() {
        Toggle selectedToggle = selectionToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            showAlert(Alert.AlertType.WARNING,
                    resources.getString("alert.notSelected.title"),
                    resources.getString("alert.notSelected.content"));
            return;
        }

        // 1. LẤY DỮ LIỆU CŨ
        // idOrName: Chứa ID (cho Studio/People) hoặc Tên (cho Genre/Tag)
        String idOrName = (String) selectedToggle.getUserData();
        TagModel oldTagModel = null;
        if (selectedToggle instanceof ToggleButton && ((ToggleButton) selectedToggle).getGraphic() instanceof TagView) {
            oldTagModel = ((TagView) ((ToggleButton) selectedToggle).getGraphic()).getTagModel();
        }

        if (oldTagModel == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy thông tin thẻ (TagModel) cũ.");
            return;
        }

        // 2. HIỂN THỊ DIALOG
        Optional<TagModel> result = showEditTagDialog(oldTagModel);

        if (result.isPresent()) {
            TagModel newTagModel = result.get();
            // newName: Chứa tên đã serialize (cho Genre/Tag) hoặc tên đơn giản (cho Studio/People)
            String newName = newTagModel.serialize();
            String oldDisplayName = oldTagModel.getDisplayName();
            String newDisplayName = newTagModel.getDisplayName();

            if (oldTagModel.serialize().equals(newName)) {
                updateStatus("Không có thay đổi.", false);
                return;
            }

            // 4. Chạy Task
            String taskName = String.format(resources.getString("task.updateSpecific"),
                    resources.getString(currentType.singularKey),
                    oldDisplayName,
                    newDisplayName);

            runTask(taskName, () -> {
                boolean success = false;
                try {
                    // (SỬA: Thêm case cho Studio/People)
                    switch (currentType) {
                        case STUDIO: studioService.updateStudio(idOrName, newName); success = true; break;
                        case PEOPLE: peopleService.updatePeople(idOrName, newName); success = true; break;
                        case GENRE:  genresService.updateGenre(idOrName, newName); success = true; break;
                        case TAG:    tagService.updateTag(idOrName, newName); success = true; break;
                    }
                    if(success) {
                        updateStatus(String.format(resources.getString("status.task.success"), taskName), false);
                        Platform.runLater(this::loadCurrentTypeList);
                    } else {
                        updateStatus(String.format(resources.getString("error.task.unknown"), taskName), false);
                    }
                } catch (Exception e) {
                    updateStatus(String.format(resources.getString("error.task.failed"), taskName, e.getMessage()), false);
                }
            });
        } else {
            updateStatus(resources.getString("status.cancelled"), false);
        }
    }


    /**
     * (SỬA) Hiển thị dialog để sửa TagModel (Simple hoặc JSON)
     * (Sửa: Gỡ bỏ tham số MetadataType, logic giờ dựa hoàn toàn vào oldModel)
     */
    private Optional<TagModel> showEditTagDialog(TagModel oldModel) {
        Dialog<TagModel> dialog = new Dialog<>();
        dialog.setTitle(String.format(resources.getString("dialog.rename.title"), resources.getString(currentType.singularKey)));
        dialog.setHeaderText(resources.getString("dialog.rename.header"));

        // --- (THÊM MỚI) ÁP DỤNG CSS CHO DIALOG ---
        if (statusBar != null && statusBar.getScene() != null) {
            dialog.initOwner(statusBar.getScene().getWindow());
            // Áp dụng CSS dark mode cho Dialog
            if (!statusBar.getScene().getStylesheets().isEmpty()) {
                String css = statusBar.getScene().getStylesheets().get(0);
                dialog.getDialogPane().getStylesheets().add(css);
            }
        }
        // --- (KẾT THÚC THÊM) ---

        // Thêm nút OK và Cancel
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // --- Tạo layout ---
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));

        CheckBox isJsonCheckbox = new CheckBox(resources.getString("dialog.rename.isJson"));

        // Vùng nhập Tên Đơn Giản
        VBox simpleBox = new VBox(5);
        Label simpleLabel = new Label(resources.getString("dialog.rename.simpleName"));
        TextField simpleField = new TextField();
        simpleBox.getChildren().addAll(simpleLabel, simpleField);

        // Vùng nhập Key-Value
        GridPane jsonGrid = new GridPane();
        jsonGrid.setHgap(10);
        jsonGrid.setVgap(10);
        Label keyLabel = new Label(resources.getString("dialog.rename.key"));
        TextField keyField = new TextField();
        Label valueLabel = new Label(resources.getString("dialog.rename.value"));
        TextField valueField = new TextField();
        jsonGrid.add(keyLabel, 0, 0);
        jsonGrid.add(keyField, 1, 0);
        jsonGrid.add(valueLabel, 0, 1);
        jsonGrid.add(valueField, 1, 1);
        GridPane.setHgrow(keyField, Priority.ALWAYS);
        GridPane.setHgrow(valueField, Priority.ALWAYS);

        mainLayout.getChildren().addAll(isJsonCheckbox, simpleBox, jsonGrid);
        dialog.getDialogPane().setContent(mainLayout);

        // --- Logic ẩn/hiện ---
        isJsonCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            simpleBox.setVisible(!newVal);
            simpleBox.setManaged(!newVal);
            jsonGrid.setVisible(newVal);
            jsonGrid.setManaged(newVal);
        });

        // --- (ĐÃ SỬA) Điền dữ liệu cũ (Logic đúng) ---
        // Giờ đây nó không quan tâm đến 'type', chỉ quan tâm đến 'oldModel'
        isJsonCheckbox.setVisible(true);
        isJsonCheckbox.setManaged(true);

        if (oldModel.isJson()) {
            isJsonCheckbox.setSelected(true);
            keyField.setText(oldModel.getKey());
            valueField.setText(oldModel.getValue());
            simpleBox.setVisible(false);
            simpleBox.setManaged(false);
        } else {
            isJsonCheckbox.setSelected(false);
            simpleField.setText(oldModel.getDisplayName());
            jsonGrid.setVisible(false);
            jsonGrid.setManaged(false);
        }


        // Ngăn nút OK bị disable
        dialog.getDialogPane().lookupButton(okButtonType).setDisable(false);

        // --- Chuyển đổi kết quả ---
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                if (isJsonCheckbox.isSelected()) {
                    String key = keyField.getText();
                    String value = valueField.getText();
                    if (key != null && !key.isEmpty() && value != null && !value.isEmpty()) {
                        return new TagModel(key, value);
                    }
                } else {
                    String simple = simpleField.getText();
                    if (simple != null && !simple.isEmpty()) {
                        return new TagModel(simple);
                    }
                }
                // Nếu không hợp lệ, trả về null (sẽ bị .isPresent() bắt)
                return null;
            }
            return null;
        });

        return dialog.showAndWait();
    }


    // --- Handlers cho Export JSON (Tab 2) ---
    @FXML
    private void btnRunExportJsonClick() {
        String parentId = txtExportJsonParentID.getText();
        if (parentId == null || parentId.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    resources.getString("alert.missingInfo.title"),
                    resources.getString("alert.missingInfo.contentExport"));
            return;
        }
        final String finalParentId = parentId.trim();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(resources.getString("chooser.exportJson.title"));
        File selectedDirectory = directoryChooser.showDialog(btnRunExportJson.getScene().getWindow());
        if (selectedDirectory == null) {
            updateStatus(resources.getString("status.cancelled"), false);
            return;
        }
        String taskName = String.format(resources.getString("task.exportJson"), finalParentId);
        runTask(taskName, () -> {
            exportJsonTask(finalParentId, selectedDirectory);
        });
    }

    private void exportJsonTask(String parentId, File directory) {
        updateStatus(String.format(resources.getString("status.export.gettingList"), parentId) + "...", true);
        List<BaseItemDto> itemsToExport = itemService.getListItemByParentID(parentId, null, null, true);
        if (itemsToExport == null || itemsToExport.isEmpty()) {
            updateStatus(String.format(resources.getString("status.export.noItems"), parentId), false);
            return;
        }
        updateStatus(String.format(resources.getString("status.export.found"), itemsToExport.size()), true);
        int successCount = 0;
        int errorCount = 0;
        String directoryPath = directory.getAbsolutePath();
        for (int i = 0; i < itemsToExport.size(); i++) {
            BaseItemDto listItem = itemsToExport.get(i);
            String itemId = listItem.getId();
            String itemName = listItem.getName();
            if (itemId == null) {
                System.err.println("Bỏ qua item vì không có ID: " + itemName);
                errorCount++;
                continue;
            }
            updateStatus(String.format(resources.getString("status.export.progress"), (i + 1), itemsToExport.size(), itemName), true);
            try {
                BaseItemDto fullItem = itemService.getInforItem(itemId);
                if (fullItem == null) {
                    System.err.println("Lỗi: Không thể lấy full info cho item ID: " + itemId);
                    errorCount++;
                    continue;
                }
                String originalTitle = fullItem.getOriginalTitle();
                String baseFileName = (originalTitle != null && !originalTitle.isEmpty()) ? originalTitle : itemName;
                String safeFileName = baseFileName.replaceAll("[^a-zA-Z0-9.-]", "_") + ".json";
                File outputFile = new File(directoryPath, safeFileName);
                try (FileWriter writer = new FileWriter(outputFile)) {
                    gson.toJson(fullItem, writer);
                    successCount++;
                } catch (IOException ioEx) {
                    System.err.println("Lỗi khi ghi file JSON: " + safeFileName + " - " + ioEx.getMessage());
                    errorCount++;
                }
            } catch (Exception e) {
                System.err.println("Lỗi nghiêm trọng khi xử lý item ID: " + itemId + " - " + e.getMessage());
                e.printStackTrace();
                errorCount++;
            }
        }
        String finalMessage = String.format(resources.getString("status.export.done"), successCount, errorCount, directoryPath);
        updateStatus(finalMessage, false);
    }

    // --- Handlers cho BATCH PROCESS (Tab 2) ---
    @FXML
    private void btnRunBatchProcessClick() {
        String parentId = txtBatchProcessParentID.getText();
        if (parentId == null || parentId.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    resources.getString("alert.missingInfo.title"),
                    resources.getString("alert.missingInfo.contentBatchProcess"));
            return;
        }

        final String finalParentId = parentId.trim();
        String taskName = String.format(resources.getString("task.batchProcess"), finalParentId);
        runTask(taskName, () -> {
            batchProcessTask(finalParentId);
        });
    }

    // --- (HÀM MỚI) Handlers cho IMPORT JSON (Tab 2) ---
    @FXML
    private void btnRunImportJsonClick() {
        // 1. (ĐÃ SỬA) Lấy ParentID (giờ là tùy chọn)
        String parentIdText = txtImportJsonParentID.getText();
        final String finalParentId = (parentIdText != null && !parentIdText.trim().isEmpty()) ? parentIdText.trim() : null;

        // Bỏ kiểm tra ParentID bắt buộc
        // if (parentId == null || parentId.trim().isEmpty()) {
        //     showAlert(Alert.AlertType.WARNING,
        //             resources.getString("alert.missingInfo.title"),
        //             resources.getString("alert.missingInfo.contentImport")); // Key mới
        //     return;
        // }

        // 2. Mở DirectoryChooser để chọn thư mục chứa JSON
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(resources.getString("chooser.importJson.title")); // Key mới
        File selectedDirectory = directoryChooser.showDialog(btnRunImportJson.getScene().getWindow());
        if (selectedDirectory == null) {
            updateStatus(resources.getString("status.cancelled"), false);
            return;
        }

        // 3. Chạy Task
        String taskName = String.format(resources.getString("task.importJson"), selectedDirectory.getName()); // Key mới
        runTask(taskName, () -> {
            importJsonTask(finalParentId, selectedDirectory);
        });
    }

    /**
     * (HÀM ĐÃ NÂNG CẤP) Xử lý import JSON, quét đệ quy.
     * @param parentId ID thư mục (TÙY CHỌN, có thể là null)
     * @param directory Thư mục chứa file JSON
     */
    private void importJsonTask(String parentId, File directory) {
        updateStatus(String.format(resources.getString("status.import.gettingList"), directory.getName()) + "...", true);

        // 1. (ĐÃ NÂNG CẤP) Lấy danh sách file .json (đệ quy)
        List<File> jsonFiles = new ArrayList<>(); // Khởi tạo list rỗng
        try (Stream<Path> stream = Files.walk(directory.toPath())) {
            jsonFiles = stream
                    .filter(Files::isRegularFile) // Chỉ lấy file, bỏ qua thư mục
                    .filter(path -> path.toString().toLowerCase().endsWith(".json")) // Lọc file .json
                    .map(Path::toFile) // Chuyển Path thành File
                    .collect(Collectors.toList()); // Thu thập thành List
        } catch (IOException e) {
            System.err.println("Lỗi nghiêm trọng khi quét thư mục đệ quy: " + directory.getAbsolutePath() + " - " + e.getMessage());
            e.printStackTrace();
            updateStatus("Lỗi quét thư mục: " + e.getMessage(), false);
            return; // Dừng lại nếu không thể quét file
        }

        if (jsonFiles.isEmpty()) { // Sử dụng .isEmpty()
            updateStatus(resources.getString("status.import.noFiles"), false);
            return;
        }

        int total = jsonFiles.size(); // Sử dụng .size()
        updateStatus(String.format(resources.getString("status.import.found"), total), true);

        int successCount = 0;
        int notFoundCount = 0;
        int ambiguousCount = 0; // (MỚI) Đếm số lượng bị trùng
        int errorCount = 0;

        for (int i = 0; i < total; i++) {
            File jsonFile = jsonFiles.get(i); // (SỬA) Dùng .get(i) cho List
            String fileName = jsonFile.getName();
            updateStatus(String.format(resources.getString("status.import.progress"), (i + 1), total, fileName), true);

            try {
                // 2. Đọc và Parse JSON
                BaseItemDto itemFromFile;
                try (FileReader reader = new FileReader(jsonFile)) {
                    itemFromFile = gson.fromJson(reader, BaseItemDto.class);
                }

                if (itemFromFile == null) {
                    System.err.println("Lỗi parse file: " + fileName + " (nội dung rỗng?)");
                    errorCount++;
                    continue;
                }

                // 3. Lấy OriginalTitle
                String originalTitle = itemFromFile.getOriginalTitle();
                if (originalTitle == null || originalTitle.isEmpty()) {
                    System.err.println("Bỏ qua file (không có OriginalTitle): " + fileName);
                    errorCount++;
                    continue;
                }

                // 4. (ĐÃ SỬA) Tìm item trên server (parentId có thể là null)
                if(parentId != null) {
                    System.out.println(String.format(resources.getString("status.import.searching.parent"), parentId, originalTitle));
                } else {
                    System.out.println(String.format(resources.getString("status.import.searching.server"), originalTitle));
                }

                // Gọi hàm service mới trả về List
                List<BaseItemDto> itemsOnServer = itemService.findItemsByOriginalTitle(parentId, originalTitle);

                // 5. (ĐÃ SỬA) Xử lý 3 trường hợp

                if (itemsOnServer == null || itemsOnServer.isEmpty()) {
                    // TRƯỜNG HỢP 1: KHÔNG TÌM THẤY
                    System.err.println(String.format(resources.getString("status.import.notFound"), originalTitle, fileName));
                    notFoundCount++;
                    continue;

//                } else if (itemsOnServer.size() > 1) {
//                    // TRƯỜNG HỢP 2: TÌM THẤY NHIỀU (BỊ "DOUBLE")
//                    System.err.println(String.format(resources.getString("status.import.multipleFound"), itemsOnServer.size(), originalTitle, fileName));
//                    ambiguousCount++;
//                    continue;

                } else {
                    for (BaseItemDto eachItem : itemsOnServer) {
                        if(eachItem.getId().equals(itemFromFile.getId())) {
                            continue;
                        }

                        System.out.println("Tìm thấy item: " + eachItem.getName() + " (ID: " + eachItem.getId() + "). Đang cập nhật...");
                        if (itemService.updateItemFromJson(eachItem, itemFromFile)) {
                            System.out.println("Update thành công cho: " + originalTitle);
                            successCount++;
                        } else {
                            System.err.println("Update thất bại (API) cho: " + originalTitle);
                            errorCount++;
                        }
                    }
                    // TRƯỜNG HỢP 3: TÌM THẤY ĐÚNG 1 ITEM
                    /*BaseItemDto itemOnServer = itemsOnServer.get(0); // Lấy item duy nhất

                    System.out.println("Tìm thấy item: " + itemOnServer.getName() + " (ID: " + itemOnServer.getId() + "). Đang cập nhật...");
                    if (itemService.updateItemFromJson(itemOnServer, itemFromFile)) {
                        System.out.println("Update thành công cho: " + originalTitle);
                        successCount++;
                    } else {
                        System.err.println("Update thất bại (API) cho: " + originalTitle);
                        errorCount++;
                    }*/
                }

            } catch (JsonSyntaxException jsonEx) {
                System.err.println("Lỗi cú pháp JSON: " + fileName + " - " + jsonEx.getMessage());
                errorCount++;
            } catch (Exception e) {
                System.err.println("Lỗi nghiêm trọng khi xử lý file: " + fileName + " - " + e.getMessage());
                e.printStackTrace();
                errorCount++;
            }
        }

        // 6. (ĐÃ SỬA) Báo cáo kết quả
        String finalMessage = String.format(resources.getString("status.import.done"), successCount, notFoundCount, ambiguousCount, errorCount);
        updateStatus(finalMessage, false);
    }

    private void batchProcessTask(String parentId) {
        updateStatus(String.format(resources.getString("status.batch.gettingList"), parentId) + "...", true);
        List<BaseItemDto> itemsToProcess = itemService.getListItemByParentID(parentId, null, null, true); // true = recursive
        if (itemsToProcess == null || itemsToProcess.isEmpty()) {
            updateStatus(String.format(resources.getString("status.batch.noItems"), parentId), false);
            return;
        }

        int total = itemsToProcess.size();
        updateStatus(String.format(resources.getString("status.batch.found"), total), true);
        int successCount = 0;
        int skippedCount = 0;
        int errorCount = 0;

        for (int i = 0; i < total; i++) {
            BaseItemDto listItem = itemsToProcess.get(i);
            String itemId = listItem.getId();
            String itemName = listItem.getName();
            if (itemId == null) {
                System.err.println("Bỏ qua item vì không có ID: " + itemName);
                errorCount++;
                continue;
            }

            updateStatus(String.format(resources.getString("status.batch.progress"), (i + 1), total, itemName), true);

            try {
                // 1. Lấy full info
                BaseItemDto fullItem = itemService.getInforItem(itemId);
                if (fullItem == null) {
                    System.err.println("Lỗi: Không thể lấy full info cho item ID: " + itemId);
                    errorCount++;
                    continue;
                }

                // 2. Gọi hàm logic đã tối ưu
                boolean hasChanged = itemService.checkAndProcessItemTitleAndDate(fullItem);

                // 3. Update nếu cần
                if (hasChanged) {
                    if(itemService.updateInforItem(fullItem.getId(), fullItem)) {
                        System.out.println("Update thành công: " + itemName);
                        successCount++;
                    } else {
                        System.err.println("Update thất bại (API): " + itemName);
                        errorCount++;
                    }
                } else {
                    System.out.println("Bỏ qua (không thay đổi): " + itemName);
                    skippedCount++;
                }

            } catch (Exception e) {
                System.err.println("Lỗi nghiêm trọng khi xử lý item ID: " + itemId + " - " + e.getMessage());
                e.printStackTrace();
                errorCount++;
            }
        }

        String finalMessage = String.format(resources.getString("status.batch.done"), successCount, skippedCount, errorCount);
        updateStatus(finalMessage, false);
    }
}