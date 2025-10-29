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
            // SỬA LỖI LOGIC: Dùng getPeople() chứ không phải getStudios()
            List<BaseItemPerson> listPeopleItemCopy = itemCopy.getPeople();
            for (BaseItemPerson eachPeople : listPeopleItemCopy) {
                System.out.println("ID: " + eachPeople.getId() + " Name: " + eachPeople.getName());
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
            if (itemPaste == null) continue;

            itemPaste.getPeople().clear();

            // Dùng this.itemService
            if(this.itemService.updateInforItem(itemPaste.getId(),itemPaste)) {
                System.out.println("Update success "+eachItem.getName());
            }
        }
    }

    public void clearPeople(String personId) {
        // Bỏ dòng này
        // ItemService itemService = new ItemService();

        List<BaseItemDto> listPeopleBy = getListPeopleByID(personId, null, null, true);

        if (listPeopleBy != null) {
            BaseItemDto itemDto = null;
            for (BaseItemDto eachItemOfPeople : listPeopleBy) {
                // Dùng this.itemService
                itemDto = this.itemService.getInforItem(eachItemOfPeople.getId());

                if (itemDto != null) {
                    boolean removed = itemDto.getPeople().removeIf(person -> person.getId() != null && person.getId().equals(personId));

                    // Dùng this.itemService
                    if(removed && this.itemService.updateInforItem(eachItemOfPeople.getId(),itemDto)) {
                        System.out.println("Update success (clear) "+eachItemOfPeople.getName());
                    }
                }
            }
        }
    }

    /**
     * (HÀM MỚI) Cập nhật People (Xóa cũ, Thêm mới)
     * @param oldPersonId ID của person cũ
     * @param newName Tên của person mới (server sẽ tự tạo nếu chưa có)
     */
    public void updatePeople(String oldPersonId, String newName) {
        List<BaseItemDto> listPeopleBy = getListPeopleByID(oldPersonId, null, null, true);
        if (listPeopleBy == null || listPeopleBy.isEmpty()) {
            System.out.println("Not found item for person to update: " + oldPersonId);
            return;
        }

        BaseItemDto itemDto = null;
        for (BaseItemDto eachItemOfPeople : listPeopleBy) {
            itemDto = this.itemService.getInforItem(eachItemOfPeople.getId());
            if (itemDto == null) continue;

            // 1. Xóa person cũ
            boolean removed = itemDto.getPeople().removeIf(person -> person.getId() != null && person.getId().equals(oldPersonId));

            if (removed) {
                // 2. Tạo và thêm person mới (chỉ cần Tên)
                // (Server sẽ tự xử lý Type dựa trên thư viện)
                BaseItemPerson newPerson = new BaseItemPerson();
                newPerson.setName(newName);
                itemDto.getPeople().add(newPerson);

                // 3. Update item
                if (this.itemService.updateInforItem(itemDto.getId(), itemDto)) {
                    System.out.println("Update success (rename) " + eachItemOfPeople.getName());
                } else {
                    System.err.println("Update failed (rename) " + eachItemOfPeople.getName());
                }
            } else {
                System.out.println("Item did not contain person (rename): " + eachItemOfPeople.getName());
            }
        }
    }
}