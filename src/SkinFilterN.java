import org.omg.CORBA.BAD_CONTEXT;

import javax.swing.*;
import java.util.ArrayList;


public class SkinFilterN implements PixelFilter {
    final short[][] kernel = {{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}};

    private static short[][] out;
    private static short[][] out2;

    private int kernelWeight;

    //red: 186, green: 108, blue: 73
    private short red = 60;
    private short green = 70;
    private short blue = 75;

    private double THRESHOLD = 60;
    private static final int THRESHOLD2 = 254;

    private final int maxClusters = 10;

    private short[] reds;
    private short[] blues;
    private short[] greens;
    ArrayList<Cluster> clusters;
    ArrayList<Point> oldCenters;
    ArrayList<Point> allPoints;

    private static double NUM_SDS = 3;
    private static double BAD_THRESHOLD = 1.0/NUM_SDS;

    private boolean firstLoop = true;

    public SkinFilterN() {
        reds = new short[maxClusters];
        blues = new short[maxClusters];
        greens = new short[maxClusters];

        for (int i = 0; i < maxClusters; i++) {
            reds[i] = (short) (Math.random() * 256);
            blues[i] = (short) (Math.random() * 256);
            greens[i] = (short) (Math.random() * 256);
        }
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


        //Border
        oldCenters = new ArrayList<Point>();
        allPoints = getAllPoints(out2);


        int numClusters = 0;

        do {
            numClusters++;
            if (firstLoop || !areGoodClusters(clusters)) {
                clusters = new ArrayList<Cluster>();
                initializeClusters(numClusters);
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
        } while (!areGoodClusters(clusters) && numClusters < maxClusters);

        firstLoop = false;

        // as last step, loop over all points in all your clusters
        //   change color values in img depending on what cluster each
        //   point is part of.
        // -----------------------------------------
        for (Cluster cluster : clusters) {
            for (Point p : cluster.getPoints()) {
                int r = p.getRow();
                int c = p.getCol();
                img.red[r][c] = reds[clusters.indexOf(cluster)];
                img.blue[r][c] = blues[clusters.indexOf(cluster)];
                img.green[r][c] = greens[clusters.indexOf(cluster)];
            }
        }

        pixels = PixelLib.combineColorComponents(img);
        return pixels;
    }

    private ArrayList<Cluster> addOneCluster(ArrayList<Cluster> clusters) {
        int randomR = (int) (Math.random() * out2.length);
        int randomC = (int) (Math.random() * out2[0].length);
        Point center = new Point(randomR, randomC);
        Cluster c = new Cluster(center);

        clusters.add(c);
        return clusters;
    }

    private boolean areGoodClusters(ArrayList<Cluster> clusters) {
        for (Cluster c : clusters) {
            if (isBadCluster(c)) return false;
        }
        return true;
    }

    private boolean isBadCluster(Cluster c) {
        double sd = getStandardDeviation(c);

        int badPoints = 0;

        for (Point p : c.getPoints()) {
            if (getDistance(p, c) > NUM_SDS * sd) {
                badPoints++;
            }
        }
        double average = (double) badPoints / c.getPoints().size();
        return average > BAD_THRESHOLD;
    }

    private double getStandardDeviation(Cluster c) {
        int n = c.getPoints().size();
        double mean = 0;
        for (Point p : c.getPoints()) {
            mean += getDistance(p, c);
        }
        mean /= n;

        double sd = 0;
        for (Point p : c.getPoints()) {
            sd += (getDistance(p, c) - mean) * (getDistance(p, c) - mean);
        }
        return Math.sqrt(sd / (n - 1));
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

    private void initializeClusters(int numClusters) {
        for (int i = 0; i < numClusters; i++) {
            int randomR = (int) (Math.random() * out2.length);
            int randomC = (int) (Math.random() * out2[0].length);
            Point center = new Point(randomR, randomC);
            Cluster c = new Cluster(center);

            clusters.add(c);
        }
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