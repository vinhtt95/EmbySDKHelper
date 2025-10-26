package vinhtt.emby.sdkv4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import javafx.stage.DirectoryChooser;
import vinhtt.emby.sdkv4.service.*;
import vinhtt.emby.sdkv4.ui.TagModel;
import vinhtt.emby.sdkv4.ui.TagView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
// Xóa import Predicate

// === IMPORT MỚI ĐỂ SỬA LỖI JSON ===
import java.time.OffsetDateTime;
import embyclient.JSON.OffsetDateTimeTypeAdapter;
// ===================================

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

    // --- Biến Gson để export JSON (ĐÃ SỬA ĐỂ FIX LỖI) ---
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeTypeAdapter()) // <-- DÒNG SỬA LỖI
            .setPrettyPrinting()
            .create();

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

    // --- TitledPane Export JSON (MỚI) ---
    @FXML private TitledPane exportJsonPane;
    @FXML private TextField txtExportJsonParentID;
    @FXML private Button btnRunExportJson;

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
    private ItemService itemService; // <-- THÊM DÒNG NÀY
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
        // --- SỬA CÁC DÒNG NÀY ---
        this.itemService = new ItemService(this.userId); // Khởi tạo và lưu trữ itemService
        this.studioService = new StudioService(this.itemService);
        this.genresService = new GenresService(this.itemService);
        this.peopleService = new PeopleService(this.itemService);
        this.tagService = new TagService(this.itemService);
        // --- KẾT THÚC SỬA ---
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

    // --- Helper Methods (updateStatus, showAlert, updateUITexts, clearSelectionAndSearch, loadCurrentTypeList, applyFilter, getItemName, getUserData, getDisplayNameFromToggle) ---
    // (Các hàm này giữ nguyên như cũ)

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
        // ... (Hàm này giữ nguyên) ...
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
        // ... (Hàm này giữ nguyên) ...
        selectionToggleGroup.selectToggle(null);
        searchField.clear();
    }

    private void loadCurrentTypeList() {
        // ... (Hàm này giữ nguyên) ...
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
                System.err.println("Lỗi API khi tải danh sách " + currentType.pluralName);
                rawList = Collections.emptyList();
            }

            final List<?> finalList = rawList != null ? rawList : Collections.emptyList();

            Platform.runLater(() -> {
                originalItemList.setAll(finalList);
                populateFlowPaneFromFilteredList();
                updateStatus("Tải xong " + currentType.pluralName + ". (" + originalItemList.size() + " mục)", false);
            });
        });
    }

    private void applyFilter(String filterText) {
        // ... (Hàm này giữ nguyên) ...
        String lowerCaseFilter = filterText == null ? "" : filterText.toLowerCase().trim();

        filteredItemList.setPredicate(item -> {
            if (lowerCaseFilter.isEmpty()) {
                return true;
            }
            String itemName = getItemName(item);
            return itemName != null && itemName.toLowerCase().contains(lowerCaseFilter);
        });
        populateFlowPaneFromFilteredList();
    }

    private String getItemName(Object item) {
        // ... (Hàm này giữ nguyên) ...
        if (item instanceof BaseItemDto) {
            return ((BaseItemDto) item).getName();
        } else if (item instanceof UserLibraryTagItem) {
            return ((UserLibraryTagItem) item).getName();
        }
        return null;
    }

    private String getUserData(Object item) {
        // ... (Hàm này giữ nguyên) ...
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

    private void populateFlowPaneFromFilteredList() {
        // ... (Hàm này giữ nguyên) ...
        selectionFlowPane.getChildren().clear();
        selectionToggleGroup.getToggles().clear();

        for (Object item : filteredItemList) {
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
                chipButton.getStyleClass().add("chip-toggle-button");

                if (tagModel.isJson()) {
                    chipButton.getStyleClass().add("tag-view-json");
                } else {
                    chipButton.getStyleClass().add("tag-view-simple");
                }

                selectionFlowPane.getChildren().add(chipButton);
            }
        }
    }


    private String getDisplayNameFromToggle(Toggle toggle) {
        // ... (Hàm này giữ nguyên) ...
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
        // --- SỬA DÒNG KIỂM TRA NÀY ---
        if (studioService == null || genresService == null || peopleService == null || tagService == null || itemService == null) {
            // --- KẾT THÚC SỬA ---
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
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    // --- Event Handlers ---

    @FXML private void handleLogout() {
        // ... (Hàm này giữ nguyên) ...
        if (app != null) app.handleLogout();
        else {
            updateStatus("Lỗi: Không thể đăng xuất (app is null).");
            showAlert(Alert.AlertType.ERROR, "Lỗi Đăng Xuất", "Tham chiếu ứng dụng không hợp lệ.");
        }
    }

    @FXML private void btnReloadListClick() {
        // ... (Hàm này giữ nguyên) ...
        searchField.clear();
        loadCurrentTypeList();
    }

    // --- Handlers cho Copy ---
    @FXML private void btnRunCopyClick() {
        // ... (Hàm này giữ nguyên) ...
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
        // ... (Hàm này giữ nguyên) ...
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
        // ... (Hàm này giữ nguyên) ...
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

    // --- (MỚI) Handlers cho Export JSON ---

    /**
     * Step 1, 2, 3: Lấy ParentID, Mở DirectoryChooser và gọi Task chạy nền
     */
    @FXML
    private void btnRunExportJsonClick() {
        // Step 1. Lấy ParentID
        String parentId = txtExportJsonParentID.getText();
        if (parentId == null || parentId.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập ID Thư mục Cha (Parent ID).");
            return;
        }
        final String finalParentId = parentId.trim();

        // Step 2. Mở DirectoryChooser (Phải chạy trên UI Thread)
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Chọn thư mục để lưu file JSON");
        File selectedDirectory = directoryChooser.showDialog(btnRunExportJson.getScene().getWindow());

        if (selectedDirectory == null) {
            updateStatus("Đã hủy: Người dùng không chọn thư mục.", false);
            return;
        }

        // Step 3. Chạy Task nền
        String taskName = "Export JSON từ Parent ID: " + finalParentId;
        runTask(taskName, () -> {
            // Hàm này chứa các Step 4, 5, 6 và chạy trên background thread
            exportJsonTask(finalParentId, selectedDirectory);
        });
    }

    /**
     * Step 4, 5, 6: Lấy danh sách, lấy thông tin chi tiết và lưu file
     * (Hàm này được gọi bởi runTask và chạy trên background thread)
     */
    private void exportJsonTask(String parentId, File directory) {
        // Step 4. Lấy danh sách item con
        updateStatus("Đang lấy danh sách item con từ Parent ID: " + parentId + "...", true);
        // Lấy tất cả item con, đệ quy (recursive = true)
        List<BaseItemDto> itemsToExport = itemService.getListItemByParentID(parentId, null, null, true);

        if (itemsToExport == null || itemsToExport.isEmpty()) {
            updateStatus("Không tìm thấy item con nào trong Parent ID: " + parentId, false);
            showAlert(Alert.AlertType.INFORMATION, "Hoàn thành", "Không tìm thấy item con nào để export.");
            return;
        }

        updateStatus("Tìm thấy " + itemsToExport.size() + " item. Bắt đầu export...", true);
        int successCount = 0;
        int errorCount = 0;
        String directoryPath = directory.getAbsolutePath();

        // Step 5. Vòng lặp: Lấy full info và lưu file
        for (int i = 0; i < itemsToExport.size(); i++) {
            BaseItemDto listItem = itemsToExport.get(i);
            String itemId = listItem.getId();
            String itemName = listItem.getName(); // Dùng Name làm tên dự phòng

            if (itemId == null) {
                System.err.println("Bỏ qua item vì không có ID: " + itemName);
                errorCount++;
                continue;
            }

            // Cập nhật status bar cho user biết tiến trình
            updateStatus("Đang export: (" + (i + 1) + "/" + itemsToExport.size() + ") " + itemName, true);

            try {
                // Lấy thông tin đầy đủ của item
                BaseItemDto fullItem = itemService.getInforItem(itemId);
                if (fullItem == null) {
                    System.err.println("Lỗi: Không thể lấy full info cho item ID: " + itemId);
                    errorCount++;
                    continue;
                }

                // Lấy OriginalTitle làm tên file
                String originalTitle = fullItem.getOriginalTitle();
                String baseFileName = (originalTitle != null && !originalTitle.isEmpty()) ? originalTitle : itemName;

                // Làm sạch tên file (xóa các ký tự không hợp lệ)
                String safeFileName = baseFileName.replaceAll("[^a-zA-Z0-9.-]", "_") + ".json";
                File outputFile = new File(directoryPath, safeFileName);

                // Ghi file JSON
                try (FileWriter writer = new FileWriter(outputFile)) {
                    gson.toJson(fullItem, writer); // Chuyển đổi DTO thành JSON và ghi ra file
                    successCount++;
                } catch (IOException ioEx) {
                    System.err.println("Lỗi khi ghi file JSON: " + safeFileName + " - " + ioEx.getMessage());
                    errorCount++;
                }
            } catch (Exception e) {
                // Bắt các lỗi khác (vd: lỗi API khi getInforItem)
                System.err.println("Lỗi nghiêm trọng khi xử lý item ID: " + itemId + " - " + e.getMessage());
                e.printStackTrace();
                errorCount++;
            }
        } // Kết thúc vòng lặp

        // Step 6. Báo cáo kết quả
        String finalMessage = "Hoàn thành export! Thành công: " + successCount + ", Lỗi: " + errorCount + ". Đã lưu tại: " + directoryPath;
        updateStatus(finalMessage, false);
        showAlert(Alert.AlertType.INFORMATION, "Export Hoàn tất", finalMessage);
    }

}