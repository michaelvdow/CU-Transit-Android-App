package com.apps.michaedow.cutransit

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter

class SuggestionsAdapter(context: Context?, cursor: Cursor?) : CursorAdapter(context, cursor, 0) {

    override fun newView(context: Context, cursor: Cursor, viewGroup: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.list_row_suggestion, viewGroup, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val stopName = view.findViewById<TextView>(R.id.stop_suggestion)
        val body = cursor.getString(cursor.getColumnIndex("stop_name"))
        stopName.text = body
    }

    override fun changeCursor(cursor: Cursor) {
        super.swapCursor(cursor)
    }
}
