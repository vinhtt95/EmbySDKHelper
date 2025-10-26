package vinhtt.emby.sdkv4;

import embyclient.model.BaseItemDto;
import embyclient.model.UserLibraryTagItem;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList; // *** THÊM IMPORT ***
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import vinhtt.emby.sdkv4.service.*;
import vinhtt.emby.sdkv4.ui.TagModel;
import vinhtt.emby.sdkv4.ui.TagView;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate; // *** THÊM IMPORT ***

public class MainController {

    // Enum để định nghĩa các loại Metadata
    private enum MetadataType {
        STUDIO("Studio", "Studios"),
        GENRE("Genre", "Genres"),
        PEOPLE("People", "People"),
        TAG("Tag", "Tags");

        final String singularName;
        final String pluralName;

        MetadataType(String singular, String plural) {
            this.singularName = singular;
            this.pluralName = plural;
        }

        @Override
        public String toString() {
            return this.pluralName; // Hiển thị tên số nhiều trong ComboBox
        }
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
    @FXML private TextField txtCopyFromInfo; // Hiển thị tên/ID
    @FXML private TextField txtCopyFromItemID; // Lưu trữ ID (ẩn)
    @FXML private Button btnChooseCopyFrom;
    @FXML private TextField txtCopyToInfo; // Hiển thị tên/ID
    @FXML private TextField txtCopyToParentID; // Lưu trữ ID (ẩn)
    @FXML private Button btnChooseCopyTo;
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
    private MetadataType currentType = MetadataType.STUDIO; // Mặc định là Studio

    // --- Danh sách gốc và FilteredList cho tối ưu tìm kiếm ---
    private final ObservableList<Object> originalItemList = FXCollections.observableArrayList();
    private FilteredList<Object> filteredItemList;

    // --- Properties để lưu trữ ID/Tên đã chọn cho Copy ---
    private final StringProperty selectedCopyFromId = new SimpleStringProperty();
    private final StringProperty selectedCopyFromName = new SimpleStringProperty();
    private final StringProperty selectedCopyToId = new SimpleStringProperty();
    private final StringProperty selectedCopyToName = new SimpleStringProperty();


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
        // Tải danh sách cho loại mặc định (Studio)
        Platform.runLater(this::loadCurrentTypeList);
    }

    // --- Initialize ---
    @FXML
    public void initialize() {
        // Khởi tạo ComboBox
        typeComboBox.getItems().setAll(MetadataType.values());
        typeComboBox.setValue(currentType); // Đặt giá trị mặc định
        typeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            if (newType != null) {
                currentType = newType;
                updateUITexts(); // Cập nhật các label/tiêu đề
                clearSelectionAndSearch(); // Xóa lựa chọn và tìm kiếm cũ
                loadCurrentTypeList(); // Tải danh sách cho loại mới
            }
        });

        // Binding cho các TextField hiển thị trong Copy Pane
        txtCopyFromInfo.textProperty().bind(selectedCopyFromName);
        txtCopyFromItemID.textProperty().bindBidirectional(selectedCopyFromId); // ID ẩn
        txtCopyToInfo.textProperty().bind(selectedCopyToName);
        txtCopyToParentID.textProperty().bindBidirectional(selectedCopyToId); // ID ẩn

        // Khởi tạo FilteredList
        filteredItemList = new FilteredList<>(originalItemList, p -> true); // Ban đầu hiển thị tất cả

        // Binding ô tìm kiếm với FilteredList
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            applyFilter(newVal);
        });
        // Nhấn Enter cũng áp dụng filter (mặc dù nó đã tự áp dụng khi gõ)
        searchField.setOnAction(e -> applyFilter(searchField.getText()));

        // Populate FlowPane từ FilteredList
        populateFlowPaneFromFilteredList(); // Hiển thị ban đầu (rỗng)

        // Gán sự kiện Logout
        if (logoutMenuItem != null) {
            logoutMenuItem.setOnAction(e -> handleLogout());
        } else {
            System.err.println("CẢNH BÁO: logoutMenuItem là null.");
        }

        updateUITexts(); // Cập nhật text lần đầu
        updateStatus("Main Controller đã khởi tạo. Chọn loại Metadata để bắt đầu.");
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

    /** Cập nhật các Label và Tiêu đề Pane dựa trên currentType */
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

    /** Xóa lựa chọn trong FlowPane và ô tìm kiếm */
    private void clearSelectionAndSearch() {
        selectionToggleGroup.selectToggle(null);
        searchField.clear();
        // applyFilter("") sẽ được gọi bởi listener của searchField
    }

    /** Tải danh sách gốc dựa trên currentType */
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

    /** Áp dụng bộ lọc cho FilteredList */
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

    /** Helper lấy tên từ item (BaseItemDto hoặc UserLibraryTagItem) */
    private String getItemName(Object item) {
        if (item instanceof BaseItemDto) {
            return ((BaseItemDto) item).getName();
        } else if (item instanceof UserLibraryTagItem) {
            return ((UserLibraryTagItem) item).getName();
        }
        return null;
    }

    /** Helper lấy ID hoặc Name làm UserData */
    private String getUserData(Object item) {
        switch (currentType) {
            case STUDIO:
            case PEOPLE:
                return ((BaseItemDto) item).getId(); // Studio, People dùng ID
            case GENRE:
            case TAG:
                return getItemName(item); // Genre, Tag dùng Name
            default: return null;
        }
    }


    /** Populate FlowPane từ FilteredList (thay thế hàm cũ) */
    private void populateFlowPaneFromFilteredList() {
        selectionFlowPane.getChildren().clear();
        selectionToggleGroup.getToggles().clear();

        for (Object item : filteredItemList) { // Duyệt qua FilteredList
            String rawName = getItemName(item);
            if (rawName == null || rawName.isEmpty()) continue;

            TagModel tagModel = TagModel.parse(rawName);
            String userData = getUserData(item);

            ToggleButton chipButton = new ToggleButton();
            chipButton.setUserData(userData);
            chipButton.setToggleGroup(selectionToggleGroup);

            TagView tagView = new TagView(tagModel);
            tagView.getDeleteButton().setVisible(false);
            tagView.getDeleteButton().setManaged(false);

            chipButton.setGraphic(tagView);
            chipButton.getStyleClass().add("chip-toggle-button");

            selectionFlowPane.getChildren().add(chipButton);
        }
    }


    /** Lấy tên hiển thị từ ToggleButton chứa TagView */
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

    /** Chạy tác vụ nền (giữ nguyên runTask cũ) */
    private void runTask(String taskName, Runnable task) {
        // ... (Giữ nguyên logic runTask đã cung cấp ở lần trước) ...
        if (studioService == null || genresService == null || peopleService == null || tagService == null) {
            updateStatus("LỖI: Services chưa được khởi tạo.");
            showAlert(Alert.AlertType.ERROR, "Lỗi Service", "Các service chưa sẵn sàng, vui lòng thử lại sau.");
            return;
        }

        updateStatus("Đang thực thi: " + taskName + "...", true); // Bắt đầu loading
        Thread thread = new Thread(() -> {
            try {
                task.run();
                // Không tự động báo thành công ở đây, để hàm gọi tự báo
                // updateStatus("Hoàn thành: " + taskName + " thành công!", false);
            } catch (Exception e) {
                String errorMsg = "LỖI khi thực thi " + taskName + ": " + e.getMessage();
                System.err.println("\n!!! ĐÃ XẢY RA LỖI !!!"); // Log lỗi ra console để debug
                e.printStackTrace();
                updateStatus(errorMsg, false); // Kết thúc loading, báo lỗi
                showAlert(Alert.AlertType.ERROR, "Lỗi Thực Thi", errorMsg); // Hiển thị lỗi trên UI
            } finally {
                // Luôn tắt loading indicator dù thành công hay lỗi (trừ khi hàm gọi muốn giữ)
                // Platform.runLater(() -> statusIndicator.setVisible(false));
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    // --- Event Handlers ---

    @FXML private void handleLogout() {
        // ... (Giữ nguyên) ...
        if (app != null) {
            app.handleLogout();
        } else {
            updateStatus("Lỗi: Không thể đăng xuất (app is null).");
            showAlert(Alert.AlertType.ERROR, "Lỗi Đăng Xuất", "Tham chiếu ứng dụng không hợp lệ.");
        }
    }

    @FXML private void btnReloadListClick() {
        searchField.clear(); // Xóa bộ lọc
        loadCurrentTypeList(); // Tải lại danh sách gốc
    }

    // --- Handlers cho Copy ---
    @FXML private void handleChooseCopyFromClick() {
        // TODO: Mở Dialog chọn Item Mẫu
        // Dialog này cần trả về ID và Name của item được chọn
        // Ví dụ:
        // ItemSelectionResult result = showItemSelectionDialog("Chọn Item Mẫu (" + currentType.singularName + ")");
        // if (result != null) {
        //     selectedCopyFromId.set(result.getId());
        //     selectedCopyFromName.set(result.getName() + " (ID: " + result.getId() + ")");
        // }
        updateStatus("Chức năng 'Chọn Item Mẫu' chưa được triển khai.");
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Chức năng chọn item mẫu từ thư viện chưa được hoàn thiện.");
    }

    @FXML private void handleChooseCopyToClick() {
        // TODO: Mở Dialog chọn Folder Đích
        // Dialog này cần trả về ID và Name của folder được chọn
        // Ví dụ:
        // ItemSelectionResult result = showFolderSelectionDialog("Chọn Thư Mục Đích");
        // if (result != null) {
        //     selectedCopyToId.set(result.getId());
        //     selectedCopyToName.set(result.getName() + " (ID: " + result.getId() + ")");
        // }
        updateStatus("Chức năng 'Chọn Thư Mục Đích' chưa được triển khai.");
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Chức năng chọn thư mục đích từ thư viện chưa được hoàn thiện.");
    }

    @FXML private void btnRunCopyClick() {
        String copyFromId = selectedCopyFromId.get();
        String copyToId = selectedCopyToId.get();

        if (copyFromId == null || copyFromId.isEmpty() || copyToId == null || copyToId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng chọn Item Mẫu và Thư mục Đích.");
            return;
        }

        String taskName = "Copy " + currentType.pluralName;
        runTask(taskName, () -> {
            boolean success = false;
            try {
                switch (currentType) {
                    case STUDIO: studioService.copyStudio(copyFromId, copyToId); success = true; break;
                    case GENRE:  genresService.copyGenres(copyFromId, copyToId); success = true; break;
                    case PEOPLE: peopleService.copyPeople(copyFromId, copyToId); success = true; break;
                    case TAG:    tagService.copyTags(copyFromId, copyToId); success = true; break;
                }
                if(success) {
                    updateStatus("Hoàn thành: " + taskName + " thành công!", false);
                } else {
                    updateStatus("Lỗi: Không thể thực hiện " + taskName + " cho loại không xác định.", false);
                }
            } catch (Exception e) {
                // Lỗi đã được xử lý trong runTask, chỉ cần đảm bảo loading tắt
                updateStatus("Lỗi khi " + taskName + ": " + e.getMessage(), false);
                // Không cần throw lại lỗi ở đây
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
        String taskName = "Xóa " + currentType.pluralName + " theo thư mục";
        runTask(taskName, () -> {
            boolean success = false;
            try {
                switch (currentType) {
                    case STUDIO: studioService.clearStudioByParentID(parentId); success = true; break;
                    case GENRE:  genresService.clearGenresByParentID(parentId); success = true; break;
                    case PEOPLE: peopleService.clearPeopleByParentID(parentId); success = true; break;
                    case TAG:    tagService.clearTagsByParentID(parentId); success = true; break;
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

    // Cấu trúc lớp giả định cho kết quả trả về từ Dialog chọn Item/Folder
    private static class ItemSelectionResult {
        private String id;
        private String name;
        // constructor, getters...
        public ItemSelectionResult(String id, String name) { this.id = id; this.name = name;}
        public String getId() { return id; }
        public String getName() { return name; }
    }
}