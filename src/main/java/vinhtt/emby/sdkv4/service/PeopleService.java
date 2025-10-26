package vinhtt.emby.sdkv4.service;

import embyclient.ApiException;
import embyclient.Configuration;
import embyclient.api.ItemsServiceApi;
import embyclient.api.PersonsServiceApi;
import embyclient.model.BaseItemDto;
import embyclient.model.BaseItemPerson;
import embyclient.model.NameLongIdPair;
import embyclient.model.QueryResultBaseItemDto;

import java.util.List;

public class PeopleService {

    private PersonsServiceApi  personsServiceApi;
    private ItemService itemService; // Thêm dòng này

    // Sửa hàm khởi tạo
    public PeopleService(ItemService itemService) {
        this.personsServiceApi = new PersonsServiceApi(Configuration.getDefaultApiClient());
        this.itemService = itemService; // Gán ItemService
    }

    public List<BaseItemDto> getListPeople() {
        // ... (Không thay đổi gì ở hàm này) ...
        if (personsServiceApi != null) {
            try{
                QueryResultBaseItemDto listPeople = personsServiceApi.getPersons(
                        null,	// artistType
                        null,	// maxOfficialRating
                        null,	//hasThemeSong
                        null,	//hasThemeVideo
                        null,	//hasSubtitles
                        null,	//hasSpecialFeature
                        null,	//hasTrailer
                        null,	//isSpecialSeason
                        null,	// adjacentTo
                        null,	// startItemId
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
                        null,	// excludeItemIds
                        null,	//startIndex
                        null,	//limit
                        null,	//recursive
                        null,	// searchTerm
                        null,	// sortOrder
                        null,	// parentId
                        null,	// fields
                        null,	// excludeItemTypes
                        null,	// includeItemTypes
                        null,	// anyProviderIdEquals
                        null,	// filters
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
                        null,	// mediaTypes
                        null,	// imageTypes
                        null,	// sortBy
                        null,	//isPlayed
                        null,	// genres
                        null,	// officialRatings
                        null,	// tags
                        null,	// excludeTags
                        null,	// years
                        null,	//enableImages
                        null,	//enableUserData
                        null,	//imageTypeLimit
                        null,	// enableImageTypes
                        null,	// person
                        null,	// personIds
                        null,	// personTypes
                        null,	// studios
                        null,	// studioIds
                        null,	// artists
                        null,	// artistIds
                        null,	// albums
                        null,	// ids
                        null,	// videoTypes
                        null,	// containers
                        null,	// audioCodecs
                        null,	// audioLayouts
                        null,	// videoCodecs
                        null,	// extendedVideoTypes
                        null,	// subtitleCodecs
                        null,	// path
                        null,	// userId
                        null,	// minOfficialRating
                        null,	//isLocked
                        null,	//isPlaceHolder
                        null,	//hasOfficialRating
                        null,	//groupItemsIntoCollections
                        null,	//is3D
                        null,	// seriesStatus
                        null,	// nameStartsWithOrGreater
                        null,	// artistStartsWithOrGreater
                        null,	// albumArtistStartsWithOrGreater
                        null,	// nameStartsWith
                        null	// nameLessThan
                );

                if (listPeople.getItems().isEmpty()) {
                    System.out.println("Empty People");
                }

                if (!listPeople.getItems().isEmpty()) {
                    return listPeople.getItems();
                }
            } catch (ApiException e) {
                System.out.println("Error fetching people: " + e.getMessage());
            }
        }else{
            System.out.println("PersonsServiceApi is null");
        }
        return null;
    }

    public  List<BaseItemDto> getListPeopleByID(String peopleID, Integer startIndex, Integer limit, boolean recursive) {
        // ... (Không thay đổi gì ở hàm này) ...
        if (personsServiceApi != null) {

            ItemsServiceApi itemsServiceApi = new ItemsServiceApi(Configuration.getDefaultApiClient());

            try{
                QueryResultBaseItemDto listPeople = itemsServiceApi.getItems(
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
                        peopleID,    //personIds
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

                if (listPeople.getItems().isEmpty()) {
                    System.out.println("Empty People");
                }

                if (!listPeople.getItems().isEmpty()) {

                    return listPeople.getItems();
                }
            } catch (ApiException e) {
                System.out.println("Error fetching people: " + e.getMessage());
            }
        }else{
            System.out.println("PersonsServiceApi is null");
        }

        return null;
    }

    public void copyPeople(String itemCopyID, String parentID) {
        // Bỏ dòng này
        // ItemService itemService = new ItemService();
        // Dùng this.itemService
        BaseItemDto itemCopy = this.itemService.getInforItem(itemCopyID);

        if(itemCopy == null){
            System.out.println("Not found item copy");
            return;
        }else{
            System.out.println("List People of Item copy:");
            List<NameLongIdPair> listPeopleItemCopy = itemCopy.getStudios();
            for (NameLongIdPair eachStudio : listPeopleItemCopy) {
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

            List<BaseItemPerson> listPeopleItemPaste = itemPaste.getPeople();

            itemPaste.getPeople().clear();

            listPeopleItemPaste.addAll(itemCopy.getPeople());

            for (BaseItemPerson eachPeoplePaste : itemPaste.getPeople()) {
                System.out.println(eachPeoplePaste.toString());
            }

            // Dùng this.itemService
            if(this.itemService.updateInforItem(itemPaste.getId(),itemPaste)) {
                System.out.println("Update success "+eachItemPaste.getName());
            }
        }
    }

    public void clearPeopleByParentID(String parentID) {
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
            itemPaste.getPeople().clear();

            // Dùng this.itemService
            if(this.itemService.updateInforItem(itemPaste.getId(),itemPaste)) {
                System.out.println("Update success "+eachItem.getName());
            }
        }
    }

    public void clearPeople(String studioId) {
        // Bỏ dòng này
        // ItemService itemService = new ItemService();

        List<BaseItemDto> listStudioBy = getListPeopleByID(studioId, null, null, true);

        if (listStudioBy != null) {
            BaseItemDto itemDto = null;
            for (BaseItemDto eachItemOfPeople : listStudioBy) {
                // Dùng this.itemService
                itemDto = this.itemService.getInforItem(eachItemOfPeople.getId());

                if (itemDto != null) {
                    itemDto.getPeople().removeIf(person -> person.getId() != null && person.getId().equals(studioId));

                    // Dùng this.itemService
                    if(this.itemService.updateInforItem(eachItemOfPeople.getId(),itemDto)) {
                        System.out.println("Update success "+eachItemOfPeople.getName());
                    }
                }
            }
        }
    }
}