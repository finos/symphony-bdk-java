---
layout: default
title: User API
nav_order: 10
---

# User API
The User Service is a component at the service layer of the BDK which covers the User part of the [REST API documentation](https://developers.symphony.com/restapi/reference).
More precisely:
* [Create user](https://developers.symphony.com/restapi/reference/create-user-v2)
* [Update user](https://developers.symphony.com/restapi/reference/update-user-v2)
* [Suspend user](https://developers.symphony.com/restapi/v20.10/reference/suspend-user-v1)
* [Unsuspend user](https://developers.symphony.com/restapi/v20.10/reference/suspend-user-v1)
* [Get user details](https://developers.symphony.com/restapi/reference/get-user-v2)
* [List all user details](https://developers.symphony.com/restapi/reference/list-users-v2)
* [List users by ids](https://developers.symphony.com/restapi/reference/users-lookup-v3)
* [List users by emails](https://developers.symphony.com/restapi/reference/users-lookup-v3)
* [List users by usernames](https://developers.symphony.com/restapi/reference/users-lookup-v3)
* [Search users](https://developers.symphony.com/restapi/reference/search-users)
* [Find users by filter](https://developers.symphony.com/restapi/reference/find-users)
* [Add role to user](https://developers.symphony.com/restapi/reference/add-role)
* [List roles](https://developers.symphony.com/restapi/reference/list-roles)
* [Remove a role](https://developers.symphony.com/restapi/reference/remove-role)
* [Get avatar](https://developers.symphony.com/restapi/reference/user-avatar)
* [Update avatar](https://developers.symphony.com/restapi/reference/update-user-avatar)
* [Get disclaimer](https://developers.symphony.com/restapi/reference/user-disclaimer)
* [Remove disclaimer](https://developers.symphony.com/restapi/reference/unassign-user-disclaimer)
* [Add disclaimer](https://developers.symphony.com/restapi/reference/update-disclaimer)
* [Get user delegates](https://developers.symphony.com/restapi/reference/delegates)
* [Update user delegates](https://developers.symphony.com/restapi/reference/update-delegates)
* [Get feature entitlements for a user](https://developers.symphony.com/restapi/reference/features)
* [Update feature entitlements for a user](https://developers.symphony.com/restapi/reference/update-features)
* [Get user status](https://developers.symphony.com/restapi/reference/user-status)
* [Update user status](https://developers.symphony.com/restapi/reference/update-user-status)
* [Follow a user](https://developers.symphony.com/restapi/reference/follow-user)
* [Unfollow a user](https://developers.symphony.com/restapi/reference/unfollow-user)
* [List user followers](https://developers.symphony.com/restapi/reference/list-user-followers)
* [List followed users](https://developers.symphony.com/restapi/reference/list-users-followed)
* [List audit trail](https://developers.symphony.com/restapi/reference/list-audit-trail-v1)


## How to use
The central component for the User Service is the `UserService` class, it exposes the service APIs endpoints mentioned above.
The service is accessible from the`SymphonyBdk` object by calling the `users()` method:

```java
@Slf4j
public class Example {
  public static final Long USER_ID = 123456789L;

  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    // Get user details
    UserService users = bdk.users();
    V2UserDetail userDetail = users.getUserDetail(USER_ID);
    log.info("User details: " + userDetail);
  }
}
```
