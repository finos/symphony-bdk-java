package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.message.model.SortDir;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.gen.api.model.MessageSearchQuery;
import com.symphony.bdk.gen.api.model.SemanticSearchQuery;
import com.symphony.bdk.gen.api.model.V4Message;

import org.apiguardian.api.API;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.List;

/**
 * SPI for replacing all agent-facing message *read* operations in {@code MessageService}.
 *
 * <p>When an extension implements {@link BdkMessageRetrieverOverrideProvider} and is pre-registered via
 * {@code SymphonyBdkBuilder.extension(Class)}, the override returned by that provider is used for all
 * covered operations. The agent {@code MessagesApi} is never called for those operations while an
 * override is active.
 *
 * <p>A single override instance handles both bot-context and OBO-context calls. On each call,
 * {@code MessageService} passes the {@link AuthSession} it is currently operating under as the first
 * parameter: the bot session for a bot-context {@code MessageService}, or the OBO session for a
 * {@code MessageService} obtained via {@code SymphonyBdk.obo(...)}, {@code OboServices.messages()}, or
 * {@code MessageService.obo(...)}. Implementations route bot vs. OBO behavior from this parameter rather
 * than from any injected or cached session, keeping the instance stateless and safe to invoke
 * concurrently across bot and multiple OBO sessions.
 *
 * <p>{@link MessageRetrieverOverride} is independent from {@link MessageSenderOverride}: an extension may
 * implement one, the other, or both, and neither requires the other to be present.
 *
 * <p>Exceptions thrown by override methods are propagated to the {@code MessageService} caller,
 * wrapped as necessary to match the BDK error contract.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface MessageRetrieverOverride {

  List<V4Message> listMessages(AuthSession session, String streamId, Instant since,
      @Nullable Instant until, @Nullable PaginationAttribute pagination) throws Exception;

  List<V4Message> searchMessages(AuthSession session, MessageSearchQuery query,
      @Nullable PaginationAttribute pagination, @Nullable SortDir sortDir) throws Exception;

  List<V4Message> searchMessagesSemantic(AuthSession session, String query,
      @Nullable String streamId, @Nullable PaginationAttribute pagination) throws Exception;

  /**
   * Semantic search restricted to a date range, called only when {@code from} or {@code to} is set.
   *
   * <p>The default implementation rejects the call, so that overrides predating the date range never
   * silently return messages outside of it.
   */
  default List<V4Message> searchMessagesSemantic(AuthSession session, String query, @Nullable String streamId,
      @Nullable Instant from, @Nullable Instant to, @Nullable PaginationAttribute pagination) throws Exception {
    throw new UnsupportedOperationException("Semantic search with a date range is not supported by this override");
  }

  /**
   * Called by every {@code MessageService.searchMessagesSemantic(...)} overload. The threadId, if set, is already
   * URL-safe encoded.
   * The default implementation routes to the narrower methods, so that overrides predating this method keep working.
   * The {@code semanticWeight} and {@code exactWeight} only matter when {@code mode} is {@code advanced}, and can be
   * ignored otherwise.
   */
  default List<V4Message> searchMessagesSemantic(AuthSession session, SemanticSearchQuery query,
      @Nullable PaginationAttribute pagination) throws Exception {
    // weights only apply in advanced mode, which is itself rejected here
    if (query.getMode() != null && query.getMode() != SemanticSearchQuery.ModeEnum.BALANCED) {
      throw new UnsupportedOperationException(
          "Semantic search with a mode other than balanced is not supported by this override");
    }
    if (query.getFromDate() != null || query.getToDate() != null) {
      return searchMessagesSemantic(session, query.getText(), query.getThreadId(),
          query.getFromDate() != null ? Instant.ofEpochMilli(query.getFromDate()) : null,
          query.getToDate() != null ? Instant.ofEpochMilli(query.getToDate()) : null, pagination);
    }
    return searchMessagesSemantic(session, query.getText(), query.getThreadId(), pagination);
  }

  V4Message getMessage(AuthSession session, String messageId) throws Exception;
}
