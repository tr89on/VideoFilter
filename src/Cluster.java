import java.util.ArrayList;

public class Cluster {
    private Point center;
    private ArrayList<Point> points;
    private short[] color;

    public Cluster(Point center) {
        this.center = center;
        this.points = new ArrayList<Point>();
        color = new short[3];
        for (int i = 0; i < 3; i++) {
            color[i] = (short)(Math.random()*255);
        }
    }

    public short[] getColor() {
        return color;
    }

    public void addPoint(Point p) {
        points.add(p);
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void clearPoints() {
        points = new ArrayList<Point>();
    }

    public Point getCenter() {
        return center;
    }

    public void reCalculateCenter() {
        int avgRow = 0, avgCol = 0;
        for (Point p : points) {
            avgRow += p.getRow();
            avgCol += p.getCol();
        }

        int size = points.size();
        if (size == 0) size = 1;
        avgRow /= size;
        avgCol /= size;

        center = new Point(avgRow, avgCol);
    }

    public String toString() {
        return "Center: " + center.getRow() + " " + center.getCol() + " Size: " + points.size();
    }
}
