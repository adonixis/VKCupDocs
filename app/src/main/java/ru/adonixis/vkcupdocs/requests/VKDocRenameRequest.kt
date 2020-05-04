package ru.adonixis.vkcupdocs.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class VKDocRenameRequest(ownerId: Int, docId: Int, title: String): VKRequest<Int>("docs.edit")  {
    init {
        addParam("owner_id", ownerId)
        addParam("doc_id", docId)
        addParam("title", title)
    }

    override fun parse(r: JSONObject): Int {
        return r.getInt("response")
    }
}