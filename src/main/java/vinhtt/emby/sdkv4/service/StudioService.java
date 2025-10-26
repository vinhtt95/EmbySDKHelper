package vinhtt.emby.sdkv4.service;

import embyclient.Configuration;
import embyclient.api.ItemsServiceApi;
import embyclient.api.StudiosServiceApi;
import embyclient.model.BaseItemDto;
import embyclient.model.NameLongIdPair;
import embyclient.model.QueryResultBaseItemDto;

import java.util.List;
import java.util.function.UnaryOperator;

public class StudioService {
    private StudiosServiceApi studiosServiceApi;
    private ItemService itemService; // Thêm dòng này

    // Sửa hàm khởi tạo
    public StudioService(ItemService itemService) {
        studiosServiceApi = new StudiosServiceApi(Configuration.getDefaultApiClient());
        this.itemService = itemService; // Gán ItemService
    }

    /**
     * @return Danh sách toàn bộ studio
     */
    public List<BaseItemDto> getListStudios() {
        // ... (Không thay đổi gì ở hàm này) ...
        Boolean recursive = true;

        if (studiosServiceApi != null) {
            try {
                QueryResultBaseItemDto listItems = studiosServiceApi.getStudios(
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
                        null,    //startIndex
                        null,    //limit
                        recursive,    //recursive
                        null,    //searchTerm
                        null,    //sortOrder
                        null,    //parentId
                        null,    //fields
                        null,    //excludeItemTypes
                        null,    //includeItemTypes
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
                        "SortName",    //sortBy
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

                if (listItems.getItems().isEmpty()) {
                    System.out.println("Empty Studios");
                }

                if (!listItems.getItems().isEmpty()) {
                    return listItems.getItems();
                }

            } catch (embyclient.ApiException e) {
                System.out.println("Error fetching studios: " + e.getMessage());
            }
        } else {
            System.out.println("StudiosServiceApi is null");
        }

        return null;
    }

    /**
     * Lấy danh sách các item con có chung studioID
     *
     * @param studioId
     * @param startIndex
     * @param limit
     * @param recursive  true thì lấy đệ quy các item, false: lấy các folder/item trực tiếp
     * @return
     */
    public List<BaseItemDto> getListItemByStudioId(String studioId, Integer startIndex, Integer limit, boolean recursive) {
        // ... (Không thay đổi gì ở hàm này) ...
        ItemsServiceApi itemsServiceApi = new ItemsServiceApi(Configuration.getDefaultApiClient());

        if (studiosServiceApi != null) {
            try {
                QueryResultBaseItemDto listItems = itemsServiceApi.getItems(
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
                        "Ascending",    //sortOrder
                        null,    //parentId
                        null,    //fields
                        null,    //excludeItemTypes
                        "Movie,Series,Video,Game",    //includeItemTypes
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
                        studioId,    //studioIds
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

                if (listItems.getItems().isEmpty()) {
                    System.out.println("Empty Studios");
                }

                if (!listItems.getItems().isEmpty()) {

                    return listItems.getItems();
                }

            } catch (embyclient.ApiException e) {
                System.out.println("Error fetching studios: " + e.getMessage());
            }
        } else {
            System.out.println("StudiosServiceApi is null");
        }

        return null;
    }


    /**
     * Xóa studio theo ID
     * @param studioId ID của Studio cần xóa
     */
    public void clearStudio(String studioId) {
        // Bỏ dòng này
        // ItemService itemService = new ItemService();

        List<BaseItemDto> listStudioBy = getListItemByStudioId(studioId, null, null, true);

        if (listStudioBy != null) {
            BaseItemDto itemDto = null;
            for (BaseItemDto eachItemOfStudio : listStudioBy) {
                // Dùng this.itemService
                itemDto = this.itemService.getInforItem(eachItemOfStudio.getId());

                if (itemDto != null) {
                    List<NameLongIdPair> listStudios = itemDto.getStudios();


                    listStudios.removeIf(eachStudio -> eachStudio.getId().equals(Long.parseLong(studioId)));

//                    itemDto.getStudios().removeIf(studio -> studio.getId() != null && studio.getId().equals(studioId));

                    // Dùng this.itemService
                    if(this.itemService.updateInforItem(eachItemOfStudio.getId(),itemDto)) {
                        System.out.println("Update success "+eachItemOfStudio.getName());
                    }
                }
            }
        }
    }

    /**
     * Thay thế toàn bộ studio cho các item con của parentID
     * @param itemCopyID ID của item mẫu
     * @param parentID ID của folder chứa các item cần sao chép studio từ item mẫu
     */
    public void copyStudio(String itemCopyID, String parentID) {
        // Bỏ dòng này
        // ItemService itemService = new ItemService();
        // Dùng this.itemService
        BaseItemDto itemCopy = this.itemService.getInforItem(itemCopyID);

        if(itemCopy == null){
            System.out.println("Not found item copy");
            return;
        }else{
            System.out.println("List Studio of Item copy:");
            List<NameLongIdPair> listStudoItemCopy = itemCopy.getStudios();
            for (NameLongIdPair eachStudio : listStudoItemCopy) {
                System.out.println("ID: " + eachStudio.getId() + " Name: " + eachStudio.getName());
            }
        }

        // Dùng this.itemService
        List<BaseItemDto> listItemPaste = this.itemService.getListItemByParentID(parentID, null, null, true);
        if (listItemPaste == null) {
            System.out.println("Not found item paste");
            return;
        }

        BaseItemDto itemPaste = null;
        for (BaseItemDto eachItemPaste : listItemPaste) {
            System.out.println("ID: " + eachItemPaste.getId()+ " Name: " + eachItemPaste.getName());
            // Dùng this.itemService
            itemPaste = this.itemService.getInforItem(eachItemPaste.getId());

            List<NameLongIdPair> listStudioItemPaste = itemPaste.getStudios();

            itemPaste.getStudios().clear();

            listStudioItemPaste.addAll(itemCopy.getStudios());

            for (NameLongIdPair eachStudioPaste : itemPaste.getStudios()) {
                System.out.println(eachStudioPaste.toString());
            }

            // Dùng this.itemService
            if(this.itemService.updateInforItem(itemPaste.getId(),itemPaste)) {
                System.out.println("Update success "+eachItemPaste.getName());
            }
        }
    }

    /**
     * Xóa toàn bộ studio cho các item con của parenetID
     * @param parentID
     */
    public void clearStudioByParentID(String parentID) {
        // Bỏ dòng này
        // ItemService itemService = new ItemService();

        // Dùng this.itemService
        List<BaseItemDto> listItem = this.itemService.getListItemByParentID(parentID, null, null, true);
        if (listItem == null) {
            System.out.println("Not found item");
            return;
        }

        BaseItemDto itemPaste = null;
        for (BaseItemDto eachItem : listItem) {
            System.out.println("ID: " + eachItem.getId()+ " Name: " + eachItem.getName());
            // Dùng this.itemService
            itemPaste = this.itemService.getInforItem(eachItem.getId());
            itemPaste.getStudios().clear();

            // Dùng this.itemService
            if(this.itemService.updateInforItem(itemPaste.getId(),itemPaste)) {
                System.out.println("Update success "+eachItem.getName());
            }
        }
    }
}