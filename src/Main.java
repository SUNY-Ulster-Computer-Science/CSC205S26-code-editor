import simpleUi.Editor;
import simpleUi.SimpleEditor;

import java.nio.file.Files;
import java.awt.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.*;

import arraylist.ArrayGenericList;
import arraylist.ListQueue;
import arraylist.ListStack;
import arraylist.Queue;
import arraylist.Stack;
import bst.BST;

import searchsort.SearchSort;

import genericlist.StepCounter;
import genericlist.GenericList;
import linkedlist.LinkedGenericList;

import syntax.JavaSyntaxHighlighter;
import syntax.SyntaxHighlighterManager;
import syntax.JavaSyntaxHighlighterTextColor;

/*
 * Executes TextEditor program
 * Creates a Simple Text Editor with Save/Load functions, search functions
 * clear, undo, and redo, and Replace function. Can also load in demo text
 * @author Matthew Biegel, Michael McGrath, Robert Conti, Rui Li, Luke Padilla
 * @version 1.0.1
 * @since 2/25/26
 * */
public class Main {
    
    private static BST<String> wordTree = new BST<>();
    private static SyntaxHighlighterManager syntaxManager = null;

    public static void main(String[] args) {

    	SimpleEditor ui = new SimpleEditor("CS2 Text Editor");
        
        Stack<String> undoStack =
                new ListStack<String>(new ArrayGenericList<String>(new StepCounter()));

        Stack<String> redoStack =
                new ListStack<String>(new ArrayGenericList<String>(new StepCounter()));

        Queue<String> printQueue =
                new ListQueue<>(new ArrayGenericList<String>(new StepCounter()));

        Queue<Long> printTimes =
                new ListQueue<>(new ArrayGenericList<Long>(new StepCounter()));

        // ANALYZE BUTTON
        ui.addButton("Analyze", () -> {
            wordTree = new BST<>();
            StepCounter bstCounter = new StepCounter();

            String text = ui.getText();
            String[] words = text.split("\\s+");

            for (String w : words) {
                if (w == null || w.isEmpty()) continue;

                w = w.toLowerCase();
                w = w.replaceAll("^[^a-zA-Z]+", "");
                w = w.replaceAll("[^a-zA-Z]+$", "");
                if (w.isEmpty()) continue;

                wordTree.insert(w, bstCounter);
            }

            int bstSize = wordTree.size();
            int bstInsertSteps = bstCounter.get();

            ui.alert(
                "BST built successfully!\n\n" +
                "Unique words in BST: " + bstSize + "\n" +
                "BST insertion comparisons: " + bstInsertSteps
            );
        });
        
        // REPLACE BUTTON
        ui.addButton("Replace (Case sensitive)", () -> {
            String find = ui.prompt("Text to find:");
            String replace = ui.prompt("Replace with:");

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
            ui.highlight(term);
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

        // SEARCH - LinkedList
        ui.addButton("Search LinkedList", () -> {
            StepCounter counter = new StepCounter();
            LinkedGenericList<String> list = new LinkedGenericList<>(counter);

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

            target = target.toLowerCase();
            target = target.replaceAll("^[^a-zA-Z]+", "");
            target = target.replaceAll("[^a-zA-Z]+$", "");
            if (target.isEmpty()) {
                ui.alert("Search becomes empty after cleaning.");
                return;
            }

            counter.reset();
            int index = SearchSort.linearSearch(list, target, counter);

            ui.alert("LinkedList Search\nFound at: " + index +
                     "\nSteps: " + counter.get());
        });

        // SEARCH - BST
        ui.addButton("Search BST", () -> {
            if (wordTree == null || wordTree.isEmpty()) {
                ui.alert("BST is empty.\nRun Analyze first.");
                return;
            }

            String query = ui.prompt("Search word for BST:");
            if (query == null) return;

            query = query.toLowerCase();
            query = query.replaceAll("^[^a-zA-Z]+", "");
            query = query.replaceAll("[^a-zA-Z]+$", "");
            if (query.isEmpty()) {
                ui.alert("Search word becomes empty after cleaning.");
                return;
            }

            StepCounter arrayCounter = new StepCounter();
            StepCounter linkedCounter = new StepCounter();

            ArrayGenericList<String> arrayList = new ArrayGenericList<>(arrayCounter);
            LinkedGenericList<String> linkedList = new LinkedGenericList<>(linkedCounter);

            String[] textWords = ui.getText().split("\\s+");

            for (String w : textWords) {
                if (w == null) continue;

                w = w.toLowerCase();
                w = w.replaceAll("^[^a-zA-Z]+", "");
                w = w.replaceAll("[^a-zA-Z]+$", "");

                if (w.isEmpty()) continue;

                arrayList.add(w);
                linkedList.add(w);
            }

            arrayCounter.reset();
            int arrayIndex = SearchSort.linearSearch(arrayList, query, arrayCounter);

            linkedCounter.reset();
            int linkedIndex = SearchSort.linearSearch(linkedList, query, linkedCounter);

            StepCounter bstCounter = new StepCounter();
            boolean bstFound = wordTree.contains(query, bstCounter);

            ui.alert(
                "Searching for: \"" + query + "\"\n\n" +
                "ArrayList → index = " + arrayIndex +
                " , comparisons = " + arrayCounter.get() + "\n" +
                "LinkedList → index = " + linkedIndex +
                " , comparisons = " + linkedCounter.get() + "\n" +
                "BST → found = " + bstFound +
                " , comparisons = " + bstCounter.get()
            );
        });

        // LOAD FILE BUTTON
        ui.addButton("Load File", () -> {
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
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
            } catch (Exception e) {
                ui.alert("Error loading file: \n" + e.getMessage());
                undoStack.pop();
            }
        });
    
    ui.addButton("Save File", () -> {

        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Save .txt file");
        //set to files only
        chooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);

        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }
            @Override public String getDescription() {
                return "Text files (*.txt)";
            }
        });

        int result = chooser.showSaveDialog(null);
        if (result != javax.swing.JFileChooser.APPROVE_OPTION) return;

        java.io.File file = chooser.getSelectedFile();
        if (file == null) return;

        // Force .txt extension
        String path = file.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".txt")) {
            file = new java.io.File(path + ".txt");
            //normalize and ensure file is saved as .txt
        }

        try {
            java.nio.file.Files.writeString(file.toPath(), ui.getText());
            ui.alert("File saved: \n" + file.getAbsolutePath());
            //shows file directory
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

    ui.show();
  }

}
