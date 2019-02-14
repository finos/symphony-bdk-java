package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamType {
    private StreamTypes type;

    public StreamTypes getType() {
      return type;
    }

    public void setType(StreamTypes type) {
      this.type = type;
    }
}
