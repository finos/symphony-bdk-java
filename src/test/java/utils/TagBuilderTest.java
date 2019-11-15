package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import utils.TagBuilder;

public class TagBuilderTest {
  @Test
  public void addFieldTest() throws Exception {
    // Arrange
    TagBuilder tagBuilder = new TagBuilder("aaaaa");
    String fieldName = "aaaaa";
    String fieldValue = "aaaaa";

    // Act
    TagBuilder actual = tagBuilder.addField(fieldName, fieldValue);

    // Assert
    assertSame(tagBuilder, actual);
  }

  @Test
  public void buildSelfClosingTest() throws Exception {
    // Arrange
    TagBuilder tagBuilder = new TagBuilder("aaaaa");

    // Act
    String actual = tagBuilder.buildSelfClosing();

    // Assert
    assertEquals("<aaaaa />", actual);
  }

  @Test
  public void buildTest() throws Exception {
    // Arrange
    TagBuilder tagBuilder = new TagBuilder("aaaaa");

    // Act
    String actual = tagBuilder.build();

    // Assert
    assertEquals("<aaaaa>null</aaaaa>", actual);
  }

  @Test
  public void builderTest() throws Exception {
    // Arrange
    String tagName = "aaaaa";

    // Act
    TagBuilder.builder(tagName);
  }

  @Test
  public void setContentsTest() throws Exception {
    // Arrange
    TagBuilder tagBuilder = new TagBuilder("aaaaa");
    String contents = "aaaaa";

    // Act
    TagBuilder actual = tagBuilder.setContents(contents);

    // Assert
    assertSame(tagBuilder, actual);
  }
}
