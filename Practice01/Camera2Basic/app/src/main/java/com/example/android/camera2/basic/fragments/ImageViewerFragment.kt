package com.example.android.camera2.basic.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.android.camera.utils.BitmapHelper
import com.example.android.camera2.basic.R
import com.example.android.camera2.basic.databinding.FragmentImageViewerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewerFragment : Fragment() {

    /** Android ViewBinding */
    private var _fragmentImageViewerBinding: FragmentImageViewerBinding? = null

    private val fragmentImageViewerBinding get() = _fragmentImageViewerBinding!!

    /** AndroidX navigation arguments */
    private val args: ImageViewerFragmentArgs by navArgs()

    /** Host's navigation controller */
    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.fragment_container)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _fragmentImageViewerBinding = FragmentImageViewerBinding.inflate(inflater, container, false)
        return fragmentImageViewerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listen to the effects button
        fragmentImageViewerBinding.effectsButton.setOnClickListener {

            // Disable click listener to prevent multiple requests simultaneously in flight
            it.isEnabled = false

            lifecycleScope.launch(Dispatchers.Main) {
                navController.navigate(ImageViewerFragmentDirections
                        .actionJpegViewerToEffects(args.filePath)
                        .setFxOrientation(args.orientation)
                )
            }

            // Re-enable click listener after photo is taken
            it.post { it.isEnabled = true }
        }

        val bitmapHelper = BitmapHelper(args.orientation)
        val inputBuffer = bitmapHelper.loadInputBuffer(args.filePath)
        fragmentImageViewerBinding.imageView.setImageBitmap(
                bitmapHelper.decodeBitmap(inputBuffer, 0, inputBuffer.size))
    }

}
