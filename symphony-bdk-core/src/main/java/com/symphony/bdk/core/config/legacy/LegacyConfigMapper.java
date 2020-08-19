package com.symphony.bdk.core.config.legacy;

import com.symphony.bdk.core.config.legacy.model.LegacySymConfig;
import com.symphony.bdk.core.config.model.*;

public class LegacyConfigMapper {

    public static BdkConfig map(LegacySymConfig legacySymConfig) {
        BdkClientConfig pod = new BdkClientConfig();
        pod.setHost(legacySymConfig.getPodHost());
        pod.setPort(legacySymConfig.getPodPort());
        pod.setContext(legacySymConfig.getPodContextPath());

        BdkClientConfig agent = new BdkClientConfig();
        agent.setHost(legacySymConfig.getAgentHost());
        agent.setPort(legacySymConfig.getAgentPort());
        agent.setContext(legacySymConfig.getAgentContextPath());

        BdkClientConfig keyManager = new BdkClientConfig();
        keyManager.setHost(legacySymConfig.getKeyAuthHost());
        keyManager.setPort(legacySymConfig.getKeyAuthPort());
        keyManager.setContext(legacySymConfig.getKeyAuthContextPath());

        BdkBotConfig bot = new BdkBotConfig();
        bot.setUsername(legacySymConfig.getBotUsername());
        if (legacySymConfig.getBotPrivateKeyPath() != null && legacySymConfig.getBotPrivateKeyName() != null) {
            bot.setPrivateKeyPath(legacySymConfig.getBotPrivateKeyPath() + legacySymConfig.getBotPrivateKeyName());
        }
        if (legacySymConfig.getBotCertPath() != null && legacySymConfig.getAppCertName() != null) {
            bot.setCertificatePath(legacySymConfig.getBotCertPath() + legacySymConfig.getBotCertName());
        }
        bot.setCertificatePassword(legacySymConfig.getBotCertPassword());

        BdkExtAppConfig app = new BdkExtAppConfig();
        app.setAppId(legacySymConfig.getAppId());
        if (legacySymConfig.getAppPrivateKeyPath() != null && legacySymConfig.getAppPrivateKeyName() != null) {
            app.setPrivateKeyPath(legacySymConfig.getAppPrivateKeyPath() + legacySymConfig.getAppPrivateKeyName());
        }
        if (legacySymConfig.getAppCertPath() != null && legacySymConfig.getAppCertName() != null) {
            app.setCertificatePath(legacySymConfig.getAgentContextPath() + legacySymConfig.getAppCertName());
        }
        app.setCertificatePassword(legacySymConfig.getAppCertPassword());

        BdkSslConfig ssl = new BdkSslConfig();
        ssl.setTrustStorePath(legacySymConfig.getTruststorePath());
        ssl.setTrustStorePassword(legacySymConfig.getTruststorePassword());

        BdkDatafeedConfig datafeed = new BdkDatafeedConfig();
        datafeed.setDatafeedIdFilePath(legacySymConfig.getDatafeedIdFilePath());
        datafeed.setVersion(legacySymConfig.getDatafeedVersion());

        BdkRetryConfig retry = new BdkRetryConfig();
        retry.setInitialIntervalMillis(legacySymConfig.getRetry().getInitialIntervalMillis());
        retry.setMultiplier(legacySymConfig.getRetry().getMultiplier());
        retry.setMaxAttempts(legacySymConfig.getRetry().getMaxAttempts());

        BdkConfig config = new BdkConfig();
        config.setAgent(agent);
        config.setPod(pod);
        config.setKeyManager(keyManager);
        config.setBot(bot);
        config.setApp(app);
        config.setSsl(ssl);
        config.setDatafeed(datafeed);
        config.setRetry(retry);
        return config;
    }
}
