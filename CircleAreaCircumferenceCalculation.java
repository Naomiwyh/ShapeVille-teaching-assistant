import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class CircleAreaCircumferenceCalculation extends JPanel {
    private JLabel questionLabel;
    private JButton[] optionButtons;
    private JLabel feedbackLabel;
    private JLabel scoreLabel;
    private int currentScore = 0;
    private String[] shapes = {"圆形", "正方形", "三角形", "长方形", "五角星", "六边形"};
    private Random random = new Random();
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 245);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;

    public CircleAreaCircumferenceCalculation(JDialog parent) {
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 创建标题
        JLabel titleLabel = new JLabel("图形识别练习", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));

        // 创建问题面板
        JPanel questionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        questionPanel.setBackground(BACKGROUND_COLOR);
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        questionPanel.add(questionLabel);

        // 创建选项按钮面板
        JPanel optionsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        optionsPanel.setBackground(BACKGROUND_COLOR);
        optionButtons = new JButton[shapes.length];
        for (int i = 0; i < shapes.length; i++) {
            optionButtons[i] = createStyledButton(shapes[i]);
            final int index = i;
            optionButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkAnswer(shapes[index]);
                }
            });
            optionsPanel.add(optionButtons[i]);
        }

        // 创建反馈面板
        JPanel feedbackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        feedbackPanel.setBackground(BACKGROUND_COLOR);
        feedbackLabel = new JLabel();
        feedbackLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        feedbackPanel.add(feedbackLabel);

        // 创建分数面板
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        scorePanel.setBackground(BACKGROUND_COLOR);
        scoreLabel = new JLabel("得分: 0");
        scoreLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        scorePanel.add(scoreLabel);

        // 添加所有面板
        add(titleLabel, BorderLayout.NORTH);
        add(questionPanel, BorderLayout.CENTER);
        add(optionsPanel, BorderLayout.SOUTH);
        add(feedbackPanel, BorderLayout.EAST);
        add(scorePanel, BorderLayout.WEST);

        // 生成第一个问题
        generateNewQuestion();
    }

    private void generateNewQuestion() {
        int correctIndex = random.nextInt(shapes.length);
        questionLabel.setText("请选择这个图形: " + shapes[correctIndex]);
        feedbackLabel.setText("");
    }

    private void checkAnswer(String selectedShape) {
        String correctShape = questionLabel.getText().substring(questionLabel.getText().indexOf(": ") + 2);
        
        if (selectedShape.equals(correctShape)) {
            currentScore += 10;
            feedbackLabel.setText("正确！");
            feedbackLabel.setForeground(new Color(46, 139, 87));
        } else {
            feedbackLabel.setText("错误！请再试一次");
            feedbackLabel.setForeground(new Color(220, 20, 60));
        }

        scoreLabel.setText("得分: " + currentScore);
        generateNewQuestion();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        
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