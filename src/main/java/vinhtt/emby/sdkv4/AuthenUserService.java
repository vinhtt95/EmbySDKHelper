import embyclient.ApiClient;
import embyclient.ApiException;
import embyclient.api.UserServiceApi;

public class AuthenUserService {
    private String username = "admin";
    // Password Emby on Mac
//    private String password = "123456";
    // Password Emby on Windows
    private String password = "123@123a";
    private String serverId = "";
    private String userId = "";

    private ApiClient apiClient;

    private embyclient.model.AuthenticationAuthenticationResult authenticateUser;

    public AuthenUserService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public boolean login() {
//        ApiKeyAuth apikeyauth = (ApiKeyAuth) apiClient.getAuthentication("apikeyauth");
//        apikeyauth.setApiKey("892425af7ae34ae9b0c82934320c02a8");

        UserServiceApi userServiceApi = new UserServiceApi(apiClient);
        embyclient.model.AuthenticateUserByName body = new embyclient.model.AuthenticateUserByName();
            body.setUsername(username);
            body.setPw(password);
            String XEmbyAuthorization = "Emby UserId=\"e8837bc1-ad67-520e-8cd2-f629e3155721\", Client=\"Android\", Device=\"Samsung Galaxy SIII\", DeviceId=\"xxx\", Version=\"1.0.0.0\"";
        try {
            authenticateUser = userServiceApi.postUsersAuthenticatebyname(body, XEmbyAuthorization);
            if (authenticateUser != null) {
                serverId = authenticateUser.getSessionInfo().getServerId();
                userId = authenticateUser.getSessionInfo().getUserId();
            }

        } catch (ApiException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public embyclient.model.AuthenticationAuthenticationResult getAuthenticateUser() {
        if (authenticateUser == null) {
            if (login()) {
                return authenticateUser;
            }else {
                return null;
            }
        }
        return authenticateUser;
    }

    public String getServerId() {
        return serverId;
    }

    public String getUserId() {
        return userId;
    }
}
