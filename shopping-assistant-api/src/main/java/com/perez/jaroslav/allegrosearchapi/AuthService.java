package com.perez.jaroslav.allegrosearchapi;

import com.perez.jaroslav.allegrosearchapi.soap.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthService {
    private String userToken;
    private String login;
    private String password;
    private ServicePort port;

    private String sessionHandle;

    public AuthService(ServicePort port, String userToken, String login, String password) {
        this.userToken = userToken;
        this.login = login;
        this.password = password;
        this.port = port;
    }

    public void login(){
        DoLoginEncRequest request = new DoLoginEncRequest();
        request.setUserLogin(login);
        request.setUserHashPassword(makePasswordHash());
        request.setWebapiKey(userToken);
        request.setCountryCode(1);
        //long localVersion = 1525783777;
        long localVersion = getLocalVersion();
        request.setLocalVersion(localVersion);

        DoLoginEncResponse response = port.doLoginEnc(request);
        sessionHandle = response.getSessionHandlePart();
    }

    private long getLocalVersion(){
        DoQuerySysStatusRequest request = new DoQuerySysStatusRequest();
        request.setCountryId(1);
        request.setWebapiKey(userToken);
        request.setSysvar(3);

        DoQuerySysStatusResponse response = port.doQuerySysStatus(request);
        return response.getVerKey();
    }

    public boolean isLogged(){
        return (sessionHandle != null);
    }

    public String getSessionHandle() {
        return sessionHandle;
    }

    public String getUserToken() {
        return userToken;
    }

    private String makePasswordHash(){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(hash);
    }
}
