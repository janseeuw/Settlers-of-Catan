package be.hogent.team10.catan.client.view.viewComponents;

import be.hogent.team10.catan.client.util.RetrieveImage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import be.hogent.team10.catan.client.view.GuiController;
import be.hogent.team10.catan_businesslogic.model.Game;
import be.hogent.team10.catan_businesslogic.model.Player;
import be.hogent.team10.catan_businesslogic.model.ResourceSet;
import be.hogent.team10.catan_businesslogic.util.Observable;
import be.hogent.team10.catan_businesslogic.util.Observer;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Brenden, HP displays information about the other players. (and
 * yourself)
 *
 * shows current player.
 */
public class PlayerInfoPanel extends JPanel implements Observer {

    private GuiController guiC;
    private int player;
    private JPanel title;
    private JLabel avatar;
    private String avatarUrl = "/anon.jpg";
    private JLabel titleLabel;
    private JPanel data;
    private JPanel subdata, stats;
    private JLabel resourceCount, pointCount, devCount, knightCount;
    private JLabel resource, point, dev, knight, route, longestYes, longestNo;
    private Color c = null;

    public PlayerInfoPanel() {
        setLayout(new BorderLayout());
        title = new JPanel();
        title.setBackground(Color.decode("#2F4F4F"));

        setPreferredSize(new Dimension(GuiController.getInstance().getSize("PIPSizew"), GuiController.getInstance().getSize("PIPSizeh")));

        //setAvatar("/anon.jpg");
        avatar = new JLabel();
        data = new JPanel();
        data.setBackground(Color.decode("#2F4F4F"));
        data.setLayout(new BorderLayout());
        titleLabel = new JLabel("Synchroniseren ...");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Serif", Font.BOLD, GuiController.getInstance().getSize("Font")));

        title.add(titleLabel);
        setBackground(Color.decode("#2F4F4F"));

        initStats();

        add(title, BorderLayout.NORTH);
        add(data, BorderLayout.CENTER);
        add(stats, BorderLayout.WEST);
    }

    private void initStats() {
        loadImages();
        stats = new JPanel();
        stats.setBackground(Color.decode("#2F4F4F"));
        stats.setLayout(new GridLayout(5, 2));
        stats.add(resource);
        stats.add(resourceCount);
        stats.add(dev);
        stats.add(devCount);
        stats.add(knight);
        stats.add(knightCount);
        stats.add(route);
        stats.add(longestNo);
        stats.add(point);
        stats.add(pointCount);
    }

    private void loadImages() {
        try {
            Image img1 = ImageIO.read(getClass().getResource("/resources.gif"));
            resource = new JLabel(new ImageIcon(img1));
            Image img2 = ImageIO.read(getClass().getResource("/points.gif"));
            point = new JLabel(new ImageIcon(img2));
            Image img3 = ImageIO.read(getClass().getResource("/knights.gif"));
            knight = new JLabel(new ImageIcon(img3));
            Image img4 = ImageIO.read(getClass().getResource("/devcards.gif"));
            dev = new JLabel(new ImageIcon(img4));
            Image img5 = ImageIO.read(getClass().getResource("/roads.gif"));
            route = new JLabel(new ImageIcon(img5));
            Image img6 = ImageIO.read(getClass().getResource("/longestYes.gif"));
            longestYes = new JLabel(new ImageIcon(img6));
            Image img7 = ImageIO.read(getClass().getResource("/longestNo.gif"));
            longestNo = new JLabel(new ImageIcon(img7));
        } catch (IOException ex) {
            Logger.getLogger(PlayerInfoPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        resourceCount = new JLabel("0");
        resourceCount.setForeground(Color.WHITE);
        devCount = new JLabel("0");
        devCount.setForeground(Color.WHITE);
        knightCount = new JLabel("0");
        knightCount.setForeground(Color.WHITE);
        pointCount = new JLabel("0");
        pointCount.setForeground(Color.WHITE);
        resource.setToolTipText("Grondstoffen");
        point.setToolTipText("Overwinningspunten");
        dev.setToolTipText("Ontwikkelingskaarten");
        knight.setToolTipText("Riddermacht");
        route.setToolTipText("Langste handelsroute");


    }

    public void setAvatar(String url) {
        this.avatarUrl = url;
        try {
            Image img = RetrieveImage.getInstance().getImage(url);


            if (img != null) {
                img = img.getScaledInstance(GuiController.getInstance().getSize("img"), GuiController.getInstance().getSize("img"), 1);
                avatar.setIcon(new ImageIcon(img));
                avatar.setHorizontalAlignment(JLabel.CENTER);
                data.add(avatar);
            } else {
                img = ImageIO.read(getClass().getResource("/anon.jpg"));
                img = img.getScaledInstance(GuiController.getInstance().getSize("img"), GuiController.getInstance().getSize("img"), 1);
                avatar.setIcon(new ImageIcon(img));
                avatar.setHorizontalAlignment(JLabel.CENTER);
                data.add(avatar);
            }
            repaint();
        } catch (IOException ex) {
            Logger.getLogger(SidePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void update(Observable o, Object arg) {
        //System.out.println("arriving at update");
        Player p = null;
        ResourceSet s = null;
        if (o instanceof Player) {
            p = (Player) o;
            this.player = p.getId();
            //System.out.println(p.getName());
            this.titleLabel.setText(p.getName());
            setAvatar(p.getAvatar());
            resourceCount.setText("" + p.getResources().getTotal());
            knightCount.setText("" + p.getKnightforce());
            pointCount.setText("" + p.getPoints());
            devCount.setText("" + p.getCards(2));
            s = p.getResources();
            if (p.getPlayerColor() != null) {
                c = p.getPlayerColor().getColor();
            }
        }

        if (o instanceof Game ) {
            if (((Game) o).getCurrentPlayer() == this.player) {
                setBorder(BorderFactory.createLineBorder(c, 10));
            } else {
                setBorder(BorderFactory.createLineBorder(c, 3));
            }
        }
        revalidate();
        //repaint();
    }
}
