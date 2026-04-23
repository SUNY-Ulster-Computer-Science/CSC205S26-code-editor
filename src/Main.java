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
        
        // REPLACE BUTTON
        ui.addButton("Replace (Case sensitive)", () -> {
            String find = ui.prompt("Text to find:");
            if (find == null) return;
            String replace = ui.prompt("Replace with:");
            if (replace == null) return;

            String oldText = ui.getText();

            // Save undo before change
            undoStack.push(oldText);

            // Clear redo stack
            while (!redoStack.isEmpty()) redoStack.pop();

            String newText = oldText.replace(find, replace);
            ui.setText(newText);
        });

        // CLEAR BUTTON
        ui.addButton("Clear", () -> {
            String current = ui.getText();

            // Save undo
            undoStack.push(current);

            // Clear redo stack
            while (!redoStack.isEmpty()) redoStack.pop();

            // Clear editor
            ui.setText("");
        });

        // UNDO BUTTON
        ui.addButton("Undo", () -> {
            if (undoStack.isEmpty()) return;

            String current = ui.getText();

            // Move current to redo
            redoStack.push(current);

            // Restore last state
            String prev = undoStack.pop();
            ui.setText(prev);
        });

        // REDO BUTTON
        ui.addButton("Redo", () -> {
            if (redoStack.isEmpty()) return;

            String current = ui.getText();

            // Save current to undo
            undoStack.push(current);

            // Apply redo
            String restored = redoStack.pop();
            ui.setText(restored);
        });

        // SEARCH AND HIGHLIGHT BUTTON
        ui.addButton("Search and Highlight", () -> {
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

        // SEARCH - ArrayList
        ui.addButton("Search ArrayList", () -> {
            StepCounter counter = new StepCounter();
            ArrayGenericList<String> list = new ArrayGenericList<>(counter);

            String[] words = ui.getText().split("\\s+");
            for (String w : words) {
                if (w == null) continue;
                w = w.toLowerCase();
                w = w.replaceAll("^[^a-zA-Z]+", "");
                w = w.replaceAll("[^a-zA-Z]+$", "");
                if (w.isEmpty()) continue;
                list.add(w);
            }

            String target = ui.prompt("Search for:");
            if (target == null) return;

            counter.reset();
            int index = SearchSort.linearSearch(list, target, counter);

            ui.alert("ArrayList Search\nFound at: " + index +
                     "\nSteps: " + counter.get());
        });

        

        // LOAD FILE BUTTON
        ui.addButton("Load File", () -> {
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
            chooser.setDialogTitle("Open .txt or .java file");
            chooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            javax.swing.filechooser.FileNameExtensionFilter filter =
            	    new javax.swing.filechooser.FileNameExtensionFilter(
            	        "Text and Java files (*.txt, *.java)", "txt", "java");
            	chooser.setFileFilter(filter);

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
            } catch (Exception e) {
                ui.alert("Error loading file:\n" + e.getMessage());
                undoStack.pop();
            }
        });
        
        
    
        ui.addButton("Save File", () -> {
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
            } catch (Exception e) {
                ui.alert("Error saving file:\n" + e.getMessage());
            }
        });
    
    

    // Load demo text for testing.
    ui.addButton("Load Demo", () -> ui.setText(
        "If you’re going to try, go all the way.\n" +
            "Otherwise, don’t even start.\n" +
            "If you're going to try, go all the way.\n" +
            "This could mean losing girlfriends, wives, relatives, jobs and maybe even your mind.\n" +
            "It could mean not eating for three or four days.\n" +
            "It could mean freezing on a park bench.\n" +
            "It could mean jail.\n" +
            "It could mean derision, mockery, isolation.\n" +
            "Isolation is the gift.\n" +
            "All the others are a test of your endurance, of how much you really want to do it.\n" +
            "And, you’ll do it, despite rejection and the worst odds.\n" +
            "And it will be better than anything else you can imagine.\n" +
            "If you’re going to try, go all the way.\n" +
            "There is no other feeling like that.\n\n" +
            "You will be alone with the gods, and the nights will flame with fire.\n" +
            "DO IT. DO IT. DO IT. All the way\n" +
            "You will ride life straight to perfect laughter. It’s the only good fight there is.\n\n" +
            "– Charles Bukowski"

    ));

    // Load demo text for testing.
    ui.addButton("Clean Formatting", () -> ui.setText(Formatting.cleanLine(ui.getText())

    ));

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

    	String newText = String.join("\n",
    	        Formatting.clean(oldText.split("\n")));

    	ui.setText(newText);
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
