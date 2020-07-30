package com.symphony.bdk.core.api.invoker;

import com.symphony.bdk.core.api.invoker.impl.ApiClientNotImplemented;

public class Configuration {

    private static ApiClient defaultApiClient = new ApiClientNotImplemented();

    /**
     * Get the default API client, which would be used when creating API
     * instances without providing an API client.
     *
     * @return Default API client
     */
    public static ApiClient getDefaultApiClient() {
        return defaultApiClient;
    }

    /**
     * Set the default API client, which would be used when creating API
     * instances without providing an API client.
     *
     * @param apiClient API client
     */
    public static void setDefaultApiClient(ApiClient apiClient) {
        defaultApiClient = apiClient;
    }
}
