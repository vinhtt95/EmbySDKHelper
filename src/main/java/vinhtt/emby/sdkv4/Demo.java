package vinhtt.emby.sdkv4;

import embyclient.ApiClient;
import embyclient.ApiException;
import embyclient.Configuration;
import embyclient.api.ItemsServiceApi;
import embyclient.model.BaseItemDto;
import embyclient.model.UserLibraryTagItem;
import vinhtt.emby.sdkv4.service.*;

import java.util.List;

public class Demo {
    public static void main(String[] args) {
        try {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath("http://localhost:8096/emby"); // Mac M4
//            apiClient.setBasePath("http://192.168.88.230:8096/emby");

            embyclient.auth.ApiKeyAuth apikeyauth = (embyclient.auth.ApiKeyAuth) apiClient.getAuthentication("apikeyauth");
            apikeyauth.setApiKey("47ed1090cbe84b5db4272051f654e4c1"); // Mac M4
//            apikeyauth.setApiKey("330d00ea44eb443a93ab1a2324b58861");

            AuthenUserService authenUserService = new AuthenUserService(apiClient);
            authenUserService.login();

            Configuration.setDefaultApiClient(apiClient);

            System.out.println(authenUserService.getAuthenticateUser().getAccessToken());

            /*
             * List Studios*/
            StudioService studioService = new StudioService();

            /*List<BaseItemDto> listStudio = studioService.getListStudios();

            if (!listStudio.isEmpty()) {
                System.out.println("List Studios:");
                for (BaseItemDto each : listStudio) {
                    System.out.println("ID: " + each.getId() + " Name: " + each.getName());
                }
            }*/

//            studioService.clearStudio("10547");
//            studioService.copyStudio("10700","11244");
//            studioService.clearStudioByParentID("11244");
            /*
             * List Item by ParentID*/

            /*List<BaseItemDto> listItemtByParentID = new ItemService().getListItem("10655", 0, 50, false);
            if (listItemtByParentID != null) {
                for (BaseItemDto each :
                        listItemtByParentID) {
                    System.out.println(each.getName() + " ID: " + each.getId());
                }
            }*/

           /* StudiosServiceApi studiosServiceApi = new StudiosServiceApi(apiClient);
            embyclient.model.QueryResultBaseItemDto resultBaseItemDto = studiosServiceApi.getStudios(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);

            for (embyclient.model.BaseItemDto each :
                    resultBaseItemDto.getItems()) {
                System.out.println(each);
            }*/

            /*
             * List Peoples*/
        /*PersonsServiceApi personsServiceApi = new PersonsServiceApi(apiClient);
        embyclient.model.QueryResultBaseItemDto resultBaseItemDto = personsServiceApi.getPersons(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);

        for (embyclient.model.BaseItemDto each :
                resultBaseItemDto.getItems()) {
            System.out.println(each);
        }*/

            /*
             * List Genres*/
            /*GenresService genresService = new GenresService();
            List<BaseItemDto> listGenres = genresService.getListGenres();

            if (!listGenres.isEmpty()) {
                for (BaseItemDto each : listGenres) {
                    System.out.println("ID: "+ each.getId()+" Name: "+each.getName());
                }
            }*/

//            genresService.getListItemByGenreId("maxJAV.com", null, null, true);
//            genresService.clearGenres("javpage.com");
//            genresService.copyGenres("10700","11244");
//            genresService.clearGenresByParentID("11244");
            /*GenresServiceApi genresServiceApi = new GenresServiceApi(apiClient);
            embyclient.model.QueryResultBaseItemDto genreResult = genresServiceApi.getGenres(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

            for (embyclient.model.BaseItemDto each :
                    genreResult.getItems()) {
                System.out.println(each);
            }*/

//            TagService tagService = new TagService();
//            List<UserLibraryTagItem> listTagsItems = tagService.getListTags();
//
//            if (!listTagsItems.isEmpty()) {
//                for (UserLibraryTagItem each : listTagsItems) {
//                    System.out.println("ID: "+ each.getId()+" Name: "+each.getName());
//                }
//            }
//            List<BaseItemDto> listItems = tagService.getListItemByTagId("Nữ sinh", null, null, true);

           /* if (!listItems.isEmpty()) {
                for (BaseItemDto each : listItems) {
                    System.out.println("ID: "+ each.getId()+" Name: "+each.getName());
                }
            }*/

            PeopleService peopleService = new PeopleService();
            List<BaseItemDto> listPeople = peopleService.getListPeople();

            if (!listPeople.isEmpty()) {
                for (BaseItemDto each : listPeople) {
                    System.out.println("ID: "+ each.getId()+" Name: "+each.getName());
                }
            }

           /* List<BaseItemDto> listItemByPeople = peopleService.getListPeopleByID("12801", null, null, true);

            if (!listItemByPeople.isEmpty()) {
                for (BaseItemDto each : listItemByPeople) {
                    System.out.println("ID: "+ each.getId()+" Name: "+each.getName());
                }
            }*/

//            peopleService.copyPeople("12769","11244");
//            peopleService.clearPeopleByParentID("11244");
//            peopleService.clearPeople("12812");


//            tagService.copyTags("10700","11244");
//            tagService.clearTags("New Porn");
//            tagService.clearTagsByParentID("11244");

            /**
             * Lấy danh sách các Item con theo parentID
             *
             * @param parentID
             * @param itemsServiceApi
             * @param startIndex
             * @param limit
             * @return
             * @throws ApiException
             */
            // SỬA ĐỔI: Thêm startIndex và limit
            String parentID;
            Integer startIndex = 0;
            Integer limit = 50;
            String sortBy = "SortName";
            String SortOrder = "Ascending";
            String keywords = "MIDV569";


            /*
            ItemsServiceApi itemsServiceApi = new ItemsServiceApi();
            embyclient.model.QueryResultBaseItemDto result = null;
            try {
                result = itemsServiceApi.getItems(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, startIndex, limit, true, keywords, SortOrder, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, sortBy, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

                if (result.getItems().isEmpty()) {
                    System.out.println("No items found");
                } else {
                    for (BaseItemDto each :
                            result.getItems()) {
                        System.out.println(each.getName());
                    }
                }
            } catch (ApiException e) {
                System.out.println(e.getMessage());
            }*/

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
