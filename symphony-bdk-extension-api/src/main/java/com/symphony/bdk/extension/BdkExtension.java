package com.symphony.bdk.extension;

import org.apiguardian.api.API;

/**
 * Marker interface for all BDK extensions.
 *
 * <p>An extension can be manually registered using {@code bdk.extensions().register(Class&gt;? extends BdkExtension&lt;)} method.
 *
 * <p>An extension must have a default constructor in order to be automatically instantiated by the BDK {@code ExtensionService}.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkExtension {
}
