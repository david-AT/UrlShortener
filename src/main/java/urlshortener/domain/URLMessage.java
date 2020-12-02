package urlshortener.domain;

public class URLMessage {

  private String name;

  public URLMessage() {
  }

  public URLMessage(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}