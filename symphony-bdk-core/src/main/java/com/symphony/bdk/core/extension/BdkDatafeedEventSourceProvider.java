package com.symphony.bdk.core.extension;

import org.apiguardian.api.API;

/**
 * Detection interface for extensions that provide a {@link DatafeedEventSource}.
 *
 * <p>Extensions implementing this interface must be pre-registered via
 * {@code SymphonyBdkBuilder.extension(Class)} so the source can be wired into
 * {@code DatafeedLoopV2} at construction time. If registered post-construction, a warning is
 * logged and the source has no effect.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkDatafeedEventSourceProvider {

  DatafeedEventSource getDatafeedEventSource();
}
