import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;

/**
 * A practice application for calculating sector areas.
 * This class provides an interactive interface for students to practice
 * calculating the area of sectors with different angles and radii.
 * 
 * @author Honglu Xu
 * @version 1.0
 */
public class SectorCalculationPractice extends JPanel {
    private JDialog parentDialog;
    private Timer countdownTimer;
    private JLabel timerLabel;
    private JLabel questionLabel;
    private JTextField areaInput;
    private JLabel resultLabel;
    private JPanel sectorPanel;
    private JLabel scoreLabel;
    private static int totalScore = 0;
    private static int lastScore = 0;
    private boolean sectorSelected = false;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private static final String SELECTION_PANEL = "SELECTION";
    private static final String PRACTICE_PANEL = "PRACTICE";
    
    /**
     * Inner class representing sector data including angle, radius, unit and completion status.
     */
    private static class SectorData {
        int angle;
        double radius;
        String unit;
        boolean completed;
        int attempts; // Track the number of attempts for each sector
        int score;

        /**
         * Creates a new SectorData instance.
         * 
         * @param angle the angle of the sector in degrees
         * @param radius the radius of the sector
         * @param unit the unit of measurement (cm, ft, m, etc.)
         */
        SectorData(int angle, double radius, String unit) {
            this.angle = angle;
            this.radius = radius;
            this.unit = unit;
            this.completed = false;
            this.attempts = 0;
            this.score = 0;
        }
        
        /**
         * Resets the sector data to initial state.
         */
        void reset() {
            this.completed = false;
            this.attempts = 0;
            this.score = 0;
        }
    }

    private List<SectorData> sectors;
    private int currentSectorIndex = 0;
    private int currentAttempts = 0; // Current practice attempt count
    private int remainingSeconds = 300; // 5 minutes
    
    private static final Color BACKGROUND_COLOR = new Color(230, 240, 255);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    
    private List<BufferedImage> sectorImages;
    
    private static final double PI = 3.14;
    private static boolean[] sectorsCompleted = new boolean[8];
    private static boolean isGameCompleted = false;
    
    private static MainApp mainApp;
    private static boolean[] completedStatus = new boolean[8]; // 8 small tasks
    
    /**
     * Gets the total score achieved in the practice.
     * 
     * @return the total score
     */
    public static int getTotalScore() {
        return totalScore;
    }
    
    /**
     * Resets the score by saving current total as last score.
     */
    public static void resetScore() {
        lastScore = totalScore;
    }
    
    /**
     * Updates the score in the main application using reflection.
     * This method finds the MainApp window and calls its updateScore method.
     */
    private void updateMainAppScore() {
        try {
            // Use reflection to get updateScore method in MainApp instance
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
     * Constructor for SectorCalculationPractice.
     * 
     * @param parentDialog the parent dialog window
     * @param mainApp reference to the main application
     */
    public SectorCalculationPractice(JDialog parentDialog, MainApp mainApp) {
        this.parentDialog = parentDialog;
        SectorCalculationPractice.mainApp = mainApp;
        parentDialog.setSize(900, 700);
        // Remove close button
        parentDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        initializeData();
        setupUI();
    }
    
    /**
     * Initializes the sector data with 8 different sectors in fixed order.
     */
    private void initializeData() {
        // Initialize with 8 different sectors in fixed order
        sectors = new ArrayList<>(Arrays.asList(
            new SectorData(90, 8, "cm"),    // First: right angle sector
            new SectorData(130, 18, "ft"),  // Using feet
            new SectorData(240, 19, "cm"),
            new SectorData(110, 22, "ft"),  // Using feet
            new SectorData(100, 3.5, "m"),
            new SectorData(270, 8, "in"),   // Using inches
            new SectorData(280, 12, "yd"),  // Using yards
            new SectorData(250, 15, "mm")
        ));
        
        // Load sector images
        loadSectorImages();
    }
    
    /**
     * Loads sector images from the sectorgraph folder.
     * Creates default images if loading fails.
     */
    private void loadSectorImages() {
        sectorImages = new ArrayList<>();
        try {
            // Get the class loader for the current class
            ClassLoader classLoader = getClass().getClassLoader();
            
            for (int i = 1; i <= 8; i++) {
                try {
                    // Try to load image from resource folder
                    String imagePath = "sectorgraph/" + i + ".jpg";
                    java.io.InputStream is = classLoader.getResourceAsStream(imagePath);
                    
                    if (is != null) {
                        BufferedImage img = ImageIO.read(is);
                        sectorImages.add(img);
                        is.close();
                    } else {
                        // If resource loading fails, try loading from file system
                        File imageFile = new File("sectorgraph/" + i + ".jpg");
                        if (imageFile.exists()) {
                            BufferedImage img = ImageIO.read(imageFile);
                            sectorImages.add(img);
                        } else {
                            System.err.println("Cannot find image file: " + imagePath);
                            // Create a default sector image
                            BufferedImage defaultImage = createDefaultSectorImage();
                            sectorImages.add(defaultImage);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error loading image " + i + ": " + e.getMessage());
                    // Create a default sector image
                    BufferedImage defaultImage = createDefaultSectorImage();
                    sectorImages.add(defaultImage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading sector images!\nPlease ensure the sectorgraph folder exists and contains the required image files.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates a default sector image when actual images cannot be loaded.
     * 
     * @return a BufferedImage containing a default sector drawing
     */
    private BufferedImage createDefaultSectorImage() {
        // Create a default sector image
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Set anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw sector
        g2d.setColor(new Color(70, 130, 180));
        g2d.fillArc(10, 10, 180, 180, 0, 90);
        
        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawArc(10, 10, 180, 180, 0, 90);
        
        g2d.dispose();
        return image;
    }
    
    /**
     * Sets up the user interface with CardLayout for panel switching.
     */
    private void setupUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create main panel using CardLayout for interface switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create and add selection interface
        JPanel selectionPanel = createSelectionPanel();
        mainPanel.add(selectionPanel, SELECTION_PANEL);
        
        // Create and add practice interface
        JPanel practicePanel = createPracticePanel();
        mainPanel.add(practicePanel, PRACTICE_PANEL);
        
        // Add main panel to current panel
        add(mainPanel);
        
        // Show selection interface
        cardLayout.show(mainPanel, SELECTION_PANEL);
        
        // Initialize timer (but don't start it)
        setupTimer();
    }
    
    /**
     * Updates the selection panel by recreating it with current state.
     */
    private void updateSelectionPanel() {
        // Remove old panels (if they exist)
        for (Component comp : mainPanel.getComponents()) {
            mainPanel.remove(comp);
        }
        
        // Create new selection interface
        JPanel selectionPanel = createSelectionPanel();
        mainPanel.add(selectionPanel, SELECTION_PANEL);
        
        // Create new practice interface
        JPanel practicePanel = createPracticePanel();
        mainPanel.add(practicePanel, PRACTICE_PANEL);
        
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Creates the sector selection panel where users choose which sector to practice.
     * 
     * @return JPanel containing the selection interface
     */
    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setName("SELECTION_PANEL");
        
        // Create top panel with title and score
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        
        // Add HOME button
        JButton homeButton = createHomeButton();
        topPanel.add(homeButton, BorderLayout.WEST);
        
        // Create title
        JLabel titleLabel = new JLabel("Area Calculation of Sector", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 35));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Add instruction text
        String instructionText = isGameCompleted ? 
            "Congratulations! Click any sector to start a new game" : 
            "Click an uncompleted sector to start practice";
        JLabel instructionLabel = new JLabel(instructionText, SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        instructionLabel.setForeground(TEXT_COLOR);
        
        // Create title container
        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setBackground(BACKGROUND_COLOR);
        
        // Set title and instruction text to center alignment
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titleContainer.add(titleLabel);
        titleContainer.add(Box.createVerticalStrut(10)); // Add some vertical spacing
        titleContainer.add(instructionLabel);
        
        // Create a wrapper panel to ensure content is centered
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(BACKGROUND_COLOR);
        wrapperPanel.add(titleContainer, BorderLayout.CENTER);
        
        // Create score display
        scoreLabel = new JLabel("Total Score: " + totalScore, SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        scoreLabel.setForeground(new Color(70, 130, 180));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 30));
        
        topPanel.add(wrapperPanel, BorderLayout.CENTER);
        topPanel.add(scoreLabel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Create sector selection grid
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 30, 30));
        gridPanel.setBackground(BACKGROUND_COLOR);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        for (int i = 0; i < sectors.size(); i++) {
            final int index = i;
            JPanel sectorButtonPanel = createSectorButton(i + 1, index);
            gridPanel.add(sectorButtonPanel);
        }
        
        panel.add(gridPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a sector button for the selection grid.
     * 
     * @param number the sector number (1-8)
     * @param index the sector index (0-7)
     * @return JPanel containing the sector button
     */
    private JPanel createSectorButton(int number, int index) {
        JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        // Create a custom panel to display the sector
        SectorPreviewPanel preview = new SectorPreviewPanel(index, sectorsCompleted[index]);
        preview.setPreferredSize(new Dimension(150, 150));
        
        // Simplified label display
        JLabel numberLabel = new JLabel("Sector " + number, SwingConstants.CENTER);
        numberLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        numberLabel.setForeground(TEXT_COLOR);
        
        buttonPanel.add(preview, BorderLayout.CENTER);
        buttonPanel.add(numberLabel, BorderLayout.SOUTH);
        
        // If game is completed, all sectors can be clicked to start a new round
        if (isGameCompleted) {
            preview.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Mouse clicked on sector: " + index);
                    SwingUtilities.invokeLater(() -> {
                        resetGame();
                        selectSector(index);
                    });
                }
            });
        } else if (!sectorsCompleted[index]) {
            // If sector is not completed, it can be clicked
            preview.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Mouse clicked on sector: " + index);
                    SwingUtilities.invokeLater(() -> {
                        selectSector(index);
                    });
                }
            });
        }
        
        return buttonPanel;
    }
    
    /**
     * Creates the practice panel where users calculate sector areas.
     * 
     * @return JPanel containing the practice interface
     */
    private JPanel createPracticePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setName("PRACTICE_PANEL");
        
        // Create top panel with title, HOME button and timer
        JPanel topPanel = new JPanel(new BorderLayout(20, 20));
        topPanel.setBackground(BACKGROUND_COLOR);
        
        // Left side HOME button
        JButton homeButton = createHomeButton();
        topPanel.add(homeButton, BorderLayout.WEST);
        
        // Center title
        JLabel mainTitle = new JLabel("Sector Calculation", SwingConstants.CENTER);
        mainTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        mainTitle.setForeground(TEXT_COLOR);
        topPanel.add(mainTitle, BorderLayout.CENTER);
        
        // Right side timer
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(BACKGROUND_COLOR);
        
        // Add clock icon
        JLabel clockIconLabel = new JLabel(createClockIcon());
        rightPanel.add(clockIconLabel);
        
        // Add timer label
        timerLabel = new JLabel("Time: 5:00");
        timerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        timerLabel.setForeground(TEXT_COLOR);
        rightPanel.add(timerLabel);
        
        topPanel.add(rightPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Create sector display panel
        sectorPanel = new SectorDisplayPanel();
        // Set a larger preferred size to maintain sector proportions
        sectorPanel.setPreferredSize(new Dimension(600, 400));
        panel.add(sectorPanel, BorderLayout.CENTER);
        
        // Create input panel
        JPanel inputPanel = createInputPanel();
        panel.add(inputPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Selects a sector for practice and switches to the practice panel.
     * 
     * @param index the index of the sector to select
     */
    private void selectSector(int index) {
        try {
            System.out.println("Selecting sector: " + index);
            currentSectorIndex = index;
            sectorSelected = true;
            
            // Reset current attempt count - key fix
            currentAttempts = 0;
            
            // Ensure current sector's attempt count is also reset
            sectors.get(currentSectorIndex).attempts = 0;
            
            // Reset input fields
            if (areaInput != null) areaInput.setText("");
            if (resultLabel != null) resultLabel.setText(" ");
            
            // Reset and start timer
            remainingSeconds = 300;
            updateTimerLabel();
            if (countdownTimer != null) {
                if (countdownTimer.isRunning()) {
                    countdownTimer.stop();
                }
                countdownTimer.start();
            }
            
            // Update sector display
            if (sectorPanel instanceof SectorDisplayPanel) {
                ((SectorDisplayPanel) sectorPanel).updateInfoLabel();
                ((SectorDisplayPanel) sectorPanel).updateAttemptsLabel(); // Ensure attempt count label updates
            }
            
            // Switch to practice interface
            SwingUtilities.invokeLater(() -> {
                System.out.println("Switching to practice panel");
                if (mainPanel != null && cardLayout != null) {
                    // Recreate practice panel to update unit display
                    Component[] components = mainPanel.getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JPanel && comp.getName() != null && 
                            comp.getName().equals("PRACTICE_PANEL")) {
                            mainPanel.remove(comp);
                            break;
                        }
                    }
                    mainPanel.add(createPracticePanel(), PRACTICE_PANEL);
                    
                    cardLayout.show(mainPanel, PRACTICE_PANEL);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                } else {
                    System.out.println("mainPanel or cardLayout is null");
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in selectSector: " + e.getMessage());
        }
    }
    
    /**
     * Updates input field labels with the correct unit.
     */
    private void updateInputFieldLabels() {
        // Get current sector's unit
        SectorData sector = sectors.get(currentSectorIndex);
        String unit = sector.unit;
        
        // Update labels
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                updateLabelsInPanel((JPanel)comp, unit);
            }
        }
    }

    /**
     * Recursively updates labels in a panel with the specified unit.
     * 
     * @param panel the panel to update
     * @param unit the unit to display in labels
     */
    private void updateLabelsInPanel(JPanel panel, String unit) {
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel)comp;
                if (label.getText().startsWith("Area")) {
                    label.setText("Area (" + unit + "²):");
                }
            } else if (comp instanceof JPanel) {
                updateLabelsInPanel((JPanel)comp, unit);
            }
        }
    }
    
    /**
     * Inner class for displaying sector previews in the selection grid.
     */
    private class SectorPreviewPanel extends JPanel {
        private int index;
        private boolean completed;
        
        /**
         * Creates a sector preview panel.
         * 
         * @param index the sector index
         * @param completed whether the sector is completed
         */
        public SectorPreviewPanel(int index, boolean completed) {
            this.index = index;
            this.completed = completed;
            setOpaque(false);
            
            // Add mouse listeners
            if (!completed) {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("Mouse clicked on sector: " + index);
                        SwingUtilities.invokeLater(() -> {
                            selectSector(index);
                        });
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        setBackground(BUTTON_COLOR.brighter());
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        setBackground(BUTTON_COLOR);
                        setCursor(Cursor.getDefaultCursor());
                        repaint();
                    }
                });
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (sectorImages != null && !sectorImages.isEmpty()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Get the corresponding sector image
                BufferedImage sectorImage = sectorImages.get(index);
                
                // Maintain image proportions
                int width = getWidth();
                int height = getHeight();
                double imageRatio = (double) sectorImage.getWidth() / sectorImage.getHeight();
                double panelRatio = (double) width / height;
                
                int drawWidth, drawHeight;
                int x, y;
                
                if (imageRatio > panelRatio) {
                    // Image is wider, limit by width
                    drawWidth = width;
                    drawHeight = (int)(width / imageRatio);
                    x = 0;
                    y = (height - drawHeight) / 2;
                } else {
                    // Image is taller, limit by height
                    drawHeight = height;
                    drawWidth = (int)(height * imageRatio);
                    x = (width - drawWidth) / 2;
                    y = 0;
                }
                
                // Draw image maintaining proportions
                g2d.drawImage(sectorImage, x, y, drawWidth, drawHeight, null);
                
                if (completed) {
                    // Apply gray filter if completed
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Draw green checkmark
                    drawCheckmark(g2d, getWidth());
                }
            }
        }
    
        /**
         * Draws a checkmark on the sector preview when completed.
         * 
         * @param g2d the Graphics2D context
         * @param size the size for the checkmark
         */
        private void drawCheckmark(Graphics2D g2d, int size) {
            int checkSize = size / 2;
            int startX = (getWidth() - checkSize) / 2;
            int startY = (getHeight() - checkSize) / 2;
            
            // Set checkmark style
            g2d.setColor(new Color(34, 177, 76)); // Green
            g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            // Draw checkmark
            int[] xPoints = {
                startX,
                startX + checkSize/3,
                startX + checkSize
            };
            int[] yPoints = {
                startY + checkSize/2,
                startY + checkSize,
                startY
            };
            g2d.drawPolyline(xPoints, yPoints, 3);
        }
    }
    
    /**
     * Shows a dialog when time runs out, displaying the correct answer.
     * 
     * @param angle the sector angle in degrees
     * @param radius the sector radius
     * @param unit the unit of measurement
     * @param area the correct area calculation
     */
    private void showTimeUpDialog(int angle, double radius, String unit, double area) {
        StringBuilder message = new StringBuilder();
        message.append("<html><body style='width: 400px'>");
        message.append("<h2 style='color: red'>Time's Up!</h2>");
        message.append("</body></html>");
        
        JOptionPane optionPane = new JOptionPane(message.toString(), 
            JOptionPane.INFORMATION_MESSAGE, 
            JOptionPane.DEFAULT_OPTION, 
            null, 
            new Object[]{}, 
            null);
        optionPane.setOptions(new Object[]{});
        JDialog dialog = optionPane.createDialog(this, "Time's Up!");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
        
        // Show correct answer
        showCorrectAnswer(angle, radius, unit, area);
    }

    /**
     * Creates the top panel with HOME button, question label and timer.
     * 
     * @return JPanel containing the top interface elements
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Left side HOME button
        JButton homeButton = createHomeButton();
        panel.add(homeButton, BorderLayout.WEST);
        
        // Center question label
        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        questionLabel.setForeground(TEXT_COLOR);
        panel.add(questionLabel, BorderLayout.CENTER);
        
        // Right side timer
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(BACKGROUND_COLOR);
        
        // Add clock icon
        JLabel clockIconLabel = new JLabel(createClockIcon());
        rightPanel.add(clockIconLabel);
        
        // Add timer label
        timerLabel = new JLabel("Time: 5:00");
        timerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        timerLabel.setForeground(TEXT_COLOR);
        rightPanel.add(timerLabel);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }

    /**
     * Creates the HOME button with proper styling and functionality.
     * 
     * @return JButton configured as a HOME button
     */
    private JButton createHomeButton() {
        JButton homeButton = new JButton("Home");
        homeButton.setPreferredSize(new Dimension(80, 35));
        homeButton.setBackground(BUTTON_COLOR);
        homeButton.setForeground(Color.WHITE);
        homeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        homeButton.setBorderPainted(false);
        homeButton.setFocusPainted(false);
        
        // Add mouse hover effects
        homeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                homeButton.setBackground(BUTTON_COLOR.darker());
                homeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                homeButton.setBackground(BUTTON_COLOR);
                homeButton.setCursor(Cursor.getDefaultCursor());
            }
        });
        
        homeButton.addActionListener(e -> {
            // Save current state
            lastScore = totalScore;
            
            // Stop timer
            if (countdownTimer != null && countdownTimer.isRunning()) {
                countdownTimer.stop();
            }
            
            // Close current dialog
            if (parentDialog != null) {
                parentDialog.dispose();
            }
        });
        
        return homeButton;
    }

    /**
     * Creates a home icon as an ImageIcon.
     * 
     * @return ImageIcon representing a home
     */
    private ImageIcon createHomeIcon() {
        int size = 30;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(BUTTON_COLOR);
        
        int[] xPoints = {size/2, size, size-5, size-5, 5, 5, 0};
        int[] yPoints = {0, size/2, size/2, size, size, size/2, size/2};
        g2d.fillPolygon(xPoints, yPoints, 7);
        
        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * Creates a clock icon as an ImageIcon.
     * 
     * @return ImageIcon representing a clock
     */
    private ImageIcon createClockIcon() {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw clock circle
        g2d.setColor(TEXT_COLOR);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(1, 1, size-2, size-2);
        
        // Draw clock hands
        g2d.drawLine(size/2, size/2, size/2, 3); // 12 o'clock
        g2d.drawLine(size/2, size/2, size-3, size/2); // 3 o'clock
        
        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * Creates the input panel for area calculation input and submission.
     * 
     * @return JPanel containing input fields and submit button
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Create area input field
        JPanel areaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        areaPanel.setBackground(BACKGROUND_COLOR);
        
        // Get current sector's unit
        String unit = "cm"; // Default unit
        if (currentSectorIndex < sectors.size()) {
            unit = sectors.get(currentSectorIndex).unit;
        }
        
        // Update label with current unit
        JLabel areaLabel = new JLabel("Area (" + unit + "²):");
        areaLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        areaLabel.setForeground(TEXT_COLOR);
        
        areaInput = new JTextField(10);
        areaInput.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        areaPanel.add(areaLabel);
        areaPanel.add(areaInput);
        
        // Create submit button - modified to pure blue background with white text in simple style
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        submitButton.setBackground(new Color(52, 122, 183)); // Use blue similar to the image
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false); // Remove border
        submitButton.setOpaque(true); // Ensure background color shows
        submitButton.setPreferredSize(new Dimension(100, 40)); // Set appropriate dimensions
        
        // Add mouse events for enhanced visual feedback
        submitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                submitButton.setBackground(BUTTON_COLOR.darker());
                submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                submitButton.setBackground(BUTTON_COLOR);
                submitButton.setCursor(Cursor.getDefaultCursor());
            }
        });
        
        submitButton.addActionListener(e -> checkAnswer());
        
        // Add key listener so users can press Enter to submit answer
        areaInput.addActionListener(e -> checkAnswer());
        
        // Create result label
        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        resultLabel.setForeground(TEXT_COLOR);
        
        panel.add(areaPanel);
        panel.add(submitButton);
        panel.add(resultLabel);
        
        return panel;
    }

    /**
     * Sets up the countdown timer for the practice session.
     */
    private void setupTimer() {
        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                ((Timer)e.getSource()).stop();
                showTimeUpDialog(sectors.get(currentSectorIndex).angle, sectors.get(currentSectorIndex).radius, sectors.get(currentSectorIndex).unit, 0);
            }
        });
    }

    /**
     * Updates the timer label with the current remaining time.
     */
    private void updateTimerLabel() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("Time: %d:%02d", minutes, seconds));
    }

    /**
     * Starts a new question for the current sector.
     */
    private void startNewQuestion() {
        if (currentSectorIndex < 8) {
            SectorData sector = sectors.get(currentSectorIndex);
            questionLabel.setText(String.format("Calculate the area of a sector with angle %d° and radius %.1f %s:",
                sector.angle, sector.radius, sector.unit));
            currentAttempts = 0;
            areaInput.setText("");
            resultLabel.setText(" ");
            sectorPanel.repaint();
        } else {
            if (countdownTimer != null && countdownTimer.isRunning()) {
                countdownTimer.stop();
            }
            // Show final score dialog with Main Menu button
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
            
            JLabel scoreMessage = new JLabel("Total Score: " + totalScore);
            scoreMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoreMessage.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
            
            JButton mainMenuButton = new JButton("Return to Main Menu");
            mainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainMenuButton.addActionListener(e -> parentDialog.dispose());
            
            messagePanel.add(scoreMessage);
            messagePanel.add(Box.createVerticalStrut(10));
            messagePanel.add(mainMenuButton);
            
            JOptionPane.showMessageDialog(this, messagePanel, 
                "Practice Completed", JOptionPane.PLAIN_MESSAGE);
            parentDialog.dispose();
        }
    }

    /**
     * Checks the user's answer and provides feedback.
     * Handles scoring, attempts tracking, and progression logic.
     */
    private void checkAnswer() {
        try {
            double userArea = Double.parseDouble(areaInput.getText().trim());
            SectorData sector = sectors.get(currentSectorIndex);
            
            // Calculate correct area using 3.14 as π value
            double correctArea = (sector.angle * PI * sector.radius * sector.radius) / 360.0;
            
            // Check answer
            if (Math.abs(userArea - correctArea) < 0.01) {
                // Correct answer
                int points = 6;
                resultLabel.setText("Excellent! +" + points + " points");
                resultLabel.setForeground(new Color(46, 139, 87));
                
                // Update score
                totalScore += points;
                sector.score = points;
                
                // Only update status and progress if sector is not completed
                if (!sector.completed) {
                    sector.completed = true;
                    sectorsCompleted[currentSectorIndex] = true;
                    // Update progress
                    if (mainApp != null) {
                        System.out.println("Updating progress for sector " + (currentSectorIndex + 1));
                        mainApp.updateKeyStage2Progress(1, 8); // 25% * 1/8
                    }
                }
                
                // Check if all sectors are completed
                boolean allCompleted = true;
                for (boolean completed : sectorsCompleted) {
                    if (!completed) {
                        allCompleted = false;
                        break;
                    }
                }
                if (allCompleted) {
                    isGameCompleted = true;
                }
                
                // Update score display in selection interface
                if (scoreLabel != null) {
                    scoreLabel.setText("Total Score: " + totalScore);
                }
                
                // Update main interface score
                updateMainAppScore();
                
                // Stop timer
                if (countdownTimer != null && countdownTimer.isRunning()) {
                    countdownTimer.stop();
                }
                
                // Return directly to selection interface
                Timer timer = new Timer(1000, e -> {
                    cardLayout.show(mainPanel, SELECTION_PANEL);
                    updateSelectionPanel();
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                // Wrong answer
                currentAttempts++;
                sector.attempts++;
                
                System.out.println("Wrong answer, current attempts: " + currentAttempts);
                
                // Update attempt count display
                if (sectorPanel instanceof SectorDisplayPanel) {
                    ((SectorDisplayPanel) sectorPanel).updateAttemptsLabel();
                }
                
                if (currentAttempts >= 3) {
                    // Show correct answer
                    showCorrectAnswer(sector.angle, sector.radius, sector.unit, correctArea);
                    
                    // Stop timer
                    if (countdownTimer != null && countdownTimer.isRunning()) {
                        countdownTimer.stop();
                    }
                    
                    // Only update status and progress if sector is not completed
                    if (!sector.completed) {
                        sector.completed = true;
                        sectorsCompleted[currentSectorIndex] = true;
                        // Update progress
                        if (mainApp != null) {
                            System.out.println("Updating progress for sector " + (currentSectorIndex + 1) + " (after 3 attempts)");
                            mainApp.updateKeyStage2Progress(1, 8); // 25% * 1/8
                        }
                    }
                    
                    // Delay then return to selection interface
                    Timer timer = new Timer(1000, e -> {
                        cardLayout.show(mainPanel, SELECTION_PANEL);
                        updateSelectionPanel();
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    resultLabel.setText("Wrong answer, please try again ("
                                       + (3 - currentAttempts) + " attempts left)");
                    resultLabel.setForeground(new Color(220, 20, 60));
                    areaInput.setText("");
                }
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter a valid number");
            resultLabel.setForeground(new Color(220, 20, 60));
        }
    }

    /**
     * Shows a dialog with the correct answer and calculation steps.
     * 
     * @param angle the sector angle in degrees
     * @param radius the sector radius
     * @param unit the unit of measurement
     * @param area the correct calculated area
     */
    private void showCorrectAnswer(int angle, double radius, String unit, double area) {
        DecimalFormat df = new DecimalFormat("#.##");
        
        StringBuilder message = new StringBuilder();
        message.append("<html><body style='width: 400px'>");
        message.append("<h2>Correct Answer</h2>");
        message.append("<p>The formula for the area of a sector is:</p>");
        message.append("<p><b>Area = (θ/360) × π × r²</b></p>");
        message.append("<p>Where:</p>");
        message.append("<ul>");
        message.append("<li>θ is the angle in degrees: <b>").append(angle).append("°</b></li>");
        message.append("<li>r is the radius: <b>").append(radius).append(" ").append(unit).append("</b></li>");
        message.append("<li>π is approximately 3.14</li>");
        message.append("</ul>");
        message.append("<p>Substituting the values:</p>");
        message.append("<p>Area = (").append(angle).append("/360) × 3.14 × (").append(radius).append(")²</p>");
        message.append("<p>Area = ").append(df.format((double)angle/360)).append(" × 3.14 × ")
                .append(df.format(radius * radius)).append("</p>");
        message.append("<p>Area = <b>").append(df.format(area)).append(" ").append(unit).append("²</b></p>");
        message.append("</body></html>");
        
        // Create a panel with scroll bar in case content is too long
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(message.toString());
        textPane.setEditable(false);
        textPane.setBackground(null);
        
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        
        JOptionPane optionPane = new JOptionPane(scrollPane,
            JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[]{"OK"},  // Add OK button
            "OK");  // Default button
        JDialog dialog = optionPane.createDialog(this, "Correct Answer");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);  // Allow closing
        dialog.setVisible(true);
    }

    /**
     * Inner class for displaying sector images and information during practice.
     */
    private class SectorDisplayPanel extends JPanel {
        private JLabel infoLabel;
        private JLabel attemptsLabel;
        
        /**
         * Creates a sector display panel with info and attempts labels.
         */
        public SectorDisplayPanel() {
            setLayout(new BorderLayout());
            
            // Create bottom panel
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setBackground(BACKGROUND_COLOR);
            
            // Create info label
            infoLabel = new JLabel("", SwingConstants.CENTER);
            infoLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            infoLabel.setForeground(TEXT_COLOR);
            bottomPanel.add(infoLabel, BorderLayout.CENTER);
            
            // Create attempts label
            attemptsLabel = new JLabel("Attempts: 3/3", SwingConstants.RIGHT);
            attemptsLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
            attemptsLabel.setForeground(TEXT_COLOR);
            attemptsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
            bottomPanel.add(attemptsLabel, BorderLayout.EAST);
            
            // Add bottom panel
            add(bottomPanel, BorderLayout.SOUTH);
            
            // Set background
            setBackground(BACKGROUND_COLOR);
            
            // Initialize attempts display
            updateAttemptsLabel();
        }
        
        /**
         * Updates the info label with current sector data.
         */
        public void updateInfoLabel() {
            if (currentSectorIndex < sectors.size()) {
                SectorData sector = sectors.get(currentSectorIndex);
                infoLabel.setText(String.format("r: %.1f %s   angle: %d°", 
                    sector.radius, sector.unit, sector.angle));
            }
        }
        
        /**
         * Updates the attempts label with remaining attempts.
         */
        public void updateAttemptsLabel() {
            attemptsLabel.setText("Attempts: " + (3 - currentAttempts) + "/3");
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentSectorIndex < sectors.size() && sectorImages != null && !sectorImages.isEmpty()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Get current sector image
                BufferedImage currentImage = sectorImages.get(currentSectorIndex);
                
                // Maintain image proportions
                int panelWidth = getWidth();
                int panelHeight = getHeight() - 40; // Leave space for the info label
                int imageWidth = currentImage.getWidth();
                int imageHeight = currentImage.getHeight();
                
                // Calculate scaled size maintaining proportions
                double scale = Math.min(
                    (double) panelWidth / imageWidth,
                    (double) panelHeight / imageHeight
                ) * 0.8; // Scale down 20% for margins
                
                int scaledWidth = (int) (imageWidth * scale);
                int scaledHeight = (int) (imageHeight * scale);
                
                // Calculate centered position
                int x = (panelWidth - scaledWidth) / 2;
                int y = (panelHeight - scaledHeight) / 2;
                
                // Draw image
                g2d.drawImage(currentImage, x, y, scaledWidth, scaledHeight, null);
                
                // Update info label and attempts label
                updateInfoLabel();
                updateAttemptsLabel();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sectorSelected) {
            sectorPanel.repaint();
        }
    }

    /**
     * Resets the game to initial state for a new round.
     */
    private void resetGame() {
        // Reset all states
        totalScore = 0;
        lastScore = 0;
        isGameCompleted = false;
        
        // Stop timer
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
        
        // Reset all sector data
        for (int i = 0; i < sectorsCompleted.length; i++) {
            sectorsCompleted[i] = false;
            if (i < sectors.size()) {
                sectors.get(i).reset();
            }
        }
        
        // Reset current attempt count
        currentAttempts = 0;
        
        // Update score display
        if (scoreLabel != null) {
            scoreLabel.setText("Total Score: 0");
        }
    }

    /**
     * Marks a sector as completed and updates progress.
     * 
     * @param sectorIndex the index of the sector to mark as completed
     */
    private void markSectorCompleted(int sectorIndex) {
        if (!completedStatus[sectorIndex]) {
            completedStatus[sectorIndex] = true;
            mainApp.updateKeyStage2Progress(1, 8); // 25% * 1/8
            updateSelectionPanel(); // Use existing updateSelectionPanel method
        }
    }
}