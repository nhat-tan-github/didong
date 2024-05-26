package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.model.Exercise

class ExerciseAdapter(context: Context, private val resource: Int, private val items: List<Exercise>) :
    ArrayAdapter<Exercise>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val exerciseItem = items[position]
        val dayCreatedTextView = view.findViewById<TextView>(R.id.dayCreated)
        val postNameTextView = view.findViewById<TextView>(R.id.postName)
        val exIdTextView = view.findViewById<TextView>(R.id.exId)

        exIdTextView.text = exerciseItem.postId.toString()
        dayCreatedTextView.text = exerciseItem.dayCreated
        postNameTextView.text = exerciseItem.postName

        return view
    }
}
