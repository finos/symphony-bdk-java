# Symphony Group Extension
The Symphony Group Extension allows the bot developer to manage their groups of users.

## Requirements
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

## Usage
This extension must be manually loaded in the BDK. 

### Usage with BDK Core
```java
public class Example {

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = SymphonyBdk.builder()
        .config(loadFromSymphonyDir("config.yaml"))
        .extension(SymphonyGroupBdkExtension.class) // or bdk.extensions().register(SymphonyGroupBdkExtension.class);
        .build();
    
    // retrieve the service provided by the group extension
    final SymphonyGroupService groupService = bdk.extensions().service(SymphonyGroupBdkExtension.class);
    
    // use service to list or modify groups
    GroupList groups = groupService.listGroups("SDL", Status.ACTIVE, null, null, null, null);
  }
}
```

### Usage with BDK Spring Boot Starter
Since this extension is `BdkExtensionServiceProvider` only, the simplest way to use is to directly register the `SymphonyGroupService`
as a bean in your application context: 
```java
@Configuration
public class GroupExtensionConfig {

  @Bean
  public SymphonyGroupService groupService(BdkConfig config, ApiClientFactory apiClientFactory, AuthSession session) {
    return new SymphonyGroupService(
        new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()),
        apiClientFactory,
        session
    );
  }
}
```
