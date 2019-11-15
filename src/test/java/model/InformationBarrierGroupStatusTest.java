package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.InformationBarrierGroupStatus;
import org.junit.Test;

public class InformationBarrierGroupStatusTest {
  @Test
  public void InformationBarrierGroupStatusTest() throws Exception {
    // Arrange and Act
    InformationBarrierGroupStatus informationBarrierGroupStatus = new InformationBarrierGroupStatus();

    // Assert
    assertEquals(null, informationBarrierGroupStatus.getOverallResult());
  }

  @Test
  public void getOverallResultTest() throws Exception {
    // Arrange
    InformationBarrierGroupStatus informationBarrierGroupStatus = new InformationBarrierGroupStatus();

    // Act
    String actual = informationBarrierGroupStatus.getOverallResult();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getResultsTest() throws Exception {
    // Arrange
    InformationBarrierGroupStatus informationBarrierGroupStatus = new InformationBarrierGroupStatus();

    // Act
    List<String> actual = informationBarrierGroupStatus.getResults();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setOverallResultTest() throws Exception {
    // Arrange
    InformationBarrierGroupStatus informationBarrierGroupStatus = new InformationBarrierGroupStatus();
    String overallResult = "aaaaa";

    // Act
    informationBarrierGroupStatus.setOverallResult(overallResult);

    // Assert
    assertEquals("aaaaa", informationBarrierGroupStatus.getOverallResult());
  }

  @Test
  public void setResultsTest() throws Exception {
    // Arrange
    InformationBarrierGroupStatus informationBarrierGroupStatus = new InformationBarrierGroupStatus();
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");

    // Act
    informationBarrierGroupStatus.setResults(arrayList);

    // Assert
    assertSame(arrayList, informationBarrierGroupStatus.getResults());
  }
}
