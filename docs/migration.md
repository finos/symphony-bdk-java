# Migration guide to Symphony BDK 2.0

This guide provides information about how to migrate from Symphony BDK 1.0 to BDK 2.0. Migration for the following topics will be detailed here:
- Dependencies
- Bot's configuration
- Symphony BDK entry point
- BDK services
- Event listeners

## Dependencies
In Java BDK 1.0, the bot had dependencies on `symphony-api-client-java` in addition to the application framework (SpringBoot for e.g). With BDK 2.0, we can replace both of them with `symphony-bdk-core-spring-boot-starter`.
If your project is not framework based, dependencies such as *jersey* and *freemarker* should be added as well.
### Spring Boot based project

<table>
<tr>
<th>Java BDK 1.0</th>
<th>Java BDK 2.0</th>
</tr>
<tr>
<td>
<pre>&lt;<span class="pl-ent">parent</span>&gt;
    &lt;<span class="pl-ent">groupId</span>&gt;org.springframework.boot&lt;/<span class="pl-ent">groupId</span>&gt;
    &lt;<span class="pl-ent">artifactId</span>&gt;spring-boot-starter-parent&lt;/<span class="pl-ent">artifactId</span>&gt;
    &lt;<span class="pl-ent">version</span>&gt;2.3.5.RELEASE&lt;/<span class="pl-ent">version</span>&gt;
    &lt;<span class="pl-ent">relativePath</span>/&gt;
&lt;/<span class="pl-ent">parent</span>&gt;

&lt;<span class="pl-ent">dependencies</span>&gt;
    &lt;<span class="pl-ent">dependency</span>&gt;
        &lt;<span class="pl-ent">groupId</span>&gt;com.symphony.platformsolutions&lt;/<span class="pl-ent">groupId</span>&gt;
        &lt;<span class="pl-ent">artifactId</span>&gt;symphony-api-client-java&lt;/<span class="pl-ent">artifactId</span>&gt;
        &lt;<span class="pl-ent">version</span>&gt;1.3.3&lt;/<span class="pl-ent">version</span>&gt;
    &lt;/<span class="pl-ent">dependency</span>&gt;      
&lt;/<span class="pl-ent">dependencies</span>&gt;</pre>
</td>

<td>
<pre>&lt;<span class="pl-ent">dependencyManagement</span>&gt;
    &lt;<span class="pl-ent">dependencies</span>&gt;
        &lt;<span class="pl-ent">dependency</span>&gt;
            &lt;<span class="pl-ent">groupId</span>&gt;org.finos.symphony.bdk&lt;/<span class="pl-ent">groupId</span>&gt;
            &lt;<span class="pl-ent">artifactId</span>&gt;symphony-bdk-bom&lt;/<span class="pl-ent">artifactId</span>&gt;
            &lt;<span class="pl-ent">version</span>&gt;2.1.1&lt;/<span class="pl-ent">version</span>&gt;
            &lt;<span class="pl-ent">type</span>&gt;pom&lt;/<span class="pl-ent">type</span>&gt;
            &lt;<span class="pl-ent">scope</span>&gt;import&lt;/<span class="pl-ent">scope</span>&gt;
        &lt;/<span class="pl-ent">dependency</span>&gt;
    &lt;/<span class="pl-ent">dependencies</span>&gt;
&lt;/<span class="pl-ent">dependencyManagement</span>&gt;

&lt;<span class="pl-ent">dependencies</span>&gt;
    &lt;<span class="pl-ent">dependency</span>&gt;
        &lt;<span class="pl-ent">groupId</span>&gt;org.finos.symphony.bdk&lt;/<span class="pl-ent">groupId</span>&gt;
        &lt;<span class="pl-ent">artifactId</span>&gt;symphony-bdk-core-spring-boot-starter&lt;/<span class="pl-ent">artifactId</span>&gt;
    &lt;/<span class="pl-ent">dependency</span>&gt;
    &lt;<span class="pl-ent">dependency</span>&gt;
        &lt;<span class="pl-ent">groupId</span>&gt;org.springframework.boot&lt;/<span class="pl-ent">groupId</span>&gt;
        &lt;<span class="pl-ent">artifactId</span>&gt;spring-boot-starter&lt;/<span class="pl-ent">artifactId</span>&gt;
    &lt;/<span class="pl-ent">dependency</span>&gt;
&lt;/<span class="pl-ent">dependencies</span>&gt;</pre>
</td>
</tr>
</table>

### Non framework based project

<table>
<tr>
<th>Java BDK 1.0</th>
<th>Java BDK 2.0</th>
</tr>
<tr>
<td>

<pre>&lt;<span class="pl-ent">dependencies</span>&gt;
    &lt;<span class="pl-ent">dependency</span>&gt;
        &lt;<span class="pl-ent">groupId</span>&gt;com.symphony.platformsolutions&lt;/<span class="pl-ent">groupId</span>&gt;
        &lt;<span class="pl-ent">artifactId</span>&gt;symphony-api-client-java&lt;/<span class="pl-ent">artifactId</span>&gt;
        &lt;<span class="pl-ent">version</span>&gt;1.3.3&lt;/<span class="pl-ent">version</span>&gt;
    &lt;/<span class="pl-ent">dependency</span>&gt;
&lt;/<span class="pl-ent">dependencies</span>&gt;</pre>
</td>
<td>

<pre>&lt;<span class="pl-ent">dependencyManagement</span>&gt;
    &lt;<span class="pl-ent">dependencies</span>&gt;
        &lt;<span class="pl-ent">dependency</span>&gt;
            &lt;<span class="pl-ent">groupId</span>&gt;org.finos.symphony.bdk&lt;/<span class="pl-ent">groupId</span>&gt;
            &lt;<span class="pl-ent">artifactId</span>&gt;symphony-bdk-bom&lt;/<span class="pl-ent">artifactId</span>&gt;
            &lt;<span class="pl-ent">version</span>&gt;2.1.1&lt;/<span class="pl-ent">version</span>&gt;
            &lt;<span class="pl-ent">type</span>&gt;pom&lt;/<span class="pl-ent">type</span>&gt;
            &lt;<span class="pl-ent">scope</span>&gt;import&lt;/<span class="pl-ent">scope</span>&gt;
        &lt;/<span class="pl-ent">dependency</span>&gt;
    &lt;/<span class="pl-ent">dependencies</span>&gt;
&lt;/<span class="pl-ent">dependencyManagement</span>&gt;

&lt;<span class="pl-ent">dependencies</span>&gt;
    &lt;<span class="pl-ent">dependency</span>&gt;
        &lt;<span class="pl-ent">groupId</span>&gt;org.finos.symphony.bdk&lt;/<span class="pl-ent">groupId</span>&gt;
        &lt;<span class="pl-ent">artifactId</span>&gt;symphony-bdk-core&lt;/<span class="pl-ent">artifactId</span>&gt;
    &lt;/<span class="pl-ent">dependency</span>&gt;
    &lt;<span class="pl-ent">dependency</span>&gt;
        &lt;<span class="pl-ent">groupId</span>&gt;org.finos.symphony.bdk&lt;/<span class="pl-ent">groupId</span>&gt;
        &lt;<span class="pl-ent">artifactId</span>&gt;symphony-bdk-http-jersey2&lt;/<span class="pl-ent">artifactId</span>&gt; <span class="pl-c"><span class="pl-c">&lt;!--</span> or symphony-bdk-http-webclient <span class="pl-c">--&gt;</span></span>
        &lt;<span class="pl-ent">scope</span>&gt;runtime&lt;/<span class="pl-ent">scope</span>&gt;
    &lt;/<span class="pl-ent">dependency</span>&gt;
    &lt;<span class="pl-ent">dependency</span>&gt;
        &lt;<span class="pl-ent">groupId</span>&gt;org.finos.symphony.bdk&lt;/<span class="pl-ent">groupId</span>&gt;
        &lt;<span class="pl-ent">artifactId</span>&gt;symphony-bdk-template-freemarker&lt;/<span class="pl-ent">artifactId</span>&gt;  <span class="pl-c"><span class="pl-c">&lt;!--</span> or symphony-bdk-http-handlebars <span class="pl-c">--&gt;</span></span>
        &lt;<span class="pl-ent">scope</span>&gt;runtime&lt;/<span class="pl-ent">scope</span>&gt;
    &lt;/<span class="pl-ent">dependency</span>&gt;
&lt;/<span class="pl-ent">dependencies</span>&gt;</pre>
</td>
</tr>
</table>

## Bot's configuration
In Java BDK 1.0, two configuration files were required : `application.yaml` (or `application.config`) and `bot-config.json`. Java BDK 2.0 lightened the configuration. Therefore, only `src/main/resources/config.yaml` file is required with a minimum of configuration.

Bot’s configuration in Java BDK 2.0 should have the following properties:
- `host`: pod’s host name

- `bot.username`: bot’s (or service account) username

- `bot.privatekey.path`: bot’s private key path

If your bot is deployed on premise, the following properties are required as well:

- `agent`: on premise agent configuration

- `keyManager`: on premise Key manager configuration

- `proxy`: proxy configuration to reach the pod

- `ssl.trustStore`: trust store path and password

> Click [here](./configuration.md) for more detailed documentation about BDK configuration

### Minimal configuration example
#### Spring Boot based project
<table>
<tr>
<th>Java BDK 1.0</th>
<th>Java BDK 2.0</th>
</tr>
<tr>
<td>

<h4><a id="user-content-applicationyaml" class="anchor" aria-hidden="true" href="#applicationyaml"><svg class="octicon octicon-link" viewBox="0 0 16 16" version="1.1" width="16" height="16" aria-hidden="true"><path fill-rule="evenodd" d="M7.775 3.275a.75.75 0 001.06 1.06l1.25-1.25a2 2 0 112.83 2.83l-2.5 2.5a2 2 0 01-2.83 0 .75.75 0 00-1.06 1.06 3.5 3.5 0 004.95 0l2.5-2.5a3.5 3.5 0 00-4.95-4.95l-1.25 1.25zm-4.69 9.64a2 2 0 010-2.83l2.5-2.5a2 2 0 012.83 0 .75.75 0 001.06-1.06 3.5 3.5 0 00-4.95 0l-2.5 2.5a3.5 3.5 0 004.95 4.95l1.25-1.25a.75.75 0 00-1.06-1.06l-1.25 1.25a2 2 0 01-2.83 0z"></path></svg></a><strong><code>application.yaml</code>:</strong></h4>

<pre><span class="pl-ent">server</span>:
    <span class="pl-ent">port</span>: <span class="pl-c1">8080</span>
    <span class="pl-ent">servlet</span>:
        <span class="pl-ent">context-path</span>: <span class="pl-s"><span class="pl-pds">"</span>/botapp<span class="pl-pds">"</span></span>

<span class="pl-ent">certs</span>: <span class="pl-s">/path/to/private/key</span>
<span class="pl-ent">bot-config</span>: <span class="pl-s">/path/to/bot-config.json</span></pre>

<h4><a id="user-content-bot-configjson" class="anchor" aria-hidden="true" href="#bot-configjson"><svg class="octicon octicon-link" viewBox="0 0 16 16" version="1.1" width="16" height="16" aria-hidden="true"><path fill-rule="evenodd" d="M7.775 3.275a.75.75 0 001.06 1.06l1.25-1.25a2 2 0 112.83 2.83l-2.5 2.5a2 2 0 01-2.83 0 .75.75 0 00-1.06 1.06 3.5 3.5 0 004.95 0l2.5-2.5a3.5 3.5 0 00-4.95-4.95l-1.25 1.25zm-4.69 9.64a2 2 0 010-2.83l2.5-2.5a2 2 0 012.83 0 .75.75 0 001.06-1.06 3.5 3.5 0 00-4.95 0l-2.5 2.5a3.5 3.5 0 004.95 4.95l1.25-1.25a.75.75 0 00-1.06-1.06l-1.25 1.25a2 2 0 01-2.83 0z"></path></svg></a><strong><code>bot-config.json</code>:</strong></h4>

<pre>{
  <span class="pl-s"><span class="pl-pds">"</span>sessionAuthHost<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>session.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>sessionAuthPort<span class="pl-pds">"</span></span>: <span class="pl-c1">443</span>,
  <span class="pl-s"><span class="pl-pds">"</span>keyAuthHost<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>keyAuthPort<span class="pl-pds">"</span></span>: <span class="pl-c1">443</span>,
  <span class="pl-s"><span class="pl-pds">"</span>podHost<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>pod.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>podPort<span class="pl-pds">"</span></span>: <span class="pl-c1">443</span>,
  <span class="pl-s"><span class="pl-pds">"</span>agentHost<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>agent.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>agentPort<span class="pl-pds">"</span></span>: <span class="pl-c1">443</span>,
  <span class="pl-s"><span class="pl-pds">"</span>botPrivateKeyPath<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>certs/<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>botPrivateKeyName<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>/path/to/bot/rsa-privatekey.pem<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>botUsername<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>bot-username<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>authTokenRefreshPeriod<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>30<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>authenticationFilterUrlPattern<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>/secure/<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>showFirehoseErrors<span class="pl-pds">"</span></span>: <span class="pl-c1">false</span>,
  <span class="pl-s"><span class="pl-pds">"</span>connectionTimeout<span class="pl-pds">"</span></span>: <span class="pl-c1">45000</span>, 
  <span class="pl-s"><span class="pl-pds">"</span>proxyURL<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxy.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>proxyUsername<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxy.username<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>proxyPassword<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxy.password<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>keyManagerProxyURL<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>keyManagerProxyUsername<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.username<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>keyManagerProxyPassword<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.password<span class="pl-pds">"</span></span>
}</pre>
</td>
<td>

<p>Only <code>application.yaml</code> file is required. It can be in <em>JSON</em></p> 


<pre>{
    <span class="pl-s"><span class="pl-pds">"</span>bdk<span class="pl-pds">"</span></span>: {
        <span class="pl-s"><span class="pl-pds">"</span>host<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>acme.symphony.com<span class="pl-pds">"</span></span>,
        <span class="pl-s"><span class="pl-pds">"</span>bot<span class="pl-pds">"</span></span>: {
            <span class="pl-s"><span class="pl-pds">"</span>username<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>bot-username<span class="pl-pds">"</span></span>,
            <span class="pl-s"><span class="pl-pds">"</span>privateKey<span class="pl-pds">"</span></span>: {
                <span class="pl-s"><span class="pl-pds">"</span>path<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>/path/to/bot/rsa-privatekey.pem<span class="pl-pds">"</span></span>
            }
        },
        <span class="pl-s"><span class="pl-pds">"</span>proxy<span class="pl-pds">"</span></span>: {
            <span class="pl-s"><span class="pl-pds">"</span>host<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxy.symphony.com<span class="pl-pds">"</span></span>,
            <span class="pl-s"><span class="pl-pds">"</span>port<span class="pl-pds">"</span></span>: <span class="pl-c1">1234</span>,
            <span class="pl-s"><span class="pl-pds">"</span>username<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxyuser<span class="pl-pds">"</span></span>,
            <span class="pl-s"><span class="pl-pds">"</span>password<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxypassword<span class="pl-pds">"</span></span>
        },
        <span class="pl-s"><span class="pl-pds">"</span>keyManager<span class="pl-pds">"</span></span>: {
            <span class="pl-s"><span class="pl-pds">"</span>proxy<span class="pl-pds">"</span></span>: {
                <span class="pl-s"><span class="pl-pds">"</span>host<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.symphony.com<span class="pl-pds">"</span></span>,
                <span class="pl-s"><span class="pl-pds">"</span>port<span class="pl-pds">"</span></span>: <span class="pl-c1">1234</span>,
                <span class="pl-s"><span class="pl-pds">"</span>username<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.username<span class="pl-pds">"</span></span>,
                <span class="pl-s"><span class="pl-pds">"</span>password<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.password<span class="pl-pds">"</span></span>
            }
        }   
    }
}</pre>

<p>or <em>YAML</em> format.</p>

<pre><span class="pl-ent">bdk</span>:
    <span class="pl-ent">host</span>: <span class="pl-s">acme.symphony.com</span>
    <span class="pl-ent">bot</span>:
        <span class="pl-ent">username</span>: <span class="pl-s">bot-username</span>
        <span class="pl-ent">privateKey</span>:
            <span class="pl-ent">path</span>: <span class="pl-s"><span class="pl-pds">"</span>/path/to/bot/rsa-privatekey.pem<span class="pl-pds">"</span></span>
    <span class="pl-ent">proxy</span>:
        <span class="pl-ent">host</span>: <span class="pl-s">proxy.symphony.com</span>
        <span class="pl-ent">port</span>: <span class="pl-c1">1234</span>
        <span class="pl-ent">username</span>: <span class="pl-s">proxyuser</span>
        <span class="pl-ent">password</span>: <span class="pl-s">proxypassword</span>
    <span class="pl-ent">keyManager</span>:
        <span class="pl-ent">proxy</span>:
            <span class="pl-ent">host</span>: <span class="pl-s">km.proxy.symphony.com</span>
            <span class="pl-ent">port</span>: <span class="pl-c1">1234</span>
            <span class="pl-ent">username</span>: <span class="pl-s">km.proxy.username</span>
            <span class="pl-ent">password</span>: <span class="pl-s">km.proxy.passwor</span></pre>
</td>
</tr>
</table>

#### Non framework based project
<table>
<tr>
<th>Java BDK 1.0</th>
<th>Java BDK 2.0</th>
</tr>
<tr>
<td>

<h4><a id="user-content-bot-configjson-1" class="anchor" aria-hidden="true" href="#bot-configjson-1"><svg class="octicon octicon-link" viewBox="0 0 16 16" version="1.1" width="16" height="16" aria-hidden="true"><path fill-rule="evenodd" d="M7.775 3.275a.75.75 0 001.06 1.06l1.25-1.25a2 2 0 112.83 2.83l-2.5 2.5a2 2 0 01-2.83 0 .75.75 0 00-1.06 1.06 3.5 3.5 0 004.95 0l2.5-2.5a3.5 3.5 0 00-4.95-4.95l-1.25 1.25zm-4.69 9.64a2 2 0 010-2.83l2.5-2.5a2 2 0 012.83 0 .75.75 0 001.06-1.06 3.5 3.5 0 00-4.95 0l-2.5 2.5a3.5 3.5 0 004.95 4.95l1.25-1.25a.75.75 0 00-1.06-1.06l-1.25 1.25a2 2 0 01-2.83 0z"></path></svg></a><strong><code>bot-config.json</code>:</strong></h4>

<pre>{
  <span class="pl-s"><span class="pl-pds">"</span>sessionAuthHost<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>session.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>sessionAuthPort<span class="pl-pds">"</span></span>: <span class="pl-c1">443</span>,
  <span class="pl-s"><span class="pl-pds">"</span>keyAuthHost<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>keyAuthPort<span class="pl-pds">"</span></span>: <span class="pl-c1">443</span>,
  <span class="pl-s"><span class="pl-pds">"</span>podHost<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>pod.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>podPort<span class="pl-pds">"</span></span>: <span class="pl-c1">443</span>,
  <span class="pl-s"><span class="pl-pds">"</span>agentHost<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>agent.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>agentPort<span class="pl-pds">"</span></span>: <span class="pl-c1">443</span>,
  <span class="pl-s"><span class="pl-pds">"</span>botPrivateKeyPath<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>certs/<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>botPrivateKeyName<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>/path/to/bot/rsa-privatekey.pem<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>botUsername<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>bot-username<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>authTokenRefreshPeriod<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>30<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>authenticationFilterUrlPattern<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>/secure/<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>showFirehoseErrors<span class="pl-pds">"</span></span>: <span class="pl-c1">false</span>,
  <span class="pl-s"><span class="pl-pds">"</span>connectionTimeout<span class="pl-pds">"</span></span>: <span class="pl-c1">45000</span>, 
  <span class="pl-s"><span class="pl-pds">"</span>proxyURL<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxy.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>proxyUsername<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxy.username<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>proxyPassword<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxy.password<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>keyManagerProxyURL<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.symphony.com<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>keyManagerProxyUsername<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.username<span class="pl-pds">"</span></span>,
  <span class="pl-s"><span class="pl-pds">"</span>keyManagerProxyPassword<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.password<span class="pl-pds">"</span></span>
}</pre>
</td>
<td>

<p>Only <code>config.yaml</code> file is required. It can be in <em>JSON</em></p>

<pre>{
    <span class="pl-s"><span class="pl-pds">"</span>host<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>acme.symphony.com<span class="pl-pds">"</span></span>,
    <span class="pl-s"><span class="pl-pds">"</span>bot<span class="pl-pds">"</span></span>: {
        <span class="pl-s"><span class="pl-pds">"</span>username<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>bot-username<span class="pl-pds">"</span></span>,
        <span class="pl-s"><span class="pl-pds">"</span>privateKey<span class="pl-pds">"</span></span>: {
            <span class="pl-s"><span class="pl-pds">"</span>path<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>/path/to/bot/rsa-privatekey.pem<span class="pl-pds">"</span></span>
        }
    },
    <span class="pl-s"><span class="pl-pds">"</span>proxy<span class="pl-pds">"</span></span>: {
        <span class="pl-s"><span class="pl-pds">"</span>host<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxy.symphony.com<span class="pl-pds">"</span></span>,
        <span class="pl-s"><span class="pl-pds">"</span>port<span class="pl-pds">"</span></span>: <span class="pl-c1">1234</span>,
        <span class="pl-s"><span class="pl-pds">"</span>username<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxyuser<span class="pl-pds">"</span></span>,
        <span class="pl-s"><span class="pl-pds">"</span>password<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>proxypassword<span class="pl-pds">"</span></span>
    },
    <span class="pl-s"><span class="pl-pds">"</span>keyManager<span class="pl-pds">"</span></span>: {
        <span class="pl-s"><span class="pl-pds">"</span>proxy<span class="pl-pds">"</span></span>: {
            <span class="pl-s"><span class="pl-pds">"</span>host<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.symphony.com<span class="pl-pds">"</span></span>,
            <span class="pl-s"><span class="pl-pds">"</span>port<span class="pl-pds">"</span></span>: <span class="pl-c1">1234</span>,
            <span class="pl-s"><span class="pl-pds">"</span>username<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.username<span class="pl-pds">"</span></span>,
            <span class="pl-s"><span class="pl-pds">"</span>password<span class="pl-pds">"</span></span>: <span class="pl-s"><span class="pl-pds">"</span>km.proxy.password<span class="pl-pds">"</span></span>
        }
    }
}</pre>

<p>or <em>YAML</em> format.</p>

<pre><span class="pl-ent">host</span>: <span class="pl-s">acme.symphony.com</span>
<span class="pl-ent">bot</span>:
    <span class="pl-ent">username</span>: <span class="pl-s">bot-username</span>
    <span class="pl-ent">privateKey</span>:
        <span class="pl-ent">path</span>: <span class="pl-s"><span class="pl-pds">"</span>/path/to/bot/rsa-privatekey.pem<span class="pl-pds">"</span></span>
<span class="pl-ent">proxy</span>:
    <span class="pl-ent">host</span>: <span class="pl-s">proxy.symphony.com</span>
    <span class="pl-ent">port</span>: <span class="pl-c1">1234</span>
    <span class="pl-ent">username</span>: <span class="pl-s">proxyuser</span>
    <span class="pl-ent">password</span>: <span class="pl-s">proxypassword</span>
<span class="pl-ent">keyManager</span>:
    <span class="pl-ent">proxy</span>:
        <span class="pl-ent">host</span>: <span class="pl-s">km.proxy.symphony.com</span>
        <span class="pl-ent">port</span>: <span class="pl-c1">1234</span>
        <span class="pl-ent">username</span>: <span class="pl-s">km.proxy.username</span>
        <span class="pl-ent">password</span>: <span class="pl-s">km.proxy.password</span></pre>
</td>
</tr>
</table>


## Symphony BDK entry point
The `SymphonyBdk` class acts as an entry point into the library and provides a [fluent API](./fluent-api.md) to access to the main BDK features such as [Datafeed](./datafeed.md), services or [Activities](./activity-api.md).
With this class, all BDK services are auto-configured and can be directly accessed without any bot client. Examples of this class usage will be provided in next parts.
> Click [here](./fluent-api.md) for more detailed documentation about Symphony BDK fluent api

## BDK services
If you use a Spring Boot based project, BDK services can be directly injected in your bot service. If it is not a framework based project, BDK services can be retrieved with Symphony BDK entry point.
To illustrate this, let's take an example of a bot reacting to *ping pong* messages.
<table>
<tr>
<th>Java BDK 1.0</th>
<th>Java BDK 2.0</th>
</tr>
<tr>
<td>

<p>In Java BDK 1.0, the main class should have <em>SymBotClient</em> object that the bot service can use to call <code>sendMessage()</code> method.</p>

<pre><span class="pl-k">@Slf4j</span>
<span class="pl-k">@Service</span>
<span class="pl-k">public</span> <span class="pl-k">class</span> <span class="pl-en">PingPongBotService</span> {
  <span class="pl-k">public</span> <span class="pl-en">handleIncomingMessage</span>(<span class="pl-smi">InboundMessage</span> <span class="pl-v">message</span>, <span class="pl-smi">StreamTypes</span> <span class="pl-v">streamType</span>) {
      <span class="pl-smi">String</span> streamId <span class="pl-k">=</span> message<span class="pl-k">.</span>getStream()<span class="pl-k">.</span>getStreamId();
      <span class="pl-smi">String</span> messageText <span class="pl-k">=</span> message<span class="pl-k">.</span>getMessageText();

      <span class="pl-k">switch</span> (messageText) {
          <span class="pl-k">case</span> <span class="pl-s"><span class="pl-pds">"</span>/ping<span class="pl-pds">"</span></span><span class="pl-k">:</span>
              <span class="pl-smi">PingPongBot</span><span class="pl-k">.</span>sendMessage(streamId, <span class="pl-s"><span class="pl-pds">"</span>pong<span class="pl-pds">"</span></span>);
              <span class="pl-k">break</span>;
          <span class="pl-k">case</span> <span class="pl-s"><span class="pl-pds">"</span>/pong<span class="pl-pds">"</span></span><span class="pl-k">:</span>
              <span class="pl-smi">PingPongBot</span><span class="pl-k">.</span>sendMessage(streamId, <span class="pl-s"><span class="pl-pds">"</span>ping<span class="pl-pds">"</span></span>);
              <span class="pl-k">break</span>;
          <span class="pl-k">default</span><span class="pl-k">:</span>
            <span class="pl-smi">PingPongBot</span><span class="pl-k">.</span>sendMessage(streamId, <span class="pl-s"><span class="pl-pds">"</span>Sorry, I don't understand!<span class="pl-pds">"</span></span>);
            <span class="pl-k">break</span>;
      }
  }
}

<span class="pl-k">@Slf4j</span>
<span class="pl-k">public</span> <span class="pl-k">class</span> <span class="pl-en">PingPongBot</span> {
  <span class="pl-k">private</span> <span class="pl-k">static</span> <span class="pl-smi">SymBotClient</span> botClient;
  
  <span class="pl-k">public</span> <span class="pl-en">PingPongBot</span>(<span class="pl-smi">IMListenerImpl</span> <span class="pl-v">imListener</span>, <span class="pl-smi">RoomListenerImpl</span> <span class="pl-v">roomListener</span>, <span class="pl-smi">ElementsListenerImpl</span> <span class="pl-v">elementsListener</span>) {
      <span class="pl-k">try</span> {
          <span class="pl-c"><span class="pl-c">//</span> Bot init</span>
          botClient <span class="pl-k">=</span> <span class="pl-smi">SymBotClient</span><span class="pl-k">.</span>initBotRsa(<span class="pl-s"><span class="pl-pds">"</span>config.json<span class="pl-pds">"</span></span>);
      
          <span class="pl-c"><span class="pl-c">//</span> Bot listeners</span>
          botClient<span class="pl-k">.</span>getDatafeedEventsService()<span class="pl-k">.</span>addListeners(imListener, roomListener, elementsListener);
      } <span class="pl-k">catch</span> (<span class="pl-smi">Exception</span> e) {
        log<span class="pl-k">.</span>error(<span class="pl-s"><span class="pl-pds">"</span>Error: {}<span class="pl-pds">"</span></span>, e<span class="pl-k">.</span>getMessage());
      }
  }
  
  <span class="pl-k">public</span> <span class="pl-k">static</span> <span class="pl-k">void</span> <span class="pl-en">sendMessage</span>(<span class="pl-smi">String</span> <span class="pl-v">streamId</span>, <span class="pl-smi">String</span> <span class="pl-v">message</span>) {
      botClient<span class="pl-k">.</span>getMessageClient<span class="pl-k">.</span>sendMessage(streamId, <span class="pl-k">new</span> <span class="pl-smi">OutboundMessage</span>(message));
  }
}</pre>
</td>
<td>

<p>In Java BDK 2.0, <code>MessageService</code> can be injected in the Bot service class to directly call <code>send()</code> without needing <code>SymBotClient</code>. <em>(The example below uses a Spring Boot based project)</em></p>

<pre><span class="pl-k">@Slf4j</span>
<span class="pl-k">@Service</span>
<span class="pl-k">public</span> <span class="pl-k">class</span> <span class="pl-en">PingPongBotService</span> {

    <span class="pl-k">private</span> <span class="pl-k">final</span> <span class="pl-smi">MessageService</span> messageService;

    <span class="pl-k">public</span> <span class="pl-en">PingPongBotService</span>(<span class="pl-smi">MessageService</span> <span class="pl-v">messageService</span>) {
        <span class="pl-c1">this</span><span class="pl-k">.</span>messageService <span class="pl-k">=</span> messageService;
    }

    <span class="pl-k">public</span> <span class="pl-en">handleIncomingMessage</span>(<span class="pl-smi">V4Message</span> <span class="pl-v">message</span>, <span class="pl-smi">StreamType</span>.<span class="pl-smi">TypeEnum</span> <span class="pl-v">streamType</span>) {
        <span class="pl-smi">String</span> streamId <span class="pl-k">=</span> message<span class="pl-k">.</span>getStream()<span class="pl-k">.</span>getStreamId();
        <span class="pl-smi">String</span> messageText <span class="pl-k">=</span> message<span class="pl-k">.</span>getMessage();

        <span class="pl-k">switch</span> (messageText) {
            <span class="pl-k">case</span> <span class="pl-s"><span class="pl-pds">"</span>/ping<span class="pl-pds">"</span></span><span class="pl-k">:</span>
                <span class="pl-c1">this</span><span class="pl-k">.</span>messageService<span class="pl-k">.</span>send(streamId, <span class="pl-s"><span class="pl-pds">"</span>pong<span class="pl-pds">"</span></span>);
                <span class="pl-k">break</span>;
            <span class="pl-k">case</span> <span class="pl-s"><span class="pl-pds">"</span>/pong<span class="pl-pds">"</span></span><span class="pl-k">:</span>
                <span class="pl-c1">this</span><span class="pl-k">.</span>messageService<span class="pl-k">.</span>send(streamId, <span class="pl-s"><span class="pl-pds">"</span>ping<span class="pl-pds">"</span></span>);
                <span class="pl-k">break</span>;
            <span class="pl-k">default</span><span class="pl-k">:</span>
                <span class="pl-c1">this</span><span class="pl-k">.</span>messageService<span class="pl-k">.</span>send(streamId, <span class="pl-s"><span class="pl-pds">"</span>Sorry, I don't understand!<span class="pl-pds">"</span></span>);
                <span class="pl-k">break</span>;
        }
    }
}


<span class="pl-k">@Component</span>
<span class="pl-k">public</span> <span class="pl-k">class</span> <span class="pl-en">RealTimeEventComponent</span> {

    <span class="pl-k">private</span> <span class="pl-k">final</span> <span class="pl-smi">PingPongBotService</span> pingPongBotService;

    <span class="pl-k">public</span> <span class="pl-en">RealTimeEventComponent</span>(<span class="pl-smi">PingPongBotService</span> <span class="pl-v">pingPongBotService</span>) {
        <span class="pl-c1">this</span><span class="pl-k">.</span>pingPongBotService <span class="pl-k">=</span> pingPongBotService;
    }

    <span class="pl-k">@EventListener</span>
    <span class="pl-k">public</span> <span class="pl-k">void</span> <span class="pl-en">onMessageSent</span>(<span class="pl-k">RealTimeEvent&lt;<span class="pl-smi">V4MessageSent</span>&gt;</span> <span class="pl-v">event</span>) {
        <span class="pl-c1">this</span><span class="pl-k">.</span>pingPongBotService<span class="pl-k">.</span>handleIncomingMessage(event<span class="pl-k">.</span>getSource()<span class="pl-k">.</span>getMessage,
                <span class="pl-smi">StreamType</span><span class="pl-k">.</span><span class="pl-smi">TypeEnum</span><span class="pl-k">.</span>formValue(event<span class="pl-k">.</span>getSource()<span class="pl-k">.</span>getMessage<span class="pl-k">.</span>getStream<span class="pl-k">.</span>getStreamType()));
    }
}</pre>
</td>
</tr>
</table>

<p>An example of non framework based project using <code>SymphonyBdk</code> to retrieve BDK services:</p>

<pre><span class="pl-k">@Slf4j</span>
<span class="pl-k">public</span> <span class="pl-k">class</span> <span class="pl-en">GreetingsAllRoomsBot</span> {

  <span class="pl-k">public</span> <span class="pl-k">static</span> <span class="pl-k">void</span> <span class="pl-en">main</span>(<span class="pl-k">String</span>[] <span class="pl-v">args</span>) <span class="pl-k">throws</span> <span class="pl-smi">Exception</span> {

    <span class="pl-k">final</span> <span class="pl-smi">SymphonyBdk</span> bdk <span class="pl-k">=</span> <span class="pl-k">new</span> <span class="pl-smi">SymphonyBdk</span>(loadFromSymphonyDir(<span class="pl-s"><span class="pl-pds">"</span>config.yaml<span class="pl-pds">"</span></span>));

    <span class="pl-c"><span class="pl-c">//</span> list all rooms</span>
    <span class="pl-k">Stream&lt;<span class="pl-smi">StreamAttributes</span>&gt;</span> rooms <span class="pl-k">=</span> bdk<span class="pl-k">.</span>streams()<span class="pl-k">.</span>listAllStreams(<span class="pl-k">new</span> <span class="pl-smi">StreamFilter</span>());
    
    rooms<span class="pl-k">.</span>forEach(streamAttributes <span class="pl-k">-</span><span class="pl-k">&gt;</span> {
          <span class="pl-c"><span class="pl-c">//</span> send message to room</span>
          bdk<span class="pl-k">.</span>messages()<span class="pl-k">.</span>send(streamAttributes<span class="pl-k">.</span>getId(), <span class="pl-s"><span class="pl-pds">"</span>Hello world!<span class="pl-pds">"</span></span>);
          log<span class="pl-k">.</span>info(<span class="pl-s"><span class="pl-pds">"</span>Message sent to room with: id:{}, name:{}<span class="pl-pds">"</span></span>, streamAttributes<span class="pl-k">.</span>getId(),
              streamAttributes<span class="pl-k">.</span>getRoomAttributes()<span class="pl-k">.</span>getName());
    });
    
    bdk<span class="pl-k">.</span>datafeed()<span class="pl-k">.</span>start();
  }
}</pre>

<p>A list of BDK available services can be found <a href="/finos/symphony-bdk-java/blob/main/docs/fluent-api.md">here</a></p>

## Event listeners
Java BDK 2.0 comes with a simplified way to handle event listeners.
<table>
<tr>
<th>Java BDK 1.0</th>
<th>Java BDK 2.0</th>
</tr>
<tr>
<td>

In Java BDK 1.0, the bot had to implement 3 listeners classes: 
- one for IM (1 to 1 conversation)
- one for MIM (room)
- one for Symphony elements

<pre><span class="pl-k">@Slf4j</span>
<span class="pl-k">@Service</span>
<span class="pl-k">public</span> <span class="pl-k">class</span> <span class="pl-en">ElementsListenerImpl</span> <span class="pl-k">implements</span> <span class="pl-e">ElementsListener</span> {
    <span class="pl-k">public</span> <span class="pl-k">void</span> <span class="pl-en">onElementsAction</span>(<span class="pl-smi">User</span> <span class="pl-v">initiator</span>, <span class="pl-smi">SymphonyElementsAction</span> <span class="pl-v">action</span>) {<span class="pl-c1">...</span>}
}

<span class="pl-k">@Slf4j</span>
<span class="pl-k">@Service</span>
<span class="pl-k">public</span> <span class="pl-k">class</span> <span class="pl-en">IMListenerImpl</span> <span class="pl-k">implements</span> <span class="pl-e">ElementsListener</span> {
    <span class="pl-k">public</span> <span class="pl-k">void</span> <span class="pl-en">onIMMessage</span>(<span class="pl-smi">InboundMessage</span> <span class="pl-v">msg</span>) {<span class="pl-c1">...</span>}
}

<span class="pl-k">@Slf4j</span>
<span class="pl-k">@Service</span>
<span class="pl-k">public</span> <span class="pl-k">class</span> <span class="pl-en">RoomListenerImpl</span> <span class="pl-k">implements</span> <span class="pl-e">RoomListener</span> {
    <span class="pl-k">public</span> <span class="pl-k">void</span> <span class="pl-en">onRoomMessage</span>(<span class="pl-smi">InboundMessage</span> <span class="pl-v">msg</span>) {<span class="pl-c1">...</span>}
}</pre>

</td>
<td>

<p>In Java BDK 2.0, only one component <code>RealTimeEventComponent</code> has to be implemented with two methods having <code>@EventListener</code> annotation: This works provided we have the correct parameters with correct types. The 3 classes can be factored in one single component. <em>(The example below uses a Spring Boot based project)</em></p>
<pre><span class="pl-k">public</span> <span class="pl-k">class</span> <span class="pl-en">RealTimeEventComponent</span> {
    <span class="pl-k">@EventListener</span>
    <span class="pl-k">public</span> <span class="pl-k">void</span> <span class="pl-en">onMessageSent</span>(<span class="pl-k">RealTimeEvent&lt;<span class="pl-smi">V4MessageSent</span>&gt;</span> <span class="pl-v">event</span>) {<span class="pl-c1">...</span>}

    <span class="pl-k">@EventListener</span>
    <span class="pl-k">public</span> <span class="pl-en">onElementsAction</span>(<span class="pl-k">RealTimeEvent&lt;<span class="pl-smi">V4SymphonyElementsAction</span>&gt;</span> <span class="pl-v">event</span>) {<span class="pl-c1">...</span>}
}</pre>
</td>
</tr>
</table>

## Models
Models names have been changed in Java BDK 2.0. They actually follow the models in Swagger specification of Symphony's public API. Field names in Java classes correspond to the field names in API's JSON payloads. 
This requires to change some variables names in your legacy bots.

Example of types to change : *(non exhaustive list, please refer to our [public API specs](//https://github.com/symphonyoss/symphony-api-spec))*
- `SymphonyElementsAction` → `V4SymphonyElementsAction`
- `User` → `V4User`
- `InboundMessage` → `V4Message`
- `StreamTypes` → `StreamType.TypeEnum`
- `RoomInfo` → `V3RoomDetail`
