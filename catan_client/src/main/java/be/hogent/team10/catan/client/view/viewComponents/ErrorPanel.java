package be.hogent.team10.catan.client.view.viewComponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Brenden Dit paneel voorziet de user van een foutboodschap wanneer
 * nodig. Er is een autoclear.
 */
public class ErrorPanel extends JPanel {

    private static ErrorPanel instance;
    private JLabel errorText;

    private ErrorPanel() {

        setPreferredSize(new Dimension(400, 100));
        setOpaque(true);
        setBackground(Color.decode("#778899"));
        setLayout(new BorderLayout());
        errorText = new JLabel();
        setError("Error: there is a disturbance in the force!");
        errorText.setForeground(Color.red);
        errorText.setHorizontalAlignment(JLabel.CENTER);
        errorText.setFont(new Font("Serif", Font.PLAIN, 15));
        add(errorText, BorderLayout.NORTH);
    }

    public static ErrorPanel getInstance() {
        if (instance == null) {
            instance = new ErrorPanel();
        }
        return instance;
    }

    /**
     *
     * sets the text in errorpanel. The panel cleans itself after 5 seconds
     */
    public void setError(String e) {
        System.out.println(e);
        errorText.setText(e);
        revalidate();

        Timer timer = new Timer(5000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                clearError();
            }
        });
        timer.setRepeats(false); // Only execute once
        timer.start(); // Go go go!
    }

    public void clearError() {
        errorText.setText("");
        revalidate();
    }
}