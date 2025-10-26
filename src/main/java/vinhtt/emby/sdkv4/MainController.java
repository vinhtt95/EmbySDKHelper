package vinhtt.emby.sdkv4;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import vinhtt.emby.sdkv4.service.*; // Import tất cả service

import java.io.PrintStream;

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
    @FXML private TextField txtStudio_ClearSpecific;
    @FXML private Button btnStudio_RunClearSpecific;

    // Vùng Genres
    @FXML private TextField txtGenres_CopyFromItemID;
    @FXML private TextField txtGenres_CopyToParentID;
    @FXML private Button btnGenres_RunCopy;
    @FXML private TextField txtGenres_ClearByParentID;
    @FXML private Button btnGenres_RunClearParent;
    @FXML private TextField txtGenres_ClearSpecific;
    @FXML private Button btnGenres_RunClearSpecific;

    // Vùng People
    @FXML private TextField txtPeople_CopyFromItemID;
    @FXML private TextField txtPeople_CopyToParentID;
    @FXML private Button btnPeople_RunCopy;
    @FXML private TextField txtPeople_ClearByParentID;
    @FXML private Button btnPeople_RunClearParent;
    @FXML private TextField txtPeople_ClearSpecific;
    @FXML private Button btnPeople_RunClearSpecific;

    // Vùng Tags
    @FXML private TextField txtTags_CopyFromItemID;
    @FXML private TextField txtTags_CopyToParentID;
    @FXML private Button btnTags_RunCopy;
    @FXML private TextField txtTags_ClearByParentID;
    @FXML private Button btnTags_RunClearParent;
    @FXML private TextField txtTags_ClearSpecific;
    @FXML private Button btnTags_RunClearSpecific;

    // Khai báo các Service
    private StudioService studioService;
    private GenresService genresService;
    private PeopleService peopleService;
    private TagService tagService;
    // Chúng ta không cần ItemService ở đây, vì nó đã được "tiêm" vào các service kia
    // private ItemService itemService;

    // Thêm biến để giữ UserId
    private String userId;

    /**
     * Hàm này được LoginController gọi để truyền UserId vào.
     * Đây là nơi chúng ta khởi tạo tất cả các service.
     */
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
    }

    @FXML
    public void initialize() {
        // *** Phép màu ở đây ***
        // Chuyển hướng System.out đến logTextArea
        PrintStream ps = new PrintStream(new TextAreaOutputStream(logTextArea));
        System.setOut(ps);
        System.setErr(ps);

        // Xóa các hàm khởi tạo service khỏi đây,
        // vì chúng cần UserId (sẽ được gọi trong setUserId)
        // this.studioService = new StudioService();
        // this.genresService = new GenresService();
        // this.peopleService = new PeopleService();
        // this.tagService = new TagService();

        System.out.println("Main Controller đã khởi tạo. Đang chờ UserId từ Login...");
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
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA STUDIO CỤ THỂ ---");
            studioService.clearStudio(txtStudio_ClearSpecific.getText());
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
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA GENRES CỤ THỂ ---");
            genresService.clearGenres(txtGenres_ClearSpecific.getText());
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
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA PEOPLE CỤ THỂ ---");
            peopleService.clearPeople(txtPeople_ClearSpecific.getText());
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
        runTask(() -> {
            System.out.println("--- BẮT ĐẦU XÓA TAGS CỤ THỂ ---");
            tagService.clearTags(txtTags_ClearSpecific.getText());
            System.out.println("--- HOÀN THÀNH XÓA TAGS CỤ THỂ ---");
        });
    }

    /**
     * Một hàm tiện ích để chạy các tác vụ dài (như gọi API) trên một luồng riêng
     * để không làm đóng băng giao diện người dùng (GUI).
     * @param task Tác vụ cần chạy (sử dụng Lambda).
     */
    private void runTask(Runnable task) {
        // Kiểm tra xem service đã được khởi tạo chưa
        // (đề phòng trường hợp người dùng click quá nhanh)
        if (studioService == null || genresService == null || peopleService == null || tagService == null) {
            System.out.println("LỖI: Services chưa được khởi tạo. Vui lòng thử lại.");
            return;
        }

        // Tạo một luồng mới để chạy tác vụ
        Thread thread = new Thread(() -> {
            try {
                // Chạy tác vụ
                task.run();
            } catch (Exception e) {
                // Nếu có lỗi, in lỗi ra log (đã được chuyển hướng đến TextArea)
                System.out.println("\n!!! ĐÃ XẢY RA LỖI !!!");
                e.printStackTrace();
            }
        });

        // Bắt đầu luồng
        thread.start();
    }
}