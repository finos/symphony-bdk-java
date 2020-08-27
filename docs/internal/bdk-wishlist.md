# BDK2.0 - Wishlist

This file will help to track ideas and improvements raised during Peer Reviews. It is organized by component such as 
Core or SpringBoot and sub-modules like Config, Services, etc.

## Core
Specific wishlist dedicated to the `Core` layer. 

### Config

- [ ] Ability to define a common `host` shared across each Symphony's components (Agent, Pod, KM). So that, a very basic
configuration would look like: 

```yaml
host: devx1.symphony.com

bot:
  username: bot-username
  privateKeyPath: /path/to/privatekey.pem
```
