package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    /**
     * Builds a new CGUI.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();
        new Thread(agent).start();
        /*
         * Register a listener to increase counter.
         */
        up.addActionListener((e) -> agent.increaseCounting());
        /*
         * Register a listener to decrements counter.
         */
        down.addActionListener((e) -> agent.decrementCounting());
        /*
         * Register a listener that stops it.
         */
        stop.addActionListener((e) -> {
            agent.stopCounting();
            disableButtons();
        });
    }

    /*
     * The counter agent is implemented as a nested class. This makes it
     * invisible outside and encapsulated.
     */
    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean increase = true;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                    if (increase) {
                        counter++;
                    } else {
                        counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        /**
         * External command to increase counter.
         */
        public void increaseCounting() {
            this.increase = true;
        }

        /**
         * External command to decrease counter.
         */
        public void decrementCounting() {
            this.increase = false;
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }
    }

    private void disableButtons() {
        up.setEnabled(false);
        down.setEnabled(false);
        stop.setEnabled(false);
    }
}
