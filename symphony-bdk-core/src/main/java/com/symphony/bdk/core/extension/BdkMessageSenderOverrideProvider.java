package com.symphony.bdk.core.extension;

import org.apiguardian.api.API;

/**
 * Detection interface for extensions that provide a {@link MessageSenderOverride}.
 *
 * <p>Extensions implementing this interface must be pre-registered via
 * {@code SymphonyBdkBuilder.extension(Class)} so the override can be wired into
 * {@code MessageService} at construction time. If registered post-construction, a warning is
 * logged and the override has no effect.
 *
 * <p>If more than one registered extension implements this interface, the first registered
 * override is used and a warning is logged.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkMessageSenderOverrideProvider {

  MessageSenderOverride getMessageSenderOverride();
}
