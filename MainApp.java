import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import javax.swing.*;

/**
 * Background panel with decorative geometric shapes for visual enhancement.
 * This panel creates and renders various geometric shapes with transparency effects.
 * 
 * @author Group 65
 * @version 1.0
 */
class GeometricBackgroundPanel extends JPanel {
    /** Number of shapes to be drawn on the background */
    private static final int NUM_SHAPES = 25;
    /** Minimum alpha transparency value for shapes */
    private static final float MIN_ALPHA = 0.15f;
    /** Range of alpha transparency values */
    private static final float ALPHA_RANGE = 0.15f;
    /** Minimum size for generated shapes */
    private static final int MIN_SHAPE_SIZE = 30;
    /** Range of sizes for generated shapes */
    private static final int SHAPE_SIZE_RANGE = 100;
    /** Number of different shape types available */
    private static final int NUM_SHAPE_TYPES = 4;
    
    /** Color palette for background shapes */
    private static final Color[] SHAPE_PALETTE = {
        new Color(135, 206, 235), // Sky blue
        new Color(144, 238, 144), // Light green
        new Color(255, 182, 193), // Light pink
        new Color(255, 220, 177), // Light orange
        new Color(230, 230, 250)  // Lavender
    };
    
    /** Random number generator for shape properties */
    private Random random = new Random();
    /** Array to store generated shapes */
    private Shape[] shapes;
    /** Array to store shape colors */
    private Color[] colors;
    /** Array to store shape types (0=circle, 1=square, 2=triangle, 3=pentagon) */
    private int[] shapeTypes;
    /** Array to store transparency values */
    private float[] alphas;
    /** Array to store shape sizes */
    private int[] sizes;
    /** Array to store X positions of shapes */
    private int[] xPositions;
    /** Array to store Y positions of shapes */
    private int[] yPositions;
    
    /**
     * Constructs a new GeometricBackgroundPanel and initializes shapes.
     */
    public GeometricBackgroundPanel() {
        setOpaque(true);
        initShapes();
    }
    
    /**
     * Initializes arrays and generates random properties for all shapes.
     */
    private void initShapes() {
        shapes = new Shape[NUM_SHAPES];
        colors = new Color[NUM_SHAPES];
        shapeTypes = new int[NUM_SHAPES];
        alphas = new float[NUM_SHAPES];
        sizes = new int[NUM_SHAPES];
        xPositions = new int[NUM_SHAPES];
        yPositions = new int[NUM_SHAPES];
        
        for (int i = 0; i < NUM_SHAPES; i++) {
            shapeTypes[i] = random.nextInt(NUM_SHAPE_TYPES);
            alphas[i] = MIN_ALPHA + random.nextFloat() * ALPHA_RANGE;
            colors[i] = SHAPE_PALETTE[random.nextInt(SHAPE_PALETTE.length)];
            sizes[i] = MIN_SHAPE_SIZE + random.nextInt(SHAPE_SIZE_RANGE);
        }
    }
    
    /**
     * Paints the background with decorative geometric shapes.
     * 
     * @param g the Graphics object to paint on
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw background color first
        g.setColor(MainApp.UI_CONSTANTS.BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable anti-aliasing for smoother shapes
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Generate or update shapes based on the current size
        for (int i = 0; i < NUM_SHAPES; i++) {
            // Update position if not set or if window was resized
            if (shapes[i] == null || width != getWidth() || height != getHeight()) {
                xPositions[i] = random.nextInt(width - sizes[i]);
                yPositions[i] = random.nextInt(height - sizes[i]);
            }
            
            int x = xPositions[i];
            int y = yPositions[i];
            int size = sizes[i];
            
            // Create different shape types
            switch (shapeTypes[i]) {
                case 0: // Circle
                    shapes[i] = new java.awt.geom.Ellipse2D.Float(x, y, size, size);
                    break;
                case 1: // Square
                    shapes[i] = new Rectangle(x, y, size, size);
                    break;
                case 2: // Triangle
                    int[] xPoints = {x, x + size/2, x + size};
                    int[] yPoints = {y + size, y, y + size};
                    shapes[i] = new Polygon(xPoints, yPoints, 3);
                    break;
                case 3: // Pentagon
                    Polygon pentagon = new Polygon();
                    for (int j = 0; j < 5; j++) {
                        double angle = 2 * Math.PI * j / 5 - Math.PI / 2;
                        pentagon.addPoint(
                            x + size/2 + (int)(size/2 * Math.cos(angle)),
                            y + size/2 + (int)(size/2 * Math.sin(angle))
                        );
                    }
                    shapes[i] = pentagon;
                    break;
            }
            
            // Set transparency
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphas[i]));
            
            // Draw the shape
            g2d.setColor(colors[i]);
            g2d.fill(shapes[i]);
            
            // Draw a subtle outline
            g2d.setColor(colors[i].darker());
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.draw(shapes[i]);
        }
        
        g2d.dispose();
    }
}

/**
 * Main application class for the Shapeville geometry learning game.
 * This class manages the main window, user interface, scoring system, and level progression.
 * It provides an interactive educational environment for learning geometric concepts.
 * 
 * @author Group 65
 * @version 1.0
 * @since 2025
 */
public class MainApp {
    // ==================== CONSTANTS ====================
    
    /**
     * UI-related constants including colors for various interface elements.
     */
    public static class UI_CONSTANTS {
        /** Main background color for the application */
        public static final Color BACKGROUND_COLOR = new Color(230, 240, 255); // Light blue background
        /** Standard button color */
        public static final Color BUTTON_COLOR = new Color(70, 130, 180);
        /** Warning color for important messages */
        public static final Color WARNING_RED = new Color(220, 53, 69);
        /** Gold-orange color for special buttons */
        public static final Color GOLD_ORANGE_COLOR = new Color(218, 165, 32);
        /** Text color for buttons */
        public static final Color BUTTON_TEXT_COLOR = Color.WHITE;
        /** Color for main title text */
        public static final Color TITLE_COLOR = new Color(44, 62, 80);
        /** Color for welcome messages */
        public static final Color WELCOME_COLOR = new Color(70, 130, 180);
        /** Color for subtitle text */
        public static final Color SUBTITLE_COLOR = new Color(100, 100, 100);
        /** Color for score display */
        public static final Color SCORE_COLOR = new Color(255, 87, 34);
        
        /** Key Stage 1 button color */
        public static final Color KS1_COLOR = new Color(135, 206, 250);
        /** Key Stage 2 button color */
        public static final Color KS2_COLOR = new Color(144, 238, 144);
        /** Border color for Key Stage 1 selection */
        public static final Color KS1_BORDER_COLOR = new Color(0, 0, 139);
        /** Border color for Key Stage 2 selection */
        public static final Color KS2_BORDER_COLOR = new Color(0, 100, 0);
        
        /** Progress bar track background color */
        public static final Color PROGRESS_TRACK_COLOR = new Color(230, 230, 230);
        /** Progress bar gradient start color */
        public static final Color PROGRESS_START_COLOR = new Color(100, 200, 255);
        /** Progress bar gradient end color */
        public static final Color PROGRESS_END_COLOR = new Color(50, 150, 255);
        /** Progress bar border color */
        public static final Color PROGRESS_BORDER_COLOR = new Color(150, 150, 150);
    }
    
    /**
     * Window size constants for main window and dialogs.
     */
    public static class WINDOW_CONSTANTS {
        /** Main window width */
        public static final int WINDOW_WIDTH = 1400;
        /** Main window height */
        public static final int WINDOW_HEIGHT = 850;
        /** Small dialog width */
        public static final int DIALOG_WIDTH_SMALL = 900;
        /** Small dialog height */
        public static final int DIALOG_HEIGHT_SMALL = 700;
        /** Medium dialog width */
        public static final int DIALOG_WIDTH_MEDIUM = 1100;
        /** Medium dialog height */
        public static final int DIALOG_HEIGHT_MEDIUM = 800;
        /** Large dialog height */
        public static final int DIALOG_HEIGHT_LARGE = 650;
    }
    
    /**
     * Layout and spacing constants for consistent UI arrangement.
     */
    public static class LAYOUT_CONSTANTS {
        /** Main panel padding */
        public static final int MAIN_PANEL_PADDING = 30;
        /** Standard component spacing */
        public static final int COMPONENT_SPACING = 20;
        /** Small spacing between elements */
        public static final int SMALL_SPACING = 10;
        /** Large spacing between sections */
        public static final int LARGE_SPACING = 40;
        /** Grid horizontal spacing */
        public static final int GRID_SPACING_H = 10;
        /** Grid vertical spacing */
        public static final int GRID_SPACING_V = 10;
        /** Grid padding */
        public static final int GRID_PADDING = 40;
        /** Progress panel padding */
        public static final int PROGRESS_PANEL_PADDING = 20;
        
        /** Button border radius for rounded corners */
        public static final int BUTTON_BORDER_RADIUS = 20;
        /** Border width for selected elements */
        public static final int BORDER_WIDTH = 3;
        /** Border width for hover effects */
        public static final int HOVER_BORDER_WIDTH = 1;
    }
    
    /**
     * Font constants for consistent typography throughout the application.
     */
    public static class FONT_CONSTANTS {
        /** Main title font */
        public static final Font TITLE_FONT = new Font("Comic Sans MS", Font.BOLD, 55);
        /** Welcome message font */
        public static final Font WELCOME_FONT = new Font("Comic Sans MS", Font.BOLD, 24);
        /** Subtitle text font */
        public static final Font SUBTITLE_FONT = new Font("Comic Sans MS", Font.ITALIC, 18);
        /** Prompt text font */
        public static final Font PROMPT_FONT = new Font("Comic Sans MS", Font.BOLD, 25);
        /** Grade title font */
        public static final Font GRADE_TITLE_FONT = new Font("Comic Sans MS", Font.BOLD, 22);
        /** Grade subtitle font */
        public static final Font GRADE_SUBTITLE_FONT = new Font("Comic Sans MS", Font.PLAIN, 16);
        /** Level button font */
        public static final Font LEVEL_BUTTON_FONT = new Font("Comic Sans MS", Font.BOLD, 25);
        /** Small level button font */
        public static final Font LEVEL_BUTTON_SMALL_FONT = new Font("Comic Sans MS", Font.BOLD, 18);
        /** Progress label font */
        public static final Font PROGRESS_FONT = new Font("Comic Sans MS", Font.BOLD, 22);
        /** Progress bar text font */
        public static final Font PROGRESS_BAR_FONT = new Font("Comic Sans MS", Font.BOLD, 14);
        /** Score display font */
        public static final Font SCORE_FONT = new Font("Comic Sans MS", Font.BOLD, 32);
        /** Standard button font */
        public static final Font BUTTON_FONT = new Font("Comic Sans MS", Font.BOLD, 22);
    }
    
    /**
     * Component size constants for consistent UI element dimensions.
     */
    public static class SIZE_CONSTANTS {
        /** Grade selection button size */
        public static final Dimension GRADE_BUTTON_SIZE = new Dimension(200, 90);
        /** Level selection button size */
        public static final Dimension LEVEL_BUTTON_SIZE = new Dimension(500, 150);
        /** Progress bar size */
        public static final Dimension PROGRESS_BAR_SIZE = new Dimension(200, 30);
        /** Standard styled button size */
        public static final Dimension STYLED_BUTTON_SIZE = new Dimension(180, 50);
        
        /** Progress bar height */
        public static final int PROGRESS_BAR_HEIGHT = 25;
        /** Progress bar corner arc radius */
        public static final int PROGRESS_BAR_ARC = 15;
    }
    
    /**
     * Animation timing and effect constants.
     */
    public static class ANIMATION_CONSTANTS {
        /** Timer delay for color animations */
        public static final int COLOR_TIMER_DELAY = 50;
        /** Hue increment for rainbow effect */
        public static final float HUE_INCREMENT = 0.01f;
        /** Saturation for rainbow colors */
        public static final float RAINBOW_SATURATION = 0.7f;
        /** Brightness for rainbow colors */
        public static final float RAINBOW_BRIGHTNESS = 0.8f;
        
        /** Timer delay for hover animations */
        public static final int HOVER_TIMER_DELAY = 30;
        /** Glow effect increment */
        public static final float GLOW_INCREMENT = 0.1f;
    }
    
    /**
     * Progress tracking and calculation constants.
     */
    public static class PROGRESS_CONSTANTS {
        /** Percentage value for Key Stage 1 task completion */
        public static final double KEY_STAGE1_TASK_PERCENTAGE = 50.00;
        /** Percentage value for Key Stage 2 task completion */
        public static final double KEY_STAGE2_TASK_PERCENTAGE = 25.00;
        /** Decimal precision multiplier for accurate calculations */
        public static final int DECIMAL_PRECISION = 1000000; // For 6 decimal places
        /** Minimum progress value */
        public static final int PROGRESS_MIN = 0;
        /** Maximum progress value */
        public static final int PROGRESS_MAX = 100;
    }
    
    /**
     * Task and level configuration constants.
     */
    public static class TASK_CONSTANTS {
        /** Number of shapes in Task 3 */
        public static final int NUM_SHAPES_IN_TASK3 = 4;
        /** Total number of levels */
        public static final int NUM_LEVELS = 6;
        /** Number of grid rows for level layout */
        public static final int GRID_ROWS = 3;
        /** Number of grid columns for level layout */
        public static final int GRID_COLS = 2;
        /** Number of angle types for identification */
        public static final int NUM_ANGLE_TYPES = 5;
    }
    
    /**
     * Enumeration of available learning levels with their properties.
     * Each level has a display name and availability for different key stages.
     */
    public enum Level {
        /** Shape identification level for beginners */
        SHAPE_IDENTIFICATION(1, "Identification of Shapes", true, false),
        /** Angle type identification level */
        ANGLE_IDENTIFICATION(2, "Identification of Angle Types", true, false),
        /** Basic area calculation level */
        AREA_CALCULATION(3, "Area Calculation of Shapes", false, true),
        /** Circle area and circumference calculation */
        CIRCLE_CALCULATION(4, "Area and Circumference Calculation of Circle", false, true),
        /** Compound shape area calculation */
        COMPOUND_AREA_CALCULATION(5, "Compound Shape Area Calculation", false, true),
        /** Sector area calculation level */
        SECTOR_CALCULATION(6, "Sector Area Calculation", false, true);
        
        /** Level number identifier */
        private final int levelNumber;
        /** Display name for the level */
        private final String displayName;
        /** Whether available for Key Stage 1 */
        private final boolean keyStage1Available;
        /** Whether available for Key Stage 2 */
        private final boolean keyStage2Available;
        
        /**
         * Constructor for Level enum.
         * 
         * @param levelNumber the numeric identifier for the level
         * @param displayName the human-readable name for the level
         * @param keyStage1Available whether this level is available for Key Stage 1
         * @param keyStage2Available whether this level is available for Key Stage 2
         */
        Level(int levelNumber, String displayName, boolean keyStage1Available, boolean keyStage2Available) {
            this.levelNumber = levelNumber;
            this.displayName = displayName;
            this.keyStage1Available = keyStage1Available;
            this.keyStage2Available = keyStage2Available;
        }
        
        /**
         * Gets the level number.
         * @return the level number
         */
        public int getLevelNumber() { return levelNumber; }
        
        /**
         * Gets the display name.
         * @return the display name
         */
        public String getDisplayName() { return displayName; }
        
        /**
         * Checks if level is available for Key Stage 1.
         * @return true if available for Key Stage 1
         */
        public boolean isKeyStage1Available() { return keyStage1Available; }
        
        /**
         * Checks if level is available for Key Stage 2.
         * @return true if available for Key Stage 2
         */
        public boolean isKeyStage2Available() { return keyStage2Available; }
        
        /**
         * Gets available levels for a specific key stage.
         * 
         * @param isKeyStage1 true for Key Stage 1, false for Key Stage 2
         * @return array of available levels
         */
        public static Level[] getAvailableLevels(boolean isKeyStage1) {
            return java.util.Arrays.stream(values())
                .filter(level -> isKeyStage1 ? level.isKeyStage1Available() : level.isKeyStage2Available())
                .toArray(Level[]::new);
        }
        
        /**
         * Gets a level by its number.
         * 
         * @param levelNumber the level number to search for
         * @return the Level enum value or null if not found
         */
        public static Level getByNumber(int levelNumber) {
            return java.util.Arrays.stream(values())
                .filter(level -> level.getLevelNumber() == levelNumber)
                .findFirst()
                .orElse(null);
        }
    }
    
    /**
     * Text constants for consistent messaging throughout the application.
     */
    public static class TEXT_CONSTANTS {
        /** Application title */
        public static final String APP_TITLE = "Shapeville";
        /** Welcome message for users */
        public static final String WELCOME_MESSAGE = "Welcome to your geometric adventure!";
        /** Subtitle describing the application */
        public static final String SUBTITLE_MESSAGE = "Explore shapes, angles, and calculations in a fun way ";
        /** Prompt for grade level selection */
        public static final String GRADE_PROMPT = "Please select your grade level:";
        /** Key Stage 1 title */
        public static final String KS1_TITLE = "Key Stage 1";
        /** Key Stage 1 subtitle */
        public static final String KS1_SUBTITLE = "Grades 1-2";
        /** Key Stage 2 title */
        public static final String KS2_TITLE = "Key Stage 2";
        /** Key Stage 2 subtitle */
        public static final String KS2_SUBTITLE = "Grades 3-4";
        /** Progress bar label */
        public static final String PROGRESS_LABEL = "progress bar";
        /** Score display prefix */
        public static final String SCORE_PREFIX = "Total score: ";
        /** Exit button text */
        public static final String EXIT_BUTTON_TEXT = "End session";
        /** Exit confirmation dialog title */
        public static final String EXIT_CONFIRMATION_TITLE = "End session";
        /** Development message for incomplete features */
        public static final String DEVELOPMENT_MESSAGE = "This stage is developing!";
        /** Development dialog title */
        public static final String DEVELOPMENT_TITLE = "prompt";
        
        /** Key Stage 1 selection confirmation message */
        public static final String KS1_SELECTED = "You selected: Key Stage 1 (Grades 1-2)";
        /** Key Stage 2 selection confirmation message */
        public static final String KS2_SELECTED = "You selected: Key Stage 2 (Grades 3-4)";
    }
    
    // ==================== INSTANCE VARIABLES ====================
    
    /** Main application frame */
    private JFrame mainFrame;
    /** Progress bar component */
    private JProgressBar progressBar;
    /** Score display label */
    private JLabel scoreLabel;
    /** Current total score */
    private int currentScore = 0;
    /** Last recorded shape identification score */
    private int lastShapeScore = 0;
    /** Last recorded angle identification score */
    private int lastAngleScore = 0;
    /** Last recorded sector calculation score */
    private int lastSectorScore = 0;
    /** Last recorded compound shape score */
    private int lastCompoundScore = 0;
    
    /** Key Stage 1 progress percentage */
    private double keyStage1Progress = 0.00;
    /** Key Stage 2 progress percentage */
    private double keyStage2Progress = 0.00;
    /** Current key stage selection flag */
    private boolean isKeyStage1 = true;
    
    /** Button group for key stage selection */
    private ButtonGroup keyStageGroup;
    /** Array of level selection buttons */
    private JButton[] levelButtons;
    /** Prompt label for instructions */
    private JLabel promptLabel;

    /** Task 3 completion status for each shape */
    private boolean[] task3CompletedShapes = new boolean[TASK_CONSTANTS.NUM_SHAPES_IN_TASK3];
    /** Task 3 scores for each shape */
    private int[] task3ShapeScores = new int[TASK_CONSTANTS.NUM_SHAPES_IN_TASK3];
    /** Task 3 total accumulated score */
    private int task3TotalScore = 0;

    // ==================== CONSTRUCTOR ====================
    
    /**
     * Constructs the main application window and initializes all UI components.
     * Sets up the main frame, background panel, and all child panels.
     */
    public MainApp() {
        mainFrame = new JFrame(TEXT_CONSTANTS.APP_TITLE);
        mainFrame.setSize(WINDOW_CONSTANTS.WINDOW_WIDTH, WINDOW_CONSTANTS.WINDOW_HEIGHT);
        mainFrame.setMinimumSize(new Dimension(WINDOW_CONSTANTS.WINDOW_WIDTH, WINDOW_CONSTANTS.WINDOW_HEIGHT));
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Create geometric background panel
        GeometricBackgroundPanel backgroundPanel = new GeometricBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout(LAYOUT_CONSTANTS.COMPONENT_SPACING, LAYOUT_CONSTANTS.COMPONENT_SPACING));
        backgroundPanel.setBackground(UI_CONSTANTS.BACKGROUND_COLOR);
        
        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(LAYOUT_CONSTANTS.COMPONENT_SPACING, LAYOUT_CONSTANTS.COMPONENT_SPACING));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
            LAYOUT_CONSTANTS.MAIN_PANEL_PADDING, LAYOUT_CONSTANTS.MAIN_PANEL_PADDING, 
            LAYOUT_CONSTANTS.MAIN_PANEL_PADDING, LAYOUT_CONSTANTS.MAIN_PANEL_PADDING));

        // Create all UI panels
        JPanel titlePanel = createTitlePanel();
        JPanel levelsPanel = createLevelsPanel();
        JPanel progressPanel = createProgressPanel();
        JPanel bottomPanel = createBottomPanel();

        // Add all panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(levelsPanel, BorderLayout.CENTER);
        mainPanel.add(progressPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        backgroundPanel.add(mainPanel);

        // Add window closing event handler
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showExitConfirmation();
            }
        });

        mainFrame.add(backgroundPanel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    // ==================== PUBLIC METHODS ====================

    /**
     * Gets the main application frame.
     * 
     * @return the main JFrame
     */
    public JFrame getFrame() {
        return mainFrame;
    }
    
    /**
     * Shows the main menu window.
     */
    public void showMainMenu() {
        mainFrame.setVisible(true);
    }
    
    /**
     * Shows the geometry learning system dialog.
     */
    public void showGeometryLearningSystem() {
        JDialog dialog = new JDialog(mainFrame, Level.CIRCLE_CALCULATION.getDisplayName(), true);
        dialog.setSize(WINDOW_CONSTANTS.DIALOG_WIDTH_MEDIUM, WINDOW_CONSTANTS.DIALOG_HEIGHT_MEDIUM);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        GeometryLearningSystem panel = new GeometryLearningSystem(dialog, this);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Adds points to the current score and updates the display.
     * 
     * @param points the number of points to add
     */
    public void addScore(int points) {
        currentScore += points;
        if (scoreLabel != null) {
            scoreLabel.setText(TEXT_CONSTANTS.SCORE_PREFIX + currentScore);
        }
        updateProgress();
    }
    
    /**
     * Updates the completion status of Task 3 shapes.
     * 
     * @param completedShapes array indicating which shapes are completed
     */
    public void updateTask3CompletedShapes(boolean[] completedShapes) {
        for (int i = 0; i < completedShapes.length && i < task3CompletedShapes.length; i++) {
            task3CompletedShapes[i] = completedShapes[i];
        }
    }
    
    /**
     * Gets the completion status of Task 3 shapes.
     * 
     * @return array of completion status for each shape
     */
    public boolean[] getTask3CompletedShapes() {
        return task3CompletedShapes;
    }
    
    /**
     * Updates the scores for individual Task 3 shapes.
     * 
     * @param scores array of scores for each shape
     */
    public void updateTask3ShapeScores(int[] scores) {
        task3TotalScore = 0;
        for (int i = 0; i < scores.length && i < task3ShapeScores.length; i++) {
            task3ShapeScores[i] = scores[i];
            task3TotalScore += scores[i];
        }
    }
    
    /**
     * Gets the scores for individual Task 3 shapes.
     * 
     * @return array of scores for each shape
     */
    public int[] getTask3ShapeScores() {
        return task3ShapeScores;
    }
    
    /**
     * Gets the total score for Task 3.
     * 
     * @return the total Task 3 score
     */
    public int getTask3TotalScore() {
        return task3TotalScore;
    }

    /**
     * Updates the progress for Key Stage 1 based on completed tasks.
     * 
     * @param taskNumber the current task number completed
     * @param totalTasks the total number of tasks
     */
    public void updateKeyStage1Progress(double taskNumber, double totalTasks) {
        if (taskNumber == TASK_CONSTANTS.NUM_ANGLE_TYPES && totalTasks == TASK_CONSTANTS.NUM_ANGLE_TYPES) {
            keyStage1Progress = Math.round((keyStage1Progress + 50.0) * PROGRESS_CONSTANTS.DECIMAL_PRECISION) / (double)PROGRESS_CONSTANTS.DECIMAL_PRECISION;
        } else {
            double increment = Math.round((PROGRESS_CONSTANTS.KEY_STAGE1_TASK_PERCENTAGE / totalTasks) * PROGRESS_CONSTANTS.DECIMAL_PRECISION) / (double)PROGRESS_CONSTANTS.DECIMAL_PRECISION;
            keyStage1Progress = Math.round((keyStage1Progress + increment) * PROGRESS_CONSTANTS.DECIMAL_PRECISION) / (double)PROGRESS_CONSTANTS.DECIMAL_PRECISION;
        }
        
        if (isKeyStage1) {
            updateProgress();
        }
    }

    /**
     * Updates the progress for Key Stage 2 based on completed tasks.
     * 
     * @param taskNumber the current task number completed
     * @param totalTasks the total number of tasks
     */
    public void updateKeyStage2Progress(double taskNumber, double totalTasks) {
        double increment = Math.round((PROGRESS_CONSTANTS.KEY_STAGE2_TASK_PERCENTAGE / totalTasks) * PROGRESS_CONSTANTS.DECIMAL_PRECISION) / (double)PROGRESS_CONSTANTS.DECIMAL_PRECISION;
        System.out.println("Updating Key Stage 2 progress: current=" + keyStage2Progress + 
            ", increment=" + increment + ", totalTasks=" + totalTasks);
        keyStage2Progress = Math.round((keyStage2Progress + increment) * PROGRESS_CONSTANTS.DECIMAL_PRECISION) / (double)PROGRESS_CONSTANTS.DECIMAL_PRECISION;
        if (!isKeyStage1) {
            updateProgress();
            System.out.println("New Key Stage 2 progress: " + keyStage2Progress);
        }
    }

    /**
     * Gets the current stage progress as a percentage.
     * 
     * @return the current progress percentage
     */
    public int getCurrentStageProgress() {
        return isKeyStage1 ? (int)Math.round(keyStage1Progress) : (int)Math.round(keyStage2Progress);
    }

    // ==================== PRIVATE UTILITY METHODS ====================
    
    /**
     * Adds a hover effect to a button that darkens the background on mouse enter.
     * This method eliminates code duplication for button hover effects.
     * 
     * @param button the button to add the effect to
     * @param originalColor the original background color of the button
     */
    private void addHoverEffect(JButton button, Color originalColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
    }
    
    /**
     * Applies standard styling to a button including font, colors, and size.
     * This method eliminates code duplication for button styling.
     * 
     * @param button the button to style
     * @param backgroundColor the background color
     * @param textColor the text color
     * @param font the font to use
     * @param size the preferred size (can be null)
     */
    private void applyButtonStyle(JButton button, Color backgroundColor, Color textColor, Font font, Dimension size) {
        button.setFont(font);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        if (size != null) {
            button.setPreferredSize(size);
        }
    }
    
    /**
     * Adds an animated hover effect to a button with glow animation.
     * This method eliminates code duplication for animated button effects.
     * 
     * @param button the button to add the effect to
     * @param originalColor the original background color
     */
    private void addAnimatedHoverEffect(JButton button, Color originalColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private Timer hoverTimer;
            private float glowAlpha = 0f;
            
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (hoverTimer != null) hoverTimer.stop();
                hoverTimer = new Timer(ANIMATION_CONSTANTS.HOVER_TIMER_DELAY, e -> {
                    glowAlpha = Math.min(1f, glowAlpha + ANIMATION_CONSTANTS.GLOW_INCREMENT);
                    button.repaint();
                    if (glowAlpha >= 1f) hoverTimer.stop();
                });
                hoverTimer.start();
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (hoverTimer != null) hoverTimer.stop();
                hoverTimer = new Timer(ANIMATION_CONSTANTS.HOVER_TIMER_DELAY, e -> {
                    glowAlpha = Math.max(0f, glowAlpha - ANIMATION_CONSTANTS.GLOW_INCREMENT);
                    button.repaint();
                    if (glowAlpha <= 0f) hoverTimer.stop();
                });
                hoverTimer.start();
            }
        });
    }
    
    /**
     * Creates a standard dialog with common settings.
     * This method eliminates code duplication for dialog creation.
     * 
     * @param title the dialog title
     * @param width the dialog width
     * @param height the dialog height
     * @return a configured JDialog
     */
    private JDialog createStandardDialog(String title, int width, int height) {
        JDialog dialog = new JDialog(mainFrame, title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setBackground(UI_CONSTANTS.BACKGROUND_COLOR);
        return dialog;
    }
    
    /**
     * Adds a score update listener to a dialog to handle score changes when the dialog closes.
     * This method eliminates code duplication for score management.
     * 
     * @param dialog the dialog to add the listener to
     * @param scoreSupplier function to get the current score
     * @param lastScoreField array containing the last recorded score (used for reference passing)
     */
    private void addScoreUpdateListener(JDialog dialog, java.util.function.Supplier<Integer> scoreSupplier, int[] lastScoreField) {
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                int newTotalScore = scoreSupplier.get();
                int scoreIncrease = newTotalScore - lastScoreField[0];
                if (scoreIncrease > 0) {
                    currentScore += scoreIncrease;
                    lastScoreField[0] = newTotalScore;
                    scoreLabel.setText(TEXT_CONSTANTS.SCORE_PREFIX + currentScore);
                    updateProgress();
                }
            }
        });
    }
    
    // ==================== PRIVATE UI CREATION METHODS ====================
    
    /**
     * Updates the progress bar display based on current stage progress.
     */
    private void updateProgress() {
        double progress;
        if (isKeyStage1) {
            progress = Math.min(keyStage1Progress, PROGRESS_CONSTANTS.PROGRESS_MAX);
        } else {
            progress = Math.min(keyStage2Progress, PROGRESS_CONSTANTS.PROGRESS_MAX);
        }
        progressBar.setValue((int)Math.round(progress));
        progressBar.setString(String.format("%.1f%%", progress));
    }

    /**
     * Creates the title panel containing the main application title and welcome message.
     * 
     * @return the configured title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // Title label
        JLabel titleLabel = new JLabel(TEXT_CONSTANTS.APP_TITLE);
        titleLabel.setFont(FONT_CONSTANTS.TITLE_FONT);
        titleLabel.setForeground(UI_CONSTANTS.TITLE_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add color animation
        Timer colorTimer = new Timer(ANIMATION_CONSTANTS.COLOR_TIMER_DELAY, new ActionListener() {
            float hue = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                hue += ANIMATION_CONSTANTS.HUE_INCREMENT;
                if (hue > 1) hue = 0;
                Color rainbow = Color.getHSBColor(hue, ANIMATION_CONSTANTS.RAINBOW_SATURATION, ANIMATION_CONSTANTS.RAINBOW_BRIGHTNESS);
                titleLabel.setForeground(rainbow);
            }
        });
        colorTimer.start();
        
        // Welcome message panel
        JPanel welcomePanel = createWelcomeMessagePanel();
        
        // Add components to main panel
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(LAYOUT_CONSTANTS.SMALL_SPACING));
        panel.add(welcomePanel);
        
        return panel;
    }
    
    /**
     * Creates the welcome message panel with main and subtitle text.
     * Extracted from createTitlePanel to reduce method complexity.
     * 
     * @return the configured welcome message panel
     */
    private JPanel createWelcomeMessagePanel() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setOpaque(false);
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        
        // Main welcome message
        JLabel welcomeLabel = new JLabel(TEXT_CONSTANTS.WELCOME_MESSAGE);
        welcomeLabel.setFont(FONT_CONSTANTS.WELCOME_FONT);
        welcomeLabel.setForeground(UI_CONSTANTS.WELCOME_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle message
        JLabel subtitleLabel = new JLabel(TEXT_CONSTANTS.SUBTITLE_MESSAGE);
        subtitleLabel.setFont(FONT_CONSTANTS.SUBTITLE_FONT);
        subtitleLabel.setForeground(UI_CONSTANTS.SUBTITLE_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add labels to welcome panel
        welcomePanel.add(Box.createVerticalStrut(LAYOUT_CONSTANTS.SMALL_SPACING));
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(5));
        welcomePanel.add(subtitleLabel);
        
        return welcomePanel;
    }

    /**
     * Creates the levels panel containing grade selection and level buttons.
     * 
     * @return the configured levels panel
     */
    private JPanel createLevelsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
    
        // Add prompt label
        JPanel promptPanel = createPromptPanel();
        panel.add(promptPanel);
        panel.add(Box.createVerticalStrut(LAYOUT_CONSTANTS.SMALL_SPACING));
    
        // Create Key Stage selection panel
        JPanel keyStagePanel = createKeyStageSelectionPanel();
        panel.add(keyStagePanel);
        panel.add(Box.createVerticalStrut(0));
    
        // Create level buttons grid
        JPanel gridPanel = createLevelButtonsGrid();
        panel.add(gridPanel);
        
        return panel;
    }
    
    /**
     * Creates the prompt panel with instruction text.
     * Extracted from createLevelsPanel to reduce method complexity.
     * 
     * @return the configured prompt panel
     */
    private JPanel createPromptPanel() {
        promptLabel = new JLabel(TEXT_CONSTANTS.GRADE_PROMPT);
        promptLabel.setFont(FONT_CONSTANTS.PROMPT_FONT);
        promptLabel.setForeground(UI_CONSTANTS.WARNING_RED);
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel promptPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        promptPanel.setOpaque(false);
        promptPanel.add(promptLabel);
        return promptPanel;
    }
    
    /**
     * Creates the key stage selection panel with KS1 and KS2 buttons.
     * Extracted from createLevelsPanel to reduce method complexity.
     * 
     * @return the configured key stage selection panel
     */
    private JPanel createKeyStageSelectionPanel() {
        JPanel keyStagePanel = new JPanel();
        keyStagePanel.setOpaque(false);
        keyStagePanel.setLayout(new FlowLayout(FlowLayout.CENTER, LAYOUT_CONSTANTS.LARGE_SPACING, LAYOUT_CONSTANTS.MAIN_PANEL_PADDING));
    
        // Create buttons
        JButton ks1Button = createGradeButton(TEXT_CONSTANTS.KS1_TITLE, TEXT_CONSTANTS.KS1_SUBTITLE, UI_CONSTANTS.KS1_COLOR);
        JButton ks2Button = createGradeButton(TEXT_CONSTANTS.KS2_TITLE, TEXT_CONSTANTS.KS2_SUBTITLE, UI_CONSTANTS.KS2_COLOR);
        
        // Store the currently selected button
        final JButton[] selectedButton = {null};
        
        // Add action listeners
        ks1Button.addActionListener(e -> {
            updateGradeSelection(selectedButton, ks1Button, ks2Button, true);
        });
        
        ks2Button.addActionListener(e -> {
            updateGradeSelection(selectedButton, ks2Button, ks1Button, false);
        });
    
        keyStagePanel.add(ks1Button);
        keyStagePanel.add(ks2Button);
        
        return keyStagePanel;
    }
    
    /**
     * Creates the level buttons grid layout.
     * Extracted from createLevelsPanel to reduce method complexity.
     * 
     * @return the configured level buttons grid panel
     */
    private JPanel createLevelButtonsGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(TASK_CONSTANTS.GRID_ROWS, TASK_CONSTANTS.GRID_COLS, 
            LAYOUT_CONSTANTS.GRID_SPACING_H, LAYOUT_CONSTANTS.GRID_SPACING_V));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, LAYOUT_CONSTANTS.GRID_PADDING, 0, LAYOUT_CONSTANTS.GRID_PADDING));
    
        // Create the level buttons
        Level[] allLevels = Level.values();
        levelButtons = new JButton[allLevels.length];
    
        for (int i = 0; i < allLevels.length; i++) {
            Level level = allLevels[i];
            Color buttonColor = (level.getLevelNumber() >= 5) ? UI_CONSTANTS.GOLD_ORANGE_COLOR : UI_CONSTANTS.BUTTON_COLOR;
            levelButtons[i] = createLevelButton(level, buttonColor);
            levelButtons[i].setEnabled(false);
            gridPanel.add(levelButtons[i]);
        }
        
        return gridPanel;
    }
    
    /**
     * Updates the visual state of grade selection buttons and enables appropriate levels.
     * 
     * @param selectedButton array containing the currently selected button reference
     * @param newSelection the newly selected button
     * @param otherButton the other grade button
     * @param isKS1 true if Key Stage 1 is selected
     */
    private void updateGradeSelection(JButton[] selectedButton, JButton newSelection, JButton otherButton, boolean isKS1) {
        // Update UI
        if (selectedButton[0] != null) {
            Color originalColor = selectedButton[0] == newSelection ? 
                (isKS1 ? UI_CONSTANTS.KS1_COLOR : UI_CONSTANTS.KS2_COLOR) : 
                (isKS1 ? UI_CONSTANTS.KS2_COLOR : UI_CONSTANTS.KS1_COLOR);
            selectedButton[0].setBackground(originalColor);
            selectedButton[0].setBorder(BorderFactory.createLineBorder(Color.GRAY, LAYOUT_CONSTANTS.HOVER_BORDER_WIDTH));
        }
        
        newSelection.setBackground((isKS1 ? UI_CONSTANTS.KS1_COLOR : UI_CONSTANTS.KS2_COLOR).darker());
        newSelection.setBorder(BorderFactory.createLineBorder(
            isKS1 ? UI_CONSTANTS.KS1_BORDER_COLOR : UI_CONSTANTS.KS2_BORDER_COLOR, LAYOUT_CONSTANTS.BORDER_WIDTH));
        selectedButton[0] = newSelection;
        
        // Update level buttons and prompt
        updateLevelButtons(isKS1);
        promptLabel.setText(isKS1 ? TEXT_CONSTANTS.KS1_SELECTED : TEXT_CONSTANTS.KS2_SELECTED);
        promptLabel.setForeground(Color.BLACK);
    }
    
    /**
     * Creates a grade selection button with custom styling and content.
     * 
     * @param title the main title text
     * @param subtitle the subtitle text
     * @param bgColor the background color
     * @return the configured grade button
     */
    private JButton createGradeButton(String title, String subtitle, Color bgColor) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, bgColor.brighter(), 
                    0, getHeight(), bgColor.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), LAYOUT_CONSTANTS.BUTTON_BORDER_RADIUS, LAYOUT_CONSTANTS.BUTTON_BORDER_RADIUS);
                
                // Draw border
                g2.setColor(bgColor.darker().darker());
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, LAYOUT_CONSTANTS.BUTTON_BORDER_RADIUS, LAYOUT_CONSTANTS.BUTTON_BORDER_RADIUS);
                
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        
        // Set button basic properties
        button.setLayout(new BorderLayout());
        button.setPreferredSize(SIZE_CONSTANTS.GRADE_BUTTON_SIZE);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Add text content
        JPanel textPanel = createGradeButtonContent(title, subtitle);
        button.add(textPanel, BorderLayout.CENTER);
        
        // Use common animated hover effect
        addAnimatedHoverEffect(button, bgColor);
        
        return button;
    }
    
    /**
     * Creates the content panel for a grade button with title and subtitle.
     * Extracted from createGradeButton to reduce method complexity.
     * 
     * @param title the main title text
     * @param subtitle the subtitle text
     * @return the configured content panel
     */
    private JPanel createGradeButtonContent(String title, String subtitle) {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_CONSTANTS.GRADE_TITLE_FONT);
        titleLabel.setForeground(UI_CONSTANTS.TITLE_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(FONT_CONSTANTS.GRADE_SUBTITLE_FONT);
        subtitleLabel.setForeground(UI_CONSTANTS.TITLE_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        textPanel.add(Box.createVerticalGlue());
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        textPanel.add(Box.createVerticalGlue());
        
        return textPanel;
    }

    /**
     * Updates level button availability based on selected key stage.
     * 
     * @param isKeyStage1 true if Key Stage 1 is selected
     */
    private void updateLevelButtons(boolean isKeyStage1) {
        this.isKeyStage1 = isKeyStage1;
        Level[] allLevels = Level.values();
        for (int i = 0; i < levelButtons.length && i < allLevels.length; i++) {
            Level level = allLevels[i];
            if (isKeyStage1) {
                levelButtons[i].setEnabled(level.isKeyStage1Available());
            } else {
                levelButtons[i].setEnabled(level.isKeyStage2Available());
            }
        }
        updateProgress();
    }

    /**
     * Creates a level selection button for a specific learning level.
     * 
     * @param level the Level enum value
     * @param backgroundColor the background color for the button
     * @return the configured level button
     */
    private JButton createLevelButton(Level level, Color backgroundColor) {
        JButton button = new JButton(level.getDisplayName());
        
        // Set smaller font for circle calculation module
        Font buttonFont = (level == Level.CIRCLE_CALCULATION) ? 
            FONT_CONSTANTS.LEVEL_BUTTON_SMALL_FONT : FONT_CONSTANTS.LEVEL_BUTTON_FONT;
        
        // Use common button styling method
        applyButtonStyle(button, backgroundColor, UI_CONSTANTS.BUTTON_TEXT_COLOR, buttonFont, SIZE_CONSTANTS.LEVEL_BUTTON_SIZE);

        // Use common hover effect
        addHoverEffect(button, backgroundColor);

        // Add click event
        button.addActionListener(e -> handleLevelButtonClick(level));

        return button;
    }

    /**
     * Handles level button click events by delegating to specific level handlers.
     * 
     * @param level the Level that was clicked
     */
    private void handleLevelButtonClick(Level level) {
        switch (level) {
            case SHAPE_IDENTIFICATION:
                handleShapeIdentificationLevel();
                break;
            
            case ANGLE_IDENTIFICATION:
                handleAngleIdentificationLevel();
                break;
                
            case AREA_CALCULATION:
                handleAreaCalculationLevel();
                break;
                
            case CIRCLE_CALCULATION:
                handleCircleCalculationLevel();
                break;

            case COMPOUND_AREA_CALCULATION:
                handleCompoundAreaCalculationLevel();
                break;

            case SECTOR_CALCULATION:
                handleSectorCalculationLevel();
                break;
            
            default:
                // Other levels reserved
                JOptionPane.showMessageDialog(mainFrame,
                    TEXT_CONSTANTS.DEVELOPMENT_MESSAGE,
                    TEXT_CONSTANTS.DEVELOPMENT_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Handles the shape identification level by showing the task selection dialog.
     * Extracted from handleLevelButtonClick to reduce method complexity.
     */
    private void handleShapeIdentificationLevel() {
        ShapeIdentification.showTaskSelectionDialog(mainFrame, MainApp.this);
        // Update main interface score
        int shapeScore = ShapeIdentification.getTotalScore();
        int newScore = shapeScore - lastShapeScore;
        if (newScore > 0) {
            currentScore += newScore;
            scoreLabel.setText(TEXT_CONSTANTS.SCORE_PREFIX + currentScore);
            updateProgress();
        }
        lastShapeScore = shapeScore;
    }
    
    /**
     * Handles the angle identification level by creating and showing the appropriate dialog.
     * Extracted from handleLevelButtonClick to reduce method complexity.
     */
    private void handleAngleIdentificationLevel() {
        JDialog dialog = createStandardDialog(Level.ANGLE_IDENTIFICATION.getDisplayName(), 
            WINDOW_CONSTANTS.DIALOG_WIDTH_SMALL, WINDOW_CONSTANTS.DIALOG_HEIGHT_SMALL);
        
        AngleTypeIdentification angleTypeIdentification = new AngleTypeIdentification(dialog, MainApp.this);
        dialog.add(angleTypeIdentification);
        
        // Use common score update listener
        int[] lastScoreArray = {lastAngleScore};
        addScoreUpdateListener(dialog, AngleTypeIdentification::getTotalScore, lastScoreArray);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                lastAngleScore = lastScoreArray[0];
            }
        });
        
        dialog.setVisible(true);
    }
    
    /**
     * Handles the area calculation level by creating and showing the appropriate dialog.
     * Extracted from handleLevelButtonClick to reduce method complexity.
     */
    private void handleAreaCalculationLevel() {
        JDialog dialog = createStandardDialog(Level.AREA_CALCULATION.getDisplayName(), 
            WINDOW_CONSTANTS.DIALOG_WIDTH_MEDIUM, WINDOW_CONSTANTS.DIALOG_HEIGHT_LARGE);
        
        ShapeAreaCalculation shapeAreaCalculation = new ShapeAreaCalculation(dialog, MainApp.this);
        dialog.add(shapeAreaCalculation);
        
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (shapeAreaCalculation.getCompletedShapes() != null) {
                    updateTask3CompletedShapes(shapeAreaCalculation.getCompletedShapes());
                }
                if (shapeAreaCalculation.getShapeScores() != null) {
                    updateTask3ShapeScores(shapeAreaCalculation.getShapeScores());
                }     
            }
        });
        
        dialog.setVisible(true);
    }
    
    /**
     * Handles the circle calculation level by creating and showing the appropriate dialog.
     * Extracted from handleLevelButtonClick to reduce method complexity.
     */
    private void handleCircleCalculationLevel() {
        JDialog dialog = createStandardDialog(Level.CIRCLE_CALCULATION.getDisplayName(), 
            WINDOW_CONSTANTS.DIALOG_WIDTH_MEDIUM, WINDOW_CONSTANTS.DIALOG_HEIGHT_MEDIUM);
        
        GeometryLearningSystem geometryLearningSystem = new GeometryLearningSystem(dialog, MainApp.this);
        dialog.add(geometryLearningSystem);
        
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                updateScore(GeometryLearningSystem.getTotalScore());
            }
        });
        
        dialog.setVisible(true);
    }
    
    /**
     * Handles the compound area calculation level by creating and showing the appropriate dialog.
     * Extracted from handleLevelButtonClick to reduce method complexity.
     */
    private void handleCompoundAreaCalculationLevel() {
        JDialog dialog = createStandardDialog(Level.COMPOUND_AREA_CALCULATION.getDisplayName(), 
            WINDOW_CONSTANTS.DIALOG_WIDTH_MEDIUM, WINDOW_CONSTANTS.DIALOG_HEIGHT_MEDIUM);
        
        CompoundShapeAreaCalculation compoundShapeAreaCalculation = new CompoundShapeAreaCalculation(dialog, MainApp.this);
        dialog.add(compoundShapeAreaCalculation);
        
        // Use common score update listener
        int[] lastScoreArray = {lastCompoundScore};
        addScoreUpdateListener(dialog, CompoundShapeAreaCalculation::getTotalScore, lastScoreArray);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                lastCompoundScore = lastScoreArray[0];
            }
        });
        
        dialog.setVisible(true);
    }
    
    /**
     * Handles the sector calculation level by creating and showing the appropriate dialog.
     * Extracted from handleLevelButtonClick to reduce method complexity.
     */
    private void handleSectorCalculationLevel() {
        JDialog dialog = createStandardDialog(Level.SECTOR_CALCULATION.getDisplayName(), 
            WINDOW_CONSTANTS.DIALOG_WIDTH_SMALL, WINDOW_CONSTANTS.DIALOG_HEIGHT_SMALL);
        
        SectorCalculationPractice sectorCalculationPractice = new SectorCalculationPractice(dialog, MainApp.this);
        dialog.add(sectorCalculationPractice);
        
        // Use common score update listener
        int[] lastScoreArray = {lastSectorScore};
        addScoreUpdateListener(dialog, SectorCalculationPractice::getTotalScore, lastScoreArray);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                lastSectorScore = lastScoreArray[0];
            }
        });
        
        dialog.setVisible(true);
    }

    /**
     * Creates the progress panel containing the progress bar and score display.
     * 
     * @return the configured progress panel
     */
    private JPanel createProgressPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(
            LAYOUT_CONSTANTS.PROGRESS_PANEL_PADDING, LAYOUT_CONSTANTS.PROGRESS_PANEL_PADDING, 
            LAYOUT_CONSTANTS.PROGRESS_PANEL_PADDING, LAYOUT_CONSTANTS.PROGRESS_PANEL_PADDING));

        // Add progress bar
        JLabel progressLabel = new JLabel(TEXT_CONSTANTS.PROGRESS_LABEL);
        progressLabel.setFont(FONT_CONSTANTS.PROGRESS_FONT);
        progressLabel.setForeground(UI_CONSTANTS.TITLE_COLOR);
        panel.add(progressLabel);
        panel.add(Box.createVerticalStrut(LAYOUT_CONSTANTS.SMALL_SPACING));

        progressBar = createCustomProgressBar();
        panel.add(progressBar);
        panel.add(Box.createVerticalStrut(LAYOUT_CONSTANTS.COMPONENT_SPACING));

        // Add score display
        JPanel scorePanel = createScorePanel();
        panel.add(scorePanel);

        return panel;
    }
    
    /**
     * Creates a custom-styled progress bar with gradient colors and rounded corners.
     * Extracted from createProgressPanel to reduce method complexity.
     * 
     * @return the configured custom progress bar
     */
    private JProgressBar createCustomProgressBar() {
        JProgressBar progressBar = new JProgressBar(PROGRESS_CONSTANTS.PROGRESS_MIN, PROGRESS_CONSTANTS.PROGRESS_MAX) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded track
                int trackHeight = SIZE_CONSTANTS.PROGRESS_BAR_HEIGHT;
                int arc = SIZE_CONSTANTS.PROGRESS_BAR_ARC;
                g2d.setColor(UI_CONSTANTS.PROGRESS_TRACK_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), trackHeight, arc, arc);
                
                // Draw gradient progress
                if (getValue() > 0) {
                    int progressWidth = (int) (getWidth() * ((double) getValue() / getMaximum()));
                    GradientPaint gp = new GradientPaint(0, 0, UI_CONSTANTS.PROGRESS_START_COLOR, 
                                                        progressWidth, 0, UI_CONSTANTS.PROGRESS_END_COLOR);
                    g2d.setPaint(gp);
                    g2d.fillRoundRect(0, 0, progressWidth, trackHeight, arc, arc);
                }
                
                // Draw text
                g2d.setColor(Color.BLACK);
                g2d.setFont(FONT_CONSTANTS.PROGRESS_BAR_FONT);
                FontMetrics fm = g2d.getFontMetrics();
                String text = String.format("%.1f%%", (double)getValue());
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((trackHeight - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, x, y);
                
                // Draw border
                g2d.setColor(UI_CONSTANTS.PROGRESS_BORDER_COLOR);
                g2d.drawRoundRect(0, 0, getWidth()-1, trackHeight-1, arc, arc);
            }
        };
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("0.0%");
        progressBar.setPreferredSize(SIZE_CONSTANTS.PROGRESS_BAR_SIZE);
        progressBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        return progressBar;
    }
    
    /**
     * Creates the score display panel.
     * Extracted from createProgressPanel to reduce method complexity.
     * 
     * @return the configured score panel
     */
    private JPanel createScorePanel() {
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setOpaque(false);
        
        scoreLabel = new JLabel(TEXT_CONSTANTS.SCORE_PREFIX + currentScore);
        scoreLabel.setFont(FONT_CONSTANTS.SCORE_FONT);
        scoreLabel.setForeground(UI_CONSTANTS.SCORE_COLOR);
        
        scorePanel.add(scoreLabel, BorderLayout.CENTER);
        return scorePanel;
    }

    /**
     * Creates the bottom panel containing the exit button.
     * 
     * @return the configured bottom panel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        JButton exitButton = createStyledButton(TEXT_CONSTANTS.EXIT_BUTTON_TEXT);
        exitButton.addActionListener(e -> showExitConfirmation());

        panel.add(exitButton);
        return panel;
    }

    /**
     * Creates a styled button with standard appearance and hover effects.
     * 
     * @param text the button text
     * @return the configured styled button
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        
        // Use common button styling method
        applyButtonStyle(button, UI_CONSTANTS.BUTTON_COLOR, UI_CONSTANTS.BUTTON_TEXT_COLOR, 
            FONT_CONSTANTS.BUTTON_FONT, SIZE_CONSTANTS.STYLED_BUTTON_SIZE);
        
        // Use common hover effect
        addHoverEffect(button, UI_CONSTANTS.BUTTON_COLOR);

        return button;
    }
    
    /**
     * Updates the current score by adding the specified points.
     * 
     * @param score the points to add to the current score
     */
    private void updateScore(int score) {
        currentScore += score;
        scoreLabel.setText(TEXT_CONSTANTS.SCORE_PREFIX + currentScore);
        updateProgress();
    }

    /**
     * Shows the exit confirmation dialog and exits if confirmed.
     */
    private void showExitConfirmation() {
        int choice = JOptionPane.showConfirmDialog(
            mainFrame,
            "You have achieved " + currentScore + " points in this session.\nAre you sure to end the session？",
            TEXT_CONSTANTS.EXIT_CONFIRMATION_TITLE,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // ==================== MAIN METHOD ====================
    
    /**
     * Main entry point for the Shapeville application.
     * Initializes the look and feel and creates the main application window.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainApp();
            }
        });
    }
}