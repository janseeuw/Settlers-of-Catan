/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.util;

import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joachim
 */
public class RetrieveImage {

    private static final org.slf4j.Logger slf4jLogger = LoggerFactory.getLogger(RetrieveImage.class);
    private static RetrieveImage instance = null;

    public static RetrieveImage getInstance() {
        if (instance == null) {
            instance = new RetrieveImage();
        }
        return instance;
    }

    public Image getImage(String url) {
        Image image = null;
        try {
            if (url != null) {
                Sardine sardine = SardineFactory.begin("team10", "Dc56zYzV");
                InputStream is = sardine.get(url);
                image = ImageIO.read(is);
            } else {
                image = null;
            }
            slf4jLogger.debug("Leaving getImage(): {}", url);
        } catch (IOException ex) {
            Logger.getLogger(RetrieveImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }
}
