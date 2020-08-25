import javax.swing.*;
import java.util.ArrayList;


public class SkinFilterDraw implements PixelFilter {
    final short[][] kernel = {{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}};

    private static short[][] out;
    private static short[][] out2;

    private int kernelWeight;

    //red: 186, green: 108, blue: 73
    private short red = 60;
    private short green = 70;
    private short blue = 70;

    private double THRESHOLD = 60;
    private static final int THRESHOLD2 = 254;

    private int numClusters;

    private short[] reds;
    private short[] blues;
    private short[] greens;
    ArrayList<Cluster> clusters;
    ArrayList<Point> oldCenters;
    ArrayList<Point> allPoints;
    ArrayList<Point> drawCenters;

    private boolean first = true;

    public SkinFilterDraw() {
        numClusters = Integer.parseInt(JOptionPane.showInputDialog("enter a number"));

        reds = new short[numClusters];
        blues = new short[numClusters];
        greens = new short[numClusters];

        for (int i = 0; i < numClusters; i++) {
            reds[i] = (short)(Math.random()*256);
            blues[i] = (short)(Math.random()*256);
            greens[i] = (short)(Math.random()*256);
        }

        drawCenters = new ArrayList<>();
    }

    @Override
    public int[] filter(int[] pixels, int width, int height) {
        int[][] pixels2d = PixelLib.convertTo2dArray(pixels, width, height);
        PixelLib.ColorComponents2d img = PixelLib.getColorComponents2d(pixels2d);

        kernelWeight = sumOf(kernel);
        if (out == null) {  // initialize to start, then re-use
            out = new short[height][width];
            out2 = new short[height][width];
        }

        performThreshold(img, out);
        performBlur(out, out2);
        performSecondThreshold(out2);



        oldCenters = new ArrayList<Point>();
        allPoints = getAllPoints(out2);
        //Border
        if (first) {
            clusters = new ArrayList<Cluster>();
            initializeClusters();
        }

        do {
            for (Cluster c : clusters) {
                c.clearPoints();
            }
            for (Point p : allPoints) {
                addToClosestCluster(p, clusters);
            }
            oldCenters = new ArrayList<Point>();
            for (Cluster c : clusters) {
                oldCenters.add(c.getCenter());
                c.reCalculateCenter();
            }
        } while (isUnchanged(clusters, oldCenters));

        // as last step, loop over all points in all your clusters
        //   change color values in img depending on what cluster each
        //   point is part of.
        // -----------------------------------------
        for (Cluster c : clusters) {
            drawCenters.add(c.getCenter());
        }
        for (Point center : drawCenters) {
            int r = center.getRow();
            int c = center.getCol();

            img.red[r][c] = (short)(Math.random()*256);
            img.green[r][c] = (short)(Math.random()*256);
            img.blue[r][c] = (short)(Math.random()*256);
        }

        //PixelLib.fill1dArray(out2, pixels);
        pixels = PixelLib.combineColorComponents(img);
        return pixels;
    }

    private boolean isUnchanged(ArrayList<Cluster> clusters, ArrayList<Point> oldCenters) {
        for (int i = 0; i < clusters.size(); i++) {
            if (!clusters.get(i).equals(oldCenters.get(i))) return false;
        }
        return true;
    }

    private void addToClosestCluster(Point p, ArrayList<Cluster> clusters) {
        Cluster closest = clusters.get(0);
        for (Cluster c : clusters) {
            if (getDistance(p, c) < getDistance(p, closest)) {
                closest = c;
            }
        }
        closest.addPoint(p);
    }

    private double getDistance(Point p, Cluster c) {
        return getDistance(p.getRow(), c.getCenter().getRow(), p.getCol(), c.getCenter().getCol());
    }

    private double getDistance(int r1, int r2, int c1, int c2) {
        return Math.sqrt((r1 - r2) * (r1 - r2) + (c1 - c2) * (c1 - c2));
    }

    private void initializeClusters() {
        for (int i = 0; i < numClusters; i++) {
            int randomR = (int) (Math.random() * out2.length);
            int randomC = (int) (Math.random() * out2[0].length);
            Point center = new Point(randomR, randomC);
            Cluster c = new Cluster(center);

            clusters.add(c);
        }
        first = false;
    }

    private ArrayList<Point> getAllPoints(short[][] out2) {
        ArrayList<Point> points = new ArrayList<Point>();
        for (int r = 0; r < out2.length; r++) {
            for (int c = 0; c < out2[0].length; c++) {
                if (out2[r][c] != 0) {
                    Point p = new Point(r, c);
                    points.add(p);
                }
            }
        }
        return points;
    }

    private void performSecondThreshold(short[][] out2) {
        for (int r = 0; r < out2.length; r++) {
            for (int c = 0; c < out2[0].length; c++) {
                int dist = out2[r][c];
                if (dist > THRESHOLD2) {
                    out2[r][c] = 255;
                } else {
                    out2[r][c] = 0;
                }
            }
        }
    }

    private int sumOf(short[][] kernal) {
        int sum = 0;
        for (int i = 0; i < kernal.length; i++) {
            for (int j = 0; j < kernal[i].length; j++) {
                sum += kernal[i][j];
            }
        }

        if (sum == 0) return 1;
        return sum;
    }

    private void performBlur(short[][] out, short[][] out2) {
        for (int r = 0; r < out.length - kernel.length - 1; r++) {
            for (int c = 0; c < out[0].length - kernel.length - 1; c++) {

                int outputColor = calculateOutputFrom(r, c, out, kernel);
                out2[r][c] = (short) (outputColor / kernelWeight);
                if (out2[r][c] < 0) out2[r][c] = 0;
                if (out2[r][c] > 255) out2[r][c] = 255;
            }
        }
    }

    private int calculateOutputFrom(int r, int c, short[][] im, short[][] kernal) {
        int out = 0;
        for (int i = 0; i < kernal.length; i++) {
            for (int j = 0; j < kernal[i].length; j++) {
                out += im[r + i][c + j] * kernal[i][j];
            }
        }

        return out;
    }

    private void performThreshold(PixelLib.ColorComponents2d img, short[][] out) {
        for (int r = 0; r < out.length; r++) {
            for (int c = 0; c < out[0].length; c++) {
                double dist = distance(red, img.red[r][c], green, img.green[r][c], blue, img.blue[r][c]);
                if (dist > THRESHOLD) {
                    out[r][c] = 0;
                } else {
                    out[r][c] = 255;
                }
            }
        }
    }

    public double distance(short r1, short r2, short g1, short g2, short b1, short b2) {
        int dr = r2 - r1;
        int dg = g2 - g1;
        int db = b2 - b1;

        return Math.sqrt(dr * dr + dg * dg + db * db);
    }
}