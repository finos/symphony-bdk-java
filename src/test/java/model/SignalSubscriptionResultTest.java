package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.SignalSubscriptionResult;
import org.junit.Test;

public class SignalSubscriptionResultTest {
  @Test
  public void SignalSubscriptionResultTest() throws Exception {
    // Arrange and Act
    SignalSubscriptionResult signalSubscriptionResult = new SignalSubscriptionResult();

    // Assert
    assertEquals(0, signalSubscriptionResult.getSuccessfulSubscription());
  }

  @Test
  public void getFailedSubscriptionTest() throws Exception {
    // Arrange
    SignalSubscriptionResult signalSubscriptionResult = new SignalSubscriptionResult();

    // Act
    int actual = signalSubscriptionResult.getFailedSubscription();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getRequestedSubscriptionTest() throws Exception {
    // Arrange
    SignalSubscriptionResult signalSubscriptionResult = new SignalSubscriptionResult();

    // Act
    int actual = signalSubscriptionResult.getRequestedSubscription();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getSubscriptionErrorsTest() throws Exception {
    // Arrange
    SignalSubscriptionResult signalSubscriptionResult = new SignalSubscriptionResult();

    // Act
    List<Long> actual = signalSubscriptionResult.getSubscriptionErrors();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSuccessfulSubscriptionTest() throws Exception {
    // Arrange
    SignalSubscriptionResult signalSubscriptionResult = new SignalSubscriptionResult();

    // Act
    int actual = signalSubscriptionResult.getSuccessfulSubscription();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void setFailedSubscriptionTest() throws Exception {
    // Arrange
    SignalSubscriptionResult signalSubscriptionResult = new SignalSubscriptionResult();
    int failedSubscription = 1;

    // Act
    signalSubscriptionResult.setFailedSubscription(failedSubscription);

    // Assert
    assertEquals(1, signalSubscriptionResult.getFailedSubscription());
  }

  @Test
  public void setRequestedSubscriptionTest() throws Exception {
    // Arrange
    SignalSubscriptionResult signalSubscriptionResult = new SignalSubscriptionResult();
    int requestedSubscription = 1;

    // Act
    signalSubscriptionResult.setRequestedSubscription(requestedSubscription);

    // Assert
    assertEquals(1, signalSubscriptionResult.getRequestedSubscription());
  }

  @Test
  public void setSubscriptionErrorsTest() throws Exception {
    // Arrange
    SignalSubscriptionResult signalSubscriptionResult = new SignalSubscriptionResult();
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act
    signalSubscriptionResult.setSubscriptionErrors(arrayList);

    // Assert
    assertSame(arrayList, signalSubscriptionResult.getSubscriptionErrors());
  }

  @Test
  public void setSuccessfulSubscriptionTest() throws Exception {
    // Arrange
    SignalSubscriptionResult signalSubscriptionResult = new SignalSubscriptionResult();
    int successfulSubscription = 1;

    // Act
    signalSubscriptionResult.setSuccessfulSubscription(successfulSubscription);

    // Assert
    assertEquals(1, signalSubscriptionResult.getSuccessfulSubscription());
  }
}
