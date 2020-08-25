import javafx.scene.effect.MotionBlur;

import javax.swing.*;

public class MotionBlurFilter implements PixelFilter {
    private int rad;
    public MotionBlurFilter() {
        rad = Integer.parseInt(JOptionPane.showInputDialog("Specify the radius"));
    }
    @Override
    public int[] filter(int[] pixels, int width, int height) {
        short[] bwpixels = PixelLib.convertToShortGreyscale(pixels);

        short[][] img = PixelLib.convertTo2dArray(bwpixels, width, height);

        for (int r = rad; r < img.length; r++) {
            for (int c = rad; c < img[r].length; c++) {
                convertToNeighborColor(img, r, c);
            }
        }

        PixelLib.fill1dArray(img, pixels);

        return pixels;
    }

    private void convertToNeighborColor(short[][] img, int r, int c) {
        int rightAmount = Math.min(rad,img[0].length-c);
        float avg = 0;
        for (int col = c; col < c+rightAmount; col++) {
            avg += img[r][col];
        }
        img[r][c] = (short)(avg/rightAmount);
    }
}
