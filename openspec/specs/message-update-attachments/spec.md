# Message-Update-Attachments Specification

## Purpose

Defines how the BDK forwards file attachments and previews when updating an existing message, so users can rely on update supporting the same content as send.

## Requirements

### Requirement: Update forwards attachments and previews
When a `Message` passed to a message update operation (bot or OBO) contains attachments or previews, the BDK SHALL send each of them as `attachment` and `preview` parts of the multipart body of the Update Message v4 request, exactly as it does for message creation. Attachment support on update requires a pod running SBE v24.1 or later.

#### Scenario: Update with attachment and preview
- **WHEN** a user updates a message with a `Message` containing one attachment and its preview
- **THEN** the Update Message v4 request contains one `attachment` part and one `preview` part with the provided filename and content type

#### Scenario: Update with multiple attachments
- **WHEN** a user updates a message with a `Message` containing two attachments
- **THEN** the request contains two `attachment` parts

#### Scenario: Update through OBO
- **WHEN** a user updates a message through `OboMessageService` with a `Message` containing an attachment
- **THEN** the request contains the `attachment` part, using the delegated session tokens

#### Scenario: Update without attachments
- **WHEN** a user updates a message with a `Message` containing only text
- **THEN** the request contains no `attachment` or `preview` parts
