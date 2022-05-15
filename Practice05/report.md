## Лабораторная работа 5. Баланс белого.

В ходе выполнения рабораторной работы было разработано консольное Java приложение.
### Структура проекта
[/result](https://github.com/m-aks/imageProcessing/tree/main/Practice05/result) - папка содержащая результаты работы программы. Каждое задание находится в своей подпапке.

[/src/main/resources/original.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice05/src/main/resources/original.jpg) - изображение с правильным балансом белого.

[/src/main/resources/pic.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice05/src/main/resources/pic.jpg) - изображение с неправильным балансом белого.

### Преобразование с помощью матрицы
С помощью color picker'а, встроенного в среду разработки, было определено значение "белого" цвета, находящегося на хвосте снигеря. 

<div align="center">
  <img src="https://user-images.githubusercontent.com/57611938/168489700-7069818d-f1e4-40a5-80b6-3b76dc2f8b94.png"/>
  
  Рисунок 1 – Ручное определение цвета
</div>

Была построена матрица преобразования. Ниже приведен фрагмент кода, осуществляющий корректировку ББ с помощью матрицы:
```
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
```
Результат работы: [/result/whiteBalance/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice05/result/whiteBalance/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice05/result/whiteBalance/result.jpg"/>
  
  Рисунок 2 – Результат коррекции ББ с помощью матрицы
</div>

### Теория Серого мира

Ниже приведен фрагмент кода, осуществляющий корректировку ББ с помощью теории Серого мира:
```
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
```
Результат работы: [/result/grayWorld/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice05/result/grayWorld/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice05/result/grayWorld/result.jpg"/>
  
  Рисунок 3 – Результат коррекции ББ с помощью теории Серого мира
</div>

### Теория Серого мира (OpenCV)

Ниже приведен фрагмент кода, осуществляющий корректировку ББ с помощью библиотечной функции теории Серого мира:
```
private void grayWorldLib(BufferedImage img) throws IOException {
    Mat mat = new Mat();
    GrayworldWB alg = Xphoto.createGrayworldWB();
    alg.balanceWhite(img2Mat(img), mat);
    BufferedImage result = (BufferedImage) HighGui.toBufferedImage(mat);
    save(result, "result/grayWorldLib", "result", FORMAT);
}
```
Результат работы: [/result/grayWorldLib/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice05/result/grayWorldLib/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice05/result/grayWorldLib/result.jpg"/>
  
  Рисунок 4 – Результат коррекции ББ с помощью библиотечной функции теории Серого мира
</div>
