package clients.symphony.api.constants;


public class PodConstants {

    public static final String POD = "/pod";
    public static final String GETUSERSV3 = POD+"/v3/users";
    public static final String GETUSERV2 = POD+"/v2/user";
    public static final String GETIM = POD+"/v1/im/create";
    public static final String CREATEROOM = POD+"/v3/room/create";
    public static final String ADDMEMBER = POD+"/v1/room/{id}/membership/add";
    public static final String REMOVEMEMBER = POD+"/v1/room/{id}/membership/remove";
    public static final String GETROOMINFO = POD+"/v3/room/{id}/info";
    public static final String UPDATEROOMINFO = POD+"/v3/room/{id}/update";
    public static final String GETSTREAMINFO = POD+"/v2/streams/{id}/info";
    public static final String GETROOMMEMBERS = POD+"/v2/room/{id}/membership/list";
    public static final String SETACTIVE = POD+"/v1/admin/room/{id}/setActive";
    public static final String PROMOTEOWNER = POD+"/v1/room/{id}/membership/promoteOwner";
    public static final String DEMOTEOWNER = POD+"/v1/room/{id}/membership/demoteOwner";
    public static final String ACCEPTCONNECTION = POD+"/v1/connection/accept";
    public static final String REJECTCONNECTION = POD+"/v1/connection/reject";
    public static final String GETCONNECTIONSTATUS = POD+"/v1/connection/user/{userId}/info";
    public static final String REMOVECONNECTION = POD+"/v1/connection/user/{userId}/remove";
    public static final String GETCONNECTIONS = POD + "v1/connection/list";
    public static final String SENDCONNECTIONREQUEST = POD+"/v1/connection/create";
    public static final String GETMESSAGESTATUS =  POD+"/v1/message/{mid}/status";
    public static final String GETUSERPRESENCE =  POD+"/v3/user/{uid}/presence";
    public static final String SETPRESENCE =  POD+"/v2/user/presence";
    public static final String REGISTERPRESENCEINTEREST =  POD+"/v1/user/presence/register";

}
