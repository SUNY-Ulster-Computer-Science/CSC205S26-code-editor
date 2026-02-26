package simpleUi;

/*
 * Provides Partial Implementation of Editor,
 * handling common functionalities and leaving 
 * specific implementations to subclasses
 * 
 * Final methods that delegate to abstract hooks
 * These cannot be overridden by subclasses
 * This ensures consistent behavior across different UI implementations
 * This follows the Template Method design pattern
 */
public abstract class AbstractEditor implements Editor {

 
  @Override public final void show() { uiShow(); }
  @Override public final String getText() { return uiGetText(); }
  @Override public final void setText(String text) { uiSetText(text); }
  @Override public final void clearText() { uiClearText(); }
  @Override public final void addButton(String label, Runnable action) { uiAddButton(label, action); }
  @Override public final void alert(String message) { uiAlert(message); }
  @Override public final String prompt(String message) { return uiPrompt(message); }
  @Override public final void highlight(String term) { uiHighlight(term); }
  @Override public final void replace(String target, String replacement) { uiReplace(target, replacement); }

  // Hooks for concrete UI
  /*
	* Displays editor window
	* @param none 
	* @ returns none
	*/
  protected abstract void uiShow();
  
  /*
   * Gets current text from editor
   * @param none 
   * @ returns String - editor text
   */
  protected abstract String uiGetText();
  
  /*
   * Sets editor text as String inputted
   * @param String text - inputted text
   * @ returns none
   */
  protected abstract void uiSetText(String text);
  
  /*
   * Clears editor of text
   * @param none 
   * @ returns none
   */
  protected abstract void uiClearText();
  
  /*
   * adds executable action to button in UI
   * @param String label - labels button
   * @param Runnable action - determines what executable the button will do when clicked
   */
  protected abstract void uiAddButton(String label, Runnable action);
  
  
  /*
   * Shows message dialogue
   * @param String message - message to display
   * @ returns none
   */
  protected abstract void uiAlert(String message);
  
  
  /*
   * Prompts user for input
   * @param String message - message to display
   * @ returns String - user input
   */
  protected abstract String uiPrompt(String message);
  
  /*
   * Highlights all matches of the term
   * @param String term - text to be highlighted
   * @ returns none
   */
  protected abstract void uiHighlight(String term);
  
  /*
   * Replaces all occurences of target text with replacement text
   * @param String target - text being swapped
   * @param String replacement  -text to replace
   * @ returns none
   */
  protected abstract void uiReplace(String target, String replacement);
  
}
