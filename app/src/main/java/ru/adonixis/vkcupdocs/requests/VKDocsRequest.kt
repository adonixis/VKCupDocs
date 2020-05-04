package ru.adonixis.vkcupdocs.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.adonixis.vkcupdocs.models.VKDoc
import java.util.ArrayList

class VKDocsRequest(offset: Int = 0): VKRequest<List<VKDoc>>("docs.get")  {
    init {
        if (offset != 0) {
            addParam("offset", offset)
        }
        addParam("count", 20)
        addParam("return_tags", 1)
    }

    override fun parse(r: JSONObject): List<VKDoc> {
        val docs = r.getJSONObject("response").getJSONArray("items")
        val result = ArrayList<VKDoc>()
        for (i in 0 until docs.length()) {
            result.add(VKDoc.parse(docs.getJSONObject(i)))
        }
        return result
    }
}