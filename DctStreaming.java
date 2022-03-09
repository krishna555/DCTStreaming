import java.awt.*;
// import java.util.List;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.nio.Buffer;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class Utils {
    public static int getRGBIntFromValues(int r, int g, int b) {
        return 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }

    public static int normalizePixelToRGB(int pixel) {
        if (pixel < 0) {
            // System.out.println("Faulty Pixel : "+ pixel);
            return 0;
        }
        else if (pixel > 255) {
            // System.out.println("Faulty Pixel : "+ pixel);
            return 255;
        }
        return pixel;
    }
    public static int getColorFromPixel(int num, int bitsDisplacement) {
        return (num >> (bitsDisplacement * 8)) & 0xFF;
    }

    public static int[] getRGBArrayFromInteger(int pixel) {
        int[] rgb = new int[3];
        rgb[0] = (pixel >> 16) & 0xff;
        rgb[1] = (pixel >> 8) & 0xff;
        rgb[2] = (pixel) & 0xff;
        return rgb;
    }
    public static JFrame showImage(BufferedImage image, String title) {

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

        return frame;
    }

    public static void customShowImage(BufferedImage image, String title) {

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

    public static void readImageRGB(int width, int height, String imgPath, BufferedImage img)
    {
        try
        {
            int frameLength = width*height*3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            raf.read(bytes);

            int ind = 0;
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                    img.setRGB(x,y,pix);
                    ind++;
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
};
class EncodeDecodeUtils {
    public static void normalizeBlock(int[][] rgbArray) {
        for (int i = 0; i < rgbArray.length; ++i) {
            for (int j = 0; j < rgbArray[0].length; ++j) {
                rgbArray[i][j] -= 128;
            }
        }
    }
    public static double[][] getTMatrix() {
        double[][] tMatrix = new double[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (i == 0)  {
                    tMatrix[i][j] = 1.0 / Math.sqrt(8.0);
                }
                else {
                    double cosine_angle = ((2 * j + 1) * i * Math.PI) / (2 * 8.0);
                    tMatrix[i][j] = Math.sqrt(2 / 8.0) * Math.cos(cosine_angle);
                }
            }
        }
        return tMatrix;
    }
    public static double[][] transposeMatrix(double [][] arr) {
        int m = arr.length;
        int n = arr[0].length;
        double [][] transpose = new double[m][n];
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                transpose[i][j] = arr[j][i];
            }
        }
        return transpose;
    }
    public static double[][] multiplyTM(double[][] mat1, int[][] mat2) {
        double[][] res = new double[8][8];

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                res[i][j] = 0;
                for (int k = 0; k < 8; ++k) {
                    res[i][j] += mat1[i][k] * mat2[k][j];
                }
            }
        }
        return res;
    }
    public static double[][] multiplyMTPrime(double[][] mat1, double[][] mat2) {
        double[][] res = new double[8][8];

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                res[i][j] = 0;
                for (int k = 0; k < 8; ++k) {
                    res[i][j] += mat1[i][k] * mat2[k][j];
                }
            }
        }
        return res;
    }
    public static int[][] getPixelsOfInterest(int[][] rgbArray, int lb_i, int lb_j, int ub_i, int ub_j) {
        int[][] pixelsOfInterest = new int[ub_i - lb_i + 1][ub_j - lb_j + 1];
        for (int i = lb_i, k = 0; i <= ub_i; ++i, ++k) {
            for (int j = lb_j, l = 0; j <= ub_j; ++j, ++l) {
                pixelsOfInterest[k][l] = rgbArray[i][j];
            }
        }
        return pixelsOfInterest;
    }

    public static void copyPixelsOfInterestToResult(double[][] pixels, int lb_i, int lb_j, int ub_i, int ub_j, double[][] res) {
        for (int i = lb_i, k = 0; i <= ub_i; ++i, ++k) {
            for (int j = lb_j, l = 0; j <= ub_j; ++j, ++l) {
                res[i][j] = pixels[k][l];
            }
        }
    }
    public static void copyPixelsOfInterestToResult(int[][] pixels, int lb_i, int lb_j, int ub_i, int ub_j, int[][] res) {
        for (int i = lb_i, k = 0; i <= ub_i; ++i, ++k) {
            for (int j = lb_j, l = 0; j <= ub_j; ++j, ++l) {
                res[i][j] = pixels[k][l];
            }
        }
    }
}
class Encoder {

    public static int[][][] getRGBMatrixFromImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int [][][] rgbMat = new int [3][height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int rgb = image.getRGB(x, y);
                int[] rgbArray = Utils.getRGBArrayFromInteger(rgb);
                rgbMat[0][y][x] = rgbArray[0];
                rgbMat[1][y][x] = rgbArray[1];
                rgbMat[2][y][x] = rgbArray[2];
            }
        }
        return rgbMat;
    }
    public static double[][] getTMTPrimeFor1Block(int[][] rgbArray) {
        double[][] tMatrix = EncodeDecodeUtils.getTMatrix();
        double[][] tMatrixTranspose = EncodeDecodeUtils.transposeMatrix(tMatrix);
        double[][] intermediateMatrix = EncodeDecodeUtils.multiplyTM(tMatrix, rgbArray);
        double[][] resMatrix = EncodeDecodeUtils.multiplyMTPrime(intermediateMatrix, tMatrixTranspose);
        return resMatrix;
    }

    public static double[][] getTMTPrime(int[][] rgbArray) {
        int m = rgbArray.length;
        int n = rgbArray[0].length;
        int i = 0;
        int j = 0;
        double[][] res = new double[m][n];
        while (i < m && j < n) {
            if ((j + 7) < n) {
                int[][] pixelsOfInterest = EncodeDecodeUtils.getPixelsOfInterest(rgbArray, i, j, i + 7, j + 7);
                EncodeDecodeUtils.normalizeBlock(pixelsOfInterest);
                double[][] mat = Encoder.getTMTPrimeFor1Block(pixelsOfInterest);
                EncodeDecodeUtils.copyPixelsOfInterestToResult(mat, i, j, i + 7, j + 7, res);
                j += 8;
            }
            else {
                System.out.println("TO DO: Padding with zeros");
            }
            if (j == n) {
                j = 0;
                i += 8;
            }
        }
        return res;
    }
    public static int[][] quantizeMatrix(double[][] tmtprimeArr, int levels) {
        int height = tmtprimeArr.length;
        int width = tmtprimeArr[0].length;
        int[][] quantized = new int[height][width];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                quantized[i][j] = (int) Math.round(tmtprimeArr[i][j] / Math.pow(2, levels));
            }
        }
        return quantized;
    }
    public static int[][][] getEncodedMatrix(BufferedImage image, int levels) {
        int [][][] rgbMat = Encoder.getRGBMatrixFromImage(image);
        int width = image.getWidth();
        int height = image.getHeight();
        // For each channel, Encode matrix
        double [][][] dctMatrix = new double[3][height][width];
        int [][][] quantizedImageMatrix = new int[3][height][width];
        for (int i = 0; i < 3; ++i) {

            dctMatrix[i] = getTMTPrime(rgbMat[i]);
            quantizedImageMatrix[i] = quantizeMatrix(dctMatrix[i], levels);
//            for (int j = 0; j < quantizedImageMatrix[i].length; ++j) {
//                for (int k = 0; k < quantizedImageMatrix[i][0].length; ++k) {
//                    System.out.print(quantizedImageMatrix[i][j][k] + " ");
//                }
//                System.out.println();
//            }
        }
        return quantizedImageMatrix;
    }
};

class Decoder {
    public static void dequantize(int[][] encodedArray, int levels) {
        for (int i = 0; i < encodedArray.length; ++i) {
            for (int j = 0; j < encodedArray[0].length; ++j) {
                encodedArray[i][j] *= Math.pow(2, levels);
            }
        }
    }
    public static double[][] getTPrimeMTFor1Block(int[][] encodedArray) {
        double[][] tMatrix = EncodeDecodeUtils.getTMatrix();
        double[][] tMatrixTranspose = EncodeDecodeUtils.transposeMatrix(tMatrix);
        double[][] intermediateMatrix = EncodeDecodeUtils.multiplyTM(tMatrixTranspose, encodedArray);
        double[][] resMatrix = EncodeDecodeUtils.multiplyMTPrime(intermediateMatrix, tMatrix);
        return resMatrix;
    }
    public static int[][] rescaleMatrix(double[][] decodedArray) {
        int m = decodedArray.length;
        int n = decodedArray[0].length;
        int[][] rescaledMatrix = new int[m][n];
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                rescaledMatrix[i][j] = Utils.normalizePixelToRGB((int) (Math.round(decodedArray[i][j]) + 128));
            }
        }
        return rescaledMatrix;
    }
    public static int[][] getTPrimeMT(int[][] encodedArray) {
//        dequantize(encodedArray, levels);
        int m = encodedArray.length;
        int n = encodedArray[0].length;
        int i = 0;
        int j = 0;
        int[][] res = new int[m][n];
        while (i < m && j < n) {
            if ((j + 7) < n) {
                int[][] pixelsOfInterest = EncodeDecodeUtils.getPixelsOfInterest(encodedArray, i, j, i + 7, j + 7);
                double[][] mat = Decoder.getTPrimeMTFor1Block(pixelsOfInterest);
                int [][] rescaledMatrix = rescaleMatrix(mat);
                EncodeDecodeUtils.copyPixelsOfInterestToResult(rescaledMatrix, i, j, i + 7, j + 7, res);
                j += 8;
            }
            else {
                System.out.println("TO DO: Padding with zeros");
            }
            if (j == n) {
                j = 0;
                i += 8;
            }
        }
        return res;
    }

    public static int[][][] getDecodedMatrix(int[][][] encodedMatrix) {
        int m = encodedMatrix[0].length;
        int n = encodedMatrix[0][0].length;
        int [][][] decodedMatrix = new int [3][][];
        for (int i = 0; i < 3; ++i) {
            decodedMatrix[i] = getTPrimeMT(encodedMatrix[i]);
//            for(int j = 0; j < m; ++j) {
//                for (int k = 0; k < n; ++k) {
//                    System.out.print(decodedMatrix[i][j][k] + " ");
//                }
//                System.out.println();
//            }
        }
        return decodedMatrix;
    }

    public static HashMap<Integer, java.util.List<Integer>> getSpectralOrderingFor8x8() {
        HashMap<Integer, java.util.List<Integer>> indexes = new HashMap<Integer, java.util.List<Integer>>();
        ArrayList<ArrayList<java.util.List<Integer>>> allIndexes = new ArrayList<>();
        for (int i = 0; i < 8 + 8 - 1; ++i) {
            allIndexes.add(new ArrayList<java.util.List<Integer>>());
        }
        int s;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                s = i + j;
                Integer[] arr = {i, j};
                allIndexes.get(s).add(Arrays.asList(arr));
            }
        }
        int k = 0;
        for (int i = 0; i < 15; ++i) {
            if (i % 2 == 0) {
                for (int j = allIndexes.get(i).size() - 1; j >= 0; j--) {
                    indexes.put(k, allIndexes.get(i).get(j));
                    k += 1;
                }
            }
            else {
                for (int j = 0; j < allIndexes.get(i).size(); ++j) {
                    indexes.put(k, allIndexes.get(i).get(j));
                    k += 1;
                }
            }
        }
        return indexes;
    }
};
public class DctStreaming {

    public static void showImageNextToPreviousFrame(BufferedImage image, String title, JFrame inputImageFrame, JFrame frame) {

        // Use label to display the image
        // JFrame frame = new JFrame();
        frame.setTitle(title);
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().removeAll();
        // frame.revalidate();
        // frame.repaint();
        frame.getContentPane().setLayout(gLayout);
        JLabel lbIm1 = new JLabel(new ImageIcon(image));
        frame.setLocation(inputImageFrame.getX() + inputImageFrame.getWidth(), inputImageFrame.getY());

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

    public static BufferedImage populateDecodedImage(int[][][] decodedArr, int width, int height) {
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int red = decodedArr[0][y][x];
                int green = decodedArr[1][y][x];
                int blue = decodedArr[2][y][x];
               // System.out.println(y+ " " + x + " " + red + " " + green + " " + blue);
                int pix = 0xff000000 |((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
                finalImage.setRGB(x, y, pix);
            }
        }
        return finalImage;
    }

    public static JFrame BaselineMode(int[][][] decodedArr, int width, int height, JFrame inputImageFrame, Long latency) throws InterruptedException {
        int y = 0;
        int x = 0;
        JFrame finalFrame = new JFrame();
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        while (y < height && x < width) {
            int curr_cnt = 0;
            int prev_x = x;
            int prev_y = y;
            while (curr_cnt < 64) {
                curr_cnt += 1;
                int red = decodedArr[0][y][x];
                int green = decodedArr[1][y][x];
                int blue = decodedArr[2][y][x];
                int pix = 0xff000000 | ((red & 0xff) << 16) | ((green & 0xff) << 8) | ((blue & 0xff));
                finalImage.setRGB(x, y, pix);
                x += 1;
                if (curr_cnt %  8 == 0) {
                    x = prev_x;
                    y += 1;
                }
            }
            x = prev_x + 8;
            y = prev_y;
            if (x == width) {
                x = 0;
                y = prev_y + 8;
            }
            showImageNextToPreviousFrame(finalImage, "Baseline Mode", inputImageFrame, finalFrame);
            // TimeUnit.MILLISECONDS.sleep(latency);
        }
        return  finalFrame;
    }
    public static void spectralForOneIteration(int [][][] decodedArr,
                                               int width,
                                               int height,
                                               int curr_spectral_cnt,
                                               JFrame leftImageFrame,
                                               JFrame resultFrame,
                                               HashMap<Integer, java.util.List<Integer>> spectralOrdering) {
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int y = 0;
        int x = 0;
        int local_cnt;
        while (y < height && x < width) {
            int prev_x = x, prev_y = y;
            local_cnt = 0;
            int pix;
            while (local_cnt < curr_spectral_cnt) {
                int ind_y = spectralOrdering.get(local_cnt).get(0);
                int ind_x = spectralOrdering.get(local_cnt).get(1);
                y = prev_y + ind_y;
                x = prev_x + ind_x;
                int red = decodedArr[0][y][x];
                int green = decodedArr[1][y][x];
                int blue = decodedArr[2][y][x];
                pix = 0xff000000 | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
                finalImage.setRGB(x, y, pix);
                local_cnt += 1;
            }
            x = prev_x;
            y = prev_y;
            x += 8;
            if (x == width) {
                y += 8;
                x = 0;
            }
            // TODO: Do we need to set to 0 for rest of pixels??
        }
        showImageNextToPreviousFrame(finalImage, "Progressive Mode - Spectral Selection", leftImageFrame, resultFrame);
    }

    public static void progressiveForOneIteration(int[][][] decodedArr, int width, int height, long mask, JFrame leftImageFrame, JFrame resultFrame) {
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int x = 0;
        int y = 0;
        int red, green, blue;
        while (y < height && x < width) {
            red = decodedArr[0][y][x];
            green = decodedArr[1][y][x];
            blue = decodedArr[2][y][x];
            int pix = (int) ((0xff000000 | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff)) & mask);
            finalImage.setRGB(x, y, pix);
            x += 1;
            if (x == width) {
                x = 0;
                y += 1;
            }
        }
        showImageNextToPreviousFrame(finalImage, "Progressive Mode: Successive Bit Approximation", leftImageFrame, resultFrame);
    }
//    public static void spectralSelectionDisplay(int[][][] decodedArr, int width, int height, JFrame frame, long latency) throws InterruptedException {
//        int curr_spectral_cnt = 64;
//        JFrame resultFrame = new JFrame();
//        HashMap<Integer, java.util.List<Integer>> spectralOrdering = Decoder.getSpectralOrderingFor8x8();
//        while (curr_spectral_cnt <= 8 * 8) {
//            spectralForOneIteration(decodedArr, width, height, curr_spectral_cnt, frame, resultFrame, spectralOrdering);
//           Utils.showImage(populateDecodedImage(decodedArr, width, height), "test2");
//            // TimeUnit.MILLISECONDS.sleep(latency);
//            curr_spectral_cnt += 1;
//        }
//    }

    public static int[][][] getDecodeArrForSpectralIteration(
            int[][][] encodedArr,
            int width,
            int height,
            int curr_spectral_cnt,
            HashMap<Integer, java.util.List<Integer>> spectralOrdering) {
        int [][][] matrix = new int[3][height][width];
        int y = 0;
        int x = 0;
        int local_cnt;
        while (y < height && x < width) {
            int prev_x = x, prev_y = y;
            local_cnt = 0;
            while (local_cnt < curr_spectral_cnt) {
                int ind_y = spectralOrdering.get(local_cnt).get(0);
                int ind_x = spectralOrdering.get(local_cnt).get(1);
                y = prev_y + ind_y;
                x = prev_x + ind_x;
                int red = encodedArr[0][y][x];
                int green = encodedArr[1][y][x];
                int blue = encodedArr[2][y][x];
                matrix[0][y][x] = red;
                matrix[1][y][x] = green;
                matrix[2][y][x] = blue;
                local_cnt += 1;
            }
            x = prev_x;
            y = prev_y;
            x += 8;
            if (x == width) {
                y += 8;
                x = 0;
            }
            // TODO: Do we need to set to 0 for rest of pixels??
        }
        return matrix;
    }
    public static void spectralSelectionDisplay(int[][][] encodedArr, int width, int height, JFrame leftImageFrame, long latency, int quantizationLevel) throws InterruptedException {
        // First Copy the Matrix since we do quantisation at every step.

        HashMap<Integer, java.util.List<Integer>> spectralOrdering = Decoder.getSpectralOrderingFor8x8();
        int curr_spectral_cnt = 1;
        JFrame resultFrame = new JFrame();
        while (curr_spectral_cnt <= 8 * 8) {
            int[][][] filteredArr = getDecodeArrForSpectralIteration(encodedArr, width, height, curr_spectral_cnt, spectralOrdering);
            int[][][] decodedMatrix = Decoder.getDecodedMatrix(filteredArr);
            BufferedImage finalImage = populateDecodedImage(decodedMatrix, width, height);
            showImageNextToPreviousFrame(finalImage, "Progressive Mode - Spectral Selection", leftImageFrame, resultFrame);
            curr_spectral_cnt += 1;
            TimeUnit.MILLISECONDS.sleep(latency);
        }
    }
    public static void progressiveDisplay(int[][][] decodedArr, int width, int height, JFrame jframe, long latency) throws InterruptedException {
        long mask = 0;
        JFrame resultFrame = new JFrame();
        for (int i = 1; i <= 32; ++i) {
            mask = mask | (1 << (32 - i));
            progressiveForOneIteration(decodedArr, width, height, mask, jframe, resultFrame);
            TimeUnit.MILLISECONDS.sleep(latency);
        }
    }
    public static int[][][] getMaskedArrForSuccessiveBitApprox(int[][][] encodedArr, int width, int height, long mask) {
        int [][][] filteredArr = new int[encodedArr.length][encodedArr[0].length][encodedArr[0][0].length];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                filteredArr[0][y][x] = (int) ((encodedArr[0][y][x]) & mask);
                filteredArr[1][y][x] = (int) ((encodedArr[1][y][x]) & mask);
                filteredArr[2][y][x] = (int) ((encodedArr[2][y][x]) & mask);
            }
        }
        return filteredArr;
    }
    public static void  successiveBitApproxDisplay(int[][][] encodedArr, int width, int height, JFrame leftImageFrame, long latency) throws InterruptedException {
        long mask = 0;
        JFrame resultFrame = new JFrame();
        for (int i = 1; i <= 32; ++i) {
            mask = mask | (1 << (32 - i));
            System.out.println("Iteration : " + i);
            int[][][] filteredArr = getMaskedArrForSuccessiveBitApprox(encodedArr, width, height, mask);
            int[][][] decodedMatrix = Decoder.getDecodedMatrix(filteredArr);
            BufferedImage finalImage = populateDecodedImage(decodedMatrix, width, height);
            showImageNextToPreviousFrame(finalImage, "Progressive Mode - Successive Bit Approximation", leftImageFrame, resultFrame);
            TimeUnit.MILLISECONDS.sleep(latency);
        }
    }
    public static void main(String[] args) throws InterruptedException {
        String inputImagePath = args[0];
        int quantizationLevel = Integer.parseInt(args[1]);
        int deliveryMode = Integer.parseInt(args[2]);
        Long latency = Long.parseLong(args[3]);
        int width = 352;
        int height = 288;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Utils.readImageRGB(width, height, inputImagePath, image);
        JFrame inputImageFrame = Utils.showImage(image, "Input Image");
        int[][][] encodedArr = Encoder.getEncodedMatrix(image, quantizationLevel);
        for(int i = 0; i < 3; ++i) {
            Decoder.dequantize(encodedArr[i], quantizationLevel);
        }
        //
        if (deliveryMode == 1) {
            int[][][] decodedArr = Decoder.getDecodedMatrix(encodedArr);
            BaselineMode(decodedArr, width, height, inputImageFrame, latency);
        }
        else if (deliveryMode == 2) {
            // spectralSelectionDisplay(decodedArr, width, height, inputImageFrame, latency);
             // Utils.showImage(populateDecodedImage(decodedArr, width, height), "Test Pre");
            spectralSelectionDisplay(encodedArr, width, height, inputImageFrame, latency, quantizationLevel);
        }
        else {
            successiveBitApproxDisplay(encodedArr, width, height, inputImageFrame, latency);
            // progressiveDisplay(decodedArr, width, height, inputImageFrame, latency);
        }
    }
}
