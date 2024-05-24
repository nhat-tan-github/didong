package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.model.MyClass

class ClassAdapter(context: Context, private val resource: Int, private val items: List<MyClass>) :
    ArrayAdapter<MyClass>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val classItem = items[position]
        val classIdTextView = view.findViewById<TextView>(R.id.classID)
        val classTitleTextView = view.findViewById<TextView>(R.id.title)

        classIdTextView.text = "ID: ${classItem.id}"
        classTitleTextView.text = classItem.title

        return view
    }
}
