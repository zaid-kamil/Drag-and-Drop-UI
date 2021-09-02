package com.example.draganddrop

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
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

    private val maskDragListener = View.OnDragListener { view, dragEvent ->
        val draggableItem = dragEvent.localState as View

        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                binding.maskDropArea.alpha = 0.5f
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                binding.maskDropArea.alpha = 1f
                draggableItem.visibility = View.VISIBLE
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggableItem.visibility = View.VISIBLE
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                binding.maskDropArea.alpha = 1f
                if(dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
                    val draggedData = dragEvent.clipData.getItemAt(0).text
                }
                draggableItem.x = dragEvent.x - (draggableItem.width/2)
                draggableItem.y = dragEvent.y - (draggableItem.height/2)
                val parent = draggableItem.parent as ConstraintLayout
                parent.removeView(draggableItem)
                val dropArea = view as ConstraintLayout
                dropArea.addView(draggableItem)
                true
            }
            else -> {
                false
            }

        }
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
        binding.maskDropArea.setOnDragListener(maskDragListener)
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