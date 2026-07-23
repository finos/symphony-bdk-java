package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.gen.api.model.MessageSuppressionResponse;
import com.symphony.bdk.gen.api.model.V4ImportResponse;
import com.symphony.bdk.gen.api.model.V4ImportedMessage;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageBlastResponse;

import org.apiguardian.api.API;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * SPI for replacing all agent-facing message operations in {@code MessageService}.
 *
 * <p>When an extension implements {@link BdkMessageSenderOverrideProvider} and is pre-registered via
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
 * <p>Exceptions thrown by override methods are propagated to the {@code MessageService} caller,
 * wrapped as necessary to match the BDK error contract.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface MessageSenderOverride {

  V4Message send(@Nonnull AuthSession session, @Nonnull String streamId, @Nonnull Message message) throws Exception;

  V4Message update(@Nonnull AuthSession session, @Nonnull String streamId, @Nonnull String messageId,
      @Nonnull Message content) throws Exception;

  V4MessageBlastResponse blast(@Nonnull AuthSession session, @Nonnull List<String> streamIds,
      @Nonnull Message message) throws Exception;

  List<V4ImportResponse> importMessages(@Nonnull AuthSession session, @Nonnull List<V4ImportedMessage> messages)
      throws Exception;

  MessageSuppressionResponse suppressMessage(@Nonnull AuthSession session, @Nonnull String messageId)
      throws Exception;

  byte[] getAttachment(@Nonnull AuthSession session, @Nonnull String streamId, @Nonnull String messageId,
      @Nonnull String attachmentId) throws Exception;
}
