package uz.creator.worldnews.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.creator.worldnews.models.Article

@Database(entities = [Article::class], version = 1)
abstract class SavedDatabase : RoomDatabase() {

    abstract fun savedDao(): SavedDao

    companion object {
        @Volatile
        private var instance: SavedDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            SavedDatabase::class.java,
            "savedArticle"
        ).allowMainThreadQueries().build()
    }

}