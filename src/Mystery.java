import javax.swing.*;

public class Mystery implements PixelFilter{
    private int amount;
    public Mystery() {
        amount = Integer.parseInt(JOptionPane.showInputDialog("???"));
    }
    @Override
    public int[] filter(int[] pixels, int width, int height) {
        PixelLib.ColorComponents2d vals = PixelLib.getColorComponents2d(pixels,width,height);

        int hShift = height/amount;
        int wShift = width/amount;

        for (int r = 0; r < hShift; r++) {
            for (int c = 0; c < wShift; c++) {
                for (int i = 0; i < amount; i++) {
                    for (int j = 0; j < amount; j++) {
                        vals.red[r+hShift*i][c+wShift*j] = vals.red[r][c];
                        vals.green[r+hShift*i][c+wShift*j] = vals.green[r][c];
                        vals.blue[r+hShift*i][c+wShift*j] = vals.blue[r][c];
                    }
                }
            }
        }


        pixels = PixelLib.combineColorComponents(vals);
        return pixels;
    }
}
