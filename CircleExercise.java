import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Random;
import javax.swing.*;

/**
 * CircleExercise is a Swing-based interactive educational application for practicing
 * circle circumference and area calculations. It provides a timed exercise environment
 * with visual feedback and scoring system.
 * 
 * The application features:
 * - Timed exercises (3 minutes)
 * - Multiple attempt system (up to 3 attempts per question)
 * - Visual circle display with formulas
 * - Progress tracking and scoring
 * - Integration with main learning application
 * 
 * @author Ruiheng He
 * @version 1.0
 */
public class CircleExercise extends JPanel {
    /** Flag to track if circumference progress has been updated */
    private static boolean circumferenceProgressUpdated = false;
    
    /** Flag to track if area progress has been updated */
    private static boolean areaProgressUpdated = false;
    
    /** Flag to track if user answered correctly in current session */
    private boolean answeredCorrectlyThisSession = false;
    
    /** Timer duration in seconds (3 minutes) */
    private static final int TIMER_DURATION = 180;
    
    /** Maximum number of attempts allowed per question */
    private static final int MAX_ATTEMPTS = 3;
    
    /** PI constant used for calculations */
    private static final double PI = 3.14;
    
    /** Background color for the application */
    private static final Color BACKGROUND_COLOR = new Color(230, 240, 255);
    
    /** Button color consistent with other modules */
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    
    /** Color for drawing circles */
    private static final Color CIRCLE_COLOR = new Color(100, 181, 246);
    
    /** Color for measurement lines and annotations */
    private static final Color MEASUREMENT_COLOR = new Color(255, 87, 34);
    
    /** Font for title text */
    private static final Font TITLE_FONT = new Font("Comic Sans MS", Font.BOLD, 32);
    
    /** Font for main text elements */
    private static final Font MAIN_FONT = new Font("Comic Sans MS", Font.BOLD, 24);
    
    /** Font for question text */
    private static final Font QUESTION_FONT = new Font("Comic Sans MS", Font.BOLD, 28);
    
    /** Color for radius variable in formulas */
    private static final Color VAR_COLOR_R = new Color(255, 87, 34);
    
    /** Color for PI variable in formulas */
    private static final Color VAR_COLOR_PI = new Color(0, 150, 136);
    
    /** Color for number 2 in formulas */
    private static final Color VAR_COLOR_2 = new Color(156, 39, 176);
    
    /** Parent dialog reference */
    private JDialog parentDialog;
    
    /** Flag indicating if current exercise is for circumference (true) or area (false) */
    private boolean isCircumference;
    
    /** Current circle radius */
    private int radius;
    
    /** Current number of attempts for the question */
    private int attempts;
    
    /** Timer for the exercise */
    private Timer timer;
    
    /** Remaining time in seconds */
    private int timeLeft;
    
    /** Label displaying remaining time */
    private JLabel timerLabel;
    
    /** Label displaying current score */
    private JLabel scoreLabel;
    
    /** Text field for user input */
    private JTextField answerField;
    
    /** Label displaying the current question */
    private JLabel questionLabel;
    
    /** Custom panel for drawing circles and formulas */
    private CirclePanel circlePanel;
    
    /** Random number generator for creating questions */
    private Random random;
    
    /** Decimal formatter for displaying numbers */
    private DecimalFormat df;
    
    /** Flag indicating if current question uses radius (true) or diameter (false) */
    private boolean useRadius;
    
    /** Total accumulated score */
    private static int totalScore = 0;
    
    /** Score from previous session */
    private static int lastScore = 0;
    
    /** Flag indicating if circumference exercises are completed */
    private static boolean circumferenceCompleted = false;
    
    /** Flag indicating if area exercises are completed */
    private static boolean areaCompleted = false;
    
    /** Flag to track home button clicks */
    private static boolean returnToMainMenu = false;
    
    /** Reference to main application */
    private MainApp mainApp;
    
    /** Label for displaying results and feedback */
    private JLabel resultLabel;
    
    /**
     * Gets the current total score.
     * 
     * @return the total accumulated score
     */
    public static int getTotalScore() {
        return totalScore;
    }
    
    /**
     * Resets the score by storing current score as last score.
     */
    public static void resetScore() {
        lastScore = totalScore;
    }
    
    /**
     * Checks if circumference exercises are completed.
     * 
     * @return true if circumference exercises are completed
     */
    public static boolean isCircumferenceCompleted() {
        return circumferenceCompleted;
    }
    
    /**
     * Checks if area exercises are completed.
     * 
     * @return true if area exercises are completed
     */
    public static boolean isAreaCompleted() {
        return areaCompleted;
    }
    
    /**
     * Checks if all exercises (both circumference and area) are completed.
     * 
     * @return true if both circumference and area exercises are completed
     */
    public static boolean isAllCompleted() {
        return circumferenceCompleted && areaCompleted;
    }
    
    /**
     * Checks and resets the return to main menu flag.
     * 
     * @return true if returning to main menu, false otherwise
     */
    public static boolean isReturningToMainMenu() {
        boolean result = returnToMainMenu;
        returnToMainMenu = false; // Reset the flag
        return result;
    }
    
    /**
     * Constructs a new CircleExercise panel.
     * 
     * @param dialog the parent dialog
     * @param isCircumference true for circumference exercises, false for area exercises
     * @param mainApp reference to the main application
     */
    public CircleExercise(JDialog dialog, boolean isCircumference, MainApp mainApp) {
        this.parentDialog = dialog;
        this.isCircumference = isCircumference;
        this.mainApp = mainApp;
        this.random = new Random();
        this.df = new DecimalFormat("#.#");
        
        // Disable close button
        parentDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(30, 30));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 10, 40));
        
        // Create top panel
        JPanel topPanel = createTopPanel();
        
        // Create center panel (contains circle display and control panel)
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        // Create circle display panel
        circlePanel = new CirclePanel();
        circlePanel.setPreferredSize(new Dimension(1100, 600));
        circlePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15),
            BorderFactory.createLineBorder(new Color(200, 200, 255), 3, true)
        ));
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        
        // Create result display label
        resultLabel = new JLabel();
        resultLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28)); // Increase font size
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setForeground(new Color(33, 33, 33));
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(BACKGROUND_COLOR);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        resultPanel.add(resultLabel, BorderLayout.CENTER);
        
        // Add panels to center panel
        centerPanel.add(circlePanel, BorderLayout.CENTER);
        centerPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Add all panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(resultPanel, BorderLayout.NORTH);
        bottomPanel.add(new JPanel(), BorderLayout.CENTER); // Add empty panel to maintain layout
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Initialize new problem
        generateNewProblem();
        
        // Start timer
        startTimer();
        
        // Set initial focus to input field
        SwingUtilities.invokeLater(() -> answerField.requestFocusInWindow());
    }
    
    /**
     * Creates the top panel containing home button, timer, and score display.
     * 
     * @return the configured top panel
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create top-left home button
        JButton backButton = new JButton("Home");
        backButton.setFont(MAIN_FONT);
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.setBackground(BUTTON_COLOR);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            // Check if user answered at least one question correctly
            if (answeredCorrectlyThisSession) {
                if (isCircumference) {
                    circumferenceCompleted = true;
                    // Update progress only if not previously updated
                    if (!circumferenceProgressUpdated) {
                        mainApp.updateKeyStage2Progress(1, 2); // 25% * 1/2 = 12.5%
                        circumferenceProgressUpdated = true;
                    }
                } else {
                    areaCompleted = true;
                    // Update progress only if not previously updated
                    if (!areaProgressUpdated) {
                        mainApp.updateKeyStage2Progress(1, 2); // 25% * 1/2 = 12.5%
                        areaProgressUpdated = true;
                    }
                }
            }

            returnToMainMenu = true;
            timer.stop();
            parentDialog.dispose();
            mainApp.showMainMenu();
        });
        
        // Create timer panel
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        timerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel clockIcon = new JLabel("\u23F0");
        clockIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 32));
        
        timerLabel = new JLabel("Time remaining: 3:00");
        timerLabel.setFont(MAIN_FONT);
        
        // Create score panel
        scoreLabel = new JLabel("Total Score: " + totalScore);
        scoreLabel.setFont(MAIN_FONT);
        scoreLabel.setPreferredSize(new Dimension(200, 40));
        
        timerPanel.add(clockIcon);
        timerPanel.add(timerLabel);
        
        panel.add(backButton, BorderLayout.WEST);
        panel.add(timerPanel, BorderLayout.CENTER);
        panel.add(scoreLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates the control panel containing question display and input controls.
     * 
     * @return the configured control panel
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Add question label
        questionLabel = new JLabel();
        questionLabel.setFont(QUESTION_FONT);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add decimal places prompt
        JLabel decimalPrompt = new JLabel("Answer should be rounded to 2 decimal places");
        decimalPrompt.setFont(new Font("Comic Sans MS", Font.ITALIC, 18));
        decimalPrompt.setForeground(new Color(100, 100, 100));
        decimalPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        inputPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel answerLabel = new JLabel("Your answer: ");
        answerLabel.setFont(MAIN_FONT);
        
        answerField = new JTextField(12);
        answerField.setFont(MAIN_FONT);
        answerField.setPreferredSize(new Dimension(200, 40));
        // Add Enter key submit functionality
        answerField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkAnswer();
                }
            }
        });
        
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(MAIN_FONT);
        submitButton.setBackground(BUTTON_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setPreferredSize(new Dimension(150, 40));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> checkAnswer());
        
        inputPanel.add(answerLabel);
        inputPanel.add(answerField);
        inputPanel.add(submitButton);
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(questionLabel);
        panel.add(Box.createVerticalStrut(5)); // Small space between question and prompt
        panel.add(decimalPrompt); // Add the decimal prompt
        panel.add(Box.createVerticalStrut(20));
        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(10));
        
        return panel;
    }
    
    /**
     * Generates a new random problem for the user to solve.
     * Randomly selects whether to use radius or diameter as the given value.
     */
    private void generateNewProblem() {
        useRadius = random.nextBoolean();
        // Generate random number between 1-20 for either radius or diameter
        if (useRadius) {
            radius = random.nextInt(20) + 1;  // radius 1-20
        } else {
            int diameter = random.nextInt(20) + 1;  // diameter 1-20
            radius = diameter / 2;
            if (radius == 0) radius = 1;  // Ensure radius is never 0
        }
        attempts = 0;
        answerField.setText("");
        circlePanel.setShowFormula(false);
        circlePanel.setShowMeasurement(false);
        circlePanel.repaint();
        resultLabel.setText("");  // Clear result display
        
        String question = String.format("Given circle's %s = %d, please calculate the %s",
            useRadius ? "radius" : "diameter",
            useRadius ? radius : radius * 2,
            isCircumference ? "circumference" : "area");
        questionLabel.setText(question);
    }
    
    /**
     * Paints the component with a gradient background.
     * 
     * @param g the Graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Add gradient background
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(230, 240, 255),
                                           0, getHeight(), new Color(220, 230, 255));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
    /**
     * Checks the user's answer against the correct calculation.
     * Handles scoring, progress tracking, and feedback display.
     */
    private void checkAnswer() {
        System.out.println("Starting answer check...");
        try {
            double userAnswer = Double.parseDouble(answerField.getText());
            final double correctAnswer;
            
            if (isCircumference) {
                correctAnswer = 2 * PI * radius;
            } else {
                correctAnswer = PI * radius * radius;
            }
            
            // Use original calculation result without rounding
            System.out.println("User answer:" + userAnswer + ", Correct answer:" + correctAnswer);
            if (Math.abs(userAnswer - correctAnswer) < 0.01) {
                System.out.println("Answer is correct!");
                answeredCorrectlyThisSession = true;
                // Stop timer when answer is correct
                timer.stop();
                
                int score;
                if (attempts == 0) score = 3;
                else if (attempts == 1) score = 2;
                else score = 1;
                
                // Update score
                lastScore = totalScore;
                totalScore += score;
                int newScore = totalScore - lastScore;
                mainApp.addScore(newScore); // Only add the newly earned score
                scoreLabel.setText("Total Score: " + totalScore);
                
                // Update completion status and progress here
                if (isCircumference) {
                    circumferenceCompleted = true;
                    // Update progress only if not previously updated
                    if (!circumferenceProgressUpdated) {
                        mainApp.updateKeyStage2Progress(1, 2); // 25% * 1/2 = 12.5%
                        circumferenceProgressUpdated = true;
                    }
                } else {
                    areaCompleted = true;
                    // Update progress only if not previously updated
                    if (!areaProgressUpdated) {
                        mainApp.updateKeyStage2Progress(1, 2); // 25% * 1/2 = 12.5%
                        areaProgressUpdated = true;
                    }
                }
                
                // Show correct answer and calculation process
                circlePanel.setShowFormula(true);
                circlePanel.setShowMeasurement(true);
                circlePanel.repaint();
                
                // Show congratulation message
                resultLabel.setText("Congratulations! You got " + score + " points!");
                resultLabel.setForeground(new Color(76, 175, 80));
                
                // Delay 2 seconds before asking to continue
                Timer delayTimer = new Timer(2000, e -> {
                    ((Timer)e.getSource()).stop();
                    askToContinue(true);
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            } else {
                attempts++;
                if (attempts >= MAX_ATTEMPTS) {
                    // Stop timer when max attempts reached
                    timer.stop();

                    // Mark as completed even if answer is wrong - update progress here
                    answeredCorrectlyThisSession = true; // Consider as completed even with wrong answer

                    // Update completion status and progress
                    if (isCircumference) {
                        circumferenceCompleted = true;
                        // Update progress only if not previously updated
                        if (!circumferenceProgressUpdated) {
                            mainApp.updateKeyStage2Progress(1, 2); // 25% * 1/2 = 12.5%
                            circumferenceProgressUpdated = true;
                        }
                    } else {
                        areaCompleted = true;
                        // Update progress only if not previously updated
                        if (!areaProgressUpdated) {
                            mainApp.updateKeyStage2Progress(1, 2); // 25% * 1/2 = 12.5%
                            areaProgressUpdated = true;
                        }
                    }

                    // Show correct answer and calculation process
                    circlePanel.setShowFormula(true);
                    circlePanel.setShowMeasurement(true);
                    circlePanel.repaint();
                    
                    // Show wrong answer message with unrounded answer
                    resultLabel.setText("Wrong! The correct answer is: " + String.format("%.2f", correctAnswer));
                    resultLabel.setForeground(new Color(244, 67, 54));
                    
                    // Delay 2 seconds before asking to continue
                    System.out.println("Used all 3 attempts, starting delay timer...");
                    Timer delayTimer = new Timer(2000, e -> {
                        System.out.println("Timer triggered, asking to continue...");
                        ((Timer)e.getSource()).stop();
                        askToContinue(false);
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                    System.out.println("Delay timer started");
                } else {
                    // Show remaining attempts
                    resultLabel.setText("Wrong! " + (MAX_ATTEMPTS - attempts) + " attempts remaining");
                    resultLabel.setForeground(new Color(255, 152, 0));
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid number!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Asks the user if they want to continue with more exercises.
     * 
     * @param wasCorrect true if the previous answer was correct, false otherwise
     */
    private void askToContinue(boolean wasCorrect) {
        System.out.println("Entering askToContinue method, wasCorrect=" + wasCorrect);
        String questionType = isCircumference ? "circumference" : "area";
        Object[] options = {"Continue, please", "No, thanks"};
        int option = JOptionPane.showOptionDialog(
            this,
            wasCorrect ? 
                String.format("Correct! Do you still want to work out the %s of a circle?", questionType) : 
                String.format("Wrong answer. Do you still want to work out the %s of a circle?", questionType),
            "Continue, please",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (option == JOptionPane.YES_OPTION) {
            // Reset attempts and generate new problem
            attempts = 0;
            generateNewProblem();
            answerField.setText("");
            circlePanel.setShowFormula(false);
            circlePanel.setShowMeasurement(false);
            circlePanel.repaint();
            resultLabel.setText("");
            
            // Reset timer
            timer.stop();
            timeLeft = TIMER_DURATION;
            updateTimerLabel();
            timer.start();
        } else {
            // Progress and status already updated in checkAnswer or startTimer methods
            timer.stop();
            returnToMainMenu = true; // Set flag when choosing to return to main menu
            parentDialog.dispose();
            mainApp.showGeometryLearningSystem(); // Return to GeometryLearningSystem interface
        }
    }
    
    /**
     * Starts the exercise timer and handles time-up scenarios.
     */
    private void startTimer() {
        timeLeft = TIMER_DURATION;
        timer = new Timer(1000, e -> {
            timeLeft--;
            updateTimerLabel();
            if (timeLeft <= 0) {
                timer.stop();
                final double correctAnswer = isCircumference ? 2 * PI * radius : PI * radius * radius;
                final double roundedCorrectAnswer = Math.round(correctAnswer * 10.0) / 10.0;
                
                // Time's up, also counts as completing a question - update status and progress
                answeredCorrectlyThisSession = true;

                // Update completion status and progress
                if (isCircumference) {
                    circumferenceCompleted = true;
                    // Update progress only if not previously updated
                    if (!circumferenceProgressUpdated) {
                        mainApp.updateKeyStage2Progress(1, 2); // 25% * 1/2 = 12.5%
                        circumferenceProgressUpdated = true;
                    }
                } else {
                    areaCompleted = true;
                    // Update progress only if not previously updated
                    if (!areaProgressUpdated) {
                        mainApp.updateKeyStage2Progress(1, 2); // 25% * 1/2 = 12.5%
                        areaProgressUpdated = true;
                    }
                }

                // Immediately show correct answer and calculation process
                circlePanel.setShowFormula(true);
                circlePanel.setShowMeasurement(true);
                circlePanel.repaint();
                
                // Display timeout message
                resultLabel.setText("Time's up! The correct answer is: " + df.format(roundedCorrectAnswer));
                resultLabel.setForeground(new Color(244, 67, 54));
                
                // Delay 3 seconds before asking to continue
                Timer delayTimer = new Timer(2000, ev -> {
                    ((Timer)ev.getSource()).stop();
                    askToContinue(false);
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        });
        timer.start();
    }
    
    /**
     * Updates the timer label display with current remaining time.
     */
    private void updateTimerLabel() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timerLabel.setText(String.format("Time remaining: %d:%02d", minutes, seconds));
    }
    
    /**
     * Custom JPanel for drawing circles, measurements, and mathematical formulas.
     * Provides animated transitions and visual feedback for the exercises.
     */
    private class CirclePanel extends JPanel {
        /** Flag to control formula display */
        private boolean showFormula;
        
        /** Flag to control measurement display */
        private boolean showMeasurement;
        
        /** Color for formula text */
        private final Color FORMULA_COLOR = new Color(96, 125, 139);
        
        /** Current offset for slide animation */
        private double currentOffset = 0;
        
        /** Timer for slide animation */
        private Timer slideTimer;
        
        /** Target offset for animation */
        private static final int TARGET_OFFSET = 100;
        
        /** Animation duration in milliseconds */
        private static final int ANIMATION_DURATION = 300;
        
        /** Timer delay for approximately 60fps */
        private static final int TIMER_DELAY = 16;
        
        /**
         * Constructs a new CirclePanel with animation capabilities.
         */
        public CirclePanel() {
            setPreferredSize(new Dimension(700, 450));
            setBackground(Color.WHITE);
            
            // Initialize slide animation timer
            slideTimer = new Timer(TIMER_DELAY, e -> {
                if (showFormula && currentOffset < TARGET_OFFSET) {
                    // Use easing function for smoother animation
                    currentOffset += (TARGET_OFFSET - currentOffset) * 0.2;
                    if (TARGET_OFFSET - currentOffset < 0.5) {
                        currentOffset = TARGET_OFFSET;
                        ((Timer)e.getSource()).stop();
                    }
                } else if (!showFormula && currentOffset > 0) {
                    // Reverse slide animation
                    currentOffset *= 0.8;
                    if (currentOffset < 0.5) {
                        currentOffset = 0;
                        ((Timer)e.getSource()).stop();
                    }
                }
                repaint();
            });
        }
        
        /**
         * Sets whether to show the formula with slide animation.
         * 
         * @param show true to show formula, false to hide
         */
        public void setShowFormula(boolean show) {
            this.showFormula = show;
            // Start slide animation
            if (!slideTimer.isRunning()) {
                slideTimer.start();
            }
        }
        
        /**
         * Sets whether to show measurements on the circle.
         * 
         * @param show true to show measurements, false to hide
         */
        public void setShowMeasurement(boolean show) {
            this.showMeasurement = show;
        }
        
        /**
         * Paints the circle, measurements, and formulas with animations.
         * 
         * @param g the Graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Calculate circle position using current offset
            int baseX = getWidth() / 2;
            int centerX = baseX + (int)currentOffset;
            int centerY = getHeight() / 2;
            int pixelRadius = (int)(getHeight() * 0.425);
            
            // Draw decorative background
            g2d.setColor(new Color(240, 248, 255));
            g2d.fillOval(centerX - pixelRadius - 10, centerY - pixelRadius - 10,
                        (pixelRadius + 10) * 2, (pixelRadius + 10) * 2);
            
            // Draw circle
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.setColor(CIRCLE_COLOR);
            g2d.drawOval(centerX - pixelRadius, centerY - pixelRadius, pixelRadius * 2, pixelRadius * 2);
            
            // Draw center point
            g2d.setColor(MEASUREMENT_COLOR);
            int dotSize = 12;
            g2d.fillOval(centerX - dotSize/2, centerY - dotSize/2, dotSize, dotSize);
            
            // Draw measurement lines
            g2d.setColor(MEASUREMENT_COLOR);
            g2d.setStroke(new BasicStroke(2.5f));
            
            if (!useRadius) {
                // Draw diameter line
                g2d.drawLine(centerX - pixelRadius, centerY, centerX + pixelRadius, centerY);
                drawArrow(g2d, centerX - pixelRadius, centerY, centerX + pixelRadius, centerY);
                if (showMeasurement) {
                    g2d.setFont(MAIN_FONT.deriveFont(28f)); // Increase font size
                    String measurement = "D = " + (radius * 2);
                    g2d.drawString(measurement, centerX - g2d.getFontMetrics().stringWidth(measurement)/2, centerY - 30);
                }
            } else {
                // Draw radius line
                g2d.drawLine(centerX, centerY, centerX + pixelRadius, centerY);
                drawArrow(g2d, centerX, centerY, centerX + pixelRadius, centerY);
                if (showMeasurement) {
                    g2d.setFont(MAIN_FONT.deriveFont(28f)); // Increase font size
                    String measurement = "R = " + radius;
                    g2d.drawString(measurement, centerX + pixelRadius/2 - g2d.getFontMetrics().stringWidth(measurement)/2, centerY - 30);
                }
            }
            
            if (showFormula) {
                // Use larger font
                g2d.setFont(MAIN_FONT.deriveFont(32f));
                int formulaY = getHeight() - 200;
                int calculationY = getHeight() - 150;
                int resultY = getHeight() - 100;
                
                // Draw formula title
                String formulaTitle = isCircumference ? "Circumference" : "Area";
                g2d.setColor(new Color(0, 100, 200));
                g2d.drawString(formulaTitle, 100, formulaY);
                
                // Draw formula
                int x = 100;
                
                // Draw left side of equation
                g2d.setColor(Color.BLACK);
                g2d.drawString(isCircumference ? "C = " : "A = ", x, calculationY);
                x += g2d.getFontMetrics().stringWidth(isCircumference ? "C = " : "A = ");
                
                if (isCircumference) {
                    // Draw number 2
                    g2d.setColor(VAR_COLOR_2);
                    g2d.drawString("2", x, calculationY);
                    x += g2d.getFontMetrics().stringWidth("2 ");
                    
                    // Draw π
                    g2d.setColor(VAR_COLOR_PI);
                    g2d.drawString("π", x, calculationY);
                    x += g2d.getFontMetrics().stringWidth("π ");
                    
                    // Draw r
                    g2d.setColor(VAR_COLOR_R);
                    g2d.drawString("r", x, calculationY);
                } else {
                    // Draw π
                    g2d.setColor(VAR_COLOR_PI);
                    g2d.drawString("π", x, calculationY);
                    x += g2d.getFontMetrics().stringWidth("π ");
                    
                    // Draw r²
                    g2d.setColor(VAR_COLOR_R);
                    g2d.drawString("r²", x, calculationY);
                }
                
                // Draw calculation process
                g2d.setFont(MAIN_FONT.deriveFont(28f)); // Slightly smaller font for calculation
                x = 100;
                g2d.setColor(Color.BLACK);
                g2d.drawString("=", x, resultY);
                x += g2d.getFontMetrics().stringWidth("= ");
                
                if (isCircumference) {
                    // Draw 2
                    g2d.setColor(VAR_COLOR_2);
                    String num2 = "2";
                    g2d.drawString(num2, x, resultY);
                    x += g2d.getFontMetrics().stringWidth(num2 + " ");
                    
                    // Draw ×
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("×", x, resultY);
                    x += g2d.getFontMetrics().stringWidth("× ");
                    
                    // Draw π value
                    g2d.setColor(VAR_COLOR_PI);
                    String piValue = String.format("%.2f", PI);
                    g2d.drawString(piValue, x, resultY);
                    x += g2d.getFontMetrics().stringWidth(piValue + " ");
                    
                    // Draw ×
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("×", x, resultY);
                    x += g2d.getFontMetrics().stringWidth("× ");
                    
                    // Draw radius value
                    g2d.setColor(VAR_COLOR_R);
                    g2d.drawString(String.valueOf(radius), x, resultY);
                } else {
                    // Draw π value
                    g2d.setColor(VAR_COLOR_PI);
                    String piValue = String.format("%.2f", PI);
                    g2d.drawString(piValue, x, resultY);
                    x += g2d.getFontMetrics().stringWidth(piValue + " ");
                    
                    // Draw ×
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("×", x, resultY);
                    x += g2d.getFontMetrics().stringWidth("× ");
                    
                    // Draw radius value and square
                    g2d.setColor(VAR_COLOR_R);
                    String radiusSquared = radius + "²";
                    g2d.drawString(radiusSquared, x, resultY);
                }
                
                // Draw final result
                g2d.setFont(MAIN_FONT.deriveFont(30f)); // Use larger font for final result
                x = 100;
                g2d.setColor(new Color(0, 100, 200));
                String finalResult = String.format("= %.2f", 
                    isCircumference ? 2 * PI * radius : PI * radius * radius);
                g2d.drawString(finalResult, x, resultY + 40);
            }
        }
        
        /**
         * Draws bidirectional arrows on a line segment.
         * 
         * @param g2d the Graphics2D context
         * @param x1 start x coordinate
         * @param y1 start y coordinate
         * @param x2 end x coordinate
         * @param y2 end y coordinate
         */
        private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            int arrowSize = 8;
            double angle = Math.atan2(y2 - y1, x2 - x1);
            
            // Draw arrow head
            int[] xPoints = new int[3];
            int[] yPoints = new int[3];
            
            xPoints[0] = x2;
            yPoints[0] = y2;
            xPoints[1] = x2 - arrowSize;
            yPoints[1] = y2 - arrowSize;
            xPoints[2] = x2 - arrowSize;
            yPoints[2] = y2 + arrowSize;
            
            g2d.fillPolygon(xPoints, yPoints, 3);
            
            // Draw reverse arrow
            xPoints[0] = x1;
            yPoints[0] = y1;
            xPoints[1] = x1 + arrowSize;
            yPoints[1] = y1 - arrowSize;
            xPoints[2] = x1 + arrowSize;
            yPoints[2] = y1 + arrowSize;
            
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }
}