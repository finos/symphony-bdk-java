package com.symphony.bdk.core.activity.parsing;

public class CashtagInputToken implements InputToken<Cashtag> {

  private Cashtag cashtag;

  public CashtagInputToken(String text, String value) {
    this.cashtag = new Cashtag(text, value);
  }

  @Override
  public Cashtag getContent() {
    return cashtag;
  }

  @Override
  public String getContentAsString() {
    return cashtag.getText();
  }
}
