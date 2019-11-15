package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import model.DropdownMenuOption;
import org.junit.Test;

public class DropdownMenuOptionTest {
  @Test
  public void DropdownMenuOptionTest() throws Exception {
    // Arrange
    String value = "aaaaa";
    String display = "aaaaa";
    boolean selected = true;

    // Act
    DropdownMenuOption dropdownMenuOption = new DropdownMenuOption(value, display, selected);

    // Assert
    boolean isSelectedResult = dropdownMenuOption.isSelected();
    String display1 = dropdownMenuOption.getDisplay();
    assertTrue(isSelectedResult);
    assertEquals("aaaaa", dropdownMenuOption.getValue());
    assertEquals("aaaaa", display1);
  }

  @Test
  public void getDisplayTest() throws Exception {
    // Arrange
    DropdownMenuOption dropdownMenuOption = new DropdownMenuOption("aaaaa", "aaaaa", true);

    // Act
    String actual = dropdownMenuOption.getDisplay();

    // Assert
    assertEquals("aaaaa", actual);
  }

  @Test
  public void getValueTest() throws Exception {
    // Arrange
    DropdownMenuOption dropdownMenuOption = new DropdownMenuOption("aaaaa", "aaaaa", true);

    // Act
    String actual = dropdownMenuOption.getValue();

    // Assert
    assertEquals("aaaaa", actual);
  }

  @Test
  public void isSelectedTest() throws Exception {
    // Arrange
    DropdownMenuOption dropdownMenuOption = new DropdownMenuOption("aaaaa", "aaaaa", true);

    // Act
    boolean actual = dropdownMenuOption.isSelected();

    // Assert
    assertTrue(actual);
  }

  @Test
  public void setDisplayTest() throws Exception {
    // Arrange
    DropdownMenuOption dropdownMenuOption = new DropdownMenuOption("aaaaa", "aaaaa", true);
    String display = "aaaaa";

    // Act
    dropdownMenuOption.setDisplay(display);

    // Assert
    assertEquals("aaaaa", dropdownMenuOption.getDisplay());
  }

  @Test
  public void setSelectedTest() throws Exception {
    // Arrange
    DropdownMenuOption dropdownMenuOption = new DropdownMenuOption("aaaaa", "aaaaa", true);
    boolean selected = true;

    // Act
    dropdownMenuOption.setSelected(selected);

    // Assert
    assertTrue(dropdownMenuOption.isSelected());
  }

  @Test
  public void setValueTest() throws Exception {
    // Arrange
    DropdownMenuOption dropdownMenuOption = new DropdownMenuOption("aaaaa", "aaaaa", true);
    String value = "aaaaa";

    // Act
    dropdownMenuOption.setValue(value);

    // Assert
    assertEquals("aaaaa", dropdownMenuOption.getValue());
  }
}
