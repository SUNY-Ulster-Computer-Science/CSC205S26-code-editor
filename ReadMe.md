# Text Editor UI Documentation

---

### UML Diagram 

```plaintext
+--------------------------------------------------------+
| «interface» Editor                                     |
+--------------------------------------------------------+
| + show(): void                                         |
| + getText(): String                                    |
| + setText(text: String): void                          |
| + clearText(): void                                    |
| + highlight(term: String): void                        |
| + replace(target: String, replacement: String): void   |
| + addButton(label: String, action: Runnable): void     |
| + alert(message: String): void                         |
| + prompt(message: String): String                      |
+--------------------------------------------------------+

          ^ implements
          |
+-----------------------------------------------+
| AbstractEditor                                |
+-----------------------------------------------+
| «abstract»                                    |
| + show(): final void                          |
| + getText(): final String                     |
| + setText(text: String): final void           |
| + clearText(): final void                     |
| + addButton(label, action): final void        |
| + alert(message): final void                  |
| + prompt(message): final String               |
| + highlight(term): final void                 |
| + replace(target, replacement): final void    |
+-----------------------------------------------+
| # uiShow(): abstract                          |
| # uiGetText(): abstract                       |
| # uiSetText(text): abstract                   |
| # uiClearText(): abstract                     |
| # uiAddButton(label, action): abstract        |
| # uiAlert(message): abstract                  |
| # uiPrompt(message): abstract                 |
| # uiHighlight(term): abstract                 |
| # uiReplace(target, replacement): abstract    |
+-----------------------------------------------+

              ^ extends
              |
+-----------------------------------------------+
| SimpleEditor                                  |
+-----------------------------------------------+
| - frame: JFrame                               |
| - area: JTextArea                             |
| - buttonRow: JPanel                           |
+-----------------------------------------------+
| + SimpleEditor(title: String)                 |
| + uiShow(): void                              |
| + uiGetText(): String                         |
| + uiSetText(text: String): void               |
| + uiClearText(): void                         |
| + uiAddButton(label, action): void            |
| + uiAlert(message): void                      |
| + uiPrompt(message): String                   |
| + uiHighlight(term): void                     |
| + uiReplace(target, replacement): void        |
+-----------------------------------------------+
```

---

### Methods

<small>

#### `void show()`
- **Description:** Displays the editor window.  
- **Parameters:** None  
- **Returns:** Nothing  

#### `String getText()`
- **Description:** Gets the current text from the editor.  
- **Parameters:** None  
- **Returns:** The editor’s text as a `String`  

#### `void setText(String text)`
- **Description:** Sets the editor’s text.  
- **Parameters:**  
  - `text`: String to set as the editor’s text  
- **Returns:** Nothing  

#### `void clearText()`
- **Description:** Clears all text from the editor.  
- **Parameters:** None  
- **Returns:** Nothing  

#### `void addButton(String label, Runnable action)`
- **Description:** Adds a button to the editor.  
- **Parameters:**  
  - `label`: Button text (`String`)  
  - `action`: Code to run when button is clicked (`Runnable`)  
- **Returns:** Nothing  

#### `void alert(String message)`
- **Description:** Shows a message dialog.  
- **Parameters:**  
  - `message`: Message to display (`String`)  
- **Returns:** Nothing  

#### `String prompt(String message)`
- **Description:** Prompts the user for input.  
- **Parameters:**  
  - `message`: Prompt message (`String`)  
- **Returns:** User input as a `String`  

#### `void highlight(String term)`
- **Description:** Highlights all matches of the term.  
- **Parameters:**  
  - `term`: Term to highlight (`String`)  
- **Returns:** Nothing  

#### `void replace(String target, String replacement)`
- **Description:** Replaces all occurrences of `target` with `replacement`.  
- **Parameters:**  
  - `target`: Text to replace (`String`)  
  - `replacement`: Replacement text (`String`)  
- **Returns:** Nothing  

#### `Text` Utility Class
- **Description:** Provides utility methods for text manipulation.  
- **Methods:**  
  - `String[] words(String text)`  
    - **Parameters:**  
      - `text`: Input text (`String`)  
    - **Returns:** Array of words (`String[]`)  

</small>

---

### Overview

The `Editor` interface provides a simple text editor UI with various functionalities such as displaying text, handling user input, and performing text operations. You will use this interface to create a text editor application. You do not need to modify this interface.

The `AbstractEditor` class provides a partial implementation of the `Editor` interface, handling common functionalities and leaving specific implementations to subclasses. You do not need to modify this abstract class.

The `SimpleEditor` class is a concrete implementation of the `Editor` interface, providing a basic text editor UI. You do not need to modify this concrete class.

Use the `Editor` interface to interact with the text editor UI in your application.

The `Main.java` file is where you will implement your text editor application using the `Editor` interface. An `Editor` object named `ui` has been created for you. Although its declared type is `Editor`, it is actually an instance of `SimpleEditor`. Remember that the declared type determines which methods are accessible, while the actual type determines which implementation runs at runtime. In this case, `Editor` is the supertype (interface) of `SimpleEditor`, so you can call any method defined in the `Editor` interface on the `ui` object.


Example usage:

```java
ui.show(); // Display the editor window
ui.setText("Hello, World!"); // Set initial text
String currentText = ui.getText(); // Get current text
ui.alert("Current text: " + currentText); // Show an alert with the current text
String s = ui.prompt("Enter some input:"); // Prompt the user for input and store the response
ui.highlight("World"); // Highlight the word "World"
ui.replace("World", "Java"); // Replace "World" with "Java"
ui.clearText(); // Clear all text
```

We will go over the structure of the `Editor` interface and its methods in class.

---

### Compile the Program
To compile the program, navigate to the `TextEditor` directory in your terminal and run the following command:

```bash
javac -d bin src/*.java src/simpleUi/*.java
```

javac is the Java compiler, `-d bin` specifies the output directory for compiled classes, and `src/*.java src/simpleUi/*.java` includes all Java source files in the `src` and `src/simpleUi` directories.

When you add your data structure classes (e.g., `ArrayList`, `LinkedList`, `BST`), include them in the compile command as well.

### Run the Program

To run the program, use the following command:

```bash
java -cp bin Main
```