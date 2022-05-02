import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

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
        nu.pattern.OpenCV.loadShared();
        Main m = new Main();
        URL url = m.getClass().getResource("pic.jpg");
        if (url != null) {
            BufferedImage orig = ImageIO.read(url);
            BufferedImage blurred = m.gaussian(orig);
            m.sharp(blurred, 5);
            m.sharpLib1(blurred);
            m.sharpLib2(blurred);
        }
    }

    private void sharpLib2(BufferedImage img) throws IOException {
        Mat kernel = new Mat(3, 3, CvType.CV_16SC1);
        kernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
        Mat sharped = new Mat();
        Imgproc.filter2D(img2Mat(img), sharped, -1, kernel);
        BufferedImage result = (BufferedImage) HighGui.toBufferedImage(sharped);
        save(result, "result/sharpLib2", "result", FORMAT);
    }

    private void sharpLib1(BufferedImage img) throws IOException {
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(img2Mat(img), blurred, new Size(0, 0), 3);
        Mat weighted = blurred.clone();
        Core.addWeighted(blurred, 1.5, weighted, -0.5, 0, weighted);
        BufferedImage result = (BufferedImage) HighGui.toBufferedImage(weighted);
        save(result, "result/sharpLib1", "result", FORMAT);
    }

    public BufferedImage sharp(BufferedImage img, int repeat) throws IOException {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), TYPE_INT_RGB);
        GaussianBlur blur = new GaussianBlur();
        for (int i = 0; i < repeat; i++) {
            result = sum(img, diff(img, blur.process(img)));
            img.setData(result.getData());
            save(result, "result/sharp", "result" + i, FORMAT);
        }
        return result;
    }

    public BufferedImage gaussian(BufferedImage img) throws IOException {
        BufferedImage result = new GaussianBlur().process(img);
        save(result, "result/gaussian", "result", FORMAT);
        return result;
    }

    private static BufferedImage diff(BufferedImage imgA, BufferedImage imgB) {
        return operation(imgA, imgB, -1);
    }

    private static BufferedImage sum(BufferedImage imgA, BufferedImage imgB) {
        return operation(imgA, imgB, 1);
    }

    private static BufferedImage operation(BufferedImage imgA, BufferedImage imgB, int sign) {
        if (sign >= 0) {
            sign = 1;
        } else {
            sign = -1;
        }
        int h = imgA.getHeight();
        int w = imgA.getWidth();
        BufferedImage result = new BufferedImage(w, h, TYPE_INT_RGB);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int colorA = imgA.getRGB(j, i);
                int colorB = imgB.getRGB(j, i);
                int r = ch1(colorA) + ch1(colorB) * sign;
                int g = ch2(colorA) + ch2(colorB) * sign;
                int b = ch3(colorA) + ch3(colorB) * sign;
                if (r < 0) r = 0;
                else if (r > 255) r = 255;
                if (g < 0) g = 0;
                else if (g > 255) g = 255;
                if (b < 0) b = 0;
                else if (b > 255) b = 255;
                result.setRGB(j, i, color(r, g, b));
            }
        }
        return result;
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
