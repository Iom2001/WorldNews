package uz.creator.worldnews.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.creator.worldnews.models.Article

@Dao
interface SavedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addBookmark(article: Article)

    @Query("SELECT * FROM articles")
    fun getBookmarks(): LiveData<List<Article>>

    @Query("DELETE FROM articles WHERE title=:title")
    fun removeBookmark(title: String)

    @Query("SELECT * FROM articles WHERE title=:title")
    fun checkBookmark(title: String): List<Article>
}