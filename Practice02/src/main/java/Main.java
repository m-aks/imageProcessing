import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Main {

    public static final String FORMAT = "jpg";

    public static void main(String[] args) throws IOException {
        Main m = new Main();
        URL url = m.getClass().getResource("pic.jpg");
        if (url != null) {
            BufferedImage orig = ImageIO.read(url);
            m.channels(orig);
            BufferedImage corrected = m.gammaCorrection(orig, 0.5);
            m.rgbChannels(corrected);
            m.difference(orig, corrected);
        }
    }

    private void difference(BufferedImage img, BufferedImage gCor) throws IOException {
        int h = img.getHeight();
        int w = img.getWidth();
        BufferedImage chR = new BufferedImage(w, h, TYPE_INT_RGB);
        BufferedImage chG = new BufferedImage(w, h, TYPE_INT_RGB);
        BufferedImage chB = new BufferedImage(w, h, TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int orig = img.getRGB(x, y);
                int corr = gCor.getRGB(x, y);
                int red = red(orig) - red(corr);
                int green = green(orig) - green(corr);
                int blue = blue(orig) - blue(corr);
                chR.setRGB(x, y, rgb(red, 0, 0));
                chG.setRGB(x, y, rgb(0, green, 0));
                chB.setRGB(x, y, rgb(0, 0, blue));
            }
        }
        save(chR, "result/difference", "r", FORMAT);
        save(chG, "result/difference", "g", FORMAT);
        save(chB, "result/difference", "b", FORMAT);
    }

    public void rgbChannels(BufferedImage img) throws IOException {
        int h = img.getHeight();
        int w = img.getWidth();
        BufferedImage chR = new BufferedImage(w, h, TYPE_INT_RGB);
        BufferedImage chG = new BufferedImage(w, h, TYPE_INT_RGB);
        BufferedImage chB = new BufferedImage(w, h, TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int red = red(rgb);
                int green = green(rgb);
                int blue = blue(rgb);
                chR.setRGB(x, y, rgb(red, 0, 0));
                chG.setRGB(x, y, rgb(0, green, 0));
                chB.setRGB(x, y, rgb(0, 0, blue));
            }
        }
        save(chR, "result/rgbChannels", "r", FORMAT);
        save(chG, "result/rgbChannels", "g", FORMAT);
        save(chB, "result/rgbChannels", "b", FORMAT);
    }

    public BufferedImage gammaCorrection(BufferedImage img, double gamma) throws IOException {
        int h = img.getHeight();
        int w = img.getWidth();
        BufferedImage result = new BufferedImage(w, h, TYPE_INT_RGB);
        int[] gammaLUT = new int[256];
        for (int i = 0; i < gammaLUT.length; i++) {
            gammaLUT[i] = (int) (255 * (Math.pow(i / 255f, 1 / gamma)));
        }
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int red = gammaLUT[red(rgb)];
                int green = gammaLUT[green(rgb)];
                int blue = gammaLUT[blue(rgb)];
                result.setRGB(x, y, rgb(red, green, blue));
            }
        }
        save(result, "result/gammaCor", "result", FORMAT);
        return result;
    }

    public void channels(BufferedImage img) throws IOException {
        int h = img.getHeight();
        int w = img.getWidth();
        BufferedImage chR = new BufferedImage(w, h, TYPE_INT_RGB);
        BufferedImage chG = new BufferedImage(w, h, TYPE_INT_RGB);
        BufferedImage chB = new BufferedImage(w, h, TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int red = red(rgb);
                int green = green(rgb);
                int blue = blue(rgb);
                chR.setRGB(x, y, rgb(red, red, red));
                chG.setRGB(x, y, rgb(green, green, green));
                chB.setRGB(x, y, rgb(blue, blue, blue));
            }
        }
        save(chR, "result/channels", "r", FORMAT);
        save(chG, "result/channels", "g", FORMAT);
        save(chB, "result/channels", "b", FORMAT);
    }

    private static int red(int rgb) {
        return (rgb & 0xff0000) >> 16;
    }

    private static int green(int rgb) {
        return (rgb & 0xff00) >> 8;
    }

    private static int blue(int rgb) {
        return rgb & 0xff;
    }

    private static int rgb(int r, int g, int b) {
        return (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    private static void save(BufferedImage bi, String path, String name, String format) throws IOException {
        Files.createDirectories(Paths.get(path).toAbsolutePath());
        ImageIO.write(bi, format, new File(path + "/" + name + "." + format));
    }
}
