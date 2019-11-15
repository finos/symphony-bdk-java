package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.Signal;
import org.junit.Test;

public class SignalTest {
  @Test
  public void SignalTest() throws Exception {
    // Arrange and Act
    Signal signal = new Signal();

    // Assert
    assertEquals(null, signal.getQuery());
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    Signal signal = new Signal();

    // Act
    String actual = signal.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getNameTest() throws Exception {
    // Arrange
    Signal signal = new Signal();

    // Act
    String actual = signal.getName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getQueryTest() throws Exception {
    // Arrange
    Signal signal = new Signal();

    // Act
    String actual = signal.getQuery();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTimestampTest() throws Exception {
    // Arrange
    Signal signal = new Signal();

    // Act
    long actual = signal.getTimestamp();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void isCompanyWideTest() throws Exception {
    // Arrange
    Signal signal = new Signal();

    // Act
    boolean actual = signal.isCompanyWide();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void isVisibleOnProfileTest() throws Exception {
    // Arrange
    Signal signal = new Signal();

    // Act
    boolean actual = signal.isVisibleOnProfile();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void setCompanyWideTest() throws Exception {
    // Arrange
    Signal signal = new Signal();
    boolean companyWide = true;

    // Act
    signal.setCompanyWide(companyWide);

    // Assert
    assertTrue(signal.isCompanyWide());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    Signal signal = new Signal();
    String id = "aaaaa";

    // Act
    signal.setId(id);

    // Assert
    assertEquals("aaaaa", signal.getId());
  }

  @Test
  public void setNameTest() throws Exception {
    // Arrange
    Signal signal = new Signal();
    String name = "aaaaa";

    // Act
    signal.setName(name);

    // Assert
    assertEquals("aaaaa", signal.getName());
  }

  @Test
  public void setQueryTest() throws Exception {
    // Arrange
    Signal signal = new Signal();
    String query = "aaaaa";

    // Act
    signal.setQuery(query);

    // Assert
    assertEquals("aaaaa", signal.getQuery());
  }

  @Test
  public void setTimestampTest() throws Exception {
    // Arrange
    Signal signal = new Signal();
    long timestamp = 1L;

    // Act
    signal.setTimestamp(timestamp);

    // Assert
    assertEquals(1L, signal.getTimestamp());
  }

  @Test
  public void setVisibleOnProfileTest() throws Exception {
    // Arrange
    Signal signal = new Signal();
    boolean visibleOnProfile = true;

    // Act
    signal.setVisibleOnProfile(visibleOnProfile);

    // Assert
    assertTrue(signal.isVisibleOnProfile());
  }
}
