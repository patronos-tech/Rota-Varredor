package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SetorDao {
    @Query("SELECT * FROM setores ORDER BY dataCriacao DESC")
    fun getAllSetores(): Flow<List<SetorEntity>>

    @Query("SELECT * FROM setores WHERE id = :id LIMIT 1")
    suspend fun getSetorById(id: String): SetorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetor(setor: SetorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetores(setores: List<SetorEntity>)

    @Update
    suspend fun updateSetor(setor: SetorEntity)
}

@Dao
interface RuaDao {
    @Query("SELECT * FROM ruas WHERE setorId = :setorId ORDER BY ordemSugerida ASC")
    fun getRuasBySetor(setorId: String): Flow<List<RuaEntity>>

    @Query("SELECT * FROM ruas WHERE setorId = :setorId ORDER BY ordemSugerida ASC")
    suspend fun getRuasBySetorSync(setorId: String): List<RuaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRuas(ruas: List<RuaEntity>)
}

@Dao
interface TrechoDao {
    @Query("SELECT * FROM trechos WHERE setorId = :setorId ORDER BY ordemPlanejada ASC")
    fun getTrechosBySetor(setorId: String): Flow<List<TrechoEntity>>

    @Query("SELECT * FROM trechos WHERE setorId = :setorId ORDER BY ordemPlanejada ASC")
    suspend fun getTrechosBySetorSync(setorId: String): List<TrechoEntity>

    @Query("SELECT * FROM trechos WHERE id = :id LIMIT 1")
    suspend fun getTrechoById(id: String): TrechoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrechos(trechos: List<TrechoEntity>)

    @Update
    suspend fun updateTrecho(trecho: TrechoEntity)

    @Query("UPDATE trechos SET status = :status WHERE id = :trechoId")
    suspend fun updateStatus(trechoId: String, status: String)
}

@Dao
interface ExecucaoTrechoDao {
    @Query("SELECT * FROM execucoes_trecho WHERE setorId = :setorId ORDER BY horaInicial DESC")
    fun getExecucoesBySetor(setorId: String): Flow<List<ExecucaoTrechoEntity>>

    @Query("SELECT * FROM execucoes_trecho ORDER BY horaInicial DESC")
    fun getAllExecucoes(): Flow<List<ExecucaoTrechoEntity>>

    @Query("SELECT * FROM execucoes_trecho WHERE trechoId = :trechoId AND status = 'INICIADO' LIMIT 1")
    suspend fun getExecucaoAtiva(trechoId: String): ExecucaoTrechoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExecucao(execucao: ExecucaoTrechoEntity)

    @Update
    suspend fun updateExecucao(execucao: ExecucaoTrechoEntity)
}

@Dao
interface OcorrenciaDao {
    @Query("SELECT * FROM ocorrencias WHERE setorId = :setorId ORDER BY timestamp DESC")
    fun getOcorrenciasBySetor(setorId: String): Flow<List<OcorrenciaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOcorrencia(ocorrencia: OcorrenciaEntity)
}

@Dao
interface PausaDao {
    @Query("SELECT * FROM pausas WHERE setorId = :setorId ORDER BY horaInicio DESC")
    fun getPausasBySetor(setorId: String): Flow<List<PausaEntity>>

    @Query("SELECT * FROM pausas WHERE setorId = :setorId AND horaFim IS NULL LIMIT 1")
    suspend fun getPausaAtiva(setorId: String): PausaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPausa(pausa: PausaEntity)

    @Update
    suspend fun updatePausa(pausa: PausaEntity)
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue WHERE statusSync = 'PENDENTE' ORDER BY timestamp ASC")
    fun getPendingSyncItems(): Flow<List<SyncQueueEntity>>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE statusSync = 'PENDENTE'")
    fun getPendingCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueueItem(item: SyncQueueEntity)

    @Query("UPDATE sync_queue SET statusSync = 'SINCRONIZADO' WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("UPDATE sync_queue SET statusSync = 'SINCRONIZADO' WHERE statusSync = 'PENDENTE'")
    suspend fun markAllAsSynced()
}

@Dao
interface HistoricoDao {
    @Query("SELECT * FROM historico_sessoes ORDER BY dataInicio DESC")
    fun getAllHistorico(): Flow<List<HistoricoSessaoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistorico(historico: HistoricoSessaoEntity)
}
