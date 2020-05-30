package internal.jersey;

import org.apiguardian.api.API;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

/**
 * Simple Jersey {@link javax.ws.rs.client.Client} feature that set Cache-Control header value to no-cache. That feature
 * can be applied to any ongoing request.
 *
 * @author Thibault Pensec
 * @since 24/02/2020
 */
@API(status = API.Status.INTERNAL)
public class NoCacheFeature implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) {
        requestContext.getHeaders().add(HttpHeaders.CACHE_CONTROL, "no-cache");
    }
}
