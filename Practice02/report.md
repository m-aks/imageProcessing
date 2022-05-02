## Лабораторная работа 2.

В ходе выполнения рабораторной работы было разработано консольное Java приложение.
### Структура проекта
[/result](https://github.com/m-aks/imageProcessing/tree/main/Practice02/result) - папка содержащая результаты работы программы. Каждое задание находится в своей подпапке.

[/src/main/resources/pic.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice02/src/main/resources/pic.jpg) - исходное многоцветное изображение.

### Отображение изображения по каналам RGB

Ниже приведен фрагмент кода, осуществляющий отображение изображения по каналам RGB:
```
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
```
Результаты работы: 

[/result/channels/r.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/channels/r.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/channels/r.jpg"/>
  
  Рисунок 1 – Канал R.
</div>

[/result/channels/r.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/channels/g.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/channels/g.jpg"/>
  
  Рисунок 2 – Канал G.
</div>

[/result/channels/b.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/channels/b.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/channels/b.jpg"/>
  
  Рисунок 3 – Канал B.
</div>

### Лианеризация изображения обратным гамма преобразованием.

Ниже приведен фрагмент кода, осуществляющий лианеризацию изображения обратным гамма преобразованием:
```
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
```
Результат работы: 

[/result/gammaCor/result.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/gammaCor/result.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/gammaCor/result.jpg"/>
  
  Рисунок 4 – Лианеризованное изображение.
</div>

### Отображение по каналам RGB.

Ниже приведен фрагмент кода, осуществляющий отображение по каналам RGB:
```
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
```
Результаты работы: 

[/result/rgbChannels/r.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/rgbChannels/r.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/rgbChannels/r.jpg"/>
  
  Рисунок 5 – Канал R.
</div>

[/result/rgbChannels/g.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/rgbChannels/g.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/rgbChannels/g.jpg"/>
  
  Рисунок 6 – Канал G.
</div>

[/result/rgbChannels/b.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/rgbChannels/b.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/rgbChannels/b.jpg"/>
  
  Рисунок 7 – Канал B.
</div>

### Отображение поканальной разницы между исходным изображением и линеаризованным

Ниже приведен фрагмент кода, осуществляющий отображение поканальной разницы между исходным изображением и линеаризованным:
```
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
```
Результаты работы: 

[/result/difference/r.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/difference/r.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/difference/r.jpg"/>
  
  Рисунок 8 – Канал R.
</div>

[/result/difference/g.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/difference/g.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/difference/g.jpg"/>
  
  Рисунок 9 – Канал G.
</div>

[/result/difference/b.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/difference/b.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice02/result/difference/b.jpg"/>
  
  Рисунок 10 – Канал B.
</div>
