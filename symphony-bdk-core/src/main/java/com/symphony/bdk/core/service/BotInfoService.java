package com.symphony.bdk.core.service;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.UserV2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BotInfoService {

    private final SessionApi sessionApi;
    private final AuthSession authSession;
    private static UserV2 userInfo;

    public BotInfoService(ApiClient podClient, AuthSession authSession) {
        this.sessionApi = new SessionApi(podClient);
        this.authSession = authSession;
    }

    /**
     * Get the information about the bot itself
     *
     * @return information about the bot itself
     */
    public UserV2 getBotInfo() {
        if (userInfo == null) {
            try {
                log.debug("Call session api to get bot info");
                userInfo = this.sessionApi.v2SessioninfoGet(authSession.getSessionToken());
            } catch (ApiException e) {
                log.error("Cannot retrieve bot information", e);
            }
        }
        return userInfo;
    }

}
