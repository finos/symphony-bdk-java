package utils;

import model.DropdownMenuOption;
import model.FormButtonType;
import model.TableSelectPosition;
import model.TableSelectType;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FormBuilderTest {

  @Test
  public void addLineBreakTest() {
    FormBuilder formBuilder = new FormBuilder("form_id");
    String messageText = formBuilder.addLineBreak().formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<br />"
        + "</form>");
  }

  @Test
  public void addLineBreaksTest() {
    String messageText = FormBuilder.builder("form_id")
        .addLineBreaks(3).formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<br /><br /><br />"
        + "</form>");
  }

  @Test
  public void addDivTest() {
    String messageText = FormBuilder.builder("form_id")
        .addDiv("Test Content").formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<div>Test Content</div>"
        + "</form>");
  }

  @Test
  public void addHeaderTest() {
    String messageText = FormBuilder.builder("form_id")
        .addHeader(1, "Test Header").formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<h1>Test Header</h1>"
        + "</form>");
  }

  @Test
  public void addHeaderSize7Test() {
    String messageText = FormBuilder.builder("form_id")
        .addHeader(7, "Test Header").formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<h6>Test Header</h6>"
        + "</form>");
  }

  @Test
  public void addActionButtonTest() {
    String messageText = FormBuilder.builder("form_id")
        .addButton("submit_button", "Submit", FormButtonType.ACTION).formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<button name=\"submit_button\" type=\"action\">Submit</button>"
        + "</form>");
  }

  @Test
  public void addResetButtonTest() {
    String messageText = FormBuilder.builder("form_id")
        .addButton("reset_button", "Reset", FormButtonType.RESET).formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<button type=\"reset\">Reset</button>"
        + "</form>");
  }

  @Test
  public void addButtonWithNullFields() {
    String messageText = FormBuilder.builder("form_id")
        .addButton(null, null, FormButtonType.ACTION)
        .addButton(null, null, FormButtonType.RESET)
        .formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<button name=\"null\" type=\"action\">null</button>"
        + "<button type=\"reset\">null</button>"
        + "</form>");
  }

  @Test
  public void addTextFieldTest() {
    String messageText = FormBuilder.builder("form_id")
        .addTextField("text_field", null, "Enter Text", false)
        .formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<text-field name=\"text_field\" placeholder=\"Enter Text\" required=\"false\" />"
        + "</form>");
  }

  @Test
  public void addTextFieldMaskedTest() {
    String messageText = FormBuilder.builder("form_id")
        .addTextField("text_field", "Display Test", "Enter Text", false, true)
        .formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<text-field masked=\"true\" name=\"text_field\" placeholder=\"Enter Text\" required=\"false\">"
        + "Display Test"
        + "</text-field>"
        + "</form>");
  }

  @Test
  public void addTextFieldLengthTest() {
    String messageText = FormBuilder.builder("form_id")
        .addTextField(null, null, null, true, false, 5, 25)
        .formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<text-field minlength=\"5\" maxlength=\"25\" masked=\"false\" name=\"null\" placeholder=\"null\" required=\"true\" />"
        + "</form>");
  }

  @Test
  public void addTextAreaTest() {
    String messageText = FormBuilder.builder("form_id")
        .addTextArea("text_area", "Test TextArea", "Enter Text", false)
        .formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<textarea name=\"text_area\" placeholder=\"Enter Text\" required=\"false\">"
        + "Test TextArea"
        + "</textarea>"
        + "</form>");
  }

  @Test
  public void addCheckBoxTest() {
    String messageText = FormBuilder.builder("form_id")
        .addCheckBox("test_checkbox", "Test Checkbox", "test", false)
        .formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<checkbox name=\"test_checkbox\" checked=\"false\" value=\"test\">"
        + "Test Checkbox"
        + "</checkbox>"
        + "</form>");
  }

  @Test
  public void addRadioButtonTest() {
    String messageText = FormBuilder.builder("form_id")
        .addRadioButton("test_radio", "Test Radio", "test", false)
        .formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<radio name=\"test_radio\" checked=\"false\" value=\"test\">"
        + "Test Radio"
        + "</radio>"
        + "</form>");
  }

  @Test
  public void addDropdownMenuTest() throws IOException {
    List<DropdownMenuOption> options = new ArrayList<DropdownMenuOption>() {{
      add(new DropdownMenuOption("test1", "Test 1", true));
      add(new DropdownMenuOption("test2", "Test 2", false));
      add(new DropdownMenuOption("test3", "Test 3", false));
    }};
    String messageText = FormBuilder.builder("form_id")
        .addDropdownMenu("test_dropdown", true, options)
        .formatElement();
    assertEquals(messageText, removeIndentAndNewLine(readResourceContent("/form_builder/dropdown_menu_test.xml")));
  }

  @Test
  public void addPersonSelectorTest() {
    String messageText = FormBuilder.builder("form_id")
        .addPersonSelector("test_person_selector", "Test Person Selector", false)
        .formatElement();
    assertEquals(messageText, "<form id=\"form_id\">"
        + "<person-selector name=\"test_person_selector\" placeholder=\"Test Person Selector\" required=\"false\" />"
        + "</form>");
  }

  @Test
  public void addTableSelectCheckboxTest() throws IOException {
    List<String> header = new ArrayList<>(Arrays.asList("Header1", "Header2", "Header3"));
    List<List<String>> body = new ArrayList<List<String>>(){{
      add(new ArrayList<>(Arrays.asList("Row1Col1", "Row1Col2", "Row1Col3")));
      add(new ArrayList<>(Arrays.asList("Row2Col1", "Row2Col2", "Row2Col3")));
      add(new ArrayList<>(Arrays.asList("Row3Col1", "Row3Col2", "Row3Col3")));
    }};
    List<String> footer = new ArrayList<>(Arrays.asList("Footer1", "Footer2", "Footer3"));

    String messageText = FormBuilder.builder("form_id")
        .addTableSelect(null, null, TableSelectPosition.LEFT,
            TableSelectType.CHECKBOX, header, body, footer)
        .formatElement();
    assertEquals(messageText, removeIndentAndNewLine(readResourceContent("/form_builder/table_select_checkbox_test.xml")));
  }

  @Test
  public void addTableSelectButtonTest() throws IOException {
    List<String> header = new ArrayList<>(Arrays.asList("Header1", "Header2", "Header3"));
    List<List<String>> body = new ArrayList<List<String>>(){{
      add(new ArrayList<>(Arrays.asList("Row1Col1", "Row1Col2", "Row1Col3")));
      add(new ArrayList<>(Arrays.asList("Row2Col1", "Row2Col2", "Row2Col3")));
      add(new ArrayList<>(Arrays.asList("Row3Col1", "Row3Col2", "Row3Col3")));
    }};
    List<String> footer = new ArrayList<>(Arrays.asList("Footer1", "Footer2", "Footer3"));

    String messageText = FormBuilder.builder("form_id")
        .addTableSelect("()_", null, TableSelectPosition.LEFT,
            TableSelectType.BUTTON, header, body, footer)
        .formatElement();
    assertEquals(messageText, removeIndentAndNewLine(readResourceContent("/form_builder/table_select_button_test.xml")));
  }

  private String readResourceContent(String path) throws IOException {
    InputStream resourceStream = FormBuilder.class.getResourceAsStream(path);
    return IOUtils.toString(resourceStream, StandardCharsets.UTF_8.name());
  }

  private String removeIndentAndNewLine(String text) {
    return text.replace("\n", "")
        .replace("\r", "")
        .replaceAll(" [\\s]{2,}", "");
  }
}
