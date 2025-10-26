package vinhtt.emby.sdkv4.service;

import embyclient.Configuration;
import embyclient.api.GenresServiceApi;
import embyclient.api.ItemsServiceApi;
import embyclient.model.BaseItemDto;
import embyclient.model.NameLongIdPair;
import embyclient.model.QueryResultBaseItemDto;

import java.util.ArrayList;
import java.util.List;

public class GenresService {

    private GenresServiceApi genresServiceApi;
    private ItemService itemService; // Thêm dòng này

    // Sửa hàm khởi tạo
    public GenresService(ItemService itemService) {
        genresServiceApi = new GenresServiceApi(Configuration.getDefaultApiClient());
        this.itemService = itemService; // Gán ItemService
    }

    /**
     * Lấy danh sách các genres
     * @return
     */
    public  List<BaseItemDto> getListGenres() {
        // ... (Không thay đổi gì ở hàm này) ...
        List<BaseItemDto> result = new ArrayList<>();

        if (genresServiceApi != null) {
            try {
                QueryResultBaseItemDto listItems = genresServiceApi.getGenres(
                        null,	//artistType
                        null,	//maxOfficialRating
                        null,	//hasThemeSong
                        null,	//hasThemeVideo
                        null,	//hasSubtitles
                        null,	//hasSpecialFeature
                        null,	//hasTrailer
                        null,	//isSpecialSeason
                        null,	//adjacentTo
                        null,	//startItemId
                        null,	//minIndexNumber
                        null,	//minStartDate
                        null,	//maxStartDate
                        null,	//minEndDate
                        null,	//maxEndDate
                        null,	//minPlayers
                        null,	//maxPlayers
                        null,	//parentIndexNumber
                        null,	//hasParentalRating
                        null,	//isHD
                        null,	//isUnaired
                        null,	//minCommunityRating
                        null,	//minCriticRating
                        null,	//airedDuringSeason
                        null,	//minPremiereDate
                        null,	//minDateLastSaved
                        null,	//minDateLastSavedForUser
                        null,	//maxPremiereDate
                        null,	//hasOverview
                        null,	//hasImdbId
                        null,	//hasTmdbId
                        null,	//hasTvdbId
                        null,	//excludeItemIds
                        null,	//startIndex
                        null,	//limit
                        null,	//recursive
                        null,	//searchTerm
                        null,	//sortOrder
                        null,	//parentId
                        null,	//fields
                        null,	//excludeItemTypes
                        null,	//includeItemTypes
                        null,	//anyProviderIdEquals
                        null,	//filters
                        null,	//isFavorite
                        null,	//isMovie
                        null,	//isSeries
                        null,	//isFolder
                        null,	//isNews
                        null,	//isKids
                        null,	//isSports
                        null,	//isNew
                        null,	//isPremiere
                        null,	//isNewOrPremiere
                        null,	//isRepeat
                        null,	//projectToMedia
                        null,	//mediaTypes
                        null,	//imageTypes
                        null,	//sortBy
                        null,	//isPlayed
                        null,	//genres
                        null,	//officialRatings
                        null,	//tags
                        null,	//excludeTags
                        null,	//years
                        null,	//enableImages
                        null,	//enableUserData
                        null,	//imageTypeLimit
                        null,	//enableImageTypes
                        null,	//person
                        null,	//personIds
                        null,	//personTypes
                        null,	//studios
                        null,	//studioIds
                        null,	//artists
                        null,	//artistIds
                        null,	//albums
                        null,	//ids
                        null,	//videoTypes
                        null,	//containers
                        null,	//audioCodecs
                        null,	//audioLayouts
                        null,	//videoCodecs
                        null,	//extendedVideoTypes
                        null,	//subtitleCodecs
                        null,	//path
                        null,	//userId
                        null,	//minOfficialRating
                        null,	//isLocked
                        null,	//isPlaceHolder
                        null,	//hasOfficialRating
                        null,	//groupItemsIntoCollections
                        null,	//is3D
                        null,	//seriesStatus
                        null,	//nameStartsWithOrGreater
                        null,	//artistStartsWithOrGreater
                        null,	//albumArtistStartsWithOrGreater
                        null,	//nameStartsWith
                        null	//nameLessThan
                );
                /*
                 * List trống*/
                if (listItems.getItems().isEmpty()) {
                    System.out.println("Emty Genres");
                }

                /*
                 * List có item trả về*/
                if (!listItems.getItems().isEmpty()) {

                    return listItems.getItems();
                }

            } catch (Exception e) {

            }
        }else{
            System.out.println("GenresServiceApi is null");
        }

        return result;
    }

    /**
     * Lấy danh sách các item con có chung genres
     * @param nameGenres Tên genres chung
     * @param startIndex
     * @param limit
     * @param recursive
     * @return
     */
    public List<BaseItemDto> getListItemByGenreId(String nameGenres, Integer startIndex, Integer limit, boolean recursive) {
        // ... (Không thay đổi gì ở hàm này) ...
        ItemsServiceApi itemsServiceApi = new ItemsServiceApi(Configuration.getDefaultApiClient());

        if (genresServiceApi != null) {
            try {
                QueryResultBaseItemDto listItems = itemsServiceApi.getItems(
                        null,	//artistType
                        null,	//maxOfficialRating
                        null,	//hasThemeSong
                        null,	//hasThemeVideo
                        null,	//hasSubtitles
                        null,	//hasSpecialFeature
                        null,	//hasTrailer
                        null,	//isSpecialSeason
                        null,	//adjacentTo
                        null,	//startItemId
                        null,	//minIndexNumber
                        null,	//minStartDate
                        null,	//maxStartDate
                        null,	//minEndDate
                        null,	//maxEndDate
                        null,	//minPlayers
                        null,	//maxPlayers
                        null,	//parentIndexNumber
                        null,	//hasParentalRating
                        null,	//isHD
                        null,	//isUnaired
                        null,	//minCommunityRating
                        null,	//minCriticRating
                        null,	//airedDuringSeason
                        null,	//minPremiereDate
                        null,	//minDateLastSaved
                        null,	//minDateLastSavedForUser
                        null,	//maxPremiereDate
                        null,	//hasOverview
                        null,	//hasImdbId
                        null,	//hasTmdbId
                        null,	//hasTvdbId
                        null,	//excludeItemIds
                        startIndex,	//startIndex
                        limit,	//limit
                        recursive,	//recursive
                        null,	//searchTerm
                        null,	//sortOrder
                        null,	//parentId
                        null,	//fields
                        null,	//excludeItemTypes
                        "Movie, Series, Video, Game, MusicAlbum",	//includeItemTypes
                        null,	//anyProviderIdEquals
                        null,	//filters
                        null,	//isFavorite
                        null,	//isMovie
                        null,	//isSeries
                        null,	//isFolder
                        null,	//isNews
                        null,	//isKids
                        null,	//isSports
                        null,	//isNew
                        null,	//isPremiere
                        null,	//isNewOrPremiere
                        null,	//isRepeat
                        null,	//projectToMedia
                        null,	//mediaTypes
                        null,	//imageTypes
                        null,	//sortBy
                        null,	//isPlayed
                        nameGenres,	//genres
                        null,	//officialRatings
                        null,	//tags
                        null,	//excludeTags
                        null,	//years
                        null,	//enableImages
                        null,	//enableUserData
                        null,	//imageTypeLimit
                        null,	//enableImageTypes
                        null,	//person
                        null,	//personIds
                        null,	//personTypes
                        null,	//studios
                        null,	//studioIds
                        null,	//artists
                        null,	//artistIds
                        null,	//albums
                        null,	//ids
                        null,	//videoTypes
                        null,	//containers
                        null,	//audioCodecs
                        null,	//audioLayouts
                        null,	//videoCodecs
                        null,	//extendedVideoTypes
                        null,	//subtitleCodecs
                        null,	//path
                        null,	//userId
                        null,	//minOfficialRating
                        null,	//isLocked
                        null,	//isPlaceHolder
                        null,	//hasOfficialRating
                        null,	//groupItemsIntoCollections
                        null,	//is3D
                        null,	//seriesStatus
                        null,	//nameStartsWithOrGreater
                        null,	//artistStartsWithOrGreater
                        null,	//albumArtistStartsWithOrGreater
                        null,	//nameStartsWith
                        null	//nameLessThan
                );
                if (listItems.getItems().isEmpty()) {
                    System.out.println("Empty Genres");
                }

                if (!listItems.getItems().isEmpty()) {

                    return listItems.getItems();
                }
            } catch (embyclient.ApiException e) {
                System.out.println("Error fetching Genres: " + e.getMessage());
            }
        } else {
            System.out.println("GenresAPI is null");
        }
        return null;
    }

    /**
     * Xóa genres theo tên
     * Bằng xách xóa genres trong các item có chung genres
     * @param nameGenres
     */
    public void clearGenres(String nameGenres) {

        List<BaseItemDto> listItem = getListItemByGenreId(nameGenres, null, null, true);

        if(listItem.isEmpty()){
            System.out.println("Not found item");
            return;
        }

        // Bỏ dòng này
        // ItemService itemService = new ItemService();

        BaseItemDto item = null;
        for (BaseItemDto eachItem : listItem) {
            // Dùng this.itemService
            item = this.itemService.getInforItem(eachItem.getId());

            if (item != null) {
//                System.out.println(item.getGenreItems());
                item.getGenreItems().removeIf(genre -> genre.getName() != null && genre.getName().equals(nameGenres));

                // Dùng this.itemService
                if (this.itemService.updateInforItem(eachItem.getId(), item)) {
                    System.out.println("Update success " + eachItem.getName());
                }
            }
        }
    }

    /**
     * Thay thế toàn bộ genres cho các item con của parentID bằng genres mẫu
     * @param itemCopyID ID của item mẫu
     * @param parentID
     */
    public void copyGenres(String itemCopyID, String parentID) {
        // Bỏ dòng này
        // ItemService itemService = new ItemService();
        // Dùng this.itemService
        BaseItemDto itemCopy = this.itemService.getInforItem(itemCopyID);

        if(itemCopy == null){
            System.out.println("Not found item copy");
            return;
        }else{
            System.out.println("List Genres of Item copy:");
            List<NameLongIdPair> listGenresItemCopy = itemCopy.getGenreItems();
            for (NameLongIdPair eachGenres : listGenresItemCopy) {
                System.out.println("ID: " + eachGenres.getId() + " Name: " + eachGenres.getName());
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

            List<NameLongIdPair> listGenresItemPaste = itemPaste.getGenreItems();

            itemPaste.getGenreItems().clear();

            listGenresItemPaste.addAll(itemCopy.getGenreItems());

            for (NameLongIdPair eachGenresPaste : itemPaste.getGenreItems()) {
                System.out.println(eachGenresPaste.toString());
            }

            // Dùng this.itemService
            if(this.itemService.updateInforItem(itemPaste.getId(),itemPaste)) {
                System.out.println("Update success "+eachItemPaste.getName());
            }
        }
    }

    /**
     * Xóa hết genres cho các item con của parentID
     * @param parentID
     */
    public void clearGenresByParentID(String parentID) {
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
            itemPaste.getGenreItems().clear();

            // Dùng this.itemService
            if(this.itemService.updateInforItem(itemPaste.getId(),itemPaste)) {
                System.out.println("Update success "+eachItem.getName());
            }
        }
    }
}