package ru.opalevapps.EveryGlic.ui

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.opalevapps.EveryGlic.MyUtill
import ru.opalevapps.EveryGlic.R
import java.util.*

class HelpfullInfoActivity : AppCompatActivity() {
    private lateinit var spinnerData: ArrayList<String>

    // views
    private lateinit var spinnerInfo: Spinner

    // field to output info
    private lateinit var tvInfo: TextView

    //var for db work
    private val myDB: SQLiteDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        initViews()

        // get current locale
        val curLocale = resources.configuration.locale
        val isLocaleRu = curLocale.language == Locale("ru").language
        // if locale is not russian then show an appropriate message
        if (!isLocaleRu) {
            MyUtill.showDialog(
                this, getString(R.string.attention), getString(R.string.help_is_only_in_russian)
                        + getString(R.string.languageNotSupport)
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        MyUtill.createInfoItemInActionBar(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtill.parseMenuItemInfo(this, item)
        return super.onOptionsItemSelected(item)
    }

    // initialize views on screen and their listening
    private fun initViews() {
        //TODO in the future to make loading data from DB
        spinnerData = ArrayList()
        spinnerData.add(getString(R.string.UsefulTips))
        spinnerData.add(getString(R.string.EmergencyConditions))
        spinnerData.add(getString(R.string.ChronicComplications))
        spinnerData.add(getString(R.string.UrgentMedicalAssistance))

        tvInfo = findViewById(R.id.tvInfo)

        //TODO correct this place =)
        // adapter
        spinnerInfo = findViewById(R.id.spinnerInfo)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerData)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInfo.adapter = spinnerAdapter
        //        spinnerAdapter.notifyDataSetChanged();
        val itemSelectedListener: OnItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // Получаем выбранный объект
                val item = parent.getItemAtPosition(position) as String
                tvInfo.text = item
                when (position) {
                    0 -> tvInfo.setText(R.string._InfoUsefulTips)
                    1 -> tvInfo.setText(R.string._InfoEmergencyConditions)
                    2 -> tvInfo.setText(R.string._InfoChronicComplications)
                    3 -> tvInfo.setText(R.string._InfoUrgentMedicalAssistance)
                    else -> {}
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerInfo.onItemSelectedListener = itemSelectedListener
    }

    companion object {
        // const
        private const val DB_NAME = "info.db"
        private const val TAG = "myLog"
    }
}