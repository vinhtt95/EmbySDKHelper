package vinhtt.emby.sdkv4;

import embyclient.model.BaseItemDto;
import embyclient.model.UserLibraryTagItem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox; // *** THÊM IMPORT NÀY ***
import vinhtt.emby.sdkv4.service.*;
// *** THÊM IMPORT TagModel (sẽ tạo ở bước 2) ***
import vinhtt.emby.sdkv4.ui.TagModel;
// *** THÊM IMPORT TagView (sẽ tạo ở bước 2) ***
import vinhtt.emby.sdkv4.ui.TagView;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class MainController {

    // --- Tham chiếu HelloApplication ---
    private HelloApplication app;

    // --- MenuBar ---
    @FXML private MenuBar menuBar;
    @FXML private MenuItem logoutMenuItem;

    // === XÓA VÙNG LOG ===
    // @FXML private TextArea logTextArea;

    // === THÊM STATUS BAR ===
    @FXML private HBox statusBar;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator statusIndicator;

    // --- Vùng Studio ---
    @FXML private TextField txtStudio_CopyFromItemID;
    @FXML private TextField txtStudio_CopyToParentID;
    @FXML private Button btnStudio_RunCopy;
    @FXML private TextField txtStudio_ClearByParentID;
    @FXML private Button btnStudio_RunClearParent;
    @FXML private TextField studioSearchField;
    @FXML private Button btnStudio_ReloadList;
    @FXML private ScrollPane studioScrollPane;
    @FXML private FlowPane studioSelectionFlowPane;
    @FXML private Button btnStudio_RunClearSpecific;
    private final ToggleGroup studioToggleGroup = new ToggleGroup();

    // --- Vùng Genres ---
    @FXML private TextField txtGenres_CopyFromItemID;
    @FXML private TextField txtGenres_CopyToParentID;
    @FXML private Button btnGenres_RunCopy;
    @FXML private TextField txtGenres_ClearByParentID;
    @FXML private Button btnGenres_RunClearParent;
    @FXML private TextField genresSearchField;
    @FXML private Button btnGenres_ReloadList;
    @FXML private ScrollPane genresScrollPane;
    @FXML private FlowPane genresSelectionFlowPane;
    @FXML private Button btnGenres_RunClearSpecific;
    private final ToggleGroup genresToggleGroup = new ToggleGroup();

    // --- Vùng People ---
    @FXML private TextField txtPeople_CopyFromItemID;
    @FXML private TextField txtPeople_CopyToParentID;
    @FXML private Button btnPeople_RunCopy;
    @FXML private TextField txtPeople_ClearByParentID;
    @FXML private Button btnPeople_RunClearParent;
    @FXML private TextField peopleSearchField;
    @FXML private Button btnPeople_ReloadList;
    @FXML private ScrollPane peopleScrollPane;
    @FXML private FlowPane peopleSelectionFlowPane;
    @FXML private Button btnPeople_RunClearSpecific;
    private final ToggleGroup peopleToggleGroup = new ToggleGroup();

    // --- Vùng Tags ---
    @FXML private TextField txtTags_CopyFromItemID;
    @FXML private TextField txtTags_CopyToParentID;
    @FXML private Button btnTags_RunCopy;
    @FXML private TextField txtTags_ClearByParentID;
    @FXML private Button btnTags_RunClearParent;
    @FXML private TextField tagsSearchField;
    @FXML private Button btnTags_ReloadList;
    @FXML private ScrollPane tagsScrollPane;
    @FXML private FlowPane tagsSelectionFlowPane;
    @FXML private Button btnTags_RunClearSpecific;
    private final ToggleGroup tagsToggleGroup = new ToggleGroup();

    // --- Khai báo các Service ---
    private StudioService studioService;
    private GenresService genresService;
    private PeopleService peopleService;
    private TagService tagService;

    // --- State ---
    private String userId;

    // --- Hàm Setters ---
    public void setApp(HelloApplication app) { this.app = app; }

    public void setUserId(String userId) {
        this.userId = userId;
        ItemService itemService = new ItemService(this.userId);
        this.studioService = new StudioService(itemService);
        this.genresService = new GenresService(itemService);
        this.peopleService = new PeopleService(itemService);
        this.tagService = new TagService(itemService);
        updateStatus("Đã khởi tạo các service với UserId: " + userId);
        loadAllLists();
    }

    // --- Hàm Khởi tạo Controller ---
    @FXML
    public void initialize() {
        // === XÓA CHUYỂN HƯỚNG System.out ===
        // PrintStream ps = new PrintStream(new TextAreaOutputStream(logTextArea));
        // System.setOut(ps);
        // System.setErr(ps);

        // Gán sự kiện cho nút Logout
        if (logoutMenuItem != null) {
            logoutMenuItem.setOnAction(e -> handleLogout());
        } else {
            System.err.println("CẢNH BÁO: logoutMenuItem là null."); // Vẫn giữ log lỗi này
        }

        // Thêm listeners cho các ô tìm kiếm
        addSearchFieldListener(studioSearchField, this::loadStudioList);
        addSearchFieldListener(genresSearchField, this::loadGenresList);
        addSearchFieldListener(peopleSearchField, this::loadPeopleList);
        addSearchFieldListener(tagsSearchField, this::loadTagsList);

        updateStatus("Main Controller đã khởi tạo. Đang chờ UserId từ Login...");
    }

    // --- Hàm Helper Chung ---

    /** Cập nhật status bar (thread-safe) */
    private void updateStatus(String message) {
        updateStatus(message, false); // Mặc định không hiển thị loading
    }

    /** Cập nhật status bar với tùy chọn loading (thread-safe) */
    private void updateStatus(String message, boolean isLoading) {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(message);
            }
            if (statusIndicator != null) {
                statusIndicator.setVisible(isLoading);
            }
        });
    }

    /** Thêm listener vào TextField để tự động tải lại danh sách khi người dùng gõ */
    private void addSearchFieldListener(TextField searchField, Runnable loadAction) {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (loadAction != null) {
                    loadAction.run();
                }
            });
            searchField.setOnAction(e -> {
                if (loadAction != null) {
                    loadAction.run();
                }
            });
        }
    }


    /** Chạy tác vụ dài trên luồng nền và cập nhật status bar */
    private void runTask(String taskName, Runnable task) { // Thêm taskName
        if (studioService == null || genresService == null || peopleService == null || tagService == null) {
            updateStatus("LỖI: Services chưa được khởi tạo.");
            showAlert(Alert.AlertType.ERROR, "Lỗi Service", "Các service chưa sẵn sàng, vui lòng thử lại sau.");
            return;
        }

        updateStatus("Đang thực thi: " + taskName + "...", true); // Bắt đầu loading
        Thread thread = new Thread(() -> {
            try {
                task.run();
                updateStatus("Hoàn thành: " + taskName + " thành công!", false); // Kết thúc loading
            } catch (Exception e) {
                String errorMsg = "LỖI khi thực thi " + taskName + ": " + e.getMessage();
                System.err.println("\n!!! ĐÃ XẢY RA LỖI !!!"); // Log lỗi ra console để debug
                e.printStackTrace();
                updateStatus(errorMsg, false); // Kết thúc loading, báo lỗi
                // Hiển thị lỗi cho người dùng trên UI thread
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi Thực Thi", errorMsg));
            }
        });
        thread.setDaemon(true); // Đảm bảo luồng kết thúc khi ứng dụng đóng
        thread.start();
    }

    /** Xử lý sự kiện Logout */
    private void handleLogout() {
        if (app != null) {
            app.handleLogout();
        } else {
            updateStatus("Lỗi: Không thể đăng xuất (app is null).");
            showAlert(Alert.AlertType.ERROR, "Lỗi Đăng Xuất", "Tham chiếu ứng dụng không hợp lệ.");
        }
    }

    /** Tải tất cả danh sách */
    private void loadAllLists() {
        loadStudioList();
        loadGenresList();
        loadPeopleList();
        loadTagsList();
    }


    // --- CÁC HÀM TẢI DANH SÁCH VÀ POPULATE FLOWPANE ---

    @FXML private void btnStudio_ReloadListClick() { studioSearchField.clear(); loadStudioList(); }
    private void loadStudioList() {
        runTask("Tải danh sách Studio", () -> { // <--- Sửa ở đây
            updateStatus("Đang tải danh sách Studio...", true); // Cập nhật trạng thái cụ thể hơn
            List<BaseItemDto> list = studioService.getListStudios();
            String searchTerm = studioSearchField.getText().toLowerCase().trim();

            List<BaseItemDto> filteredList = (list != null) ? list.stream()
                    .filter(item -> item.getName() != null && (searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm)))
                    .collect(Collectors.toList()) : Collections.emptyList();

            Platform.runLater(() -> {
                // *** SỬA ĐỔI Ở ĐÂY (Bước 2) ***
                populateFlowPaneWithTagModels(studioSelectionFlowPane, studioToggleGroup, filteredList,
                        item -> item.getId(), // UserData là ID
                        item -> item.getName()); // Input cho TagModel.parse là Name
                // *** KẾT THÚC SỬA ĐỔI ***
                updateStatus("Tải xong danh sách Studio. (" + filteredList.size() + " mục)", false); // <--- Sửa ở đây
            });
        });
    }

    @FXML private void btnGenres_ReloadListClick() { genresSearchField.clear(); loadGenresList(); }
    private void loadGenresList() {
        runTask("Tải danh sách Genres", () -> { // <--- Sửa ở đây
            updateStatus("Đang tải danh sách Genres...", true);
            List<BaseItemDto> list = genresService.getListGenres();
            String searchTerm = genresSearchField.getText().toLowerCase().trim();

            List<BaseItemDto> filteredList = (list != null) ? list.stream()
                    .filter(item -> item.getName() != null && (searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm)))
                    .collect(Collectors.toList()) : Collections.emptyList();

            Platform.runLater(() -> {
                // *** SỬA ĐỔI Ở ĐÂY (Bước 2) ***
                populateFlowPaneWithTagModels(genresSelectionFlowPane, genresToggleGroup, filteredList,
                        item -> item.getName(), // UserData là Name (vì Genres dùng Name)
                        item -> item.getName());
                // *** KẾT THÚC SỬA ĐỔI ***
                updateStatus("Tải xong danh sách Genres. (" + filteredList.size() + " mục)", false); // <--- Sửa ở đây
            });
        });
    }

    @FXML private void btnPeople_ReloadListClick() { peopleSearchField.clear(); loadPeopleList(); }
    private void loadPeopleList() {
        runTask("Tải danh sách People", () -> { // <--- Sửa ở đây
            updateStatus("Đang tải danh sách People...", true);
            List<BaseItemDto> list = peopleService.getListPeople();
            String searchTerm = peopleSearchField.getText().toLowerCase().trim();

            List<BaseItemDto> filteredList = (list != null) ? list.stream()
                    .filter(item -> item.getName() != null && (searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm)))
                    .collect(Collectors.toList()) : Collections.emptyList();

            Platform.runLater(() -> {
                // *** SỬA ĐỔI Ở ĐÂY (Bước 2) ***
                populateFlowPaneWithTagModels(peopleSelectionFlowPane, peopleToggleGroup, filteredList,
                        item -> item.getId(), // UserData là ID
                        item -> item.getName());
                // *** KẾT THÚC SỬA ĐỔI ***
                updateStatus("Tải xong danh sách People. (" + filteredList.size() + " mục)", false); // <--- Sửa ở đây
            });
        });
    }

    @FXML private void btnTags_ReloadListClick() { tagsSearchField.clear(); loadTagsList(); }
    private void loadTagsList() {
        runTask("Tải danh sách Tags", () -> { // <--- Sửa ở đây
            updateStatus("Đang tải danh sách Tags...", true);
            List<UserLibraryTagItem> list = tagService.getListTags();
            String searchTerm = tagsSearchField.getText().toLowerCase().trim();

            List<UserLibraryTagItem> filteredList = (list != null) ? list.stream()
                    .filter(item -> item.getName() != null && (searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm)))
                    .collect(Collectors.toList()) : Collections.emptyList();

            Platform.runLater(() -> {
                // *** SỬA ĐỔI Ở ĐÂY (Bước 2) ***
                populateFlowPaneWithTagModels(tagsSelectionFlowPane, tagsToggleGroup, filteredList,
                        item -> item.getName(), // UserData là Name (vì Tags dùng Name)
                        item -> item.getName());
                // *** KẾT THÚC SỬA ĐỔI ***
                updateStatus("Tải xong danh sách Tags. (" + filteredList.size() + " mục)", false); // <--- Sửa ở đây
            });
        });
    }

    /**
     * Helper chung để đổ dữ liệu vào FlowPane dưới dạng Chip (ToggleButton)
     * sử dụng TagModel và TagView (*** HÀM MỚI - Bước 2 ***)
     */
    private <T> void populateFlowPaneWithTagModels(
            FlowPane flowPane,
            ToggleGroup toggleGroup,
            List<T> items,
            java.util.function.Function<T, String> userDataExtractor, // Lấy UserData (ID hoặc Name thô)
            java.util.function.Function<T, String> rawNameExtractor)  // Lấy Name thô để parse TagModel
    {
        flowPane.getChildren().clear();
        toggleGroup.getToggles().clear();

        if (items != null) {
            for (T item : items) {
                String rawName = rawNameExtractor.apply(item);
                if (rawName == null || rawName.isEmpty()) continue; // Bỏ qua nếu tên rỗng

                TagModel tagModel = TagModel.parse(rawName); // Parse thành TagModel
                String userData = userDataExtractor.apply(item); // Lấy UserData (ID/Name)

                ToggleButton chipButton = new ToggleButton();
                chipButton.setUserData(userData); // Lưu ID hoặc Name thô vào UserData
                chipButton.setToggleGroup(toggleGroup);

                // Tạo TagView để hiển thị
                TagView tagView = new TagView(tagModel);
                // Vô hiệu hóa nút xóa trong TagView (chỉ để hiển thị)
                tagView.getDeleteButton().setVisible(false);
                tagView.getDeleteButton().setManaged(false);

                chipButton.setGraphic(tagView); // Đặt TagView làm nội dung đồ họa cho ToggleButton
                chipButton.getStyleClass().add("chip-toggle-button"); // CSS riêng cho ToggleButton chứa chip

                flowPane.getChildren().add(chipButton);
            }
        }
    }


    // --- CÁC HÀM XỬ LÝ SỰ KIỆN COPY ---
    @FXML private void btnStudio_RunCopyClick() {
        runTask("Copy Studio", () -> {
            studioService.copyStudio(txtStudio_CopyFromItemID.getText(), txtStudio_CopyToParentID.getText());
        });
    }
    @FXML private void btnGenres_RunCopyClick() {
        runTask("Copy Genres", () -> {
            genresService.copyGenres(txtGenres_CopyFromItemID.getText(), txtGenres_CopyToParentID.getText());
        });
    }
    @FXML private void btnPeople_RunCopyClick() {
        runTask("Copy People", () -> {
            peopleService.copyPeople(txtPeople_CopyFromItemID.getText(), txtPeople_CopyToParentID.getText());
        });
    }
    @FXML private void btnTags_RunCopyClick() {
        runTask("Copy Tags", () -> {
            tagService.copyTags(txtTags_CopyFromItemID.getText(), txtTags_CopyToParentID.getText());
        });
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN XÓA THEO THƯ MỤC ---
    @FXML private void btnStudio_RunClearParentClick() {
        runTask("Xóa Studio theo thư mục", () -> {
            studioService.clearStudioByParentID(txtStudio_ClearByParentID.getText());
        });
    }
    @FXML private void btnGenres_RunClearParentClick() {
        runTask("Xóa Genres theo thư mục", () -> {
            genresService.clearGenresByParentID(txtGenres_ClearByParentID.getText());
        });
    }
    @FXML private void btnPeople_RunClearParentClick() {
        runTask("Xóa People theo thư mục", () -> {
            peopleService.clearPeopleByParentID(txtPeople_ClearByParentID.getText());
        });
    }
    @FXML private void btnTags_RunClearParentClick() {
        runTask("Xóa Tags theo thư mục", () -> {
            tagService.clearTagsByParentID(txtTags_ClearByParentID.getText());
        });
    }


    // --- CÁC HÀM XỬ LÝ SỰ KIỆN XÓA CỤ THỂ ---
    @FXML private void btnStudio_RunClearSpecificClick() {
        Toggle selectedToggle = studioToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            updateStatus("LỖI: Vui lòng chọn một Studio từ danh sách.");
            showAlert(Alert.AlertType.WARNING, "Chưa chọn Studio", "Vui lòng chọn một Studio để xóa.");
            return;
        }
        String idToClear = (String) selectedToggle.getUserData(); // Lấy ID từ UserData
        String nameSelected = getDisplayNameFromToggle(selectedToggle); // Lấy tên hiển thị

        runTask("Xóa Studio cụ thể: " + nameSelected, () -> { // Task name rõ ràng hơn
            studioService.clearStudio(idToClear);
            // Tải lại danh sách sau khi xóa thành công (trên UI thread)
            Platform.runLater(this::loadStudioList);
        });
    }

    @FXML private void btnGenres_RunClearSpecificClick() {
        Toggle selectedToggle = genresToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            updateStatus("LỖI: Vui lòng chọn một Genre từ danh sách.");
            showAlert(Alert.AlertType.WARNING, "Chưa chọn Genre", "Vui lòng chọn một Genre để xóa.");
            return;
        }
        String nameToClear = (String) selectedToggle.getUserData(); // Genres dùng Name
        String nameSelected = getDisplayNameFromToggle(selectedToggle);

        runTask("Xóa Genre cụ thể: " + nameSelected, () -> {
            genresService.clearGenres(nameToClear);
            Platform.runLater(this::loadGenresList);
        });
    }

    @FXML private void btnPeople_RunClearSpecificClick() {
        Toggle selectedToggle = peopleToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            updateStatus("LỖI: Vui lòng chọn một People từ danh sách.");
            showAlert(Alert.AlertType.WARNING, "Chưa chọn People", "Vui lòng chọn một người để xóa.");
            return;
        }
        String idToClear = (String) selectedToggle.getUserData(); // Lấy ID
        String nameSelected = getDisplayNameFromToggle(selectedToggle);

        runTask("Xóa People cụ thể: " + nameSelected, () -> {
            peopleService.clearPeople(idToClear);
            Platform.runLater(this::loadPeopleList);
        });
    }

    @FXML private void btnTags_RunClearSpecificClick() {
        Toggle selectedToggle = tagsToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            updateStatus("LỖI: Vui lòng chọn một Tag từ danh sách.");
            showAlert(Alert.AlertType.WARNING, "Chưa chọn Tag", "Vui lòng chọn một Tag để xóa.");
            return;
        }
        String nameToClear = (String) selectedToggle.getUserData(); // Tags dùng Name
        String nameSelected = getDisplayNameFromToggle(selectedToggle);

        runTask("Xóa Tag cụ thể: " + nameSelected, () -> {
            tagService.clearTags(nameToClear);
            Platform.runLater(this::loadTagsList);
        });
    }

    /**
     * Helper để lấy tên hiển thị từ TagView bên trong ToggleButton (Bước 2).
     */
    private String getDisplayNameFromToggle(Toggle toggle) {
        if (toggle instanceof ToggleButton) {
            ToggleButton button = (ToggleButton) toggle;
            if (button.getGraphic() instanceof TagView) {
                TagView tagView = (TagView) button.getGraphic();
                return tagView.getTagModel().getDisplayName();
            }
            // Fallback nếu graphic không phải TagView (không nên xảy ra)
            return button.getText(); // Lấy text gốc nếu không có graphic TagView
        }
        return "Không xác định";
    }


    // --- Hàm Helper hiển thị Alert ---
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Lấy Stage gốc để set Owner (nếu có thể)
        if (statusBar != null && statusBar.getScene() != null) { // Sửa thành statusBar
            alert.initOwner(statusBar.getScene().getWindow());
        }
        alert.showAndWait();
    }
}