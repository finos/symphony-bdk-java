package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.DropdownMenuOption;
import model.FormButtonType;
import model.TableSelectPosition;
import model.TableSelectType;
import org.junit.Test;
import utils.FormBuilder;

public class FormBuilderTest {
  @Test
  public void addButtonTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");
    String name = "aaaaa";
    String display = "aaaaa";
    FormButtonType type = FormButtonType.ACTION;

    // Act
    FormBuilder actual = formBuilder.addButton(name, display, type);

    // Assert
    assertSame(formBuilder, actual);
  }

  @Test
  public void addCheckBoxTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");
    String name = "aaaaa";
    String display = "aaaaa";
    String value = "aaaaa";
    boolean checked = true;

    // Act
    FormBuilder actual = formBuilder.addCheckBox(name, display, value, checked);

    // Assert
    assertSame(formBuilder, actual);
  }

  @Test
  public void addDivTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");
    String contents = "aaaaa";

    // Act
    FormBuilder actual = formBuilder.addDiv(contents);

    // Assert
    assertSame(formBuilder, actual);
  }

  @Test
  public void addDropdownMenuTest() throws Exception {
    // Arrange
    String string = "aaaaa";
    FormBuilder formBuilder = new FormBuilder(string);
    String name = "aaaaa";
    boolean required = true;
    ArrayList<DropdownMenuOption> arrayList = new ArrayList<DropdownMenuOption>();
    arrayList.add(new DropdownMenuOption("aaaaa", string, true));

    // Act
    formBuilder.addDropdownMenu(name, required, arrayList);

    // Assert
    assertEquals(1, arrayList.size());
  }

  @Test
  public void addHeaderTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");
    int size = 1;
    String text = "aaaaa";

    // Act
    FormBuilder actual = formBuilder.addHeader(size, text);

    // Assert
    assertSame(formBuilder, actual);
  }

  @Test
  public void addLineBreakTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");

    // Act
    FormBuilder actual = formBuilder.addLineBreak();

    // Assert
    assertSame(formBuilder, actual);
  }

  @Test
  public void addLineBreaksTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");
    int quantity = 1;

    // Act
    FormBuilder actual = formBuilder.addLineBreaks(quantity);

    // Assert
    assertSame(formBuilder, actual);
  }

  @Test
  public void addPersonSelectorTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");
    String name = "aaaaa";
    String placeholder = "aaaaa";
    boolean required = true;

    // Act
    FormBuilder actual = formBuilder.addPersonSelector(name, placeholder, required);

    // Assert
    assertSame(formBuilder, actual);
  }

  @Test
  public void addRadioButtonTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");
    String name = "aaaaa";
    String display = "aaaaa";
    String value = "aaaaa";
    boolean checked = true;

    // Act
    FormBuilder actual = formBuilder.addRadioButton(name, display, value, checked);

    // Assert
    assertSame(formBuilder, actual);
  }

  @Test
  public void addTableSelectTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");
    String name = "aaaaa";
    String selectorDisplay = "aaaaa";
    TableSelectPosition position = TableSelectPosition.LEFT;
    TableSelectType type = TableSelectType.BUTTON;
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("akaaa");
    ArrayList<List<String>> arrayList1 = new ArrayList<List<String>>();
    arrayList1.add(arrayList);
    ArrayList<String> arrayList2 = new ArrayList<String>();
    arrayList2.add("aaaaa");

    // Act
    formBuilder.addTableSelect(name, selectorDisplay, position, type, arrayList, arrayList1, arrayList2);

    // Assert
    assertEquals(1, arrayList.size());
  }

  @Test
  public void addTextAreaTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");
    String name = "aaaaa";
    String display = "aaaaa";
    String placeholder = "aaaaa";
    boolean required = true;

    // Act
    FormBuilder actual = formBuilder.addTextArea(name, display, placeholder, required);

    // Assert
    assertSame(formBuilder, actual);
  }

  @Test
  public void addTextFieldTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");
    String name = "aaaaa";
    String display = "aaaaa";
    String placeholder = "aaaaa";
    boolean required = true;
    boolean masked = true;
    int minlength = 1;
    int maxLength = 1;

    // Act
    FormBuilder actual = formBuilder.addTextField(name, display, placeholder, required, masked, minlength, maxLength);

    // Assert
    assertSame(formBuilder, actual);
  }

  @Test
  public void addTextFieldTest2() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");
    String name = "aaaaa";
    String display = "aaaaa";
    String placeholder = "aaaaa";
    boolean required = true;

    // Act
    FormBuilder actual = formBuilder.addTextField(name, display, placeholder, required);

    // Assert
    assertSame(formBuilder, actual);
  }

  @Test
  public void builderTest() throws Exception {
    // Arrange
    String formId = "aaaaa";

    // Act
    FormBuilder.builder(formId);
  }

  @Test
  public void formatElementTest() throws Exception {
    // Arrange
    FormBuilder formBuilder = new FormBuilder("aaaaa");

    // Act
    String actual = formBuilder.formatElement();

    // Assert
    assertEquals("<form id=\"aaaaa\"></form>", actual);
  }
}
