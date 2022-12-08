# Health API
The Health Service is a component at the service layer of the BDK which covers the Health Service part of the [REST API documentation](https://developers.symphony.com/restapi/reference).
More precisely:
* [Health check](https://developers.symphony.com/restapi/reference/health-check-v3)
* [Health check extended](https://developers.symphony.com/restapi/reference/health-check-extended-v3)
* [Agent info](https://developers.symphony.com/restapi/reference/agent-info-v1)


## How to use
The central component for the Health Service is the `HealthService` class, it exposes the service APIs endpoints mentioned above.
The service is accessible from the`SymphonyBdk` object by calling the `health()` method:

```java
@Slf4j
public class Example {
  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    // Get health check extended status
    HealthService health = bdk.health();
    V3Health v3Health = health.healthCheckExtended();
    log.info("Health status: " + v3Health.getStatus());
  }
}
```

The service also provides the datafeed loop connectivity health status.

```java
@Slf4j
public class Example {
  public static void main(String[] args) throws Exception {
    // Create BDK entry point
    final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));

    // Get health check extended status
    HealthService health = bdk.health();
    V3HealthStatus v3HealthStatus = health.datafeedHealthCheck();
    log.info("Health status: " + v3HealthStatus);
  }
}
```
