package vinhtt.emby.sdkv4.service;

import embyclient.ApiException;
import embyclient.Configuration;
import embyclient.api.ItemsServiceApi;
import embyclient.api.TagServiceApi;
import embyclient.model.*;

import java.util.List;

public class TagService {

    private TagServiceApi tagServiceApi;
    private ItemService itemService; // Thêm dòng này

    // Sửa hàm khởi tạo
    public TagService(ItemService itemService) {
        this.tagServiceApi = new TagServiceApi(Configuration.getDefaultApiClient());
        this.itemService = itemService; // Gán ItemService
    }

    public List<UserLibraryTagItem> getListTags() {
        // ... (Không thay đổi gì ở hàm này) ...
        if (tagServiceApi != null) {
            try{
                QueryResultUserLibraryTagItem listTags = tagServiceApi.getTags(
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

                if (listTags.getItems().isEmpty()) {
                    System.out.println("Empty Tags");
                }

                if (!listTags.getItems().isEmpty()) {
                    return listTags.getItems();
                }
            } catch (ApiException e) {
                System.out.println("Error fetching tags: " + e.getMessage());
            }
        }else  {
            System.out.println("TagServiceApi is null");
        }
        return  null;
    }

    public List<BaseItemDto> getListItemByTagId(String tagsName, Integer startIndex, Integer limit, boolean recursive) {
        // ... (Không thay đổi gì ở hàm này) ...
        ItemsServiceApi itemsServiceApi = new ItemsServiceApi(Configuration.getDefaultApiClient());

        if (tagServiceApi != null) {
            try{
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
                        tagsName,    //tags
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
                    System.out.println("Empty Item Tags");
                }

                if (!listItems.getItems().isEmpty()) {

                    return listItems.getItems();
                }

            } catch (ApiException e) {
                System.out.println("Error fetching tags: " + e.getMessage());
            }
        }else {
            System.out.println("TagServiceApi is null");
        }

        return  null;
    }

    public void clearTags(String tagName) {
        // Bỏ dòng này
        // ItemService itemService = new ItemService();

        List<BaseItemDto> listItemByTagName = getListItemByTagId(tagName, null, null, true);

        if (listItemByTagName != null) {
            BaseItemDto itemDto = null;
            for (BaseItemDto eachItemOfTagName : listItemByTagName) {
                // Dùng this.itemService
                itemDto = this.itemService.getInforItem(eachItemOfTagName.getId());

                if (itemDto != null) {
                    boolean removed = itemDto.getTagItems().removeIf(tag -> tag.getName() != null && tag.getName().equals(tagName));

                    // Dùng this.itemService
                    if(removed && this.itemService.updateInforItem(eachItemOfTagName.getId(),itemDto)) {
                        System.out.println("Update success (clear) "+eachItemOfTagName.getName());
                    } else if (!removed) {
                        System.out.println("Item did not contain tag (clear): " + eachItemOfTagName.getName());
                    }
                }
            }
        }
    }

    /**
     * THÊM HÀM MỚI: Đổi tên một Tag (bằng cách xóa cũ, thêm mới)
     * @param oldName Tên tag cũ
     * @param newName Tên tag mới (đã serialize nếu là JSON)
     */
    public void updateTag(String oldName, String newName) {
        List<BaseItemDto> listItem = getListItemByTagId(oldName, null, null, true);

        if(listItem == null || listItem.isEmpty()){
            System.out.println("Not found item for tag to update: " + oldName);
            return;
        }

        BaseItemDto item = null;
        for (BaseItemDto eachItem : listItem) {
            item = this.itemService.getInforItem(eachItem.getId());
            if (item == null) continue;

            // 1. Xóa tag cũ
            boolean removed = item.getTagItems().removeIf(tag -> tag.getName() != null && tag.getName().equals(oldName));

            if(removed) {
                // 2. Tạo và thêm tag mới
                NameLongIdPair newTag = new NameLongIdPair();
                newTag.setName(newName);
                item.getTagItems().add(newTag);

                // 3. Update item
                if (this.itemService.updateInforItem(eachItem.getId(), item)) {
                    System.out.println("Update success (rename) " + eachItem.getName());
                } else {
                    System.err.println("Update failed (rename) " + eachItem.getName());
                }
            } else {
                System.out.println("Item did not contain tag (rename): " + eachItem.getName());
            }
        }
    }


    public void copyTags(String itemCopyID, String parentID) {
        // Bỏ dòng này
        // ItemService itemService = new ItemService();
        // Dùng this.itemService
        BaseItemDto itemCopy = this.itemService.getInforItem(itemCopyID);

        if(itemCopy == null){
            System.out.println("Not found item copy");
            return;
        }else{
            System.out.println("List Tags of Item copy:");
            List<NameLongIdPair> listTagsItemCopy = itemCopy.getTagItems();
            for (NameLongIdPair eachtags : listTagsItemCopy) {
                System.out.println("ID: " + eachtags.getId() + " Name: " + eachtags.getName());
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
            if (itemPaste == null) continue;

            List<NameLongIdPair> listTagsItemPaste = itemPaste.getTagItems();

            // LỖI LOGIC GỐC: Bạn đang clear Studios thay vì Tags
            // itemPaste.getStudios().clear(); // <-- Lỗi ở đây
            itemPaste.getTagItems().clear(); // <-- Sửa lỗi

            listTagsItemPaste.addAll(itemCopy.getTagItems());

            for (NameLongIdPair eachTagsPaste : itemPaste.getTagItems()) { // Sửa lỗi logic
                System.out.println(eachTagsPaste.toString());
            }

            // Dùng this.itemService
            if(this.itemService.updateInforItem(itemPaste.getId(),itemPaste)) {
                System.out.println("Update success "+eachItemPaste.getName());
            }
        }
    }

    public void clearTagsByParentID(String parentID) {
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
            if (itemPaste == null) continue;

            itemPaste.getTagItems().clear();

            // Dùng this.itemService
            if(this.itemService.updateInforItem(itemPaste.getId(),itemPaste)) {
                System.out.println("Update success "+eachItem.getName());
            }
        }
    }

}