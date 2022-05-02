## Лабораторная работа 4. Повышение резкости изображений.

В ходе выполнения рабораторной работы было разработано консольное Java приложение.
### Структура проекта
[/result](https://github.com/m-aks/imageProcessing/tree/main/Practice04/result) - папка содержащая результаты работы программы. Каждое задание находится в своей подпапке.

[/src/main/resources/pic.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice04/src/main/resources/pic.jpg) - исходное многоцветное изображение.

### Гауссово размытие
Алгоритм представлен в классе [GaussianBlur.java](https://github.com/m-aks/imageProcessing/blob/main/Practice04/src/main/java/GaussianBlur.java).

Ниже приведен фрагмент кода, осуществляющий размытие по одному из указанных направление:
```
  for (int i = 0; i < height; i++) {
      int y = i - radius;
      for (int j = 0; j < width; j++) {
          int rsum = 0;
          int gsum = 0;
          int bsum = 0;
          int sum = 0;
          int read = y * width + j;
          int tempy = y;
          for (int z = 0; z < kernel.length; z++) {
              if (tempy >= 0 && tempy < height) {
                  if (blurType == BlurType.VERTICAL) {
                      rsum += multable[z][r[read]];
                      gsum += multable[z][g[read]];
                      bsum += multable[z][b[read]];
                  } else {
                      rsum += multable[z][r2[read]];
                      gsum += multable[z][g2[read]];
                      bsum += multable[z][b2[read]];
                  }
                  sum += kernel[z];
              }
              read += width;
              ++tempy;
          }
          result.setRGB(j, i, Main.color(rsum / sum, gsum / sum, bsum / sum));
      }
  }
```
Результат работы: [/result/gaussian/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice04/result/gaussian/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice04/result/gaussian/result.jpg"/>
  
  Рисунок 1 – Гауссово размытие
</div>

### Функция повышения резкости методом усиления границ
Данная функция применяет Гауссово размытие повторно и вычитает результат из оригинального изображения. Таким образом получается "маска" с усиленными границами, которая прибавляется к исходному изображение. 
```
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
```
Результат работы: [/result/sharp/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice04/result/sharp/result2.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice04/result/sharp/result2.jpg"/>
  
  Рисунок 2 – Повышения резкости
</div>

### Библиотечные функции повышения резкости
В качестве сторонней библиотеки была выбрана популярная OpenCV.

#### Первая функция
Эта функция является аналогичной той что была рассмотрена выше, разница лишь в способе вычисления. Здесь используется всё то же размытие по Гауссу, но вычитание размытой версии из исходного изображения происходит взвешенным образом.
```
  private void sharpLib1(BufferedImage img) throws IOException {
      Mat blurred = new Mat();
      Imgproc.GaussianBlur(img2Mat(img), blurred, new Size(0, 0), 3);
      Mat weighted = blurred.clone();
      Core.addWeighted(blurred, 1.5, weighted, -0.5, 0, weighted);
      BufferedImage result = (BufferedImage) HighGui.toBufferedImage(weighted);
      save(result, "result/sharpLib1", "result", FORMAT);
  }
```
Результат работы: [/result/sharpLib1/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice04/result/sharpLib1/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice04/result/sharpLib1/result.jpg"/>
  
  Рисунок 3 – Повышения резкости с помощью OpenCV. Способ первый. 
</div>

#### Вторая функция
В этом способе повышения резкости ипользуется функция 2D-фильтрации и сверточная матрица, часто называемая ядром. 

<div align="center">
  <img src="https://user-images.githubusercontent.com/57611938/166205549-e30d8713-5763-441d-a8d7-d83c37366d3d.png"/>
  
  Рисунок 4 – Свёрточная матрица. 
</div>

```
  private void sharpLib2(BufferedImage img) throws IOException {
      Mat kernel = new Mat(3, 3, CvType.CV_16SC1);
      kernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
      Mat sharped = new Mat();
      Imgproc.filter2D(img2Mat(img), sharped, -1, kernel);
      BufferedImage result = (BufferedImage) HighGui.toBufferedImage(sharped);
      save(result, "result/sharpLib2", "result", FORMAT);
  }
```
Результат работы: [/result/sharpLib2/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice04/result/sharpLib2/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice04/result/sharpLib2/result.jpg"/>
  
  Рисунок 5 – Повышения резкости с помощью OpenCV. Способ второй. 
</div>
