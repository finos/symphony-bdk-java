package clients.symphony.api.constants;

public enum QueryParameterNames {
  SHOW_FIREHOSE_ERRORS("showFirehoseErrors");

  private String name;

  QueryParameterNames(String name){
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
