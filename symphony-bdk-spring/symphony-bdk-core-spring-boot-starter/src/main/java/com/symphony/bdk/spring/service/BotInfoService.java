package com.symphony.bdk.spring.service;

import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.gen.api.model.UserV2;

import lombok.Getter;

@Getter
public class BotInfoService {
    private final UserV2 botInfo;

    public BotInfoService(SessionService sessionService) {
        this.botInfo = sessionService.getSession();
    }
}
