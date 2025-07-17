import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * A class that manages the compound shape area calculation challenges.
 * This class provides a UI for selecting different shape challenges and tracks completion status.
 * 
 * @author Yiliang Zhang
 * @version 1.0
 * @since 2025
 */
public class CompoundShapeAreaCalculation extends JPanel {
    /** Constants for UI components. */
    private static final Color BACKGROUND_COLOR = new Color(230, 240, 255);
    /** Button background color. */
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    /** Button text color. */
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    /** Color used to indicate completed challenges. */
    private static final Color COMPLETED_COLOR = new Color(46, 204, 113);
    /** Window width in pixels. */
    private static final int WINDOW_WIDTH = 1100;
    /** Window height in pixels. */
    private static final int WINDOW_HEIGHT = 800;
    
    /** Challenge configuration - numbers corresponding to available challenges. */
    private static final int[] CHALLENGE_NUMBERS = {2, 3, 4, 5, 8, 9}; // 6 challenges in total
    /** Tracks which challenges have been completed. */
    private static boolean[] completedStatus = new boolean[CHALLENGE_NUMBERS.length]; 
    
    /** Number of correctly answered challenges. */
    private static int correctAnswersCount = 0;
    /** Total score accumulated across all challenges. */
    private static int totalScore = 0;
    
    /** Parent dialog that contains this panel. */
    private JDialog parentDialog;
    /** Reference to the main application. */
    private static MainApp mainApp;
    /** Label displaying the current score. */
    private JLabel scoreLabel;
    /** Array of buttons for each challenge. */
    private JButton[] challengeButtons = new JButton[CHALLENGE_NUMBERS.length];

    /**
     * Constructor for the CompoundShapeAreaCalculation.
     * 
     * @param parentDialog The parent dialog
     * @param mainApp The main application
     */
    public CompoundShapeAreaCalculation(JDialog parentDialog, MainApp mainApp) {
        this.parentDialog = parentDialog;
        CompoundShapeAreaCalculation.mainApp = mainApp;
        
        initializeUI();
        configureDialog();
        // Disable close button
        parentDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        checkAllChallengesCompleted();
    }
    
    /**
     * Initialize the user interface components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(40, 40));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Create top panel with home button and title
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Create main content panel with image grid
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Configure the parent dialog properties.
     */
    private void configureDialog() {
        parentDialog.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        parentDialog.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        parentDialog.setMaximumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        parentDialog.pack();
        parentDialog.setLocationRelativeTo(null);
    }

    /**
     * Create the top panel with home button, title, and score.
     * 
     * @return The created panel
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        // Home button
        JButton homeButton = createStyledButton("Home");
        homeButton.addActionListener(e -> parentDialog.dispose());
        panel.add(homeButton, BorderLayout.WEST);

        // Create title and prompt
        panel.add(createTitleContainer(), BorderLayout.CENTER);
        
        // Add score display
        scoreLabel = new JLabel("Score: " + totalScore);
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(70, 130, 180));
        panel.add(scoreLabel, BorderLayout.EAST);

        return panel;
    }
    
    /**
     * Create a container for the title and prompt.
     * 
     * @return The created container
     */
    private JPanel createTitleContainer() {
        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setBackground(BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Area Calculation of Compound Shapes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleContainer.add(titleLabel);

        // Add prompt label
        JLabel promptLabel = new JLabel("Please select one of the following images to calculate the shape's area", SwingConstants.CENTER);
        promptLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        promptLabel.setForeground(new Color(44, 62, 80));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleContainer.add(Box.createVerticalStrut(10));
        titleContainer.add(promptLabel);
        
        return titleContainer;
    }

    /**
     * Create the main content panel with challenge buttons.
     * 
     * @return The created panel
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 40, 40)); // 2x3 grid for 6 challenges
        panel.setBackground(BACKGROUND_COLOR);

        // Add buttons for each challenge
        for (int i = 0; i < CHALLENGE_NUMBERS.length; i++) {
            int challengeNumber = CHALLENGE_NUMBERS[i];
            JButton imageButton = createImageButton(challengeNumber, i);
            panel.add(imageButton);
        }

        return panel;
    }

    /**
     * Create a button for a specific challenge.
     * 
     * @param imageNumber The challenge number
     * @param buttonIndex The index of the button
     * @return The created button
     */
    private JButton createImageButton(int imageNumber, int buttonIndex) {
        JButton button = new JButton();
        challengeButtons[buttonIndex] = button;  // Store button reference
        
        button.setPreferredSize(new Dimension(400, 400));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(BUTTON_COLOR, 2));
        button.setLayout(new BorderLayout());

        loadImageForButton(button, imageNumber);
        updateButtonCompletionStatus(button, buttonIndex);
        button.addActionListener(e -> openChallengeWindow(imageNumber, buttonIndex));
        
        return button;
    }
    
    /**
     * Load an image for a challenge button.
     * 
     * @param button The button to add the image to
     * @param imageNumber The image number to load
     */
    private void loadImageForButton(JButton button, int imageNumber) {
        try {
            File imageFile = findImageFile(imageNumber);
            
            if (imageFile == null || !imageFile.exists()) {
                button.setText("Image " + imageNumber + "\nNot Found");
                return;
            }

            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage == null) {
                System.out.println("Failed to read image: " + imageFile.getAbsolutePath());
                button.setText("Image " + imageNumber + "\nRead Error");
                return;
            }

            Image scaledImage = originalImage.getScaledInstance(225, 154, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            button.add(imageLabel, BorderLayout.CENTER);
            
            System.out.println("Successfully loaded image: " + imageNumber + ".jpg");
        } catch (IOException e) {
            System.out.println("Error loading image " + imageNumber + ".jpg: " + e.getMessage());
            e.printStackTrace();
            button.setText("Image " + imageNumber + "\nError: " + e.getMessage());
        }
    }
    
    /**
     * Find a valid image file from several possible paths.
     * 
     * @param imageNumber The image number to find
     * @return The image file, or null if not found
     */
    private File findImageFile(int imageNumber) {
        String[] possiblePaths = {
            "resources/images/" + imageNumber + ".jpg",
            "Group65/resources/images/" + imageNumber + ".jpg",
            "../resources/images/" + imageNumber + ".jpg"
        };
        
        for (String path : possiblePaths) {
            File tempFile = new File(path);
            if (tempFile.exists()) {
                System.out.println("Found image at: " + tempFile.getAbsolutePath());
                return tempFile;
            }
        }
        
        System.out.println("Image file not found for number: " + imageNumber);
        System.out.println("Tried paths: " + String.join(", ", possiblePaths));
        return null;
    }
    
    /**
     * Update button appearance based on completion status.
     * 
     * @param button The button to update
     * @param buttonIndex The index of the button
     */
    private void updateButtonCompletionStatus(JButton button, int buttonIndex) {
        // Clear any previous completion indicators
        for (Component comp : button.getComponents()) {
            if (comp != button.getComponent(0)) { // Keep the image
                button.remove(comp);
            }
        }
        
        // Check if completed
        if (completedStatus[buttonIndex]) {
            // Disable the button
            button.setEnabled(false);
            button.setBackground(new Color(240, 240, 240)); // Gray background
            
            // Add checkmark
            JLabel checkLabel = createCheckmark();
            button.add(checkLabel, BorderLayout.NORTH);
            
            // Add "Completed" text
            JLabel completedLabel = new JLabel("Completed", SwingConstants.CENTER);
            completedLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            completedLabel.setForeground(COMPLETED_COLOR);
            completedLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 8, 0));
            button.add(completedLabel, BorderLayout.SOUTH);
        } else {
            // Ensure incomplete buttons are enabled
            button.setEnabled(true);
            button.setBackground(Color.WHITE);
        }
        
        button.revalidate();
        button.repaint();
    }
    
    /**
     * Refresh all challenge buttons to show updated completion status.
     */
    private void refreshButtonAppearance() {
        for (int i = 0; i < challengeButtons.length; i++) {
            if (challengeButtons[i] != null) {
                updateButtonCompletionStatus(challengeButtons[i], i);
            }
        }
        
        // Update score display
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + totalScore);
        }
    }
    
    /**
     * Create a checkmark label.
     * 
     * @return The created checkmark label
     */
    private JLabel createCheckmark() {
        int size = 32;
        BufferedImage checkImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = checkImage.createGraphics();
        
        // Enable anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Set color
        g2.setColor(COMPLETED_COLOR);
        
        // Draw checkmark
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(6, 16, 12, 24);  // Left segment
        g2.drawLine(12, 24, 24, 8);  // Right segment
        
        g2.dispose();
        
        // Create icon label
        JLabel label = new JLabel(new ImageIcon(checkImage));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Add vertical padding
        return label;
    }

    /**
     * Mark a challenge as completed with a specific number of attempts.
     * 
     * @param challengeNumber Challenge number
     * @param isCorrect Whether the answer was correct
     * @param attempts Number of attempts taken
     */
    public static void markChallengeCompleted(int challengeNumber, boolean isCorrect, int attempts) {
        // Find the index of the challenge in our array
        int index = -1;
        for (int i = 0; i < CHALLENGE_NUMBERS.length; i++) {
            if (CHALLENGE_NUMBERS[i] == challengeNumber) {
                index = i;
                break;
            }
        }
        
        // Only update if the challenge exists and isn't already completed
        if (index != -1 && !completedStatus[index]) {
            completedStatus[index] = true;
            
            // Update progress regardless of correct/incorrect answer
            if (mainApp != null) {
                System.out.println("Updating progress for challenge " + challengeNumber);
                mainApp.updateKeyStage2Progress(1, 6); // 25% * 1/6
            }
            
            if (isCorrect) {
                correctAnswersCount++;
                // Add score based on attempts
                if (attempts == 1) {
                    totalScore += 6; // First attempt: 6 points
                } else if (attempts == 2) {
                    totalScore += 4; // Second attempt: 4 points
                } else if (attempts >= 3) {
                    totalScore += 2; // Third or more attempts: 2 points
                }
            }
            
            System.out.println("Challenge " + challengeNumber + " marked as completed. Total correct: " + 
                correctAnswersCount + ", Total score: " + totalScore + ", Attempts: " + attempts);
        } else {
            System.out.println("Challenge " + challengeNumber + " is no longer available or already completed.");
        }
    }
    
    /**
     * Mark a challenge as completed (maintained for backward compatibility).
     * 
     * @param challengeNumber Challenge number
     * @param isCorrect Whether the answer was correct
     */
    public static void markChallengeCompleted(int challengeNumber, boolean isCorrect) {
        // Default to third attempt (minimum score)
        markChallengeCompleted(challengeNumber, isCorrect, 3);
    }
    
    /**
     * Get the current total score.
     * 
     * @return The total score
     */
    public static int getTotalScore() {
        return totalScore;
    }
    
    /**
     * Check if all challenges are completed and show the result if they are.
     */
    private void checkAllChallengesCompleted() {
        // Add debug output
        System.out.println("Checking completion status:");
        for (int i = 0; i < CHALLENGE_NUMBERS.length; i++) {
            System.out.println("Challenge index " + i + " (image " + CHALLENGE_NUMBERS[i] + "): " + completedStatus[i]);
        }
        
        boolean allCompleted = true;
        for (int i = 0; i < CHALLENGE_NUMBERS.length; i++) {
            if (!completedStatus[i]) {
                allCompleted = false;
                break;
            }
        }
        
        if (allCompleted) {
            System.out.println("All challenges completed! Showing result window...");
            showCompletionResults();
        } else {
            System.out.println("Not all challenges completed yet.");
        }
    }
    
    /**
     * Show the completion results window.
     */
    private void showCompletionResults() {
        // Create and show result window on EDT
        SwingUtilities.invokeLater(() -> {
            // Create result window
            CompletionResultWindow resultWindow = new CompletionResultWindow(parentDialog, totalScore);
            
            // Add window close listener
            resultWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    // Close selection window when result window closes
                    parentDialog.dispose();
                }
            });
            
            // Show result window
            resultWindow.setVisible(true);
        });
    }

    /**
     * Open a challenge window for a specific challenge.
     * 
     * @param challengeNumber The challenge number
     * @param buttonIndex The button index
     */
    private void openChallengeWindow(int challengeNumber, int buttonIndex) {
        try {
            JDialog challengeDialog = new JDialog(parentDialog, "Compound Shape Challenge " + challengeNumber, true);
            challengeDialog.setSize(900, 700);
            challengeDialog.setLocationRelativeTo(parentDialog);
            
            try {
                // Try to create challenge instance using reflection
                Class<?> challengeClass = Class.forName("CompoundShapeChallenge");
                Constructor<?> constructor = challengeClass.getConstructor(int.class, JDialog.class);
                Object challenge = constructor.newInstance(challengeNumber, challengeDialog);
                
                challengeDialog.add((Component)challenge);
                
                // Add window close listener to refresh button state when challenge window closes
                challengeDialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        // Refresh the UI to show any completed challenges
                        refreshButtonAppearance();
                        // Check if all challenges are completed
                        checkAllChallengesCompleted();
                    }
                });
                
                challengeDialog.setVisible(true);
            } catch (ClassNotFoundException e) {
                showErrorDialog("Challenge class not found: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                showErrorDialog("Error opening challenge: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("General error in openChallengeWindow: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("General error: " + e.getMessage());
        }
    }
    
    /**
     * Show an error dialog.
     * 
     * @param message The error message
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        System.err.println(message);
    }

    /**
     * Create a styled button with the specified text.
     * 
     * @param text The button text
     * @return The created button
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 40));

        button.addMouseListener(new MouseAdapter() {
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