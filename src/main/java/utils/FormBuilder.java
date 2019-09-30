package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import model.DropdownMenuOption;
import model.FormButtonType;
import model.TableSelectPosition;
import model.TableSelectType;

public class FormBuilder {
    private String formId;
    private StringBuilder messageML;

    public FormBuilder(String formId) {
        this.formId = formId;
        this.messageML = new StringBuilder();
    }

    public static FormBuilder builder(String formId) {
        return new FormBuilder(formId);
    }

    public String formatElement() {
        return TagBuilder.builder("form")
            .addField("id", formId)
            .setContents(messageML.toString())
            .build();
    }

    public FormBuilder addLineBreak() {
        messageML.append(TagBuilder.builder("br").buildSelfClosing());
        return this;
    }

    public FormBuilder addLineBreaks(int quantity) {
        for (int i = 0; i < quantity; i++) {
            messageML.append(TagBuilder.builder("br").buildSelfClosing());
        }
        return this;
    }

    public FormBuilder addDiv(String contents) {
        messageML.append(TagBuilder.builder("div").setContents(contents).build());
        return this;
    }

    public FormBuilder addHeader(int size, String text) {
        if (size < 1 || size > 6) {
            size = 6;
        }

        messageML.append(TagBuilder.builder("h" + size).setContents(text).build());
        return this;
    }

    public FormBuilder addButton(String name, String display, FormButtonType type) {
        String buttonML = TagBuilder.builder("button")
            .addField("name", name)
            .addField("type", type.toString().toLowerCase())
            .setContents(display)
            .build();

        messageML.append(buttonML);
        return this;
    }

    public FormBuilder addTextField(
        String name, String display, String placeholder, boolean required, boolean masked, int minlength, int maxLength
    ) {
        String textFieldML = TagBuilder.builder("text-field")
            .addField("name", name)
            .addField("placeholder", placeholder)
            .addField("required", required)
            .addField("masked", masked)
            .addField("minlength", minlength)
            .addField("maxlength", maxLength)
            .setContents(display)
            .build();

        messageML.append(textFieldML);
        return this;
    }

    public FormBuilder addTextField(String name, String display, String placeholder, boolean required) {
        TagBuilder textFieldMLBuilder = TagBuilder.builder("text-field")
            .addField("name", name)
            .addField("placeholder", placeholder)
            .addField("required", required);

        if (display != null) {
            messageML.append(textFieldMLBuilder.setContents(display).build());
        } else {
            messageML.append(textFieldMLBuilder.buildSelfClosing());
        }
        return this;
    }

    public FormBuilder addTextArea(String name, String display, String placeholder, boolean required) {
        String textAreaML = TagBuilder.builder("textarea")
            .addField("name", name)
            .addField("placeholder", placeholder)
            .addField("required", required)
            .setContents(display)
            .build();

        messageML.append(textAreaML);
        return this;
    }

    public FormBuilder addCheckBox(String name, String display, String value, boolean checked) {
        String textAreaML = TagBuilder.builder("checkbox")
            .addField("name", name)
            .addField("value", value)
            .addField("checked", checked)
            .setContents(display)
            .build();

        messageML.append(textAreaML);
        return this;
    }

    public FormBuilder addRadioButton(String name, String display, String value, boolean checked) {
        String radioButtonML = TagBuilder.builder("radio")
            .addField("name", name)
            .addField("value", value)
            .addField("checked", checked)
            .setContents(display)
            .build();

        messageML.append(radioButtonML);
        return this;
    }

    public FormBuilder addDropdownMenu(String name, boolean required, List<DropdownMenuOption> options) {
        String optionsML = options.stream()
            .map(option -> TagBuilder.builder("option")
                .addField("value", option.getValue())
                .addField("selected", option.isSelected())
                .setContents(option.getDisplay())
                .build()
            )
            .collect(join());

        String dropdownMenuML = TagBuilder.builder("select")
            .addField("name", name)
            .addField("required", required)
            .setContents(optionsML)
            .build();

        messageML.append(dropdownMenuML);
        return this;
    }

    public FormBuilder addPersonSelector(String name, String placeholder, boolean required) {
        String personSelectorML = TagBuilder.builder("person-selector")
            .addField("name", name)
            .addField("placeholder", placeholder)
            .addField("required", required)
            .buildSelfClosing();

        messageML.append(personSelectorML);
        return this;
    }

    public FormBuilder addTableSelect(
        String name,
        String selectorDisplay,
        TableSelectPosition position,
        TableSelectType type,
        List<String> header,
        List<List<String>> body,
        List<String> footer
    ) {
        String headerML = "";
        String bodyML = "";
        String footerML = "";

        if (name == null) {
            name = "table-select";
        } else {
            name = name.replaceAll("[^a-zA-Z0-9]", "");
            if (name.trim().length() == 0) {
                name = "table-select";
            }
        }

        if (selectorDisplay == null || selectorDisplay.trim().length() == 0) {
            selectorDisplay = "Select";
        }

        if (header != null && !header.isEmpty()) {
            List<String> workingHeader = new ArrayList<>(header);
            String headerSelector = "Select";
            if (type == TableSelectType.CHECKBOX) {
                headerSelector = TagBuilder.builder("input")
                    .addField("type", "checkbox")
                    .addField("name", name + "-header")
                    .buildSelfClosing();
            }
            int index = (position == TableSelectPosition.LEFT) ? 0 : workingHeader.size();
            workingHeader.add(index, headerSelector);

            String headerRow = workingHeader.stream()
                .map(value -> TagBuilder.builder("td").setContents(value).build())
                .collect(join());

            headerML = TagBuilder.builder("thead")
                .setContents(TagBuilder.builder("tr").setContents(headerRow).build())
                .build();
        }

        if (body != null && !body.isEmpty()) {
            List<List<String>> workingBody = new ArrayList<>();
            for (int i = 0; i < body.size(); i++) {
                List<String> bodyRow = new ArrayList<>(body.get(i));
                String rowSelector = buildRowSelector(name + "-row-" + i, type, selectorDisplay);
                int index = (position == TableSelectPosition.LEFT) ? 0 : bodyRow.size();
                bodyRow.add(index, rowSelector);
                workingBody.add(bodyRow);
            }

            String bodyRows = workingBody.stream().map(bodyRow ->
                TagBuilder.builder("tr")
                    .setContents(bodyRow.stream()
                        .map(value -> TagBuilder.builder("td").setContents(value).build()).collect(join())
                    )
                    .build())
                .collect(join());

            bodyML = String.format("<tbody>%s</tbody>", bodyRows);
        }

        if (footer != null && !footer.isEmpty()) {
            List<String> workingFooter = new ArrayList<>(footer);
            String footerSelector = buildRowSelector(name + "-footer", type, selectorDisplay);

            int index = (position == TableSelectPosition.LEFT) ? 0 : workingFooter.size();
            workingFooter.add(index, footerSelector);

            String footerRow = workingFooter.stream()
                .map(value -> TagBuilder.builder("td").setContents(value).build())
                .collect(join());

            footerML = TagBuilder.builder("tfoot")
                .setContents(TagBuilder.builder("tr").setContents(footerRow).build())
                .build();
        }

        String tableML = TagBuilder.builder("table").setContents(headerML + bodyML + footerML).build();
        messageML.append(tableML);
        return this;
    }

    private String buildRowSelector(String selectorName, TableSelectType type, String selectorDisplay) {
        if (type == TableSelectType.BUTTON) {
            return TagBuilder.builder("button")
                .addField("name", selectorName)
                .setContents(selectorDisplay)
                .build();
        }
        return TagBuilder.builder("input")
            .addField("name", selectorName)
            .addField("type", "checkbox")
            .buildSelfClosing();
    }

    private static Collector<CharSequence, ?, String> join() {
        return Collectors.joining("");
    }
}
