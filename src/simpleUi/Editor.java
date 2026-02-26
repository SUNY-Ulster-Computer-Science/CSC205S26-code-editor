package simpleUi;


/*
 * provides a simple text editor UI with various functionalities 
 * such as displaying text, handling user input, 
 * and performing text operations
 */
public interface Editor {

  // Methods for interacting with the text editor UI
	/*
	* Displays editor window
	* @param none 
	* @ returns none
	*/
  void show();
  
  /*
  * Gets current text from editor
  * @param none 
  * @ returns String - editor text
  */
  String getText();
  
  /*
  * Sets editor text as String inputted
  * @param String text - inputted text
  * @ returns none
  */
  void setText(String text);
  
  /*
  * Clears editor of text
  * @param none 
  * @ returns none
  */
  void clearText();
  
  /*
  * Highlights all matches of the term
  * @param String term - text to be highlighted
  * @ returns none
  */
  void highlight(String term);
  
  /*
  * Replaces all occurences of target text with replacement text
  * @param String target - text being swapped
  * @param String replacement  -text to replace
  * @ returns none
  */
  void replace(String target, String replacement);

  // Method to add a button with a label and an action (Runnable)
  /*
   * adds executable action to button in UI
   * @param String label - labels button
   * @param Runnable action - determines what executable the button will do when clicked
   */
  void addButton(String label, Runnable action);

  // Methods for user interaction
  /*
  * Shows message dialogue
  * @param String message - message to display
  * @ returns none
  */
  void alert(String message);
  
  /*
   * Prompts user for input
   * @param String message - message to display
   * @ returns String - user input
   */
  String prompt(String message);

}
