package com.farbodsz.familytree.model

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import com.farbodsz.familytree.database.schemas.PersonsSchema
import org.threeten.bp.LocalDate

/**
 * Represents a member of the family.
 *
 * @property id             a unique integer identifier of the person
 * @property forename       first name / forename. This cannot be blank.
 * @property surname        last name / surname. This cannot be blank.
 * @property gender         male or female
 * @property dateOfBirth    date when the person was born
 * @property placeOfBirth   place where the person was born. This is optional and can be left blank.
 * @property dateOfDeath    date when the person died. If the person is currently alive, this should
 *                          be null.
 * @property placeOfDeath   place where the person died. This is optional and can be left blank (for
 *                          example, if the person is currently alive).
 */
data class Person(
        val id: Int,
        val forename: String,
        val surname: String,
        val gender: Gender,
        val dateOfBirth: LocalDate,
        val placeOfBirth: String,
        val dateOfDeath: LocalDate?,
        val placeOfDeath: String
) : StandardData, WithEvent, Comparable<Person>, Parcelable {

    init {
        require(id > 0) { "the id must be greater than 0" }

        require(forename.isNotBlank() && surname.isNotBlank()) {
            "the name (forename and surname) cannot be blank"
        }

        dateOfDeath?.let {
            require(!it.isBefore(dateOfBirth)) {
                "the date of death cannot be before the date of birth"
            }
        }
    }

    val fullName = "$forename $surname"

    fun isAlive() = dateOfDeath == null

    override fun getRelatedEvent() = Birthday(id, dateOfBirth, placeOfBirth)

    override fun compareTo(other: Person) = fullName.compareTo(other.fullName)

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<Person> = object : Parcelable.Creator<Person> {
            override fun createFromParcel(source: Parcel): Person = Person(source)
            override fun newArray(size: Int): Array<Person?> = arrayOfNulls(size)
        }

        /**
         * Instantiates a [Person] by getting values in columns from a [cursor].
         */
        @JvmStatic
        fun from(cursor: Cursor): Person {
            val dateOfBirth = LocalDate.of(
                    cursor.getInt(cursor.getColumnIndex(PersonsSchema.COL_BIRTH_DATE_YEAR)),
                    cursor.getInt(cursor.getColumnIndex(PersonsSchema.COL_BIRTH_DATE_MONTH)),
                    cursor.getInt(cursor.getColumnIndex(PersonsSchema.COL_BIRTH_DATE_DAY))
            )
            val dateOfDeath = if (cursor.isNull(cursor.getColumnIndex(PersonsSchema.COL_DEATH_DATE_YEAR))) {
                null
            } else {
                LocalDate.of(
                        cursor.getInt(cursor.getColumnIndex(PersonsSchema.COL_DEATH_DATE_YEAR)),
                        cursor.getInt(cursor.getColumnIndex(PersonsSchema.COL_DEATH_DATE_MONTH)),
                        cursor.getInt(cursor.getColumnIndex(PersonsSchema.COL_DEATH_DATE_DAY))
                )
            }

            return Person(
                    cursor.getInt(cursor.getColumnIndex(PersonsSchema.COL_ID)),
                    cursor.getString(cursor.getColumnIndex(PersonsSchema.COL_FORENAME)),
                    cursor.getString(cursor.getColumnIndex(PersonsSchema.COL_SURNAME)),
                    Gender(cursor.getInt(cursor.getColumnIndex(PersonsSchema.COL_GENDER_ID))),
                    dateOfBirth,
                    cursor.getString(cursor.getColumnIndex(PersonsSchema.COL_PLACE_OF_BIRTH)),
                    dateOfDeath,
                    cursor.getString(cursor.getColumnIndex(PersonsSchema.COL_PLACE_OF_DEATH))
            )
        }
    }

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readParcelable<Gender>(Gender::class.java.classLoader),
            source.readSerializable() as LocalDate,
            source.readString(),
            source.readSerializable() as LocalDate?,
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(forename)
        writeString(surname)
        writeParcelable(gender, 0)
        writeSerializable(dateOfBirth)
        writeString(placeOfBirth)
        writeSerializable(dateOfDeath)
        writeString(placeOfDeath)
    }
}

/**
 * Represents a gender.
 *
 * @property id an integer identifier corresponding to a gender. 0 = male; 1 = female.
 */
data class Gender(val id: Int) : Parcelable {

    init {
        require(id in 0..1) { "the id for a Gender must be between 0 and 1" }
    }

    fun isMale() = this == MALE

    fun isFemale() = this == FEMALE

    constructor(source: Parcel) : this(source.readInt())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
    }

    companion object {

        @JvmField val MALE = Gender(0)

        @JvmField val FEMALE = Gender(1)

        @JvmField val CREATOR: Parcelable.Creator<Gender> = object : Parcelable.Creator<Gender> {
            override fun createFromParcel(source: Parcel): Gender = Gender(source)
            override fun newArray(size: Int): Array<Gender?> = arrayOfNulls(size)
        }
    }
}
