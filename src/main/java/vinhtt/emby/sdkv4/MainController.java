package vinhtt.emby.sdkv4;

import embyclient.model.BaseItemDto;
import embyclient.model.UserLibraryTagItem;
import javafx.application.Platform;
// Xóa import StringProperty
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import vinhtt.emby.sdkv4.service.*;
import vinhtt.emby.sdkv4.ui.TagModel;
import vinhtt.emby.sdkv4.ui.TagView;

import java.util.Collections;
import java.util.List;
// Xóa import Predicate

public class MainController {

    // Enum MetadataType (giữ nguyên)
    private enum MetadataType {
        STUDIO("Studio", "Studios"),
        GENRE("Genre", "Genres"),
        PEOPLE("People", "People"),
        TAG("Tag", "Tags");
        // ... (constructor, toString giữ nguyên) ...
        final String singularName;
        final String pluralName;
        MetadataType(String singular, String plural) { this.singularName = singular; this.pluralName = plural; }
        @Override public String toString() { return this.pluralName; }
    }

    // --- Tham chiếu HelloApplication ---
    private HelloApplication app;

    // --- MenuBar ---
    @FXML private MenuBar menuBar;
    @FXML private MenuItem logoutMenuItem;

    // --- Status Bar ---
    @FXML private HBox statusBar;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator statusIndicator;

    // --- Vùng Chọn Loại ---
    @FXML private ComboBox<MetadataType> typeComboBox;

    // --- TitledPane Copy ---
    @FXML private TitledPane copyPane;
    @FXML private TextField txtCopyFromItemID; // TextField nhập ID mẫu
    @FXML private TextField txtCopyToParentID; // TextField nhập ID đích
    @FXML private Button btnRunCopy;

    // --- TitledPane Xóa Theo Thư Mục ---
    @FXML private TitledPane clearParentPane;
    @FXML private Label clearParentLabel;
    @FXML private TextField txtClearByParentID;
    @FXML private Button btnRunClearParent;

    // --- TitledPane Xóa Cụ Thể ---
    @FXML private TitledPane clearSpecificPane;
    @FXML private Label clearSpecificLabel;
    @FXML private TextField searchField;
    @FXML private Button btnReloadList;
    @FXML private ScrollPane selectionScrollPane;
    @FXML private FlowPane selectionFlowPane;
    @FXML private Button btnRunClearSpecific;
    private final ToggleGroup selectionToggleGroup = new ToggleGroup();

    // --- Services ---
    private StudioService studioService;
    private GenresService genresService;
    private PeopleService peopleService;
    private TagService tagService;

    // --- State ---
    private String userId;
    private MetadataType currentType = MetadataType.STUDIO;

    // --- Danh sách gốc và FilteredList ---
    private final ObservableList<Object> originalItemList = FXCollections.observableArrayList();
    private FilteredList<Object> filteredItemList;

    // --- Setters ---
    public void setApp(HelloApplication app) { this.app = app; }

    public void setUserId(String userId) {
        this.userId = userId;
        ItemService itemService = new ItemService(this.userId);
        this.studioService = new StudioService(itemService);
        this.genresService = new GenresService(itemService);
        this.peopleService = new PeopleService(itemService);
        this.tagService = new TagService(itemService);
        updateStatus("Đã khởi tạo các service với UserId: " + userId);
        Platform.runLater(this::loadCurrentTypeList);
    }

    // --- Initialize ---
    @FXML
    public void initialize() {
        // Khởi tạo ComboBox (giữ nguyên)
        typeComboBox.getItems().setAll(MetadataType.values());
        typeComboBox.setValue(currentType);
        typeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            if (newType != null) {
                currentType = newType;
                updateUITexts();
                clearSelectionAndSearch();
                // Xóa cả text trong ô copy ID
                txtCopyFromItemID.clear();
                txtCopyToParentID.clear();
                loadCurrentTypeList();
            }
        });

        // Khởi tạo FilteredList (giữ nguyên)
        filteredItemList = new FilteredList<>(originalItemList, p -> true);

        // Binding ô tìm kiếm với FilteredList (giữ nguyên)
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));
        searchField.setOnAction(e -> applyFilter(searchField.getText()));

        // Populate FlowPane từ FilteredList (giữ nguyên)
        populateFlowPaneFromFilteredList();

        // Gán sự kiện Logout (giữ nguyên)
        if (logoutMenuItem != null) logoutMenuItem.setOnAction(e -> handleLogout());
        else System.err.println("CẢNH BÁO: logoutMenuItem là null.");

        updateUITexts(); // Cập nhật text lần đầu
        updateStatus("Main Controller đã khởi tạo. Chọn loại Metadata để bắt đầu.");
    }

    // --- Helper Methods (updateStatus, showAlert, updateUITexts, clearSelectionAndSearch, loadCurrentTypeList, applyFilter, getItemName, getUserData, getDisplayNameFromToggle, runTask) ---
    // Giữ nguyên các hàm helper này như đã cung cấp ở lần trước

    private void updateStatus(String message) { updateStatus(message, false); }
    private void updateStatus(String message, boolean isLoading) {
        Platform.runLater(() -> {
            if (statusLabel != null) statusLabel.setText(message);
            if (statusIndicator != null) statusIndicator.setVisible(isLoading);
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(() -> { // Đảm bảo chạy trên UI thread
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            if (statusBar != null && statusBar.getScene() != null) {
                alert.initOwner(statusBar.getScene().getWindow());
            }
            alert.showAndWait();
        });
    }

    private void updateUITexts() {
        String singular = currentType.singularName.toLowerCase();
        String plural = currentType.pluralName;

        copyPane.setText("Copy " + plural + " Hàng loạt");
        clearParentPane.setText("Xóa " + plural + " Hàng loạt theo Thư mục");
        clearParentLabel.setText("ID Thư mục Đích (Xóa mọi " + singular + " trong thư mục này):");
        btnRunClearParent.setText("Thực thi Xóa " + plural + " (Theo Thư mục)");
        clearSpecificPane.setText("Xóa " + plural + " Cụ Thể");
        clearSpecificLabel.setText("Tìm và Chọn " + currentType.singularName + " cần xóa (Xóa " + singular + " này khỏi MỌI item):");
        searchField.setPromptText("Tìm tên " + singular + "...");
        btnRunClearSpecific.setText("Thực thi Xóa " + currentType.singularName + " đã chọn");
        btnRunCopy.setText("Thực thi Copy " + plural);
    }

    private void clearSelectionAndSearch() {
        selectionToggleGroup.selectToggle(null);
        searchField.clear();
        // applyFilter("") sẽ được gọi bởi listener của searchField
    }

    private void loadCurrentTypeList() {
        String taskName = "Tải danh sách " + currentType.pluralName;
        runTask(taskName, () -> {
            updateStatus("Đang " + taskName.toLowerCase() + "...", true);
            List<?> rawList = Collections.emptyList(); // Dùng wildcard List<?>
            try {
                switch (currentType) {
                    case STUDIO: rawList = studioService.getListStudios(); break;
                    case GENRE:  rawList = genresService.getListGenres(); break;
                    case PEOPLE: rawList = peopleService.getListPeople(); break;
                    case TAG:    rawList = tagService.getListTags(); break;
                }
            } catch (Exception e) {
                // Lỗi đã được xử lý trong runTask, chỉ cần log thêm nếu muốn
                System.err.println("Lỗi API khi tải danh sách " + currentType.pluralName);
                rawList = Collections.emptyList(); // Đảm bảo list không null
            }

            final List<?> finalList = rawList != null ? rawList : Collections.emptyList(); // Đảm bảo không null

            Platform.runLater(() -> {
                originalItemList.setAll(finalList); // Cập nhật danh sách gốc
                // FilteredList tự động cập nhật
                populateFlowPaneFromFilteredList(); // Cập nhật lại FlowPane từ FilteredList
                updateStatus("Tải xong " + currentType.pluralName + ". (" + originalItemList.size() + " mục)", false);
            });
        });
    }

    private void applyFilter(String filterText) {
        String lowerCaseFilter = filterText == null ? "" : filterText.toLowerCase().trim();

        filteredItemList.setPredicate(item -> {
            if (lowerCaseFilter.isEmpty()) {
                return true; // Hiển thị tất cả nếu filter rỗng
            }
            String itemName = getItemName(item);
            return itemName != null && itemName.toLowerCase().contains(lowerCaseFilter);
        });
        // Cập nhật lại FlowPane sau khi filter thay đổi
        populateFlowPaneFromFilteredList();
    }

    private String getItemName(Object item) {
        if (item instanceof BaseItemDto) {
            return ((BaseItemDto) item).getName();
        } else if (item instanceof UserLibraryTagItem) {
            return ((UserLibraryTagItem) item).getName();
        }
        return null;
    }

    private String getUserData(Object item) {
        switch (currentType) {
            case STUDIO:
            case PEOPLE:
                if (item instanceof BaseItemDto) {
                    return ((BaseItemDto) item).getId();
                }
                break;
            case GENRE:
            case TAG:
                return getItemName(item);
        }
        return null;
    }

    /** Populate FlowPane từ FilteredList (*** SỬA ĐỔI ĐỂ THÊM STYLE CLASS ***) */
    private void populateFlowPaneFromFilteredList() {
        selectionFlowPane.getChildren().clear();
        selectionToggleGroup.getToggles().clear();

        for (Object item : filteredItemList) { // Duyệt qua FilteredList
            String rawName = getItemName(item);
            if (rawName == null || rawName.isEmpty()) continue;

            TagModel tagModel = TagModel.parse(rawName);
            String userData = getUserData(item);

            if (userData != null) {
                ToggleButton chipButton = new ToggleButton();
                chipButton.setUserData(userData);
                chipButton.setToggleGroup(selectionToggleGroup);

                TagView tagView = new TagView(tagModel);
                tagView.getDeleteButton().setVisible(false);
                tagView.getDeleteButton().setManaged(false);

                chipButton.setGraphic(tagView);
                chipButton.getStyleClass().add("chip-toggle-button"); // Class chung

                // *** THÊM CLASS CSS CỤ THỂ CHO MÀU SẮC ***
                if (tagModel.isJson()) {
                    chipButton.getStyleClass().add("tag-view-json"); // Class cho JSON
                } else {
                    chipButton.getStyleClass().add("tag-view-simple"); // Class cho Simple
                }
                // *** KẾT THÚC THÊM CLASS ***

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
        // ... (Giữ nguyên logic runTask) ...
        if (studioService == null || genresService == null || peopleService == null || tagService == null) {
            updateStatus("LỖI: Services chưa được khởi tạo.");
            showAlert(Alert.AlertType.ERROR, "Lỗi Service", "Các service chưa sẵn sàng, vui lòng thử lại sau.");
            return;
        }

        updateStatus("Đang thực thi: " + taskName + "...", true); // Bắt đầu loading
        Thread thread = new Thread(() -> {
            try {
                task.run();
                // Không tự động báo thành công
            } catch (Exception e) {
                String errorMsg = "LỖI khi thực thi " + taskName + ": " + e.getMessage();
                System.err.println("\n!!! ĐÃ XẢY RA LỖI !!!");
                e.printStackTrace();
                updateStatus(errorMsg, false);
                showAlert(Alert.AlertType.ERROR, "Lỗi Thực Thi", errorMsg);
            } finally {
                // Luôn tắt loading indicator trừ khi hàm gọi tự xử lý
                // Platform.runLater(() -> statusIndicator.setVisible(false));
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    // --- Event Handlers ---

    @FXML private void handleLogout() {
        if (app != null) app.handleLogout();
        else {
            updateStatus("Lỗi: Không thể đăng xuất (app is null).");
            showAlert(Alert.AlertType.ERROR, "Lỗi Đăng Xuất", "Tham chiếu ứng dụng không hợp lệ.");
        }
    }

    @FXML private void btnReloadListClick() {
        searchField.clear();
        loadCurrentTypeList();
    }

    // --- Handlers cho Copy ---
    @FXML private void btnRunCopyClick() {
        // Lấy ID trực tiếp từ TextField
        String copyFromId = txtCopyFromItemID.getText();
        String copyToId = txtCopyToParentID.getText();

        if (copyFromId == null || copyFromId.trim().isEmpty() || copyToId == null || copyToId.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập ID Item Mẫu và ID Thư mục Đích.");
            return;
        }
        final String finalCopyFromId = copyFromId.trim();
        final String finalCopyToId = copyToId.trim();

        String taskName = "Copy " + currentType.pluralName;
        runTask(taskName, () -> {
            boolean success = false;
            try {
                switch (currentType) {
                    case STUDIO: studioService.copyStudio(finalCopyFromId, finalCopyToId); success = true; break;
                    case GENRE:  genresService.copyGenres(finalCopyFromId, finalCopyToId); success = true; break;
                    case PEOPLE: peopleService.copyPeople(finalCopyFromId, finalCopyToId); success = true; break;
                    case TAG:    tagService.copyTags(finalCopyFromId, finalCopyToId); success = true; break;
                }
                if(success) {
                    updateStatus("Hoàn thành: " + taskName + " thành công!", false);
                } else {
                    updateStatus("Lỗi: Không thể thực hiện " + taskName + " cho loại không xác định.", false);
                }
            } catch (Exception e) {
                updateStatus("Lỗi khi " + taskName + ": " + e.getMessage(), false);
            }
        });
    }


    // --- Handlers cho Xóa Theo Thư Mục ---
    @FXML private void btnRunClearParentClick() {
        String parentId = txtClearByParentID.getText();
        if (parentId == null || parentId.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập ID Thư mục Đích.");
            return;
        }
        final String finalParentId = parentId.trim();
        String taskName = "Xóa " + currentType.pluralName + " theo thư mục";
        runTask(taskName, () -> {
            boolean success = false;
            try {
                switch (currentType) {
                    case STUDIO: studioService.clearStudioByParentID(finalParentId); success = true; break;
                    case GENRE:  genresService.clearGenresByParentID(finalParentId); success = true; break;
                    case PEOPLE: peopleService.clearPeopleByParentID(finalParentId); success = true; break;
                    case TAG:    tagService.clearTagsByParentID(finalParentId); success = true; break;
                }
                if(success) {
                    updateStatus("Hoàn thành: " + taskName + " thành công!", false);
                } else {
                    updateStatus("Lỗi: Không thể thực hiện " + taskName + " cho loại không xác định.", false);
                }
            } catch (Exception e) {
                updateStatus("Lỗi khi " + taskName + ": " + e.getMessage(), false);
            }
        });
    }

    // --- Handlers cho Xóa Cụ Thể ---
    @FXML private void btnRunClearSpecificClick() {
        Toggle selectedToggle = selectionToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn một " + currentType.singularName + " từ danh sách.");
            return;
        }
        String idOrNameToClear = (String) selectedToggle.getUserData();
        String nameSelected = getDisplayNameFromToggle(selectedToggle);

        String taskName = "Xóa " + currentType.singularName + " cụ thể: " + nameSelected;
        runTask(taskName, () -> {
            boolean success = false;
            try {
                switch (currentType) {
                    case STUDIO: studioService.clearStudio(idOrNameToClear); success = true; break; // Dùng ID
                    case GENRE:  genresService.clearGenres(idOrNameToClear); success = true; break; // Dùng Name
                    case PEOPLE: peopleService.clearPeople(idOrNameToClear); success = true; break; // Dùng ID
                    case TAG:    tagService.clearTags(idOrNameToClear); success = true; break; // Dùng Name
                }
                if(success) {
                    updateStatus("Hoàn thành: " + taskName + " thành công!", false);
                    Platform.runLater(this::loadCurrentTypeList); // Tải lại danh sách sau khi xóa
                } else {
                    updateStatus("Lỗi: Không thể thực hiện " + taskName + " cho loại không xác định.", false);
                }
            } catch (Exception e) {
                updateStatus("Lỗi khi " + taskName + ": " + e.getMessage(), false);
            }
        });
    }

}