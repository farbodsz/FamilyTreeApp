package com.farbodsz.familytree.ui.marriage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.MarriagesManager
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Marriage
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.util.DATE_FORMATTER_BIRTH
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Activity for displaying the details of a marriage.
 *
 * This activity is not responsible for editing a marriage: for such cases, [EditMarriageActivity]
 * should be started for result (it will return the new/updated object to the calling activity).
 *
 * While this activity does not deal with editing a marriage, it does provide the option to edit the
 * person being displayed by starting [EditMarriageActivity] for result. Consequently, **this
 * activity should be started for result, because it could return an updated person object to the
 * calling activity.**
 */
class ViewMarriageActivity : AppCompatActivity() {

    companion object {

        private const val LOG_TAG = "ViewMarriageActivity"

        /**
         * Request code for starting [EditMarriageActivity] for result.
         */
        private const val REQUEST_MARRIAGE_EDIT = 3

        /**
         * Intent extra key for supplying a [Marriage] to this activity.
         */
        const val EXTRA_MARRIAGE = "extra_marriage"
    }

    /**
     * The [Marriage] received via intent extra from the previous activity.
     *
     * It cannot be null. To create a new [Marriage], [EditMarriageActivity] should be used, since
     * this activity is only for displaying existing [Marriage]s.
     */
    private lateinit var marriage: Marriage

    private var hasBeenModified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_marriage)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        marriage = intent.extras?.getParcelable(EXTRA_MARRIAGE)
                ?: // received Person could be null, throw exception if so
                throw IllegalArgumentException("ViewMarriageActivity can't display a null marriage")

        setupLayout()
    }

    private fun setupLayout() {
        val pm = PersonManager(this)
        val person1 = pm.get(marriage.person1Id)
        val person2 = pm.get(marriage.person2Id)

        setupPersonLayout(person1, findViewById(R.id.container_1))
        setupPersonLayout(person2, findViewById(R.id.container_2))

        findViewById<TextView>(R.id.text_married).text =
                marriage.startDate.format(DATE_FORMATTER_BIRTH)

        val divorceInfo = findViewById<LinearLayout>(R.id.group_divorceInfo)
        if (marriage.isOngoing()) {
            divorceInfo.visibility = View.GONE
        } else {
            divorceInfo.visibility = View.VISIBLE
            findViewById<TextView>(R.id.text_divorced).text =
                    marriage.endDate!!.format(DATE_FORMATTER_BIRTH)
        }
    }

    /**
     * Adds the layout for a person item.
     *
     * @param person    the [Person] to use for displaying the layout
     * @param container the [ViewGroup] in which the layout item will be added
     */
    private fun setupPersonLayout(person: Person, container: ViewGroup) {
        val item = layoutInflater.inflate(R.layout.item_list_person, container, false)

        item.findViewById<CircleImageView>(R.id.circleImageView).borderColor =
                ContextCompat.getColor(this, person.gender.getColorRes())
        item.findViewById<TextView>(R.id.text1).text = person.fullName
        item.findViewById<TextView>(R.id.text2).text =
                person.dateOfBirth.format(DATE_FORMATTER_BIRTH)

        container.addView(item)
    }

    /**
     * Sends the correct result back to where this activity was invoked from, and finishes the
     * activity.
     *
     * An "ok" result will be used if the [marriage] has been modified, otherwise a "cancelled"
     * result. This function is not concerned with "successful" results that indicate the deletion
     * of a [marriage]. Those are handled in [showDeleteDialog].
     *
     * @see android.app.Activity.RESULT_OK
     * @see android.app.Activity.RESULT_CANCELED
     */
    private fun sendResult() {
        if (hasBeenModified) {
            Log.d(LOG_TAG, "Sending successful result: $marriage")
            val returnIntent = Intent().putExtra(EXTRA_MARRIAGE, marriage)
            setResult(Activity.RESULT_OK, returnIntent)
        } else {
            Log.d(LOG_TAG, "Sending cancelled result")
            setResult(Activity.RESULT_CANCELED)
        }
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_view_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_edit -> {
                val intent = Intent(this, EditMarriageActivity::class.java)
                        .putExtra(EditMarriageActivity.EXTRA_MARRIAGE, marriage)
                startActivityForResult(intent, REQUEST_MARRIAGE_EDIT)
            }
            R.id.action_delete -> {
                showDeleteDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = sendResult()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_MARRIAGE_EDIT -> if (resultCode == Activity.RESULT_OK) {
                hasBeenModified = true
                marriage = data!!.getParcelableExtra(EditMarriageActivity.EXTRA_MARRIAGE)
                setupLayout() // update UI
            }
        }
    }

    /**
     * Displays a confirmation dialog for deleting this [marriage], with positive and negative
     * actions.
     */
    private fun showDeleteDialog() {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
                .setTitle(R.string.dialog_confirm_delete_title)
                .setMessage(R.string.dialog_confirm_delete_marriage_message)
                .setPositiveButton(R.string.action_delete) { _, _ ->
                    MarriagesManager(this).deleteWithReferences(marriage.getIds())

                    Log.d(LOG_TAG, "Sending successful result: marriage has been deleted")
                    val returnIntent = Intent() // no "extra" to indicate deletion
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> dialog.dismiss() }
        dialog = builder.show()
    }

}
