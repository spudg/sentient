package com.spudg.sentient

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.spudg.sentient.databinding.ActivityMainBinding
import com.spudg.sentient.databinding.DayMonthYearPickerBinding
import com.spudg.sentient.databinding.DialogAddRecordBinding
import com.spudg.sentient.databinding.HourMinutePickerBinding
import java.util.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private lateinit var bindingMain: ActivityMainBinding
    private lateinit var bindingAddRecord: DialogAddRecordBinding
    private lateinit var bindingDMYP: DayMonthYearPickerBinding
    private lateinit var bindingHMP: HourMinutePickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        val view = bindingMain.root
        setContentView(view)

        bindingMain.recordsBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        bindingMain.visualiserBtn.setOnClickListener {

        }

        bindingMain.aboutBtn.setOnClickListener {

        }

        bindingMain.addRecord.setOnClickListener {
            addRecord()
        }

        setUpRecordList()

    }

    private fun setUpRecordList() {
        if (getRecordList().size > 0) {
            bindingMain.rvRecords.visibility = View.VISIBLE
            bindingMain.tvNoRecords.visibility = View.GONE
            val manager = LinearLayoutManager(this)
            bindingMain.rvRecords.layoutManager = manager
            val policyAdapter = RecordAdapter(this, getRecordList())
            bindingMain.rvRecords.adapter = policyAdapter
        } else {
            bindingMain.rvRecords.visibility = View.GONE
            bindingMain.tvNoRecords.visibility = View.VISIBLE
        }
    }

    private fun getRecordList(): ArrayList<RecordModel> {
        val dbHandler = RecordHandler(this, null)
        val result = dbHandler.filterRecords(-1)
        dbHandler.close()
        return result
    }

    private fun addRecord() {
        val addDialog = Dialog(this, R.style.Theme_Dialog)
        addDialog.setCancelable(false)
        bindingAddRecord = DialogAddRecordBinding.inflate(layoutInflater)
        val view = bindingAddRecord.root
        addDialog.setContentView(view)
        addDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var dayPicked = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        var monthPicked = Calendar.getInstance()[Calendar.MONTH] + 1
        var yearPicked = Calendar.getInstance()[Calendar.YEAR]

        bindingAddRecord.dateRecordPost.text =
                "$dayPicked ${getShortMonth(monthPicked)} $yearPicked"

        bindingAddRecord.dateRecordPost.setOnClickListener {
            val changeDateDialog = Dialog(this, R.style.Theme_Dialog)
            changeDateDialog.setCancelable(false)
            bindingDMYP = DayMonthYearPickerBinding.inflate(layoutInflater)
            val view = bindingDMYP.root
            changeDateDialog.setContentView(view)
            changeDateDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            if (Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 4 || Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 6 || Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 9 || Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 11) {
                bindingDMYP.dmypDay.maxValue = 30
                bindingDMYP.dmypDay.minValue = 1
            } else if (Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 2 && (Calendar.getInstance()[Calendar.DAY_OF_MONTH] % 4 == 0)) {
                bindingDMYP.dmypDay.maxValue = 29
                bindingDMYP.dmypDay.minValue = 1
            } else if (Calendar.getInstance()[Calendar.DAY_OF_MONTH] == 2 && (Calendar.getInstance()[Calendar.DAY_OF_MONTH] % 4 != 0)) {
                bindingDMYP.dmypDay.maxValue = 28
                bindingDMYP.dmypDay.minValue = 1
            } else {
                bindingDMYP.dmypDay.maxValue = 31
                bindingDMYP.dmypDay.minValue = 1
            }

            bindingDMYP.dmypMonth.maxValue = 12
            bindingDMYP.dmypMonth.minValue = 1
            bindingDMYP.dmypYear.maxValue = 2999
            bindingDMYP.dmypYear.minValue = 1000

            bindingDMYP.dmypDay.value = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
            bindingDMYP.dmypMonth.value = Calendar.getInstance()[Calendar.MONTH] + 1
            bindingDMYP.dmypYear.value = Calendar.getInstance()[Calendar.YEAR]
            dayPicked = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
            monthPicked = Calendar.getInstance()[Calendar.MONTH] + 1
            yearPicked = Calendar.getInstance()[Calendar.YEAR]

            bindingDMYP.dmypMonth.displayedValues = monthsShortArray

            bindingDMYP.dmypDay.setOnValueChangedListener { _, _, newVal ->
                dayPicked = newVal
            }

            bindingDMYP.dmypMonth.setOnValueChangedListener { _, _, newVal ->
                if (newVal == 4 || newVal == 6 || newVal == 9 || newVal == 11) {
                    bindingDMYP.dmypDay.maxValue = 30
                    bindingDMYP.dmypDay.minValue = 1
                } else if (newVal == 2 && (bindingDMYP.dmypYear.value % 4 == 0)) {
                    bindingDMYP.dmypDay.maxValue = 29
                    bindingDMYP.dmypDay.minValue = 1
                } else if (newVal == 2 && (bindingDMYP.dmypYear.value % 4 != 0)) {
                    bindingDMYP.dmypDay.maxValue = 28
                    bindingDMYP.dmypDay.minValue = 1
                } else {
                    bindingDMYP.dmypDay.maxValue = 31
                    bindingDMYP.dmypDay.minValue = 1
                }
                monthPicked = newVal
            }

            bindingDMYP.dmypYear.setOnValueChangedListener { _, _, newVal ->
                if (newVal % 4 == 0 && bindingDMYP.dmypMonth.value == 2) {
                    bindingDMYP.dmypDay.maxValue = 29
                    bindingDMYP.dmypDay.minValue = 1
                } else if (newVal % 4 != 0 && bindingDMYP.dmypMonth.value == 2) {
                    bindingDMYP.dmypDay.maxValue = 28
                    bindingDMYP.dmypDay.minValue = 1
                }
                yearPicked = newVal
            }

            bindingDMYP.submitDmy.setOnClickListener {
                bindingAddRecord.dateRecordPost.text =
                        "$dayPicked ${getShortMonth(monthPicked)} $yearPicked"
                changeDateDialog.dismiss()
            }

            bindingDMYP.dmypDay.wrapSelectorWheel = true
            bindingDMYP.dmypMonth.wrapSelectorWheel = true
            bindingDMYP.dmypYear.wrapSelectorWheel = true

            bindingDMYP.cancelDmy.setOnClickListener {
                dayPicked = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
                monthPicked = Calendar.getInstance()[Calendar.MONTH] + 1
                yearPicked = Calendar.getInstance()[Calendar.YEAR]
                bindingAddRecord.dateRecordPost.text =
                        "$dayPicked ${getShortMonth(monthPicked)} $yearPicked"
                changeDateDialog.dismiss()
            }

            changeDateDialog.show()

        }

        var hourPicked = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
        var minutePicked = Calendar.getInstance()[Calendar.MINUTE]

        bindingAddRecord.timeRecordPost.text =
                "$hourPicked:$minutePicked"

        bindingAddRecord.timeRecordPost.setOnClickListener {
            val changeTimeDialog = Dialog(this, R.style.Theme_Dialog)
            changeTimeDialog.setCancelable(false)
            bindingHMP = HourMinutePickerBinding.inflate(layoutInflater)
            val view = bindingHMP.root
            changeTimeDialog.setContentView(view)
            changeTimeDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            bindingHMP.dmypHour.maxValue = 23
            bindingHMP.dmypHour.minValue = 1
            bindingHMP.dmypMinute.maxValue = 59
            bindingHMP.dmypMinute.minValue = 1

            bindingHMP.dmypHour.value = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
            bindingHMP.dmypMinute.value = Calendar.getInstance()[Calendar.MINUTE]
            hourPicked = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
            minutePicked = Calendar.getInstance()[Calendar.MINUTE]

            bindingHMP.dmypHour.setOnValueChangedListener { _, _, newVal ->
                hourPicked = newVal
            }

            bindingHMP.dmypMinute.setOnValueChangedListener { _, _, newVal ->
                minutePicked = newVal
            }

            bindingHMP.submitHm.setOnClickListener {
                bindingAddRecord.timeRecordPost.text =
                        "$hourPicked:$minutePicked"
                changeTimeDialog.dismiss()
            }

            bindingHMP.dmypHour.wrapSelectorWheel = true
            bindingHMP.dmypMinute.wrapSelectorWheel = true

            bindingHMP.cancelHm.setOnClickListener {
                hourPicked = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
                minutePicked = Calendar.getInstance()[Calendar.MINUTE]
                bindingAddRecord.timeRecordPost.text =
                        "$hourPicked:$minutePicked"
                changeTimeDialog.dismiss()
            }

            changeTimeDialog.show()

        }

        bindingAddRecord.scoreSlider.value = 50F
        bindingAddRecord.currentScore.text = bindingAddRecord.scoreSlider.value.roundToInt().toString()
        bindingAddRecord.currentScore.setTextColor(-16728577)

        bindingAddRecord.scoreSlider.addOnChangeListener { slider, value, fromUser ->
            slider.value = value.roundToInt().toFloat()
            bindingAddRecord.currentScore.text = value.roundToInt().toString()
            when (value) {
                in 0F..9F -> {
                    bindingAddRecord.currentScore.setTextColor(-65527)
                    slider.thumbTintList
                }
                in 10F..39F -> {
                    bindingAddRecord.currentScore.setTextColor(-25088)
                }
                in 40F..69F -> {
                    bindingAddRecord.currentScore.setTextColor(-16728577)
                }
                in 70F..89F -> {
                    bindingAddRecord.currentScore.setTextColor(-16711896)
                }
                in 90F..100F -> {
                    bindingAddRecord.currentScore.setTextColor(-6881025)
                }
            }
        }

        bindingAddRecord.tvPostRecord.setOnClickListener {

            val dbHandlerRecord = RecordHandler(this, null)

            val calendar = Calendar.getInstance()
            calendar.set(yearPicked,monthPicked-1,dayPicked,hourPicked,minutePicked)

            val score = bindingAddRecord.scoreSlider.value.toInt()
            val time = calendar.timeInMillis.toString()
            val note = bindingAddRecord.etNotePostRecord.text.toString()

            if (score.toString().isNotEmpty() && time.isNotEmpty()) {
                dbHandlerRecord.addRecord(
                        RecordModel(
                                0,
                                score,
                                time,
                                note,
                        )
                )

                Toast.makeText(this, "Record posted.", Toast.LENGTH_LONG).show()
                setUpRecordList()
                addDialog.dismiss()

            } else {
                Toast.makeText(this, "Mood can't be blank.", Toast.LENGTH_LONG)
                        .show()
            }

            dbHandlerRecord.close()

        }
        addDialog.show()

        bindingAddRecord.tvCancelPostRecord.setOnClickListener {
            addDialog.dismiss()
        }
    }

    private fun getShortMonth(month: Int): String {
        return when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> "Error"
        }
    }

    private var monthsShortArray: Array<String> = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
    )


}