public class Point {
    private int row;
    private int col;

    public Point(int r, int c) {
        this.row = r;
        this.col = c;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public String toString() {
        return row + " " + col;
    }

    public boolean equals(Point p) {
        return this.row == p.getRow() && this.col == p.getCol();
    }
}
