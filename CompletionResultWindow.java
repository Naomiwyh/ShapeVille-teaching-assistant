/**
 * CompletionResultWindow.java
 * 
 * @version 1.0
 * @author Yiliang Zhang
 * @since 2025
 * 
 * 
 * This class displays a congratulatory dialog window after completing all challenges.
 * It shows the user's final score and provides appropriate feedback based on performance.
 */

 import java.awt.*;
 import java.awt.event.*;
 import javax.swing.*;
 
 /**
  * CompletionResultWindow displays a modal dialog showing the user's final score
  * after completing all geometric area calculation challenges. The window includes
  * a congratulatory message, score display, and automatically closes after a short countdown.
  */
 public class CompletionResultWindow extends JDialog {
     // Constants for UI appearance
     private static final Color BACKGROUND_COLOR = new Color(240, 240, 245);
     private static final Color BUTTON_COLOR = new Color(70, 130, 180);
     private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
     private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
     private static final Font TITLE_FONT = new Font("Comic Sans MS", Font.BOLD, 24);
     private static final Font SCORE_FONT = new Font("Comic Sans MS", Font.BOLD, 24);
     private static final Font MESSAGE_FONT = new Font("Comic Sans MS", Font.BOLD, 18);
     private static final Font BUTTON_FONT = new Font("Comic Sans MS", Font.BOLD, 14);
     private static final Font COUNTDOWN_FONT = new Font("Comic Sans MS", Font.PLAIN, 14);
     
     // Constants for dialog behavior
     private static final int COUNTDOWN_SECONDS = 5;
     private static final int DIALOG_WIDTH = 500;
     private static final int DIALOG_HEIGHT = 350;
     private static final int MAX_SCORE = 36; // 6 challenges, max 6 points each
     
     // UI components
     private Timer countdownTimer;
     private int remainingSeconds = COUNTDOWN_SECONDS;
     private JLabel countdownLabel;
     
     /**
      * Constructs a new completion result window.
      * 
      * @param parent The parent window for this dialog
      * @param score The user's final score
      */
     public CompletionResultWindow(Window parent, int score) {
         super(parent, "Congratulations!", Dialog.ModalityType.APPLICATION_MODAL);
         
         // Window properties
         setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
         setLocationRelativeTo(parent);
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         setResizable(false);
         
         // Create main panel
         JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
         mainPanel.setBackground(BACKGROUND_COLOR);
         mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
         
         // Add content
         mainPanel.add(createNorthPanel(), BorderLayout.NORTH);
         mainPanel.add(createCenterPanel(score), BorderLayout.CENTER);
         mainPanel.add(createSouthPanel(), BorderLayout.SOUTH);
         
         add(mainPanel);
         
         // Setup keyboard shortcuts
         setupKeyboardShortcuts();
         
         // Start the countdown
         startCountdown();
     }
     
     /**
      * Sets up keyboard shortcuts for the dialog.
      * ESC, SPACE, or ENTER will close the window.
      */
     private void setupKeyboardShortcuts() {
         // ESC key to close window
         getRootPane().registerKeyboardAction(
             e -> dispose(),
             KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
             JComponent.WHEN_IN_FOCUSED_WINDOW
         );
         
         // SPACE key to close window
         getRootPane().registerKeyboardAction(
             e -> dispose(),
             KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
             JComponent.WHEN_IN_FOCUSED_WINDOW
         );
         
         // ENTER key to close window
         getRootPane().registerKeyboardAction(
             e -> dispose(),
             KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
             JComponent.WHEN_IN_FOCUSED_WINDOW
         );
     }
     
     /**
      * Creates the top panel containing the home button.
      * 
      * @return JPanel containing the home button
      */
     private JPanel createNorthPanel() {
         JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
         panel.setBackground(BACKGROUND_COLOR);
         
         JButton homeButton = createStyledButton("Home");
         homeButton.setPreferredSize(new Dimension(100, 35));
         homeButton.setMnemonic(KeyEvent.VK_H); // Alt+H shortcut
         homeButton.setToolTipText("Return to home screen (Alt+H)");
         homeButton.addActionListener(e -> dispose());
         
         panel.add(homeButton);
         return panel;
     }
     
     /**
      * Creates the center panel with score display and congratulatory message.
      * 
      * @param score The user's final score
      * @return JPanel containing the score and message components
      */
     private JPanel createCenterPanel(int score) {
         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.setBackground(BACKGROUND_COLOR);
         
         // Add emoticon
         String emoticon = getEmoticonForScore(score);
         JLabel emoticonLabel = new JLabel(emoticon, JLabel.CENTER);
         emoticonLabel.setFont(new Font("Dialog", Font.PLAIN, 72));
         emoticonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         panel.add(emoticonLabel);
         panel.add(Box.createVerticalStrut(20));
         
         // Add score label
         JLabel scoreLabel = new JLabel("Your Score: " + score + " / " + MAX_SCORE, JLabel.CENTER);
         scoreLabel.setFont(SCORE_FONT);
         scoreLabel.setForeground(SUCCESS_COLOR);
         scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         panel.add(scoreLabel);
         panel.add(Box.createVerticalStrut(20));
         
         // Add congratulatory message
         String message = getCongratsMessage(score);
         JLabel messageLabel = new JLabel(message, JLabel.CENTER);
         messageLabel.setFont(MESSAGE_FONT);
         messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         panel.add(messageLabel);
         
         return panel;
     }
     
     /**
      * Creates the bottom panel with the countdown timer display.
      * 
      * @return JPanel containing the countdown label
      */
     private JPanel createSouthPanel() {
         JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         panel.setBackground(BACKGROUND_COLOR);
         
         countdownLabel = new JLabel("Window will close in " + COUNTDOWN_SECONDS + " seconds");
         countdownLabel.setFont(COUNTDOWN_FONT);
         panel.add(countdownLabel);
         
         return panel;
     }
     
     /**
      * Determines the appropriate emoticon based on the user's score percentage.
      * 
      * @param score The user's final score
      * @return An emoticon string representing the performance level
      */
     private String getEmoticonForScore(int score) {
         double percentage = (double) score / MAX_SCORE;
         
         if (percentage >= 0.9) return "🏆";  // Trophy
         else if (percentage >= 0.7) return "🎉";  // Celebration
         else if (percentage >= 0.5) return "👍";  // Thumbs up
         else if (percentage >= 0.3) return "🙂";  // Smile
         else return "💪";  // Muscle/Effort
     }
     
     /**
      * Generates a congratulatory message based on the user's score percentage.
      * 
      * @param score The user's final score
      * @return A message string appropriate for the performance level
      */
     private String getCongratsMessage(int score) {
         double percentage = (double) score / MAX_SCORE;
         
         if (percentage >= 0.9) return "Amazing! You're a math genius!";
         else if (percentage >= 0.7) return "Great job! You have a good understanding of math!";
         else if (percentage >= 0.5) return "Good work! Keep practicing!";
         else if (percentage >= 0.3) return "You're improving! Keep going!";
         else return "Keep learning! Math takes practice!";
     }
     
     /**
      * Starts the automatic countdown timer to close the window.
      */
     private void startCountdown() {
         countdownTimer = new Timer(1000, e -> {
             remainingSeconds--;
             countdownLabel.setText("Window will close in " + remainingSeconds + " seconds");
             
             if (remainingSeconds <= 0) {
                 stopCountdown();
                 dispose();
             }
         });
         countdownTimer.setInitialDelay(1000); // Start countdown after first second
         countdownTimer.start();
     }
     
     /**
      * Stops the countdown timer and releases associated resources.
      */
     private void stopCountdown() {
         if (countdownTimer != null && countdownTimer.isRunning()) {
             countdownTimer.stop();
             countdownTimer = null;
         }
     }
     
     /**
      * Overrides the dispose method to ensure timer resources are properly released.
      */
     @Override
     public void dispose() {
         stopCountdown();
         super.dispose();
     }
     
     /**
      * Creates a styled button with consistent appearance and hover effects.
      * 
      * @param text The text to display on the button
      * @return A styled JButton
      */
     private JButton createStyledButton(String text) {
         JButton button = new JButton(text);
         button.setFont(BUTTON_FONT);
         button.setBackground(BUTTON_COLOR);
         button.setForeground(BUTTON_TEXT_COLOR);
         button.setFocusPainted(false);
         button.setBorderPainted(false);
         button.setCursor(new Cursor(Cursor.HAND_CURSOR));
         
         button.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseEntered(MouseEvent e) {
                 button.setBackground(BUTTON_COLOR.darker());
             }
             
             @Override
             public void mouseExited(MouseEvent e) {
                 button.setBackground(BUTTON_COLOR);
             }
         });
         
         return button;
     }
 }