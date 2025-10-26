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

import java.util.List;

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
}