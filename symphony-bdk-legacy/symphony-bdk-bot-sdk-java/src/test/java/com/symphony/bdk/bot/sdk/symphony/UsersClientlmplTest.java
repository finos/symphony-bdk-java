package com.symphony.bdk.bot.sdk.symphony;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyUser;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyUserFilter;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyUserSearchResult;
import com.symphony.bdk.bot.sdk.symphony.model.UserAvatar;

import clients.SymBotClient;
import clients.symphony.api.UsersClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import exceptions.SymClientException;
import model.Avatar;
import model.UserFilter;
import model.UserInfo;
import model.UserSearchResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.NoContentException;

public class UsersClientlmplTest {

  private UsersClientImpl usersClientImpl;
  private UsersClient usersClient;
  private SymBotClient symBotClient;
  private UserInfo userInfo1;
  private UserInfo userInfo2;

  @Before
  public void initBot() {
    final SymConfig symConfig = SymConfigLoader.loadConfig("src/test/resources/sym-config.json");
    assertNotNull(symConfig);

    this.symBotClient = Mockito.mock(SymBotClient.class);
    Mockito.when(this.symBotClient.getConfig()).thenReturn(symConfig);

    this.usersClient = Mockito.mock(UsersClient.class);

    this.userInfo1 = new UserInfo();
    this.initUserInfo(this.userInfo1,1L);

    this.userInfo2 = new UserInfo();
    this.initUserInfo(this.userInfo2,2L);

    Mockito.when(this.usersClient.getSessionUser()).thenReturn(this.userInfo1);
    Mockito.when(this.symBotClient.getUsersClient()).thenReturn(this.usersClient);

    this.usersClientImpl = new UsersClientImpl(this.symBotClient);
    assertNotNull(this.usersClientImpl);
  }

  @Test
  public void testGetBotUserId(){
    assertEquals(1L, this.usersClientImpl.getBotUserId().longValue());
  }

  @Test
  public void testGetDisplayName(){
    assertEquals("test symphony", this.usersClientImpl.getBotDisplayName());
  }

  @Test
  public void testGetUserFromUsername() throws SymphonyClientException, NoContentException {
    Mockito.when(this.usersClient.getUserFromUsername("test1_symphony")).thenReturn(this.userInfo1);

    final SymphonyUser symphonyUser = this.usersClientImpl.getUserFromUsername("test1_symphony");
    assertNotNull(symphonyUser);
    this.verifySymphonyUser(symphonyUser,1L);
  }

  @Test
  public void testGetUserFromUsernameWithNoContent() throws SymphonyClientException, NoContentException {
    Mockito.when(this.usersClient.getUserFromUsername(anyString())).thenThrow(
        NoContentException.class);

    final SymphonyUser symphonyUser = this.usersClientImpl.getUserFromUsername("symphony");
    assertNull(symphonyUser);
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetUserFromUsernameWithException() throws SymphonyClientException, NoContentException {
    Mockito.when(this.usersClient.getUserFromUsername(anyString())).thenThrow(
        SymClientException.class);

    this.usersClientImpl.getUserFromUsername("symphony");
  }

  @Test
  public void testGetUserFromEmail() throws SymphonyClientException, NoContentException {
    Mockito.when(this.usersClient.getUserFromEmail("test1@symphony.com", true)).thenReturn(this.userInfo1);

    final SymphonyUser symphonyUser = this.usersClientImpl.getUserFromEmail("test1@symphony.com",true);
    assertNotNull(symphonyUser);
    this.verifySymphonyUser(symphonyUser,1L);
  }

  @Test
  public void testGetUserFromEmailWithNoContent() throws SymphonyClientException, NoContentException {
    Mockito.when(this.usersClient.getUserFromEmail("test1@symphony.com", true)).thenThrow(
        NoContentException.class);

    final SymphonyUser symphonyUser = this.usersClientImpl.getUserFromEmail("test1@symphony.com",true);
    assertNull(symphonyUser);
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetUserFromEmailWithException() throws SymphonyClientException, NoContentException {
    Mockito.when(this.usersClient.getUserFromEmail("test1@symphony.com", true)).thenThrow(
        SymClientException.class);

    this.usersClientImpl.getUserFromEmail("test1@symphony.com", true);
  }

  @Test
  public void testGetUserFromId() throws SymphonyClientException, NoContentException {
    Mockito.when(this.usersClient.getUserFromId(1L, true)).thenReturn(this.userInfo1);

    final SymphonyUser symphonyUser = this.usersClientImpl.getUserFromId(1L,true);
    assertNotNull(symphonyUser);
    this.verifySymphonyUser(symphonyUser, 1L);
  }

  @Test
  public void testGetUserFromIdWithNoContent() throws SymphonyClientException, NoContentException {
    Mockito.when(this.usersClient.getUserFromId(1L, true)).thenThrow(
        NoContentException.class);

    final SymphonyUser symphonyUser = this.usersClientImpl.getUserFromId(1L,true);
    assertNull(symphonyUser);
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetUserFromIdWithException() throws SymphonyClientException, NoContentException {
    Mockito.when(this.usersClient.getUserFromId(1L, true)).thenThrow(
        SymClientException.class);

    this.usersClientImpl.getUserFromId(1L, true);
  }

  @Test
  public void testGetUsersFromIdList() throws NoContentException, SymphonyClientException {
    final List<Long> ids = Arrays.asList(1L, 2L);
    assertNotNull(ids);

    Mockito.when(this.usersClient.getUsersFromIdList(ids, true)).thenReturn(Arrays.asList(this
        .userInfo1, this.userInfo2));

    final List<SymphonyUser> symphonyUsers = this.usersClientImpl.getUsersFromIdList(ids, true);
    assertNotNull(symphonyUsers);
    this.verifySymphonyUser(symphonyUsers.get(0), 1L);
    this.verifySymphonyUser(symphonyUsers.get(1), 2L);
  }

  @Test
  public void testGetUsersFromIdListWithNoContent() throws NoContentException, SymphonyClientException {
    final List<Long> ids = Arrays.asList(1L, 2L);
    assertNotNull(ids);

    Mockito.when(this.usersClient.getUsersFromIdList(ids, true)).thenThrow(NoContentException.class);

    final List<SymphonyUser> symphonyUsers = this.usersClientImpl.getUsersFromIdList(ids, true);
    assertNotNull(symphonyUsers);
    assertTrue(symphonyUsers.isEmpty());
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetUsersFromIdListWithException() throws NoContentException, SymphonyClientException {
    final List<Long> ids = Arrays.asList(1L, 2L);
    assertNotNull(ids);

    Mockito.when(this.usersClient.getUsersFromIdList(ids, true)).thenThrow(SymClientException.class);

    this.usersClientImpl.getUsersFromIdList(ids, true);
  }

  @Test
  public void testGetUsersFromEmailList() throws NoContentException, SymphonyClientException {
    final List<String> emails = Arrays.asList("test1@symphony.com", "test2@symphony.com");
    assertNotNull(emails);

    Mockito.when(this.usersClient.getUsersFromEmailList(emails, true)).thenReturn(Arrays.asList(this
        .userInfo1, this.userInfo2));

    final List<SymphonyUser> symphonyUsers = this.usersClientImpl.getUsersFromEmailList(emails, true);
    assertNotNull(symphonyUsers);
    this.verifySymphonyUser(symphonyUsers.get(0), 1L);
    this.verifySymphonyUser(symphonyUsers.get(1), 2L);
  }

  @Test
  public void testGetUsersFromEmailListWithNoContent() throws NoContentException, SymphonyClientException {
    final List<String> emails = Arrays.asList("test1@symphony.com", "test2@symphony.com");
    assertNotNull(emails);

    Mockito.when(this.usersClient.getUsersFromEmailList(emails, true)).thenThrow(NoContentException.class);

    final List<SymphonyUser> symphonyUsers = this.usersClientImpl.getUsersFromEmailList(emails, true);
    assertNotNull(symphonyUsers);
    assertTrue(symphonyUsers.isEmpty());
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetUsersFromEmailListWithException() throws NoContentException, SymphonyClientException {
    final List<String> emails = Arrays.asList("test1@symphony.com", "test2@symphony.com");
    assertNotNull(emails);

    Mockito.when(this.usersClient.getUsersFromEmailList(emails, true)).thenThrow(SymClientException.class);

    this.usersClientImpl.getUsersFromEmailList(emails, true);
  }

  @Test
  public void testSearchUsers() throws SymphonyClientException, NoContentException {
    final SymphonyUserFilter symphonyUserFilter = new SymphonyUserFilter();
    this.initSymphonyUserFilter(symphonyUserFilter);

    final SymphonyUserSearchResult symphonyUserSearchResult = new SymphonyUserSearchResult();
    this.initUserSearchResult(symphonyUserSearchResult);

    final UserSearchResult userSearchResult = new UserSearchResult();
    this.initUserSearchResult(userSearchResult);

    Mockito.when(this.usersClient.searchUsers(anyString(), anyBoolean(), anyInt(), anyInt(), any(UserFilter.class))).thenReturn(userSearchResult);

    final SymphonyUserSearchResult symphonyUserSearchResult1 = this.usersClientImpl.searchUsers(symphonyUserFilter);
    assertNotNull(symphonyUserSearchResult1);
    this.verifySymphonyUserSearchResult(symphonyUserSearchResult, symphonyUserFilter);
  }

  @Test
  public void testSearchUsersWithNoContent() throws SymphonyClientException, NoContentException {
    final SymphonyUserFilter symphonyUserFilter = new SymphonyUserFilter();
    this.initSymphonyUserFilter(symphonyUserFilter);

    final SymphonyUserSearchResult symphonyUserSearchResult = new SymphonyUserSearchResult();
    this.initUserSearchResult(symphonyUserSearchResult);

    final UserSearchResult userSearchResult = new UserSearchResult();
    this.initUserSearchResult(userSearchResult);

    Mockito.when(this.usersClient.searchUsers(anyString(), anyBoolean(), anyInt(), anyInt(), any(UserFilter.class))).thenThrow(NoContentException.class);

    final SymphonyUserSearchResult symphonyUserSearchResult1 = this.usersClientImpl.searchUsers(symphonyUserFilter);
    assertEquals(new SymphonyUserSearchResult(), symphonyUserSearchResult1);
  }

  @Test(expected = SymphonyClientException.class)
  public void testSearchUsersWithException() throws SymphonyClientException, NoContentException {
    final SymphonyUserFilter symphonyUserFilter = new SymphonyUserFilter();
    this.initSymphonyUserFilter(symphonyUserFilter);

    final SymphonyUserSearchResult symphonyUserSearchResult = new SymphonyUserSearchResult();
    this.initUserSearchResult(symphonyUserSearchResult);

    final UserSearchResult userSearchResult = new UserSearchResult();
    this.initUserSearchResult(userSearchResult);

    Mockito.when(this.usersClient.searchUsers(anyString(), anyBoolean(), anyInt(), anyInt(), any(UserFilter.class))).thenThrow(SymClientException.class);

    final SymphonyUserSearchResult symphonyUserSearchResult1 = this.usersClientImpl.searchUsers(symphonyUserFilter);
    assertEquals(new SymphonyUserSearchResult(), symphonyUserSearchResult1);
  }

  @Test
  public void testGetSessionUser() throws SymphonyClientException {
    Mockito.when(this.usersClient.getSessionUser()).thenReturn(this.userInfo1);

    final SymphonyUser symphonyUser = this.usersClientImpl.getSessionUser();
    assertNotNull(symphonyUser);
    this.verifySymphonyUser(symphonyUser, 1L);
  }

  @Test(expected = SymphonyClientException.class)
  public void testGetSessionUserWithException() throws SymphonyClientException {
    Mockito.when(this.usersClient.getSessionUser()).thenThrow(SymClientException.class);

    this.usersClientImpl.getSessionUser();
  }

  ////////  Private methods
  private void verifySymphonyUserSearchResult(final SymphonyUserSearchResult symphonyUserSearchResult, final SymphonyUserFilter symphonyUserFilter) {
    assertEquals(symphonyUserFilter.getQuery(), symphonyUserSearchResult.getQuery());
    assertEquals(symphonyUserFilter.getSkip(), symphonyUserSearchResult.getSkip());
    assertEquals(symphonyUserFilter.getLimit(), symphonyUserSearchResult.getLimit());
  }

  private void initSymphonyUserFilter(final SymphonyUserFilter symphonyUserFilter) {
    symphonyUserFilter.setQuery("query");
    symphonyUserFilter.setLocal(true);
    symphonyUserFilter.setSkip(1);
    symphonyUserFilter.setLimit(1);
    symphonyUserFilter.setTitle("title1");
    symphonyUserFilter.setLocation("California");
    symphonyUserFilter.setCompany("Symphony");
  }

  private void initUserSearchResult(final SymphonyUserSearchResult symphonyUserSearchResult) {
    symphonyUserSearchResult.setSkip(1);
    symphonyUserSearchResult.setQuery("query");
    symphonyUserSearchResult.setLimit(1);
    symphonyUserSearchResult.setCount(2);

    final Map<String, String> filters = new HashMap<>();
    filters.put("key1", "value1");
    filters.put("key2", "value2");
    filters.put("key3", "value3");
    symphonyUserSearchResult.setFilters(filters);

    final SymphonyUser symphonyUser1 = new SymphonyUser(this.userInfo1);
    final SymphonyUser symphonyUser2 = new SymphonyUser(this.userInfo2);
    symphonyUserSearchResult.setUsers(Arrays.asList(symphonyUser1, symphonyUser2));
  }

  private void initUserSearchResult(final UserSearchResult userSearchResult) {
    userSearchResult.setSkip(1);
    userSearchResult.setQuery("query");
    userSearchResult.setLimit(1);
    userSearchResult.setCount(2);

    final Map<String, String> filters = new HashMap<>();
    filters.put("key1", "value1");
    filters.put("key2", "value2");
    filters.put("key3", "value3");
    userSearchResult.setFilters(filters);

    userSearchResult.setUsers(Arrays.asList(this.userInfo1, this.userInfo2));
  }

  private void initUserInfo(final UserInfo userInfo, final long number){
    userInfo.setId(number);
    userInfo.setEmailAddress("test"+number+"@symphony.com");
    userInfo.setFirstName("test");
    userInfo.setLastName("symphony");
    userInfo.setDisplayName("test symphony");
    userInfo.setTitle("title"+number);
    userInfo.setCompany("Symphony");
    userInfo.setUsername("test"+number+"_symphony");
    userInfo.setLocation("California");
    userInfo.setWorkPhoneNumber("555-000 999"+number);
    userInfo.setMobilePhoneNumber("666-111 679"+number);
    userInfo.setJobFunction("tester");
    userInfo.setDepartment("Test department");
    userInfo.setDivision("Symph");
    userInfo.setAccountType("Temp");

    final Avatar avatar1 = new Avatar();
    avatar1.setSize("small");
    avatar1.setUrl("../avatars/static/50/"+number+".png");

    final Avatar avatar2 = new Avatar();
    avatar2.setSize("original");
    avatar2.setUrl("../avatars/static/150/"+number+".png");

    final List<Avatar> avatars = new ArrayList<Avatar>();
    avatars.add(avatar1);
    avatars.add(avatar2);

    userInfo.setAvatars(avatars);
  }

  private void verifySymphonyUser(final SymphonyUser symphonyUser, final long number) {
    assertEquals(number, symphonyUser.getUserId().longValue());
    assertEquals("test"+number+"@symphony.com", symphonyUser.getEmailAddress());
    assertEquals("test", symphonyUser.getFirstName());
    assertEquals("symphony", symphonyUser.getLastName());
    assertEquals("test symphony", symphonyUser.getDisplayName());
    assertEquals("title"+number, symphonyUser.getTitle());
    assertEquals("Symphony", symphonyUser.getCompany());
    assertEquals("test"+number+"_symphony", symphonyUser.getUsername());
    assertEquals("California", symphonyUser.getLocation());
    assertEquals("555-000 999"+number, symphonyUser.getWorkPhoneNumber());
    assertEquals("666-111 679"+number, symphonyUser.getMobilePhoneNumber());
    assertEquals("tester", symphonyUser.getJobFunction());
    assertEquals("Test department", symphonyUser.getDepartment());
    assertEquals("Symph", symphonyUser.getDivision());
    assertEquals("Temp", symphonyUser.getAccountType());

    this.verifyAvatar(symphonyUser);
  }

  private void verifyAvatar(final SymphonyUser symphonyUser) {
    final List<UserAvatar> avatars = symphonyUser.getAvatars();
    assertNotNull(avatars);
    assertEquals(avatars.size(), symphonyUser.getAvatars().size());
    final int avatarsSize = avatars.size();
    for (int i = 0; i < avatarsSize; i++) {
      assertEquals(avatars.get(i).getSize(), symphonyUser.getAvatars().get(i).getSize());
      assertEquals(avatars.get(i).getUrl(), symphonyUser.getAvatars().get(i).getUrl());
    }
  }
}
