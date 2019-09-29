package com.symphony.ms.songwriter.internal.event;

import com.symphony.ms.songwriter.internal.event.model.IMCreatedEvent;
import com.symphony.ms.songwriter.internal.event.model.MessageEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomCreatedEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomDeactivatedEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomMemberDemotedFromOwnerEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomMemberPromotedToOwnerEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomReactivatedEvent;
import com.symphony.ms.songwriter.internal.event.model.RoomUpdatedEvent;
import com.symphony.ms.songwriter.internal.event.model.UserJoinedRoomEvent;
import com.symphony.ms.songwriter.internal.event.model.UserLeftRoomEvent;

public interface InternalEventListener {

  void onRoomMessage(MessageEvent message);

  void onIMMessage(MessageEvent message);

  void onRoomCreated(RoomCreatedEvent event);

  void onRoomReactivated(RoomReactivatedEvent event);

  void onRoomDeactivated(RoomDeactivatedEvent event);

  void onRoomUpdated(RoomUpdatedEvent event);

  void onIMCreated(IMCreatedEvent event);

  void onRoomMemberDemotedFromOwner(RoomMemberDemotedFromOwnerEvent event);

  void onRoomMemberPromotedToOwner(RoomMemberPromotedToOwnerEvent event);

  void onUserJoinedRoom(UserJoinedRoomEvent event);

  void onUserLeftRoom(UserLeftRoomEvent event);

}
