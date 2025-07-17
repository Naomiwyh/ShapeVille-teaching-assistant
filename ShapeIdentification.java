import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.border.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

/**
 * A shape identification game panel that supports both 2D and 3D shape recognition.
 * This class provides an interactive educational game where users can identify various geometric shapes.
 * The game tracks scores, completion status, and provides visual feedback for correct and incorrect answers.
 * 
 * @author Honglu Xu
 * @version 1.0
 */
public class ShapeIdentification extends JPanel {
    
    /** Flag indicating if the 2D task has been completed */
    private static boolean task2DCompleted = false;
    
    /** Flag indicating if the 3D task has been completed */
    private static boolean task3DCompleted = false;
    
    /** Current score for 2D shapes identification */
    private static int score2D = 0;
    
    /** Current score for 3D shapes identification */
    private static int score3D = 0;
    
    /** Current shape index for 2D mode */
    private static int currentShapeIndex2D = 0;
    
    /** Current shape index for 3D mode */
    private static int currentShapeIndex3D = 0;
    
    /** Array tracking completion status of each 2D shape (11 shapes total) */
    private static boolean[] shapesCompleted2D = new boolean[11];
    
    /** Array tracking completion status of each 3D shape (8 shapes total) */
    private static boolean[] shapesCompleted3D = new boolean[8];
    
    /** Currently selected shapes for 2D mode */
    private static java.util.List<String> currentSelectedShapes2D = null;
    
    /** Currently selected shapes for 3D mode */
    private static java.util.List<String> currentSelectedShapes3D = null;
    
    /** Button for 2D task selection */
    private static JButton button2D;
    
    /** Button for 3D task selection */
    private static JButton button3D;
    
    /** Reference to the parent dialog */
    private JDialog parentDialog;
    
    /** Reference to the main application */
    private static MainApp mainApp;
    
    /** Label displaying the current question */
    private JLabel questionLabel;
    
    /** Text field for user input */
    private JTextField answerInput;
    
    /** Label showing the result of the answer */
    private JLabel resultLabel;
    
    /** Label displaying the current score */
    private JLabel scoreLabel;
    
    /** Current score in the active game session */
    private int currentScore = 0;
    
    /** Flag indicating if the game is in 3D mode */
    private boolean is3DMode = false;
    
    /** List of all available shapes for the current mode */
    private java.util.List<String> allShapes;
    
    /** List of shapes selected for the current game session */
    private java.util.List<String> selectedShapes;
    
    /** Index of the current shape being displayed */
    private int currentShapeIndex = 0;
    
    /** Number of attempts made for the current shape */
    private int currentAttempts = 0;
    
    /** Name of the current shape being displayed */
    private String currentShape;
    
    /** Panel responsible for drawing shapes */
    private ShapePanel shapePanel;
    
    /** Title label at the top of the panel */
    private JLabel titleLabel;
    
    /**
     * Color mapping for 2D shapes.
     * Each shape is associated with a specific color for visual consistency.
     */
    private static final Map<String, Color> SHAPE_COLORS = new HashMap<String, Color>() {{
        put("circle", Color.RED);
        put("rectangle", Color.YELLOW);
        put("triangle", Color.GREEN);
        put("oval", new Color(128, 0, 128)); // Purple
        put("octagon", Color.ORANGE);
        put("square", Color.BLUE);
        put("heptagon", Color.PINK);
        put("rhombus", Color.CYAN);
        put("pentagon", new Color(139, 69, 19)); // Brown
        put("hexagon", Color.GRAY);
        put("kite", new Color(255, 20, 147)); // Deep Pink
    }};

    /**
     * Color mapping for 3D shapes.
     * Each 3D shape is associated with a specific color for visual consistency.
     */
    private static final Map<String, Color> SHAPE_COLORS_3D = new HashMap<String, Color>() {{
        put("cube", Color.GREEN);
        put("cuboid", Color.BLUE);
        put("cylinder", Color.RED);
        put("sphere", Color.ORANGE);
        put("triangular prism", Color.YELLOW);
        put("square-based pyramid", Color.PINK);
        put("cone", Color.CYAN);
        put("tetrahedron", new Color(128, 0, 128)); // Purple
    }};
    
    /** Soft background color for better visual appeal */
    private static final Color BACKGROUND_COLOR = new Color(230, 240, 255);
    
    /** Standard button color */
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    
    /** Button text color */
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    
    /** Main text color */
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    
    /** Input field background color */
    private static final Color INPUT_BACKGROUND = Color.WHITE;
    
    /** Border color for UI elements */
    private static final Color BORDER_COLOR = new Color(200, 200, 200);

    /**
     * Constructs a new ShapeIdentification panel.
     * 
     * @param parentDialog The parent dialog that contains this panel
     * @param is3DMode True if the game should operate in 3D mode, false for 2D mode
     * @param mainApp Reference to the main application for progress tracking
     */
    public ShapeIdentification(JDialog parentDialog, boolean is3DMode, MainApp mainApp) {
        this.parentDialog = parentDialog;
        this.is3DMode = is3DMode;
        ShapeIdentification.mainApp = mainApp;
        initializeShapes();
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create panels
        JPanel topPanel = createTopPanel();
        shapePanel = new ShapePanel();
        JPanel questionPanel = createQuestionPanel();
        JPanel inputPanel = createInputPanel();
        JPanel resultPanel = createResultPanel();

        // Add all panels
        add(topPanel, BorderLayout.NORTH);
        add(shapePanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBackground(BACKGROUND_COLOR);
        southPanel.add(questionPanel);
        southPanel.add(inputPanel);
        southPanel.add(resultPanel);
        
        add(southPanel, BorderLayout.SOUTH);

        // Restore previous game state
        if (is3DMode) {
            currentScore = score3D;
            currentShapeIndex = currentShapeIndex3D;
        } else {
            currentScore = score2D;
            currentShapeIndex = currentShapeIndex2D;
        }
        
        // If no previous game was started, start a new game
        if (currentShapeIndex == 0) {
            startNewGame();
        } else {
            // Continue previous game
            showNextShape();
        }
        
        updateScoreLabel();
    }

    /**
     * Initializes the shapes list based on the current game mode (2D or 3D).
     * Handles shape selection and completion tracking.
     */
    private void initializeShapes() {
        if (!is3DMode) {
            // Initialize 2D shapes
            allShapes = Arrays.asList("circle", "rectangle", "triangle", "oval", "octagon", "square", "heptagon", "rhombus", "pentagon", "hexagon", "kite");
            
            // If starting a new game (currentShapeIndex is 0), randomly select 4 uncompleted shapes
            if (currentShapeIndex == 0) {
                java.util.List<String> tempShapes = new ArrayList<>();
                // Only add uncompleted shapes
                for (int i = 0; i < allShapes.size(); i++) {
                    if (!shapesCompleted2D[i]) {
                        tempShapes.add(allShapes.get(i));
                    }
                }
                
                // If fewer than 4 uncompleted shapes remain, reset all completion status
                if (tempShapes.size() < 4) {
                    Arrays.fill(shapesCompleted2D, false);
                    tempShapes = new ArrayList<>(allShapes);
                }
                
                Collections.shuffle(tempShapes);
                selectedShapes = tempShapes.subList(0, Math.min(4, tempShapes.size()));
                // Save current shape selection order
                currentSelectedShapes2D = new ArrayList<>(selectedShapes);
            } else {
                // If not a new game, use previously saved shape order
                selectedShapes = new ArrayList<>(currentSelectedShapes2D);
            }
        } else {
            // Initialize 3D shapes
            allShapes = Arrays.asList("cube", "cuboid", "cylinder", "sphere", "triangular prism", "square-based pyramid", "cone", "tetrahedron");
            
            // If starting a new game (currentShapeIndex is 0), randomly select 4 uncompleted shapes
            if (currentShapeIndex == 0) {
                java.util.List<String> tempShapes = new ArrayList<>();
                // Only add uncompleted shapes
                for (int i = 0; i < allShapes.size(); i++) {
                    if (!shapesCompleted3D[i]) {
                        tempShapes.add(allShapes.get(i));
                    }
                }
                
                // If fewer than 4 uncompleted shapes remain, reset all completion status
                if (tempShapes.size() < 4) {
                    Arrays.fill(shapesCompleted3D, false);
                    tempShapes = new ArrayList<>(allShapes);
                }
                
                Collections.shuffle(tempShapes);
                selectedShapes = tempShapes.subList(0, Math.min(4, tempShapes.size()));
                // Save current shape selection order
                currentSelectedShapes3D = new ArrayList<>(selectedShapes);
            } else {
                // If not a new game, use previously saved shape order
                selectedShapes = new ArrayList<>(currentSelectedShapes3D);
            }
        }
    }

    /**
     * Starts a new game session by resetting game state and initializing shapes.
     */
    private void startNewGame() {
        currentShapeIndex = 0;
        currentAttempts = 0;
        currentScore = 0;
        
        // Check if completion status needs to be reset
        boolean needReset2D = true;
        boolean needReset3D = true;
        
        // Check if all 2D shapes are completed
        for (boolean completed : shapesCompleted2D) {
            if (!completed) {
                needReset2D = false;
                break;
            }
        }
        
        // Check if all 3D shapes are completed
        for (boolean completed : shapesCompleted3D) {
            if (!completed) {
                needReset3D = false;
                break;
            }
        }
        
        // Reset completion status if all shapes are completed
        if (needReset2D) {
            Arrays.fill(shapesCompleted2D, false);
        }
        if (needReset3D) {
            Arrays.fill(shapesCompleted3D, false);
        }
        
        // Reset saved shape order
        if (!is3DMode) {
            currentSelectedShapes2D = null;
        } else {
            currentSelectedShapes3D = null;
        }
        
        updateTitle();
        showNextShape();
    }

    /**
     * Updates the title label to reflect the current game mode.
     */
    private void updateTitle() {
        if (titleLabel != null) {
            titleLabel.setText("Shape Identification (" + (is3DMode ? "3D" : "2D") + ")");
            titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        }
    }

    /**
     * Displays the next shape in the sequence or completes the game if all shapes are done.
     */
    private void showNextShape() {
        if (currentShapeIndex < selectedShapes.size()) {
            currentShape = selectedShapes.get(currentShapeIndex);
            currentAttempts = 0;
            questionLabel.setText("Please enter the name of the shape:");
            shapePanel.setCurrentShape(currentShape);
            shapePanel.repaint();
            resultLabel.setText("Please enter your answer");
            resultLabel.setForeground(TEXT_COLOR);
            answerInput.setText("");
            answerInput.requestFocus(); // Focus on the input field
        } else {
            // Game completed
            if (!is3DMode) {
                score2D = currentScore;
                task2DCompleted = true;
                showGameCompletionDialog();
            } else {
                score3D = currentScore;
                task3DCompleted = true;
                showGameCompletionDialog();
            }
        }
    }

    /**
     * Checks the user's answer against the correct shape name and provides feedback.
     * Handles scoring, attempt tracking, and progression to the next shape.
     */
    private void checkAnswer() {
        String answer = answerInput.getText().trim().toLowerCase();
        
        // Check for empty input
        if (answer.isEmpty()) {
            resultLabel.setText("Please enter a valid answer");
            resultLabel.setForeground(new Color(220, 20, 60)); // Red color
            return;
        }
        
        // Check if input contains numbers or special characters
        if (!answer.matches("^[a-zA-Z\\s]+$")) {
            resultLabel.setText("Please enter a valid shape name (letters only)");
            resultLabel.setForeground(new Color(220, 20, 60)); // Red color
            return;
        }
        
        if (answer.equals(currentShape)) {
            // Correct answer handling
            int shapeIndex = allShapes.indexOf(currentShape);
            if (shapeIndex != -1) {
                if (!is3DMode) {
                    shapesCompleted2D[shapeIndex] = true;
                } else {
                    shapesCompleted3D[shapeIndex] = true;
                }
            }
            
            int points = 0;
            String feedback = "";
            
            // Calculate points based on attempts and mode
            if (!is3DMode) {
                if (currentAttempts == 0) {
                    points = 3;
                    feedback = "Excellent!";
                } else if (currentAttempts == 1) {
                    points = 2;
                    feedback = "Very Good!";
                } else if (currentAttempts == 2) {
                    points = 1;
                    feedback = "Good!";
                }
            } else {
                if (currentAttempts == 0) {
                    points = 6;
                    feedback = "Excellent!";
                } else if (currentAttempts == 1) {
                    points = 4;
                    feedback = "Very Good!";
                } else if (currentAttempts == 2) {
                    points = 2;
                    feedback = "Good!";
                }
            }
            
            currentScore += points;
            scoreLabel.setText("Score: " + currentScore);
            scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
            resultLabel.setText(feedback + " +" + points + " points");
            resultLabel.setForeground(new Color(46, 139, 87));
            currentShapeIndex++;
            answerInput.setText("");
            
            if (currentShapeIndex >= selectedShapes.size()) {
                // Game completed
                showGameCompletionDialog();
                return;
            }
            
            // Show next shape after 1 second delay
            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> showNextShape());
            timer.setRepeats(false);
            timer.start();
        } else {
            // Incorrect answer handling
            currentAttempts++;
            if (currentAttempts >= 3) {
                // Maximum attempts reached
                resultLabel.setText("Correct answer is: " + currentShape);
                resultLabel.setForeground(new Color(220, 20, 60));
                currentShapeIndex++;
                answerInput.setText("");
                
                if (currentShapeIndex >= selectedShapes.size()) {
                    // Game completed
                    showGameCompletionDialog();
                    return;
                }
                
                // Show next shape after 2 second delay
                javax.swing.Timer timer = new javax.swing.Timer(2000, e -> showNextShape());
                timer.setRepeats(false);
                timer.start();
            } else {
                // Allow another attempt
                resultLabel.setText("Wrong answer, please try again");
                resultLabel.setForeground(new Color(220, 20, 60));
                answerInput.setText("");
            }
        }
    }

    /**
     * Creates the top panel containing the home button, title, and score.
     * 
     * @return The configured top panel
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        // Create home button
        JButton homeButton = createHomeButton();
        panel.add(homeButton, BorderLayout.WEST);
        
        titleLabel = new JLabel("Shape Identification (" + (is3DMode ? "3D" : "2D") + ")", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        
        scoreLabel = new JLabel("Score: 0", SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        scoreLabel.setForeground(TEXT_COLOR);
        
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(scoreLabel, BorderLayout.EAST);
        
        return panel;
    }

    /**
     * Creates the home button with appropriate styling and functionality.
     * 
     * @return The configured home button
     */
    private JButton createHomeButton() {
        JButton homeButton = new JButton("Home");
        homeButton.setPreferredSize(new Dimension(90, 40));
        homeButton.setBackground(new Color(70, 130, 180)); // Match blue from other modules
        homeButton.setForeground(Color.WHITE);
        homeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        homeButton.setBorderPainted(false);
        homeButton.setFocusPainted(false);
        
        homeButton.addActionListener(e -> {
            // Save current state
            if (is3DMode) {
                score3D = currentScore;
                currentShapeIndex3D = currentShapeIndex;
                task3DCompleted = currentShapeIndex >= selectedShapes.size();
            } else {
                score2D = currentScore;
                currentShapeIndex2D = currentShapeIndex;
                task2DCompleted = currentShapeIndex >= selectedShapes.size();
            }
            
            // Update main interface score
            if (parentDialog != null && parentDialog.getOwner() instanceof JFrame) {
                JFrame mainFrame = (JFrame) parentDialog.getOwner();
                for (Component comp : mainFrame.getContentPane().getComponents()) {
                    if (comp instanceof JPanel) {
                        for (Component panelComp : ((JPanel) comp).getComponents()) {
                            if (panelComp instanceof JLabel && ((JLabel) panelComp).getText().startsWith("Current score: ")) {
                                String currentScoreText = ((JLabel) panelComp).getText();
                                int mainFrameScore = Integer.parseInt(currentScoreText.replaceAll("[^0-9]", ""));
                                int newTotalScore = mainFrameScore + currentScore;
                                ((JLabel) panelComp).setText("Current score: " + newTotalScore);
                                break;
                            }
                        }
                    }
                }
            }
            
            // Close all dialogs and return to main interface
            if (parentDialog != null) {
                parentDialog.dispose();
            }
        });
        
        return homeButton;
    }

    /**
     * Creates the question panel containing the question label.
     * 
     * @return The configured question panel
     */
    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        questionLabel.setForeground(TEXT_COLOR);
        panel.add(questionLabel);

        return panel;
    }

    /**
     * Creates the input panel containing the answer input field and submit button.
     * 
     * @return The configured input panel
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JLabel inputLabel = new JLabel("Enter answer:");
        inputLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        inputLabel.setForeground(TEXT_COLOR);

        answerInput = new JTextField(15);
        answerInput.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        answerInput.setBackground(INPUT_BACKGROUND);
        answerInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Add enter key listener
        answerInput.addActionListener(e -> checkAnswer());

        JButton submitButton = createStyledButton("Submit");
        submitButton.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        submitButton.setPreferredSize(new Dimension(100, 35));
        submitButton.addActionListener(e -> checkAnswer());

        panel.add(inputLabel);
        panel.add(answerInput);
        panel.add(submitButton);

        return panel;
    }

    /**
     * Creates the result panel containing the result label.
     * 
     * @return The configured result panel
     */
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        resultLabel = new JLabel("Please enter your answer");
        resultLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        resultLabel.setForeground(TEXT_COLOR);
        panel.add(resultLabel);

        return panel;
    }

    /**
     * Creates a styled button with consistent appearance and hover effects.
     * 
     * @param text The text to display on the button
     * @return The configured styled button
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 35));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
    }

    /**
     * Inner class responsible for drawing shapes on the game panel.
     * Supports both 2D and 3D shape rendering with appropriate colors and styling.
     */
    private class ShapePanel extends JPanel {
        /** The current shape to be displayed */
        private String currentShape;
        
        /**
         * Constructs a new ShapePanel with default settings.
         */
        public ShapePanel() {
            setPreferredSize(new Dimension(300, 200));
            setBackground(BACKGROUND_COLOR);
        }
        
        /**
         * Sets the current shape to be drawn.
         * 
         * @param shape The name of the shape to display
         */
        public void setCurrentShape(String shape) {
            this.currentShape = shape;
        }
        
        /**
         * Paints the current shape on the panel.
         * 
         * @param g The Graphics object for drawing
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (currentShape == null) return;
            
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int size = Math.min(getWidth(), getHeight()) / 2;
            
            if (!is3DMode) {
                g2d.setColor(SHAPE_COLORS.get(currentShape));
                draw2DShape(g2d, centerX, centerY, size);
            } else {
                // Draw filled shape
                g2d.setColor(SHAPE_COLORS_3D.get(currentShape));
                draw3DShape(g2d, centerX, centerY, size, true);
                
                // Draw black border
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                draw3DShape(g2d, centerX, centerY, size, false);
            }
        }

        /**
         * Draws a 2D shape at the specified location.
         * 
         * @param g2d The Graphics2D object for drawing
         * @param centerX The center X coordinate
         * @param centerY The center Y coordinate
         * @param size The size of the shape
         */
        private void draw2DShape(Graphics2D g2d, int centerX, int centerY, int size) {
            switch(currentShape) {
                case "circle":
                    g2d.fillOval(centerX - size/2, centerY - size/2, size, size);
                    break;
                case "rectangle":
                    g2d.fillRect(centerX - size/2, centerY - size/3, size, size*2/3);
                    break;
                case "triangle":
                    int[] triangleX = {centerX, centerX - size/2, centerX + size/2};
                    int[] triangleY = {centerY - size/2, centerY + size/2, centerY + size/2};
                    g2d.fillPolygon(triangleX, triangleY, 3);
                    break;
                case "oval":
                    g2d.fillOval(centerX - size/2, centerY - size/3, size, size*2/3);
                    break;
                case "octagon":
                    fillRegularPolygon(g2d, centerX, centerY, size/2, 8);
                    break;
                case "square":
                    g2d.fillRect(centerX - size/2, centerY - size/2, size, size);
                    break;
                case "heptagon":
                    fillRegularPolygon(g2d, centerX, centerY, size/2, 7);
                    break;
                case "rhombus":
                    int[] rhombusX = {centerX, centerX + size/3, centerX, centerX - size/3};
                    int[] rhombusY = {centerY - size/2, centerY, centerY + size/2, centerY};
                    g2d.fillPolygon(rhombusX, rhombusY, 4);
                    break;
                case "pentagon":
                    fillRegularPolygon(g2d, centerX, centerY, size/2, 5);
                    break;
                case "hexagon":
                    fillRegularPolygon(g2d, centerX, centerY, size/2, 6);
                    break;
                case "kite":
                    int[] kiteX = {centerX, centerX + size/3, centerX, centerX - size/3};
                    int[] kiteY = {centerY - size/2, centerY, centerY + size/3, centerY};
                    g2d.fillPolygon(kiteX, kiteY, 4);
                    break;
            }
        }

        /**
         * Draws a 3D shape at the specified location.
         * 
         * @param g2d The Graphics2D object for drawing
         * @param centerX The center X coordinate
         * @param centerY The center Y coordinate
         * @param size The size of the shape
         * @param fill Whether to fill the shape with color
         */
        private void draw3DShape(Graphics2D g2d, int centerX, int centerY, int size, boolean fill) {
            switch(currentShape) {
                case "cube":
                    drawCube(g2d, centerX, centerY, size, fill);
                    break;
                case "cuboid":
                    drawCuboid(g2d, centerX, centerY, size, fill);
                    break;
                case "cylinder":
                    drawCylinder(g2d, centerX, centerY, size, fill);
                    break;
                case "sphere":
                    drawSphere(g2d, centerX, centerY, size, fill);
                    break;
                case "triangular prism":
                    drawTriangularPrism(g2d, centerX, centerY, size, fill);
                    break;
                case "square-based pyramid":
                    drawSquareBasedPyramid(g2d, centerX, centerY, size, fill);
                    break;
                case "cone":
                    drawCone(g2d, centerX, centerY, size, fill);
                    break;
                case "tetrahedron":
                    drawTetrahedron(g2d, centerX, centerY, size, fill);
                    break;
            }
        }

        /**
         * Draws a cube with 3D perspective.
         * 
         * @param g2d The Graphics2D object for drawing
         * @param centerX The center X coordinate
         * @param centerY The center Y coordinate
         * @param size The size of the cube
         * @param fill Whether to fill the cube with color
         */
        private void drawCube(Graphics2D g2d, int centerX, int centerY, int size, boolean fill) {
            int offset = size / 4;
            if (fill) {
                // Front face
                g2d.fillRect(centerX - size/3, centerY - size/3, size*2/3, size*2/3);
                // Top face
                int[] xPoints = {centerX - size/3, centerX + size/3, centerX + size/3 + offset, centerX - size/3 + offset};
                int[] yPoints = {centerY - size/3, centerY - size/3, centerY - size/3 - offset, centerY - size/3 - offset};
                g2d.fillPolygon(xPoints, yPoints, 4);
                // Right face
                int[] xPoints2 = {centerX + size/3, centerX + size/3 + offset, centerX + size/3 + offset, centerX + size/3};
                int[] yPoints2 = {centerY - size/3, centerY - size/3 - offset, centerY + size/3 - offset, centerY + size/3};
                g2d.fillPolygon(xPoints2, yPoints2, 4);
            } else {
                // Draw visible edges with solid lines
                g2d.setStroke(new BasicStroke(2));
                // Front face
                g2d.drawRect(centerX - size/3, centerY - size/3, size*2/3, size*2/3);
                // Top edges
                g2d.drawLine(centerX - size/3, centerY - size/3, centerX - size/3 + offset, centerY - size/3 - offset);
                g2d.drawLine(centerX + size/3, centerY - size/3, centerX + size/3 + offset, centerY - size/3 - offset);
                g2d.drawLine(centerX - size/3 + offset, centerY - size/3 - offset, centerX + size/3 + offset, centerY - size/3 - offset);
                // Right edge
                g2d.drawLine(centerX + size/3 + offset, centerY - size/3 - offset, centerX + size/3 + offset, centerY + size/3 - offset);
                g2d.drawLine(centerX + size/3, centerY + size/3, centerX + size/3 + offset, centerY + size/3 - offset);

                // Draw hidden edges with dashed lines
                float[] dash = {5.0f};
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2d.drawLine(centerX - size/3, centerY + size/3, centerX - size/3 + offset, centerY + size/3 - offset);
                g2d.drawLine(centerX - size/3 + offset, centerY + size/3 - offset, centerX + size/3 + offset, centerY + size/3 - offset);
            }
        }

        /**
         * Draws a cuboid (rectangular prism) with 3D perspective.
         * 
         * @param g2d The Graphics2D object for drawing
         * @param centerX The center X coordinate
         * @param centerY The center Y coordinate
         * @param size The size of the cuboid
         * @param fill Whether to fill the cuboid with color
         */
        private void drawCuboid(Graphics2D g2d, int centerX, int centerY, int size, boolean fill) {
            int offset = size / 4;
            if (fill) {
                // Front face
                g2d.fillRect(centerX - size/3, centerY - size/4, size*2/3, size/2);
                // Top face
                int[] xPoints = {centerX - size/3, centerX - size/3 + offset, centerX + size/3 + offset, centerX + size/3};
                int[] yPoints = {centerY - size/4, centerY - size/4 - offset, centerY - size/4 - offset, centerY - size/4};
                g2d.fillPolygon(xPoints, yPoints, 4);
                // Right face
                int[] xPoints2 = {centerX + size/3, centerX + size/3 + offset, centerX + size/3 + offset, centerX + size/3};
                int[] yPoints2 = {centerY - size/4, centerY - size/4 - offset, centerY + size/4 - offset, centerY + size/4};
                g2d.fillPolygon(xPoints2, yPoints2, 4);
            } else {
                // Front face
                g2d.drawRect(centerX - size/3, centerY - size/4, size*2/3, size/2);
                // Top face
                int[] xPoints = {centerX - size/3, centerX - size/3 + offset, centerX + size/3 + offset, centerX + size/3};
                int[] yPoints = {centerY - size/4, centerY - size/4 - offset, centerY - size/4 - offset, centerY - size/4};
                g2d.drawPolygon(xPoints, yPoints, 4);
                // Right face
                int[] xPoints2 = {centerX + size/3, centerX + size/3 + offset, centerX + size/3 + offset, centerX + size/3};
                int[] yPoints2 = {centerY - size/4, centerY - size/4 - offset, centerY + size/4 - offset, centerY + size/4};
                g2d.drawPolygon(xPoints2, yPoints2, 4);
                // Hidden edges (dashed)
                float[] dash = {5.0f};
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2d.drawLine(centerX - size/3, centerY + size/4, centerX - size/3 + offset, centerY + size/4 - offset);
                g2d.drawLine(centerX - size/3 + offset, centerY + size/4 - offset, centerX + size/3 + offset, centerY + size/4 - offset);
            }
        }

        /**
         * Draws a cylinder with 3D perspective.
         * 
         * @param g2d The Graphics2D object for drawing
         * @param centerX The center X coordinate
         * @param centerY The center Y coordinate
         * @param size The size of the cylinder
         * @param fill Whether to fill the cylinder with color
         */
        private void drawCylinder(Graphics2D g2d, int centerX, int centerY, int size, boolean fill) {
            if (fill) {
                // Body
                g2d.fillRect(centerX - size/3, centerY - size/3, size*2/3, size*2/3);
                // Top ellipse
                g2d.fillOval(centerX - size/3, centerY - size/3 - size/6, size*2/3, size/3);
                // Bottom ellipse
                g2d.fillOval(centerX - size/3, centerY + size/3 - size/6, size*2/3, size/3);
            } else {
                // Body
                g2d.drawRect(centerX - size/3, centerY - size/3, size*2/3, size*2/3);
                // Top ellipse
                g2d.drawOval(centerX - size/3, centerY - size/3 - size/6, size*2/3, size/3);
                // Bottom ellipse
                g2d.drawOval(centerX - size/3, centerY + size/3 - size/6, size*2/3, size/3);
            }
        }

        /**
         * Draws a sphere with 3D shading effects.
         * 
         * @param g2d The Graphics2D object for drawing
         * @param centerX The center X coordinate
         * @param centerY The center Y coordinate
         * @param size The size of the sphere
         * @param fill Whether to fill the sphere with color
         */
        private void drawSphere(Graphics2D g2d, int centerX, int centerY, int size, boolean fill) {
            if (fill) {
                g2d.fillOval(centerX - size/2, centerY - size/2, size, size);
                // Add shading
                g2d.setColor(g2d.getColor().brighter());
                g2d.fillOval(centerX - size/3, centerY - size/3, size/2, size/2);
            } else {
                g2d.drawOval(centerX - size/2, centerY - size/2, size, size);
                // Draw ellipses for 3D effect
                g2d.drawOval(centerX - size/3, centerY - size/2, size*2/3, size);
                g2d.drawOval(centerX - size/2, centerY - size/3, size, size*2/3);
            }
        }

        /**
         * Draws a triangular prism with 3D perspective.
         * 
         * @param g2d The Graphics2D object for drawing
         * @param centerX The center X coordinate
         * @param centerY The center Y coordinate
         * @param size The size of the prism
         * @param fill Whether to fill the prism with color
         */
        private void drawTriangularPrism(Graphics2D g2d, int centerX, int centerY, int size, boolean fill) {
            int offset = size / 4;
            int[] frontX = {centerX, centerX - size/3, centerX + size/3};
            int[] frontY = {centerY - size/3, centerY + size/3, centerY + size/3};
            
            if (fill) {
                // Front face
                g2d.fillPolygon(frontX, frontY, 3);
                // Top face
                int[] topX = {centerX, centerX - size/3, centerX - size/3 + offset, centerX + offset};
                int[] topY = {centerY - size/3, centerY + size/3, centerY + size/3 - offset, centerY - size/3 - offset};
                g2d.fillPolygon(topX, topY, 4);
                // Right face
                int[] rightX = {centerX + size/3, centerX + size/3 + offset, centerX + offset, centerX};
                int[] rightY = {centerY + size/3, centerY + size/3 - offset, centerY - size/3 - offset, centerY - size/3};
                g2d.fillPolygon(rightX, rightY, 4);
            } else {
                // Front face
                g2d.drawPolygon(frontX, frontY, 3);
                // Top face
                int[] topX = {centerX, centerX - size/3, centerX - size/3 + offset, centerX + offset};
                int[] topY = {centerY - size/3, centerY + size/3, centerY + size/3 - offset, centerY - size/3 - offset};
                g2d.drawPolygon(topX, topY, 4);
                // Right face
                int[] rightX = {centerX + size/3, centerX + size/3 + offset, centerX + offset, centerX};
                int[] rightY = {centerY + size/3, centerY + size/3 - offset, centerY - size/3 - offset, centerY - size/3};
                g2d.drawPolygon(rightX, rightY, 4);
            }
        }

        /**
         * Draws a square-based pyramid with 3D perspective.
         * 
         * @param g2d The Graphics2D object for drawing
         * @param centerX The center X coordinate
         * @param centerY The center Y coordinate
         * @param size The size of the pyramid
         * @param fill Whether to fill the pyramid with color
         */
        private void drawSquareBasedPyramid(Graphics2D g2d, int centerX, int centerY, int size, boolean fill) {
            if (fill) {
                // Base
                g2d.fillRect(centerX - size/3, centerY, size*2/3, size*2/3);
                // Front triangle
                int[] triangleX = {centerX - size/3, centerX, centerX + size/3};
                int[] triangleY = {centerY, centerY - size/2, centerY};
                g2d.fillPolygon(triangleX, triangleY, 3);
                // Right triangle
                int[] rightX = {centerX + size/3, centerX, centerX + size/3};
                int[] rightY = {centerY, centerY - size/2, centerY + size*2/3};
                g2d.fillPolygon(rightX, rightY, 3);
            } else {
                // Base
                g2d.drawRect(centerX - size/3, centerY, size*2/3, size*2/3);
                // Front triangle
                int[] triangleX = {centerX - size/3, centerX, centerX + size/3};
                int[] triangleY = {centerY, centerY - size/2, centerY};
                g2d.drawPolygon(triangleX, triangleY, 3);
                // Right triangle
                int[] rightX = {centerX + size/3, centerX, centerX + size/3};
                int[] rightY = {centerY, centerY - size/2, centerY + size*2/3};
                g2d.drawPolygon(rightX, rightY, 3);
                // Left triangle (dashed)
                float[] dash = {5.0f};
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2d.drawLine(centerX - size/3, centerY, centerX, centerY - size/2);
                g2d.drawLine(centerX - size/3, centerY + size*2/3, centerX, centerY - size/2);
            }
        }

        /**
         * Draws a cone with 3D perspective.
         * 
         * @param g2d The Graphics2D object for drawing
         * @param centerX The center X coordinate
         * @param centerY The center Y coordinate
         * @param size The size of the cone
         * @param fill Whether to fill the cone with color
         */
        private void drawCone(Graphics2D g2d, int centerX, int centerY, int size, boolean fill) {
            if (fill) {
                // Draw the cone body
                g2d.fillOval(centerX - size/3, centerY + size/3, size*2/3, size/6); // Base ellipse
                int[] triangleX = {centerX - size/3, centerX, centerX + size/3};
                int[] triangleY = {centerY + size/3, centerY - size/3, centerY + size/3};
                g2d.fillPolygon(triangleX, triangleY, 3);
            } else {
                // Draw visible lines with solid lines
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(centerX - size/3, centerY + size/3, centerX, centerY - size/3); // Left side
                g2d.drawLine(centerX + size/3, centerY + size/3, centerX, centerY - size/3); // Right side
                g2d.drawOval(centerX - size/3, centerY + size/3, size*2/3, size/6); // Front half of base

                // Draw hidden lines with dashed lines
                float[] dash = {5.0f};
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2d.drawArc(centerX - size/3, centerY + size/3, size*2/3, size/6, 180, 180); // Back half of base
            }
        }

        /**
         * Draws a tetrahedron (triangular pyramid) with 3D perspective.
         * 
         * @param g2d The Graphics2D object for drawing
         * @param centerX The center X coordinate
         * @param centerY The center Y coordinate
         * @param size The size of the tetrahedron
         * @param fill Whether to fill the tetrahedron with color
         */
        private void drawTetrahedron(Graphics2D g2d, int centerX, int centerY, int size, boolean fill) {
            if (fill) {
                // Front face
                int[] triangleX = {centerX, centerX - size/3, centerX + size/3};
                int[] triangleY = {centerY - size/3, centerY + size/3, centerY + size/3};
                g2d.fillPolygon(triangleX, triangleY, 3);
                // Right face
                int[] rightX = {centerX, centerX + size/3, centerX + size/2};
                int[] rightY = {centerY - size/3, centerY + size/3, centerY};
                g2d.fillPolygon(rightX, rightY, 3);
            } else {
                // Front face
                int[] triangleX = {centerX, centerX - size/3, centerX + size/3};
                int[] triangleY = {centerY - size/3, centerY + size/3, centerY + size/3};
                g2d.drawPolygon(triangleX, triangleY, 3);
                // Right face
                int[] rightX = {centerX, centerX + size/3, centerX + size/2};
                int[] rightY = {centerY - size/3, centerY + size/3, centerY};
                g2d.drawPolygon(rightX, rightY, 3);
                // Hidden edges (dashed)
                float[] dash = {5.0f};
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2d.drawLine(centerX, centerY - size/3, centerX - size/2, centerY);
                g2d.drawLine(centerX - size/3, centerY + size/3, centerX - size/2, centerY);
            }
        }

        /**
         * Fills a regular polygon with the specified number of sides.
         * 
         * @param g2d The Graphics2D object for drawing
         * @param centerX The center X coordinate
         * @param centerY The center Y coordinate
         * @param radius The radius of the polygon
         * @param sides The number of sides
         */
        private void fillRegularPolygon(Graphics2D g2d, int centerX, int centerY, int radius, int sides) {
            int[] xPoints = new int[sides];
            int[] yPoints = new int[sides];
            
            for (int i = 0; i < sides; i++) {
                double angle = 2 * Math.PI * i / sides - Math.PI / 2;
                xPoints[i] = (int) (centerX + radius * Math.cos(angle));
                yPoints[i] = (int) (centerY + radius * Math.sin(angle));
            }
            
            g2d.fillPolygon(xPoints, yPoints, sides);
        }
    }

    /**
     * Gets the current score for the active game session.
     * 
     * @return The current score
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Shows the task selection dialog allowing users to choose between 2D and 3D modes.
     * 
     * @param parentFrame The parent frame for the dialog
     * @param mainApp Reference to the main application
     */
    public static void showTaskSelectionDialog(JFrame parentFrame, MainApp mainApp) {
        JDialog dialog = new JDialog(parentFrame, "Shape Identification", true);
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create top panel
        JPanel topPanel = new JPanel(new BorderLayout(20, 0));
        topPanel.setBackground(BACKGROUND_COLOR);
        
        // Create HOME button
        JButton homeButton = new JButton("Home");
        homeButton.setPreferredSize(new Dimension(90, 40));
        homeButton.setBackground(new Color(70, 130, 180));
        homeButton.setForeground(Color.WHITE);
        homeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        homeButton.setBorderPainted(false);
        homeButton.setFocusPainted(false);
        
        homeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(new Color(60, 120, 170));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(new Color(70, 130, 180));
            }
        });
        
        homeButton.addActionListener(e -> {
            if (parentFrame != null) {
                for (Component comp : parentFrame.getContentPane().getComponents()) {
                    if (comp instanceof JPanel) {
                        for (Component panelComp : ((JPanel) comp).getComponents()) {
                            if (panelComp instanceof JLabel && ((JLabel) panelComp).getText().startsWith("Current score: ")) {
                                String currentScoreText = ((JLabel) panelComp).getText();
                                int mainFrameScore = Integer.parseInt(currentScoreText.replaceAll("[^0-9]", ""));
                                int newTotalScore = mainFrameScore + (score2D + score3D);
                                ((JLabel) panelComp).setText("Current score: " + newTotalScore);
                                break;
                            }
                        }
                    }
                }
            }
            dialog.dispose();
        });
        
        topPanel.add(homeButton, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel("Shape Selection", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        // Create 2D and 3D buttons
        button2D = createSelectionButton("Basic Task (2D)", false);
        button3D = createSelectionButton("Advanced Task (3D)", true);
        button2D.setIcon(new Triangle2DIcon());
        button3D.setIcon(new Pyramid3DIcon());
        
        if (task2DCompleted) {
            button2D.setEnabled(false);
            button2D.setText("Basic Task (2D) Completed");
            button2D.setBackground(new Color(200, 200, 200));
        }
        if (task3DCompleted) {
            button3D.setEnabled(false);
            button3D.setText("Advanced Task (3D) Completed");
            button3D.setBackground(new Color(200, 200, 200));
        }
        
        button2D.addActionListener(e -> {
            dialog.dispose();
            startGame(parentFrame, mainApp, false);
        });
        button3D.addActionListener(e -> {
            dialog.dispose();
            startGame(parentFrame, mainApp, true);
        });
        
        buttonPanel.add(button2D);
        buttonPanel.add(button3D);
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        dialog.setContentPane(panel);
        dialog.setSize(1200, 700);
        dialog.setLocationRelativeTo(null);
        // Disable close button
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }

    /**
     * Starts a new game instance with the specified mode.
     * 
     * @param parentFrame The parent frame for the game dialog
     * @param mainApp Reference to the main application
     * @param is3D True for 3D mode, false for 2D mode
     */
    private static void startGame(JFrame parentFrame, MainApp mainApp, boolean is3D) {
        JDialog gameDialog = new JDialog(parentFrame, (is3D ? "3D" : "2D") + " Shape Identification", true);
        gameDialog.setContentPane(new ShapeIdentification(gameDialog, is3D, mainApp));
        gameDialog.setSize(800, 600);
        gameDialog.setLocationRelativeTo(null);
        // Disable close button
        gameDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        gameDialog.setVisible(true);
    }

    /**
     * Creates a selection button for the task selection dialog.
     * 
     * @param text The text to display on the button
     * @param is3D Whether this button is for 3D mode
     * @return The configured selection button
     */
    private static JButton createSelectionButton(String text, boolean is3D) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        button.setBackground(new Color(41, 84, 175));  // Use bright blue color from hover state
        button.setForeground(Color.BLACK);  // Change to black text
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setIconTextGap(20);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        // Larger button with prominent border
        button.setPreferredSize(new Dimension(220, 220));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 0), 4),  // Use bright yellow border from hover state
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        
        return button;
    }
    
    /**
     * Icon class for 2D triangle using soft blue-purple gradient.
     */
    private static class Triangle2DIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw equilateral triangle
            int[] xPoints = {x + getIconWidth()/2, x + getIconWidth() - 10, x + 10};
            int[] yPoints = {y + 10, y + getIconHeight() - 10, y + getIconHeight() - 10};
            
            // Create gradient fill
            GradientPaint gradient = new GradientPaint(
                x, y, new Color(100, 149, 237), // Cornflower blue
                x, y + getIconHeight(), new Color(123, 104, 238) // Medium slate blue
            );
            g2d.setPaint(gradient);
            g2d.fillPolygon(xPoints, yPoints, 3);
            
            // Draw border
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(new Color(25, 25, 112)); // Midnight blue border
            g2d.drawPolygon(xPoints, yPoints, 3);
            
            g2d.dispose();
        }
        
        @Override
        public int getIconWidth() {
            return 100;
        }
        
        @Override
        public int getIconHeight() {
            return 100;
        }
    }
    
    /**
     * Icon class for 3D pyramid using advanced blue-green tones.
     */
    private static class Pyramid3DIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Pyramid coordinates
            int[] xPoints = {x + getIconWidth()/2, x + getIconWidth() - 10, x + 10};
            int[] yPoints = {y + 10, y + getIconHeight() - 10, y + getIconHeight() - 10};
            int backX = x + getIconWidth()/2 + 30;
            int backY = y + getIconHeight()/2;
            
            // Fill bottom triangle - light teal
            g2d.setColor(new Color(64, 224, 208)); // Turquoise
            g2d.fillPolygon(xPoints, yPoints, 3);
            
            // Fill sides using gradient
            int[] sideX1 = {xPoints[0], xPoints[1], backX};
            int[] sideY1 = {yPoints[0], yPoints[1], backY};
            g2d.setColor(new Color(0, 139, 139)); // Dark cyan
            g2d.fillPolygon(sideX1, sideY1, 3);
            
            int[] sideX2 = {xPoints[0], xPoints[2], backX};
            int[] sideY2 = {yPoints[0], yPoints[2], backY};
            g2d.setColor(new Color(32, 178, 170)); // Light sea green
            g2d.fillPolygon(sideX2, sideY2, 3);
            
            // Draw borders - dark blue-green
            g2d.setStroke(new BasicStroke(2.5f));
            g2d.setColor(new Color(0, 105, 148));
            
            // Draw base triangle
            g2d.drawPolygon(xPoints, yPoints, 3);
            
            // Draw all connecting lines
            g2d.drawLine(xPoints[0], yPoints[0], backX, backY);  // Top to back
            g2d.drawLine(backX, backY, xPoints[1], yPoints[1]);  // Back to bottom right
            g2d.drawLine(backX, backY, xPoints[2], yPoints[2]);  // Back to bottom left
            
            g2d.dispose();
        }
        
        @Override
        public int getIconWidth() {
            return 100;
        }
        
        @Override
        public int getIconHeight() {
            return 100;
        }
    }

    /**
     * Checks if both 2D and 3D tasks have been completed.
     * 
     * @return True if both tasks are completed, false otherwise
     */
    public static boolean isTaskCompleted() {
        return task2DCompleted && task3DCompleted;
    }

    /**
     * Gets the total combined score from both 2D and 3D tasks.
     * 
     * @return The total score
     */
    public static int getTotalScore() {
        return score2D + score3D;
    }

    /**
     * Updates the score label with the current score.
     */
    private void updateScoreLabel() {
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + currentScore);
        }
    }

    /**
     * Resets the game state upon completion and updates progress tracking.
     */
    private void resetGameState() {
        if (is3DMode) {
            score3D = currentScore;
            task3DCompleted = true;
            currentShapeIndex3D = 0;
            currentSelectedShapes3D = null;
        } else {
            score2D = currentScore;
            task2DCompleted = true;
            currentShapeIndex2D = 0;
            currentSelectedShapes2D = null;
        }
        mainApp.updateKeyStage1Progress(1, 2);
    }

    /**
     * Shows the game completion dialog with the final score.
     */
    private void showGameCompletionDialog() {
        resetGameState();
        JOptionPane.showMessageDialog(parentDialog,
            "Congratulations on completing the " + (is3DMode ? "3D" : "2D") + 
            " shape identification task!\nYour score is: " + currentScore,
            "Task Completed",
            JOptionPane.INFORMATION_MESSAGE);
        parentDialog.dispose();
    }
}