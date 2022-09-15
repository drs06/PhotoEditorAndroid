package com.appstexture.droid.permission.allowPermissionView

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.appstexture.droid.permission.R

open class AllowPermissionView : ConstraintLayout {

    interface OnActionButtonClickListener {
        fun onActionButtonClicked()
    }

    var onActionButtonClickListener: OnActionButtonClickListener? = null

    private lateinit var iconImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var actionButtonTextView: TextView
    private lateinit var actionButtonTextViewContainer: CardView

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val view = inflate(context, R.layout.layout_allow_permission_view, this)
        iconImageView = view.findViewById(R.id.icon)
        titleTextView = view.findViewById(R.id.title)
        descriptionTextView = view.findViewById(R.id.description)
        actionButtonTextView = view.findViewById(R.id.actionBtn)
        actionButtonTextViewContainer = view.findViewById(R.id.actionBtnContainer)

        attrs?.apply {
            val ta = context.obtainStyledAttributes(this, R.styleable.AllowPermissionView, 0, 0)
            ta.getDrawable(R.styleable.AllowPermissionView_apv_icon)?.let {
                iconImageView.setImageDrawable(it)
            }
            ta.getString(R.styleable.AllowPermissionView_apv_title)?.let {
                titleTextView.text = it
            }
            ta.getColor(R.styleable.AllowPermissionView_apv_title_color, 0).let {
                if (it != 0) titleTextView.setTextColor(it)
            }
            ta.getString(R.styleable.AllowPermissionView_apv_description)?.let {
                descriptionTextView.text = it
            }
            ta.getColor(R.styleable.AllowPermissionView_apv_description_color, 0).let {
                if (it != 0) descriptionTextView.setTextColor(it)
            }
            ta.getString(R.styleable.AllowPermissionView_apv_actionText)?.let {
                actionButtonTextView.text = it
            }
            ta.getColor(R.styleable.AllowPermissionView_apv_actionText_color, 0).let {
                if (it != 0) actionButtonTextView.setTextColor(it)
            }
            ta.getColor(R.styleable.AllowPermissionView_apv_actionText_bg_color, 0).let {
                if (it != 0) actionButtonTextViewContainer.setCardBackgroundColor(it)
            }
            ta.recycle()
        }

        actionButtonTextView.setOnClickListener {
            onActionButtonClickListener?.onActionButtonClicked()
        }
    }

    fun setIcon(resId: Int) {
        iconImageView.setImageResource(resId)
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setTitleColor(color: Int) {
        titleTextView.setTextColor(color)
    }

    fun setDescription(description: String) {
        descriptionTextView.text = description
    }

    fun setDescriptionColor(color: Int) {
        descriptionTextView.setTextColor(color)
    }

    fun setActionButtonText(text: String) {
        actionButtonTextView.text = text
    }

    fun setActionButtonTextColor(color: Int) {
        actionButtonTextView.setTextColor(color)
    }

    fun setActionButtonTextBackgroundColor(color: Int) {
        actionButtonTextViewContainer.setCardBackgroundColor(color)
    }
}