---
layout: default
title: Application API
nav_order: 12
---

# Application API

The Application Service is a component at the service layer of the BDK which aims to cover the Applications part of the [REST API documentation](https://developers.symphony.com/restapi/main/manage-apps).
More precisely:
* [Create application](https://developers.symphony.com/restapi/main/manage-apps/create-app)
* [Create application with an RSA public key](https://developers.symphony.com/restapi/main/manage-apps/create-app#rsa-application-authentication)
* [Update application](https://developers.symphony.com/restapi/main/manage-apps/update-application)
* [Update application with an RSA public key](https://developers.symphony.com/restapi/main/manage-apps/update-application#rsa-application-authentication)
* [Delete application](hhttps://developers.symphony.com/restapi/main/manage-apps/delete-application)
* [Get application](https://developers.symphony.com/restapi/main/manage-apps/get-application)
* [List application entitlements](https://developers.symphony.com/restapi/main/apps-entitlements/list-app-entitlements)
* [Update application entitlements](https://developers.symphony.com/restapi/main/apps-entitlements/update-application-entitlements)
* [List user applications](https://developers.symphony.com/restapi/main/apps-entitlements/user-apps)
* [Update user applications](https://developers.symphony.com/restapi/main/apps-entitlements/update-user-apps)
* [Patch user applications](https://developers.symphony.com/restapi/main/apps-entitlements/partial-update-user-apps)


## How to use
The central component for the Application Service is the `ApplicationService` class.
This class exposes the user-friendly service APIs which serve all the services mentioned above
and is accessible from the `SymphonyBdk` object by calling the `applications()` method:
```java
@Slf4j
public class Example {
  public static final String APP_ID = "MY_APP_ID";

  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    // Get an application details by id
    ApplicationService applications = bdk.applications();
    ApplicationDetail applicationDetails = applications.getApplication(APP_ID);
    log.info("Application details: " + applicationDetails);
  }
}
```
