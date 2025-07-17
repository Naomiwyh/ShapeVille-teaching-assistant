import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;

/**
 * GeometryLearningSystem is a JPanel that provides an interactive learning interface
 * for circle geometry calculations including circumference and area exercises.
 * 
 * This class creates a main menu interface with two primary options:
 * - Circumference calculation exercises
 * - Area calculation exercises
 * 
 * The system tracks completion status and provides visual feedback for completed exercises.
 * 
 * @author Ruiheng He
 * @version 1.0
 * @since 1.0
 */
public class GeometryLearningSystem extends JPanel {
    /** The parent dialog containing this panel */
    private JDialog parentDialog;
    
    /** Reference to the main application */
    private MainApp mainApp;
    
    /** Background color for the interface */
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 245);
    
    /** Button color for interactive elements */
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    
    /** Main font used throughout the interface */
    private static final Font MAIN_FONT = new Font("Comic Sans MS", Font.BOLD, 18);
    
    /** Total score accumulated across all exercises */
    private static int totalScore = 0;
    
    // Progress tracking flags (currently commented out)
    // private static boolean circumferenceProgressUpdated = false;
    // private static boolean areaProgressUpdated = false;
    
    /**
     * Gets the current total score from all completed exercises.
     * 
     * @return the total score accumulated
     */
    public static int getTotalScore() {
        return totalScore;
    }
    
    /**
     * Constructs a new GeometryLearningSystem panel.
     * 
     * Sets up the main interface with title, navigation buttons, and exercise options.
     * Configures the layout, colors, and event handlers for the learning system.
     * 
     * @param dialog the parent JDialog that contains this panel
     * @param mainApp reference to the main application for navigation
     */
    public GeometryLearningSystem(JDialog dialog, MainApp mainApp) {
        this.parentDialog = dialog;
        this.mainApp = mainApp;
        dialog.setSize(1100, 800);
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(30, 30));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create top panel (contains title and return button)
        JPanel topPanel = new JPanel(new BorderLayout(20, 20));
        topPanel.setBackground(BACKGROUND_COLOR);
        
        // Add title
        JLabel titleLabel = new JLabel("Circle Area and Circumference Calculation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Add Home button
        JButton homeButton = new JButton("Home");
        homeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        homeButton.setBackground(BUTTON_COLOR);
        homeButton.setForeground(Color.WHITE);
        homeButton.setFocusPainted(false);
        homeButton.setBorderPainted(false);
        homeButton.setPreferredSize(new Dimension(120, 50));
        homeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeButton.addActionListener(e -> {
            // First close all child dialogs
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                if (window != mainApp.getFrame() && window.isShowing()) {
                    window.dispose();
                }
            }
            // Then close current dialog
            parentDialog.dispose();
            // Finally show main interface
            mainApp.showMainMenu();
        });
        topPanel.add(homeButton, BorderLayout.WEST);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));
        
        // Create circumference button
        JButton circumferenceButton = createLargeButton("Circumference");
        circumferenceButton.addActionListener(e -> openExercise(true));
        buttonPanel.add(circumferenceButton);
        
        // Create area button
        JButton areaButton = createLargeButton("Area");
        areaButton.addActionListener(e -> openExercise(false));
        buttonPanel.add(areaButton);
        
        // Add panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Add main panel to current panel
        add(mainPanel);
    }
    
    /**
     * Creates a large styled button for exercise selection.
     * 
     * Configures the button appearance based on completion status and adds appropriate
     * icons for circumference or area exercises.
     * 
     * @param text the text to display on the button ("Circumference" or "Area")
     * @return a configured JButton for the specified exercise type
     */
    private JButton createLargeButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(400, 200));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Check completion status and update button appearance
        if ((text.equals("Circumference") && CircleExercise.isCircumferenceCompleted()) ||
            (text.equals("Area") && CircleExercise.isAreaCompleted())) {
            updateCompletedButtonState(button, text);
        } else {
            setupNormalButtonState(button, text);
        }
        
        return button;
    }
    
    /**
     * Updates the button appearance to show completed state.
     * 
     * Changes the button to disabled state with completion message and styling.
     * Optionally updates progress tracking (currently commented out).
     * 
     * @param button the JButton to update
     * @param text the exercise type ("Circumference" or "Area")
     */
    private void updateCompletedButtonState(JButton button, String text) {
        button.setEnabled(false);
        button.setText("<html><center>" + text + "<br>Completed!</center></html>");
        button.setBackground(new Color(200, 200, 200));
        button.setForeground(new Color(76, 175, 80));
        button.setIcon(null);
        
        // Progress update logic (currently commented out)
        // Only update progress on first completion
        // if (mainApp != null) {
        //     if (text.equals("Circumference") && !circumferenceProgressUpdated) {
        //         mainApp.updateKeyStage2Progress(1, 2); // 25% * 1/2 = 12.5%
        //         circumferenceProgressUpdated = true;
        //     } else if (text.equals("Area") && !areaProgressUpdated) {
        //         mainApp.updateKeyStage2Progress(1, 2); // 25% * 1/2 = 12.5%
        //         areaProgressUpdated = true;
        //     }
        // }
    }
    
    /**
     * Sets up the normal (not completed) button state with appropriate icons.
     * 
     * Loads exercise-specific icons and configures button layout for active exercises.
     * Falls back to programmatically created icons if image resources are not available.
     * 
     * @param button the JButton to configure
     * @param text the exercise type ("Circumference" or "Area")
     */
    private void setupNormalButtonState(JButton button, String text) {
        if (text.equals("Circumference")) {
            URL imageUrl = getClass().getResource("/resources/images/c.png");
            if (imageUrl != null) {
                button.setIcon(new ImageIcon(imageUrl));
            } else {
                button.setIcon(createFallbackCircleIcon(true));
            }
        } else if (text.equals("Area")) {
            URL imageUrl = getClass().getResource("/resources/images/a.png");
            if (imageUrl != null) {
                button.setIcon(new ImageIcon(imageUrl));
            } else {
                button.setIcon(createFallbackCircleIcon(false));
            }
        }
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.TOP);
        button.setIconTextGap(5);
    }
    
    /**
     * Creates a fallback icon when original image resources cannot be loaded.
     * 
     * Programmatically generates a circle icon with different styling based on
     * whether it represents circumference (outlined) or area (filled) exercises.
     * 
     * @param isCircumference true for circumference icon, false for area icon
     * @return an ImageIcon containing the generated circle graphic
     */
    private ImageIcon createFallbackCircleIcon(boolean isCircumference) {
        // Create a transparent 100x100 image
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw circle shape
        g2d.setColor(new Color(0, 120, 215));
        g2d.fillOval(10, 10, 80, 80);
        
        // If circumference, draw bold circle outline
        if (isCircumference) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.drawOval(20, 20, 60, 60);
        } else {
            // If area, fill interior with lighter color
            g2d.setColor(new Color(200, 230, 255, 120));
            g2d.fillOval(20, 20, 60, 60);
        }
        
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * Opens the specified exercise type in a new dialog.
     * 
     * Checks completion status and either shows a completion message or launches
     * the appropriate exercise interface. Handles dialog lifecycle and navigation.
     * 
     * @param isCircumference true to open circumference exercise, false for area exercise
     */
    private void openExercise(boolean isCircumference) {
        // Check if already completed
        if ((isCircumference && CircleExercise.isCircumferenceCompleted()) ||
            (!isCircumference && CircleExercise.isAreaCompleted())) {
            JOptionPane.showMessageDialog(this,
                "You have already completed this exercise!",
                "Exercise Completed",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        parentDialog.dispose(); // Immediately close current interface
        JDialog exerciseDialog = new JDialog(mainApp.getFrame(), 
            isCircumference ? "Circle Circumference" : "Circle Area", true);
        exerciseDialog.setSize(1100, 800);
        exerciseDialog.setLocationRelativeTo(mainApp.getFrame());
        exerciseDialog.setResizable(false);
        exerciseDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        CircleExercise exercise = new CircleExercise(exerciseDialog, isCircumference, mainApp);
        exerciseDialog.add(exercise);
        
        exerciseDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            /**
             * Handles window closure event to manage navigation flow.
             * Creates new GeometryLearningSystem instance unless returning to main menu.
             * 
             * @param e the window event
             */
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // Only create new GeometryLearningSystem if not returning to main menu
                if (!CircleExercise.isReturningToMainMenu()) {
                    JDialog newDialog = new JDialog(mainApp.getFrame(), "Circle Area and Circumference Calculation", true);
                    newDialog.setSize(1100, 800);
                    newDialog.setLocationRelativeTo(mainApp.getFrame());
                    newDialog.setResizable(false);
                    newDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                    
                    GeometryLearningSystem newPanel = new GeometryLearningSystem(newDialog, mainApp);
                    newDialog.add(newPanel);
                    newDialog.setVisible(true);
                }
            }
        });
        
        exerciseDialog.setVisible(true);
    }
}