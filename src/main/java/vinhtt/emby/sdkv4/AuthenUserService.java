package vinhtt.emby.sdkv4;

import embyclient.ApiClient;
import embyclient.ApiException;
import embyclient.api.UserServiceApi;

public class AuthenUserService {
    // XÓA 2 DÒNG USERNAME/PASSWORD CỐ ĐỊNH NÀY
    // private String username = "admin";
    // private String password = "123@123a";

    // THÊM 2 DÒNG NÀY ĐỂ NHẬN GIÁ TRỊ TỪ LOGIN
    private String username;
    private String password;

    private String serverId = "";
    private String userId = "";

    private ApiClient apiClient;

    private embyclient.model.AuthenticationAuthenticationResult authenticateUser;

    // SỬA HÀM KHỞI TẠO (CONSTRUCTOR) TỪ 1 THAM SỐ THÀNH 3 THAM SỐ
    public AuthenUserService(ApiClient apiClient, String username, String password) {
        this.apiClient = apiClient;
        this.username = username; // THÊM DÒNG NÀY
        this.password = password; // THÊM DÒNG NÀY
    }

    public boolean login() {
//        ApiKeyAuth apikeyauth = (ApiKeyAuth) apiClient.getAuthentication("apikeyauth");
//        apikeyauth.setApiKey("892425af7ae34ae9b0c82934320c02a8");

        UserServiceApi userServiceApi = new UserServiceApi(apiClient);
        embyclient.model.AuthenticateUserByName body = new embyclient.model.AuthenticateUserByName();

        // SỬA 2 DÒNG NÀY ĐỂ DÙNG BIẾN CỦA CLASS
        body.setUsername(this.username);
        body.setPw(this.password);

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