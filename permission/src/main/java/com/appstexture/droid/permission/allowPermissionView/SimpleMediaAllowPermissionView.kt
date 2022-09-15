package com.appstexture.droid.permission.allowPermissionView

import android.content.Context
import android.util.AttributeSet
import com.appstexture.droid.permission.R

open class SimpleMediaAllowPermissionView : AllowPermissionView {
    enum class MediaType {
        VIDEO, PHOTO, GIF
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    open fun setMediaType(mediaType: MediaType) {
        setMediaTypes(arrayOf(mediaType))
    }

    open fun setMediaTypes(mediaTypes: Array<MediaType>) {
        require(mediaTypes.isNotEmpty())
        val s = getMediaTypeNamesString(mediaTypes)
        setTitle(makeTitle(s))
        setDescription(makeDescription(s))
        setActionButtonText(makeActionButtonText(s))
    }

    protected open fun makeTitle(mediaTypesString: String): String {
        return context.getString(R.string.permission_apv_simple_media_title, mediaTypesString)
    }

    protected open fun makeDescription(mediaTypesString: String): String {
        return context.getString(R.string.permission_apv_simple_media_description, mediaTypesString)
    }

    protected open fun makeActionButtonText(mediaTypesString: String): String {
        return context.getString(R.string.permission_apv_simple_action_btn_text)
    }

    protected fun getMediaTypeNamesString(mediaTypes: Array<MediaType>): String {
        return mediaTypes.run {
            var s = ""
            for (i in this.indices) {
                val n = getName(this[i])
                s += if (i == 0) n else if (i == this.size - 1) " and $n" else ", $n"
            }
            s
        }
    }

    protected open fun getName(mediaType: MediaType): String {
        return when (mediaType) {
            MediaType.VIDEO -> "Videos"
            MediaType.PHOTO -> "Photos"
            MediaType.GIF -> "GIFs"
        }
    }
}