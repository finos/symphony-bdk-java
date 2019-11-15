package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.StreamAttributes;
import org.junit.Test;

public class StreamAttributesTest {
  @Test
  public void StreamAttributesTest() throws Exception {
    // Arrange and Act
    StreamAttributes streamAttributes = new StreamAttributes();

    // Assert
    assertEquals(null, streamAttributes.getMembers());
  }

  @Test
  public void getMembersTest() throws Exception {
    // Arrange
    StreamAttributes streamAttributes = new StreamAttributes();

    // Act
    List<Long> actual = streamAttributes.getMembers();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setMembersTest() throws Exception {
    // Arrange
    StreamAttributes streamAttributes = new StreamAttributes();
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act
    streamAttributes.setMembers(arrayList);

    // Assert
    assertSame(arrayList, streamAttributes.getMembers());
  }
}
