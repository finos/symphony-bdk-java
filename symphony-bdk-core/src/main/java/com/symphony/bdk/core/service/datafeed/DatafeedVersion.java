package com.symphony.bdk.core.service.datafeed;

/**
 * Options of datafeed version can be used.
 */
public enum DatafeedVersion {
    V1, V2;

    /**
     * Get {@link DatafeedVersion} from a String
     *
     * @param version version in string
     * @return DatafeedVersion
     */
    public static DatafeedVersion of(String version) {
        if ("v2".equalsIgnoreCase(version)) {
            return V2;
        }
        return V1;
    }
}
