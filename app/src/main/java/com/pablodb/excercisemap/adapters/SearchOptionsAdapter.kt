package com.pablodb.excercisemap.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pablodb.excercisemap.R
import org.json.JSONArray
import org.json.JSONObject

class SearchOptionsAdapter(val context : Context, var dataSet: JSONArray) : BaseAdapter() {
    override fun getCount(): Int {
        return dataSet.length()
    }

    override fun getItem(p0: Int): JSONObject {
        return dataSet[p0] as JSONObject
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var v = p1
        val obj = getItem(p0)
        if (p1 == null) {
            val inf = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inf.inflate(R.layout.element_list_option, null)
        }

        v!!.findViewById<TextView>(R.id.tv_option).text = obj.get("description").toString()

        return v!!
    }
}