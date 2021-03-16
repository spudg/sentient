package com.spudg.sentient

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RecordHandler(context: Context, factory: SQLiteDatabase.CursorFactory?) :
        SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "SentientRecords.db"
        private const val TABLE_RECORDS = "records"

        private const val KEY_ID = "_id"
        private const val KEY_SCORE = "score"
        private const val KEY_TIME = "time"
        private const val KEY_NOTE = "note"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val createRecordTable =
                ("CREATE TABLE $TABLE_RECORDS($KEY_ID INTEGER PRIMARY KEY,$KEY_SCORE INTEGER,$KEY_TIME TEXT,$KEY_NOTE TEXT)")
        db?.execSQL(createRecordTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECORDS")
        onCreate(db)
    }

    fun drop() {
        val db = this.writableDatabase
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECORDS")
    }

    fun filterRecords(sortBy: Int = 0): ArrayList<RecordModelOld> {
        val list = ArrayList<RecordModelOld>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
                "SELECT * FROM $TABLE_RECORDS",
                null
        )

        var id: Int
        var score: Int
        var time: String
        var note: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                score = cursor.getInt(cursor.getColumnIndex(KEY_SCORE))
                time = cursor.getString(cursor.getColumnIndex(KEY_TIME))
                note = cursor.getString(cursor.getColumnIndex(KEY_NOTE))
                val record = RecordModelOld(
                        id = id,
                        score = score,
                        time = time,
                        note = note,
                )
                list.add(record)
            } while (cursor.moveToNext())
        }

        if (sortBy == -1) {
            list.sortByDescending {
                it.time
            }
        }

        if (sortBy == 1) {
            list.sortBy {
                it.time
            }
        }

        cursor.close()
        db.close()

        return list

    }


}