package com.example.proyecto_finhabits.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.proyecto_finhabits.data.entities.Category
import com.example.proyecto_finhabits.data.entities.Habit
import com.example.proyecto_finhabits.data.entities.HabitCompletion
import com.example.proyecto_finhabits.data.entities.HabitFrequency
import com.example.proyecto_finhabits.data.entities.Transaction
import com.example.proyecto_finhabits.data.dao.HabitDao
import com.example.proyecto_finhabits.data.dao.TransactionDao
import com.example.proyecto_finhabits.data.entities.TransactionType

class Converters {
    @TypeConverter fun fromTransactionType(v: TransactionType): String = v.name
    @TypeConverter fun toTransactionType(v: String): TransactionType = TransactionType.valueOf(v)
    @TypeConverter fun fromCategory(v: Category): String = v.name
    @TypeConverter fun toCategory(v: String): Category = Category.valueOf(v)
    @TypeConverter fun fromFrequency(v: HabitFrequency): String = v.name
    @TypeConverter fun toFrequency(v: String): HabitFrequency = HabitFrequency.valueOf(v)
}

@Database(
    entities = [Transaction::class, Habit::class, HabitCompletion::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FinHabitsDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile private var INSTANCE: FinHabitsDatabase? = null

        fun getDatabase(context: Context): FinHabitsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinHabitsDatabase::class.java,
                    "finhabits_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

