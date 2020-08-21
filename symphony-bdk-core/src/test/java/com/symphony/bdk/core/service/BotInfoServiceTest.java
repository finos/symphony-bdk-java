package com.symphony.bdk.core.service;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.BotAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.BotAuthenticatorRsaImpl;
import com.symphony.bdk.core.test.BdkMockServer;
import com.symphony.bdk.core.test.BdkMockServerExtension;
import com.symphony.bdk.core.test.ResResponseHelper;
import com.symphony.bdk.core.test.RsaTestHelper;
import com.symphony.bdk.gen.api.model.UserV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BdkMockServerExtension.class)
public class BotInfoServiceTest {

    private BotInfoService botInfoService;

    @BeforeEach
    void init(BdkMockServer mockServer) throws AuthUnauthorizedException {
        mockServer.onPost("/login/pubkey/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
        mockServer.onPost("/relay/pubkey/authenticate", res -> res.withBody("{ \"token\": \"1234\", \"name\": \"sessionToken\" }"));
        BotAuthenticator botAuthenticator = new BotAuthenticatorRsaImpl(
                "username",
                RsaTestHelper.generateKeyPair().getPrivate(),
                mockServer.newApiClient("/login"),
                mockServer.newApiClient("/relay")
        );
        AuthSession authSession = botAuthenticator.authenticateBot();
        this.botInfoService = new BotInfoService(
                mockServer.newApiClient("/pod"),
                authSession
        );
    }

    @Test
    void getBotInfo(BdkMockServer mockServer) throws ApiException, IOException {
        String botInfoResponse = ResResponseHelper.readResResponseFromClasspath("bot_info.json");
        mockServer.onGet("/pod/v2/sessioninfo",
                res -> res.withBody(botInfoResponse));

        UserV2 botInfo = this.botInfoService.getBotInfo();

        assertEquals(botInfo.getId(), 7696581394433L);
        assertEquals(botInfo.getDisplayName(), "Symphony Admin");
    }
}
