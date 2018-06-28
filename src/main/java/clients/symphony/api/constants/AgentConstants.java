package clients.symphony.api.constants;

public class AgentConstants {
    public static final String CREATEDATAFEED = "/agent/v4/datafeed/create";
    public static final String READDATAFEED = "/agent/v4/datafeed/{id}/read";
    public static final String CREATEMESSAGE = "/agent/v4/stream/{sid}/message/create";
    public static final String GETMESSAGES = "/agent/v4/stream/{sid}/message";
    public static final String GETATTACHMENT = "/agent/v1/stream/{sid}/attachment";

    public static final String SEARCHMESSAGES = "/agent/v1/message/search";
    public static final String MESSAGEIMPORT = "/agent/v4/message/import" ;

}
