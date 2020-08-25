public class SobelFilter implements PixelFilter {
    static final int THRESHOLD = 90;

    int[][] sobelVertical = {{1,0,-1},
            {2,0,-2},
            {1,0,-1}};

    int[][] sobelHorizontal = {{1,2,1},
            {0,0,0},
            {-1,-2,-1}};

    int[][] thinning1 = {{0,0,0},
            {0,1,0},
            {1,1,1}};

    int[][] thinning2 = {{0,0,0},
            {1,1,0},
            {0,1,0}};

    @Override
    public int[] filter(int[] pixels, int width, int height) {
        short[] bwpixels = PixelLib.convertToShortGreyscale(pixels);
        short[][] img = PixelLib.convertTo2dArray(bwpixels, width, height);

        img = applySobel(img);
        img = applyThinning(img);

        PixelLib.fill1dArray(img, pixels);

        return pixels;
    }

    public short[][] applyKernel(short[][] img, int[][] kernel) {
        short[][] newImg = new short[img.length][img[0].length];

        int maskRows = kernel.length-1;
        int maskCols = kernel[0].length-1;
        for (int r = 0; r < img.length-maskRows; r++) {
            for (int c = 0; c < img[0].length-maskCols; c++) {
                int output = 0;

                for (int rowOffSet = 0; rowOffSet < kernel.length; rowOffSet++) {
                    for (int colOffSet = 0; colOffSet < kernel[0].length; colOffSet++) {
                        int maskValue = kernel[rowOffSet][colOffSet];
                        int pixelVal = img[r+rowOffSet][c+colOffSet];
                        output += maskValue*pixelVal;
                    }
                }
                output = output/getWeight(kernel);
                if (output < 0) output = 0;
                if (output > 255) output = 255;

                output = checkThreshold(output);
                newImg[r+1][c+1] = (short)output;
            }
        }
        return newImg;
    }

    public short[][] applySobel(short[][] img) {
        short[][] img1 = applyKernel(img, sobelHorizontal);
        short[][] img2 = applyKernel(img,sobelVertical);
        short[][] newImg = new short[img.length][img[0].length];

        for (int r = 0; r < img.length; r++) {
            for (int c = 0; c < img[0].length; c++) {
                int gx = img1[r][c];
                int gy = img2[r][c];
                short newPixel = (short)Math.sqrt(gx*gx+gy*gy);
                newImg[r][c] = newPixel;
            }
        }
        return newImg;
    }

    public short[][] applyThinning(short[][] img) {
        for (int i = 0; i < 4; i++) {
            img = applyKernel(img, thinning1);
            img = applyKernel(img, thinning2);
            rotate90degrees(thinning1);
            rotate90degrees(thinning2);
        }
        return img;
    }

    public int[][] rotate90degrees(int[][] kernel) {
        int length = kernel.length;
        int[][] newKernel = new int[length][length];
        for (int r = 0; r < length; r++) {
            for (int c = 0; c < length; c++) {
                newKernel[c][length-1-r] = kernel[r][c];
            }
        }
        return newKernel;
    }

    public int checkThreshold(int output) {
        if (output < THRESHOLD) {
            return 0;
        } else return 255;
    }

    public int getWeight(int[][] kernel) {
        int kernelWeight = 0;
        for (int r = 0; r < kernel.length; r++) {
            for (int c = 0; c < kernel[0].length; c++) {
                kernelWeight += kernel[r][c];
            }
        }
        if (kernelWeight == 0) return 1;
        return kernelWeight;
    }
}
