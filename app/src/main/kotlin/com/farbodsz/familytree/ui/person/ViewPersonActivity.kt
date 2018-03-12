package com.farbodsz.familytree.ui.person

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.farbodsz.familytree.IOUtils
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.ChildrenManager
import com.farbodsz.familytree.database.manager.MarriagesManager
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.ui.marriage.MarriageAdapter
import com.farbodsz.familytree.ui.tree.TreeActivity
import com.farbodsz.familytree.util.DATE_FORMATTER_BIRTH
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Activity for displaying the details of a person.
 *
 * This activity is not responsible for editing a person: for such cases, [EditPersonActivity]
 * should be started for result (it will return the new/updated object to the calling activity).
 *
 * While this activity does not deal with editing a person, it does provide the option to edit the
 * person being displayed by starting [EditPersonActivity] for result. Consequently, **this activity
 * should be started for result, because it could return an updated person object to the calling
 * activity.**
 */
class ViewPersonActivity : AppCompatActivity() {

    companion object {

        private const val LOG_TAG = "ViewPersonActivity"

        /**
         * Request code for starting [EditPersonActivity] for result.
         */
        private const val REQUEST_PERSON_EDIT = 3

        /**
         * Request code for starting [TreeActivity] for result.
         */
        private const val REQUEST_VIEW_TREE = 9

        /**
         * Intent extra key for supplying a [Person] to this activity.
         */
        const val EXTRA_PERSON = "extra_person"
    }

    /**
     * The [Person] received via intent extra from the previous activity.
     *
     * It cannot be null. To create a new [Person], [CreatePersonActivity] should be used, since
     * this activity is only for displaying existing [Person]s.
     */
    private lateinit var person: Person

    private var hasBeenModified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_person)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        person = intent.extras?.getParcelable(EXTRA_PERSON)
                ?: // received Person could be null, throw exception if so
                throw IllegalArgumentException("ViewPersonActivity cannot display a null person")

        setupLayout()
    }

    private fun setupLayout() {
        val personImage = IOUtils.readPersonImage(person, applicationContext)
        val circleImageView = findViewById<CircleImageView>(R.id.circleImageView)
        circleImageView.borderColor = ContextCompat.getColor(this, person.gender.getColorRes())
        circleImageView.setImageDrawable(personImage)

        findViewById<TextView>(R.id.text_name).text = person.fullName

        val genderText = findViewById<TextView>(R.id.text_gender)
        if (person.gender.isMale()) {
            genderText.setText(R.string.male)
            genderText.setTextColor(ContextCompat.getColor(this, R.color.image_border_male))
        } else {
            genderText.setText(R.string.female)
            genderText.setTextColor(ContextCompat.getColor(this, R.color.image_border_female))
        }

        findViewById<TextView>(R.id.text_birth).text =
                person.dateOfBirth.format(DATE_FORMATTER_BIRTH)

        val deathInfo = findViewById<LinearLayout>(R.id.group_deathInfo)
        if (person.isAlive()) {
            deathInfo.visibility = View.GONE
        } else {
            deathInfo.visibility = View.VISIBLE
            findViewById<TextView>(R.id.text_death).text =
                    person.dateOfDeath!!.format(DATE_FORMATTER_BIRTH)
        }

        setupParentsList()
        setupMarriagesList()
        setupChildrenList()

        val button = findViewById<Button>(R.id.button_viewTree)
        button.text = getString(R.string.view_tree, person.forename)
        button.setOnClickListener {
            val treeRoot = ChildrenManager(this).getRootParent(person.id)
            val intent = Intent(this, TreeActivity::class.java)
                    .putExtra(TreeActivity.EXTRA_PERSON, treeRoot)
                    .putExtra(TreeActivity.EXTRA_NAME, person.forename)
            startActivityForResult(intent, REQUEST_VIEW_TREE)
        }
    }

    private fun setupParentsList() {
        val parents = ChildrenManager(this).getParents(person.id)

        if (parents.isEmpty()) {
            findViewById<LinearLayout>(R.id.group_parents).visibility = View.GONE
            with(findViewById<TextView>(R.id.text_noParents)) {
                visibility = View.VISIBLE
                text = getString(R.string.no_parents, person.forename)
            }
            return
        }

        val personAdapter = PersonAdapter(this, parents)
        findViewById<RecyclerView>(R.id.recyclerView_parents).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = personAdapter
        }
    }

    private fun setupMarriagesList() {
        val marriages = MarriagesManager(this).getMarriages(person.id)

        val marriagesInfo = findViewById<LinearLayout>(R.id.group_marriages)
        val noMarriagesText = findViewById<TextView>(R.id.text_noMarriages)

        if (marriages.isEmpty()) {
            marriagesInfo.visibility = View.GONE
            with(noMarriagesText) {
                visibility = View.VISIBLE
                text = getString(R.string.no_marriages, person.forename)
            }
            return
        }

        marriagesInfo.visibility = View.VISIBLE
        noMarriagesText.visibility = View.GONE

        val personAdapter = MarriageAdapter(this, person.id, marriages)
        findViewById<RecyclerView>(R.id.recyclerView_marriages).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = personAdapter
        }
    }

    private fun setupChildrenList() {
        val children = ChildrenManager(this).getChildren(person.id)

        if (children.isEmpty()) {
            findViewById<LinearLayout>(R.id.group_children).visibility = View.GONE
            with(findViewById<TextView>(R.id.text_noChildren)) {
                visibility = View.VISIBLE
                text = getString(R.string.no_children, person.forename)
            }
            return
        }

        val personAdapter = PersonAdapter(this, children)
        findViewById<RecyclerView>(R.id.recyclerView_children).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = personAdapter
        }
    }

    /**
     * Sends the correct result back to where this activity was invoked from, and finishes the
     * activity.
     *
     * An "ok" result will be used if the [person] has been modified, otherwise a "cancelled" result.
     * This function is not concerned with "successful" results that indicate the deletion of a
     * [person]. Those are handled in [showDeleteDialog].
     *
     * @see android.app.Activity.RESULT_OK
     * @see android.app.Activity.RESULT_CANCELED
     */
    private fun sendResult() {
        if (hasBeenModified) {
            Log.d(LOG_TAG, "Sending successful result: $person")
            val returnIntent = Intent().putExtra(EXTRA_PERSON, person)
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
                val intent = Intent(this, EditPersonActivity::class.java)
                        .putExtra(EditPersonActivity.EXTRA_PERSON, person)
                startActivityForResult(intent, REQUEST_PERSON_EDIT)
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
            REQUEST_PERSON_EDIT -> if (resultCode == Activity.RESULT_OK) {
                hasBeenModified = true
                person = data!!.getParcelableExtra(EditPersonActivity.EXTRA_PERSON)
                setupLayout() // update UI
            }
            REQUEST_VIEW_TREE -> if (resultCode == Activity.RESULT_OK) {
                hasBeenModified = true
                setupLayout()
            }
        }
    }

    /**
     * Displays a confirmation dialog for deleting this [person], with positive and negative
     * actions.
     */
    private fun showDeleteDialog() {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
                .setTitle(R.string.dialog_confirm_delete_title)
                .setMessage(R.string.dialog_confirm_delete_person_message)
                .setPositiveButton(R.string.action_delete) { _, _ ->
                    PersonManager(this).deleteWithReferences(person.id)

                    Log.d(LOG_TAG, "Sending successful result: person has been deleted")
                    val returnIntent = Intent() // no "extra" to indicate deletion
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> dialog.dismiss() }
        dialog = builder.show()
    }

}
