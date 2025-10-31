package vinhtt.emby.sdkv4.service;

import embyclient.ApiException;
import embyclient.Configuration;
import embyclient.api.ItemUpdateServiceApi;
import embyclient.api.ItemsServiceApi;
import embyclient.api.UserLibraryServiceApi;
import embyclient.model.BaseItemDto;
import embyclient.model.QueryResultBaseItemDto;
// Bỏ AuthenUserService đi
// import vinhtt.emby.sdkv4.AuthenUserService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class ItemService {
    private ItemsServiceApi itemsServiceApi;
    // Bỏ AuthenUserService
    // private AuthenUserService authenUserService;
    private UserLibraryServiceApi userLibraryServiceApi;
    private String userId; // Thêm dòng này

    // Sửa hàm khởi tạo
    public ItemService(String userId) {
        this.itemsServiceApi = new ItemsServiceApi(Configuration.getDefaultApiClient());
        this.userId = userId; // Gán userId

        // Bỏ 2 dòng tự đăng nhập
        // this.authenUserService = new AuthenUserService(Configuration.getDefaultApiClient());
        // authenUserService.login();

        userLibraryServiceApi = new UserLibraryServiceApi(Configuration.getDefaultApiClient());
    }

    /**
     * @param parentID:  ID của item cha
     * @param startIndex
     * @param limit
     * @param recursive: true thì lấy đệ quy các item, false: lấy các folder/item trực tiếp
     * @return
     */
    public List<BaseItemDto> getListItemByParentID(String parentID, Integer startIndex, Integer limit, boolean recursive) {
        if (itemsServiceApi != null) {
            try {
                QueryResultBaseItemDto result = itemsServiceApi.getItems(
                        null,    //artistType
                        null,    //maxOfficialRating
                        null,    //hasThemeSong
                        null,    //hasThemeVideo
                        null,    //hasSubtitles
                        null,    //hasSpecialFeature
                        null,    //hasTrailer
                        null,    //isSpecialSeason
                        null,    //adjacentTo
                        null,    //startItemId
                        null,    //minIndexNumber
                        null,    //minStartDate
                        null,    //maxStartDate
                        null,    //minEndDate
                        null,    //maxEndDate
                        null,    //minPlayers
                        null,    //maxPlayers
                        null,    //parentIndexNumber
                        null,    //hasParentalRating
                        null,    //isHD
                        null,    //isUnaired
                        null,    //minCommunityRating
                        null,    //minCriticRating
                        null,    //airedDuringSeason
                        null,    //minPremiereDate
                        null,    //minDateLastSaved
                        null,    //minDateLastSavedForUser
                        null,    //maxPremiereDate
                        null,    //hasOverview
                        null,    //hasImdbId
                        null,    //hasTmdbId
                        null,    //hasTvdbId
                        null,    //excludeItemIds
                        startIndex,    //startIndex
                        limit,    //limit
                        recursive,    //recursive
                        null,    //searchTerm
                        null,    //sortOrder
                        parentID,    //parentId
                        null,    //fields
                        null,    //excludeItemTypes
                        "Movie",    //includeItemTypes
                        null,    //anyProviderIdEquals
                        null,    //filters
                        null,    //isFavorite
                        null,    //isMovie
                        null,    //isSeries
                        null,    //isFolder
                        null,    //isNews
                        null,    //isKids
                        null,    //isSports
                        null,    //isNew
                        null,    //isPremiere
                        null,    //isNewOrPremiere
                        null,    //isRepeat
                        null,    //projectToMedia
                        null,    //mediaTypes
                        null,    //imageTypes
                        null,    //sortBy
                        null,    //isPlayed
                        null,    //genres
                        null,    //officialRatings
                        null,    //tags
                        null,    //excludeTags
                        null,    //years
                        null,    //enableImages
                        null,    //enableUserData
                        null,    //imageTypeLimit
                        null,    //enableImageTypes
                        null,    //person
                        null,    //personIds
                        null,    //personTypes
                        null,    //studios
                        null,    //studioIds
                        null,    //artists
                        null,    //artistIds
                        null,    //albums
                        null,    //ids
                        null,    //videoTypes
                        null,    //containers
                        null,    //audioCodecs
                        null,    //audioLayouts
                        null,    //videoCodecs
                        null,    //extendedVideoTypes
                        null,    //subtitleCodecs
                        null,    //path
                        null,    //userId
                        null,    //minOfficialRating
                        null,    //isLocked
                        null,    //isPlaceHolder
                        null,    //hasOfficialRating
                        null,    //groupItemsIntoCollections
                        null,    //is3D
                        null,    //seriesStatus
                        null,    //nameStartsWithOrGreater
                        null,    //artistStartsWithOrGreater
                        null,    //albumArtistStartsWithOrGreater
                        null,    //nameStartsWith
                        null    //nameLessThan
                );

                if (result.getItems().isEmpty()) {
                    System.out.println("Không có IDParent: " + parentID + ", startIndex: " + startIndex + ", limit: " + limit + ", recursive: " + recursive);
                } else {
                    // Sửa lại: Không in ra đây nữa, trả về list cho controller xử lý
                    // for (BaseItemDto each : result.getItems()) {
                    //     System.out.println(each.getName());
                    // }
                    return result.getItems();
                }
            } catch (ApiException e) {
                System.out.println(e.getMessage());
            }

        } else {
            System.out.println("ItemsServiceApi is null");
        }

        return null;
    }

    public BaseItemDto getInforItem(String itemId) {
        try {
            // Sửa dòng này để dùng this.userId
            BaseItemDto itemInfo = userLibraryServiceApi.getUsersByUseridItemsById(this.userId, itemId);

            if (itemInfo != null) {
                return  itemInfo;
            }
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }

        return null;

    }

    public boolean updateInforItem(String itemID, BaseItemDto newInfoItem) {

        ItemUpdateServiceApi itemUpdateServiceApi = new ItemUpdateServiceApi(Configuration.getDefaultApiClient());
        try {
            itemUpdateServiceApi.postItemsByItemid(newInfoItem, itemID);
            return true;
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }


    /**
     * Xử lý logic đặt OriginalTitle và PremiereDate cho một item.
     * @param itemInfo Item đã được fetch full info.
     * @return true nếu có thay đổi, false nếu không.
     */
    public boolean checkAndProcessItemTitleAndDate(BaseItemDto itemInfo) {
        boolean isUpdate = false;
        String originalTitle = itemInfo.getOriginalTitle();

        // 1. Xử lý Original Title
        if (true || originalTitle == null || originalTitle.equals("")) {
            String fileName = itemInfo.getFileName();
            if(fileName == null || fileName.isEmpty()) {
                System.out.println("Bỏ qua item (không có filename): " + itemInfo.getName());
                return false;
            }

            String name = fileName.substring(0, fileName.lastIndexOf('.'));
            String newName = normalizeFileName(name);

            if (newName.equals("")) {
                System.out.println("New name (từ filename) rỗng cho item: " + fileName);
                newName = nameType2(itemInfo.getName()); // Thử fallback về item name
                System.out.println("Fallback về item name: " + newName);
            }

            itemInfo.setOriginalTitle(newName);
            originalTitle = newName; // Cập nhật biến local để dùng cho bước 2
            isUpdate = true;
            System.out.println("Đã set OriginalTitle: " + newName + " cho item: " + itemInfo.getName());
        }

        // 2. Xử lý Premiere Date
        if (itemInfo.getPremiereDate() == null) {
            if (originalTitle == null || originalTitle.equals("")) {
                System.out.println("Original Title rỗng, không thể lấy ngày release cho item: " + itemInfo.getName() + " ID: " + itemInfo.getId());
                // Không return, vẫn có thể xử lý ProductionYear
            } else {
                OffsetDateTime releaseDate = setDateRelease(originalTitle); // Thử lấy theo OriginalTitle

                if (releaseDate == null) { // Nếu thất bại, thử lấy theo Item Name
                    System.out.println("Không tìm thấy ngày release cho code: " + originalTitle + ". Thử với Item Name: " + itemInfo.getName());
                    releaseDate = setDateRelease(itemInfo.getName());
                }

                if (releaseDate != null) {
                    itemInfo.setPremiereDate(releaseDate);
                    isUpdate = true;
                    System.out.println("Đã set PremiereDate: " + releaseDate + " cho item: " + itemInfo.getName());
                } else {
                    System.out.println("Không tìm thấy ngày release cho cả OriginalTitle và Name: " + itemInfo.getName());
                }
            }
        }

        // 3. Xử lý Production Year (Luôn chạy, để đồng bộ)
        Integer currentYear = itemInfo.getProductionYear();
        Integer yearFromPremiere = null;

        if (itemInfo.getPremiereDate() != null) {
            yearFromPremiere = itemInfo.getPremiereDate().getYear();
        }

        if (yearFromPremiere != null) {
            if (currentYear == null || !currentYear.equals(yearFromPremiere)) {
                itemInfo.setProductionYear(yearFromPremiere);
                isUpdate = true;
                System.out.println("Đã set/update ProductionYear: " + yearFromPremiere + " cho item: " + itemInfo.getName());
            }
        } else if (currentYear != null) {
            // Nếu không có ngày premiere mà lại có năm sản xuất, có thể xóa đi? (Tùy logic)
            // Tạm thời giữ nguyên logic cũ: nếu có year thì set null (logic này hơi lạ, nhưng tôi giữ)
            // itemInfo.setProductionYear(null);
            // isUpdate = true;
            // System.out.println("Đã XÓA ProductionYear (vì không có PremiereDate?): " + itemInfo.getName());
        }

        // Logic cũ của đồng chí:
        // if (itemInfo.getProductionYear() != null) {
        //     itemInfo.setProductionYear(null);
        //     isUpdate = true;
        // }
        // if (itemInfo.getProductionYear() == null && itemInfo.getPremiereDate() != null) {
        //     int year = itemInfo.getPremiereDate().getYear();
        //     itemInfo.setProductionYear(year);
        //     isUpdate = true;
        // }


        return isUpdate;
    }


    private String nameType2(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Sử dụng regex để chèn dấu gạch giữa chữ cái và số
        return input.replaceAll("([a-zA-Z]+)(\\d+)", "$1-$2");
    }

    private static final Pattern NORMALIZE_PATTERN =
            Pattern.compile("^[^a-zA-Z]*([a-zA-Z]+)[^a-zA-Z0-9]*(\\d+)");

    /**
     * Chuẩn hóa tên file về định dạng [CHỮ]-[SỐ]
     * (ví dụ: "ABC-123")
     */
    public static String normalizeFileName(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Tạo một Matcher để tìm kiếm dựa trên pattern
        Matcher matcher = NORMALIZE_PATTERN.matcher(input);

        // Kiểm tra xem có tìm thấy sự trùng khớp không
        if (matcher.find()) {
            // matcher.group(1) là phần ([a-zA-Z]+)
            String letters = matcher.group(1);
            // matcher.group(2) là phần (\d+)
            String numbers = matcher.group(2);

            // Trả về chuỗi đã chuẩn hóa theo định dạng [CHỮ]-[SỐ]
            return letters.toUpperCase() + "-" + numbers;
        }

        // Nếu không tìm thấy mẫu nào khớp (ví dụ: "chỉ chữ", "chỉ số", "123-ABC")
        // trả về chuỗi rỗng để đảm bảo tính nghiêm ngặt của định dạng.
        return "";
    }


    /**
     * Get date release with code
     * @param code
     * @return
     */
    private OffsetDateTime setDateRelease(String code) {
        // API này là của đồng chí, tôi giữ nguyên
        String apiUrl = "http://localhost:8081/movies/movie/date/?movieCode=" + code;
        HttpURLConnection connection = null; // Khai báo bên ngoài để đóng trong finally
        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000); // Thêm timeout
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Try-with-resources để tự động đóng BufferedReader
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    // Parse JSON response to extract "data"
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String dataValue = jsonResponse.optString("data", null);
                    if (dataValue != null && !dataValue.equals("null")) {
                        return OffsetDateTime.parse(dataValue);
                    } else {
                        return null; // API trả về data: null
                    }
                }
            } else {
                // System.out.println("API call failed for code: " + code + ". Response: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            // System.out.println("Error calling API for code: " + code + " - " + e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect(); // Đảm bảo đóng connection
            }
        }
    }

    /**
     * (HÀM MỚI) Tìm item con đầu tiên trong một ParentID khớp với OriginalTitle.
     * LƯU Ý: Hàm này có thể chậm (N+1) nếu thư mục cha có RẤT NHIỀU item,
     * vì phải getInfo cho từng item để kiểm tra OriginalTitle.
     *
     * @param parentId ID thư mục cha để tìm kiếm bên trong.
     * @param originalTitle OriginalTitle cần tìm.
     * @return BaseItemDto (full info) của item tìm thấy, hoặc null.
     */
    public BaseItemDto findItemByOriginalTitle(String parentId, String originalTitle) {
        if (originalTitle == null || originalTitle.isEmpty() || parentId == null || parentId.isEmpty()) {
            return null;
        }

        // 1. Lấy danh sách item con (chỉ là stub, không có OriginalTitle)
        List<BaseItemDto> childItems = getListItemByParentID(parentId, null, null, true);
        if (childItems == null || childItems.isEmpty()) {
            return null;
        }

        // 2. Lặp qua và get full info để kiểm tra
        for (BaseItemDto stubItem : childItems) {
            BaseItemDto fullItem = getInforItem(stubItem.getId());
            if (fullItem != null && originalTitle.toUpperCase().equals(fullItem.getOriginalTitle().toUpperCase())) {
                return fullItem; // Tìm thấy!
            }
        }

        return null; // Không tìm thấy
    }

    /**
     * (HÀM MỚI) Cập nhật một item trên server (itemToUpdate)
     * bằng metadata từ item trong file (itemFromFile).
     * Chỉ copy các trường metadata, giữ nguyên ID và các thông tin hệ thống.
     *
     * @param itemOnServer Item gốc trên server (sẽ bị ghi đè metadata).
     * @param itemFromFile Item deserialize từ JSON (chỉ đọc metadata).
     * @return true nếu update API thành công.
     */
    public boolean updateItemFromJson(BaseItemDto itemOnServer, BaseItemDto itemFromFile) {
        if (itemOnServer == null || itemFromFile == null) {
            return false;
        }

        // Lấy ID gốc của server
        String serverId = itemOnServer.getId();

        // --- BẮT ĐẦU SAO CHÉP METADATA ---
        itemOnServer.setName(itemFromFile.getName());
        itemOnServer.setOriginalTitle(itemFromFile.getOriginalTitle());
        itemOnServer.setPremiereDate(itemFromFile.getPremiereDate());
        itemOnServer.setProductionYear(itemFromFile.getProductionYear());
        itemOnServer.setSortName(itemFromFile.getSortName());
        itemOnServer.setOverview(itemFromFile.getOverview());

        // Xóa list cũ, thêm list mới (Đây là cách copy chuẩn)
        itemOnServer.getStudios().clear();
        if (itemFromFile.getStudios() != null) {
            itemOnServer.getStudios().addAll(itemFromFile.getStudios());
        }

        itemOnServer.getGenreItems().clear();
        if (itemFromFile.getGenreItems() != null) {
            itemOnServer.getGenreItems().addAll(itemFromFile.getGenreItems());
        }

        itemOnServer.getPeople().clear();
        if (itemFromFile.getPeople() != null) {
            itemOnServer.getPeople().addAll(itemFromFile.getPeople());
        }

        itemOnServer.getTagItems().clear();
        if (itemFromFile.getTagItems() != null) {
            itemOnServer.getTagItems().addAll(itemFromFile.getTagItems());
        }
        // --- KẾT THÚC SAO CHÉP ---

        // Gọi API update bằng ID gốc của server
        return updateInforItem(serverId, itemOnServer);
    }
}