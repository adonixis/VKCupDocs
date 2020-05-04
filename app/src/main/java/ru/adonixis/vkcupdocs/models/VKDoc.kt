package ru.adonixis.vkcupdocs.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class VKDoc(
    val id: Int = 0,
    val ownerId: Int = 0,
    var title: String = "",
    val size: Int = 0,
    val ext: String = "",
    val url: String = "",
    val date: Int = 0,
    val type: Int = 0,
    val tags: Array<String>? = null,
    val preview: VKPreview? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.createStringArray() as Array<String>,
        parcel.readParcelable<VKPreview>(VKPreview::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(ownerId)
        parcel.writeString(title)
        parcel.writeInt(size)
        parcel.writeString(ext)
        parcel.writeString(url)
        parcel.writeInt(date)
        parcel.writeInt(type)
        parcel.writeStringArray(tags)
        parcel.writeParcelable(preview, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKDoc> {
        override fun createFromParcel(parcel: Parcel): VKDoc {
            return VKDoc(parcel)
        }

        override fun newArray(size: Int): Array<VKDoc?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject): VKDoc {
            val tagsArray = json.optJSONArray("tags")
            var resultTags: Array<String>? = null
            if (tagsArray != null && tagsArray.length() != 0) {
                resultTags = Array(tagsArray.length()) { "" }
                for (i in 0 until tagsArray.length()) {
                    resultTags[i] = (tagsArray.getString(i))
                }
            }
            val previewJson = json.optJSONObject("preview")
            var parsedPreview : VKPreview? = null
            if (previewJson != null) {
                parsedPreview = VKPreview.parse(previewJson)
            }

            return VKDoc(
                id = json.optInt("id", 0),
                ownerId = json.optInt("owner_id", 0),
                title = json.optString("title", ""),
                size = json.optInt("size", 0),
                ext = json.optString("ext", ""),
                url = json.optString("url", ""),
                date = json.optInt("date", 0),
                type = json.optInt("type", 0),
                tags = resultTags,
                preview = parsedPreview
            )
        }
    }

    data class VKPreview(
        val photo: VKPhoto? = VKPhoto(),
        val graffiti: VKGraffiti? = VKGraffiti(),
        val audioMessage: VKAudioMessage? = VKAudioMessage()) : Parcelable {

        constructor(parcel: Parcel) : this(
            parcel.readParcelable<VKPhoto>(VKPhoto::class.java.classLoader),
            parcel.readParcelable<VKGraffiti>(VKGraffiti::class.java.classLoader),
            parcel.readParcelable<VKAudioMessage>(VKAudioMessage::class.java.classLoader))

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeParcelable(photo, flags)
            parcel.writeParcelable(graffiti, flags)
            parcel.writeParcelable(audioMessage, flags)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<VKPreview> {
            override fun createFromParcel(parcel: Parcel): VKPreview {
                return VKPreview(parcel)
            }

            override fun newArray(size: Int): Array<VKPreview?> {
                return arrayOfNulls(size)
            }

            fun parse(json: JSONObject): VKPreview {
                val photoJson = json.optJSONObject("photo")
                val graffitiJson = json.optJSONObject("graffiti")
                val audioJson = json.optJSONObject("audio_message")
                var parsedPhoto : VKPhoto? = null
                var parsedGraffiti : VKGraffiti? = null
                var parsedAudio : VKAudioMessage? = null
                if (photoJson != null) {
                    parsedPhoto = VKPhoto.parse(photoJson)
                }
                if (graffitiJson != null) {
                    parsedGraffiti = VKGraffiti.parse(graffitiJson)
                }
                if (audioJson != null) {
                    parsedAudio = VKAudioMessage.parse(audioJson)
                }
                return VKPreview(
                    photo = parsedPhoto,
                    graffiti = parsedGraffiti,
                    audioMessage = parsedAudio
                )
            }
        }

        data class VKPhoto(
            val sizes: Array<VKSize>? = null) : Parcelable {

            constructor(parcel: Parcel) : this(
                parcel.createStringArray() as Array<VKSize>)

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeTypedArray(sizes, flags)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<VKPhoto> {
                override fun createFromParcel(parcel: Parcel): VKPhoto {
                    return VKPhoto(parcel)
                }

                override fun newArray(size: Int): Array<VKPhoto?> {
                    return arrayOfNulls(size)
                }

                fun parse(json: JSONObject): VKPhoto {
                    val sizesArray = json.optJSONArray("sizes")
                    var result: Array<VKSize>? = null
                    if (sizesArray != null && sizesArray.length() != 0) {
                        result = Array(sizesArray.length()) { VKSize() }
                        for (i in 0 until sizesArray.length()) {
                            result[i] = (VKSize.parse(sizesArray.getJSONObject(i)))
                        }
                    }

                    return VKPhoto(
                        sizes = result
                    )
                }
            }

            data class VKSize(
                val src : String = "",
                val width : Int = 0,
                val height : Int = 0,
                val type : String = "") : Parcelable {

                constructor(parcel: Parcel) : this(
                    parcel.readString()!!,
                    parcel.readInt(),
                    parcel.readInt(),
                    parcel.readString()!!)

                override fun writeToParcel(parcel: Parcel, flags: Int) {
                    parcel.writeString(src)
                    parcel.writeInt(width)
                    parcel.writeInt(height)
                    parcel.writeString(type)
                }

                override fun describeContents(): Int {
                    return 0
                }

                companion object CREATOR : Parcelable.Creator<VKSize> {
                    override fun createFromParcel(parcel: Parcel): VKSize {
                        return VKSize(parcel)
                    }

                    override fun newArray(size: Int): Array<VKSize?> {
                        return arrayOfNulls(size)
                    }

                    fun parse(json: JSONObject) = VKSize(
                        src = json.optString("src", ""),
                        width = json.optInt("width", 0),
                        height = json.optInt("height", 0),
                        type = json.optString("type", ""))
                }
            }
        }

        data class VKGraffiti(
            val src : String = "",
            val width : Int = 0,
            val height : Int = 0) : Parcelable {

            constructor(parcel: Parcel) : this(
                parcel.readString()!!,
                parcel.readInt(),
                parcel.readInt())

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(src)
                parcel.writeInt(width)
                parcel.writeInt(height)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<VKGraffiti> {
                override fun createFromParcel(parcel: Parcel): VKGraffiti {
                    return VKGraffiti(parcel)
                }

                override fun newArray(size: Int): Array<VKGraffiti?> {
                    return arrayOfNulls(size)
                }

                fun parse(json: JSONObject) = VKGraffiti(
                    src = json.optString("src", ""),
                    width = json.optInt("width", 0),
                    height  = json.optInt("height ", 0))
            }
        }

        data class VKAudioMessage(
            val duration : Int = 0,
            val linkOgg  : String = "",
            val linkMp3  : String = "") : Parcelable {

            constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readString()!!,
                parcel.readString()!!)

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeInt(duration)
                parcel.writeString(linkOgg)
                parcel.writeString(linkMp3)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<VKAudioMessage> {
                override fun createFromParcel(parcel: Parcel): VKAudioMessage {
                    return VKAudioMessage(parcel)
                }

                override fun newArray(size: Int): Array<VKAudioMessage?> {
                    return arrayOfNulls(size)
                }

                fun parse(json: JSONObject) = VKAudioMessage(
                    duration = json.optInt("duration", 0),
                    linkOgg = json.optString("link_ogg", ""),
                    linkMp3 = json.optString("link_mp3", ""))
            }
        }
    }
}
