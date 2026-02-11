package simpleUi;

public interface Editor {

  // Methods for interacting with the text editor UI
  void show();
  String getText();
  void setText(String text);
  void clearText();
  void highlight(String term);
  void replace(String target, String replacement);

  // Method to add a button with a label and an action (Runnable)
  void addButton(String label, Runnable action);

  // Methods for user interaction
  void alert(String message);
  String prompt(String message);

}
