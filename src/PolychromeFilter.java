import javax.swing.*;

public class PolychromeFilter implements PixelFilter {
    private int colors;
    private double splitRange;
    public PolychromeFilter() {
        colors = Integer.parseInt(JOptionPane.showInputDialog("Enter number of colors"));
        if (colors > 0) {
            splitRange = 255.0/colors;
        } else {
            splitRange = 1;
        }
    }
    @Override
    public int[] filter(int[] pixels, int width, int height) {
        short[] bwpixels = PixelLib.convertToShortGreyscale(pixels);
        short[][] img = PixelLib.convertTo2dArray(bwpixels, width, height);


        for (int r = 0; r < img.length; r++) {
            for (int c = 0; c < img[r].length; c++) {
                img[r][c] = getSplitColor(img[r][c]);
            }
        }

        PixelLib.fill1dArray(img, pixels);

        return pixels;
    }
    public short getSplitColor(short color) {
        for (double split = 0; split <= 255; split += splitRange) {
            if ((double)color < split+splitRange) {
                return (short)(split+splitRange/2);
            }
        }
        return 0;
    }
}
