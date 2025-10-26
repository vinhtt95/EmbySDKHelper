package vinhtt.emby.sdkv4;

import embyclient.model.BaseItemDto;
import embyclient.model.UserLibraryTagItem; // Cần import này
import javafx.application.Platform;
import javafx.collections.FXCollections; // Cần import này
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane; // Cần import này
import javafx.scene.layout.VBox; // Cần import này
import vinhtt.emby.sdkv4.service.*; // Import tất cả service

import java.io.PrintStream;
import java.util.Collections; // Cần import này
import java.util.List;
import java.util.stream.Collectors; // Cần import này

public class MainController {

    // --- Tham chiếu HelloApplication ---
    private HelloApplication app; // Để gọi logout

    // --- MenuBar ---
    @FXML private MenuBar menuBar;
    @FXML private MenuItem logoutMenuItem;

    // --- Vùng Log ---
    @FXML private TextArea logTextArea;

    // --- Vùng Studio ---
    @FXML private TextField txtStudio_CopyFromItemID;
    @FXML private TextField txtStudio_CopyToParentID;
    @FXML private Button btnStudio_RunCopy;
    @FXML private TextField txtStudio_ClearByParentID;
    @FXML private Button btnStudio_RunClearParent;
    // UI Xóa Cụ Thể (Mới)
    @FXML private TextField studioSearchField;
    @FXML private Button btnStudio_ReloadList;
    @FXML private ScrollPane studioScrollPane; // Có thể không cần FXML ID nếu không thao tác trực tiếp
    @FXML private FlowPane studioSelectionFlowPane;
    @FXML private Button btnStudio_RunClearSpecific;
    private final ToggleGroup studioToggleGroup = new ToggleGroup(); // ToggleGroup để chọn

    // --- Vùng Genres ---
    @FXML private TextField txtGenres_CopyFromItemID;
    @FXML private TextField txtGenres_CopyToParentID;
    @FXML private Button btnGenres_RunCopy;
    @FXML private TextField txtGenres_ClearByParentID;
    @FXML private Button btnGenres_RunClearParent;
    // UI Xóa Cụ Thể (Mới)
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
    // UI Xóa Cụ Thể (Mới)
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
    // UI Xóa Cụ Thể (Mới)
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

        // 1. Khởi tạo ItemService DUY NHẤT với UserId
        ItemService itemService = new ItemService(this.userId);

        // 2. Khởi tạo các service khác và "tiêm" ItemService vào
        this.studioService = new StudioService(itemService);
        this.genresService = new GenresService(itemService);
        this.peopleService = new PeopleService(itemService);
        this.tagService = new TagService(itemService);

        System.out.println("Main Controller đã khởi tạo xong các service với UserId: " + userId);

        // Tải danh sách cho FlowPanes lần đầu tiên
        loadAllLists();
    }

    // --- Hàm Khởi tạo Controller ---
    @FXML
    public void initialize() {
        // Chuyển hướng System.out đến logTextArea
        PrintStream ps = new PrintStream(new TextAreaOutputStream(logTextArea));
        System.setOut(ps);
        System.setErr(ps);

        // Gán sự kiện cho nút Logout
        if (logoutMenuItem != null) {
            logoutMenuItem.setOnAction(e -> handleLogout());
        } else {
            System.err.println("CẢNH BÁO: logoutMenuItem là null. Kiểm tra fx:id trong FXML.");
        }

        // Thêm listeners cho các ô tìm kiếm
        addSearchFieldListener(studioSearchField, this::loadStudioList);
        addSearchFieldListener(genresSearchField, this::loadGenresList);
        addSearchFieldListener(peopleSearchField, this::loadPeopleList);
        addSearchFieldListener(tagsSearchField, this::loadTagsList);


        System.out.println("Main Controller đã khởi tạo. Đang chờ UserId từ Login...");
    }

    // --- Hàm Helper Chung ---

    /** Thêm listener vào TextField để tự động tải lại danh sách khi người dùng gõ */
    private void addSearchFieldListener(TextField searchField, Runnable loadAction) {
        if (searchField != null) {
            // Sử dụng Platform.runLater với độ trễ nhỏ để tránh gọi API quá nhiều khi gõ nhanh
            // Hoặc đơn giản là gọi trực tiếp nếu không quá lo lắng về hiệu năng
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                // Gọi loadAction trực tiếp khi text thay đổi
                if (loadAction != null) {
                    loadAction.run();
                }
            });

            // Gán luôn sự kiện onAction (nhấn Enter) để tải lại
            searchField.setOnAction(e -> {
                if (loadAction != null) {
                    loadAction.run();
                }
            });
        }
    }


    /** Chạy tác vụ dài trên luồng nền */
    private void runTask(Runnable task) {
        if (studioService == null || genresService == null || peopleService == null || tagService == null) {
            System.out.println("LỖI: Services chưa được khởi tạo. Vui lòng thử lại.");
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                task.run();
            } catch (Exception e) {
                System.out.println("\n!!! ĐÃ XẢY RA LỖI !!!");
                e.printStackTrace();
            }
        });
        thread.start();
    }

    /** Xử lý sự kiện Logout */
    private void handleLogout() {
        if (app != null) {
            app.handleLogout();
        } else {
            System.err.println("Lỗi: Tham chiếu HelloApplication là null, không thể đăng xuất.");
            // Có thể hiển thị Alert lỗi ở đây
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
        runTask(() -> {
            System.out.println("Đang tải danh sách Studio...");
            List<BaseItemDto> list = studioService.getListStudios();
            String searchTerm = studioSearchField.getText().toLowerCase().trim();

            List<BaseItemDto> filteredList = (list != null) ? list.stream()
                    .filter(item -> item.getName() != null && (searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm)))
                    .collect(Collectors.toList()) : Collections.emptyList();

            Platform.runLater(() -> {
                populateFlowPane(studioSelectionFlowPane, studioToggleGroup, filteredList, item -> item.getId(), item -> item.getName());
                System.out.println("Tải xong danh sách Studio. (" + filteredList.size() + " mục)");
            });
        });
    }

    @FXML private void btnGenres_ReloadListClick() { genresSearchField.clear(); loadGenresList(); }
    private void loadGenresList() {
        runTask(() -> {
            System.out.println("Đang tải danh sách Genres...");
            List<BaseItemDto> list = genresService.getListGenres();
            String searchTerm = genresSearchField.getText().toLowerCase().trim();

            List<BaseItemDto> filteredList = (list != null) ? list.stream()
                    .filter(item -> item.getName() != null && (searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm)))
                    .collect(Collectors.toList()) : Collections.emptyList();

            Platform.runLater(() -> {
                populateFlowPane(genresSelectionFlowPane, genresToggleGroup, filteredList, item -> item.getName(), item -> item.getName()); // Genres dùng Name
                System.out.println("Tải xong danh sách Genres. (" + filteredList.size() + " mục)");
            });
        });
    }

    @FXML private void btnPeople_ReloadListClick() { peopleSearchField.clear(); loadPeopleList(); }
    private void loadPeopleList() {
        runTask(() -> {
            System.out.println("Đang tải danh sách People...");
            List<BaseItemDto> list = peopleService.getListPeople();
            String searchTerm = peopleSearchField.getText().toLowerCase().trim();

            List<BaseItemDto> filteredList = (list != null) ? list.stream()
                    .filter(item -> item.getName() != null && (searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm)))
                    .collect(Collectors.toList()) : Collections.emptyList();

            Platform.runLater(() -> {
                populateFlowPane(peopleSelectionFlowPane, peopleToggleGroup, filteredList, item -> item.getId(), item -> item.getName());
                System.out.println("Tải xong danh sách People. (" + filteredList.size() + " mục)");
            });
        });
    }

    @FXML private void btnTags_ReloadListClick() { tagsSearchField.clear(); loadTagsList(); }
    private void loadTagsList() {
        runTask(() -> {
            System.out.println("Đang tải danh sách Tags...");
            List<UserLibraryTagItem> list = tagService.getListTags(); // Loại dữ liệu khác
            String searchTerm = tagsSearchField.getText().toLowerCase().trim();

            List<UserLibraryTagItem> filteredList = (list != null) ? list.stream()
                    .filter(item -> item.getName() != null && (searchTerm.isEmpty() || item.getName().toLowerCase().contains(searchTerm)))
                    .collect(Collectors.toList()) : Collections.emptyList();

            Platform.runLater(() -> {
                populateFlowPane(tagsSelectionFlowPane, tagsToggleGroup, filteredList, item -> item.getName(), item -> item.getName()); // Tags dùng Name
                System.out.println("Tải xong danh sách Tags. (" + filteredList.size() + " mục)");
            });
        });
    }

    /**
     * Helper chung để đổ dữ liệu vào FlowPane dưới dạng ToggleButton (chip).
     * @param <T> Kiểu dữ liệu của danh sách (BaseItemDto hoặc UserLibraryTagItem)
     * @param flowPane FlowPane đích
     * @param toggleGroup ToggleGroup để quản lý lựa chọn
     * @param items Danh sách các item đã lọc
     * @param userDataExtractor Hàm để lấy giá trị userData (thường là ID hoặc Name)
     * @param displayTextExtractor Hàm để lấy text hiển thị trên chip
     */
    private <T> void populateFlowPane(FlowPane flowPane, ToggleGroup toggleGroup, List<T> items,
                                      java.util.function.Function<T, String> userDataExtractor,
                                      java.util.function.Function<T, String> displayTextExtractor)
    {
        flowPane.getChildren().clear(); // Xóa chip cũ
        toggleGroup.getToggles().clear(); // Xóa toggle cũ khỏi group

        if (items != null) {
            for (T item : items) {
                ToggleButton chip = new ToggleButton(displayTextExtractor.apply(item));
                chip.setUserData(userDataExtractor.apply(item)); // Lưu ID hoặc Name
                chip.setToggleGroup(toggleGroup);
                chip.getStyleClass().add("chip-button"); // Áp dụng CSS
                // Tùy chọn: setMaxWidth để chip không quá dài
                chip.setMaxWidth(Double.MAX_VALUE);

                flowPane.getChildren().add(chip);
            }
        }
    }


    // --- CÁC HÀM XỬ LÝ SỰ KIỆN COPY (Không đổi) ---

    @FXML private void btnStudio_RunCopyClick() { /* ... như cũ ... */
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU COPY STUDIO ---");
            studioService.copyStudio(txtStudio_CopyFromItemID.getText(), txtStudio_CopyToParentID.getText());
            System.out.println("--- HOÀN THÀNH COPY STUDIO ---");
        });
    }
    @FXML private void btnGenres_RunCopyClick() { /* ... như cũ ... */
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU COPY GENRES ---");
            genresService.copyGenres(txtGenres_CopyFromItemID.getText(), txtGenres_CopyToParentID.getText());
            System.out.println("--- HOÀN THÀNH COPY GENRES ---");
        });
    }
    @FXML private void btnPeople_RunCopyClick() { /* ... như cũ ... */
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU COPY PEOPLE ---");
            peopleService.copyPeople(txtPeople_CopyFromItemID.getText(), txtPeople_CopyToParentID.getText());
            System.out.println("--- HOÀN THÀNH COPY PEOPLE ---");
        });
    }
    @FXML private void btnTags_RunCopyClick() { /* ... như cũ ... */
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU COPY TAGS ---");
            tagService.copyTags(txtTags_CopyFromItemID.getText(), txtTags_CopyToParentID.getText());
            System.out.println("--- HOÀN THÀNH COPY TAGS ---");
        });
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN XÓA THEO THƯ MỤC (Không đổi) ---

    @FXML private void btnStudio_RunClearParentClick() { /* ... như cũ ... */
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA STUDIO THEO THƯ MỤC ---");
            studioService.clearStudioByParentID(txtStudio_ClearByParentID.getText());
            System.out.println("--- HOÀN THÀNH XÓA STUDIO THEO THƯ MỤC ---");
        });
    }
    @FXML private void btnGenres_RunClearParentClick() { /* ... như cũ ... */
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA GENRES THEO THƯ MỤC ---");
            genresService.clearGenresByParentID(txtGenres_ClearByParentID.getText());
            System.out.println("--- HOÀN THÀNH XÓA GENRES THEO THƯ MỤC ---");
        });
    }
    @FXML private void btnPeople_RunClearParentClick() { /* ... như cũ ... */
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA PEOPLE THEO THƯ MỤC ---");
            peopleService.clearPeopleByParentID(txtPeople_ClearByParentID.getText());
            System.out.println("--- HOÀN THÀNH XÓA PEOPLE THEO THƯ MỤC ---");
        });
    }
    @FXML private void btnTags_RunClearParentClick() { /* ... như cũ ... */
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA TAGS THEO THƯ MỤC ---");
            tagService.clearTagsByParentID(txtTags_ClearByParentID.getText());
            System.out.println("--- HOÀN THÀNH XÓA TAGS THEO THƯ MỤC ---");
        });
    }


    // --- CÁC HÀM XỬ LÝ SỰ KIỆN XÓA CỤ THỂ (Đã cập nhật) ---

    @FXML private void btnStudio_RunClearSpecificClick() {
        Toggle selectedToggle = studioToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            System.out.println("LỖI: Vui lòng chọn một Studio từ danh sách.");
            // Optional: Hiển thị Alert cho người dùng
            showAlert(Alert.AlertType.WARNING, "Chưa chọn Studio", "Vui lòng chọn một Studio để xóa.");
            return;
        }
        String idToClear = (String) selectedToggle.getUserData(); // Lấy ID từ UserData
        String nameSelected = ((ToggleButton)selectedToggle).getText(); // Lấy tên để hiển thị log

        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA STUDIO CỤ THỂ: " + nameSelected + " (ID: " + idToClear + ") ---");
            studioService.clearStudio(idToClear);
            System.out.println("--- HOÀN THÀNH XÓA STUDIO CỤ THỂ ---");
            // Tải lại danh sách sau khi xóa thành công
            Platform.runLater(this::loadStudioList);
        });
    }

    @FXML private void btnGenres_RunClearSpecificClick() {
        Toggle selectedToggle = genresToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            System.out.println("LỖI: Vui lòng chọn một Genre từ danh sách.");
            showAlert(Alert.AlertType.WARNING, "Chưa chọn Genre", "Vui lòng chọn một Genre để xóa.");
            return;
        }
        String nameToClear = (String) selectedToggle.getUserData(); // Genres dùng Name
        String nameSelected = ((ToggleButton)selectedToggle).getText();

        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA GENRES CỤ THỂ: " + nameSelected + " ---");
            genresService.clearGenres(nameToClear);
            System.out.println("--- HOÀN THÀNH XÓA GENRES CỤ THỂ ---");
            Platform.runLater(this::loadGenresList); // Tải lại
        });
    }

    @FXML private void btnPeople_RunClearSpecificClick() {
        Toggle selectedToggle = peopleToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            System.out.println("LỖI: Vui lòng chọn một People từ danh sách.");
            showAlert(Alert.AlertType.WARNING, "Chưa chọn People", "Vui lòng chọn một người để xóa.");
            return;
        }
        String idToClear = (String) selectedToggle.getUserData(); // Lấy ID
        String nameSelected = ((ToggleButton)selectedToggle).getText();

        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA PEOPLE CỤ THỂ: " + nameSelected + " (ID: " + idToClear + ") ---");
            peopleService.clearPeople(idToClear);
            System.out.println("--- HOÀN THÀNH XÓA PEOPLE CỤ THỂ ---");
            Platform.runLater(this::loadPeopleList); // Tải lại
        });
    }

    @FXML private void btnTags_RunClearSpecificClick() {
        Toggle selectedToggle = tagsToggleGroup.getSelectedToggle();
        if (selectedToggle == null || selectedToggle.getUserData() == null) {
            System.out.println("LỖI: Vui lòng chọn một Tag từ danh sách.");
            showAlert(Alert.AlertType.WARNING, "Chưa chọn Tag", "Vui lòng chọn một Tag để xóa.");
            return;
        }
        String nameToClear = (String) selectedToggle.getUserData(); // Tags dùng Name
        String nameSelected = ((ToggleButton)selectedToggle).getText();

        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA TAGS CỤ THỂ: " + nameSelected + " ---");
            tagService.clearTags(nameToClear);
            System.out.println("--- HOÀN THÀNH XÓA TAGS CỤ THỂ ---");
            Platform.runLater(this::loadTagsList); // Tải lại
        });
    }

    // --- Hàm Helper hiển thị Alert ---
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Không cần Header
        alert.setContentText(content);
        // Lấy Stage gốc để set Owner (nếu có thể)
        if (logTextArea != null && logTextArea.getScene() != null) {
            alert.initOwner(logTextArea.getScene().getWindow());
        }
        alert.showAndWait();
    }

}