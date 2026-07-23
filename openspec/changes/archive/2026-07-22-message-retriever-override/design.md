## Context

`message-sender-override` (see `openspec/specs/message-sender-override/spec.md`) gave extensions a way to fully replace the agent-facing *write* side of `MessageService` via `MessageSenderOverride` / `BdkMessageSenderOverrideProvider`. `MessageService` mixes three backends: the agent `MessagesApi`/`AttachmentsApi` clients, and the pod `MessageApi`/`MessageSuppressionApi`/`StreamsApi`/`PodApi`/`DefaultApi` clients (see `ServiceFactory.getMessageService()`). Only the agent-backed calls are meaningful to override — an agentless extension has no reason to intercept pod calls, since the pod is unaffected by the agent's absence.

Today the agent-backed calls are: `send`, `update`, `send(blast)`, `importMessages`, `getAttachment` (already covered by `MessageSenderOverride`), plus `listMessages`, `searchMessages`, `searchMessagesSemantic`, `getMessage` (not covered — always hit `messagesApi` directly, regardless of whether a sender override is active). This asymmetry means an agentless extension can absorb outbound messages but not read them back — a bot built on top of it cannot list or search stream history, or fetch a message by ID, without a live agent connection.

This change closes that gap with a symmetric read-side SPI, reusing every construction/wiring pattern established for `MessageSenderOverride` (pre-registration requirement, first-registered-wins, `ExtensionService` detection, `ApiException`/`BdkExtensionException` propagation).

## Goals / Non-Goals

**Goals:**
- `MessageRetrieverOverride` SPI covering `listMessages`, `searchMessages`, `searchMessagesSemantic`, `getMessage` — the four agent-backed read operations in `MessageService`
- `BdkMessageRetrieverOverrideProvider` detection interface, wired the same way as `BdkMessageSenderOverrideProvider`
- `MessageRetrieverOverride` and `MessageSenderOverride` are independent: an extension may provide one, the other, or both; neither requires the other
- Zero change to `MessageService`'s public API or return types
- Same OBO behavior as `MessageSenderOverride`: the override instance is shared between bot and OBO contexts (see D3 below)

**Non-Goals:**
- Pod-backed read operations (`getMessageStatus`, `listAttachments`, `listMessageReceipts`, `getMessageRelationships`, `getAttachmentTypes`) — these don't call the agent and are out of scope
- Merging `MessageRetrieverOverride` into `MessageSenderOverride` as a single interface (considered and rejected, see D1)
- Streaming/paginated override semantics beyond what `MessageService` already exposes — the override receives the same resolved parameters (`since`, `until`, `PaginationAttribute`, etc.) `MessageService` already computes

## Decisions

### D1 — Separate `MessageRetrieverOverride` interface, not an extension of `MessageSenderOverride`

**Decision**: `MessageRetrieverOverride` is a new, independent interface with its own provider (`BdkMessageRetrieverOverrideProvider`), not additional methods on `MessageSenderOverride`.

**Rationale**: `MessageSenderOverride` is already merged and `@API(status = EXPERIMENTAL)` but has a real first consumer in flight (an agentless-send extension). Adding read methods to it would force every existing/in-progress implementer to add four new methods to keep compiling, even if they only care about sending. Read and write are also independently useful: a bot might want to override only how it fetches history (e.g. read from a local cache/replica) while sending normally through the agent, or vice versa. Two small, focused interfaces compose better than one that grows without bound as more agent surface gets covered.

**Alternative considered**: Fold everything into a single `MessageOverride` (rename of `MessageSenderOverride`) covering all agent-facing message operations — rejected as a breaking rename of an interface with a real consumer, for no behavioral benefit over two interfaces that can be implemented by the same class when an extension genuinely wants both.

---

### D2 — One override object per capability; `ExtensionService` looks each up independently

**Decision**: `ExtensionService` gains a `findMessageRetrieverOverride()` lookup mirroring `findMessageSenderOverride()`, independent of it. A single extension class may implement both `BdkMessageSenderOverrideProvider` and `BdkMessageRetrieverOverrideProvider` (returning the same or different backing objects); `ServiceFactory`/`SymphonyBdk` pass both optional overrides into `MessageService` without either depending on the other being present.

**Rationale**: Keeps the two capabilities orthogonal, consistent with D1. Reuses the exact "first registered wins + warn" rule already specified for `MessageSenderOverride`, applied separately to `MessageRetrieverOverride`.

---

### D3 — Same override instance is reused for OBO reads, no separate OBO-specific methods

**Decision**: `MessageRetrieverOverride` methods (`listMessages`, `searchMessages`, `searchMessagesSemantic`, `getMessage`) take the same parameters `MessageService` already resolves internally (stream ID, time bounds, pagination, query objects) and do not take an explicit auth session. The same `MessageRetrieverOverride` instance passed into a bot-context `MessageService` is threaded into `MessageService#obo(...)`'s copy constructor, exactly as `senderOverride` is today.

**Rationale**: `MessageSenderOverride` already established this pattern (D4 in the parent change) and resolved the "does the override need per-call auth context" open question implicitly: no override method takes a session parameter, and implementations that need OBO-awareness get it via `BdkAuthenticationAware`/`BdkOboSessionAware` at construction time if needed. Reusing the identical shape avoids re-opening that question and keeps `MessageService`'s OBO copy constructor a single, uniform pass-through of all overrides.

---

### D4 — Overload collapsing: override receives fully-resolved parameters, not raw overload arguments

**Decision**: `MessageService` has many public overloads of `listMessages` (stream vs. streamId, with/without `until`, with/without pagination) and `searchMessages` (with/without pagination, with/without sort). All overloads funnel into one canonical private call site per operation (already true today for `listMessages`/`searchMessages`); the override is invoked from that single call site with the fully-resolved arguments (`streamId`, resolved `since`/`until` instants, `PaginationAttribute` possibly `null`, `SortDir` possibly `null`), not once per public overload.

**Rationale**: Mirrors how `send`/`update`/`blast` already fan into one call site before checking `senderOverride != null`. Keeps the `MessageRetrieverOverride` interface small (4 methods) instead of one method per public overload (10+).

## Risks / Trade-offs

**[Risk] Two independent overrides (`MessageSenderOverride` + `MessageRetrieverOverride`) drift out of sync for an extension that needs both**
→ *Mitigation*: Nothing prevents a single extension class from implementing both provider interfaces and backing them with one shared object that implements both SPIs internally. Document this composition pattern in `docs/extension.md`.

**[Risk] `getMessage` and `listMessages` overlap conceptually — unclear which one an implementer should trust for a single message lookup**
→ *Mitigation*: Keep the mapping 1:1 with `MessageService`'s existing public methods (`getMessage` overrides `getMessage`, `listMessages` overrides `listMessages`) rather than trying to unify them; this is the same granularity `MessageSenderOverride` uses for send/update/blast, which are similarly related.

**[Trade-off] `searchMessages` and `searchMessagesSemantic` are two distinct override methods rather than one**
→ They hit different agent endpoints (`v1MessageSearchPost` vs. `v4MessageSearchSemanticPost`) with different query models (`MessageSearchQuery` vs. plain text). Unifying them behind one method would require a union/variant parameter type, adding complexity for no real gain — an override implementer replacing agent search almost certainly needs to distinguish the two anyway.

## Migration Plan

All changes are additive. Existing extensions implementing `BdkMessageSenderOverrideProvider` are unaffected — nothing about that interface changes. No migration required for standard BDK users. Extensions that want read-side override add `BdkMessageRetrieverOverrideProvider` to their extension class in addition to (or instead of) `BdkMessageSenderOverrideProvider`; no other code changes.

## Open Questions

- **Should `MessageRetrieverOverride` also cover `getMessageStatus`/`listMessageReceipts`/`getMessageRelationships`?** These are pod-backed today (see Non-Goals) — out of scope unless a future change moves them behind the agent, which is not currently planned.
