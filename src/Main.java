import simpleUi.Editor;
import simpleUi.SimpleEditor;

import java.nio.file.Files;
import java.awt.*;
import java.io.File;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.*;

import arraylist.ArrayGenericList;

import arraylist.ListStack;

import arraylist.Stack;


import searchsort.SearchSort;

import genericlist.StepCounter;
import genericlist.GenericList;

import syntax.SyntaxHighlighterManager;
import syntax.JavaSyntaxHighlighterTextColor;

import compiler.CompleteJavaRunner;
import compiler.MemoryJavaFileManager;
import compiler.JavaCompilerAPI;
import compiler.RunResult;
import compiler.MemoryClassLoader;
import compiler.CompilationResult;
import compiler.CompilationError;
import compiler.InMemoryJavaFileObject;

import formatting.Formatting;
/*
 * Executes TextEditor program
 * Creates a Simple Text Editor with Save/Load functions, search functions
 * clear, undo, and redo, and Replace function. Can also load in demo text
 * @author Matthew Biegel, Michael McGrath, Robert Conti, Rui Li, Luke Padilla
 * @version 1.0.1
 * @since 2/25/26
 * */
public class Main {
    
    private static SyntaxHighlighterManager syntaxManager = null;

    public static void main(String[] args) {
    	
    	

    	SimpleEditor ui = new SimpleEditor("CS2 Text Editor");
    	
    	// Initialize syntax highlighting (ALWAYS ON)
    	syntaxManager = new SyntaxHighlighterManager(ui);
    	syntaxManager.enable();
        
    	Stack<String> undoStack =
    		    new ListStack<>(new ArrayGenericList<>(new StepCounter()));

    		Stack<String> redoStack =
    		    new ListStack<>(new ArrayGenericList<>(new StepCounter()));
        

    ui.getNewItem().addActionListener(e -> ui.setText(""));

    ui.getOpenItem().addActionListener(e -> {
    	 File desktop = new File(System.getProperty("user.home"), "Desktop");
         JFileChooser chooser = new JFileChooser(desktop);
         
        chooser.setDialogTitle("Open .txt file (or java tbd)");
        chooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);

        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt") ||
                       f.getName().toLowerCase().endsWith(".java");
            }
            @Override public String getDescription() {
                return "Text files (*.txt, *.java)";
            }
        });

        int result = chooser.showOpenDialog(null);
        if (result != javax.swing.JFileChooser.APPROVE_OPTION) return;

        java.io.File file = chooser.getSelectedFile();
        if (file == null) return;

        String oldText = ui.getText();
        undoStack.push(oldText);
        while (!redoStack.isEmpty()) redoStack.pop();

        try {
            String fileContent = java.nio.file.Files.readString(file.toPath());
            ui.setText(fileContent);
        } catch (Exception ex) {
            ui.alert("Error loading file: \n" + ex.getMessage());
            undoStack.pop();
        }
    });

    ui.getSaveItem().addActionListener(e -> {
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        JFileChooser chooser = new JFileChooser(desktop);

        chooser.setDialogTitle("Save .txt or .java file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileHidingEnabled(true);

        javax.swing.filechooser.FileNameExtensionFilter filter =
            new javax.swing.filechooser.FileNameExtensionFilter(
                "Text and Java files (*.txt, *.java)", "txt", "java");
        chooser.setFileFilter(filter);

        int result = chooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (file == null) return;

        String parent = file.getParent();
        String cleanName = sanitizeFileName(file.getName());

        if (!cleanName.toLowerCase().endsWith(".txt") &&
            !cleanName.toLowerCase().endsWith(".java")) {
            cleanName += ".txt";
        }

        file = (parent == null)
            ? new File(cleanName)
            : new File(parent, cleanName);

        if (file.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(
                null,
                "File already exists. Overwrite it?",
                "Confirm Overwrite",
                JOptionPane.YES_NO_OPTION
            );

            if (overwrite != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            java.nio.file.Files.writeString(file.toPath(), ui.getText());
            ui.alert("File saved:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            ui.alert("Error saving file:\n" + ex.getMessage());
        }
    });

    ui.getUndoItem().addActionListener(e -> {
        if (undoStack.isEmpty()) return;

        String current = ui.getText();
        redoStack.push(current);

        String prev = undoStack.pop();
        ui.setText(prev);
    });

    ui.getRedoItem().addActionListener(e -> {
        if (redoStack.isEmpty()) return;

        String current = ui.getText();
        undoStack.push(current);

        String restored = redoStack.pop();
        ui.setText(restored);
    });

    ui.getClearItem().addActionListener(e -> {
        String current = ui.getText();
        undoStack.push(current);
        while (!redoStack.isEmpty()) redoStack.pop();
        ui.setText("");
    });

    ui.getFindItem().addActionListener(e -> {
        String term = ui.prompt("Highlight what word/phrase?");
        if (term == null) return;

        term = term.trim();
        if (term.isEmpty()) {
            ui.alert("Nothing to highlight.");
            return;
        }

        final String searchTerm = term;

        if (syntaxManager != null && syntaxManager.isEnabled()) {
            syntaxManager.disable();
        }

        SwingUtilities.invokeLater(() -> ui.highlight(searchTerm));
    });
    
    ui.getReplaceItem().addActionListener(e -> {
        JTextField field1 = new JTextField(10);
        JTextField field2 = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Text to find:"));
        panel.add(field1);
        panel.add(new JLabel("Replace with:"));
        panel.add(field2);

        int result = JOptionPane.showConfirmDialog(
            ui.getTextPane(),
            panel,
            "Replace Text",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String find = field1.getText();
        String replace = field2.getText();

        if (find == null || find.isEmpty()) {
            ui.alert("Nothing to find.");
            return;
        }

        String oldText = ui.getText();

        // Save undo before change
        undoStack.push(oldText);

        // Clear redo stack
        while (!redoStack.isEmpty()) redoStack.pop();

        String newText = oldText.replace(find, replace == null ? "" : replace);
        ui.setText(newText);
    });
    
    ui.getformatItem().addActionListener(e -> {
    	String oldText = ui.getText();
    	undoStack.push(oldText);
    	while (!redoStack.isEmpty()) redoStack.pop();

    	ui.setText(Formatting.clean(oldText));
    });

    ui.getHighlightItem().addActionListener(e -> {
    	String term = ui.prompt("Highlight what word/phrase?");
    	if (term == null) return;

    	term = term.trim();
    	if (term.isEmpty()) {
    	    ui.alert("Nothing to highlight.");
    	    return;
    	}

    	final String searchTerm = term;

    	if (syntaxManager != null && syntaxManager.isEnabled()) {
    	    syntaxManager.disable();
    	}

    	SwingUtilities.invokeLater(() -> ui.highlight(searchTerm));
    });
    
    ui.show();
  }

    private static String sanitizeFileName(String name) {
        if (name == null) return "untitled";

        name = name.replaceAll("[\\\\/:*?\"<>|]", "_");
        name = name.trim().replaceAll("[. ]+$", "");

        return name.isEmpty() ? "untitled" : name;
    }
}
