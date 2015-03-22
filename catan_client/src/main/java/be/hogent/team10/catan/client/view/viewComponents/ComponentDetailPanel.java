/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.view.viewComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author HP
 */
public class ComponentDetailPanel extends JPanel {

    private List<JLabel> labels;
    private List<JTextField> textfields;
    private JTextArea descriptionArea;
    private JTextArea robberArea;
    private JButton closeButton;
    private int labelheight = 14;
    private int labellength = 90;
    private int textfieldlength = 130;
    private int margintop = 5;
    private int marginleft = 5;
    private int pxTopToNLTop = 30;

    public ComponentDetailPanel(boolean docked) {


        this.setLayout(null);
        this.setSize(200, 300);
        this.setVisible(true);
        this.setBackground(Color.LIGHT_GRAY);
        textfields = new ArrayList<JTextField>();
        labels = new ArrayList<JLabel>();
        initCloseButton();

    }

    private void initCloseButton() {
        closeButton = new JButton();
        try {
            Image img = ImageIO.read(getClass().getResource("/close.png"));
            img = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            closeButton.setIcon(new ImageIcon(img));
        } catch (IOException ex) {
            closeButton.setText("X");
        }
        this.setBackground(Color.GREEN);
        closeButton.setSize(20, 20);
        closeButton.setLocation(220, 0);
        closeButton.setVisible(true);
        this.add(closeButton);
        closeButton.addMouseListener(
                new MouseListener() {

                    public void mouseClicked(MouseEvent me) {
                        ComponentDetailPanel panel = ((ComponentDetailPanel) ((JComponent) me.getSource()).getParent());
                        panel.clear();
                        panel.setVisible(false);
                        panel.dispose();
                    }

                    public void mousePressed(MouseEvent me) {
                    }

                    public void mouseReleased(MouseEvent me) {
                    }

                    public void mouseEntered(MouseEvent me) {
                    }

                    public void mouseExited(MouseEvent me) {
                    }
                });
    }

    public void showData(Map<String, String> data) {
        int row = 0;
        addTextField("Element:", data.get("COMPONENTTYPE"), row);
        row++;
        this.descriptionArea = new JTextArea(5, 20);
        descriptionArea.setText(data.get("DESCRIPTION"));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        addTextArea("Omschrijving", descriptionArea, row);
        if (data.containsKey("ROBBER")) {
            robberArea = new JTextArea(5, 20);
            robberArea.setText(data.get("ROBBER"));
            addTextArea("Rover", robberArea, row + 5);
            robberArea.setEditable(false);
            robberArea.setEnabled(false);
        }

        descriptionArea.setEditable(false);
        descriptionArea.setEnabled(false);
        revalidate();
    }

    private void addTextField(String label, String field, int row) {

        JLabel jlabel = new JLabel(field);
        labels.add(jlabel);
        jlabel.setFont(new Font("Serif", Font.BOLD, 18));

        jlabel.setSize(labellength * 2, labelheight * 2);
        jlabel.setLocation(marginleft, margintop + pxTopToNLTop * row);
        this.add(jlabel);
    }

    private void addTextArea(String label, JTextArea textArea, int row) {
        this.add(textArea);
        JLabel jlabel = new JLabel(label);
        labels.add(jlabel);
        //position fields.
        jlabel.setLocation(marginleft, margintop + pxTopToNLTop * row);
        this.add(jlabel, 2);
        descriptionArea.setSize(textfieldlength + textfieldlength / 2, textfieldlength);
        descriptionArea.setLocation(marginleft, margintop + pxTopToNLTop * (row + 1));
        descriptionArea.setMargin(new Insets(5, 5, 5, 5));
        jlabel.setSize(labellength, labelheight);
        this.add(descriptionArea);
    }

    public void clear() {
        for (JLabel label : labels) {
            label.setText("");
        }
        for (JTextField field : textfields) {
            field.setText("");
        }
        if (descriptionArea != null) {
            descriptionArea.setText("");
        }
    }

    private void dispose() {
        this.getParent().remove(this);
    }
}
