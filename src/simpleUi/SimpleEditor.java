package simpleUi;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

//ui improvements needed, drop down menu

/*
 * Concrete implementation of the Editor interface, 
 * provides a basic text editor UI
 */
public final class SimpleEditor extends AbstractEditor {
/*represents new window display*/
  private final JFrame frame;
/*represents area for text*/  
  private final JTextArea area;
/*represents area for buttons*/
  private final JPanel buttonRow;
  
  
/*
 * Constructs simple Text Editor in a new window
 * @param title - text editor name
 * @returns none
 */
  public SimpleEditor(String title) {
    frame = new JFrame(title);
    area = new JTextArea();
    buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    init();
  }

  /*
   * Builds frame to display content in text editor
   * @param none
   * @return none
   */
   private void init() {
   area.setLineWrap(true);
    area.setWrapStyleWord(true);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1300, 800);

    JPanel content = new JPanel(new BorderLayout(5, 5));
    content.add(new JScrollPane(area), BorderLayout.CENTER);
    content.add(buttonRow, BorderLayout.SOUTH);

    frame.setContentPane(content);
    
    JScrollPane scrollPane = new JScrollPane(area);

    JTextArea lines = new JTextArea("1");
    lines.setBackground(Color.LIGHT_GRAY);
    lines.setEditable(false); //dont want this edited
    lines.setFocusable(false);
    lines.setFont(area.getFont()); // Match the editor font

    area.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        private String getNumbers() {
            int lineposition = area.getDocument().getLength();
            Element root = area.getDocument().getDefaultRootElement();
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

    JPanel content1 = new JPanel(new BorderLayout(10, 5));
    content1.add(scrollPane, BorderLayout.CENTER); // Use the scrollPane here
    content1.add(buttonRow, BorderLayout.SOUTH);

    frame.setContentPane(content1);
    
    //adding drop down menu
	   
	// 1. Create the Menu Bar
	    JMenuBar menuBar = new JMenuBar();

	    // 2. Create a Menu (e.g., File)
	    JMenu fileMenu = new JMenu("File");
	    JMenu editMenu = new JMenu("Edit");
        JMenu searchMenu = new JMenu("Search");
        JMenu helpMenu = new JMenu("Info");

	    // 3. Create Menu Items
	    JMenuItem newItem = new JMenuItem("New");
	    JMenuItem openItem = new JMenuItem("Open");
	    JMenuItem saveItem = new JMenuItem("Save");
	    JMenuItem exitItem = new JMenuItem("Exit");

	    // 4. Add items to the file menu
	    fileMenu.add(newItem);
	    fileMenu.add(saveItem);
	    fileMenu.add(openItem);
	    fileMenu.addSeparator(); // Adds a visual line
	    fileMenu.add(exitItem);
	    
	    editMenu.add(new JMenuItem("Undo"));
        editMenu.add(new JMenuItem("Redo"));
        editMenu.add(new JMenuItem("Clear"));
        
        searchMenu.add(new JMenuItem("Find"));
        searchMenu.add(new JMenuItem("Replace"));
        searchMenu.add(new JMenuItem("Highlight Matches"));


	    // 5. Add menu to the bar, and bar to the frame
	    menuBar.add(fileMenu);
	    menuBar.add(editMenu);
        menuBar.add(searchMenu);
        menuBar.add(helpMenu);
	    frame.setJMenuBar(menuBar);

	    
	    JMenuItem helpItem = new JMenuItem("Help");

	    helpItem.addActionListener(e -> {
	        JOptionPane.showMessageDialog(
	            frame,
	            "Welcome to the our Text Editor!\n\n"
	          + "File:\nOpen and save text and java files.\n\n"
	          + "Edit:\nUndo, redo, and clear text.\n\n"
	          + "Search:\nFind words and replace text.\n\n"
	          + "Use the buttons or menus to perform actions.\n\n"
	          + "Brought to you by: Matthew Biegel, Robert Conti, \nMichael McGrath, Rui Li, Luke Padilla \n 2026",
	          "Help",
	            JOptionPane.INFORMATION_MESSAGE
	        );
	    });

	    helpMenu.add(helpItem);
	    menuBar.add(helpMenu);
	    
	    // 6. Add logic to the items (optional but recommended)
	    exitItem.addActionListener(e -> System.exit(0));
	}
  
  @Override
  protected void uiShow() {
    SwingUtilities.invokeLater(() -> frame.setVisible(true));
  }

  @Override
  protected String uiGetText() { return area.getText(); }

  @Override
  protected void uiSetText(String text) { area.setText(text); }

  @Override
  protected void uiClearText() { area.setText(""); clearHighlights(); }

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

    Highlighter hl = area.getHighlighter();
    Highlighter.HighlightPainter painter =
        new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 255, 0, 128));

    String text = area.getText();
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
    String current = area.getText();
    String updated = current.replace(target, replacement == null ? "" : replacement);
    area.setText(updated);
    clearHighlights();
  }

  /*
   * Removes highlights from display
   * @param none
   * @return none 
   */
  private void clearHighlights() {
    area.getHighlighter().removeAllHighlights();
  }
}
