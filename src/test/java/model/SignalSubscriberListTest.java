package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import model.SignalSubscriberList;
import org.junit.Test;

public class SignalSubscriberListTest {
  @Test
  public void SignalSubscriberListTest() throws Exception {
    // Arrange and Act
    SignalSubscriberList signalSubscriberList = new SignalSubscriberList();

    // Assert
    assertEquals(null, signalSubscriberList.getData());
  }

  @Test
  public void getDataTest() throws Exception {
    // Arrange
    SignalSubscriberList signalSubscriberList = new SignalSubscriberList();

    // Act
    List<SignalSubscriber> actual = signalSubscriberList.getData();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOffsetTest() throws Exception {
    // Arrange
    SignalSubscriberList signalSubscriberList = new SignalSubscriberList();

    // Act
    int actual = signalSubscriberList.getOffset();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getTotalTest() throws Exception {
    // Arrange
    SignalSubscriberList signalSubscriberList = new SignalSubscriberList();

    // Act
    int actual = signalSubscriberList.getTotal();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void isHasMoreTest() throws Exception {
    // Arrange
    SignalSubscriberList signalSubscriberList = new SignalSubscriberList();

    // Act
    boolean actual = signalSubscriberList.isHasMore();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void setDataTest() throws Exception {
    // Arrange
    SignalSubscriberList signalSubscriberList = new SignalSubscriberList();
    ArrayList<SignalSubscriber> arrayList = new ArrayList<SignalSubscriber>();
    arrayList.add(new SignalSubscriber());

    // Act
    signalSubscriberList.setData(arrayList);

    // Assert
    assertSame(arrayList, signalSubscriberList.getData());
  }

  @Test
  public void setHasMoreTest() throws Exception {
    // Arrange
    SignalSubscriberList signalSubscriberList = new SignalSubscriberList();
    boolean hasMore = true;

    // Act
    signalSubscriberList.setHasMore(hasMore);

    // Assert
    assertTrue(signalSubscriberList.isHasMore());
  }

  @Test
  public void setOffsetTest() throws Exception {
    // Arrange
    SignalSubscriberList signalSubscriberList = new SignalSubscriberList();
    int offset = 1;

    // Act
    signalSubscriberList.setOffset(offset);

    // Assert
    assertEquals(1, signalSubscriberList.getOffset());
  }

  @Test
  public void setTotalTest() throws Exception {
    // Arrange
    SignalSubscriberList signalSubscriberList = new SignalSubscriberList();
    int total = 1;

    // Act
    signalSubscriberList.setTotal(total);

    // Assert
    assertEquals(1, signalSubscriberList.getTotal());
  }
}
