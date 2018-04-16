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

}
