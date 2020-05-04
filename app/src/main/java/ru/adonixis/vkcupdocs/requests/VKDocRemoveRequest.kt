package ru.adonixis.vkcupdocs.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class VKDocRemoveRequest(ownerId: Int, docId: Int): VKRequest<Int>("docs.delete")  {
    init {
        addParam("owner_id", ownerId)
        addParam("doc_id", docId)
    }

    override fun parse(r: JSONObject): Int {
        return r.getInt("response")
    }
}