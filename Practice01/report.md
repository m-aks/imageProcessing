## Лабораторная работа 1.

В ходе выполнения рабораторной работы было разработано мобильное Android приложение на языке Kotlin.

### Подготовка среды программирования

В качестве среды программирования была выбрана Android Studio - интегрированная среда разработки (IDE) для работы с платформой Android, с поддержкой языка программирования Kotlin.

### Поиск библиотеки для работы с изображениями

Наиболее популярной библиотекой для обработки изображения является OpenCV. Она поддержиается многими платформами и имеет хорошую документацию с описаниями функций. 
Для облегчения интергации библиотеки была использована [OpenCV-android](https://github.com/quickbirdstudios/opencv-android).

### Чтение изображений с камеры устройства

Благодаря подробной [документации](https://developer.android.com/training/camera2/capture-sessions-requests) было реализовано чтение изображений с камеры устройства в 
различных форматах, включая RAW.

### Получение RAW изображения с устройства

Для получения и обработки изображения необходимо запустить приложение и выбрать один из пунктов меню. 

[/Screenshots/menu.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/menu.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/menu.jpg" width="30%"/>
  
  Рисунок 1 – Меню.
</div>

Данное меню генерируется автоматически с помощью сканирования всех доступных камер и поддерживаемых форматов.

Выберите один из режимов поддерживаемых RAW-съемку и сделайте фотографию.

[/Screenshots/0.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/0.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/0.jpg" width="30%"/>
  
  Рисунок 2 – Предпросмотр камеры.
</div>

### Алгоритм "байеризации"

После захвата изображения камерой приложение отобразит его.

[/Screenshots/1.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/1.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/1.jpg" width="30%"/>
  
  Рисунок 3 – Изображение с камеры.
</div>

Нажав на кнопку :star: приложение перейдет к списку доступных эффектов. Для начал необходимо выбрать BAYER для моделирования пикселизации фильтра Байера.

[/Screenshots/effects.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/effects.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/effects.jpg" width="30%"/>
  
  Рисунок 4 – Список доступных алгоритмов.
</div>

Результат работы алгоритма:

[/Screenshots/2.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/2.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/2.jpg" width="30%"/>
  
  Рисунок 5 – Результат работы алгоритма "байеризации".
</div>

Фрагмент кода:

```
/*
      0 1 2 3 4 5
    0 R G R G R G
    1 G B G B G B
    2 R G R G R G
    3 G B G b G B
    4 R G R G R G
*/
private fun bayer(bitmap: Bitmap): String {
    val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val bg = 255
    IntStream.range(0, result.height).forEach { y ->
        IntStream.range(0, result.width).forEach { x ->
            if (y % 2 == 0) { //rows 0 and 2
                if (x % 2 == 0) { //only red
                    result.setPixel(x, y, Color.argb(red(bitmap.getPixel(x, y)), bg, bg, bg))
                } else { //only green
                    result.setPixel(x, y, Color.argb(green(bitmap.getPixel(x, y)), bg, bg, bg))
                }
            } else { //rows 1 and 3
                if (x % 2 == 0) { //only green
                    result.setPixel(x, y, Color.argb(green(bitmap.getPixel(x, y)), bg, bg, bg))
                } else { //only blue
                    result.setPixel(x, y, Color.argb(blue(bitmap.getPixel(x, y)), bg, bg, bg))
                }
            }
        }
    }
    val output = BitmapHelper.createFile()
    result.compress(Bitmap.CompressFormat.WEBP, 100, FileOutputStream(output))
    return output.absolutePath
}
```

### Алгоритм "суперпикселей"

После получения байеризованного изображения необходимо повторно нажать :star: и выбрать из списка один из нескольких алгоритмов дебайрезации. Выберем SUPER_PIXEL.

Результат работы алгоритма:

[/Screenshots/3.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/3.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/3.jpg" width="30%"/>
  
  Рисунок 6 – Результат работы алгоритма "суперпикселей".
</div>

Фрагмент кода:

```
private fun superPixel(bitmap: Bitmap): String {
    val result = Bitmap.createBitmap(bitmap.width / 2, bitmap.height / 2, bitmap.config)
    IntStream.range(0, result.height).forEach { y ->
        IntStream.range(0, result.width).forEach { x ->
            val r = alpha(bitmap.getPixel(x * 2, y * 2))
            val g = (alpha(bitmap.getPixel(x * 2 + 1, y * 2)) +
                    alpha(bitmap.getPixel(x * 2, y * 2 + 1))) / 2
            val b = alpha(bitmap.getPixel(x * 2 + 1, y * 2 + 1))
            result.setPixel(x, y, Color.rgb(r, g, b))
        }
    }
    val output = BitmapHelper.createFile()
    result.compress(Bitmap.CompressFormat.WEBP, 100, FileOutputStream(output))
    return output.absolutePath
}
```

### Аналог "суперпикселей" из библиотеки

К сожалению, данный аглоритм не поддерживается библиотекой. Вероятно из-за малой эффективности и снижения разрешения изображения вчетверо.

### Алгоритм "билинейной интерполяции"

Вернушись обратно в меню выберем пункт BI_LINEAR.

Результат работы алгоритма:

[/Screenshots/4.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/4.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/4.jpg" width="30%"/>
  
  Рисунок 7 – Результат работы алгоритма "билинейной интерполяции".
</div>

Фрагмент кода:

```
private fun biLinear(bitmap: Bitmap): String {
    val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

    IntStream.range(1, result.height - 1).forEach { y ->
        IntStream.range(1, result.width - 1).forEach { x ->
            if (y % 2 == 0) { //rows 0 and 2
                if (x % 2 == 0) { //only red
                    val r = alpha(bitmap.getPixel(x, y))
                    val g = (alpha(bitmap.getPixel(x, y - 1)) +
                            alpha(bitmap.getPixel(x + 1, y)) +
                            alpha(bitmap.getPixel(x, y + 1)) +
                            alpha(bitmap.getPixel(x - 1, y))) / 4
                    val b = (alpha(bitmap.getPixel(x - 1, y - 1)) +
                            alpha(bitmap.getPixel(x + 1, y - 1)) +
                            alpha(bitmap.getPixel(x - 1, y + 1)) +
                            alpha(bitmap.getPixel(x + 1, y + 1))) / 4
                    result.setPixel(x, y, Color.rgb(r, g, b))
                } else { //only green
                    val r = (alpha(bitmap.getPixel(x - 1, y)) +
                            alpha(bitmap.getPixel(x + 1, y))) / 2
                    val g = alpha(bitmap.getPixel(x, y))
                    val b = (alpha(bitmap.getPixel(x, y - 1)) +
                            alpha(bitmap.getPixel(x, y + 1))) / 2
                    result.setPixel(x, y, Color.rgb(r, g, b))
                }
            } else { //rows 1 and 3
                if (x % 2 == 0) { //only green
                    val r = (alpha(bitmap.getPixel(x, y - 1)) +
                            alpha(bitmap.getPixel(x, y + 1))) / 2
                    val g = alpha(bitmap.getPixel(x, y))
                    val b = (alpha(bitmap.getPixel(x - 1, y)) +
                            alpha(bitmap.getPixel(x + 1, y))) / 2
                    result.setPixel(x, y, Color.rgb(r, g, b))
                } else { //only blue
                    val r = (alpha(bitmap.getPixel(x - 1, y - 1)) +
                            alpha(bitmap.getPixel(x + 1, y - 1)) +
                            alpha(bitmap.getPixel(x - 1, y + 1)) +
                            alpha(bitmap.getPixel(x + 1, y + 1))) / 4
                    val g = (alpha(bitmap.getPixel(x, y - 1)) +
                            alpha(bitmap.getPixel(x + 1, y)) +
                            alpha(bitmap.getPixel(x, y + 1)) +
                            alpha(bitmap.getPixel(x - 1, y))) / 4
                    val b = alpha(bitmap.getPixel(x, y))
                    result.setPixel(x, y, Color.rgb(r, g, b))
                }
            }
        }
    }
    val output = BitmapHelper.createFile()
    result.compress(Bitmap.CompressFormat.WEBP, 100, FileOutputStream(output))
    return output.absolutePath
}
```

### Аналог "билинейной интерполяции" из библиотеки

Вернушись обратно в меню выберем пункт BI_LINEAR_LIB.

Результат работы алгоритма:

[/Screenshots/5.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/5.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/5.jpg" width="30%"/>
  
  Рисунок 8 – Результат работы алгоритма "билинейной интерполяции" из библиотеки.
</div>

Фрагмент кода:

```
lib(bitmap, Imgproc.COLOR_BayerBG2RGB)
private fun lib(bitmap: Bitmap, type: Int): String {
    val height = bitmap.height
    val width = bitmap.width;
    val mat = Mat(height, width, CvType.CV_8U)
    IntStream.range(0, height).forEach { y ->
        IntStream.range(0, width).forEach { x ->
            mat.put(y, x, byteArrayOf(alphaByte(bitmap.getPixel(x, y))))
        }
    }
    val coloredMat = Mat()
    Imgproc.cvtColor(mat, coloredMat, type)
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    IntStream.range(0, height).forEach { y ->
        IntStream.range(0, width).forEach { x ->
            val ch = byteArrayOf(0, 0, 0)
            coloredMat.get(y, x, ch)
            result.setPixel(x, y, Color.rgb(ch[0].toInt(), ch[1].toInt(), ch[2].toInt()))
        }
    }
    val output = BitmapHelper.createFile()
    result.compress(Bitmap.CompressFormat.WEBP, 100, FileOutputStream(output))
    return output.absolutePath
}
```

### Алгоритм "VNG"

Вернушись обратно в меню выберем пункт VNG.

Результат работы алгоритма:

[/Screenshots/6.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/6.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/6.jpg" width="30%"/>
  
  Рисунок 9 – Результат работы алгоритма "VNG".
</div>

Фрагмент кода:

```
private fun vng(bitmap: Bitmap): String {
  val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
  val xPattern = intArrayOf(1, 3, 5, 7)
  val colPattern = intArrayOf(0, 4)
  val rowPattern = intArrayOf(2, 6)
  val plusPattern = intArrayOf(0, 2, 4, 6)
  IntStream.range(2, result.height - 2).forEach { y ->
      IntStream.range(2, result.width - 2).forEach { x ->
          try {
              val gr: Array<IntArray> = arrayOf(
                      intArrayOf(bitmap.getPixel(x, y - 1), bitmap.getPixel(x, y - 2),
                              bitmap.getPixel(x + 1, y - 2)),
                      intArrayOf(bitmap.getPixel(x + 1, y - 1), bitmap.getPixel(x + 2, y - 2),
                              bitmap.getPixel(x + 2, y - 1)),
                      intArrayOf(bitmap.getPixel(x + 1, y), bitmap.getPixel(x + 2, y),
                              bitmap.getPixel(x + 2, y + 1)),
                      intArrayOf(bitmap.getPixel(x + 1, y + 1), bitmap.getPixel(x + 2, y + 2),
                              bitmap.getPixel(x + 1, y + 2)),
                      intArrayOf(bitmap.getPixel(x, y + 1), bitmap.getPixel(x, y + 2),
                              bitmap.getPixel(x - 1, y + 2)),
                      intArrayOf(bitmap.getPixel(x - 1, y + 1), bitmap.getPixel(x - 2, y + 2),
                              bitmap.getPixel(x - 2, y + 1)),
                      intArrayOf(bitmap.getPixel(x - 1, y), bitmap.getPixel(x - 2, y),
                              bitmap.getPixel(x - 2, y - 1)),
                      intArrayOf(bitmap.getPixel(x - 1, y - 1), bitmap.getPixel(x - 2, y - 2),
                              bitmap.getPixel(x - 1, y - 2))
              )
              val threshold = gr.sumOf { g -> g.sumOf { alpha(it) } } / 19
              val red = ArrayList<Int>()
              val green = ArrayList<Int>()
              val blue = ArrayList<Int>()
              if (y % 2 == 0) { //rows 0 and 2
                  if (x % 2 == 0) { //only red
                      red.add(alpha(bitmap.getPixel(x, y)))
                  } else { //only green
                      green.add(alpha(bitmap.getPixel(x, y)))
                  }
              } else { //rows 1 and 3
                  if (x % 2 == 0) { //only green
                      green.add(alpha(bitmap.getPixel(x, y)))
                  } else { //only blue
                      blue.add(alpha(bitmap.getPixel(x, y)))
                  }
              }
              for (i in 0 until 8) {
                  if (gr[i].sumOf { alpha(it) } / 3 <= threshold) {
                      if (y % 2 == 0) { //rows 0 and 2
                          if (x % 2 == 0) when { //X blue, + green
                              xPattern.contains(i) -> blue.add(alpha(gr[i][0]))
                              plusPattern.contains(i) -> green.add(alpha(gr[i][0]))
                          } else when { // | blue, -- red, X green
                              colPattern.contains(i) -> blue.add(alpha(gr[i][0]))
                              rowPattern.contains(i) -> red.add(alpha(gr[i][0]))
                              xPattern.contains(i) -> green.add(alpha(gr[i][0]))
                          }
                      } else { //rows 1 and 3
                          if (x % 2 == 0) when { // | red, -- blue, X green
                              colPattern.contains(i) -> red.add(alpha(gr[i][0]))
                              rowPattern.contains(i) -> blue.add(alpha(gr[i][0]))
                              xPattern.contains(i) -> green.add(alpha(gr[i][0]))
                          } else when { // X red, + green
                              xPattern.contains(i) -> red.add(alpha(gr[i][0]))
                              plusPattern.contains(i) -> green.add(alpha(gr[i][0]))
                          }
                      }
                  }
              }
              val r = if (red.size == 0) 0 else red.sum() / red.size
              val g = if (green.size == 0) 0 else green.sum() / green.size
              val b = if (blue.size == 0) 0 else blue.sum() / blue.size
              result.setPixel(x, y, Color.rgb(r, g, b))
          } catch (t: Throwable) {
              println(t)
              result.setPixel(x, y, Color.rgb(4, 244, 4))
          }
      }
  }
  val output = BitmapHelper.createFile()
  result.compress(Bitmap.CompressFormat.WEBP, 100, FileOutputStream(output))
  return output.absolutePath
}
```

### Аналог "VNG" из библиотеки

Вернушись обратно в меню выберем пункт VNG_LIB.

Результат работы алгоритма:

[/Screenshots/7.jpg](https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/7.jpg)
<div align="center">
  <img src="https://github.com/m-aks/imageProcessing/blob/main/Practice01/Screenshots/7.jpg" width="30%"/>
  
  Рисунок 8 – Результат работы алгоритма "VNG" из библиотеки.
</div>

Поскольку в OpenCV используется одна функция для дебайеризации изображения, то алгоритм задается с помощью параметра этой фунции. Ниже приведен изменившийся фрагмент кода:

```
lib(bitmap, Imgproc.COLOR_BayerBG2RGB_VNG)
```
