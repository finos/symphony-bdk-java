package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.service.message.model.SortDir;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.gen.api.model.MessageSearchQuery;
import com.symphony.bdk.gen.api.model.V4Message;

import org.apiguardian.api.API;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * SPI for replacing all agent-facing message *read* operations in {@code MessageService}.
 *
 * <p>When an extension implements {@link BdkMessageRetrieverOverrideProvider} and is pre-registered via
 * {@code SymphonyBdkBuilder.extension(Class)}, the override returned by that provider is used for all
 * covered operations. The agent {@code MessagesApi} is never called for those operations while an
 * override is active.
 *
 * <p>The same override instance handles both bot-context and OBO-context calls. Implementations
 * receive auth sessions via Aware injection at construction time and are responsible for routing
 * bot vs. OBO operations internally.
 *
 * <p>{@link MessageRetrieverOverride} is independent from {@link MessageSenderOverride}: an extension may
 * implement one, the other, or both, and neither requires the other to be present.
 *
 * <p>Exceptions thrown by override methods are propagated to the {@code MessageService} caller,
 * wrapped as necessary to match the BDK error contract.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface MessageRetrieverOverride {

  List<V4Message> listMessages(@Nonnull String streamId, @Nonnull Instant since, @Nullable Instant until,
      @Nullable PaginationAttribute pagination) throws Exception;

  List<V4Message> searchMessages(@Nonnull MessageSearchQuery query, @Nullable PaginationAttribute pagination,
      @Nullable SortDir sortDir) throws Exception;

  List<V4Message> searchMessagesSemantic(@Nonnull String query, @Nullable String streamId,
      @Nullable PaginationAttribute pagination) throws Exception;

  V4Message getMessage(@Nonnull String messageId) throws Exception;
}
