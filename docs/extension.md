# BDK Extension Model
> :bulb: since `2.6`

## Overview
The BDK extension model consists of a single, coherent concept: the `BdkExtension` API. Note, however, that `BdkExtension` 
itself is just a marker interface.

The `BdkExtension` API is available through the module `:symphony-bdk-extension-api` but other modules might be required
depending on what your extension needs to use.

## Registering Extensions
Extensions are registered _programmatically_ via the `ExtensionService`:
```java
class ExtensionExample {
    
    public static void main(String[] args) {
        // using the ExtensionService
        final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));
        bdk.extensions().register(MyBdkExtension.class);
        
        // or using the SymphonyBdkBuilder
        final SymphonyBdk bdk = SymphonyBdk.builder()
                .config(loadFromSymphonyDir("config.yaml"))
                .extension(MyBdkExtension.class) 
                .build();
    }
}
```

### Registering Extensions in Spring Boot
To use your extension in the [BDK Spring Boot Starter](./spring-boot/core-starter.md), you simply need to register your 
extension as a bean added to the application context:
```java
@Configuration
public class MyBdkExtensionConfig {

  @Bean
  public MyBdkExtension myBdkExtension() {
    return new MyBdkExtension();
  }
}
```
This way, your extension will automatically be registered within the `ExtensionService`.

## Service Provider Extension
A _Service Provider_ extension is a specific type of extension loaded on demand when calling the 
`ExtensionService#service(Class)` method.

To make your extension _Service Provider_, your extension definition class must implement the `BdkExtensionServiceProvider`
interface along with the `BdkExtension` marker interface: 
```java
/**
 * The <i>Service</i> implementation class.
 */
public class MyBdkExtensionService implements BdkExtensionService {
    
    public void sayHello(String name) {
        System.out.println("Hello, %s!", name); // #noLog4Shell
    }
}
/**
 * The <i>Extension</i> definition class.
 */
public class MyBdkExtension implements BdkExtension, BdkExtensionServiceProvider<MyBdkExtensionService> {

    private final MyBdkExtensionService service = new MyBdkExtensionService();
    
    @Override
    public MyBdkExtensionService getService() {
        return this.service;
    }
}
/**
 * Usage example.
 */
class ExtensionExample {

    public static void main(String[] args) {
        final SymphonyBdk bdk = SymphonyBdk.builder()
                .config(loadFromSymphonyDir("config.yaml"))
                .extension(MyBdkExtension.class)
                .build();
        
        final MyBdkExtensionService service = bdk.extensions().service(MyBdkExtension.class);
        service.sayHello("Symphony");
    }
}
```
