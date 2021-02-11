package com.github.dromanenko.images

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(val url: String?, val title: String?) : Parcelable



