/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.hogent.team10.catan.client.util;

import javax.sound.sampled.*;

/**
 *
 * @author Brenden Deze klasse zal threads genereren om audio te voorzien. Er is
 * ook een mute mogelijkheid. De initaudio methode is afgeschermd om foutieve
 * locatie invoer tegen te gaan.
 */
public class AudioStream {

    private static Thread t;
    private static AudioStream instance = null;
    private boolean enabled = true;

    private AudioStream() {
    }

    public static AudioStream getInstance() {
        if (instance == null) {
            instance = new AudioStream();
        }
        return instance;
    }

    public void playKnight() {
        initAudio("/knight.wav");
    }

    public void playTrade() {
        initAudio("/trade.wav");
    }

    public void playDice() {
        initAudio("/dice.wav");
    }

    public void playRobber() {
        initAudio("/robber.wav");
    }

    public void playRoad() {
        initAudio("/road.wav");
    }

    public void playCity() {
        initAudio("/city.wav");
    }

    public void playVillage() {
        initAudio("/village.wav");
    }

    public void playFanfare() {
        initAudio("/fanfare.wav");
    }

    public void playUni() {
        initAudio("/uni.wav");
    }

    public void setEnabled(boolean enable) {
        enabled = enable;
    }

    private void initAudio(String url) {
        if (enabled) {
            Runnable background_music = new CustomAudio(url); // runnable voor muziek thread


            t = new Thread(background_music);
            t.setDaemon(true);
            t.start();
        }
    }

    private class CustomAudio implements Runnable {

        private String url;

        public CustomAudio(String u) {
            url = u;
        }

        @Override
        public void run() {


            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(getClass().getResource(url));

                AudioFormat format = stream.getFormat();
                if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                    format = new AudioFormat(
                            AudioFormat.Encoding.PCM_SIGNED,
                            format.getSampleRate(),
                            format.getSampleSizeInBits() * 2,
                            format.getChannels(),
                            format.getFrameSize() * 2,
                            format.getFrameRate(),
                            true);        // big endian
                    stream = AudioSystem.getAudioInputStream(format, stream);
                }

                // Create line
                SourceDataLine.Info info = new DataLine.Info(
                        SourceDataLine.class, stream.getFormat(),
                        ((int) stream.getFrameLength() * format.getFrameSize()));
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(stream.getFormat());
                line.start();

                // Continuously read and play chunks of audio
                int numRead = 0;
                byte[] buf = new byte[line.getBufferSize()];
                while ((numRead = stream.read(buf, 0, buf.length)) >= 0) {
                    int offset = 0;
                    while (offset < numRead) {
                        offset += line.write(buf, offset, numRead - offset);
                    }
                }
                line.drain();
                line.stop();
                Thread.sleep(100);
                stream.close();
            } catch (Exception e) {
            }
        }
    }
}
