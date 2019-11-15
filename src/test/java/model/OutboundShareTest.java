package model;

import static org.junit.Assert.assertEquals;
import model.OutboundShare;
import org.junit.Test;

public class OutboundShareTest {
  @Test
  public void OutboundShareTest() throws Exception {
    // Arrange and Act
    OutboundShare outboundShare = new OutboundShare();

    // Assert
    assertEquals(null, outboundShare.getAppIconUrl());
  }

  @Test
  public void getAppIconUrlTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getAppIconUrl();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAppIdTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getAppId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAppNameTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getAppName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getArticleIdTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getArticleId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getArticleUrlTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getArticleUrl();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAuthorTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getAuthor();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMessageTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getMessage();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPublishDateTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    long actual = outboundShare.getPublishDate();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getPublisherTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getPublisher();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSubTitleTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getSubTitle();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSummaryTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getSummary();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getThumbnailUrlTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getThumbnailUrl();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTitleTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();

    // Act
    String actual = outboundShare.getTitle();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAppIconUrlTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String appIconUrl = "aaaaa";

    // Act
    outboundShare.setAppIconUrl(appIconUrl);

    // Assert
    assertEquals("aaaaa", outboundShare.getAppIconUrl());
  }

  @Test
  public void setAppIdTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String appId = "aaaaa";

    // Act
    outboundShare.setAppId(appId);

    // Assert
    assertEquals("aaaaa", outboundShare.getAppId());
  }

  @Test
  public void setAppNameTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String appName = "aaaaa";

    // Act
    outboundShare.setAppName(appName);

    // Assert
    assertEquals("aaaaa", outboundShare.getAppName());
  }

  @Test
  public void setArticleIdTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String articleId = "aaaaa";

    // Act
    outboundShare.setArticleId(articleId);

    // Assert
    assertEquals("aaaaa", outboundShare.getArticleId());
  }

  @Test
  public void setArticleUrlTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String articleUrl = "aaaaa";

    // Act
    outboundShare.setArticleUrl(articleUrl);

    // Assert
    assertEquals("aaaaa", outboundShare.getArticleUrl());
  }

  @Test
  public void setAuthorTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String author = "aaaaa";

    // Act
    outboundShare.setAuthor(author);

    // Assert
    assertEquals("aaaaa", outboundShare.getAuthor());
  }

  @Test
  public void setMessageTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String message = "aaaaa";

    // Act
    outboundShare.setMessage(message);

    // Assert
    assertEquals("aaaaa", outboundShare.getMessage());
  }

  @Test
  public void setPublishDateTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    long publishDate = 1L;

    // Act
    outboundShare.setPublishDate(publishDate);

    // Assert
    assertEquals(1L, outboundShare.getPublishDate());
  }

  @Test
  public void setPublisherTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String publisher = "aaaaa";

    // Act
    outboundShare.setPublisher(publisher);

    // Assert
    assertEquals("aaaaa", outboundShare.getPublisher());
  }

  @Test
  public void setSubTitleTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String subTitle = "aaaaa";

    // Act
    outboundShare.setSubTitle(subTitle);

    // Assert
    assertEquals("aaaaa", outboundShare.getSubTitle());
  }

  @Test
  public void setSummaryTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String summary = "aaaaa";

    // Act
    outboundShare.setSummary(summary);

    // Assert
    assertEquals("aaaaa", outboundShare.getSummary());
  }

  @Test
  public void setThumbnailUrlTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String thumbnailUrl = "aaaaa";

    // Act
    outboundShare.setThumbnailUrl(thumbnailUrl);

    // Assert
    assertEquals("aaaaa", outboundShare.getThumbnailUrl());
  }

  @Test
  public void setTitleTest() throws Exception {
    // Arrange
    OutboundShare outboundShare = new OutboundShare();
    String title = "aaaaa";

    // Act
    outboundShare.setTitle(title);

    // Assert
    assertEquals("aaaaa", outboundShare.getTitle());
  }
}
