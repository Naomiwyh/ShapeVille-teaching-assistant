import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Completion result window, displayed after the user identifies all four types of angles.
 * This dialog shows the user's final score and congratulatory message based on performance.
 * 
 * @author Yiliang Zhang
 * @version 1.0
 * @since 1.0
 */
public class AngleCompletionWindow extends JDialog implements AutoCloseable {
    /** Background color for the window panels. */
    private static final Color BACKGROUND_COLOR = new Color(230, 240, 255);
    /** Color for buttons in the interface. */
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    /** Text color for button labels. */
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    /** Color used for success messages and score display. */
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    
    /** Font used for buttons. */
    private static final Font BUTTON_FONT = new Font("Comic Sans MS", Font.BOLD, 14);
    /** Font used for score display. */
    private static final Font SCORE_FONT = new Font("Comic Sans MS", Font.BOLD, 24);
    /** Font used for congratulatory messages. */
    private static final Font MESSAGE_FONT = new Font("Comic Sans MS", Font.BOLD, 18);
    /** Font used for countdown text. */
    private static final Font COUNTDOWN_FONT = new Font("Comic Sans MS", Font.PLAIN, 14);
    /** Font used for emoticon display. */
    private static final Font EMOTICON_FONT = new Font("Dialog", Font.PLAIN, 48);
    
    /** Maximum possible score (5 angle types, 3 points max each). */
    private static final int MAX_SCORE = 15; // 5 angle types, 3 points max each
    
    /** Timer for automatic window closure. */
    private Timer countdownTimer;
    /** Number of seconds remaining before automatic closure. */
    private int remainingSeconds = 5;
    /** Label that displays the countdown. */
    private JLabel countdownLabel;
    
    /** Width of the completion window. */
    private static final int WINDOW_WIDTH = 600;
    /** Height of the completion window. */
    private static final int WINDOW_HEIGHT = 500;
    
    /**
     * Constructs a new completion window to display the user's final score.
     *
     * @param parent The parent window that owns this dialog
     * @param score The user's final score to display
     */
    public AngleCompletionWindow(Window parent, int score) {
        super(parent, "Congratulations!", Dialog.ModalityType.APPLICATION_MODAL);
        
        // Debug output to confirm score value
        System.out.println("Creating completion window, score: " + score);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 25));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Add content
        mainPanel.add(createNorthPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(score), BorderLayout.CENTER);
        mainPanel.add(createSouthPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Set size after adding components
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        
        // Start countdown
        startCountdown();
    }
    
    /**
     * Creates the top panel containing the close button.
     *
     * @return A JPanel containing the close button
     */
    private JPanel createNorthPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BACKGROUND_COLOR);
        
        JButton homeButton = createStyledButton("Close");
        homeButton.setPreferredSize(new Dimension(100, 35));
        homeButton.addActionListener(e -> dispose());
        
        panel.add(homeButton);
        return panel;
    }
    
    /**
     * Creates the center panel containing the score display and congratulatory message.
     *
     * @param score The user's final score
     * @return A JPanel containing the score display and message
     */
    private JPanel createCenterPanel(int score) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 20, 0);
        
        // Add emoji
        JLabel emoticonLabel = new JLabel(getEmoticonForScore(score), JLabel.CENTER);
        emoticonLabel.setFont(EMOTICON_FONT);
        panel.add(emoticonLabel, gbc);
        
        // Add score label
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 20, 20);
        JLabel scoreLabel = new JLabel("Your score: " + score + " / " + MAX_SCORE, JLabel.CENTER);
        scoreLabel.setFont(SCORE_FONT);
        scoreLabel.setForeground(SUCCESS_COLOR);
        panel.add(scoreLabel, gbc);
        
        // Add congratulatory message
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 15, 0);
        
        JLabel messageLabel = new JLabel("<html><div style='text-align:center;'>" + 
                getCongratsMessage(score) + "</div></html>", JLabel.CENTER);
        messageLabel.setFont(MESSAGE_FONT);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        panel.add(messageLabel, gbc);
        
        return panel;
    }
    
    /**
     * Creates the bottom panel containing the countdown timer.
     *
     * @return A JPanel containing the countdown label
     */
    private JPanel createSouthPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_COLOR);
        
        countdownLabel = new JLabel("Window will close in 5 seconds");
        countdownLabel.setFont(COUNTDOWN_FONT);
        panel.add(countdownLabel);
        
        return panel;
    }
    
    /**
     * Determines which emoticon to display based on the user's score.
     *
     * @param score The user's final score
     * @return A string containing an emoticon appropriate for the score
     */
    private String getEmoticonForScore(int score) {
        double percentage = (double) score / MAX_SCORE;
        
        if (percentage >= 0.9) return "🏆";  // Trophy
        else if (percentage >= 0.7) return "🎉";  // Celebration
        else if (percentage >= 0.5) return "👍";  // Thumbs up
        else if (percentage >= 0.3) return "🙂";  // Smile
        else return "💪";  // Keep going
    }
    
    /**
     * Generates a congratulatory message based on the user's score.
     *
     * @param score The user's final score
     * @return A string containing an appropriate message
     */
    private String getCongratsMessage(int score) {
        double percentage = (double) score / MAX_SCORE;
        
        if (percentage >= 0.9) return "Excellent! You're a master of angles!";
        else if (percentage >= 0.7) return "Great job! You understand angles well!";
        else if (percentage >= 0.5) return "Well done! Keep practicing!";
        else if (percentage >= 0.3) return "Good start! Keep learning!";
        else return "Keep studying! You'll improve!";
    }
    
    /**
     * Starts the 5-second countdown for automatic window closure.
     * When the countdown reaches zero, both this window and its parent are disposed.
     */
    private void startCountdown() {
        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            countdownLabel.setText("Window will close in " + remainingSeconds + " seconds");
            
            if (remainingSeconds <= 0) {
                stopCountdown();
                dispose();
                Window owner = getOwner();
                if (owner != null) {
                    owner.dispose();
                }
            }
        });
        countdownTimer.start();
    }
    
    /**
     * Stops the countdown timer if it's running.
     */
    private void stopCountdown() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }
    
    /**
     * Creates a styled button with consistent appearance and hover effects.
     *
     * @param text The text to display on the button
     * @return A styled JButton instance
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        button.addMouseListener(new ButtonHoverListener(button));
        
        return button;
    }
    
    /**
     * Mouse adapter class that handles hover effects for buttons.
     * 
     * @see MouseAdapter
     */
    private static class ButtonHoverListener extends MouseAdapter {
        /** The button to apply hover effects to. */
        private final JButton button;
        
        /**
         * Constructs a new hover listener for the specified button.
         *
         * @param button The button to apply hover effects to
         */
        public ButtonHoverListener(JButton button) {
            this.button = button;
        }
        
        /**
         * Darkens the button background when the mouse enters.
         *
         * @param e The mouse event
         */
        @Override
        public void mouseEntered(MouseEvent e) {
            button.setBackground(BUTTON_COLOR.darker());
        }
        
        /**
         * Restores the original button background when the mouse exits.
         *
         * @param e The mouse event
         */
        @Override
        public void mouseExited(MouseEvent e) {
            button.setBackground(BUTTON_COLOR);
        }
    }
    
    /**
     * Overrides the dispose method to ensure countdown timer is stopped.
     */
    @Override
    public void dispose() {
        stopCountdown();
        super.dispose();
    }
    
    /**
     * Implements the AutoCloseable interface close method.
     * Calls dispose() to release resources.
     */
    @Override
    public void close() {
        dispose();
    }
}