package com.example.android.camera2.basic.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.camera.utils.BitmapHelper
import com.example.android.camera.utils.GenericListAdapter
import com.example.android.camera2.basic.Effects
import com.example.android.camera2.basic.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.FileOutputStream
import java.util.stream.IntStream

class EffectsFragment : Fragment() {

    private val args: EffectsFragmentArgs by navArgs()

    /** Host's navigation controller */
    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.fragment_container)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = RecyclerView(requireContext())

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view as RecyclerView
        view.apply {
            layoutManager = LinearLayoutManager(requireContext())
            val effectsList = Effects.values().toList()
            val layoutId = android.R.layout.simple_list_item_1
            adapter = GenericListAdapter(effectsList, itemLayoutId = layoutId) { view, item, _ ->
                view.findViewById<TextView>(android.R.id.text1).text = item.toString()
                view.setOnClickListener { process(item) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun process(effect: Effects) {
        val bitmapHelper = BitmapHelper(args.fxOrientation)
        val inputBuffer = bitmapHelper.loadInputBuffer(args.filePath)
        val bitmap = bitmapHelper.decodeBitmap(inputBuffer, 0, inputBuffer.size)
        lifecycleScope.launch(Dispatchers.Default) {
            val result = when (effect) {
                Effects.BAYER -> bayer(bitmap)
                Effects.SUPER_PIXEL -> superPixel(bitmap)
                Effects.BI_LINEAR -> biLinear(bitmap)
                Effects.BI_LINEAR_LIB -> biLinearLib(bitmap)
                Effects.VNG -> vng(bitmap)
                Effects.VNG_LIB -> vngLib(bitmap)
            }
            lifecycleScope.launch(Dispatchers.Main) {
                navController.navigate(EffectsFragmentDirections
                        .actionEffectsToJpegViewer(result)
                )
            }
        }
    }

    /*
          0 1 2 3 4 5
        0 R G R G R G
        1 G B G B G B
        2 R G R G R G
        3 G B G b G B
        4 R G R G R G
    */
    @RequiresApi(Build.VERSION_CODES.N)
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

    @RequiresApi(Build.VERSION_CODES.N)
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

    @RequiresApi(Build.VERSION_CODES.N)
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun biLinearLib(bitmap: Bitmap): String = lib(bitmap, Imgproc.COLOR_BayerBG2RGB)

    @RequiresApi(Build.VERSION_CODES.N)
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun vngLib(bitmap: Bitmap): String = lib(bitmap, Imgproc.COLOR_BayerBG2RGB_VNG)

    @RequiresApi(Build.VERSION_CODES.N)
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

    private fun alphaByte(color: Int): Byte = ((color shr 24) and 0xff).toByte()
    private fun alpha(color: Int): Int = (color shr 24) and 0xff
    private fun red(color: Int): Int = (color shr 16) and 0xff
    private fun green(color: Int): Int = (color shr 8) and 0xff
    private fun blue(color: Int): Int = color and 0xff

}