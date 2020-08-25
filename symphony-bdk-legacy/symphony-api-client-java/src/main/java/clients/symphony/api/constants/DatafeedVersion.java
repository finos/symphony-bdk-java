package clients.symphony.api.constants;

public enum DatafeedVersion {
    V1, V2;

    public static DatafeedVersion of(String version) {
        if ("v2".equalsIgnoreCase(version)) {
            return V2;
        } else {
            return V1;
        }
    }
}
