import javax.swing.*;

public class ContrastFilter implements PixelFilter {
    private double contrast;
    public ContrastFilter() {
        contrast = Double.parseDouble(JOptionPane.showInputDialog("Enter a number between 0.0 and 2.0"));
    }

    @Override
    public int[] filter(int[] pixels, int width, int height) {
        short[] bwpixels = PixelLib.convertToShortGreyscale(pixels);
        short[][] img = PixelLib.convertTo2dArray(bwpixels, width, height);

        for (int r = 0; r < img.length; r++) {
            for (int c = 0; c < img[r].length; c++) {
                double pixel = (img[r][c]/255.0)-0.5;

                pixel *= contrast;
                pixel += 0.5;
                pixel *= 255.0;
                if (pixel > 255) {
                    pixel = 255;
                }
                if (pixel < 0) {
                    pixel = 0;
                }
                img[r][c] = (short)pixel;
            }
        }

        PixelLib.fill1dArray(img, pixels);

        return pixels;
    }

}
