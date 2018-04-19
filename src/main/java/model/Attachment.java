package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class Attachment {

    private String id;
    private String name;
    private Long size;
    private ImageInfo image;

}
