import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Random;

/**
 * Abstract base class for all geometric shapes in the application.
 * Provides common functionality for shape rendering, area calculation,
 * and visual representation.
 * @author Yuhong Wang
 * @version 1.0
 */
abstract class AbstractShape {
   /** Array containing the dimensional values for the shape (length, width, height, etc.) */
   protected int[] dimensions;
    
   /** Flag indicating whether to display the solution and formula */
   protected boolean showSolution;
   
   /** Standard fill color for all shapes */
   protected static final Color SHAPE_FILL_COLOR = new Color(230, 242, 255);
   
   /** Standard outline color for all shapes */
   protected static final Color SHAPE_LINE_COLOR = new Color(65, 156, 251);
   
   /** Color used for length dimension indicators */
   protected static final Color LENGTH_COLOR = new Color(75, 196, 75);
   
   /** Color used for width dimension indicators */
   protected static final Color WIDTH_COLOR = new Color(0, 175, 185);
   
   /** Color used for base dimension indicators */
   protected static final Color BASE_COLOR = new Color(255, 153, 0);
   
   /** Color used for height dimension indicators */
   protected static final Color HEIGHT_COLOR = new Color(153, 51, 255);
   
   /** Color used for top dimension indicators (trapezoid) */
   protected static final Color TOP_COLOR = new Color(255, 64, 129);
    /**
     * Constructs an AbstractShape with specified dimensions and solution visibility.
     * 
     * @param dimensions Array containing the shape's dimensional values
     * @param showSolution Whether to display the solution and formula
     */
    public AbstractShape(int[] dimensions, boolean showSolution) {
        this.dimensions = dimensions;
        this.showSolution = showSolution;
    }
    
    /**
     * Calculates the area of the shape.
     * Must be implemented by concrete shape classes.
     * 
     * @return The calculated area as a double value
     */
    public abstract double calculateArea();
    
    /**
     * Draws the shape on the graphics context.
     * Must be implemented by concrete shape classes.
     * 
     * @param g2d Graphics2D context for drawing
     * @param panelWidth Width of the drawing panel
     * @param panelHeight Height of the drawing panel
     */
    public abstract void drawShape(Graphics2D g2d, int panelWidth, int panelHeight);
    
    /**
     * Draws the area calculation formula for the shape.
     * Must be implemented by concrete shape classes.
     * 
     * @param g2d Graphics2D context for drawing
     */
    public abstract void drawFormula(Graphics2D g2d);
    
    /**
     * Draws a horizontal arrow with arrowheads at both ends.
     * Used for dimension indicators.
     * 
     * @param g2d Graphics2D context
     * @param x1 Starting x-coordinate
     * @param y1 Starting y-coordinate
     * @param x2 Ending x-coordinate
     * @param y2 Ending y-coordinate
     */
    protected void drawHorizontalArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x1, y1, x2, y2);
        
        int arrowSize = 10;
        g2d.fillPolygon(
            new int[] {x1, x1 + arrowSize, x1 + arrowSize}, 
            new int[] {y1, y1 - arrowSize/2, y1 + arrowSize/2}, 
            3);
        g2d.fillPolygon(
            new int[] {x2, x2 - arrowSize, x2 - arrowSize}, 
            new int[] {y2, y2 - arrowSize/2, y2 + arrowSize/2}, 
            3);
    }
    
    /**
     * Draws a vertical arrow with arrowheads at both ends.
     * Used for dimension indicators.
     * 
     * @param g2d Graphics2D context
     * @param x1 Starting x-coordinate
     * @param y1 Starting y-coordinate
     * @param x2 Ending x-coordinate
     * @param y2 Ending y-coordinate
     */
    protected void drawVerticalArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x1, y1, x2, y2);
        
        int arrowSize = 10;
        g2d.fillPolygon(
            new int[] {x1, x1 - arrowSize/2, x1 + arrowSize/2}, 
            new int[] {y1, y1 + arrowSize, y1 + arrowSize}, 
            3);
        g2d.fillPolygon(
            new int[] {x2, x2 - arrowSize/2, x2 + arrowSize/2}, 
            new int[] {y2, y2 - arrowSize, y2 - arrowSize}, 
            3);
    }
}

/**
 * Concrete implementation of AbstractShape for rectangles.
 * Handles rectangle-specific area calculation and rendering.
 */
class RectangleShape extends AbstractShape {
    /**
     * Constructs a RectangleShape with specified dimensions.
     * 
     * @param dimensions Array containing length and width
     * @param showSolution Whether to show the solution
     */
    public RectangleShape(int[] dimensions, boolean showSolution) {
        super(dimensions, showSolution);
    }
    
    /**
     * Calculates the area of the rectangle.
     * 
     * @return Area calculated as length × width
     */
    @Override
    public double calculateArea() {
        return dimensions[0] * dimensions[1];
    }
    
    /**
     * Draws the rectangle shape with optional dimension indicators.
     * 
     * @param g2d Graphics2D context
     * @param panelWidth Panel width
     * @param panelHeight Panel height
     */
    @Override
    public void drawShape(Graphics2D g2d, int panelWidth, int panelHeight) {
        int rectWidth = dimensions[0] * 10;
        int rectHeight = dimensions[1] * 10;
        int x = -rectWidth / 2 + 50;
        int y = -rectHeight / 2;

        g2d.setColor(SHAPE_FILL_COLOR);
        g2d.fillRect(x, y, rectWidth, rectHeight);
        g2d.setColor(SHAPE_LINE_COLOR);
        g2d.drawRect(x, y, rectWidth, rectHeight);
        
        if (showSolution) {
            g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
            
            g2d.setColor(WIDTH_COLOR);
            drawVerticalArrow(g2d, x - 20, y, x - 20, y + rectHeight);
            g2d.drawString("WIDTH", x - 65, y + rectHeight/2 + 5);

            g2d.setColor(LENGTH_COLOR);
            drawHorizontalArrow(g2d, x, y + rectHeight + 20, x + rectWidth, y + rectHeight + 20);
            g2d.drawString("LENGTH", x + rectWidth/2 - 40, y + rectHeight + 40);
        }
    }
    
    /**
     * Draws the rectangle area formula with color-coded variables.
     * 
     * @param g2d Graphics2D context
     */
    @Override
    public void drawFormula(Graphics2D g2d) {
        int fontSize = 18;
        int lineSpacing = 40;
        int y = 0; 
        
        g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, fontSize));
        g2d.setColor(Color.GRAY);
        g2d.drawString("AREA OF RECTANGLE:", 0, y);
        y += lineSpacing;

        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("AREA = ", 0, y);
        
        g2d.setColor(LENGTH_COLOR);
        g2d.drawString("l", 80, y);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(" × ", 90, y);
        g2d.setColor(WIDTH_COLOR);
        g2d.drawString("w", 120, y);
        y += lineSpacing;
        
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        g2d.setColor(new Color(0, 128, 0));
        String calculation = "A = " + dimensions[0] + " × " + dimensions[1] + " = " + calculateArea();
        g2d.drawString(calculation, 0, y);
    }
}

/**
 * Concrete implementation of AbstractShape for parallelograms.
 * Handles parallelogram-specific area calculation and rendering.
 */
class ParallelogramShape extends AbstractShape {
    /**
     * Constructs a ParallelogramShape with specified dimensions.
     * 
     * @param dimensions Array containing base and height
     * @param showSolution Whether to show the solution
     */
    public ParallelogramShape(int[] dimensions, boolean showSolution) {
        super(dimensions, showSolution);
    }
    
    /**
     * Calculates the area of the parallelogram.
     * 
     * @return Area calculated as base × height
     */
    @Override
    public double calculateArea() {
        return dimensions[0] * dimensions[1];
    }
    
    /**
     * Draws the parallelogram shape with optional dimension indicators.
     * 
     * @param g2d Graphics2D context
     * @param panelWidth Panel width
     * @param panelHeight Panel height
     */
    @Override
    public void drawShape(Graphics2D g2d, int panelWidth, int panelHeight) {
        int baseWidth = dimensions[0] * 10;
        int pHeight = dimensions[1] * 10;
        int offset = dimensions[0] * 3;  
        int x = -(baseWidth + offset) / 2 + 50;
        int y = -pHeight / 2;
        int[] xPoints = {x + offset, x + offset + baseWidth, x + baseWidth, x};
        int[] yPoints = {y, y, y + pHeight, y + pHeight};
        
        g2d.setColor(SHAPE_FILL_COLOR);
        g2d.fillPolygon(xPoints, yPoints, 4);
        g2d.setColor(SHAPE_LINE_COLOR);
        g2d.drawPolygon(xPoints, yPoints, 4);
        
        if (showSolution) {
            g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
            
            g2d.setColor(HEIGHT_COLOR);
            Stroke dashedStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
            Stroke originalStroke = g2d.getStroke();
            g2d.setStroke(dashedStroke);
            g2d.drawLine(x + offset + baseWidth / 2, y, x + offset + baseWidth / 2, y + pHeight);
            g2d.setStroke(originalStroke);
            
            drawVerticalArrow(g2d, x + offset + baseWidth / 2 + 20, y, x + offset + baseWidth / 2 + 20, y + pHeight);
            g2d.drawString("HEIGHT", x + offset + baseWidth / 2 + 30, y + pHeight / 2 + 5);
            
            g2d.setColor(BASE_COLOR);
            drawHorizontalArrow(g2d, x, y + pHeight + 20, x + baseWidth, y + pHeight + 20);
            g2d.drawString("BASE", x + baseWidth / 2 - 30, y + pHeight + 40);
        }
    }
    
    /**
     * Draws the parallelogram area formula with color-coded variables.
     * 
     * @param g2d Graphics2D context
     */
    @Override
    public void drawFormula(Graphics2D g2d) {
        int fontSize = 18;
        int lineSpacing = 40;
        int y = 0;
        
        g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, fontSize));
        g2d.setColor(Color.GRAY);
        g2d.drawString("AREA OF PARALLELOGRAM:", 0, y);
        y += lineSpacing;
        
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("AREA = ", 0, y);
        
        g2d.setColor(BASE_COLOR);
        g2d.drawString("b", 80, y);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(" × ", 90, y);
        g2d.setColor(HEIGHT_COLOR);
        g2d.drawString("h", 120, y);
        y += lineSpacing;
        
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        g2d.setColor(new Color(0, 128, 0));
        String calculation = "A = " + dimensions[0] + " × " + dimensions[1] + " = " + calculateArea();
        g2d.drawString(calculation, 0, y);
    }
}

/**
 * Concrete implementation of AbstractShape for triangles.
 * Handles triangle-specific area calculation and rendering.
 */
class TriangleShape extends AbstractShape {
    /**
     * Constructs a TriangleShape with specified dimensions.
     * 
     * @param dimensions Array containing base and height
     * @param showSolution Whether to show the solution
     */
    public TriangleShape(int[] dimensions, boolean showSolution) {
        super(dimensions, showSolution);
    }
    
    /**
     * Calculates the area of the triangle.
     * 
     * @return Area calculated as (base × height) / 2
     */
    @Override
    public double calculateArea() {
        return (dimensions[0] * dimensions[1]) / 2.0;
    }
    
    /**
     * Draws the triangle shape with optional dimension indicators.
     * 
     * @param g2d Graphics2D context
     * @param panelWidth Panel width
     * @param panelHeight Panel height
     */
    @Override
    public void drawShape(Graphics2D g2d, int panelWidth, int panelHeight) {
        int baseWidth = dimensions[0] * 10;
        int tHeight = dimensions[1] * 10;
        
        int x = -baseWidth / 2 + 50;
        int y = tHeight / 2; 
        
        int[] xPoints = {x, x + baseWidth / 2, x + baseWidth};
        int[] yPoints = {y, y - tHeight, y};
        
        g2d.setColor(SHAPE_FILL_COLOR);
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(SHAPE_LINE_COLOR);
        g2d.drawPolygon(xPoints, yPoints, 3);
        
        if (showSolution) {
            g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
            
            g2d.setColor(HEIGHT_COLOR);
            Stroke dashedStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
            Stroke originalStroke = g2d.getStroke();
            g2d.setStroke(dashedStroke);
            g2d.drawLine(x + baseWidth/2, y, x + baseWidth/2, y - tHeight);
            g2d.setStroke(originalStroke);
            
            drawVerticalArrow(g2d, x + baseWidth/2 + 20, y - tHeight, x + baseWidth/2 + 20, y);
            g2d.drawString("HEIGHT", x + baseWidth/2 + 30, y - tHeight/2);
            
            g2d.setColor(BASE_COLOR);
            drawHorizontalArrow(g2d, x, y + 20, x + baseWidth, y + 20);
            g2d.drawString("BASE", x + baseWidth / 2 - 30, y + 40);
        }
    }
    
    /**
     * Draws the triangle area formula with color-coded variables.
     * 
     * @param g2d Graphics2D context
     */
    @Override
    public void drawFormula(Graphics2D g2d) {
        int fontSize = 18;
        int lineSpacing = 40;
        int y = 0;
        
        g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, fontSize));
        g2d.setColor(Color.GRAY);
        g2d.drawString("AREA OF TRIANGLE:", 0, y);
        y += lineSpacing;
        
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("AREA = ", 0, y);
        
        int fractionX = 80;
        g2d.drawString("1", fractionX, y - 10);
        g2d.drawLine(fractionX, y - 5, fractionX + 15, y - 5);
        g2d.drawString("2", fractionX, y + 15);
        
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(" × ", 100, y);
        g2d.setColor(BASE_COLOR);
        g2d.drawString("b", 125, y);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(" × ", 135, y);
        g2d.setColor(HEIGHT_COLOR);
        g2d.drawString("h", 155, y);
        y += lineSpacing;
        
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        g2d.setColor(new Color(0, 128, 0));
        String calculation = String.format("A = 1/2 × %d × %d = %.1f", 
            dimensions[0], dimensions[1], calculateArea());
        g2d.drawString(calculation, 0, y);
    }
}

/**
 * Concrete implementation of AbstractShape for trapezoids.
 * Handles trapezoid-specific area calculation and rendering.
 */
class TrapezoidShape extends AbstractShape {
    /**
     * Constructs a TrapezoidShape with specified dimensions.
     * 
     * @param dimensions Array containing bottom base, top base, and height
     * @param showSolution Whether to show the solution
     */
    public TrapezoidShape(int[] dimensions, boolean showSolution) {
        super(dimensions, showSolution);
    }
    
    /**
     * Calculates the area of the trapezoid.
     * 
     * @return Area calculated as ((base1 + base2) × height) / 2
     */
    @Override
    public double calculateArea() {
        return ((dimensions[0] + dimensions[1]) * dimensions[2]) / 2.0;
    }
    
    /**
     * Draws the trapezoid shape with optional dimension indicators.
     * 
     * @param g2d Graphics2D context
     * @param panelWidth Panel width
     * @param panelHeight Panel height
     */
    @Override
    public void drawShape(Graphics2D g2d, int panelWidth, int panelHeight) {
        int bottomWidth = dimensions[0] * 10;
        int topWidth = dimensions[1] * 10;
        int tHeight = dimensions[2] * 10;
        
        /** Offset 50 units to the right */
        int bottomX = -bottomWidth / 2 + 50;
        int topX = -topWidth / 2 + 50;
        int y = -tHeight / 2;
        
        int[] xPoints = {bottomX, bottomX + bottomWidth, topX + topWidth, topX};
        int[] yPoints = {y + tHeight, y + tHeight, y, y};
        
        g2d.setColor(SHAPE_FILL_COLOR);
        g2d.fillPolygon(xPoints, yPoints, 4);
        g2d.setColor(SHAPE_LINE_COLOR);
        g2d.drawPolygon(xPoints, yPoints, 4);
        
        if (showSolution) {
            g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
            
            g2d.setColor(HEIGHT_COLOR);
            Stroke dashedStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
            Stroke originalStroke = g2d.getStroke();
            g2d.setStroke(dashedStroke);
            int midPointX = (bottomX + bottomWidth/2 + topX + topWidth/2) / 2;
            g2d.drawLine(midPointX, y, midPointX, y + tHeight);
            g2d.setStroke(originalStroke);
            
            drawVerticalArrow(g2d, bottomX + bottomWidth + 20, y, bottomX + bottomWidth + 20, y + tHeight);
            g2d.drawString("HEIGHT", bottomX + bottomWidth + 30, y + tHeight / 2 + 5);
            
            g2d.setColor(TOP_COLOR);
            drawHorizontalArrow(g2d, topX, y - 20, topX + topWidth, y - 20);
            g2d.drawString("a", topX + topWidth / 2 - 5, y - 30);
            
            g2d.setColor(BASE_COLOR);
            drawHorizontalArrow(g2d, bottomX, y + tHeight + 20, bottomX + bottomWidth, y + tHeight + 20);
            g2d.drawString("b", bottomX + bottomWidth / 2 - 5, y + tHeight + 40);
        }
    }
    
    /**
     * Draws the trapezoid area formula with color-coded variables.
     * 
     * @param g2d Graphics2D context
     */
    @Override
    public void drawFormula(Graphics2D g2d) {
        int fontSize = 18;
        int lineSpacing = 40;
        int y = 0;
        
        g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, fontSize));
        g2d.setColor(Color.GRAY);
        g2d.drawString("AREA OF TRAPEZIUM:", 0, y);
        y += lineSpacing;
        
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("AREA = ", 0, y);
        
        g2d.drawString("(", 80, y-15);
        g2d.setColor(TOP_COLOR);
        g2d.drawString("a", 90, y-15);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(" + ", 100, y-15);
        g2d.setColor(BASE_COLOR);
        g2d.drawString("b", 125, y-15);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(")", 135, y-15);
        
        g2d.drawLine(80, y - 5, 140, y - 5);
        g2d.drawString("2", 105, y + 15);
        
        g2d.drawString(" × ", 145, y);
        g2d.setColor(HEIGHT_COLOR);
        g2d.drawString("h", 170, y);
        y += lineSpacing;
        
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        g2d.setColor(new Color(0, 128, 0));
        String calculation = String.format("A = (%d + %d)/2 × %d = %.1f", 
            dimensions[1], dimensions[0], dimensions[2], calculateArea());
        g2d.drawString(calculation, 0, y);
    }
}

/**
 * Main panel class for the Shape Area Calculation educational game.
 * Manages the UI, game logic, scoring, and timer functionality.
 */
public class ShapeAreaCalculation extends JPanel {
    /** Constants */
    private static final int MAX_ATTEMPTS = 3;
    private static final int TIME_LIMIT = 180; /** 3 minutes = 180 seconds */
    private static final Color BACKGROUND_COLOR = new Color(230, 240, 255);
    private static final Color SHAPE_FILL_COLOR = new Color(230, 242, 255);
    private static final Color SHAPE_LINE_COLOR = new Color(65, 156, 251);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color COMPLETED_BUTTON_COLOR = new Color(50, 128, 200);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Color CORRECT_COLOR = new Color(40, 167, 69);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Color CONDITION_TEXT_COLOR = new Color(220, 53, 69);
    private static final Color COMPLETED_TEXT_COLOR = new Color(255, 255, 150);
    private static final Color LENGTH_COLOR = new Color(75, 196, 75);
    private static final Color WIDTH_COLOR = new Color(0, 175, 185);
    private static final Color BASE_COLOR = new Color(255, 153, 0);
    private static final Color HEIGHT_COLOR = new Color(153, 51, 255);
    private static final Color TOP_COLOR = new Color(255, 64, 129);

    /**
     * Enumeration of available shape types.
     */
    private enum ShapeType {
        RECTANGLE("Rectangle"),
        PARALLELOGRAM("Parallelogram"),
        TRIANGLE("Triangle"),
        TRAPEZOID("Trapezoid");

        private final String displayName;

        ShapeType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** UI Components */
    private JPanel mainPanel;
    private JPanel selectionPanel;
    private JPanel calculationPanel;
    private JTextField answerField;
    private JButton submitButton;
    private JLabel feedbackLabel;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JButton changeShapeButton;
    private ShapeDrawingPanel drawingPanel;
    private JButton[] shapeButtons;

    /** State variables */
    private ShapeType currentShape;
    private int currentScore = 0;
    private int[] dimensions = new int[3]; /** Store shape dimension parameters */
    private Random random = new Random();
    private Timer countdownTimer;
    private int remainingSeconds;
    private int attemptCount = 0; /** Number of attempts for current problem */
    private boolean showSolution = false; /** Whether to show the solution */
    private JDialog parentDialog; /** Store parent dialog reference */
    private MainApp mainApp; /** Reference to MainApp */
    private boolean[] completedShapes; /** Record whether each shape has been completed */
    private int[] shapeScores; /** Track scores for each shape */
    private int completedCount = 0; /** Number of completed shapes */
    private boolean[] progressUpdated; /** Track whether progress has been updated for each shape */

    /** 1. Constructor and initialization related methods */
    
    /**
     * Main constructor for ShapeAreaCalculation panel.
     * Initializes the UI and loads saved state if available.
     * 
     * @param parent Parent dialog window
     * @param mainApp Reference to main application
     * @param savedCompletedShapes Previously completed shapes
     * @param savedShapeScores Previously earned scores
     */
    public ShapeAreaCalculation(JDialog parent, MainApp mainApp, boolean[] savedCompletedShapes, int[] savedShapeScores) {
        this.parentDialog = parent;
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        /** Initialize state tracking arrays */
        completedShapes = new boolean[ShapeType.values().length];
        shapeScores = new int[ShapeType.values().length];
        progressUpdated = new boolean[ShapeType.values().length];
        shapeButtons = new JButton[ShapeType.values().length];
        
        /** Load saved state if available */
        if (savedCompletedShapes != null) {
            for (int i = 0; i < completedShapes.length && i < savedCompletedShapes.length; i++) {
                completedShapes[i] = savedCompletedShapes[i];
            }
            
            /** Update completed count based on saved state */
            completedCount = 0;
            for (boolean completed : completedShapes) {
                if (completed) completedCount++;
            }
        }

        if (savedShapeScores != null) {
            for (int i = 0; i < shapeScores.length && i < savedShapeScores.length; i++) {
                shapeScores[i] = savedShapeScores[i];
                currentScore += shapeScores[i]; /** Add up the saved scores to current score */
            }
        }
        
        /** Setup UI */
        mainPanel = new JPanel(new CardLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        createSelectionPanel();
        createCalculationPanel();

        /** Add both panels to the main panel */
        mainPanel.add(selectionPanel, "selection");
        mainPanel.add(calculationPanel, "calculation");

        add(mainPanel, BorderLayout.CENTER);
        
        /** Show selection panel by default */
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "selection");
        
        /** Create countdown timer */
        createCountdownTimer();
        updateScoreDisplay();
    }

    /**
     * Alternative constructor that uses the main constructor.
     * Loads saved state from MainApp if available.
     * 
     * @param parent Parent dialog window
     * @param mainApp Reference to main application
     */
    public ShapeAreaCalculation(JDialog parent, MainApp mainApp) {
        this(parent, mainApp, 
             mainApp != null ? mainApp.getTask3CompletedShapes() : null,
             mainApp != null ? mainApp.getTask3ShapeScores() : null);
    }
    
    /** 2. Getter/Setter methods */
    
    /**
     * Gets the array of completed shapes.
     * 
     * @return Array indicating which shapes have been completed
     */
    public boolean[] getCompletedShapes() {
        return completedShapes;
    }
    
    /**
     * Gets the array of shape scores.
     * 
     * @return Array containing scores earned for each shape
     */
    public int[] getShapeScores() {
        return shapeScores;
    }
    
    /** 3. UI creation methods */
    
    /**
     * Creates the shape selection panel UI.
     * This panel displays buttons for each shape type that users can select.
     */
    private void createSelectionPanel() {
        selectionPanel = new JPanel(new BorderLayout(20, 20));
        selectionPanel.setBackground(BACKGROUND_COLOR);
        
        /** Create exit button to close the window */
        JButton exitButton = createStyledButton("Home");
        exitButton.setPreferredSize(new Dimension(90, 40));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainApp != null) {
                    mainApp.updateTask3CompletedShapes(completedShapes);
                    mainApp.updateTask3ShapeScores(shapeScores);
                }
                /** Close the parent dialog or frame */
                if (parentDialog != null) {
                    parentDialog.dispose();
                } else {
                    /** If running as standalone, find the parent window */
                    Window window = SwingUtilities.getWindowAncestor(ShapeAreaCalculation.this);
                    if (window != null) {
                        window.dispose();
                    }
                }
            }
        });
        
        /** Create title */
        JLabel titleLabel = new JLabel("Shape Area Calculation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        titleLabel.setForeground(new Color(40, 44, 52));
        
        /** Create instructions */
        JLabel instructionLabel = new JLabel("Please select a shape for area calculation:", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        instructionLabel.setForeground(new Color(70, 70, 70));
        
        /** Header panel with exit button and title */
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.add(exitButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        
        /** Create button panel for shape selection */
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        
        /** Create selection buttons for each shape */
        ShapeType[] shapes = ShapeType.values();
        for (int i = 0; i < shapes.length; i++) {
            final ShapeType shapeType = shapes[i];
            JButton shapeButton = new JButton(shapeType.getDisplayName());
            shapeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
            shapeButton.setBackground(BUTTON_COLOR);
            shapeButton.setForeground(BUTTON_TEXT_COLOR);
            shapeButton.setFocusPainted(false);
            shapeButton.setBorderPainted(false);
            shapeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            shapeButton.setPreferredSize(new Dimension(180, 120));
            
            /** Add icon to button */
            shapeButton.setIcon(new ShapeButtonIcon(shapeType, 160, 80));
            shapeButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            shapeButton.setHorizontalTextPosition(SwingConstants.CENTER);
            shapeButton.setIconTextGap(10);
            
            /** Check if already completed */
            if (completedShapes[i]) {
                markButtonAsCompleted(shapeButton, shapeScores[i]);
            } else {
                shapeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        currentShape = shapeType;
                        generateShapeQuestion();
                        CardLayout cl = (CardLayout) mainPanel.getLayout();
                        cl.show(mainPanel, "calculation");
                        startTimer(); /** Start timer */
                    }
                });
            }
            
            /** Add hover effect */
            shapeButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    shapeButton.setBackground(BUTTON_COLOR.darker());
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    shapeButton.setBackground(BUTTON_COLOR);
                }
            });
            
            /** Save button reference */
            shapeButtons[i] = shapeButton;
            buttonPanel.add(shapeButton);
        }
        
        /** Add components to selection panel */
        JPanel titleContentPanel = new JPanel(new BorderLayout());
        titleContentPanel.setBackground(BACKGROUND_COLOR);
        titleContentPanel.add(headerPanel, BorderLayout.NORTH);
        titleContentPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        titleContentPanel.add(instructionLabel, BorderLayout.SOUTH);
        titleContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        selectionPanel.add(titleContentPanel, BorderLayout.NORTH);
        selectionPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the calculation panel UI.
     * This panel displays the shape, formula, and answer input area.
     */
    private void createCalculationPanel() {
        calculationPanel = new JPanel(new BorderLayout(20, 20));
        calculationPanel.setBackground(BACKGROUND_COLOR);
        
        /** Create title panel */
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        titleLabel.setForeground(new Color(40, 44, 52));
        
        /** Create exit button */
        JButton exitButton = new JButton("Home");
        exitButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        exitButton.setPreferredSize(new Dimension(90, 40));
        exitButton.setBackground(BUTTON_COLOR);
        exitButton.setForeground(BUTTON_TEXT_COLOR);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        
        /** Apply rounded corners to exit button */
        exitButton.putClientProperty("JButton.buttonType", "roundRect");
        exitButton.setToolTipText("Exit the application");
        
        /** Add action to exit button */
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                countdownTimer.stop(); /** Stop timer */
                
                if (mainApp != null) {
                    mainApp.updateTask3CompletedShapes(completedShapes);
                    mainApp.updateTask3ShapeScores(shapeScores);
                }
                
                /** Close the parent dialog or frame */
                if (parentDialog != null) {
                    parentDialog.dispose();
                } else {
                    /** If running as standalone, find the parent window */
                    Window window = SwingUtilities.getWindowAncestor(ShapeAreaCalculation.this);
                    if (window != null) {
                        window.dispose();
                    }
                }
            }
        });
        
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(BUTTON_COLOR.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(BUTTON_COLOR);
            }
        });
        
        /** Create timer label with clock icon */
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timerPanel.setBackground(BACKGROUND_COLOR);
        
        timerLabel = new JLabel("Time remaining: 03:00");
        timerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        timerPanel.add(timerLabel);
        
        /** Create score label */
        scoreLabel = new JLabel("Score: " + currentScore, SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        scoreLabel.setForeground(new Color(40, 44, 52));
        
        /** Create panel for header elements */
        JPanel headerContentPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerContentPanel.setBackground(BACKGROUND_COLOR);
        headerContentPanel.add(timerPanel);
        headerContentPanel.add(scoreLabel);
        
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.add(exitButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(headerContentPanel, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        /** Create shape display panel */
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        
        /** Shape display panel with increased size */
        drawingPanel = new ShapeDrawingPanel();
        drawingPanel.setPreferredSize(new Dimension(700, 400));
        contentPanel.add(drawingPanel, BorderLayout.CENTER);
        
        /** Create answer input panel (at the bottom) */
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JPanel answerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        answerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel answerLabel = new JLabel("Area: ");
        answerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        
        /** Styled text field */
        answerField = new JTextField(10);
        answerField.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        answerField.setHorizontalAlignment(JTextField.RIGHT);
        answerField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        /** Create styled submit button */
        submitButton = createStyledButton("Submit");
        submitButton.setPreferredSize(new Dimension(120, 40));
        submitButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });

        /** Add Enter key event */
        answerField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });

        answerPanel.add(answerLabel);
        answerPanel.add(answerField);
        answerPanel.add(submitButton);
        
        /** Create change shape button panel */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setVisible(false); /** Initially invisible, shown after answering */
        
        changeShapeButton = createStyledButton("Change to Another Shape");
        /** Increase button width to fit longer text */
        changeShapeButton.setPreferredSize(new Dimension(300, 45));
        changeShapeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        changeShapeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /** Check if all shapes are completed */
                boolean allCompleted = true;
                for (boolean completed : completedShapes) {
                    if (!completed) {
                        allCompleted = false;
                        break;
                    }
                }
                
                if (allCompleted) {
                    /** All shapes are completed */
                    countdownTimer.stop();
                    
                    /** Show completion dialog */
                    showCompletionDialog();
                    
                    /** Return to main app after user confirmation */
                    if (parentDialog != null) {
                        parentDialog.dispose();
                    }
                } else {
                    /** Return to shape selection screen */
                    CardLayout cl = (CardLayout) mainPanel.getLayout();
                    cl.show(mainPanel, "selection");
                    buttonPanel.setVisible(false);
                }
            }
        });
        
        buttonPanel.add(changeShapeButton);
        
        /** Create feedback panel */
        JPanel feedbackPanel = new JPanel(new BorderLayout());
        feedbackPanel.setBackground(BACKGROUND_COLOR);
        feedbackPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        feedbackLabel = new JLabel();
        feedbackLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        feedbackPanel.add(feedbackLabel, BorderLayout.CENTER);
        
        inputPanel.add(answerPanel);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(buttonPanel);
        inputPanel.add(feedbackPanel);
        
        /** Add all panels to calculation panel */
        calculationPanel.add(headerPanel, BorderLayout.NORTH);
        calculationPanel.add(contentPanel, BorderLayout.CENTER);
        calculationPanel.add(inputPanel, BorderLayout.SOUTH);
    }
    
    /** 4. UI update methods */
    
    /**
     * Updates the score display with the current score.
     */
    private void updateScoreDisplay() {
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + currentScore);
        }
    }
    
    /**
     * Marks a button as completed with visual indicators and score display.
     * 
     * @param button The button to mark as completed
     * @param earnedPoints Points earned for this shape
     */
    private void markButtonAsCompleted(JButton button, int earnedPoints) {
        button.setBackground(COMPLETED_BUTTON_COLOR);
        /** Remove existing text, set complete text with points */
        String originalText = button.getText().replaceAll(" \\(Completed!.*\\)", "");
        button.setText(originalText + " (Completed! +" + earnedPoints + " pts)");
        button.setForeground(COMPLETED_TEXT_COLOR);
        
        /** Make button non-focusable and non-clickable */
        button.setFocusable(false);
        
        /** Remove all action listeners */
        for (ActionListener al : button.getActionListeners()) {
            button.removeActionListener(al);
        }
        
        /** Remove mouse effects */
        for (MouseListener ml : button.getMouseListeners()) {
            if (ml instanceof java.awt.event.MouseAdapter) {
                button.removeMouseListener(ml);
            }
        }
        
        /** Create a new icon with completed style */
        Icon originalIcon = button.getIcon();
        if (originalIcon instanceof ShapeButtonIcon) {
            ShapeButtonIcon shapeIcon = (ShapeButtonIcon) originalIcon;
            button.setIcon(new ShapeButtonIcon(shapeIcon.shapeType, shapeIcon.getIconWidth(), shapeIcon.getIconHeight()) {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    
                    /** Set colors for completed state */
                    g2d.setColor(COMPLETED_BUTTON_COLOR);
                    g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    
                    /** Draw shape based on type */
                    switch (shapeType) {
                        case RECTANGLE:
                            g2d.fillRect(x + 10, y + 10, width - 20, height - 20);
                            g2d.setColor(COMPLETED_TEXT_COLOR);
                            g2d.drawRect(x + 10, y + 10, width - 20, height - 20);
                            break;
                        case PARALLELOGRAM:
                            int[] xPoints = {x + 20, x + width - 10, x + width - 20, x + 10};
                            int[] yPoints = {y + 10, y + 10, y + height - 10, y + height - 10};
                            g2d.fillPolygon(xPoints, yPoints, 4);
                            g2d.setColor(COMPLETED_TEXT_COLOR);
                            g2d.drawPolygon(xPoints, yPoints, 4);
                            break;
                        case TRIANGLE:
                            int[] txPoints = {x + width/2, x + width - 10, x + 10};
                            int[] tyPoints = {y + 10, y + height - 10, y + height - 10};
                            g2d.fillPolygon(txPoints, tyPoints, 3);
                            g2d.setColor(COMPLETED_TEXT_COLOR);
                            g2d.drawPolygon(txPoints, tyPoints, 3);
                            break;
                        case TRAPEZOID:
                            int[] zxPoints = {x + 20, x + width - 20, x + width - 10, x + 10};
                            int[] zyPoints = {y + height - 10, y + height - 10, y + 10, y + 10};
                            g2d.fillPolygon(zxPoints, zyPoints, 4);
                            g2d.setColor(COMPLETED_TEXT_COLOR);
                            g2d.drawPolygon(zxPoints, zyPoints, 4);
                            break;
                    }
                }
            });
        }
    }
    
    /**
     * Updates the text on the change shape button.
     */
    private void updateChangeShapeButtonText() {
        changeShapeButton.setText("Change to Another Shape");
        changeShapeButton.setPreferredSize(new Dimension(300, 45));
        changeShapeButton.setBackground(BUTTON_COLOR);
        changeShapeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
    }
    
    /**
     * Shows a congratulatory dialog when all exercises are completed.
     * Displays the total score and provides a message about completing all exercises.
     */
    private void showCompletionDialog() {
        /** Create a custom dialog with congratulation message and score */
        JDialog completionDialog = new JDialog(parentDialog, "Congratulations!", true);
        completionDialog.setLayout(new BorderLayout());
        completionDialog.setBackground(BACKGROUND_COLOR);
        
        /** Create panel for the message */
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(BACKGROUND_COLOR);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        /** Congratulations title with larger, bold font */
        JLabel titleLabel = new JLabel("Congratulations!");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        titleLabel.setForeground(new Color(40, 44, 52));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        /** Message explaining the achievement */
        JLabel messageLabel = new JLabel("Area calculations finished!");
        messageLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        messageLabel.setForeground(new Color(70, 70, 70));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        /** Score display with highlighting */
        JLabel scoreLabel = new JLabel("Your total score: " + currentScore + " points");
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        scoreLabel.setForeground(CORRECT_COLOR); /** Use green color for score */
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        /** Add components to the message panel with spacing */
        messagePanel.add(Box.createVerticalStrut(10));
        messagePanel.add(titleLabel);
        messagePanel.add(Box.createVerticalStrut(15));
        messagePanel.add(messageLabel);
        messagePanel.add(Box.createVerticalStrut(20));
        messagePanel.add(scoreLabel);
        messagePanel.add(Box.createVerticalStrut(20));
        
        /** Create a styled OK button */
        JButton okButton = createStyledButton("Continue to Home");
        okButton.setPreferredSize(new Dimension(200, 45));
        okButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainApp != null) {
                    mainApp.updateTask3CompletedShapes(completedShapes);
                    mainApp.updateTask3ShapeScores(shapeScores);
                }
                completionDialog.dispose();
            }
        });
        
        /** Button panel */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(okButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        /** Add panels to the dialog */
        completionDialog.add(messagePanel, BorderLayout.CENTER);
        completionDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        /** Set dialog properties */
        completionDialog.setSize(450, 300);
        completionDialog.setLocationRelativeTo(parentDialog);
        completionDialog.setResizable(false);
        
        /** Show the dialog and wait for user response */
        completionDialog.setVisible(true);
    }
    
    /** 5. Timer related methods */
    
    /**
     * Creates a countdown timer for the question.
     * Timer counts down from TIME_LIMIT seconds.
     */
    private void createCountdownTimer() {
        /** Independent 3-minute timer for each question */
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingSeconds--;
                updateTimerDisplay();
                
                if (remainingSeconds <= 0) {
                    countdownTimer.stop();
                    
                    /** Show feedback in label */
                    feedbackLabel.setText(String.format("Time's up! The correct answer is: %.1f (No points)", calculateAreaWithDecimal()));
                    feedbackLabel.setForeground(ERROR_COLOR);
                    
                    /** Show solution */
                    showSolution = true;
                    drawingPanel.repaint();
                    
                    /** Disable submit button and answer field to prevent further input */
                    submitButton.setEnabled(false);
                    answerField.setEnabled(false);
                    
                    /** Show change shape button */
                    JPanel buttonPanel = (JPanel)((JPanel)((BorderLayout)calculationPanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH)).getComponent(2);
                    buttonPanel.setVisible(true);
                    
                    /** Check if this is the last shape, if so update button text */
                    updateChangeShapeButtonText();
                }
            }
        });
    }
    
    /**
     * Updates the timer display showing remaining time.
     * Changes color to red when less than 30 seconds remain.
     */
    private void updateTimerDisplay() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("Time remaining: %02d:%02d", minutes, seconds));
        
        /** When less than 30 seconds remain, make the text red */
        if (remainingSeconds <= 30) {
            timerLabel.setForeground(ERROR_COLOR);
        } else {
            timerLabel.setForeground(Color.BLACK);
        }
    }
    
    /**
     * Starts the countdown timer for a new question.
     * Resets timer to TIME_LIMIT seconds.
     */
    private void startTimer() {
        /** Reset to 3 minutes */
        remainingSeconds = TIME_LIMIT;
        updateTimerDisplay();
        
        /** Stop previous timer (if any) */
        if (countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
        
        /** Start new timer */
        countdownTimer.start();
    }
    
    /** 6. Game logic methods */
    
    /**
     * Generates a new shape question with random dimensions.
     * Resets UI elements and starts the timer.
     */
    private void generateShapeQuestion() {
        /** Reset attempt count and solution display flag */
        attemptCount = 0;
        showSolution = false;

        /** Generate random dimensions based on shape type */
        if (currentShape == ShapeType.TRAPEZOID) {
            /** Trapezoid needs 3 parameters */
            dimensions[0] = random.nextInt(15) + 5; /** Bottom base: 5-20 */
            dimensions[1] = random.nextInt(dimensions[0] - 2) + 2; /** Top base: 2 to bottom base-1 */
            dimensions[2] = random.nextInt(15) + 5; /** Height: 5-20 */
        } else {
            /** Other shapes need 2 parameters */
            dimensions[0] = random.nextInt(15) + 5; /** Basic dimension 1: 5-20 */
            dimensions[1] = random.nextInt(15) + 5; /** Basic dimension 2: 5-20 */
            dimensions[2] = 0; /** Reset third parameter */
        }

        /** Update shape type title */
        JLabel titleLabel = (JLabel) ((JPanel)((BorderLayout)calculationPanel.getLayout()).getLayoutComponent(BorderLayout.NORTH)).getComponent(1);
        titleLabel.setText(currentShape.getDisplayName() + " Area Calculation");

        /** Clear answer input and feedback */
        answerField.setText("");
        feedbackLabel.setText("");

        /** Enable submit button and answer field */
        submitButton.setEnabled(true);
        answerField.setEnabled(true);

        /** Redraw shape */
        drawingPanel.repaint();

        /** Hide change shape button */
        JPanel buttonPanel = (JPanel)((JPanel)((BorderLayout)calculationPanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH)).getComponent(2);
        buttonPanel.setVisible(false);

        /** Start timer for new question */
        startTimer();
    }

    /**
     * Checks if the user's answer is correct.
     * Handles scoring based on attempt count and updates UI accordingly.
     */
    private void checkAnswer() {
        /** Check if submit button is disabled (prevent checking answer after time ends or correct answer) */
        if (!submitButton.isEnabled() || !answerField.isEnabled()) {
            return; /** If submit is disabled, exit method */
        }
        
        if (answerField.getText().trim().isEmpty()) {
            feedbackLabel.setText("Please enter an answer");
            feedbackLabel.setForeground(Color.BLACK);
            return;
        }
    
        try {
            double userAnswer = Double.parseDouble(answerField.getText().trim());
            double correctAnswer = calculateAreaWithDecimal();
    
            attemptCount++;
    
            /** Allow small floating point precision differences (error within 0.1 is considered correct) */
            if (Math.abs(userAnswer - correctAnswer) < 0.1) {
                /** Answer is correct, stop timer */
                countdownTimer.stop();
                
                /** Determine score based on number of attempts */
                int earnedPoints = 0;
                if (attemptCount == 1) {
                    /** Correct on first try: +3 points */
                    earnedPoints = 3;
                    feedbackLabel.setText("Congratulations! Correct on first try! +3 points!");
                } else if (attemptCount == 2) {
                    /** Wrong once, correct on second try: +2 points */
                    earnedPoints = 2;
                    feedbackLabel.setText("Correct! +2 points!");
                } else if (attemptCount == 3) {
                    /** Wrong twice, correct on third try: +1 point */
                    earnedPoints = 1;
                    feedbackLabel.setText("Finally correct! +1 point!");
                }
                
                /** Mark the current shape as completed */
                for (int i = 0; i < ShapeType.values().length; i++) {
                    if (ShapeType.values()[i] == currentShape) {
                        shapeScores[i] = earnedPoints;
                        completedShapes[i] = true;
                        markButtonAsCompleted(shapeButtons[i], shapeScores[i]);
                        completedCount++;

                        if (mainApp != null && !progressUpdated[i]) {
                            mainApp.updateKeyStage2Progress(1, 4); /** Update 25%/4 progress for each completed shape */
                            progressUpdated[i] = true; /** Mark this shape's progress as updated */
                        }
                        
                        /** Update MainApp (if exists) */
                        if (mainApp != null) {
                            mainApp.updateTask3CompletedShapes(completedShapes);
                            mainApp.updateTask3ShapeScores(shapeScores);
                        }
                        break;
                    }
                }
                
                currentScore += earnedPoints;
                updateScoreDisplay();
                
                /** If mainApp reference exists, update main application score */
                if (mainApp != null) {
                    mainApp.addScore(earnedPoints);
                }
                
                feedbackLabel.setForeground(CORRECT_COLOR);
                
                /** Disable submit button and answer field */
                submitButton.setEnabled(false);
                answerField.setEnabled(false);
                
                /** Show solution */
                showSolution = true;
                drawingPanel.repaint();
                
                /** Show change shape button */
                JPanel buttonPanel = (JPanel)((JPanel)((BorderLayout)calculationPanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH)).getComponent(2);
                buttonPanel.setVisible(true);
                
                /** Update button text */
                updateChangeShapeButtonText();
            } else {
                /** Answer is incorrect */
                if (attemptCount >= MAX_ATTEMPTS) {
                    /** All three attempts used, stop timer */
                    countdownTimer.stop();
                    
                    /** All attempts exhausted, no points awarded */
                    feedbackLabel.setText(String.format("Sorry, the correct answer is: %.1f (No points)", correctAnswer));
                    feedbackLabel.setForeground(ERROR_COLOR);

                    /** Mark the current shape as completed */
                    for (int i = 0; i < ShapeType.values().length; i++) {
                        if (ShapeType.values()[i] == currentShape) {
                            completedShapes[i] = true;
                            markButtonAsCompleted(shapeButtons[i], shapeScores[i]);
                            completedCount++;
                            
                            if (mainApp != null && !progressUpdated[i]) {
                                mainApp.updateKeyStage2Progress(1, 4); /** Update 25%/4 progress for each completed shape */
                                progressUpdated[i] = true; /** Mark this shape's progress as updated */
                            }
                            
                            /** Update MainApp (if exists) */
                            if (mainApp != null) {
                                mainApp.updateTask3CompletedShapes(completedShapes);
                                mainApp.updateTask3ShapeScores(shapeScores);
                            }
                            break;
                        }
                    }
                    
                    /** Disable submit button and answer field */
                    submitButton.setEnabled(false);
                    answerField.setEnabled(false);
                    
                    /** Show solution */
                    showSolution = true;
                    drawingPanel.repaint();
                    
                    /** Show change shape button */
                    JPanel buttonPanel = (JPanel)((JPanel)((BorderLayout)calculationPanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH)).getComponent(2);
                    buttonPanel.setVisible(true);
                    
                    /** Update button text */
                    updateChangeShapeButtonText();
                } else {
                    /** Attempts remaining */
                    feedbackLabel.setText("Incorrect answer, please try again. (Attempt " + attemptCount + "/" + MAX_ATTEMPTS + ")");
                    feedbackLabel.setForeground(ERROR_COLOR);
                    
                    /** Reset timer and clear answer field for new attempt */
                    startTimer(); /** Reset timer after incorrect answer */
                    answerField.setText(""); /** Clear answer field */
                }
            }
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid number");
            feedbackLabel.setForeground(Color.BLACK);
        }
    }
    
    /**
     * Calculates the area with decimal precision.
     * 
     * @return The calculated area as a double value
     */
    private double calculateAreaWithDecimal() {
        if (currentShape == null) return 0;
        
        AbstractShape shape = null;
        switch (currentShape) {
            case RECTANGLE:
                shape = new RectangleShape(dimensions, false);
                break;
            case PARALLELOGRAM:
                shape = new ParallelogramShape(dimensions, false);
                break;
            case TRIANGLE:
                shape = new TriangleShape(dimensions, false);
                break;
            case TRAPEZOID:
                shape = new TrapezoidShape(dimensions, false);
                break;
        }
        
        return shape != null ? shape.calculateArea() : 0;
    }
    
    /** 7. Helper methods */
    
    /**
     * Creates a styled button with rounded corners and hover effects.
     * 
     * @param text The text to display on the button
     * @return A styled JButton
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        /** Apply rounded corners */
        button.putClientProperty("JButton.buttonType", "roundRect");
        
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
    
    /** 8. Inner classes */
    
    /**
     * Inner class for drawing shapes with graphics.
     * Handles shape rendering and formula display.
     */
    private class ShapeDrawingPanel extends JPanel {
        private AbstractShape currentShapeObject;
        
        /**
         * Constructs a new ShapeDrawingPanel.
         */
        public ShapeDrawingPanel() {
            setBackground(Color.WHITE); 
            setPreferredSize(new Dimension(700, 400));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }
        
        /**
         * Factory method to create shape object based on current shape type.
         * 
         * @return AbstractShape instance or null if no shape selected
         */
        private AbstractShape createShapeObject() {
            if (currentShape == null) return null;
            
            switch (currentShape) {
                case RECTANGLE:
                    return new RectangleShape(dimensions, showSolution);
                case PARALLELOGRAM:
                    return new ParallelogramShape(dimensions, showSolution);
                case TRIANGLE:
                    return new TriangleShape(dimensions, showSolution);
                case TRAPEZOID:
                    return new TrapezoidShape(dimensions, showSolution);
                default:
                    return null;
            }
        }
        
        /**
         * Paints the shape and formula on the panel.
         * 
         * @param g Graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            if (currentShape == null) return;
            
            /** Create appropriate shape object */
            currentShapeObject = createShapeObject();
            if (currentShapeObject == null) return;
            
            int width = getWidth();
            int height = getHeight();
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int leftSectionWidth = width / 2;
            /** Increase the zoom factor to make the graph larger */
            double shapeScaleFactor = Math.min(
                (double)(leftSectionWidth - 80) / 300,
                (double)(height - 80) / 300
            ) * 1.5; /** Increase scaling by 50% */
            
            /** Left drawing section */
            Graphics2D g2dLeft = (Graphics2D) g2d.create();
            g2dLeft.translate(leftSectionWidth / 2, height / 2);
            g2dLeft.scale(shapeScaleFactor, shapeScaleFactor);
            
            /** Drawing known condition labels */
            Font conditionFont = new Font("Comic Sans MS", Font.PLAIN, 15);
            g2dLeft.setFont(conditionFont);
            
            /** Set the color for conditions */
            g2dLeft.setColor(CONDITION_TEXT_COLOR);
            
            /** Display dimensions depending on shape type */
            switch (currentShape) {
                case RECTANGLE:
                    g2dLeft.drawString("Length = " + dimensions[0], -170, -80);
                    g2dLeft.drawString("Width = " + dimensions[1], -170, -50);
                    break;
                case PARALLELOGRAM:
                    g2dLeft.drawString("Base = " + dimensions[0], -170, -80);
                    g2dLeft.drawString("Height = " + dimensions[1], -170, -50);
                    break;
                case TRIANGLE:
                    g2dLeft.drawString("Base = " + dimensions[0], -170, -80);
                    g2dLeft.drawString("Height = " + dimensions[1], -170, -50);
                    break;
                case TRAPEZOID:
                    g2dLeft.drawString("Bottom base (b) = " + dimensions[0], -170, -80);
                    g2dLeft.drawString("Top base (a) = " + dimensions[1], -170, -50);
                    g2dLeft.drawString("Height = " + dimensions[2], -170, -20);
                    break;
            }
            
            /** Use abstract class method to draw the shape */
            currentShapeObject.drawShape(g2dLeft, leftSectionWidth, height);
            g2dLeft.dispose();
            
            /** Right drawing section - show formula if solution is visible */
            if (showSolution) {
                Graphics2D g2dRight = (Graphics2D) g2d.create();
                float formulaScaleFactor = 1.5f; 
                g2dRight.translate(leftSectionWidth + 40, height / 4); 
                g2dRight.scale(formulaScaleFactor, formulaScaleFactor);
                
                /** Draw the formula */
                currentShapeObject.drawFormula(g2dRight);
                g2dRight.dispose();
            }
        }
    }
    
    /**
     * Inner class for creating button icons with shape previews.
     */
    private class ShapeButtonIcon implements Icon {
        protected final ShapeType shapeType;
        protected final int width;
        protected final int height;

        /**
         * Constructs a ShapeButtonIcon.
         * 
         * @param shapeType Type of shape to draw
         * @param width Icon width
         * @param height Icon height
         */
        public ShapeButtonIcon(ShapeType shapeType, int width, int height) {
            this.shapeType = shapeType;
            this.width = width;
            this.height = height;
        }

        /**
         * Paints the icon.
         * 
         * @param c Component to paint on
         * @param g Graphics context
         * @param x X coordinate
         * @param y Y coordinate
         */
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            
            /** Set colors */
            g2d.setColor(SHAPE_FILL_COLOR);
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            /** Draw shape based on type */
            switch (shapeType) {
                case RECTANGLE:
                    g2d.fillRect(x + 10, y + 10, width - 20, height - 20);
                    g2d.setColor(SHAPE_LINE_COLOR);
                    g2d.drawRect(x + 10, y + 10, width - 20, height - 20);
                    break;
                case PARALLELOGRAM:
                    int[] xPoints = {x + 20, x + width - 10, x + width - 20, x + 10};
                    int[] yPoints = {y + 10, y + 10, y + height - 10, y + height - 10};
                    g2d.fillPolygon(xPoints, yPoints, 4);
                    g2d.setColor(SHAPE_LINE_COLOR);
                    g2d.drawPolygon(xPoints, yPoints, 4);
                    break;
                case TRIANGLE:
                    int[] txPoints = {x + width/2, x + width - 10, x + 10};
                    int[] tyPoints = {y + 10, y + height - 10, y + height - 10};
                    g2d.fillPolygon(txPoints, tyPoints, 3);
                    g2d.setColor(SHAPE_LINE_COLOR);
                    g2d.drawPolygon(txPoints, tyPoints, 3);
                    break;
                case TRAPEZOID:
                    int[] zxPoints = {x + 20, x + width - 20, x + width - 10, x + 10};
                    int[] zyPoints = {y + height - 10, y + height - 10, y + 10, y + 10};
                    g2d.fillPolygon(zxPoints, zyPoints, 4);
                    g2d.setColor(SHAPE_LINE_COLOR);
                    g2d.drawPolygon(zxPoints, zyPoints, 4);
                    break;
            }
        }

        /**
         * Gets the icon width.
         * 
         * @return Icon width
         */
        @Override
        public int getIconWidth() {
            return width;
        }

        /**
         * Gets the icon height.
         * 
         * @return Icon height
         */
        @Override
        public int getIconHeight() {
            return height;
        }
    }
    
    /** 9. Main method */
    
    /**
     * Main method for standalone testing.
     * Creates a frame and displays the ShapeAreaCalculation panel.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    /** Set system look and feel for better integration */
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    
                    /** Enable anti-aliasing for all Swing components */
                    System.setProperty("awt.useSystemAAFontSettings", "on");
                    System.setProperty("swing.aatext", "true");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                JFrame frame = new JFrame("Shape Area Calculation Practice");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                /** Create a dialog as parent window */
                JDialog dialog = new JDialog();
                
                ShapeAreaCalculation panel = new ShapeAreaCalculation(dialog, null);
                frame.getContentPane().add(panel);
                
                /** Adjust window size to better fit content */
                frame.setSize(1100, 650);
                
                /** Center window on screen */
                frame.setLocationRelativeTo(null);
                
                /** Set minimum window size to ensure content isn't compressed */
                frame.setMinimumSize(new Dimension(900, 650));
                frame.setVisible(true);
            }
        });
    }
}