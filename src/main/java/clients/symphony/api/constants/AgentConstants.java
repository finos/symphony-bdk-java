package clients.symphony.api.constants;

public class AgentConstants {
    public static final String AGENT = "/agent";
    public static final String CREATEDATAFEED = AGENT + "/v4/datafeed/create";
    public static final String READDATAFEED = AGENT + "/v4/datafeed/{id}/read";
    public static final String CREATEMESSAGE = AGENT + "/v4/stream/{sid}/message/create";
    public static final String GETMESSAGES = AGENT + "/v4/stream/{sid}/message";
    public static final String GETATTACHMENT = AGENT + "/v1/stream/{sid}/attachment";
    public static final String SEARCHMESSAGES = AGENT + "/v1/message/search";
    public static final String MESSAGEIMPORT = AGENT + "/v4/message/import";
    public static final String SHARE = AGENT + "/v3/stream/{sid}/share";
    public static final String LISTSIGNALS = AGENT + "/v1/signals/list";
    public static final String GETSIGNAL = AGENT + "/v1/signals/{id}/get";
    public static final String CREATESIGNAL = AGENT + "/v1/signals/create";
    public static final String UPDATESIGNAL = AGENT + "/v1/signals/{id}/update";
    public static final String DELETESIGNAL = AGENT + "/v1/signals/{id}/delete";
    public static final String SUBSCRIBESIGNAL = AGENT + "/v1/signals/{id}/subscribe";
    public static final String UNSUBSCRIBESIGNAL = AGENT + "/v1/signals/{id}/unsubscribe";
    public static final String GETSUBSCRIBERS = AGENT + "/v1/signals/{id}/subscribers";
    public static final String CREATEFIREHOSE = AGENT + "/v4/firehose/create";
    public static final String READFIREHOSE = AGENT + "/v4/firehose/{id}/read";
    public static final String HEALTHCHECK = AGENT + "/v2/HealthCheck";
    public static final String INFO = AGENT + "/v1/info";
}
