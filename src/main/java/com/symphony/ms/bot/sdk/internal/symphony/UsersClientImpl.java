package com.symphony.ms.bot.sdk.internal.symphony;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.NoContentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyUser;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyUserFilter;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyUserSearchResult;
import clients.SymBotClient;
import model.UserFilter;
import model.UserInfo;

@Service
public class UsersClientImpl implements UsersClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(UsersClientImpl.class);

  private UserInfo botUserInfo;
  private clients.symphony.api.UsersClient usersClient;

  public UsersClientImpl(SymBotClient symBotClient) {
    this.botUserInfo = symBotClient.getBotUserInfo();
    this.usersClient = symBotClient.getUsersClient();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getBotUserId() {
    return botUserInfo.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getBotDisplayName() {
    return botUserInfo.getDisplayName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SymphonyUser getUserFromUsername(String username)
      throws SymphonyClientException {
    try {
      return new SymphonyUser(usersClient.getUserFromUsername(username));
    } catch (NoContentException nce) {
      return null;
    } catch (Exception e) {
      LOGGER.error("Error on getUserFromUsername");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SymphonyUser getUserFromEmail(String email, Boolean local)
      throws SymphonyClientException {
    try {
      return new SymphonyUser(usersClient.getUserFromEmail(email, local));
    } catch (NoContentException nce) {
      return null;
    } catch (Exception e) {
      LOGGER.error("Error on getUserFromEmail");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SymphonyUser getUserFromId(Long userId, Boolean local)
      throws SymphonyClientException {
    try {
      return new SymphonyUser(usersClient.getUserFromId(userId, local));
    } catch (NoContentException nce) {
      return null;
    } catch (Exception e) {
      LOGGER.error("Error on getUserFromId");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SymphonyUser> getUsersFromIdList(List<Long> userIds, Boolean local)
      throws SymphonyClientException {
    try {
      return usersClient
          .getUsersFromIdList(userIds, local)
          .stream()
          .map(SymphonyUser::new)
          .collect(Collectors.toList());
    } catch (NoContentException nce) {
      return new ArrayList<SymphonyUser>();
    } catch (Exception e) {
      LOGGER.error("Error on getUsersFromIdList");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SymphonyUser> getUsersFromEmailList(List<String> emails, Boolean local)
      throws SymphonyClientException {
    try {
      return usersClient
          .getUsersFromEmailList(emails, local)
          .stream()
          .map(SymphonyUser::new)
          .collect(Collectors.toList());
    } catch (NoContentException nce) {
      return new ArrayList<SymphonyUser>();
    } catch (Exception e) {
      LOGGER.error("Error on getUsersFromEmailList");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SymphonyUserSearchResult searchUsers(SymphonyUserFilter userFilter)
      throws SymphonyClientException {
    try {
      return new SymphonyUserSearchResult(usersClient
          .searchUsers(userFilter.getQuery(), userFilter.isLocal(), userFilter.getSkip(),
              userFilter.getLimit(), toUserFilter(userFilter)));
    } catch (NoContentException nce) {
      return new SymphonyUserSearchResult();
    } catch (Exception e) {
      LOGGER.error("Error on searchUsers");
      throw new SymphonyClientException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SymphonyUser getSessionUser() throws SymphonyClientException {
    try {
      return new SymphonyUser(usersClient.getSessionUser());
    } catch (Exception e) {
      LOGGER.error("Error on getSessionUser");
      throw new SymphonyClientException(e);
    }
  }

  private UserFilter toUserFilter(SymphonyUserFilter symphonyUserFilter) {
    UserFilter userFilter = new UserFilter();
    userFilter.setCompany(symphonyUserFilter.getCompany());
    userFilter.setLocation(symphonyUserFilter.getLocation());
    userFilter.setTitle(symphonyUserFilter.getTitle());
    return userFilter;
  }

}
