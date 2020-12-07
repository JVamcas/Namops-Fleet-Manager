package com.pet001kambala.namopsfleetmanager.ui

import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.model.AbstractModel

abstract class ItemSwipeCallback<K : AbstractModel, T : RecyclerView.ViewHolder>(
    private val mAdapter: AbstractAdapter<K, T>,
    private val mListener: AbstractAdapter.ModelViewClickListener<K>,
    private val leftSwipe: Boolean,
    private val rightSwipe: Boolean
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {

        return when{
            leftSwipe && rightSwipe ->ItemTouchHelper.Callback.makeMovementFlags(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
            leftSwipe -> ItemTouchHelper.Callback.makeMovementFlags(
                0,
                ItemTouchHelper.LEFT
            )

            rightSwipe -> ItemTouchHelper.Callback.makeMovementFlags(
                0,
                ItemTouchHelper.RIGHT
            )
            else -> 0 //all swipes disabled
        }

    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(
            c, recyclerView, viewHolder, dX,
            dY, actionState, isCurrentlyActive
        )
        val deleteBackground = ContextCompat.getDrawable(recyclerView.context,R.drawable.view_delete_drawable)
        val editBackground = ContextCompat.getDrawable(recyclerView.context,R.drawable.view_edit_drawable)
        val deleteIcon = ContextCompat.getDrawable(recyclerView.context,R.drawable.ic_delete)
        val editIcon = ContextCompat.getDrawable(recyclerView.context,R.drawable.ic_edit)

        val itemView = viewHolder.itemView
        if (dX > 0) { // Swiping to the right
            val iconMargin = (itemView.height - editIcon!!.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + editIcon.intrinsicHeight
            val iconLeft = itemView.left + iconMargin
            val iconRight = iconLeft + editIcon.intrinsicWidth
            editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            editBackground!!.setBounds(
                itemView.left, itemView.top,
                itemView.left + dX.toInt(),
                itemView.bottom
            )
            editBackground.draw(c)
            editIcon.draw(c)
        } else if (dX < 0) { // Swiping to the left
            val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + deleteIcon.intrinsicHeight
            val iconLeft = itemView.right - deleteIcon.intrinsicWidth - iconMargin
            val iconRight = itemView.right - iconMargin
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            deleteBackground!!.setBounds(
                itemView.right + dX.toInt(),
                itemView.top, itemView.right, itemView.bottom
            )
            deleteBackground.draw(c)
            deleteIcon.draw(c)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.LEFT -> mListener.onDeleteModel(viewHolder.adapterPosition)
            ItemTouchHelper.RIGHT -> {
                val modelPos = viewHolder.adapterPosition
                mListener.onEditModel(mAdapter.get(modelPos), modelPos)
            }
        }
    }
}