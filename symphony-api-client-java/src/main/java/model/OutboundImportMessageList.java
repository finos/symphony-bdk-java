package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OutboundImportMessageList extends ArrayList<OutboundImportMessage> {
}
