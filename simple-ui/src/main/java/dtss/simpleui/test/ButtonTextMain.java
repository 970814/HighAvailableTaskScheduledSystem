package dtss.simpleui.test;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ButtonTextMain {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            final JTextField field = new JTextField(20);
            
            final JButton button = new JButton("Click to change text");
            button.addActionListener(e -> button.setText(field.getText()));
            
            final JPanel panel = new JPanel(new BorderLayout());
            panel.add(field, BorderLayout.CENTER);
            panel.add(button, BorderLayout.PAGE_END);
            
            final JFrame frame = new JFrame("Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}