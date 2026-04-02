package Syntax;

import simpleUi.SimpleEditor;
import javax.swing.JTextPane;

/**
 * Manages syntax highlighting for the SimpleEditor
 * Works with the updated SimpleEditor that uses JTextPane
 */
public class SyntaxHighlighterManager {
    
    private JavaSyntaxHighlighterTextColor highlighter;
    private final SimpleEditor editor;  // Changed from Editor to SimpleEditor
    private JTextPane textPane;
    
    /**
     * Constructor - gets the text pane from the editor
     * @param editor The SimpleEditor instance
     */
    public SyntaxHighlighterManager(SimpleEditor editor) {
        this.editor = editor;
        this.textPane = editor.getTextPane();  // Use the getter we added
        
        if (textPane != null) {
            this.highlighter = new JavaSyntaxHighlighterTextColor(textPane);
        }
    }
    
    /**
     * Enable syntax highlighting
     */
    public void enable() {
        if (highlighter != null) {
            highlighter.enable();
        }
    }
    
    /**
     * Disable syntax highlighting
     */
    public void disable() {
        if (highlighter != null) {
            highlighter.disable();
        }
    }
    
    /**
     * Toggle syntax highlighting
     */
    public void toggle() {
        if (highlighter != null) {
            highlighter.toggle();
        }
    }
    
    /**
     * Check if syntax highlighting is enabled
     */
    public boolean isEnabled() {
        return highlighter != null && highlighter.isEnabled();
    }
}
