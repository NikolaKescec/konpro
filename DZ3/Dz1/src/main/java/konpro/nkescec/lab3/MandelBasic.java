package konpro.nkescec.lab3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MandelBasic extends JFrame implements MouseListener {

    private static ExecutorService pool;

    private static List<Integer> iterations;

    private static final long serialVersionUID = 48567845485868634L;
    private static int MAX = 64;
    private double viewX = 0.0;
    private double viewY = 0.0;
    private double zoom = 1.0;
    private int mouseX;
    private int mouseY;

    private Color[][] colors;

    public MandelBasic(String string) {
        super(string);
    }

    public MandelBasic() {
        super();
    }

    public static void main(String[] args) {
        iterations = Arrays.asList(2);

        if (args.length > 0) {
            iterations = new ArrayList<>();
            final String[] strings = args[0].split(";");
            for(String string: strings) {
                int parsed = Integer.parseInt(string);
                if(parsed == 0) continue;
                if(parsed < 0) {
                    parsed = parsed * -1;
                    for(int i = 0; i < parsed; i++) {
                        iterations.add(i + 1);
                    }
                    continue;
                }
                iterations.add(parsed);
            }
        }
        int max = iterations.stream().max(Integer::compareTo).get();
        MandelBasic.pool = Executors.newFixedThreadPool(max);

        MandelBasic frame = new MandelBasic("Mandelbrot fractal demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setVisible(true);
        frame.addMouseListener(frame);
    }

    @Override
    public void paint(Graphics g) {
        Dimension size = getSize();
        long start = System.currentTimeMillis();
        double r = zoom / Math.min(size.width, size.height);
        final List<Callable<Object>> drawers = new ArrayList<>();
        for (int numberOfThreads : iterations) {
            colors = new Color[size.height][size.width];
            double chunk = (size.width / (numberOfThreads * 1.0));
            for (int thread = 0; thread < numberOfThreads; thread++) {
                int startingWidth = (int) chunk * thread;
                int endingWidth = (int) (startingWidth + chunk);
                int startingHeight = 0;
                int endingHeight = size.height;
                final Job job = new Job(startingHeight, endingHeight, startingWidth, endingWidth, r);
                drawers.add(Executors.callable(job));
            }

            try {
                MandelBasic.pool.invokeAll(drawers);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (int y = 0; y < size.height; y++) {
                for (int x = 0; x < size.width; x++) {
                    g.setColor(colors[y][x]);
                    g.drawLine(x, y, x, y);
                }
            }

            long end = System.currentTimeMillis();
            System.out.printf("With %s threads: %d%n", numberOfThreads, (end - start));
        }
    }

//    private void calculate(Graphics g, int startingHeight, int endingHeight, int startingWidth, int endingWidth,
//        double r) {
//        for (int y = startingHeight; y < endingHeight; y++) {
//            for (int x = startingWidth; x < endingWidth; x++) {
//                double dx = 2.5 * (x * r + viewX) - 2.0;
//                double dy = 1.25 - 2.5 * (y * r + viewY);
//                int value = mandel(dx, dy);
//                value = value * 255 / MAX;
//                g.setColor(new Color(value, value, value));
//                g.drawLine(x, y, x, y);
//            }
//        }
//    }

    public void clear() {
        Graphics g = getGraphics();
        Dimension d = getSize();
        Color c = Color.RED;
        g.setColor(c);
        g.fillRect(0, 0, d.width, d.height);
        repaint();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    private int mandel(double px, double py) {
        int value = 0;
        double zx = 0.0;
        double zy = 0.0;
        double zx2 = 0.0;
        double zy2 = 0.0;
        while (value < MAX && zx2 + zy2 < 4.0) {
            zy = 2.0 * zx * zy + py;
            zx = zx2 - zy2 + px;
            zx2 = zx * zx;
            zy2 = zy * zy;
            value++;
        }
        return value == MAX ? 0 : MAX - value;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        clear();
        int x = e.getX();
        int y = e.getY();
        if (e.getButton() == 1) {
            if (x != mouseX && y != mouseY) {
                int w = getSize().width;
                int h = getSize().height;
                viewX += zoom * Math.min(x, mouseX) / Math.min(w, h);
                viewY += zoom * Math.min(y, mouseY) / Math.min(w, h);
                zoom *= Math.max((double) Math.abs(x - mouseX) / w,
                    (double) Math.abs(y - mouseY) / h);
            }
        } else if (e.getButton() == 3) {
            MAX = 64;
            viewX = viewY = 0.0;
            zoom = 1.0;
        } else {
            MAX += MAX / 4;
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public class Job implements Runnable {

        private final int startingHeight;
        private final int endingHeight;
        private final int startingWidth;
        private final int endingWidth;
        private final double r;

        public Job(int startingHeight, int endingHeight, int startingWidth, int endingWidth, double r) {
            this.startingHeight = startingHeight;
            this.endingHeight = endingHeight;
            this.startingWidth = startingWidth;
            this.endingWidth = endingWidth;
            this.r = r;
        }

        @Override
        public void run() {
            for (int y = startingHeight; y < endingHeight; y++) {
                for (int x = startingWidth; x < endingWidth; x++) {
                    double dx = 2.5 * (x * r + viewX) - 2.0;
                    double dy = 1.25 - 2.5 * (y * r + viewY);
                    int value = mandel(dx, dy);
                    value = value * 255 / MAX;
                    colors[y][x] = new Color(value, value, value);
                }
            }
        }

    }

}
