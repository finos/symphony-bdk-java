package com.symphony.bdk.core.activity.parsing.input;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public class CashtagInputToken implements InputToken<Cashtag> {

  private Cashtag cashtag;

  public CashtagInputToken(String text, String value) {
    this.cashtag = new Cashtag(text, value);
  }

  @Override
  public Cashtag getContent() {
    return cashtag;
  }
}
