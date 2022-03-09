import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
class Utils2 {
    public static void showImage(BufferedImage image, String title) {

        // Use label to display the image
        JFrame frame = new JFrame();
        frame.setTitle(title);
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);
        JLabel lbIm1 = new JLabel(new ImageIcon(image));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.getContentPane().add(lbIm1, c);

        frame.pack();
        frame.setVisible(true);


    }
};
public class Problem4 {
    public static BufferedImage getScaledImage(BufferedImage img) {
        BufferedImage newImage = new BufferedImage(img.getWidth() * 50, img.getHeight() * 50, img.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(img, 0, 0, img.getWidth() * 50, img.getHeight() * 50, null);
        g.dispose();
        return newImage;
    }
    public static void main(String[] args) {
        BufferedImage img = new BufferedImage(12, 8, BufferedImage.TYPE_BYTE_GRAY);
        int[] starts = {1, 0, 9, 8, 7, 6, 5, 4};
        int[][] array;
        array = new int[8][];
        for (int i = 0; i < starts.length; ++i) {
            array[i] = new int[12];
            int temp = starts[i];
            for (int j = 0; j < 12; ++j) {
                array[i][j] = temp % 10;
                temp += 1;
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }

        for (int y = 0; y < 8; ++y) {
            for (int x = 0; x < 12; ++x) {
                int pixelValue = 255 - (int) Math.round((255.0 / 9.0) * array[y][x]);
                // int grayLevel = (int) (255.0 * Math.pow(pixelValue, 1.0 / 2.2));
                int gray = ((pixelValue & 0xff) << 16) + ((pixelValue & 0xff) << 8) + (pixelValue & 0xff);
//                System.out.print(gray + " ");
                img.setRGB(x, y, gray);
            }
            // System.out.println();
        }
        // Utils2.showImage(img, "Testing");

        BufferedImage newImage = getScaledImage(img);
        // System.out.println(newImage.getWidth() + " " +  newImage.getHeight());
        Utils2.showImage(newImage, "Part 1");

        // Part - 2
        BufferedImage img2 = new BufferedImage(12, 8, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < 8; ++y) {
            for (int x = 0; x < 12; ++x) {
                int pixelValue = 0;
                if (array[y][x] > 4.5) {
                    pixelValue = 9;
                }
                pixelValue = 255 - (int) Math.round((255.0 / 9.0) * pixelValue);
                // System.out.print(pixelValue + " ");
                int gray = ((pixelValue & 0xff) << 16) + ((pixelValue & 0xff) << 8) + (pixelValue & 0xff);
                img2.setRGB(x, y, gray);
            }
            // System.out.println();
        }
        BufferedImage newImage2 = getScaledImage(img2);
        Utils2.showImage(newImage2, "Part 2");

        // Part - 3 Dithering
        int[][] d= new int[3][];
        d[0] = new int[] {6, 8, 4};
        d[1] = new int[] {1, 0, 3};
        d[2] = new int[] {5, 2, 7};
        BufferedImage img3 = new BufferedImage(12, 8, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < 8; ++y) {
            for (int x = 0; x < 12; ++x) {
                int i = x % 3;
                int j = y % 3;
                int pixelValue = array[y][x];
                int whiteIntensity = 9 - pixelValue;
                if (whiteIntensity > d[j][i]) {
                    pixelValue = 0;
                }
                else {
                    pixelValue = 9;
                }
                pixelValue = 255 - (int) Math.round((255.0 / 9.0) * pixelValue);
                // System.out.print(pixelValue + " ");
                int gray = ((pixelValue & 0xff) << 16) + ((pixelValue & 0xff) << 8) + (pixelValue & 0xff);
                img3.setRGB(x, y, gray);
            }
            // System.out.println();
        }

        BufferedImage newImage3 = getScaledImage(img3);
        Utils2.showImage(newImage3, "Part 3");

        // Part - 4:
        BufferedImage img4 = new BufferedImage(12, 8, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < 8; ++y) {
            for (int x = 0; x < 12; ++x) {
                int i, j;
                if (y == 0 || y == 1) {
                    i = (x + 1) % 3;
                    j = (y + 1) % 3;
                } else {
                    i = x % 3;
                    j = (y - 2) % 3;
                }
                // System.out.println(y + " " + x + " -> " + j + " " + i);
                int pixelValue = array[y][x];
                int whiteIntensity = 9 - pixelValue;
                if (whiteIntensity > d[j][i]) {
                    pixelValue = 0;
                }
                else {
                    pixelValue = 9;
                }
                pixelValue = 255 - (int) Math.round((255.0 / 9.0) * pixelValue);
                // System.out.print(pixelValue + " ");
                int gray = ((pixelValue & 0xff) << 16) + ((pixelValue & 0xff) << 8) + (pixelValue & 0xff);
                img3.setRGB(x, y, gray);
            }
            // System.out.println();
        }
        BufferedImage newImage4 = getScaledImage(img3);
        Utils2.showImage(newImage4, "Part 4");
    }
}
