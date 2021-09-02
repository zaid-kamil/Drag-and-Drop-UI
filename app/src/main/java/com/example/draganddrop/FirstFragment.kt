package com.example.draganddrop

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.example.draganddrop.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val maskDragMessage = "Mask Added"
    private val maskOn = "Mask On!"
    private val maskOff = "Mask Off"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mask.setOnLongClickListener {
            val item = ClipData.Item(maskDragMessage)
            val clipData =
                ClipData(maskDragMessage, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
            val maskShadow = MaskDragShadowBuilder(it)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                view.startDrag(clipData, maskShadow, it, 0)
            } else {
                view.startDragAndDrop(clipData, maskShadow, it, 0)
            }
            it.visibility = View.INVISIBLE
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class MaskDragShadowBuilder(view: View) : View.DragShadowBuilder(view) {
        private val shadow = ResourcesCompat.getDrawable(
            view.context.resources,
            R.drawable.ic_mask,
            view.context.theme
        )

        override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
            val width = view.width
            val height = view.height
            shadow?.setBounds(0, 0, width, height)
            outShadowSize?.set(width, height)
            outShadowTouchPoint?.set(width / 2, height / 2) // center
        }

        override fun onDrawShadow(canvas: Canvas?) {
            if (canvas != null) {
                shadow?.draw(canvas)
            }
        }
    }
}