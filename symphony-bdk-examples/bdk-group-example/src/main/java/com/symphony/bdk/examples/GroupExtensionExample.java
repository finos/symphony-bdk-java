package com.symphony.bdk.examples;

import static com.symphony.bdk.core.activity.command.SlashCommand.slash;
import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.activity.parsing.Mention;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.util.UserIdUtil;
import com.symphony.bdk.ext.group.SymphonyGroupBdkExtension;
import com.symphony.bdk.ext.group.SymphonyGroupService;
import com.symphony.bdk.ext.group.gen.api.model.AddMember;
import com.symphony.bdk.ext.group.gen.api.model.GroupList;
import com.symphony.bdk.ext.group.gen.api.model.Member;
import com.symphony.bdk.ext.group.gen.api.model.ReadGroup;
import com.symphony.bdk.ext.group.gen.api.model.Status;
import com.symphony.bdk.http.api.ApiRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Example of usage of the Groups API. The bot bellow curretnly provides 2 different commands:
 * <ul>
 *   <li>{@code /groups} to list available active groups</li>
 *   <li>{@code /groups {groupId} add @member} to list available active groups</li>
 * </ul>
 */
@SuppressWarnings("all")
public class GroupExtensionExample {

  private static final Logger log = LoggerFactory.getLogger(GroupExtensionExample.class);

  private static final String TYPE_SDL = "SDL";

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = SymphonyBdk.builder()
        .config(loadFromSymphonyDir("config.yaml"))
        .extension(SymphonyGroupBdkExtension.class) // or bdk.extensions().register(SymphonyGroupBdkExtension.class);
        .build();

    // extension is also service provider
    final SymphonyGroupService groupService = bdk.extensions().service(SymphonyGroupBdkExtension.class);

    // list groups
    bdk.activities().register(slash("/groups", false, c -> {
      final GroupList groups = groupService.listGroups(TYPE_SDL, Status.ACTIVE, null, null, null, null);
      bdk.messages().send(c.getStreamId(), Message.builder()
          .template(bdk.messages().templates().newTemplateFromClasspath("/groups.ftl"), groups)
          .build());
    }));

    // add member to group
    bdk.activities().register(slash("/groups {groupId} add {@member}", false, c -> {

      final String groupId = c.getArguments().getString("groupId");
      final Mention member = c.getArguments().getMention("member");

      Optional<ReadGroup> group = getGroup(groupService, groupId);

      if (group.isPresent()) {
        groupService.addMemberToGroup(groupId, new AddMember().member(new Member().memberId(member.getUserId()).memberTenant(UserIdUtil.extractTenantId(member.getUserId()))));
        bdk.messages().send(c.getStreamId(), "Member <b>" + member.getUserDisplayName() + "</b> successfully added to group <b>" + group.get().getName() + "</b>");
      } else {
        bdk.messages().send(c.getStreamId(), "Group <b>" + groupId + "</b> not found.");
      }
    }));

    bdk.datafeed().start();
  }

  private static Optional<ReadGroup> getGroup(SymphonyGroupService groupService, String groupId) {
    try {
      return Optional.of(groupService.getGroup(groupId));
    } catch (ApiRuntimeException ex) {
      return Optional.empty();
    }
  }
}
