package com.symphony.bdk.core.service.user.constant;

import org.apiguardian.api.API;

/**
 * The list of available user features.
 *
 * @see <a href="https://developers.symphony.com/restapi/reference#list-features">List Features</a>
 */
@API(status = API.Status.STABLE)
public enum UserFeature {
  /**
   * Allows the user to read wall posts.
   */
  postReadEnabled,
  /**
   * Allows the user to write wall posts.
   */
  postWriteEnabled,
  /**
   * Allows the user to have delegates.
   */
  delegatesEnabled,
  /**
   * Allows the user to chat in external IM/MIMs.
   */
  isExternalIMEnabled,
  /**
   * Allows the user to share files externally.
   */
  canShareFilesExternally,
  /**
   * Allows the user to create internal public rooms.
   */
  canCreatePublicRoom,
  /**
   * Allows the user to edit profile picture.
   */
  canUpdateAvatar,
  /**
   * Allows the user to chat in private external rooms.
   */
  isExternalRoomEnabled,
  /**
   * Allows the user to create push signals.
   */
  canCreatePushedSignals,
  /**
   * Enables Lite Mode.
   */
  canUseCompactMode,
  /**
   * Must be recorded in meetings.
   */
  mustBeRecorded,
  /**
   * Allows the user to send files internally.
   */
  sendFilesEnabled,
  /**
   * Allows the user to use audio in internal Meetings.
   */
  canUseInternalAudio,
  /**
   * Allows the user to use video in internal Meetings.
   */
  canUseInternalVideo,
  /**
   * Allows the user to share screens in internal Meetings.
   */
  canProjectInternalScreenShare,
  /**
   * Allows the user to view shared screens in internal Meetings.
   */
  canViewInternalScreenShare,
  /**
   * Allows the user to create multi-lateral room.
   */
  canCreateMultiLateralRoom,
  /**
   * Allows the user to join multi-lateral room.
   */
  canJoinMultiLateralRoom,
  /**
   * Allows the user to use Firehose.
   */
  canUseFirehose,
  /**
   * Allows the user to use audio in internal meetings on mobile.
   */
  canUseInternalAudioMobile,
  /**
   * Allows the user to use video in internal meetings on mobile.
   */
  canUseInternalVideoMobile,
  /**
   * Allows the user to share screens in internal meetings on mobile.
   */
  canProjectInternalScreenShareMobile,
  /**
   * Allows the user to view shared screens in internal meetings on mobile.
   */
  canViewInternalScreenShareMobile,
  /**
   * Allows the user to manage signal subscriptions.
   */
  canManageSignalSubscription
}
