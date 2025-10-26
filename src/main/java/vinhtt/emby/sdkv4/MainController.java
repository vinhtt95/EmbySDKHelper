package vinhtt.emby.sdkv4;

import embyclient.model.BaseItemDto;
import embyclient.model.UserLibraryTagItem;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import vinhtt.emby.sdkv4.service.*;

import java.io.PrintStream;
import java.util.List;

public class MainController {

    // Vùng Log
    @FXML
    private TextArea logTextArea;

    // Vùng Studio
    @FXML private TextField txtStudio_CopyFromItemID;
    @FXML private TextField txtStudio_CopyToParentID;
    @FXML private Button btnStudio_RunCopy;
    @FXML private TextField txtStudio_ClearByParentID;
    @FXML private Button btnStudio_RunClearParent;
    @FXML private ComboBox<BaseItemDto> cmbStudio_ClearSpecific; // THAY ĐỔI
    @FXML private Button btnStudio_ReloadList; // MỚI
    @FXML private Button btnStudio_RunClearSpecific;

    // Vùng Genres
    @FXML private TextField txtGenres_CopyFromItemID;
    @FXML private TextField txtGenres_CopyToParentID;
    @FXML private Button btnGenres_RunCopy;
    @FXML private TextField txtGenres_ClearByParentID;
    @FXML private Button btnGenres_RunClearParent;
    @FXML private ComboBox<BaseItemDto> cmbGenres_ClearSpecific; // THAY ĐỔI
    @FXML private Button btnGenres_ReloadList; // MỚI
    @FXML private Button btnGenres_RunClearSpecific;

    // Vùng People
    @FXML private TextField txtPeople_CopyFromItemID;
    @FXML private TextField txtPeople_CopyToParentID;
    @FXML private Button btnPeople_RunCopy;
    @FXML private TextField txtPeople_ClearByParentID;
    @FXML private Button btnPeople_RunClearParent;
    @FXML private ComboBox<BaseItemDto> cmbPeople_ClearSpecific; // THAY ĐỔI
    @FXML private Button btnPeople_ReloadList; // MỚI
    @FXML private Button btnPeople_RunClearSpecific;

    // Vùng Tags
    @FXML private TextField txtTags_CopyFromItemID;
    @FXML private TextField txtTags_CopyToParentID;
    @FXML private Button btnTags_RunCopy;
    @FXML private TextField txtTags_ClearByParentID;
    @FXML private Button btnTags_RunClearParent;
    @FXML private ComboBox<UserLibraryTagItem> cmbTags_ClearSpecific; // THAY ĐỔI
    @FXML private Button btnTags_ReloadList; // MỚI
    @FXML private Button btnTags_RunClearSpecific;

    // Khai báo các Service
    private StudioService studioService;
    private GenresService genresService;
    private PeopleService peopleService;
    private TagService tagService;

    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;

        ItemService itemService = new ItemService(this.userId);

        this.studioService = new StudioService(itemService);
        this.genresService = new GenresService(itemService);
        this.peopleService = new PeopleService(itemService);
        this.tagService = new TagService(itemService);

        System.out.println("Main Controller đã khởi tạo xong các service với UserId: " + userId);

        // Tải danh sách cho ComboBox lần đầu tiên
        setupComboBoxes();
        loadAllLists();
    }

    @FXML
    public void initialize() {
        PrintStream ps = new PrintStream(new TextAreaOutputStream(logTextArea));
        System.setOut(ps);
        System.setErr(ps);
        System.out.println("Main Controller đã khởi tạo. Đang chờ UserId từ Login...");
    }

    // --- CÀI ĐẶT VÀ TẢI DỮ LIỆU COMBOBOX ---

    private void setupComboBoxes() {
        // Cài đặt cách hiển thị tên cho ComboBox Studio
        setupBaseItemDtoComboBox(cmbStudio_ClearSpecific);

        // Cài đặt cách hiển thị tên cho ComboBox Genres
        setupBaseItemDtoComboBox(cmbGenres_ClearSpecific);

        // Cài đặt cách hiển thị tên cho ComboBox People
        setupBaseItemDtoComboBox(cmbPeople_ClearSpecific);

        // Cài đặt cách hiển thị tên cho ComboBox Tags (dùng UserLibraryTagItem)
        cmbTags_ClearSpecific.setConverter(new StringConverter<UserLibraryTagItem>() {
            @Override
            public String toString(UserLibraryTagItem item) {
                return (item == null) ? null : item.getName();
            }
            @Override
            public UserLibraryTagItem fromString(String string) { return null; }
        });
        cmbTags_ClearSpecific.setCellFactory(lv -> new ListCell<UserLibraryTagItem>() {
            @Override
            protected void updateItem(UserLibraryTagItem item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });
    }

    /** Helper chung để cài đặt ComboBox cho BaseItemDto (Studio, Genres, People) */
    private void setupBaseItemDtoComboBox(ComboBox<BaseItemDto> comboBox) {
        comboBox.setConverter(new StringConverter<BaseItemDto>() {
            @Override
            public String toString(BaseItemDto item) {
                return (item == null) ? null : item.getName();
            }
            @Override
            public BaseItemDto fromString(String string) { return null; }
        });
        comboBox.setCellFactory(lv -> new ListCell<BaseItemDto>() {
            @Override
            protected void updateItem(BaseItemDto item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });
    }

    private void loadAllLists() {
        loadStudioList();
        loadGenresList();
        loadPeopleList();
        loadTagsList();
    }

    // --- Các hàm tải danh sách ---

    @FXML private void btnStudio_ReloadListClick() { loadStudioList(); }
    private void loadStudioList() {
        runTask(() -> {
            System.out.println("Đang tải danh sách Studio...");
            List<BaseItemDto> list = studioService.getListStudios();
            Platform.runLater(() -> {
                cmbStudio_ClearSpecific.setItems(FXCollections.observableArrayList(list));
                System.out.println("Tải xong danh sách Studio. (" + list.size() + " mục)");
            });
        });
    }

    @FXML private void btnGenres_ReloadListClick() { loadGenresList(); }
    private void loadGenresList() {
        runTask(() -> {
            System.out.println("Đang tải danh sách Genres...");
            List<BaseItemDto> list = genresService.getListGenres();
            Platform.runLater(() -> {
                cmbGenres_ClearSpecific.setItems(FXCollections.observableArrayList(list));
                System.out.println("Tải xong danh sách Genres. (" + list.size() + " mục)");
            });
        });
    }

    @FXML private void btnPeople_ReloadListClick() { loadPeopleList(); }
    private void loadPeopleList() {
        runTask(() -> {
            System.out.println("Đang tải danh sách People...");
            List<BaseItemDto> list = peopleService.getListPeople();
            Platform.runLater(() -> {
                cmbPeople_ClearSpecific.setItems(FXCollections.observableArrayList(list));
                System.out.println("Tải xong danh sách People. (" + list.size() + " mục)");
            });
        });
    }

    @FXML private void btnTags_ReloadListClick() { loadTagsList(); }
    private void loadTagsList() {
        runTask(() -> {
            System.out.println("Đang tải danh sách Tags...");
            List<UserLibraryTagItem> list = tagService.getListTags();
            Platform.runLater(() -> {
                cmbTags_ClearSpecific.setItems(FXCollections.observableArrayList(list));
                System.out.println("Tải xong danh sách Tags. (" + list.size() + " mục)");
            });
        });
    }


    // --- XỬ LÝ SỰ KIỆN STUDIO ---

    @FXML
    private void btnStudio_RunCopyClick() {
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU COPY STUDIO ---");
            studioService.copyStudio(txtStudio_CopyFromItemID.getText(), txtStudio_CopyToParentID.getText());
            System.out.println("--- HOÀN THÀNH COPY STUDIO ---");
        });
    }

    @FXML
    private void btnStudio_RunClearParentClick() {
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA STUDIO THEO THƯ MỤC ---");
            studioService.clearStudioByParentID(txtStudio_ClearByParentID.getText());
            System.out.println("--- HOÀN THÀNH XÓA STUDIO THEO THƯ MỤC ---");
        });
    }

    @FXML
    private void btnStudio_RunClearSpecificClick() {
        BaseItemDto selected = cmbStudio_ClearSpecific.getValue();
        if (selected == null) {
            System.out.println("LỖI: Vui lòng chọn một Studio từ danh sách.");
            return;
        }
        String idToClear = selected.getId(); // Lấy ID

        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA STUDIO CỤ THỂ: " + selected.getName() + " (ID: " + idToClear + ") ---");
            studioService.clearStudio(idToClear);
            System.out.println("--- HOÀN THÀNH XÓA STUDIO CỤ THỂ ---");
        });
    }

    // --- XỬ LÝ SỰ KIỆN GENRES ---

    @FXML
    private void btnGenres_RunCopyClick() {
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU COPY GENRES ---");
            genresService.copyGenres(txtGenres_CopyFromItemID.getText(), txtGenres_CopyToParentID.getText());
            System.out.println("--- HOÀN THÀNH COPY GENRES ---");
        });
    }

    @FXML
    private void btnGenres_RunClearParentClick() {
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA GENRES THEO THƯ MỤC ---");
            genresService.clearGenresByParentID(txtGenres_ClearByParentID.getText());
            System.out.println("--- HOÀN THÀNH XÓA GENRES THEO THƯ MỤC ---");
        });
    }

    @FXML
    private void btnGenres_RunClearSpecificClick() {
        BaseItemDto selected = cmbGenres_ClearSpecific.getValue();
        if (selected == null) {
            System.out.println("LỖI: Vui lòng chọn một Genre từ danh sách.");
            return;
        }
        String nameToClear = selected.getName(); // Lấy TÊN

        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA GENRES CỤ THỂ: " + nameToClear + " ---");
            genresService.clearGenres(nameToClear);
            System.out.println("--- HOÀN THÀNH XÓA GENRES CỤ THỂ ---");
        });
    }

    // --- XỬ LÝ SỰ KIỆN PEOPLE ---

    @FXML
    private void btnPeople_RunCopyClick() {
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU COPY PEOPLE ---");
            peopleService.copyPeople(txtPeople_CopyFromItemID.getText(), txtPeople_CopyToParentID.getText());
            System.out.println("--- HOÀN THÀNH COPY PEOPLE ---");
        });
    }

    @FXML
    private void btnPeople_RunClearParentClick() {
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA PEOPLE THEO THƯ MỤC ---");
            peopleService.clearPeopleByParentID(txtPeople_ClearByParentID.getText());
            System.out.println("--- HOÀN THÀNH XÓA PEOPLE THEO THƯ MỤC ---");
        });
    }

    @FXML
    private void btnPeople_RunClearSpecificClick() {
        BaseItemDto selected = cmbPeople_ClearSpecific.getValue();
        if (selected == null) {
            System.out.println("LỖI: Vui lòng chọn một People từ danh sách.");
            return;
        }
        String idToClear = selected.getId(); // Lấy ID

        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA PEOPLE CỤ THỂ: " + selected.getName() + " (ID: " + idToClear + ") ---");
            peopleService.clearPeople(idToClear);
            System.out.println("--- HOÀN THÀNH XÓA PEOPLE CỤ THỂ ---");
        });
    }

    // --- XỬ LÝ SỰ KIỆN TAGS ---

    @FXML
    private void btnTags_RunCopyClick() {
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU COPY TAGS ---");
            tagService.copyTags(txtTags_CopyFromItemID.getText(), txtTags_CopyToParentID.getText());
            System.out.println("--- HOÀN THÀNH COPY TAGS ---");
        });
    }

    @FXML
    private void btnTags_RunClearParentClick() {
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA TAGS THEO THƯ MỤC ---");
            tagService.clearTagsByParentID(txtTags_ClearByParentID.getText());
            System.out.println("--- HOÀN THÀNH XÓA TAGS THEO THƯ MỤC ---");
        });
    }

    @FXML
    private void btnTags_RunClearSpecificClick() {
        UserLibraryTagItem selected = cmbTags_ClearSpecific.getValue();
        if (selected == null) {
            System.out.println("LỖI: Vui lòng chọn một Tag từ danh sách.");
            return;
        }
        String nameToClear = selected.getName(); // Lấy TÊN

        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA TAGS CỤ THỂ: " + nameToClear + " ---");
            tagService.clearTags(nameToClear);
            System.out.println("--- HOÀN THÀNH XÓA TAGS CỤ THỂ ---");
        });
    }

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
}