public class ConvolutionFilter implements PixelFilter {
    int THRESHOLD = 100;
    int[][] edgeDetection1 = {{-1,-1,-1},
            {-1,8,-1},
            {-1,-1,-1}};

    int[][] edgeDetection2 = {{0,1,0},
            {1,-4,1},
            {0,1,0}};

    int[][] sobelVertical = {{1,0,-1},
            {2,0,-2},
            {1,0,-1}};

    int[][] sobelHorizontal = {{1,2,1},
            {0,0,0},
            {-1,-2,-1}};

    int[][] test = {{-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1},
            {-1,-1,8,-1,-1},
            {-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1}};

    int[][] sobel5x5 = {{2,1,0,-1,-2},
            {2,1,0,-1,-2},
            {4,2,0,-2,-4},
            {2,1,0,-1,-2},
            {2,1,0,-1,-2}};

    int[][] edgeDetection = {{-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1},
            {-1,-1,24,-1,-1},
            {-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1}};
    int[][] boxBlur = {{9,9,9},
            {9,9,9},
            {9,9,9}};
    int[][] mask = edgeDetection;
    @Override
    public int[] filter(int[] pixels, int width, int height) {
        short[] bwpixels = PixelLib.convertToShortGreyscale(pixels);
        short[][] img = PixelLib.convertTo2dArray(bwpixels, width, height);
        short[][] newImg = new short[img.length][img[0].length];

        int maskRows = mask.length-1;
        int maskCols = mask[0].length-1;
        for (int r = 0; r < img.length-maskRows; r++) {
            for (int c = 0; c < img[0].length-maskCols; c++) {
                int output = 0;

                for (int rowOffSet = 0; rowOffSet < mask.length; rowOffSet++) {
                    for (int colOffSet = 0; colOffSet < mask[0].length; colOffSet++) {
                        int maskValue = mask[rowOffSet][colOffSet];
                        int pixelVal = img[r+rowOffSet][c+colOffSet];
                        output += maskValue*pixelVal;
                    }
                }
                output = output/getWeight(mask);
                if (output < 0) output = 0;

                if (output < THRESHOLD) {
                    output = 0;
                } else {
                    output = 255;
                }
                newImg[r+1][c+1] = (short)output;
            }
        }

        PixelLib.fill1dArray(newImg, pixels);

        return pixels;
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
