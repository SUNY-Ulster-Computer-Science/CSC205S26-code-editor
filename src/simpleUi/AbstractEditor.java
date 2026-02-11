package simpleUi;

public abstract class AbstractEditor implements Editor {

  // Final methods that delegate to abstract hooks
  // These cannot be overridden by subclasses
  // This ensures consistent behavior across different UI implementations
  // This follows the Template Method design pattern
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
  protected abstract void uiShow();
  protected abstract String uiGetText();
  protected abstract void uiSetText(String text);
  protected abstract void uiClearText();
  protected abstract void uiAddButton(String label, Runnable action);
  protected abstract void uiAlert(String message);
  protected abstract String uiPrompt(String message);
  protected abstract void uiHighlight(String term);
  protected abstract void uiReplace(String target, String replacement);
  
}
