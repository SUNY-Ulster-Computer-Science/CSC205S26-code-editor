package simpleUi;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

/*
 * Concrete implementation of the Editor interface, 
 * provides a basic text editor UI with JTextPane support
 */
public final class SimpleEditor extends AbstractEditor {
    /*represents new window display*/
    private final JFrame frame;
    /*represents area for text - now using JTextPane for styling*/
    private final JTextPane textPane;
    /*represents area for buttons*/
    private final JPanel buttonRow;
    
    /*
     * Constructs simple Text Editor in a new window
     * @param title - text editor name
     * @returns none
     */
    public SimpleEditor(String title) {
        frame = new JFrame(title);
        textPane = new JTextPane();
        buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        init();
    }
    
    /*
     * Builds frame to display content in text editor
     * @param none
     * @return none
     */
    private void init() {
        // Enable line wrapping for JTextPane
        textPane.setEditorKit(new StyledEditorKit());
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 800);
        
        JScrollPane scrollPane = new JScrollPane(textPane);
        
        // Add line numbers
        JTextArea lines = new JTextArea("1");
        lines.setBackground(Color.LIGHT_GRAY);
        lines.setEditable(false);
        lines.setFocusable(false);
        lines.setFont(textPane.getFont());
        
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            private String getNumbers() {
                int lineposition = textPane.getDocument().getLength();
                Element root = textPane.getDocument().getDefaultRootElement();
                StringBuilder sb = new StringBuilder("1");
                for (int i = 2; i <= root.getElementIndex(lineposition) + 1; i++) {
                    sb.append(System.lineSeparator()).append(i);
                }
                return sb.toString();
            }
            public void changedUpdate(DocumentEvent e) { lines.setText(getNumbers()); }
            public void insertUpdate(DocumentEvent e) { lines.setText(getNumbers()); }
            public void removeUpdate(DocumentEvent e) { lines.setText(getNumbers()); }
        });
        
        scrollPane.setRowHeaderView(lines);
        
        JPanel content = new JPanel(new BorderLayout(5, 5));
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(buttonRow, BorderLayout.SOUTH);
        
        frame.setContentPane(content);
    }
    
    @Override
    protected void uiShow() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
    
    @Override
    protected String uiGetText() { 
        return textPane.getText(); 
    }
    
    @Override
    protected void uiSetText(String text) { 
        textPane.setText(text); 
    }
    
    @Override
    protected void uiClearText() { 
        textPane.setText(""); 
        clearHighlights(); 
    }
    
    @Override
    protected void uiAddButton(String label, Runnable action) {
        JButton b = new JButton(label);
        b.addActionListener(e -> action.run());
        buttonRow.add(b);
        buttonRow.revalidate();
        buttonRow.repaint();
    }
    
    @Override
    protected void uiAlert(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }
    
    @Override
    protected String uiPrompt(String message) {
        return JOptionPane.showInputDialog(frame, message);
    }
    
    @Override
    protected void uiHighlight(String term) {
        clearHighlights();
        if (term == null || term.isEmpty()) return;
        
        Highlighter hl = textPane.getHighlighter();
        Highlighter.HighlightPainter painter =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 255, 0, 128));
        
        String text = textPane.getText();
        String hay = text.toLowerCase();
        String needle = term.toLowerCase();
        
        int index = 0;
        while (true) {
            index = hay.indexOf(needle, index);
            if (index < 0) break;
            try {
                hl.addHighlight(index, index + term.length(), painter);
            } catch (BadLocationException ignored) { }
            index += term.length();
        }
    }
    
    @Override
    protected void uiReplace(String target, String replacement) {
        if (target == null || target.isEmpty()) return;
        String current = textPane.getText();
        String updated = current.replace(target, replacement == null ? "" : replacement);
        textPane.setText(updated);
        clearHighlights();
    }
    
    /*
     * Removes highlights from display
     * @param none
     * @return none 
     */
    private void clearHighlights() {
        textPane.getHighlighter().removeAllHighlights();
    }
    
    // Getter for syntax highlighting
    public JTextPane getTextPane() {
        return textPane;
    }
}
