package com.symphony.bdk.core.activity.parsing;

public class HashtagInputToken implements InputToken<Hashtag> {

  private Hashtag hashtag;

  public HashtagInputToken(String text, String value) {
    this.hashtag = new Hashtag(text, value);
  }

  @Override
  public Hashtag getContent() {
    return hashtag;
  }

  @Override
  public String getContentAsString() {
    return hashtag.getText();
  }
}
