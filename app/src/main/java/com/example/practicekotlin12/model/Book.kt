package com.example.practicekotlin12.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Book(
    @SerializedName("isbn") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: String,
    @SerializedName("image") val coverSmallUrl: String,
    @SerializedName("link") val mobileLink: String
) : Parcelable

/*
@SerializedName(내부값) 과 val 값 을 매칭
위 @SerializedName("isbn") val id: String 을 예시로,
서버에서는 'isbn'에 해당하는 값을 데이터 클래스의 id 에 해당하는 값과 매핑
 */