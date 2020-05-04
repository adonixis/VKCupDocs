package ru.adonixis.vkcupdocs.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_doc.view.*
import ru.adonixis.vkcupdocs.R
import ru.adonixis.vkcupdocs.models.VKDoc
import ru.adonixis.vkcupdocs.util.OnItemClickListener
import ru.adonixis.vkcupdocs.util.Utils.formatFileSize
import java.text.SimpleDateFormat
import java.util.*


class DocsAdapter(
    private val docs : ArrayList<VKDoc>,
    private val context: Context,
    private val itemClickListener: OnItemClickListener,
    private val renameClickListener: OnItemClickListener,
    private val removeClickListener: OnItemClickListener
) : RecyclerView.Adapter<DocViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        return DocViewHolder(LayoutInflater.from(context).inflate(R.layout.item_doc, parent, false))
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        val doc = docs[position]
        val fileTitle = doc.title
        val fileExt = doc.ext
        val fileSize = doc.size
        val fileDate = doc.date
        val fileType = doc.type
        val fileTags = doc.tags
        val filePreview = doc.preview

        val croppedTitle: String
        if (fileTitle.endsWith(fileExt)) {
            croppedTitle = fileTitle.substring(0, fileTitle.length - fileExt.length - 1)
        } else {
            croppedTitle = fileTitle
        }
        holder.tvFileTitle.text = croppedTitle
        val formattedFileSize = formatFileSize(fileSize.toLong())
        val date = Date(fileDate.toLong() * 1000)
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dateFormat: SimpleDateFormat
        val formattedDate: String
        val calendarYesterday = Calendar.getInstance()
        calendarYesterday.add(Calendar.DAY_OF_YEAR, -1)
        if (calendar[Calendar.YEAR] == Calendar.getInstance().get(Calendar.YEAR)) {
            if (calendar[Calendar.MONTH] == Calendar.getInstance().get(Calendar.MONTH) &&
                calendar[Calendar.DAY_OF_MONTH] == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                formattedDate = context.getString(R.string.title_today)
            } else if (calendarYesterday.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                formattedDate = context.getString(R.string.title_yesterday)
            } else {
                dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
                formattedDate = dateFormat.format(date)
            }
        } else {
            dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
            formattedDate = dateFormat.format(date)
        }
        val fileInfo: String
        if (fileExt.isNotEmpty()) {
            fileInfo = "${fileExt.toUpperCase()} · $formattedFileSize · $formattedDate"
        } else {
            fileInfo = "$formattedFileSize · $formattedDate"
        }
        holder.tvFileInfo.text = fileInfo

        if (fileTags == null || fileTags.isEmpty()) {
            holder.tvFileTitle.maxLines = 2
            holder.icTag.visibility = View.GONE
            holder.tvFileTag.visibility = View.GONE
        } else {
            holder.tvFileTitle.maxLines = 1
            holder.icTag.visibility = View.VISIBLE
            holder.tvFileTag.visibility = View.VISIBLE
            holder.tvFileTag.text = fileTags.joinToString()
        }

        val fileIcon = when (fileType) {
            1 -> R.drawable.ic_placeholder_document_text_72
            2 -> R.drawable.ic_placeholder_document_archive_72
            3 -> R.drawable.ic_placeholder_document_image_72
            4 -> R.drawable.ic_placeholder_document_image_72
            5 -> R.drawable.ic_placeholder_document_music_72
            6 -> R.drawable.ic_placeholder_document_video_72
            7 -> R.drawable.ic_placeholder_document_book_72
            else -> R.drawable.ic_placeholder_document_other_72
        }
        Glide
            .with(context)
            .load(fileIcon)
            .placeholder(R.drawable.ic_placeholder_document_other_72)
            .into(holder.icDoc)

        if (filePreview?.photo?.sizes != null) {
            for (size: VKDoc.VKPreview.VKPhoto.VKSize in filePreview.photo.sizes) {
                if (size.type == "m") {
                    Glide
                        .with(context)
                        .load(size.src)
                        .centerCrop()
                        .placeholder(R.drawable.ic_placeholder_document_image_72)
                        .into(holder.icDoc)
                }
            }
        }

        holder.itemView.setOnClickListener{ itemClickListener.onItemClick(holder.itemView, position) }

        holder.icMore.setOnClickListener { showPopupMenu(holder.icMore, position) }
    }

    override fun getItemCount(): Int {
        return docs.size
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu
            .setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_rename -> {
                        renameClickListener.onItemClick(view, position)
                        true
                    }
                    R.id.action_remove -> {
                        removeClickListener.onItemClick(view, position)
                        true
                    }
                    else -> false
                }
            }
        popupMenu.show()
    }
}

class DocViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val icDoc = view.icDoc!!
    val tvFileTitle = view.tvFileTitle!!
    val tvFileInfo = view.tvFileInfo!!
    val tvFileTag = view.tvFileTag!!
    val icMore = view.icMore!!
    val icTag = view.icTag!!
}