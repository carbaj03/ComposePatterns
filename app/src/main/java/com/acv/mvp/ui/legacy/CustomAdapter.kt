package com.acv.mvp.ui.legacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.acv.mvp.R
import com.acv.mvp.domain.Task


class CustomAdapter(
    private val tasks: MutableList<Task>,
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    fun addAll(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    fun add(task: Task) {
        tasks.add(task)
        notifyItemInserted(tasks.size)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = tasks[position].task
    }

    override fun getItemCount() = tasks.size
}