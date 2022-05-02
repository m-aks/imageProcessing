## Лабораторная работа 3. Переход между цветовыми пространствами.

В ходе выполнения рабораторной работы было разработано консольное Java приложение.
### Структура проекта
[/result](https://github.com/m-aks/imageProcessing/tree/main/Practice03/result) - папка содержащая результаты работы программы. Каждое задание находится в своей подпапке.

[/src/main/resources/pic.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/src/main/resources/pic.jpg) - исходное многоцветное изображение.

### Перевод цветов из RGB в XYZ

Ниже приведен фрагмент кода, осуществляющий перевод цветов из линейного RGB в XYZ:
```
private static int[] RGBtoXYZ(double r, double g, double b) {
    return new int[]{
            (int) Math.round(r * 0.412453 + g * 0.357580 + b * 0.180423),
            (int) Math.round(r * 0.212671 + g * 0.715160 + b * 0.072169),
            (int) Math.round(r * 0.019334 + g * 0.119193 + b * 0.950227)
    };
}
```
Результат работы: [/result/RGBtoXYZ/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoXYZ/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoXYZ/result.jpg"/>
  
  Рисунок 1 – RGB в XYZ.
</div>

В качестве сторонней библиотеки была выбрана популярная OpenCV. Переход между всеми цветовыми пространствами осуществляется с помощью одной функции `Imgproc.cvtColor`, а направление перехода указывается с помощью константы, передаваемой в качестве последнего параметра.

```
Mat xyzMat = new Mat();
Imgproc.cvtColor(img2Mat(img), xyzMat, Imgproc.COLOR_BGR2XYZ);
BufferedImage resultL = (BufferedImage) HighGui.toBufferedImage(xyzMat);
```
Результат работы: [/result/RGBtoXYZ/resultLib.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoXYZ/resultLib.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoXYZ/resultLib.jpg"/>
  
  Рисунок 2 – RGB в XYZ с помощью OpenCV.
</div>

Сравнение с помощью построения разностного изображения представленной ниже: [/result/RGBtoXYZ/diff.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoXYZ/diff.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoXYZ/diff.jpg"/>
  
  Рисунок 3 – Разностное изображение.
</div>

### Перевод цветов из XYZ в RGB

Ниже приведен фрагмент кода, осуществляющий перевод цветов из XYZ в RGB:
```
private static int XYZtoRGB(int x, int y, int z) {
    double r = x * 3.240479 + y * -1.537150 + z * -0.498535;
    double g = x * -0.969256 + y * 1.875991 + z * 0.041556;
    double b = x * 0.055648 + y * -0.204043 + z * 1.057311;
    return color(r, g, b);
}
```
Результат работы: [/result/XYZtoRGB/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/XYZtoRGB/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/XYZtoRGB/result.jpg"/>
  
  Рисунок 4 – XYZ в RGB.
</div>

Аналогичная функция реализованная с помощью OpenCV.

```
Mat rgbMat = new Mat();
Imgproc.cvtColor(img2Mat(img), rgbMat, Imgproc.COLOR_XYZ2BGR);
BufferedImage resultL = (BufferedImage) HighGui.toBufferedImage(rgbMat);
```
Результат работы: [/result/XYZtoRGB/resultLib.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/XYZtoRGB/resultLib.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/XYZtoRGB/resultLib.jpg"/>
  
  Рисунок 5 – XYZ в RGB с помощью OpenCV.
</div>

Сравнение с помощью построения разностного изображения представленной ниже: [/result/XYZtoRGB/diff.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/XYZtoRGB/diff.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/XYZtoRGB/diff.jpg"/>
  
  Рисунок 6 – Разностное изображение.
</div>

### Проекцию цветов исходного изображения на цветовой локус

Ниже приведен фрагмент кода, осуществляющий проекцию цветов исходного изображения на цветовой локус:
```
public void loscut(BufferedImage img) throws IOException {
    int size = 1000;
    int xMove = (int) Math.round(0.312 * size) / 2;
    int yMove = (int) Math.round(0.329 * size) / 2;
    BufferedImage loscut = new BufferedImage(size, size, TYPE_INT_RGB);
    for (int i = 0; i < img.getHeight(); i++) {
        for (int j = 0; j < img.getWidth(); j++) {
            int rgb = img.getRGB(j, i);
            int[] xyz = RGBtoXYZ(ch1(rgb), ch2(rgb), ch3(rgb));
            double sum = xyz[0] + xyz[1] + xyz[2];
            if (sum > 0) {
                double nx = xyz[0] / sum;
                double ny = xyz[1] / sum;
                double nz = xyz[2] / sum;
                int x = (int) Math.round((1 - ny - nz) * size) + xMove;
                int y = (int) Math.round((1 - nx - nz) * size * -1) + size - yMove;
                try {
                    loscut.setRGB(x, y, rgb);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("x: " + x + "; y: " + (size - y));
                }
            }
        }
    }
    save(loscut, "result/loscut", "result", FORMAT);
}
```
Результат работы: [/result/loscut/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/loscut/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/loscut/result.jpg" width="50%"/>
  
  Рисунок 7 – Цветовой локус.
</div>

### Перевод цветов из RGB в HSV

Ниже приведен фрагмент кода, осуществляющий перевод цветов из RGB в HSV:
```
private static double[] RGBtoHSV(int r, int g, int b) {
    List<Integer> arr = Arrays.asList(r, g, b);
    double min = Collections.min(arr);
    double v = Collections.max(arr);
    double s;
    if (v == 0) {
        s = 0;
    } else {
        s = 1 - min / v;
    }
    double h = 0;
    if (v == r) {
        h = 60 * (g - b) / (v - min);
    } else if (v == g) {
        h = 60 * (b - r) / (v - min) + 120;
    } else if (v == b) {
        h = 60 * (r - g) / (v - min) + 240;
    }
    if (h < 0) h += 360;
    return new double[]{h / 2, s * 255, v * 255};
}
```
Результат работы: [/result/RGBtoHSV/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoHSV/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoHSV/result.jpg"/>
  
  Рисунок 8 – RGB в HSV.
</div>

Аналогичная функция реализованная с помощью OpenCV.

```
Mat hsvMat = new Mat();
Imgproc.cvtColor(img2Mat(img), hsvMat, Imgproc.COLOR_BGR2HSV);
BufferedImage resultL = (BufferedImage) HighGui.toBufferedImage(hsvMat);
```
Результат работы: [/result/RGBtoHSV/resultLib.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoHSV/resultLib.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoHSV/resultLib.jpg"/>
  
  Рисунок 9 – RGB в HSV с помощью OpenCV.
</div>

Сравнение с помощью построения разностного изображения представленной ниже: [/result/RGBtoHSV/diff.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoHSV/diff.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/RGBtoHSV/diff.jpg"/>
  
  Рисунок 10 – Разностное изображение.
</div>

### Перевод цветов из HSV в RGB

Ниже приведен фрагмент кода, осуществляющий перевод цветов из HSV в RGB:
```
public static int HSVtoRGB(float H, float S, float V) {
    float R, G, B;
    H /= 180f;
    S /= 255f;
    V /= 255f;
    if (S == 0) {
        R = V * 255;
        G = V * 255;
        B = V * 255;
    } else {
        float var_h = H * 6;
        if (var_h == 6) {
            var_h = 0;
        }
        int var_i = (int) Math.floor(var_h);
        float var_1 = V * (1 - S);
        float var_2 = V * (1 - S * (var_h - var_i));
        float var_3 = V * (1 - S * (1 - (var_h - var_i)));
        float var_r;
        float var_g;
        float var_b;
        if (var_i == 0) {
            var_r = V;
            var_g = var_3;
            var_b = var_1;
        } else if (var_i == 1) {
            var_r = var_2;
            var_g = V;
            var_b = var_1;
        } else if (var_i == 2) {
            var_r = var_1;
            var_g = V;
            var_b = var_3;
        } else if (var_i == 3) {
            var_r = var_1;
            var_g = var_2;
            var_b = V;
        } else if (var_i == 4) {
            var_r = var_3;
            var_g = var_1;
            var_b = V;
        } else {
            var_r = V;
            var_g = var_1;
            var_b = var_2;
        }
        R = var_r * 255;
        G = var_g * 255;
        B = var_b * 255;
    }
    return color(R, G, B);
}
```
Результат работы: [/result/HSVtoRGB/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/HSVtoRGB/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/HSVtoRGB/result.jpg"/>
  
  Рисунок 11 – HSV в RGB.
</div>

Аналогичная функция реализованная с помощью OpenCV.

```
Mat rgbMat = new Mat();
Imgproc.cvtColor(img2Mat(img), rgbMat, Imgproc.COLOR_HSV2BGR);
BufferedImage resultL = (BufferedImage) HighGui.toBufferedImage(rgbMat);
```
Результат работы: [/result/HSVtoRGB/resultLib.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/HSVtoRGB/resultLib.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/HSVtoRGB/resultLib.jpg"/>
  
  Рисунок 12 – HSV в RGB с помощью OpenCV.
</div>

Сравнение с помощью построения разностного изображения представленной ниже: [/result/HSVtoRGB/diff.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/HSVtoRGB/diff.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice03/result/HSVtoRGB/diff.jpg"/>
  
  Рисунок 13 – Разностное изображение.
</div>
