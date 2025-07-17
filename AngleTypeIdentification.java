import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.*;

/**
 * A panel for practicing angle type identification.
 * This module allows users to input angles and identify their types (acute, right, obtuse, straight, or reflex).
 * It includes features for drawing angles, tracking progress, and scoring.
 *
 * @author Yiliang Zhang
 * @version 1.0
 * @since 2025
 * @see JPanel
 * @see JDialog
 * @see MainApp
 */
public class AngleTypeIdentification extends JPanel {
    // UI Components
    private JTextField angleInput;
    private JLabel angleImage;
    private JLabel resultLabel;
    private ButtonGroup angleTypeGroup;
    private JLabel scoreLabel;
    
    // State variables
    private int attempts = 0;
    private static List<String> recognizedTypes = new ArrayList<>();
    private int currentAngle;
    private boolean currentAngleProcessed = true;
    private JDialog parentDialog;
    private static MainApp mainApp;
    
    // Score system
    private static int totalScore = 0;
    private static int lastScore = 0;  // 添加变量记录上一次的分数
    
    // Constants
    private static final String[] ANGLE_TYPES = {"Acute Angle", "Right Angle", "Obtuse Angle", "Straight Angle", "Reflex Angle"};
    private static final int TOTAL_ANGLE_TYPES = ANGLE_TYPES.length;
    private static final int ANIMATION_SPEED = 5; // Angle increase per frame
    private static final int MAX_ATTEMPTS = 3;
    private static final int POINTS_FIRST_ATTEMPT = 3;
    private static final int POINTS_SECOND_ATTEMPT = 2;
    private static final int POINTS_THIRD_ATTEMPT = 1;
    private static final int NEXT_ANGLE_DELAY = 2000; // ms
    private static final int MIN_ANGLE = 10;
    private static final int MAX_ANGLE = 350;
    private static final int ANGLE_STEP = 10;
    
    // Animation related variables
    private Timer animationTimer;
    private int currentAnimationAngle = 0;
    private int targetAngle = 0;

    // Color constants
    private static final Color BACKGROUND_COLOR = new Color(230, 240, 255); // Light blue background
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color INPUT_BACKGROUND = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Color SUCCESS_COLOR = new Color(46, 139, 87);
    private static final Color ERROR_COLOR = new Color(220, 20, 60);
    private static final Color COMPLETED_COLOR = new Color(46, 204, 113);
    private static final Color INCOMPLETE_COLOR = new Color(189, 195, 199);

    /**
     * Constructor for the AngleTypeIdentification panel.
     * Initializes the UI components and sets up the parent dialog.
     * 
     * @param parentDialog The parent dialog window that contains this panel
     * @param mainApp Reference to the main application for score tracking
     */
    public AngleTypeIdentification(JDialog parentDialog, MainApp mainApp) {
        this.parentDialog = parentDialog;
        AngleTypeIdentification.mainApp = mainApp;
        
        restoreRecognizedTypes();
        initializeUI();
        setupWindowClosingHandler();
    }
    
    /**
     * Restores previously recognized angle types from the main application.
     * This method is called during initialization to maintain state between sessions.
     */
    private void restoreRecognizedTypes() {
        if (mainApp != null) {
            try {
                java.lang.reflect.Method getRecognizedTypesMethod = 
                    mainApp.getClass().getDeclaredMethod("getRecognizedAngleTypes");
                getRecognizedTypesMethod.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<String> savedTypes = (List<String>) getRecognizedTypesMethod.invoke(mainApp);
                if (savedTypes != null && !savedTypes.isEmpty()) {
                    recognizedTypes = new ArrayList<>(savedTypes);
                }
            } catch (Exception e) {
                System.out.println("Failed to restore recognized angle types: " + e.getMessage());
            }
        }
    }
    
    /**
     * Initializes the user interface components.
     * Sets up the layout, panels, and initial state of the UI.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        configureParentDialog();
        
        // Create and add panels
        JPanel topPanel = createTopPanel();
        JPanel mainContentPanel = createMainContentPanel();
        JPanel resultPanel = createResultPanel();
        
        add(topPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Configures the parent dialog window properties.
     * Sets size, background color, and close operation.
     */
    private void configureParentDialog() {
        if (parentDialog != null) {
            parentDialog.setSize(900, 750);
            parentDialog.getContentPane().setBackground(BACKGROUND_COLOR);
            parentDialog.getRootPane().setBackground(BACKGROUND_COLOR);
            parentDialog.setBackground(BACKGROUND_COLOR);
            // Disable close button
            parentDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        }
    }
    
    /**
     * Creates the top panel containing score display and input controls.
     * 
     * @return JPanel containing the top panel components
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        
        // Create score display
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        scorePanel.setBackground(BACKGROUND_COLOR);
        scoreLabel = new JLabel("Score: " + totalScore);
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(70, 130, 180));
        scorePanel.add(scoreLabel);
        topPanel.add(scorePanel, BorderLayout.EAST);
        
        // Create input panel
        JPanel inputPanel = createInputPanel();
        topPanel.add(inputPanel, BorderLayout.CENTER);
        
        return topPanel;
    }
    
    /**
     * Creates the main content panel containing the angle image and options.
     * 
     * @return JPanel containing the main content components
     */
    private JPanel createMainContentPanel() {
        JPanel mainContentPanel = new JPanel(new BorderLayout(15, 0));
        mainContentPanel.setBackground(BACKGROUND_COLOR);
        
        // Create image display panel
        JPanel imagePanel = createImagePanel();
        mainContentPanel.add(imagePanel, BorderLayout.CENTER);
        
        // Create options panel (on the right)
        JPanel optionsPanel = createOptionsPanel();
        mainContentPanel.add(optionsPanel, BorderLayout.EAST);
        
        return mainContentPanel;
    }
    
    /**
     * Sets up the window closing event handler.
     * Updates the main application score when the window is closed.
     */
    private void setupWindowClosingHandler() {
        if (parentDialog != null) {
            parentDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    updateMainAppScore();
                }
            });
        }
    }

    /**
     * Creates the input panel with Home button and angle input controls.
     * 
     * @return JPanel containing the input controls
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));

        // Create Home button panel
        JPanel homePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        homePanel.setBackground(BACKGROUND_COLOR);
        JButton homeButton = createStyledButton("Home");
        homeButton.setPreferredSize(new Dimension(80, 35));
        homeButton.addActionListener(e -> {
            if (parentDialog != null) {
                parentDialog.dispose();
            }
        });
        homePanel.add(homeButton);
        
        // Create title and input controls panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Angle Identification Practice", JLabel.LEFT);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        
        JPanel inputControlPanel = createAngleInputPanel();
        
        // Add to vertical panel
        controlPanel.add(titleLabel);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(inputControlPanel);
        
        // Add panels to main panel
        panel.add(homePanel, BorderLayout.WEST);
        panel.add(controlPanel, BorderLayout.CENTER);

        return panel;
    }
    
    /**
     * Creates the angle input panel with text field and submit button.
     * 
     * @return JPanel containing the angle input components
     */
    private JPanel createAngleInputPanel() {
        JPanel inputControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputControlPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel inputLabel = new JLabel("Enter angle (10-350, multiples of 10):");
        inputLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        inputLabel.setForeground(TEXT_COLOR);

        angleInput = new JTextField(8);
        angleInput.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        angleInput.setBackground(INPUT_BACKGROUND);
        angleInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Add Enter key listener
        angleInput.addActionListener(e -> processAngleInput());

        JButton submitButton = createStyledButton("Submit");
        submitButton.setPreferredSize(new Dimension(80, 35));
        submitButton.addActionListener(e -> processAngleInput());
        
        // Add components
        inputControlPanel.add(inputLabel);
        inputControlPanel.add(angleInput);
        inputControlPanel.add(submitButton);
        
        return inputControlPanel;
    }
    
    /**
     * Processes the angle input from the text field.
     * Validates the input and updates the UI accordingly.
     */
    private void processAngleInput() {
        // Check if current angle is already processed
        if (!currentAngleProcessed) {
            JOptionPane.showMessageDialog(AngleTypeIdentification.this,
                    "Please complete the current angle identification first.");
            return;
        }
        
        try {
            int angle = Integer.parseInt(angleInput.getText());
            
            // Check if angle is valid
            if (angle < MIN_ANGLE || angle > MAX_ANGLE || angle % ANGLE_STEP != 0) {
                JOptionPane.showMessageDialog(AngleTypeIdentification.this,
                        "Invalid input. Please enter an angle between " + MIN_ANGLE + 
                        " and " + MAX_ANGLE + " that is a multiple of " + ANGLE_STEP + ".");
                return;
            }
            
            // Check if angle type is already identified
            String angleType = getAngleType(angle);
            if (recognizedTypes.contains(angleType)) {
                JOptionPane.showMessageDialog(AngleTypeIdentification.this,
                    "You have already identified this type of angle (" + angleType + 
                    "). Please try a different angle type.",
                    "Angle Type Already Identified",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // All checks passed, proceed with the angle
            drawAngle(angle);
            currentAngle = angle;
            attempts = 0;
            resultLabel.setText("Please select the angle type:");
            resultLabel.setForeground(TEXT_COLOR);
            angleTypeGroup.clearSelection();
            currentAngleProcessed = false;
            
            // Enable all radio buttons
            enableAllRadioButtons(true);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(AngleTypeIdentification.this,
                    "Invalid input. Please enter a valid integer.");
        }
    }
    
    /**
     * Enables or disables all radio buttons in the angle type group.
     * 
     * @param enable true to enable the buttons, false to disable them
     */
    private void enableAllRadioButtons(boolean enable) {
        for (Enumeration<AbstractButton> buttons = angleTypeGroup.getElements(); buttons.hasMoreElements();) {
            buttons.nextElement().setEnabled(enable);
        }
    }

    /**
     * Creates the image panel for displaying angles.
     * 
     * @return JPanel containing the angle image display
     */
    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Create image label
        angleImage = new JLabel();
        angleImage.setHorizontalAlignment(JLabel.CENTER);
        panel.add(angleImage, BorderLayout.CENTER);
        
        // Add title
        JLabel titleLabel = new JLabel("Angle Diagram", JLabel.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    /**
     * Creates the options panel with angle type selection and completion status.
     * 
     * @return JPanel containing the options and status components
     */
    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(220, 0));

        // Add selection title
        JLabel typeLabel = new JLabel("Select angle type:");
        typeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        typeLabel.setForeground(TEXT_COLOR);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(typeLabel);
        panel.add(Box.createVerticalStrut(15));

        // Add angle type selection radio buttons
        panel.add(createAngleTypeSelectionPanel());
        
        // Add some space
        panel.add(Box.createVerticalStrut(20));
        
        // Add completion status section
        JLabel completionLabel = new JLabel("Completion Status:");
        completionLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        completionLabel.setForeground(TEXT_COLOR);
        completionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(completionLabel);
        panel.add(Box.createVerticalStrut(10));
        
        // Add scrollable completion status panel
        panel.add(createCompletionStatusPanel());
        
        // Add vertical filler space
        panel.add(Box.createVerticalGlue());

        return panel;
    }
    
    /**
     * Creates the panel with angle type radio buttons.
     * 
     * @return JPanel containing the radio button selection components
     */
    private JPanel createAngleTypeSelectionPanel() {
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
        selectionPanel.setBackground(BACKGROUND_COLOR);
        selectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        angleTypeGroup = new ButtonGroup();
        
        for (String type : ANGLE_TYPES) {
            JRadioButton radioButton = new JRadioButton(type);
            radioButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
            radioButton.setForeground(TEXT_COLOR);
            radioButton.setBackground(BACKGROUND_COLOR);
            radioButton.addActionListener(new AngleTypeSelectionListener());
            radioButton.setEnabled(false); // Initially disable
            radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            angleTypeGroup.add(radioButton);
            selectionPanel.add(radioButton);
            selectionPanel.add(Box.createVerticalStrut(10));
        }
        
        return selectionPanel;
    }
    
    /**
     * Creates the scrollable completion status panel.
     * 
     * @return JScrollPane containing the completion status components
     */
    private JScrollPane createCompletionStatusPanel() {
        JPanel completionPanel = new JPanel();
        completionPanel.setLayout(new BoxLayout(completionPanel, BoxLayout.Y_AXIS));
        completionPanel.setBackground(BACKGROUND_COLOR);
        completionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create scrollable panel
        JScrollPane scrollPane = new JScrollPane(completionPanel);
        scrollPane.setPreferredSize(new Dimension(200, 200));
        scrollPane.setMaximumSize(new Dimension(200, 200));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add status items for each angle type
        for (String type : ANGLE_TYPES) {
            completionPanel.add(createCompletionStatusItem(type));
            completionPanel.add(Box.createVerticalStrut(3));
        }
        
        return scrollPane;
    }
    
    /**
     * Creates a single completion status item.
     * 
     * @param type The angle type to create the status item for
     * @return JPanel containing the status item components
     */
    private JPanel createCompletionStatusItem(String type) {
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        typePanel.setBackground(BACKGROUND_COLOR);
        typePanel.setMaximumSize(new Dimension(200, 30));
        
        // Create status icon
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color color = recognizedTypes.contains(type) ? COMPLETED_COLOR : INCOMPLETE_COLOR;
                g2d.setColor(color);
                g2d.fillOval(0, 0, 15, 15);
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(15, 15);
            }
        };
        
        JLabel typeLabel = new JLabel(type);
        typeLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        typeLabel.setForeground(TEXT_COLOR);
        
        // Add checkmark for completed types
        JLabel checkmarkLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (recognizedTypes.contains(type)) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2d.setColor(new Color(0, 128, 0));
                    g2d.setStroke(new BasicStroke(2));
                    
                    int[] xPoints = {0, 4, 12};
                    int[] yPoints = {6, 12, 0};
                    g2d.drawPolyline(xPoints, yPoints, 3);
                    
                    g2d.dispose();
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(15, 15);
            }
        };
        
        typePanel.add(iconLabel);
        typePanel.add(Box.createHorizontalStrut(5));
        typePanel.add(typeLabel);
        typePanel.add(Box.createHorizontalStrut(5));
        typePanel.add(checkmarkLabel);
        
        return typePanel;
    }

    /**
     * Creates the result panel for displaying feedback.
     * 
     * @return JPanel containing the result display components
     */
    private JPanel createResultPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        resultLabel = new JLabel("Enter an angle and submit");
        resultLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        resultLabel.setForeground(TEXT_COLOR);
        resultLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(resultLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a styled button with hover effects.
     * 
     * @param text The text to display on the button
     * @return JButton with the specified styling
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
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
     * Checks if an angle is valid and not already identified.
     * 
     * @param angle The angle to validate
     * @return true if the angle is valid and not already identified
     */
    private boolean isValidAngle(int angle) {
        if (angle <= 0 || angle >= 360 || angle % 10 != 0) {
            return false;
        }
        
        String angleType = getAngleType(angle);
        return !recognizedTypes.contains(angleType);
    }

    /**
     * Starts the angle animation.
     * 
     * @param angle The target angle to animate to
     */
    private void drawAngle(int angle) {
        // Save target angle
        targetAngle = angle;
        
        // Initialize animation angle
        currentAnimationAngle = 0;
        
        // Stop existing animation if running
        stopAnimationTimer();
        
        // Create and start new animation timer
        animationTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Increase current animation angle
                currentAnimationAngle += ANIMATION_SPEED;
                
                // If the target angle is reached, stop animation
                if (currentAnimationAngle >= targetAngle) {
                    currentAnimationAngle = targetAngle;
                    ((Timer)e.getSource()).stop();
                }
                
                // Draw current angle
                drawAngleFrame(currentAnimationAngle);
            }
        });
        
        animationTimer.start();
    }
    
    /**
     * Stops the animation timer if it's running.
     */
    private void stopAnimationTimer() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }
    
    /**
     * Draws a single frame of the angle animation.
     * 
     * @param angle The current angle to draw
     */
    private void drawAngleFrame(int angle) {
        int width = 400;
        int height = 400;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Set anti-aliasing for smooth drawing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background and grid
        drawBackground(g2d, width, height);
        
        int centerX = width / 2;
        int centerY = height / 2;
        
        // Draw coordinate axis and labels
        drawCoordinateSystem(g2d, centerX, centerY, width, height);
        
        // Draw the angle rays
        drawAngleRays(g2d, centerX, centerY, width, height, angle);
        
        // Draw angle arc and text
        drawAngleArcAndText(g2d, centerX, centerY, angle);
        
        g2d.dispose();
        angleImage.setIcon(new ImageIcon(image));
    }
    
    /**
     * Draws the background and grid.
     * 
     * @param g2d The graphics context to draw with
     * @param width The width of the drawing area
     * @param height The height of the drawing area
     */
    private void drawBackground(Graphics2D g2d, int width, int height) {
        // Fill background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // Draw grid
        g2d.setColor(new Color(240, 240, 240));
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{2}, 0));
        
        int centerX = width / 2;
        int centerY = height / 2;
        
        // Horizontal grid lines
        for (int i = centerY % 20; i < height; i += 20) {
            g2d.drawLine(0, i, width, i);
        }
        
        // Vertical grid lines
        for (int i = centerX % 20; i < width; i += 20) {
            g2d.drawLine(i, 0, i, height);
        }
    }
    
    /**
     * Draws the coordinate system (axis and labels).
     * 
     * @param g2d The graphics context to draw with
     * @param centerX The x-coordinate of the center
     * @param centerY The y-coordinate of the center
     * @param width The width of the drawing area
     * @param height The height of the drawing area
     */
    private void drawCoordinateSystem(Graphics2D g2d, int centerX, int centerY, int width, int height) {
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        g2d.drawString("y", centerX + 5, centerY - height/3 - 10);
        g2d.drawString("O", centerX - 15, centerY + 15);
    }
    
    /**
     * Draws the angle rays (fixed vertical and rotating).
     * 
     * @param g2d The graphics context to draw with
     * @param centerX The x-coordinate of the center
     * @param centerY The y-coordinate of the center
     * @param width The width of the drawing area
     * @param height The height of the drawing area
     * @param angle The current angle to draw
     */
    private void drawAngleRays(Graphics2D g2d, int centerX, int centerY, int width, int height, int angle) {
        g2d.setColor(TEXT_COLOR);
        g2d.setStroke(new BasicStroke(3));
        
        // Draw fixed vertical ray
        g2d.drawLine(centerX, centerY, centerX, centerY - height/3);
        
        // Draw rotating ray
        double radians = Math.toRadians(angle);
        int endX = (int) (centerX + (width / 3) * Math.sin(radians));
        int endY = (int) (centerY - (height / 3) * Math.cos(radians));
        g2d.drawLine(centerX, centerY, endX, endY);
        
        // Only draw the x label at the final angle
        if (angle == targetAngle) {
            g2d.drawString("x", endX + 5, endY + 5);
        }
    }
    
    /**
     * Draws the angle arc and angle text.
     * 
     * @param g2d The graphics context to draw with
     * @param centerX The x-coordinate of the center
     * @param centerY The y-coordinate of the center
     * @param angle The current angle to draw
     */
    private void drawAngleArcAndText(Graphics2D g2d, int centerX, int centerY, int angle) {
        // Draw the arc
        int arcRadius = 45;
        int startAngle = 90;
        int arcAngle = -angle;
        g2d.setStroke(new BasicStroke(2));
        g2d.drawArc(centerX - arcRadius, centerY - arcRadius, 2 * arcRadius, 2 * arcRadius, startAngle, arcAngle);
        
        // Draw angle value text
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        String angleText = angle + "°";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(angleText);
        
        // Position text based on angle quadrant
        int textX, textY;
        if (angle < 90) {
            // First quadrant
            textX = centerX + arcRadius / 2;
            textY = centerY - arcRadius / 2;
        } else if (angle < 180) {
            // Second quadrant
            textX = centerX - textWidth - arcRadius / 2;
            textY = centerY - arcRadius / 2;
        } else if (angle < 270) {
            // Third quadrant
            textX = centerX - textWidth - arcRadius / 2;
            textY = centerY + arcRadius + 5;
        } else {
            // Fourth quadrant
            textX = centerX + arcRadius / 2;
            textY = centerY + arcRadius + 5;
        }
        
        // Draw text background for better visibility
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRoundRect(textX - 5, textY - fm.getAscent(), textWidth + 10, fm.getHeight(), 5, 5);
        
        // Draw angle text
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(angleText, textX, textY);
    }

    /**
     * Determines the type of angle based on its measure.
     * 
     * @param angle The angle measure in degrees
     * @return String representing the angle type
     */
    private String getAngleType(int angle) {
        if (angle > 0 && angle < 90) {
            return "Acute Angle";
        } else if (angle == 90) {
            return "Right Angle";
        } else if (angle > 90 && angle < 180) {
            return "Obtuse Angle";
        } else if (angle == 180) {
            return "Straight Angle";
        } else {
            return "Reflex Angle";
        }
    }
    
    /**
     * Updates the completion status display.
     */
    private void updateCompletionStatus() {
        repaint();
    }
    
    /**
     * Gets the total score for the angle identification practice.
     * 
     * @return The total score achieved
     */
    public static int getTotalScore() {
        return totalScore;
    }
    
    /**
     * Resets the score tracking.
     */
    public static void resetScore() {
        lastScore = totalScore;
    }
    
    /**
     * Gets the list of recognized angle types.
     * 
     * @return A new ArrayList containing the recognized angle types
     */
    public static List<String> getRecognizedTypes() {
        return new ArrayList<>(recognizedTypes);
    }
    
    /**
     * Sets the list of recognized angle types.
     * 
     * @param types The new list of recognized angle types
     */
    public static void setRecognizedTypes(List<String> types) {
        recognizedTypes = new ArrayList<>(types);
    }
    
    /**
     * Resets the list of recognized angle types.
     */
    public static void resetRecognizedTypes() {
        recognizedTypes.clear();
    }
    
    /**
     * Shows the completion window when all angle types are identified.
     */
    private void showCompletionWindow() {
        // Ensure animation timer is stopped
        stopAnimationTimer();
        
        SwingUtilities.invokeLater(() -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            if (parent == null) {
                parent = parentDialog;
            }
            
            AngleCompletionWindow completionWindow = new AngleCompletionWindow(parent, totalScore);
            completionWindow.setVisible(true);
            
            // Update main app score
            updateMainAppScore();
        });
    }
    
    /**
     * Updates the score in the main application.
     */
    private void updateMainAppScore() {
        try {
            for (Window window : Window.getWindows()) {
                if (window instanceof JFrame) {
                    JFrame frame = (JFrame) window;
                    if (frame.getTitle().contains("Shapeville")) {
                        // Found MainApp
                        Class<?> mainAppClass = frame.getClass();
                        java.lang.reflect.Method updateScoreMethod = 
                            mainAppClass.getDeclaredMethod("updateScore", int.class);
                        updateScoreMethod.setAccessible(true);
                        updateScoreMethod.invoke(frame, totalScore - lastScore);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to update main window score: " + e.getMessage());
        }
    }

    /**
     * Prepares the UI for the next angle input.
     */
    private void prepareNextAngle() {
        // Clear angle display
        angleImage.setIcon(null);
        
        // Reset input controls
        angleInput.setText("");
        angleInput.requestFocus();
        
        // Reset result display
        resultLabel.setText("Enter an angle and submit");
        resultLabel.setForeground(TEXT_COLOR);
        
        // Clear selection
        angleTypeGroup.clearSelection();
        
        // Update state
        currentAngleProcessed = true;
        
        // Enable all radio buttons
        enableAllRadioButtons(true);
        
        // Update completion status display
        updateCompletionStatus();
    }
    
    /**
     * Schedules the preparation of the next angle after a delay.
     */
    private void scheduleNextAngle() {
        new Timer(NEXT_ANGLE_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer)e.getSource()).stop();
                prepareNextAngle();
            }
        }).start();
    }
    
    /**
     * Action listener for angle type selection radio buttons.
     */
    private class AngleTypeSelectionListener implements ActionListener {
        /**
         * Handles the selection of an angle type.
         * 
         * @param e The action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            // If current angle is already processed, do nothing
            if (currentAngleProcessed) {
                return;
            }
            
            JRadioButton selectedButton = (JRadioButton) e.getSource();
            String selectedType = selectedButton.getText();
            String correctType = getAngleType(currentAngle);
            
            if (selectedType.equals(correctType)) {
                handleCorrectAnswer(correctType);
            } else {
                handleIncorrectAnswer(selectedButton, correctType);
            }
        }
        
        /**
         * Handles a correct answer selection.
         * 
         * @param correctType The correct angle type
         */
        private void handleCorrectAnswer(String correctType) {
            // Mark as processed to prevent re-selection
            currentAngleProcessed = true;
            
            // Disable all radio buttons
            enableAllRadioButtons(false);
            
            // Award points based on number of attempts
            int pointsEarned = getPointsForAttempt(attempts);
            totalScore += pointsEarned;
            scoreLabel.setText("Score: " + totalScore);
            
            // Show success message
            resultLabel.setText("<html>Congratulation! You've got the correct answer.<br>You earned " + 
                pointsEarned + " points! Current total: " + totalScore + "</html>");
            resultLabel.setForeground(SUCCESS_COLOR);
            
            // Update recognized types if needed
            if (!recognizedTypes.contains(correctType)) {
                recognizedTypes.add(correctType);
                mainApp.updateKeyStage1Progress(1, 5); // 1/5 = 10%
                updateCompletionStatus();
            }
            
            // Check if all types are recognized
            if (recognizedTypes.size() == TOTAL_ANGLE_TYPES) {
                showCompletionWindow();
            } else {
                scheduleNextAngle();
            }
        }
        
        /**
         * Handles an incorrect answer selection.
         * 
         * @param selectedButton The button that was selected
         * @param correctType The correct angle type
         */
        private void handleIncorrectAnswer(JRadioButton selectedButton, String correctType) {
            attempts++;
            
            // Disable the selected button to prevent repeated selection
            selectedButton.setEnabled(false);
            
            if (attempts >= MAX_ATTEMPTS) {
                // Out of attempts
                currentAngleProcessed = true;
                enableAllRadioButtons(false);
                
                // Show correct answer
                resultLabel.setText("<html>Wrong! The correct answer is " + correctType + "</html>");
                resultLabel.setForeground(ERROR_COLOR);
                
                // Update recognized types if needed
                if (!recognizedTypes.contains(correctType)) {
                    recognizedTypes.add(correctType);
                    mainApp.updateKeyStage1Progress(1, 5);
                    updateCompletionStatus();
                }
                
                // Check if all types are recognized
                if (recognizedTypes.size() == TOTAL_ANGLE_TYPES) {
                    showCompletionWindow();
                } else {
                    scheduleNextAngle();
                }
            } else {
                // Still have attempts remaining
                int remainingAttempts = MAX_ATTEMPTS - attempts;
                resultLabel.setText("<html>Incorrect. You have " + remainingAttempts + 
                    " more " + (remainingAttempts == 1 ? "attempt" : "attempts") + " remaining.</html>");
                resultLabel.setForeground(ERROR_COLOR);
            }
        }
        
        /**
         * Gets the points awarded based on the number of attempts.
         * 
         * @param attempts The number of attempts made
         * @return The points awarded
         */
        private int getPointsForAttempt(int attempts) {
            switch (attempts) {
                case 0: return POINTS_FIRST_ATTEMPT;
                case 1: return POINTS_SECOND_ATTEMPT;
                case 2: return POINTS_THIRD_ATTEMPT;
                default: return 0;
            }
        }
    }
}