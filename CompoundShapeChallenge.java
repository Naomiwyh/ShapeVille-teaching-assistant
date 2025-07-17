/**
 * CompoundShapeChallenge.java
 * 
 * @version 1.0
 * @author Yiliang Zhang
 * @since 2025
 * 
 * 
 * 
 * This class provides an interactive challenge for elementary school students (grades 3-4)
 * to calculate the area of compound geometric shapes. The application presents
 * visual challenges with timed exercises to reinforce mathematical concepts.
 */

 import java.awt.*;
 import java.awt.event.*;
 import java.awt.image.BufferedImage;
 import java.io.File;
 import java.io.IOException;
 import javax.imageio.ImageIO;
 import javax.swing.*;
 import javax.swing.border.EmptyBorder;
 import java.util.HashMap;
 import java.util.Map;
 
 /**
  * CompoundShapeChallenge class manages the UI and logic for a geometric area calculation game.
  * Students are presented with compound shapes and must calculate their areas within a time limit.
  */
 public class CompoundShapeChallenge extends JPanel {
     private int challengeNumber;
     private JDialog parentDialog;
     private static final Color BACKGROUND_COLOR = new Color(230, 240, 255);
     private static final Color BUTTON_COLOR = new Color(70, 130, 180);
     private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
     private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
     private static final Color ERROR_COLOR = new Color(231, 76, 60);
     private static final Color TIMER_WARNING_COLOR = new Color(230, 126, 34);
     private static final Font TITLE_FONT = new Font("Comic Sans MS", Font.BOLD, 22);
     private static final Font INSTRUCTION_FONT = new Font("Comic Sans MS", Font.PLAIN, 16);
     private static final Font INPUT_FONT = new Font("Comic Sans MS", Font.BOLD, 18);
     
     // Challenge data
     private BufferedImage challengeImage;
     private Map<Integer, Double> correctAnswers = new HashMap<>();
     private Map<Integer, String> units = new HashMap<>();
     private Map<Integer, String> solutions = new HashMap<>();
     
     // UI components
     private JTextField answerField;
     private JLabel resultLabel;
     private JLabel timerLabel;
     private JButton submitButton;
     private JButton closeButton;
     
     // Game mechanics
     private Timer countdownTimer;
     private int remainingSeconds = 300; // 5 minutes
     private int remainingAttempts = 3;
     private boolean challengeCompleted = false;
     
     // Tracking attempt count
     private int attemptCount = 0;
     
     // Score display
     private JLabel scoreLabel;
     
     // Flag to track if window has been closed
     private boolean windowClosed = false;
     
     /**
      * Constructor for the CompoundShapeChallenge.
      * 
      * @param challengeNumber The challenge number (1-9)
      * @param parentDialog The parent dialog containing this challenge
      */
     public CompoundShapeChallenge(int challengeNumber, JDialog parentDialog) {
         this.challengeNumber = challengeNumber;
         this.parentDialog = parentDialog;
         
         // Disable close button
         parentDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
         
         setLayout(new BorderLayout(10, 10));
         setBackground(BACKGROUND_COLOR);
         setBorder(new EmptyBorder(20, 20, 20, 20));
         
         // Initialize correct answers and solutions
         initializeSolutions();
         
         // Load challenge image
         loadChallengeImage();
         
         // Create UI
         createUI();
         
         // Start timer
         startCountdownTimer();
     }
     
     /**
      * Initializes the solutions, correct answers, and units for each challenge.
      */
     private void initializeSolutions() {
         // Set up correct answers
         correctAnswers.put(1, 0.0);    // 0cm²
         correctAnswers.put(2, 310.0);  // 310cm²
         correctAnswers.put(3, 598.0);  // 598cm²
         correctAnswers.put(4, 288.0);  // 288m²
         correctAnswers.put(5, 18.0);   // 18m²
         correctAnswers.put(6, 159.5);  // 159.5m²
         correctAnswers.put(7, 0.0);    // 0cm²
         correctAnswers.put(8, 3456.0); // 3456m²
         correctAnswers.put(9, 174.0);  // 174m²
         
         // Set up units
         units.put(1, "cm²");
         units.put(2, "cm²");
         units.put(3, "cm²");
         units.put(4, "m²");
         units.put(5, "m²");
         units.put(6, "m²");
         units.put(7, "cm²");
         units.put(8, "m²");
         units.put(9, "m²");
         
         // Set up detailed solutions
         solutions.put(1, "This shape is one-dimensional, so it has no area: 0 cm²");
         
         solutions.put(2, "Method: Divide the shape into two rectangles.\n\n" +
                        "Upper rectangle: Length = 10 cm, Width = 11 cm\n" +
                        "Area of upper rectangle = 10 cm × 11 cm = 110 cm²\n\n" +
                        "Lower rectangle: Length = 20 cm, Width = 10 cm\n" +
                        "Area of lower rectangle = 20 cm × 10 cm = 200 cm²\n\n" +
                        "Total area = 110 cm² + 200 cm² = 310 cm²");
         
         solutions.put(3, "Method: Divide the shape into two rectangles.\n\n" +
                        "Upper rectangle: Length = 18 cm, Width = (19 - 16) cm = 3 cm\n" +
                        "Area of upper rectangle = 18 cm × 3 cm = 54 cm²\n\n" +
                        "Lower rectangle: Length = (18 + 16) cm = 34 cm, Width = 16 cm\n" +
                        "Area of lower rectangle = 34 cm × 16 cm = 544 cm²\n\n" +
                        "Total area = 54 cm² + 544 cm² = 598 cm²");
         
         solutions.put(4, "Method: Divide the shape into a square and a rectangle.\n\n" +
                        "Upper square: Side length = (24 - 2 - 10) m = 12 m\n" +
                        "Area of square = 12 m × 12 m = 144 m²\n\n" +
                        "Lower rectangle: Length = 24 m, Width = 6 m\n" +
                        "Area of rectangle = 24 m × 6 m = 144 m²\n\n" +
                        "Total area = 144 m² + 144 m² = 288 m²");
         
         solutions.put(5, "Method: Divide the shape into a triangle and a rectangle.\n\n" +
                        "Upper triangle: Base = 4 m, Height = (6 - 3) m = 3 m\n" +
                        "Area of triangle = (1/2) × 4 m × 3 m = 6 m²\n\n" +
                        "Lower rectangle: Length = 4 m, Width = 3 m\n" +
                        "Area of rectangle = 4 m × 3 m = 12 m²\n\n" +
                        "Total area = 6 m² + 12 m² = 18 m²");
         
         solutions.put(6, "Rectangle: 10 × 15 = 150 m²\nTriangle: (9.5 × 2) ÷ 2 = 9.5 m²\nTotal area: 150 + 9.5 = 159.5 m²");
         
         solutions.put(7, "This shape is one-dimensional, so it has no area: 0 cm²");
         
         solutions.put(8, "Method: Divide the shape into a square and a rectangle.\n\n" +
                        "Upper square: Side length = 36 m\n" +
                        "Area of square = 36 m × 36 m = 1296 m²\n\n" +
                        "Lower rectangle: Length = 60 m, Width = 36 m\n" +
                        "Area of rectangle = 60 m × 36 m = 2160 m²\n\n" +
                        "Total area = 1296 m² + 2160 m² = 3456 m²");
         
         solutions.put(9, "Method: Divide the shape into two rectangles.\n\n" +
                        "Upper rectangle: Length = 10 m, Width = (11 - 8) m = 3 m\n" +
                        "Area of upper rectangle = 10 m × 3 m = 30 m²\n\n" +
                        "Lower rectangle: Length = (10 + 8) m = 18 m, Width = 8 m\n" +
                        "Area of lower rectangle = 18 m × 8 m = 144 m²\n\n" +
                        "Total area = 30 m² + 144 m² = 174 m²");
     }
     
     /**
      * Loads the challenge image from the resources directory.
      */
     private void loadChallengeImage() {
         try {
             // Try multiple possible paths
             File imageFile = null;
             String[] possiblePaths = {
                 "resources/images/" + challengeNumber + ".jpg",
                 "task-5-19/resources/images/" + challengeNumber + ".jpg",
                 "../resources/images/" + challengeNumber + ".jpg"
             };
             
             for (String path : possiblePaths) {
                 File tempFile = new File(path);
                 if (tempFile.exists()) {
                     imageFile = tempFile;
                     System.out.println("Found challenge image at: " + tempFile.getAbsolutePath());
                     break;
                 }
             }
             
             if (imageFile != null && imageFile.exists()) {
                 challengeImage = ImageIO.read(imageFile);
                 System.out.println("Successfully loaded image: " + imageFile.getAbsolutePath());
             } else {
                 System.out.println("Image file not found for challenge: " + challengeNumber);
                 System.out.println("Tried paths: " + String.join(", ", possiblePaths));
             }
         } catch (IOException e) {
             System.out.println("Error loading image: " + e.getMessage());
             e.printStackTrace();
         }
     }
     
     /**
      * Creates the UI components for the challenge.
      */
     private void createUI() {
         // Top panel - Home button
         JPanel topPanel = createTopPanel();
         add(topPanel, BorderLayout.NORTH);
         
         // Center panel - Main content
         JPanel centerPanel = createCenterPanel();
         add(centerPanel, BorderLayout.CENTER);
         
         // Bottom panel - Timer and controls
         JPanel bottomPanel = createBottomPanel();
         add(bottomPanel, BorderLayout.SOUTH);
     }
     
     /**
      * Creates the top panel with the home button and score display.
      * 
      * @return JPanel containing the top panel elements
      */
     private JPanel createTopPanel() {
         JPanel panel = new JPanel(new BorderLayout());
         panel.setBackground(BACKGROUND_COLOR);
         
         // Home button - Modified behavior
         JButton homeButton = createStyledButton("Home");
         homeButton.setPreferredSize(new Dimension(100, 40));
         homeButton.addActionListener(e -> {
             stopTimer();
             
             // Get window hierarchy
             Window window = SwingUtilities.getWindowAncestor(this);
             Window parentWindow = null;
             
             // Close all dialogs until back to main window
             while (window != null) {
                 if (window instanceof JDialog) {
                     parentWindow = window.getOwner();
                     window.dispose();
                     window = parentWindow;
                 } else {
                     break;
                 }
             }
         });
         panel.add(homeButton, BorderLayout.WEST);
         
         // Add score display
         scoreLabel = new JLabel("Score: " + CompoundShapeAreaCalculation.getTotalScore());
         scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
         scoreLabel.setForeground(new Color(70, 130, 180));
         panel.add(scoreLabel, BorderLayout.EAST);
         
         return panel;
     }
 
     /**
      * Creates the center panel with the challenge image, instructions, and answer input.
      * 
      * @return JPanel containing the center panel elements
      */
     private JPanel createCenterPanel() {
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.setBackground(BACKGROUND_COLOR);
         
         // Image panel
         JPanel imagePanel = createImagePanel();
         imagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
         panel.add(imagePanel);
         
         // Add space
         panel.add(Box.createVerticalStrut(20));
         
         // Instructions panel
         JPanel instructionsPanel = createInstructionsPanel();
         instructionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
         panel.add(instructionsPanel);
         
         // Add space
         panel.add(Box.createVerticalStrut(20));
         
         // Answer panel
         JPanel answerPanel = createAnswerPanel();
         answerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
         panel.add(answerPanel);
         
         // Results label
         JPanel resultsPanel = new JPanel();
         resultsPanel.setBackground(BACKGROUND_COLOR);
         resultLabel = new JLabel(" ");
         resultLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
         resultsPanel.add(resultLabel);
         resultsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
         panel.add(Box.createVerticalStrut(10));
         panel.add(resultsPanel);
         
         return panel;
     }
     
     /**
      * Creates the image panel that displays the challenge image.
      * 
      * @return JPanel containing the challenge image
      */
     private JPanel createImagePanel() {
         JPanel panel = new JPanel(new BorderLayout());
         panel.setBackground(BACKGROUND_COLOR);
         panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
         
         if (challengeImage != null) {
             // Calculate appropriate image size
             int maxWidth = 400;
             int maxHeight = 300;
             
             double widthRatio = (double) maxWidth / challengeImage.getWidth();
             double heightRatio = (double) maxHeight / challengeImage.getHeight();
             double ratio = Math.min(widthRatio, heightRatio);
             
             int width = (int) (challengeImage.getWidth() * ratio);
             int height = (int) (challengeImage.getHeight() * ratio);
             
             Image scaledImage = challengeImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
             JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
             imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
             panel.add(imageLabel, BorderLayout.CENTER);
         } else {
             JLabel noImageLabel = new JLabel("Image not available", SwingConstants.CENTER);
             noImageLabel.setFont(INSTRUCTION_FONT);
             panel.add(noImageLabel, BorderLayout.CENTER);
         }
         
         return panel;
     }
     
     /**
      * Creates the instructions panel with challenge instructions and unit information.
      * 
      * @return JPanel containing the instruction elements
      */
     private JPanel createInstructionsPanel() {
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.setBackground(BACKGROUND_COLOR);
         
         JLabel instructionsLabel = new JLabel("Please calculate the area of this compound shape in 5 minutes!");
         instructionsLabel.setFont(INSTRUCTION_FONT);
         instructionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         panel.add(instructionsLabel);
         
         // Unit reminder
         String unit = units.get(challengeNumber);
         JLabel unitLabel = new JLabel("Note: The unit (" + unit + ") is already provided. You only need to enter the number.");
         unitLabel.setFont(new Font("Comic Sans MS", Font.ITALIC, 14));
         unitLabel.setForeground(new Color(65, 105, 225)); // Royal blue
         unitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         panel.add(Box.createVerticalStrut(5));
         panel.add(unitLabel);
         
         return panel;
     }
     
     /**
      * Creates the answer panel with the answer input field and submit button.
      * 
      * @return JPanel containing the answer input elements
      */
     private JPanel createAnswerPanel() {
         JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         panel.setBackground(BACKGROUND_COLOR);
         
         JLabel answerLabel = new JLabel("Your answer: ");
         answerLabel.setFont(INPUT_FONT);
         
         answerField = new JTextField(8);
         answerField.setFont(INPUT_FONT);
         answerField.addKeyListener(new KeyAdapter() {
             @Override
             public void keyPressed(KeyEvent e) {
                 if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                     checkAnswer();
                 }
             }
         });
         
         submitButton = createStyledButton("Submit");
         submitButton.addActionListener(e -> checkAnswer());
         
         panel.add(answerLabel);
         panel.add(answerField);
         panel.add(submitButton);
         
         // Add the unit label
         String unit = units.get(challengeNumber);
         JLabel unitDisplayLabel = new JLabel(unit);
         unitDisplayLabel.setFont(INPUT_FONT);
         panel.add(unitDisplayLabel);
         
         return panel;
     }
     
     /**
      * Creates the bottom panel with the timer and close button.
      * 
      * @return JPanel containing the bottom panel elements
      */
     private JPanel createBottomPanel() {
         JPanel panel = new JPanel(new BorderLayout());
         panel.setBackground(BACKGROUND_COLOR);
         panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
         
         // Timer panel
         JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         timerPanel.setBackground(BACKGROUND_COLOR);
         
         // Create clock icon
         ImageIcon clockIcon = createClockIcon();
         JLabel clockLabel = new JLabel(clockIcon);
         timerPanel.add(clockLabel);
         
         timerLabel = new JLabel("Time remaining: 5:00");
         timerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
         timerLabel.setForeground(BUTTON_COLOR);
         timerPanel.add(timerLabel);
         
         panel.add(timerPanel, BorderLayout.NORTH);
         
         // Button panel - only keeps Close button and centers it
         JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         buttonsPanel.setBackground(BACKGROUND_COLOR);
         
         closeButton = createStyledButton("Close");
         closeButton.addActionListener(e -> {
             // Stop timer and mark window as closed
             stopTimer();
             windowClosed = true;
             parentDialog.dispose();
         });
         
         buttonsPanel.add(closeButton);
         
         panel.add(buttonsPanel, BorderLayout.CENTER);
         
         return panel;
     }
     
     /**
      * Creates a simple clock icon for the timer display.
      * 
      * @return ImageIcon with the clock design
      */
     private ImageIcon createClockIcon() {
         // Create 24x24 clock icon
         BufferedImage clockImage = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
         Graphics2D g2d = clockImage.createGraphics();
         
         // Enable anti-aliasing
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         
         // Draw clock face
         g2d.setColor(Color.WHITE);
         g2d.fillOval(1, 1, 22, 22);
         g2d.setColor(BUTTON_COLOR);
         g2d.drawOval(1, 1, 22, 22);
         
         // Draw center
         g2d.fillOval(11, 11, 2, 2);
         
         // Draw hour hand
         g2d.setStroke(new BasicStroke(2));
         g2d.drawLine(12, 12, 12, 7);
         
         // Draw minute hand
         g2d.drawLine(12, 12, 16, 12);
         
         g2d.dispose();
         
         return new ImageIcon(clockImage);
     }
     
     /**
      * Starts the countdown timer for the challenge.
      */
     private void startCountdownTimer() {
         countdownTimer = new Timer(1000, new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 remainingSeconds--;
                 updateTimerDisplay();
                 
                 if (remainingSeconds <= 0) {
                     timeExpired();
                 } else if (remainingSeconds <= 60) {
                     timerLabel.setForeground(TIMER_WARNING_COLOR);
                 }
             }
         });
         countdownTimer.start();
     }
     
     /**
      * Updates the timer display with the current remaining time.
      */
     private void updateTimerDisplay() {
         int minutes = remainingSeconds / 60;
         int seconds = remainingSeconds % 60;
         timerLabel.setText(String.format("Time remaining: %d:%02d", minutes, seconds));
     }
     
     /**
      * Stops the countdown timer.
      */
     private void stopTimer() {
         if (countdownTimer != null && countdownTimer.isRunning()) {
             countdownTimer.stop();
         }
     }
     
     /**
      * Handles the timer expiration logic.
      * Shows the solution if time expires.
      */
     private void timeExpired() {
         stopTimer();
         
         // If window is already closed, don't display solution
         if (windowClosed) {
             return;
         }
         
         if (!challengeCompleted) {
             challengeCompleted = true;
             
             resultLabel.setText("Time's up!");
             resultLabel.setForeground(TIMER_WARNING_COLOR);
             disableInputs();
             
             // Mark challenge as completed (even if time expired) - pass attempts count
             CompoundShapeAreaCalculation.markChallengeCompleted(challengeNumber, false, 3);
             
             // Update score display
             scoreLabel.setText("Score: " + CompoundShapeAreaCalculation.getTotalScore());
             
             // Display solution in dialog
             String solution = solutions.get(challengeNumber);
             String unit = units.get(challengeNumber);
             double correctAnswer = correctAnswers.get(challengeNumber);
             
             // Create custom dialog
             JDialog solutionDialog = new JDialog(parentDialog, "Time's Up - Solution for Challenge " + challengeNumber, true);
             solutionDialog.setLayout(new BorderLayout(10, 10));
             solutionDialog.setSize(650, 650); // Increase window size to fit image
             solutionDialog.setLocationRelativeTo(this);
             
             // Solution panel
             JPanel contentPanel = new JPanel();
             contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
             contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
             contentPanel.setBackground(new Color(240, 248, 255));
             
             // Title
             JLabel titleLabel = new JLabel("Correct Answer: " + correctAnswer + " " + unit);
             titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
             titleLabel.setForeground(SUCCESS_COLOR);
             titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
             contentPanel.add(titleLabel);
             contentPanel.add(Box.createVerticalStrut(20));
             
             // Add solution image
             try {
                 // Try multiple possible paths
                 File imageFile = null;
                 String[] possiblePaths = {
                     "resources/solutions/solution" + challengeNumber + ".jpg",
                     "task-5-19/resources/solutions/solution" + challengeNumber + ".jpg",
                     "../resources/solutions/solution" + challengeNumber + ".jpg"
                 };
                 
                 for (String path : possiblePaths) {
                     File tempFile = new File(path);
                     if (tempFile.exists()) {
                         imageFile = tempFile;
                         System.out.println("Found solution image at: " + tempFile.getAbsolutePath());
                         break;
                     }
                 }
                 
                 if (imageFile != null && imageFile.exists()) {
                     BufferedImage img = ImageIO.read(imageFile);
                     // Resize image to fit window, using higher quality scaling method
                     int maxWidth = 500;
                     int maxHeight = 200;
                     
                     // Calculate scaling ratio
                     double widthRatio = (double) maxWidth / img.getWidth();
                     double heightRatio = (double) maxHeight / img.getHeight();
                     double ratio = Math.min(widthRatio, heightRatio);
                     
                     // Use higher quality scaling
                     int width = (int) (img.getWidth() * ratio);
                     int height = (int) (img.getHeight() * ratio);
                     
                     // Create high-quality scaled image
                     BufferedImage scaledBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                     Graphics2D g2d = scaledBufferedImage.createGraphics();
                     
                     // Set high-quality rendering hints
                     g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                     g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                     
                     g2d.drawImage(img, 0, 0, width, height, null);
                     g2d.dispose();
                     
                     JLabel imageLabel = new JLabel(new ImageIcon(scaledBufferedImage));
                     imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                     imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                     contentPanel.add(imageLabel);
                     contentPanel.add(Box.createVerticalStrut(20));
                     System.out.println("Solution image loaded: solution" + challengeNumber + ".jpg");
                 } else {
                     System.out.println("Solution image not found: solution" + challengeNumber + ".jpg");
                 }
             } catch (IOException e) {
                 System.out.println("Error loading solution image: " + e.getMessage());
             }
             
             // Solution text
             JTextArea solutionText = new JTextArea(solution);
             solutionText.setFont(new Font("Comic Sans MS", Font.PLAIN, 16)); // Larger font
             solutionText.setEditable(false);
             solutionText.setWrapStyleWord(true);
             solutionText.setLineWrap(true);
             solutionText.setBackground(new Color(240, 248, 255));
             solutionText.setMargin(new Insets(15, 15, 15, 15)); // Increased padding
             
             JScrollPane scrollPane = new JScrollPane(solutionText);
             scrollPane.setBorder(BorderFactory.createEmptyBorder());
             contentPanel.add(scrollPane);
             
             // Encouraging message
             contentPanel.add(Box.createVerticalStrut(20));
             JLabel encouragementLabel = new JLabel("Good try! Next time you'll solve it before time runs out!");
             encouragementLabel.setFont(new Font("Comic Sans MS", Font.ITALIC, 14));
             encouragementLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
             contentPanel.add(encouragementLabel);
             
             // Close button
             JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
             buttonPanel.setBackground(new Color(240, 248, 255));
             JButton closeDialogButton = createStyledButton("Got it!");
             closeDialogButton.addActionListener(e -> solutionDialog.dispose());
             buttonPanel.add(closeDialogButton);
             
             // Add panels to dialog
             solutionDialog.add(contentPanel, BorderLayout.CENTER);
             solutionDialog.add(buttonPanel, BorderLayout.SOUTH);
             
             // Show dialog
             solutionDialog.setVisible(true);
         }
     }
     
     /**
      * Checks the submitted answer against the correct answer.
      */
     private void checkAnswer() {
         if (challengeCompleted) {
             return;
         }
         
         String answerText = answerField.getText().trim();
         if (answerText.isEmpty()) {
             resultLabel.setText("Please enter an answer first!");
             resultLabel.setForeground(TIMER_WARNING_COLOR);
             return;
         }
         
         try {
             // Increment attempt count
             attemptCount++;
             
             double userAnswer = Double.parseDouble(answerText);
             double correctAnswer = correctAnswers.get(challengeNumber);
             
             // Special case for zero answers (challenges 1 and 7)
             if (correctAnswer == 0) {
                 if (userAnswer == 0) {
                     // Correct!
                     handleCorrectAnswer();
                 } else {
                     handleIncorrectAnswer();
                 }
                 return;
             }
             
             // Use strict matching for mathematically equal values
             // Using very small tolerance (1e-10) only for floating point precision issues, such as 10 vs 10.0
             double epsilon = 1e-10;
             double diff = Math.abs(userAnswer - correctAnswer);
             
             if (diff < epsilon || diff / Math.max(Math.abs(userAnswer), Math.abs(correctAnswer)) < epsilon) {
                 // Mathematically equal answer, consider correct
                 handleCorrectAnswer();
             } else {
                 // Answer doesn't match, consider incorrect
                 handleIncorrectAnswer();
             }
         } catch (NumberFormatException e) {
             resultLabel.setText("Please enter a valid number!");
             resultLabel.setForeground(TIMER_WARNING_COLOR);
             // Failed attempts don't count
             attemptCount--;
         }
     }
     
     /**
      * Handles the case when the user submits a correct answer.
      * Displays congratulations and solution.
      */
     private void handleCorrectAnswer() {
         resultLabel.setText("Great job! Your answer is correct!");
         resultLabel.setForeground(SUCCESS_COLOR);
         
         challengeCompleted = true;
         stopTimer();
         disableInputs();
         
         // Use attempt count to mark completion
         CompoundShapeAreaCalculation.markChallengeCompleted(challengeNumber, true, attemptCount);
         
         // Update score display
         scoreLabel.setText("Score: " + CompoundShapeAreaCalculation.getTotalScore());
         
         // Display celebration and solution in dialog
         String solution = solutions.get(challengeNumber);
         String unit = units.get(challengeNumber);
         double correctAnswer = correctAnswers.get(challengeNumber);
         
         // Create celebration dialog
         JDialog celebrationDialog = new JDialog(parentDialog, "Congratulations!", true);
         celebrationDialog.setLayout(new BorderLayout(10, 10));
         celebrationDialog.setSize(650, 650); // Increase window size to fit image
         celebrationDialog.setLocationRelativeTo(this);
         
         // Solution panel
         JPanel contentPanel = new JPanel();
         contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
         contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
         contentPanel.setBackground(new Color(240, 255, 240));  // Light green
         
         // Celebration title
         JLabel titleLabel = new JLabel("CORRECT!");
         titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
         titleLabel.setForeground(SUCCESS_COLOR);
         titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         contentPanel.add(titleLabel);
         contentPanel.add(Box.createVerticalStrut(10));
         
         // Answer confirmation
         JLabel answerLabel = new JLabel("The correct answer is: " + correctAnswer + " " + unit);
         answerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
         answerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         contentPanel.add(answerLabel);
         contentPanel.add(Box.createVerticalStrut(20));
         
         // Add score information
         int pointsEarned = 0;
         if (attemptCount == 1) pointsEarned = 6;
         else if (attemptCount == 2) pointsEarned = 4;
         else pointsEarned = 2;
         
         JLabel pointsLabel = new JLabel("Points earned: " + pointsEarned);
         pointsLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
         pointsLabel.setForeground(new Color(46, 134, 193)); // Blue
         pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         contentPanel.add(pointsLabel);
         contentPanel.add(Box.createVerticalStrut(20));
 
         // Add solution image
         try {
             // Try multiple possible paths
             File imageFile = null;
             String[] possiblePaths = {
                 "resources/solutions/solution" + challengeNumber + ".jpg",
                 "task-5-19/resources/solutions/solution" + challengeNumber + ".jpg",
                 "../resources/solutions/solution" + challengeNumber + ".jpg"
             };
             
             for (String path : possiblePaths) {
                 File tempFile = new File(path);
                 if (tempFile.exists()) {
                     imageFile = tempFile;
                     System.out.println("Found solution image at: " + tempFile.getAbsolutePath());
                     break;
                 }
             }
             
             if (imageFile != null && imageFile.exists()) {
                 BufferedImage img = ImageIO.read(imageFile);
                 // Resize image to fit window
                 int maxWidth = 500;
                 int maxHeight = 200;
                 
                 double widthRatio = (double) maxWidth / img.getWidth();
                 double heightRatio = (double) maxHeight / img.getHeight();
                 double ratio = Math.min(widthRatio, heightRatio);
                 
                 int width = (int) (img.getWidth() * ratio);
                 int height = (int) (img.getHeight() * ratio);
                 
                 Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                 JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                 imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                 contentPanel.add(imageLabel);
                 contentPanel.add(Box.createVerticalStrut(20));
                 System.out.println("Solution image loaded: solution" + challengeNumber + ".jpg");
             } else {
                 System.out.println("Solution image not found: solution" + challengeNumber + ".jpg");
             }
         } catch (IOException e) {
             System.out.println("Error loading solution image: " + e.getMessage());
         }
         
         // Solution explanation
         JLabel solutionTitle = new JLabel("Solution Method:");
         solutionTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
         solutionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
         contentPanel.add(solutionTitle);
         contentPanel.add(Box.createVerticalStrut(10));
         
         // Solution text
         JTextArea solutionText = new JTextArea(solution);
         solutionText.setFont(new Font("Comic Sans MS", Font.PLAIN, 16)); // Larger font
         solutionText.setEditable(false);
         solutionText.setWrapStyleWord(true);
         solutionText.setLineWrap(true);
         solutionText.setBackground(new Color(240, 255, 240));
         solutionText.setMargin(new Insets(15, 15, 15, 15)); // Increased padding
         
         JScrollPane scrollPane = new JScrollPane(solutionText);
         scrollPane.setBorder(BorderFactory.createEmptyBorder());
         contentPanel.add(scrollPane);
         
         // Celebration message
         contentPanel.add(Box.createVerticalStrut(20));
         JLabel encouragementLabel = new JLabel("Excellent work! You're becoming a math champion!");
         encouragementLabel.setFont(new Font("Comic Sans MS", Font.ITALIC, 14));
         encouragementLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         contentPanel.add(encouragementLabel);
         
         // Close button
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         buttonPanel.setBackground(new Color(240, 255, 240));
         JButton closeDialogButton = createStyledButton("Awesome!");
         closeDialogButton.addActionListener(e -> celebrationDialog.dispose());
         buttonPanel.add(closeDialogButton);
         
         // Add panels to dialog
         celebrationDialog.add(contentPanel, BorderLayout.CENTER);
         celebrationDialog.add(buttonPanel, BorderLayout.SOUTH);
         
         // Show dialog
         celebrationDialog.setVisible(true);
     }
     
     /**
      * Handles the case when the user submits an incorrect answer.
      * Counts attempts and displays solution after last attempt.
      */
     private void handleIncorrectAnswer() {
         remainingAttempts--;
         
         if (remainingAttempts > 0) {
             resultLabel.setText("Not quite right. You have " + remainingAttempts + 
                                (remainingAttempts == 1 ? " try" : " tries") + " left.");
             resultLabel.setForeground(ERROR_COLOR);
         } else {
             resultLabel.setText("That's not correct.");
             resultLabel.setForeground(ERROR_COLOR);
             
             // Mark completion with attempt count
             CompoundShapeAreaCalculation.markChallengeCompleted(challengeNumber, false, 3);
             
             // Update score display
             scoreLabel.setText("Score: " + CompoundShapeAreaCalculation.getTotalScore());
             
             // Display solution in dialog
             String solution = solutions.get(challengeNumber);
             String unit = units.get(challengeNumber);
             double correctAnswer = correctAnswers.get(challengeNumber);
             
             // Create custom dialog
             JDialog solutionDialog = new JDialog(parentDialog, "Solution for Challenge " + challengeNumber, true);
             solutionDialog.setLayout(new BorderLayout(10, 10));
             solutionDialog.setSize(650, 650); // Increase window size to fit image
             solutionDialog.setLocationRelativeTo(this);
             
             // Solution panel
             JPanel contentPanel = new JPanel();
             contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
             contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
             contentPanel.setBackground(new Color(240, 248, 255));
             
             // Title
             JLabel titleLabel = new JLabel("Correct Answer: " + correctAnswer + " " + unit);
             titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
             titleLabel.setForeground(SUCCESS_COLOR);
             titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
             contentPanel.add(titleLabel);
             contentPanel.add(Box.createVerticalStrut(20));
             
             // Add solution image
             try {
                 // Try multiple possible paths
                 File imageFile = null;
                 String[] possiblePaths = {
                     "resources/solutions/solution" + challengeNumber + ".jpg",
                     "task-5-19/resources/solutions/solution" + challengeNumber + ".jpg",
                     "../resources/solutions/solution" + challengeNumber + ".jpg"
                 };
                 
                 for (String path : possiblePaths) {
                     File tempFile = new File(path);
                     if (tempFile.exists()) {
                         imageFile = tempFile;
                         System.out.println("Found solution image at: " + tempFile.getAbsolutePath());
                         break;
                     }
                 }
                 
                 if (imageFile != null && imageFile.exists()) {
                     BufferedImage img = ImageIO.read(imageFile);
                     // Resize image to fit window
                     int maxWidth = 500;
                     int maxHeight = 200;
                     
                     double widthRatio = (double) maxWidth / img.getWidth();
                     double heightRatio = (double) maxHeight / img.getHeight();
                     double ratio = Math.min(widthRatio, heightRatio);
                     
                     int width = (int) (img.getWidth() * ratio);
                     int height = (int) (img.getHeight() * ratio);
                     
                     Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                     JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                     imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                     contentPanel.add(imageLabel);
                     contentPanel.add(Box.createVerticalStrut(20));
                     System.out.println("Solution image loaded: solution" + challengeNumber + ".jpg");
                 } else {
                     System.out.println("Solution image not found: solution" + challengeNumber + ".jpg");
                 }
             } catch (IOException e) {
                 System.out.println("Error loading solution image: " + e.getMessage());
             }
             
             // Solution text
             JTextArea solutionText = new JTextArea(solution);
             solutionText.setFont(new Font("Comic Sans MS", Font.PLAIN, 16)); // Larger font
             solutionText.setEditable(false);
             solutionText.setWrapStyleWord(true);
             solutionText.setLineWrap(true);
             solutionText.setBackground(new Color(240, 248, 255));
             solutionText.setMargin(new Insets(15, 15, 15, 15)); // Increased padding
             
             JScrollPane scrollPane = new JScrollPane(solutionText);
             scrollPane.setBorder(BorderFactory.createEmptyBorder());
             contentPanel.add(scrollPane);
             
             // Encouraging message
             contentPanel.add(Box.createVerticalStrut(20));
             JLabel encouragementLabel = new JLabel("Don't worry! Math takes practice. You'll get it next time!");
             encouragementLabel.setFont(new Font("Comic Sans MS", Font.ITALIC, 14));
             encouragementLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
             contentPanel.add(encouragementLabel);
             
             // Close button
             JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
             buttonPanel.setBackground(new Color(240, 248, 255));
             JButton closeDialogButton = createStyledButton("Got it!");
             closeDialogButton.addActionListener(e -> solutionDialog.dispose());
             buttonPanel.add(closeDialogButton);
             
             // Add panels to dialog
             solutionDialog.add(contentPanel, BorderLayout.CENTER);
             solutionDialog.add(buttonPanel, BorderLayout.SOUTH);
             
             // Disable game inputs
             challengeCompleted = true;
             disableInputs();
             
             // Show dialog
             solutionDialog.setVisible(true);
         }
     }
     
     /**
      * Restarts the challenge with a fresh timer and attempts.
      */
     private void restartChallenge() {
         // Reset game state
         stopTimer();
         remainingSeconds = 300;
         remainingAttempts = 3;
         challengeCompleted = false;
         attemptCount = 0; // Reset attempt count
         
         // Reset UI
         updateTimerDisplay();
         timerLabel.setForeground(BUTTON_COLOR);
         resultLabel.setText(" ");
         answerField.setText("");
         answerField.setEnabled(true);
         submitButton.setEnabled(true);
         
         // Restart timer
         startCountdownTimer();
     }
     
     /**
      * Disables input fields and submit button after challenge completion.
      */
     private void disableInputs() {
         answerField.setEnabled(false);
         submitButton.setEnabled(false);
     }
     
     /**
      * Creates a styled button with consistent appearance and hover effects.
      *
      * @param text The text to display on the button
      * @return A styled JButton
      */
     private JButton createStyledButton(String text) {
         JButton button = new JButton(text);
         button.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
         button.setBackground(BUTTON_COLOR);
         button.setForeground(BUTTON_TEXT_COLOR);
         button.setFocusPainted(false);
         button.setBorderPainted(false);
         button.setPreferredSize(new Dimension(120, 40));
         button.setCursor(new Cursor(Cursor.HAND_CURSOR));
         
         // Add hover effect
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
 }
 