package com.farbodsz.familytree.model

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import com.farbodsz.familytree.database.schemas.MarriagesSchema
import org.threeten.bp.LocalDate

/**
 * Represents a marriage between two people.
 *
 * @param person1Id         the id of a person in this marriage
 * @param person2Id         the id of another person in this marriage
 * @param startDate         the date of marriage
 * @param endDate           the date when the marriage ended. If the marriage has not ended, this
 *                          should be null.
 * @param placeOfMarriage   the name of the place where the marriage took place. This is optional
 *                          and can be left blank.
 */
data class Marriage(
        val person1Id: Int,
        val person2Id: Int,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val placeOfMarriage: String
) : DataRelationship, Parcelable {

    init {
        require(person1Id > 0) { "person1Id < 1: the id of a person must be greater than 0" }
        require(person2Id > 0) { "person2Id < 1: the id of a person must be greater than 0" }
        require(person1Id != person2Id) {
            "person1Id = person2Id: a person cannot be married to themselves"
        }
    }

    fun isOngoing() = endDate == null

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readInt(),
            source.readSerializable() as LocalDate,
            source.readSerializable() as LocalDate?,
            source.readString()
    )

    override fun getIds() = Pair(person1Id, person2Id)

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(person1Id)
        writeInt(person2Id)
        writeSerializable(startDate)
        writeSerializable(endDate)
        writeString(placeOfMarriage)
    }

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<Marriage> = object : Parcelable.Creator<Marriage> {
            override fun createFromParcel(source: Parcel): Marriage = Marriage(source)
            override fun newArray(size: Int): Array<Marriage?> = arrayOfNulls(size)
        }

        /**
         * Instantiates a [Marriage] object by getting values in columns from a [cursor].
         */
        @JvmStatic
        fun from(cursor: Cursor): Marriage {
            val startDate = LocalDate.of(
                    cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_START_DATE_YEAR)),
                    cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_START_DATE_MONTH)),
                    cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_START_DATE_DAY))
            ) // TODO should marriages dates be optional?

            val endDate = if (cursor.isNull(cursor.getColumnIndex(MarriagesSchema.COL_START_DATE_DAY))) {
                null
            } else {
                LocalDate.of(
                        cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_START_DATE_YEAR)),
                        cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_START_DATE_MONTH)),
                        cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_START_DATE_DAY))
                )
            }

            return Marriage(
                    cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_ID_1)),
                    cursor.getInt(cursor.getColumnIndex(MarriagesSchema.COL_ID_2)),
                    startDate,
                    endDate,
                    cursor.getString(cursor.getColumnIndex(MarriagesSchema.COL_PLACE_OF_MARRIAGE))
            )
        }

    }
}
