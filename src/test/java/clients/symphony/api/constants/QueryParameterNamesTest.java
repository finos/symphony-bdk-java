package clients.symphony.api.constants;

import static org.junit.Assert.assertEquals;
import clients.symphony.api.constants.QueryParameterNames;
import org.junit.Test;

public class QueryParameterNamesTest {
  @Test
  public void getNameTest() throws Exception {
    // Arrange
    QueryParameterNames queryParameterNames = QueryParameterNames.SHOW_FIREHOSE_ERRORS;

    // Act
    String actual = queryParameterNames.getName();

    // Assert
    assertEquals("showFirehoseErrors", actual);
  }
}
