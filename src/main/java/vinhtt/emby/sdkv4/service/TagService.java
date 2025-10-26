package vinhtt.emby.sdkv4.service;

import embyclient.ApiException;
import embyclient.Configuration;
import embyclient.api.ItemsServiceApi;
import embyclient.api.TagServiceApi;
import embyclient.model.*;

import java.util.List;

public class TagService {

    private TagServiceApi tagServiceApi;
    public TagService() {
        this.tagServiceApi = new TagServiceApi(Configuration.getDefaultApiClient());
    }

    public List<UserLibraryTagItem> getListTags() {
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
                    System.out.println("Empty Tags");
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

        ItemService itemService = new ItemService();

        List<BaseItemDto> listItemByTagName = getListItemByTagId(tagName, null, null, true);

        if (listItemByTagName != null) {
            BaseItemDto itemDto = null;
            for (BaseItemDto eachItemOfTagName : listItemByTagName) {

                itemDto = itemService.getInforItem(eachItemOfTagName.getId());

                if (itemDto != null) {
                    itemDto.getTagItems().clear();

                    if(itemService.updateInforItem(eachItemOfTagName.getId(),itemDto)) {
                        System.out.println("Update success "+eachItemOfTagName.getName());
                    }
                }
            }
        }
    }

    public void copyTags(String itemCopyID, String parentID) {
        ItemService itemService = new ItemService();
        BaseItemDto itemCopy = itemService.getInforItem(itemCopyID);

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

        List<BaseItemDto> listItemPaste = itemService.getListItemByParentID(parentID, null, null, true);
        if (listItemPaste == null) {
            System.out.println("Not found item paste");
            return;
        }

        BaseItemDto itemPaste = null;
        for (BaseItemDto eachItemPaste : listItemPaste) {
            System.out.println("ID: " + eachItemPaste.getId()+ " Name: " + eachItemPaste.getName());
            itemPaste = itemService.getInforItem(eachItemPaste.getId());

            List<NameLongIdPair> listTagsItemPaste = itemPaste.getTagItems();

            itemPaste.getStudios().clear();

            listTagsItemPaste.addAll(itemCopy.getTagItems());

            for (NameLongIdPair eachTagsPaste : itemPaste.getStudios()) {
                System.out.println(eachTagsPaste.toString());
            }

            if(itemService.updateInforItem(itemPaste.getId(),itemPaste)) {
                System.out.println("Update success "+eachItemPaste.getName());
            }
        }
    }

    public void clearTagsByParentID(String parentID) {
        ItemService itemService = new ItemService();

        List<BaseItemDto> listItem = itemService.getListItemByParentID(parentID, null, null, true);
        if (listItem == null) {
            System.out.println("Not found item");
            return;
        }

        BaseItemDto itemPaste = null;
        for (BaseItemDto eachItem : listItem) {
            System.out.println("ID: " + eachItem.getId()+ " Name: " + eachItem.getName());
            itemPaste = itemService.getInforItem(eachItem.getId());
            itemPaste.getTagItems().clear();

            if(itemService.updateInforItem(itemPaste.getId(),itemPaste)) {
                System.out.println("Update success "+eachItem.getName());
            }
        }
    }

}
