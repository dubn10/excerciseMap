package com.pablodb.excercisemap.customViews

import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterView
import android.widget.GridView

class AllGridView : GridView {

    constructor(context: Context) : super(context)
    constructor(context : Context, attrs : AttributeSet) : super(context, attrs)
    constructor(context : Context, attrs : AttributeSet, defStyle : Int) : super(context,attrs,defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(
            Int.MAX_VALUE shr 2,
            MeasureSpec.AT_MOST )
        super.onMeasure(widthMeasureSpec, expandSpec)
    }

    override fun setOnItemClickListener(listener: OnItemClickListener?) {
        super.setOnItemClickListener(listener)
    }
}