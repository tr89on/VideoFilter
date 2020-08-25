import javax.swing.*;

public class Randomize implements PixelFilter {
    private int amount;
    public Randomize() {
        amount = Integer.parseInt(JOptionPane.showInputDialog("???"));
    }

    @Override
    public int[] filter(int[] pixels, int width, int height) {
        short[] bwpixels = PixelLib.convertToShortGreyscale(pixels);
        short[][] img = PixelLib.convertTo2dArray(bwpixels, width, height);
        short[][][] sections = new short[amount*amount][height/amount][width/amount];
        int hShift = height/amount;
        int wShift = width/amount;

        for (int i = 0; i < amount; i++) {
            for (int j = 0; j < amount; j++) {
                for (int r = i*hShift; r < (i+1)*hShift; r++) {
                    for (int c = j*wShift; c < (j+1)*wShift; c++) {
                        sections[i*amount+j][r-i*hShift][c-j*wShift] = img[r][c];
                    }
                }
            }
        }

        shuffle(sections);

        for (int i = 0; i < amount; i++) {
            for (int j = 0; j < amount; j++) {
                for (int r = i*hShift; r < (i+1)*hShift; r++) {
                    for (int c = j*wShift; c < (j+1)*wShift; c++) {
                        img[r][c] = sections[i*amount+j][r-i*hShift][c-j*wShift];
                    }
                }
            }
        }


        PixelLib.fill1dArray(img, pixels);

        return pixels;
    }

    public void shuffle(short[][][] sections) {
        for (int i = 0; i < 10; i++) {
            int r1 = (int)(Math.random()*amount*amount);
            int r2 = (int)(Math.random()*amount*amount);
            short[][] temp = sections[r1];
            sections[r1] = sections[r2];
            sections[r2] = temp;
        }
    }
}
