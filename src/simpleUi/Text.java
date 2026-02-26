package simpleUi;

/*
 * creates string of text
 */
public final class Text {
/*
 * @param String s - text
 * @returns s - split into different words
 */
  private Text() {}
  public static String[] words(String s) {
    if (s == null || s.isEmpty()) return new String[0];
    return s.toLowerCase().split("\\W+");
  }
}
