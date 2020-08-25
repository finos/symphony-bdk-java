package com.symphony.bdk.core.service;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.UserV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BotInfoServiceTest {

    private BotInfoService botInfoService;

    @BeforeEach
    void init() {
        AuthSession authSession = mock(AuthSession.class);
        when(authSession.getSessionToken()).thenReturn("1234");
        when(authSession.getKeyManagerToken()).thenReturn("1234");
        this.botInfoService = new BotInfoService(
                null,
                authSession
        );
    }

    @Test
    void getBotInfo() throws ApiException, IOException {
        SessionApi sessionApi = mock(SessionApi.class);
        UserV2 user = new UserV2().id(7696581394433L).displayName("Symphony Admin");
        when(sessionApi.v2SessioninfoGet("1234")).thenReturn(user);
        this.botInfoService.setSessionApi(sessionApi);

        UserV2 botInfo = this.botInfoService.getBotInfo();

        assertEquals(botInfo.getId(), 7696581394433L);
        assertEquals(botInfo.getDisplayName(), "Symphony Admin");
    }
}
