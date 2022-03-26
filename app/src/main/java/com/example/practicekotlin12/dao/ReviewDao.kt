package com.example.practicekotlin12.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.practicekotlin12.model.Review

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE isbn == :isbn")
    fun getOneReview(isbn: String): Review

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReview(review: Review)
}