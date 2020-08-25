import javax.swing.*;

public class MonochromeFilter implements PixelFilter {
    private int splitPoint;
    public MonochromeFilter() {
        splitPoint = Integer.parseInt(JOptionPane.showInputDialog("Enter Split Point"));
    }
    @Override
    public int[] filter(int[] pixels, int width, int height) {
        short[] bwpixels = PixelLib.convertToShortGreyscale(pixels);
        short[][] img = PixelLib.convertTo2dArray(bwpixels, width, height);


        for (int r = 0; r < img.length; r++) {
            for (int c = 0; c < img[r].length; c++) {
                if (img[r][c] > splitPoint) {
                    img[r][c] = 255;
                } else {
                    img[r][c] = 0;
                }
            }
        }

        PixelLib.fill1dArray(img, pixels);

        return pixels;
    }
}
