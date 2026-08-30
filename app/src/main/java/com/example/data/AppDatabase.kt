package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        SetorEntity::class,
        RuaEntity::class,
        TrechoEntity::class,
        ExecucaoTrechoEntity::class,
        OcorrenciaEntity::class,
        PausaEntity::class,
        SyncQueueEntity::class,
        HistoricoSessaoEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun setorDao(): SetorDao
    abstract fun ruaDao(): RuaDao
    abstract fun trechoDao(): TrechoDao
    abstract fun execucaoTrechoDao(): ExecucaoTrechoDao
    abstract fun ocorrenciaDao(): OcorrenciaDao
    abstract fun pausaDao(): PausaDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun historicoDao(): HistoricoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rota_varredor_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
