package simpleUi;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

import compiler.CompleteJavaRunner;
import compiler.MemoryJavaFileManager;
import compiler.JavaCompilerAPI;
import compiler.RunResult;
import compiler.MemoryClassLoader;
import compiler.CompilationResult;
import compiler.CompilationError;
import compiler.InMemoryJavaFileObject;
import javax.swing.event.*;
import java.io.*;

/*
 * Concrete implementation of the Editor interface, 
 * provides a basic text editor UI with JTextPane support
 * and Java code compilation/running capabilities
 */
public final class SimpleEditor extends AbstractEditor {
    /*represents new window display*/
    private final JFrame frame;
    /*represents text - now using JTextPane for styling*/
    private final JTextPane textPane;
    /*represents area for buttons*/
    private final JPanel buttonRow;
    
    private JMenuBar menuBar;
    //for menu
    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem runItem;  // Added Run menu item
    private JMenuItem compileItem; // Added Compile menu item

    private JMenuItem undoItem;
    private JMenuItem redoItem;
    private JMenuItem clearItem;

    private JMenuItem findItem;
    private JMenuItem replaceItem;
    private JMenuItem highlightItem;

    private JMenuItem syntaxItem;
    
    // Output console area
    private JTextArea consoleArea;
    private JTabbedPane tabbedPane;
    
    // Status bar
    private JLabel statusBar;

    public SimpleEditor(String title) {
        frame = new JFrame(title);
        textPane = new JTextPane();
        buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        init();
    }
    
    /*
     * Builds frame to display content in text editor
     */
    private void init() {
        // Enable line wrapping for JTextPane
        textPane.setEditorKit(new StyledEditorKit());
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textPane.setBackground(Color.WHITE);
        textPane.setForeground(Color.BLACK);
        textPane.setCaretColor(Color.BLACK);
        recolorExistingText(Color.BLACK);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        
        // Create tabbed pane for editor and console
        tabbedPane = new JTabbedPane();
        
        // Editor panel with line numbers
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
            public void changedUpdate(javax.swing.event.DocumentEvent e) { lines.setText(getNumbers()); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { lines.setText(getNumbers()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { lines.setText(getNumbers()); }
        });

        scrollPane.setRowHeaderView(lines);
        
        // Console area for output
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        consoleArea.setBackground(Color.BLACK);
        consoleArea.setForeground(Color.GREEN);
        JScrollPane consoleScroll = new JScrollPane(consoleArea);
        consoleScroll.setPreferredSize(new Dimension(800, 200));
        
        // Add tabs
        tabbedPane.addTab("Editor", scrollPane);
        tabbedPane.addTab("Console", consoleScroll);
        
        // Status bar
        statusBar = new JLabel(" Ready");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JPanel content1 = new JPanel(new BorderLayout(10, 5));
        content1.add(tabbedPane, BorderLayout.CENTER);
        content1.add(buttonRow, BorderLayout.SOUTH);
        content1.add(statusBar, BorderLayout.NORTH);

        frame.setContentPane(content1);
        
        // Create menu bar
        createMenuBar();
        
        // Add action buttons
        setupButtons();
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        undoItem = new JMenuItem("Undo");
        redoItem = new JMenuItem("Redo");
        clearItem = new JMenuItem("Clear");

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.add(clearItem);

        // Search Menu
        JMenu searchMenu = new JMenu("Search");
        findItem = new JMenuItem("Find");
        replaceItem = new JMenuItem("Replace (Case sensitive)");
        highlightItem = new JMenuItem("Highlight Matches");

        searchMenu.add(findItem);
        searchMenu.add(replaceItem);
        searchMenu.add(highlightItem);
        
        // Run Menu
        JMenu runMenu = new JMenu("Run");
        runItem = new JMenuItem("Run Code");
        compileItem = new JMenuItem("Compile Only");
        
        runMenu.add(runItem);
        runMenu.add(compileItem);
        
        // Settings Menu
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem lightModeItem = new JMenuItem("Light Mode");
        JMenuItem darkModeItem = new JMenuItem("Dark Mode");
        syntaxItem = new JMenuItem("Toggle Syntax Highlighting");

        settingsMenu.add(lightModeItem);
        settingsMenu.add(darkModeItem);
        settingsMenu.add(syntaxItem);
        
        lightModeItem.addActionListener(e -> applyLightMode());
        darkModeItem.addActionListener(e -> applyDarkMode());

        // Help Menu
        JMenu helpMenu = new JMenu("Info");
        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(e -> showHelp());
        helpMenu.add(helpItem);
        
        // Add menus to bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(searchMenu);
        menuBar.add(runMenu);
        menuBar.add(settingsMenu);
        menuBar.add(helpMenu);
        
        frame.setJMenuBar(menuBar);
        
        // Menu actions
        exitItem.addActionListener(e -> System.exit(0));
        runItem.addActionListener(e -> onRunButtonClicked());
        compileItem.addActionListener(e -> onCompileOnlyClicked());
    }
    
    private void setupButtons() {
        // Add Run button to toolbar
        uiAddButton("Run", this::onRunButtonClicked);
        uiAddButton("Compile", this::onCompileOnlyClicked);
        uiAddButton("Clear Console", this::clearConsole1);
    }
    
    private void showHelp() {
        JOptionPane.showMessageDialog(
            frame,
            "Welcome to the Java Code Editor!\n\n"
          + "File:\n  Open and save Java files.\n\n"
          + "Edit:\n  Undo, redo, and clear text.\n\n"
          + "Search:\n  Find words and replace text.\n\n"
          + "Run:\n  Compile and run Java code.\n  Compile Only - Check for errors without running.\n\n"
          + "Requirements:\n  Must be run with a JDK (Java Development Kit)\n"
          + "  Class must have a public static void main(String[] args)\n\n"
          + "Brought to you by: Matthew Biegel, Robert Conti, \nMichael McGrath, Rui Li, Luke Padilla \n 2026",
          "Help",
          JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void styleMenuBar(Color menuBg, Color menuFg, Color itemBg, Color itemFg) {
        if (menuBar == null) return;

        menuBar.setBackground(menuBg);
        menuBar.setForeground(menuFg);
        menuBar.setOpaque(true);

        for (MenuElement element : menuBar.getSubElements()) {
            Component comp = element.getComponent();

            if (comp instanceof JMenu menu) {
                menu.setOpaque(true);
                menu.setBackground(menuBg);
                menu.setForeground(menuFg);

                for (Component itemComp : menu.getMenuComponents()) {
                    if (itemComp instanceof JMenuItem item) {
                        item.setOpaque(true);
                        item.setBackground(itemBg);
                        item.setForeground(itemFg);
                    }
                }
            }
        }
    }
    
    private void recolorExistingText(Color color) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, color);
        doc.setCharacterAttributes(0, doc.getLength(), attrs, false);
    }
    
    private void applyDarkMode() {
        Color bg = new Color(30, 30, 30);
        Color panelBg = new Color(45, 45, 45);
        Color textBg = new Color(35, 35, 35);
        Color fg = new Color(200, 200, 200);

        textPane.setBackground(textBg);
        textPane.setForeground(fg);
        recolorExistingText(fg);
        textPane.setCaretColor(Color.WHITE);
        textPane.setSelectionColor(new Color(80, 120, 180));
        textPane.setSelectedTextColor(Color.WHITE);

        buttonRow.setBackground(panelBg);
        consoleArea.setBackground(Color.BLACK);
        consoleArea.setForeground(Color.GREEN);

        styleMenuBar(
        	    new Color(45, 45, 45),   // top menu background
        	    Color.WHITE,             // menu text
        	    new Color(60, 60, 60),   // dropdown background
        	    Color.WHITE              // dropdown text
        	);
        
        frame.repaint();
    }
    
    private void applyLightMode() {
        Color bg = Color.WHITE;
        Color panelBg = new Color(240, 240, 240);
        Color textBg = Color.WHITE;
        Color fg = Color.BLACK;

        textPane.setBackground(textBg);
        textPane.setForeground(fg);
        recolorExistingText(fg);
        textPane.setCaretColor(Color.BLACK);
        textPane.setSelectionColor(new Color(180, 200, 240));
        textPane.setSelectedTextColor(Color.BLACK);

        buttonRow.setBackground(panelBg);
        consoleArea.setBackground(Color.WHITE);
        consoleArea.setForeground(Color.BLACK);

        styleMenuBar(
        	    new Color(240, 240, 240), // top menu background
        	    Color.BLACK,              // menu text
        	    Color.WHITE,              // dropdown background
        	    Color.BLACK               // dropdown text
        	);

        frame.repaint();
    }
    
    // Compiler integration methods
    private void onRunButtonClicked() {
    	textPane.getHighlighter().removeAllHighlights();
        String sourceCode = textPane.getText();
        if (sourceCode.trim().isEmpty()) {
            uiAlert("No code to run!");
            return;
        }
        
        String className = extractClassName(sourceCode);
        if (className == null) {
            uiAlert("Could not find public class name in the code.\n"
                  + "Make sure your code has: public class ClassName { ... }");
            return;
        }
        
        appendToConsole(">>> Compiling and running " + className + "...\n", Color.CYAN);
        statusBar.setText(" Compiling...");
        
        // Run in background thread
        new Thread(() -> {
            try {
                RunResult result = CompleteJavaRunner.compileAndRun(className, sourceCode, new String[0]);
                
                SwingUtilities.invokeLater(() -> {
                    if (result.isSuccess()) {
                        appendToConsole(result.getOutput(), Color.GREEN);
                        appendToConsole("\n>>> Execution completed successfully\n", Color.CYAN);
                        statusBar.setText(" Ready - Last run: SUCCESS");
                        tabbedPane.setSelectedIndex(1); // Switch to console tab
                    } else {
                        appendToConsole(">>> COMPILATION ERROR:\n", Color.RED);
                        appendToConsole(result.getError() + "\n", Color.RED);
                        statusBar.setText(" Ready - Last run: FAILED");
                        
                        // Highlight error lines in editor
                        highlightErrorLines(result.getError());
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    appendToConsole(">>> ERROR: " + e.getMessage() + "\n", Color.RED);
                    statusBar.setText(" Ready - Last run: ERROR");
                });
            }
        }).start();
    }
    
    private void onCompileOnlyClicked() {
    	textPane.getHighlighter().removeAllHighlights();
        String sourceCode = textPane.getText();
        if (sourceCode.trim().isEmpty()) {
            uiAlert("No code to compile!");
            return;
        }
        
        String className = extractClassName(sourceCode);
        if (className == null) {
            uiAlert("Could not find public class name in the code.");
            return;
        }
        
        appendToConsole(">>> Compiling " + className + "...\n", Color.CYAN);
        statusBar.setText(" Compiling...");
        
        new Thread(() -> {
            try {
                CompilationResult result = CompleteJavaRunner.compileOnly(className, sourceCode);
                
                SwingUtilities.invokeLater(() -> {
                    if (result.isSuccess()) {
                        appendToConsole(">>> Compilation successful!\n", Color.GREEN);
                        statusBar.setText(" Ready - Compilation SUCCESS");
                    } else {
                        appendToConsole(">>> COMPILATION ERRORS:\n", Color.RED);
                        for (CompilationError error : result.getErrors()) {
                            appendToConsole(String.format("  Line %d: %s\n", 
                                error.getLineNumber(), error.getMessage()), Color.RED);
                        }
                        statusBar.setText(" Ready - Compilation FAILED");
                        highlightErrorLines(result.getErrors());
                    }
                    tabbedPane.setSelectedIndex(1);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    appendToConsole(">>> ERROR: " + e.getMessage() + "\n", Color.RED);
                    statusBar.setText(" Ready - Compilation ERROR");
                });
            }
        }).start();
    }
    
    private String extractClassName(String sourceCode) {
        java.util.regex.Pattern pattern = 
            java.util.regex.Pattern.compile("public\\s+class\\s+(\\w+)");
        java.util.regex.Matcher matcher = pattern.matcher(sourceCode);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private void highlightErrorLines(String errorMessage) {
        
        java.util.regex.Pattern pattern = 
            java.util.regex.Pattern.compile("line\\s+(\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(errorMessage);
        
        while (matcher.find()) {
            int lineNumber = Integer.parseInt(matcher.group(1));
            highlightLine(lineNumber);
        }
    }
    
    private void highlightErrorLines(java.util.List<CompilationError> errors) {
   
        for (CompilationError error : errors) {
            if (error.getLineNumber() > 0) {
                highlightLine((int) error.getLineNumber());
            }
        }
    }
    
    private void highlightLine(int lineNumber) {
        try {
            Element root = textPane.getDocument().getDefaultRootElement();
            int start = root.getElement(lineNumber - 1).getStartOffset();
            int end = root.getElement(lineNumber - 1).getEndOffset();
            
            Highlighter.HighlightPainter painter = 
                new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 100, 100, 100));
            textPane.getHighlighter().addHighlight(start, end, painter);
        } catch (BadLocationException e) {
            // Ignore
        }
    }
    
    private void appendToConsole(String text, Color color) {
        SwingUtilities.invokeLater(() -> {
            if (consoleArea != null) {
                consoleArea.setForeground(color);
                consoleArea.append(text);
                consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
            }
        });
    }
    
    private void clearConsole1() {
        SwingUtilities.invokeLater(() -> {
            if (consoleArea != null) {
                consoleArea.setText("");
            }
        });
    }
    
    private void clearConsole() {
        consoleArea.setText("");
        appendToConsole(">>> Console cleared\n", Color.CYAN);
    }
    
    private void showCompilingStatus(String message) {
        statusBar.setText(" " + message);
    }
    
    private void showError(String error) {
        appendToConsole(">>> ERROR: " + error + "\n", Color.RED);
        JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showOutput(String output) {
        appendToConsole(output, Color.WHITE);
    }
    
    private void showStatus(String message) {
        statusBar.setText(" " + message);
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
    	return JOptionPane.showInputDialog(
    		    frame,
    		    message,
    		    "Input",
    		    JOptionPane.PLAIN_MESSAGE
    		);
    }

    @Override
    protected void uiHighlight(String term) {
        clearHighlights();

        if (term == null) return;
        term = term.trim();
        if (term.isEmpty()) return;

        Highlighter hl = textPane.getHighlighter();
        Highlighter.HighlightPainter painter =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 255, 0, 128));

        try {
            Document doc = textPane.getDocument();
            String text = doc.getText(0, doc.getLength());
            String hay = text.toLowerCase();
            String needle = term.toLowerCase();

            int index = 0;
            while ((index = hay.indexOf(needle, index)) >= 0) {
                hl.addHighlight(index, index + needle.length(), painter);
                index += needle.length();
            }
        } catch (BadLocationException ignored) {
        }

        textPane.repaint();
        textPane.requestFocusInWindow();
    }

    @Override
    protected void uiReplace(String target, String replacement) {
        if (target == null || target.isEmpty()) return;
        String current = textPane.getText();
        String updated = current.replace(target, replacement == null ? "" : replacement);
        textPane.setText(updated);
        clearHighlights();
    }

    private void clearHighlights() {
        textPane.getHighlighter().removeAllHighlights();
        textPane.repaint();
    }
    
    // Getters for menu items
    public JTextPane getTextPane() { return textPane; }
    public JMenuItem getNewItem() { return newItem; }
    public JMenuItem getOpenItem() { return openItem; }
    public JMenuItem getSaveItem() { return saveItem; }
    public JMenuItem getUndoItem() { return undoItem; }
    public JMenuItem getRedoItem() { return redoItem; }
    public JMenuItem getClearItem() { return clearItem; }
    public JMenuItem getFindItem() { return findItem; }
    public JMenuItem getReplaceItem() { return replaceItem; }
    public JMenuItem getHighlightItem() { return highlightItem; }
    public JMenuItem getSyntaxItem() { return syntaxItem; }
    
}
