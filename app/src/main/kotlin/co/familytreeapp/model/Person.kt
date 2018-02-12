package co.familytreeapp.model

import android.os.Parcel
import android.os.Parcelable
import org.threeten.bp.LocalDate

/**
 * Represents a member of the family.
 *
 * @param id            a unique identifier of the person
 * @param forename      first name / forename. This cannot be blank.
 * @param surname       last name / surname. This cannot be blank.
 * @param gender        male or female
 * @param dateOfBirth   the date when the person was born
 * @param placeOfBirth  the place where the person was born. This is optional and can be left blank.
 * @param dateOfDeath   the date when the person died. If the person is currently alive, this should
 *                      be null.
 * @param placeOfDeath  the place where the person died. This is optional and can be left blank (for
 *                      example, if the person is currently alive).
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
) : Parcelable {

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

    val marriages: List<Marriage>
        get() {
            TODO()
        }

    fun isAlive() = dateOfDeath == null

    override fun toString() = "$id: $fullName"

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

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Person> = object : Parcelable.Creator<Person> {
            override fun createFromParcel(source: Parcel): Person = Person(source)
            override fun newArray(size: Int): Array<Person?> = arrayOfNulls(size)
        }
    }
}

/**
 * Represents a gender.
 *
 * @param id    an integer identifier corresponding to a gender. 0 = male; 1 = female.
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

        @JvmField
        val CREATOR: Parcelable.Creator<Gender> = object : Parcelable.Creator<Gender> {
            override fun createFromParcel(source: Parcel): Gender = Gender(source)
            override fun newArray(size: Int): Array<Gender?> = arrayOfNulls(size)
        }
    }
}
