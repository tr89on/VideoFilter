import javax.swing.*;

public class PixelateFilter implements PixelFilter {
    private int rad;
    private int blockSize;
    public PixelateFilter() {
        rad = Integer.parseInt(JOptionPane.showInputDialog("Specify the radius"));
        blockSize = 2*rad+1;
    }

    @Override
    public int[] filter(int[] pixels, int width, int height) {
        short[] bwpixels = PixelLib.convertToShortGreyscale(pixels);
        short[][] img = PixelLib.convertTo2dArray(bwpixels, width, height);

        for (int r = rad; r < img.length; r += blockSize) {
            for (int c = rad; c < img[r].length; c += blockSize) {
                convertToCenterColor(img, r, c);
            }
        }

        PixelLib.fill1dArray(img, pixels);

        return pixels;
    }

    public void convertToCenterColor(short[][] img, int centerR, int centerC) {
        short color = getAverageColor(img,centerR,centerC);
        for (int r = centerR-rad; r < centerR+rad+1; r++) {
            for (int c = centerC-rad; c < centerC+rad+1; c++) {
                if (r < img.length && c < img[r].length) {
                    img[r][c] = color;
                }
            }
        }
    }
    public short getAverageColor(short[][] img, int centerR, int centerC) {
        float average = 0;
        for (int r = centerR-rad; r < centerR+rad+1; r++) {
            for (int c = centerC-rad; c < centerC+rad+1; c++) {
                if (r < img.length && c < img[r].length) {
                    average += img[r][c];
                }
            }
        }
        return (short)(average/(blockSize*blockSize));
    }

}
