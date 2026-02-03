package simpleUi;

public final class Text {
  private Text() {}
  public static String[] words(String s) {
    if (s == null || s.isEmpty()) return new String[0];
    return s.toLowerCase().split("\\W+");
  }
}
