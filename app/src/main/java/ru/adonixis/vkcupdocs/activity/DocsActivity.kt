package ru.adonixis.vkcupdocs.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vk.api.sdk.VK
import kotlinx.android.synthetic.main.activity_docs.*
import ru.adonixis.vkcupdocs.R
import ru.adonixis.vkcupdocs.adapter.DocsAdapter
import ru.adonixis.vkcupdocs.models.VKDoc
import ru.adonixis.vkcupdocs.util.EndlessRecyclerViewScrollListener
import ru.adonixis.vkcupdocs.util.OnItemClickListener
import ru.adonixis.vkcupdocs.util.RecyclerItemDecoration
import ru.adonixis.vkcupdocs.util.Utils.showSnackbar
import ru.adonixis.vkcupdocs.viewmodel.DocsViewModel

class DocsActivity: AppCompatActivity() {

    private lateinit var viewModel: DocsViewModel
    private val docs: ArrayList<VKDoc> = ArrayList()
    private lateinit var docsAdapter: DocsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var currentDoc: VKDoc = VKDoc()
    private var currentPosition: Int = 0
    private var newTitle: String = ""
    private lateinit var alertDialog: AlertDialog

    private val onItemClickListener = object : OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            val doc = docs[position]
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this@DocsActivity, Uri.parse(doc.url))
        }
    }
    private val onRenameClickListener = object : OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            currentPosition = position
            currentDoc = docs[position]
            showRenameDocDialog(currentDoc)
        }
    }

    private val onRemoveClickListener = object : OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            currentPosition = position
            deleteSelectedDoc(docs[position])
        }
    }

    private fun deleteSelectedDoc(doc: VKDoc) {
        viewModel.removeDoc(doc.ownerId, doc.id)
    }

    private fun showRenameDocDialog(doc: VKDoc) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.action_rename)
            .setView(R.layout.dialog_rename_file)
            .setCancelable(true)
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .setPositiveButton(getString(R.string.btn_save) , null)
        alertDialog = builder.create()
        alertDialog.setOnShowListener {
            val button: Button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val etNewTitle = alertDialog.findViewById<EditText>(R.id.etNewTitle)
            etNewTitle.setText(currentDoc.title)
            button.setOnClickListener {
                if (etNewTitle.text.isNotEmpty()) {
                    newTitle = etNewTitle.text.toString()
                    viewModel.renameDoc(doc.ownerId, doc.id, newTitle)
                    alertDialog.dismiss()
                }
            }
        }
        alertDialog.show()
    }

    companion object {
        private const val TAG = "DocsActivity"
        private const val DEFAULT_ITEMS_COUNT = 20

        fun startFrom(context: Context) {
            val intent = Intent(context, DocsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docs)

        viewModel = ViewModelProvider(this@DocsActivity).get(DocsViewModel::class.java)
        viewModel.getDocsLiveData().observe(this, Observer {
            swipeRefresh.isRefreshing = false
            docs.addAll(it)
            docsAdapter.notifyDataSetChanged()
        })
        viewModel.getRemoveDocLiveData().observe(this, Observer {
            docsAdapter.notifyItemRemoved(currentPosition)
        })
        viewModel.getRenameDocLiveData().observe(this, Observer {
            docs[currentPosition].title = newTitle
            docsAdapter.notifyItemChanged(currentPosition)
        })
        viewModel.getErrorMessageLiveData().observe(this, Observer {
            swipeRefresh.isRefreshing = false
            showErrorMessage(it)
        })

        linearLayoutManager = LinearLayoutManager(this)
        recyclerDocs.layoutManager = linearLayoutManager
        recyclerDocs.setHasFixedSize(true)
        docsAdapter = DocsAdapter(docs, this, onItemClickListener, onRenameClickListener, onRemoveClickListener)
        recyclerDocs.adapter = docsAdapter
        val recyclerItemDecoration = RecyclerItemDecoration(this)
        recyclerDocs.addItemDecoration(recyclerItemDecoration)
        recyclerDocs.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(linearLayoutManager, DEFAULT_ITEMS_COUNT) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.getDocs(page * DEFAULT_ITEMS_COUNT)
            }
        })

        refreshDocs()

        swipeRefresh.setOnRefreshListener { refreshDocs() }
    }

    private fun refreshDocs() {
        swipeRefresh.isRefreshing = true
        docs.clear()
        docsAdapter.notifyDataSetChanged()
        viewModel.getDocs()
    }

    private fun showErrorMessage(errorMessage: String) {
        showSnackbar(
            layoutRoot, Snackbar.Callback(),
            ContextCompat.getColor(this, R.color.red),
            Color.WHITE,
            errorMessage,
            Color.WHITE,
            getString(R.string.snackbar_action_hide), null
        )
    }

    private fun showSuccessMessage(successMessage: String) {
        showSnackbar(
            layoutRoot, Snackbar.Callback(),
            ContextCompat.getColor(this, R.color.green),
            Color.WHITE,
            successMessage,
            Color.WHITE,
            getString(R.string.snackbar_action_hide), null
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        VK.logout()
        WelcomeActivity.startFrom(this)
        finish()
    }
}