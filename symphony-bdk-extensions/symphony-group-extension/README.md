# Symphony Group Extension
The Symphony Group Extension allows the bot developer to manage their groups of users.

## Prerequisites
This extension requires the **Distribution List Manager** role assigned to your bot _service account_ from the Symphony
Administration Portal.

If your service account does not have this specific role, any call to the Groups API will return a `ApiRuntimeException`
with status `403` (and error code `SYMPHONY_PROFILE_MANAGER__ENTITLEMENT_NOT_FOUND`).

> :warning: Changing the role in ACP requires to re-authenticate your bot's service account.

## How to use
As this is an additional extension, you must explicitly import it along with the required BDK dependencies. 
With Maven: 
```xml
<dependencies>
    <dependency>
        <groupId>org.finos.symphony.bdk.ext</groupId>
        <artifactId>symphony-group-extension</artifactId>
    </dependency>
</dependencies>
```
With Gradle: 
```groovy
dependencies {
    implementation 'org.finos.symphony.bdk.ext:symphony-group-extension'
}
```

### With Spring Boot 
In Spring Boot, you just need to manually register the `SymphonyGroupService` as a bean: 
```java
@Configuration
public class GroupExtensionConfig {

  @Bean
  public SymphonyGroupService groupService(
      final RetryWithRecoveryBuilder<?> retryBuilder,
      final ApiClientFactory apiClientFactory,
      final AuthSession session
  ) {
    return new SymphonyGroupService(retryBuilder, apiClientFactory, session);
  }
}
```
And then use it in your application, for example: 
```java
@RestController
public class GroupApi {

  @Autowired
  private SymphonyGroupService groupService;

  @GetMapping("/api/v1/groups")
  public GroupList getGroups(@RequestParam(defaultValue = "SDL") String type) {
    return this.groupService.listGroups(type, Status.ACTIVE, null, null, null, null);
  }
}
```

----
:bulb: For more information about BDK extensions, please refer to this [documentation](../../docs/extension.md).
