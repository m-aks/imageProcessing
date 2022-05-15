import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_java;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.xphoto.GrayworldWB;
import org.opencv.xphoto.Xphoto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Main {

    public static final String FORMAT = "jpg";

    public static void main(String[] args) throws IOException {
        Loader.load(opencv_java.class);
        Main m = new Main();
        URL url = m.getClass().getResource("pic.jpg");
        if (url != null) {
            BufferedImage orig = ImageIO.read(url);
            m.whiteBalance(orig);
            m.grayWorld(orig);
            m.grayWorldLib(orig);
        }
    }

    private void whiteBalance(BufferedImage img) throws IOException {
        float[][] m = {
                {255/222f, 0, 0},
                {0, 255/243f, 0},
                {0, 0, 255/255f}
        };
        int h = img.getHeight();
        int w = img.getWidth();
        BufferedImage result = new BufferedImage(w, h, TYPE_INT_RGB);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = img.getRGB(j, i);
                int r = ch1(color);
                int g = ch2(color);
                int b = ch3(color);
                int balancedColor = color(
                        Math.round(r * m[0][0] + g * m[0][1] + b * m[0][2]),
                        Math.round(r * m[1][0] + g * m[1][1] + b * m[1][2]),
                        Math.round(r * m[2][0] + g * m[2][1] + b * m[2][2])
                );
                result.setRGB(j, i, balancedColor);
            }
        }
        save(result, "result/whiteBalance", "result", FORMAT);
    }

    //https://blog.csdn.net/u012736685/article/details/50730784
    private void grayWorld(BufferedImage img) throws IOException {
        int h = img.getHeight();
        int w = img.getWidth();
        float avgRed = 0;
        float avgGreen = 0;
        float avgBlue = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = img.getRGB(j, i);
                avgRed += ch1(color);
                avgGreen += ch2(color);
                avgBlue += ch3(color);
            }
        }
        float pixelCount = h*w;
        avgRed = avgRed/pixelCount;
        avgGreen = avgGreen/pixelCount;
        avgBlue = avgBlue/pixelCount;
        float avgGray = (avgRed+avgGreen+avgBlue)/3f;
        float kr = avgGray/avgRed;
        float kg = avgGray/avgGreen;
        float kb = avgGray/avgBlue;
        BufferedImage result = new BufferedImage(w, h, TYPE_INT_RGB);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = img.getRGB(j, i);
                int r = Math.round(ch1(color)*kr);
                int g = Math.round(ch2(color)*kg);
                int b = Math.round(ch3(color)*kb);
                if (r<0) r=0;
                if (g<0) g=0;
                if (b<0) b=0;
                result.setRGB(j, i, color(r, g, b));
            }
        }
        save(result, "result/grayWorld", "result", FORMAT);
    }

    private void grayWorldLib(BufferedImage img) throws IOException {
        Mat mat = new Mat();
        GrayworldWB alg = Xphoto.createGrayworldWB();
        alg.balanceWhite(img2Mat(img), mat);
        BufferedImage result = (BufferedImage) HighGui.toBufferedImage(mat);
        save(result, "result/grayWorldLib", "result", FORMAT);
    }

    private static Mat img2Mat(BufferedImage image) {
        image = convertTo3ByteBGRType(image);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
    }

    private static BufferedImage convertTo3ByteBGRType(BufferedImage image) {
        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR);
        convertedImage.getGraphics().drawImage(image, 0, 0, null);
        return convertedImage;
    }

    public static int ch1(int color) {
        return (color & 0xff0000) >> 16;
    }

    public static int ch2(int color) {
        return (color & 0xff00) >> 8;
    }

    public static int ch3(int color) {
        return color & 0xff;
    }

    public static int color(int ch1, int ch2, int ch3) {
        return check(ch1) << 16 | check(ch2) << 8 | check(ch3);
    }

    private static int check(int color) {
        return color > 255 ? 255 : color & 0xff;
    }

    private static void save(BufferedImage bi, String path, String name, String format) throws IOException {
        Files.createDirectories(Paths.get(path).toAbsolutePath());
        ImageIO.write(bi, format, new File(path + "/" + name + "." + format));
    }
}
