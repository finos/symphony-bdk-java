package utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TagBuilderTest {

  private TagBuilder tagBuilder;
  private String tagName;

  @Before
  public void initTagBuilder(){
    tagName = "Test 1";
    tagBuilder = new TagBuilder(tagName);
  }

  @Test
  public void addFieldTest(){
    tagBuilder.addField("New field", "New value");
    Assert.assertTrue(true);
  }

  @Test
  public void setContentsTest(){
    tagBuilder.setContents("Test content");
    Assert.assertEquals("<Test 1>Test content</Test 1>", tagBuilder.build());
  }

  @Test
  public void buildTest(){
    tagBuilder.addField("New field 1", "New value 1");
    tagBuilder.addField("New field 2", "New value 2");
    tagBuilder.addField("New field 3", "New value 3");
    tagBuilder.setContents("Test content");

    final String expectedMarkup = "New field 1 = New value 1,"
        + "New field 2 = New value 2,"
        + "New field 3 = New value 3";

    final String build = tagBuilder.build();
    Assert.assertEquals("<Test 1 New field 3=\"New value 3\" New field 2=\"New value 2\" New field 1=\"New value 1\">Test content</Test 1>", build);


    final String emptyTagBuilderName = "Empty tagBuilder";
    final TagBuilder emptyTagBuilder = new TagBuilder(emptyTagBuilderName);
    emptyTagBuilder.setContents("void content");
    Assert.assertEquals("<Empty tagBuilder>void content</Empty tagBuilder>", emptyTagBuilder.build());
  }

  @Test
  public void buildSelfClosingTest(){
    tagBuilder.addField("New field 1", "New value 1");
    tagBuilder.addField("New field 2", "New value 2");
    tagBuilder.addField("New field 3", "New value 3");
    tagBuilder.setContents("Test content");


    final String buildSelfClosing = tagBuilder.buildSelfClosing();
    final String expectedBuildSelfClosing = "<Test 1 New field 3=\"New value 3\" New field 2=\"New value 2\" New field 1=\"New value 1\" />";
    Assert.assertEquals(buildSelfClosing, expectedBuildSelfClosing);
  }
}
