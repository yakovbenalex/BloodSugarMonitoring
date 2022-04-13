package ru.opalevapps.EveryGlic.db

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ru.opalevapps.EveryGlic.R
import java.text.SimpleDateFormat

class ItemRecordsAdapter(
    context: Context, arr: ArrayList<ItemRecords>?,
    prefsBloodLowSugar: Float, prefsBloodHighSugar: Float,
    prefsUnitBloodSugarMmol: Boolean, prefsTimeFormat24h: Boolean
) : BaseAdapter() {
    // array of ItemRecords
    private var data = ArrayList<ItemRecords>()
    private val context: Context

    // variables for preferences
    private val prefsBloodLowSugar: Float
    private val prefsBloodHighSugar: Float
    private val prefsTimeFormat24h: Boolean
    private val prefsUnitBloodSugarMmol: Boolean
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(num: Int): Any {
        return data[num]
    }

    override fun getItemId(arg0: Int): Long {
        return getId(arg0).toLong()
    }

    //    override fun getView(i: Int, someView: View, viewGroup: ViewGroup): View {
    override fun getView(i: Int, someView: View?, viewGroup: ViewGroup?): View {
        // get object of inflater from context
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_item_records, viewGroup, false)
//        val inflater = LayoutInflater.from(context)

        // If someView is null then load his from (with) inflater
//        if (view == null) {
//            view = inflater.inflate(R.layout.list_item_records, viewGroup, false)
//        }

        // find views on screen by id
        val tvListDate = view!!.findViewById<TextView>(R.id.tvListDate)
        val tvListTime = view.findViewById<TextView>(R.id.tvListTime)
        val tvListSugar = view.findViewById<TextView>(R.id.tvListSugar)

        // get data
        val sugar = data[i].measurementSugar

        // sugar color range
        if (sugar < prefsBloodLowSugar + 0.01f) { // low sugar
            tvListSugar.setTextColor(Color.rgb(0, 100, 255))
        } else {
            if (sugar < prefsBloodHighSugar) { // normal sugar
                tvListSugar.setTextColor(Color.DKGRAY)
            } else { // high sugar
                tvListSugar.setTextColor(Color.RED)
            }
        }

        // set date and time format
        val TIME_FORMAT: String = if (prefsTimeFormat24h) "HH:mm" else "h:mm a"
        val DATE_FORMAT = "dd/MM/yyyy"
        val sdfDate = SimpleDateFormat(DATE_FORMAT)
        val sdfTime = SimpleDateFormat(TIME_FORMAT)

        // set textViews data
        if (prefsUnitBloodSugarMmol)
            tvListSugar.text = "${data[i].measurementSugar}"
        else tvListSugar.text = "${((data[i].measurementSugar * 18).toInt())}"
        tvListDate.text = sdfDate.format(data[i].timeInMillis)
        tvListTime.text = sdfTime.format(data[i].timeInMillis)
        return view
    }

    private fun getId(position: Int): Int {
        return data[position].id
    }

    init {
        arr?.let { data = arr }

        this.context = context
        this.prefsUnitBloodSugarMmol = prefsUnitBloodSugarMmol
        this.prefsBloodLowSugar = prefsBloodLowSugar
        this.prefsBloodHighSugar = prefsBloodHighSugar
        this.prefsTimeFormat24h = prefsTimeFormat24h
    }
}