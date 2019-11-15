package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.HashMap;
import java.util.Map;
import model.events.SymphonyElementsAction;
import org.junit.Test;

public class SymphonyElementsActionTest {
  @Test
  public void SymphonyElementsActionTest() throws Exception {
    // Arrange and Act
    SymphonyElementsAction symphonyElementsAction = new SymphonyElementsAction();

    // Assert
    assertEquals(null, symphonyElementsAction.getStreamType());
  }

  @Test
  public void getFormIdTest() throws Exception {
    // Arrange
    SymphonyElementsAction symphonyElementsAction = new SymphonyElementsAction();

    // Act
    String actual = symphonyElementsAction.getFormId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getFormValuesTest() throws Exception {
    // Arrange
    SymphonyElementsAction symphonyElementsAction = new SymphonyElementsAction();

    // Act
    Map<String, Object> actual = symphonyElementsAction.getFormValues();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamIdTest() throws Exception {
    // Arrange
    SymphonyElementsAction symphonyElementsAction = new SymphonyElementsAction();

    // Act
    String actual = symphonyElementsAction.getStreamId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTypeTest() throws Exception {
    // Arrange
    SymphonyElementsAction symphonyElementsAction = new SymphonyElementsAction();

    // Act
    String actual = symphonyElementsAction.getStreamType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setFormIdTest() throws Exception {
    // Arrange
    SymphonyElementsAction symphonyElementsAction = new SymphonyElementsAction();
    String formId = "aaaaa";

    // Act
    symphonyElementsAction.setFormId(formId);

    // Assert
    assertEquals("aaaaa", symphonyElementsAction.getFormId());
  }

  @Test
  public void setFormStreamTest() throws Exception {
    // Arrange
    SymphonyElementsAction symphonyElementsAction = new SymphonyElementsAction();
    HashMap<String, String> hashMap = new HashMap<String, String>();
    hashMap.put("aaaaa", "aaaaa");

    // Act
    symphonyElementsAction.setFormStream(hashMap);

    // Assert
    assertEquals(null, symphonyElementsAction.getStreamId());
  }

  @Test
  public void setFormValuesTest() throws Exception {
    // Arrange
    SymphonyElementsAction symphonyElementsAction = new SymphonyElementsAction();
    HashMap<String, Object> hashMap = new HashMap<String, Object>();
    hashMap.put("aaaaa", "aaaaa");

    // Act
    symphonyElementsAction.setFormValues(hashMap);

    // Assert
    assertSame(hashMap, symphonyElementsAction.getFormValues());
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    SymphonyElementsAction symphonyElementsAction = new SymphonyElementsAction();
    HashMap<String, String> hashMap = new HashMap<String, String>();
    hashMap.put("aaaaa", "aaaaa");

    // Act
    symphonyElementsAction.setStream(hashMap);

    // Assert
    assertEquals(null, symphonyElementsAction.getStreamType());
  }
}
