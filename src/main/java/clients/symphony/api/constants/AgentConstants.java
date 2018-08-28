package clients.symphony.api.constants;

import java.util.concurrent.ConcurrentHashMap;

public class AgentConstants {
    public static final String CREATEDATAFEED = "/agent/v4/datafeed/create";
    public static final String READDATAFEED = "/agent/v4/datafeed/{id}/read";
    public static final String CREATEMESSAGE = "/agent/v4/stream/{sid}/message/create";
    public static final String GETMESSAGES = "/agent/v4/stream/{sid}/message";
    public static final String GETATTACHMENT = "/agent/v1/stream/{sid}/attachment";

    public static final String SEARCHMESSAGES = "/agent/v1/message/search";
    public static final String MESSAGEIMPORT = "/agent/v4/message/import" ;

    public static final String SHARE = "/agent/v3/stream/{sid}/share";
    public static final String LISTSIGNALS = "/agent/v1/signals/list";
    public static final String GETSIGNAL = "/agent/v1/signals/{id}/get" ;
    public static final String CREATESIGNAL = "/agent/v1/signals/create";
    public static final String UPDATESIGNAL = "/agent/v1/signals/{id}/update";
    public static final String DELETESIGNAL = "/agent/v1/signals/{id}/delete";
    public static final String SUBSCRIBESIGNAL = "/agent/v1/signals/{id}/subscribe";
    public static final String UNSUBSCRIBESIGNAL = "/agent/v1/signals/{id}/unsubscribe";
    public static final String GETSUBSCRIBERS = "/v1/signals/{id}/subscribers";
    public static final String CREATEFIREHOSE = "/agent/v4/firehose/create";
    public static final String READFIREHOSE = "/agent/v4/firehose/{id}/read";
}
