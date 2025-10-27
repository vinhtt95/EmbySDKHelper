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
                    for (BaseItemDto each : result.getItems()) {
                        System.out.println(each.getName());
                    }
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


    public void setOriginalTitleForItem(String id, String userId) throws ApiException {

        BaseItemDto itemInfo = userLibraryServiceApi.getUsersByUseridItemsById(userId, id);
        boolean isUpdate = false;
//        itemInfo.setOriginalTitle("");

        if (itemInfo.getOriginalTitle() == null || itemInfo.getOriginalTitle().equals("")) {
            String fileName = itemInfo.getFileName();
            String name = fileName.substring(0, fileName.lastIndexOf('.'));

//            System.out.println(itemInfo.getOriginalTitle());

            String newName = normalizeFileName(name);
            if (newName.equals("")) {
                System.out.println("New name is empty for item path: " + fileName);
                newName = nameType2(itemInfo.getName());
                System.out.println(newName);
            }

//            System.out.println(newName);

            itemInfo.setOriginalTitle(newName);
            isUpdate = true;
        }

        if (itemInfo.getPremiereDate() == null) {
            if (itemInfo.getOriginalTitle().equals("")) {
                System.out.println("Original Title of video is emty: " + itemInfo.getName() + " ID: " + itemInfo.getId());
                return;
            }
            OffsetDateTime releaseDate = null;
            releaseDate = setDateRelease(itemInfo.getOriginalTitle());
            /*
             * Update theo tên gốc*/
            if (releaseDate != null) {
                itemInfo.setPremiereDate(releaseDate);
                isUpdate = true;
            }else{
                System.out.println("Release date is null for item title: " + itemInfo.getFileName());
                System.out.println("Release date is null for item ID: " + itemInfo.getId());
            }
            /*
             * Update theo tên title*/
            if (releaseDate == null) {

                releaseDate = setDateRelease(itemInfo.getName());

                if (releaseDate != null) {
                    itemInfo.setPremiereDate(releaseDate);
                    isUpdate = true;
                }
                else{
                    System.out.println("Release date is null for item name: " + itemInfo.getName());
                }
            }

            /*
             * Không tìm thấy date bằng cả tên gốc và title*/
            if (releaseDate == null) {
                System.out.println("- Chịu --");
            }
        }

        if (itemInfo.getProductionYear() != null) {
            itemInfo.setProductionYear(null);
            isUpdate = true;
        }

        if (itemInfo.getProductionYear() == null && itemInfo.getPremiereDate() != null) {
            int year = itemInfo.getPremiereDate().getYear();
            itemInfo.setProductionYear(year);
            isUpdate = true;
        }

        if (isUpdate) {
            ItemUpdateServiceApi itemUpdateServiceApi = new ItemUpdateServiceApi(Configuration.getDefaultApiClient());
            itemUpdateServiceApi.postItemsByItemid(itemInfo, id);
            isUpdate = false;
        }
    }

    private String nameType2(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Sử dụng regex để chèn dấu gạch giữa chữ cái và số
        return input.replaceAll("([a-zA-Z]+)(\\d+)", "$1-$2");
    }

    public static String normalizeFileName(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String cleanedInput = input.replaceAll("(?i)[.-](HD)$", "");

        // Loại bỏ các ký tự không mong muốn và chuẩn hóa định dạng
        String normalized = cleanedInput.replaceAll("([a-zA-Z])(\\d)", "$1-$2").replaceAll("(\\d)([a-zA-Z])", "$1-$2").replaceAll("[^a-zA-Z0-9]", "-") // Thay ký tự không phải chữ cái/số bằng dấu '-'
                .replaceAll("-+", "-") // Loại bỏ dấu '-' thừa
                .replaceAll("(?i)-4k$", "");    // Loại bỏ hậu tố '-4k' (không phân biệt hoa thường)

        // Tách phần chữ và số, sau đó chuẩn hóa
        String[] parts = normalized.split("-");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (part.matches("[a-zA-Z]+")) {
                result.append(part.toUpperCase()); // Chuyển phần chữ cái thành in hoa
            } else if (part.matches("\\d+")) {
                if (result.length() > 0) {
                    result.append("-");
                }
                result.append(part); // Giữ nguyên phần số
            }
        }

        return result.toString();
    }


    /**
     * Get date release with code
     * @param code
     * @return
     */
    public OffsetDateTime setDateRelease(String code) {
        String apiUrl = "http://localhost:8081/movies/movie/date/?movieCode=" + code;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                // Parse JSON response to extract "data"
                JSONObject jsonResponse = new JSONObject(response.toString());
                String dataValue = jsonResponse.optString("data", null);
                return OffsetDateTime.parse(dataValue);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}