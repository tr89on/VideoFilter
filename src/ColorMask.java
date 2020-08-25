import javax.swing.*;

public class ColorMask implements PixelFilter {
    private boolean mouseClick = false;
    private int targetR, targetG, targetB, targetDist;
    public ColorMask () {
        targetR = Integer.parseInt(JOptionPane.showInputDialog("Red:"));
        targetG = Integer.parseInt(JOptionPane.showInputDialog("Green:"));
        targetB = Integer.parseInt(JOptionPane.showInputDialog("Blue:"));
        targetDist = Integer.parseInt(JOptionPane.showInputDialog("Distance:"));
    }
    @Override
    public int[] filter(int[] pixels, int width, int height) {
        PixelLib.ColorComponents2d vals = PixelLib.getColorComponents2d(pixels,width,height);

        if (mouseClick) {
            mouseClick = false;
        } else {
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    int dist = getDist(vals.red[r][c],vals.green[r][c],vals.blue[r][c]);
                    if (dist <= targetDist) {
                        vals.red[r][c] = 255;
                        vals.green[r][c] = 255;
                        vals.blue[r][c] = 255;
                    } else {
                        vals.red[r][c] = 0;
                        vals.green[r][c] = 0;
                        vals.blue[r][c] = 0;
                    }
                }
            }
        }

        pixels = PixelLib.combineColorComponents(vals);
        return pixels;
    }
    public int getDist(int red, int green, int blue) {
        int r = Math.abs(red-targetR);
        int g = Math.abs(green-targetG);
        int b = Math.abs(blue-targetB);
        return (int)(Math.sqrt(r*r+g*g+b*b));
    }
    public void setMouseClick() {
        mouseClick = true;
    }
}
