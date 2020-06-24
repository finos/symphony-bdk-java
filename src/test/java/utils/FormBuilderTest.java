package utils;

import model.FormButtonType;
import org.junit.Test;

import static org.junit.Assert.*;

public class FormBuilderTest {

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
}
