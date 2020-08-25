public class VerticalReflectFilter implements PixelFilter{

    @Override
    public int[] filter(int[] pixels, int width, int height) {
        short[] bwpixels = PixelLib.convertToShortGreyscale(pixels);
        short[][] img = PixelLib.convertTo2dArray(bwpixels,width,height);

        int lastRow = img.length-1;
        for (int r = 0; r < img.length/2; r++) {
            for (int c = 0; c < img[r].length; c++) {
                short tmp = img[r][c];
                img[r][c] = img[lastRow-r][c];
                img[lastRow-r][c] = tmp;
            }
        }

        PixelLib.fill1dArray(img,pixels);

        return pixels;
    }
}
