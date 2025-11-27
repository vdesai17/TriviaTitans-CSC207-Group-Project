package trivia.framework.ui;

import javax.swing.*;
import java.awt.*;

/**
 * ThemeUtils — central styling and theme utilities for Trivia Titans.
 * Defines all shared colors, fonts, gradients, glass panels, and button styles.
 */
public class ThemeUtils {

    // === Core Colors ===
    public static final Color DEEP_TEAL = new Color(0, 60, 70);
    public static final Color DEEP_TEAL_HOVER = new Color(0, 80, 90);

    public static final Color MINT = new Color(0, 200, 180);
    public static final Color MINT_HOVER = new Color(0, 230, 210);

    public static final Color AQUA = new Color(0, 165, 165);
    public static final Color AQUA_HOVER = new Color(0, 190, 185);

    public static final Color TRANSLUCENT_WHITE = new Color(255, 255, 255, 170);
    public static final Color TRANSLUCENT_DARK = new Color(0, 0, 0, 100);

    // === Fonts ===
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 34);
    public static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 20);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 16);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 17);

    /** Applies a dark-teal gradient background to a panel */
    public static void applyGradientBackground(JPanel panel) {
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        JPanel gradient = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 90, 100),
                        0, getHeight(), new Color(0, 45, 55)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradient.setLayout(new BorderLayout());
        panel.add(gradient, BorderLayout.CENTER);
    }

    /** Creates a semi-transparent rounded “glass” panel */
    public static JPanel createGlassPanel(int opacity) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity / 100f));
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    /** Styles a JLabel according to its semantic type */
    public static void styleLabel(JLabel label, String type) {
        label.setOpaque(false);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        switch (type.toLowerCase()) {
            case "title":
                label.setFont(TITLE_FONT);
                label.setForeground(Color.BLACK);
                break;

            case "subtitle":
                label.setFont(SUBTITLE_FONT);
                label.setForeground(new Color(220, 240, 240));
                break;

            case "body":
                label.setFont(BODY_FONT);
                label.setForeground(Color.BLACK);
                break;

            default:
                label.setFont(BODY_FONT);
                label.setForeground(Color.BLACK);
                break;
        }
    }

    /** Creates a consistently styled button with hover transitions */
    public static JButton createStyledButton(String text, Color base, Color hover) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(base);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(base);
            }
        });

        return button;
    }
}
