package com.example.lawyerapplication.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.lawyerapplication.db.data.StageBussinesLocal
import kotlinx.coroutines.flow.Flow


@Dao
interface StageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStage(stage: StageBussinesLocal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultipleStages(stages: List<StageBussinesLocal>)

    @Query("SELECT * FROM StageBussinesLocal")
    fun getAllStages(): LiveData<List<StageBussinesLocal>>

    @Query("SELECT * FROM StageBussinesLocal")
    fun getAllListStages(): List<StageBussinesLocal>

    @Transaction
    @Query("SELECT * FROM StageBussinesLocal")
    fun getAllStagesFlow(): Flow<List<StageBussinesLocal>>

    @Query("DELETE FROM StageBussinesLocal  WHERE Id=:stageId")
    suspend fun deleteStagesByStageId(stageId: String)

    @Query("SELECT * FROM StageBussinesLocal WHERE idBussines=:idBussines")
    fun getChatsOfGroupList(idBussines: String): List<StageBussinesLocal>

    @Query("SELECT * FROM StageBussinesLocal WHERE idBussines=:idBussines AND fireBaseId=:fireBaseId")
    fun ifExists(fireBaseId: Int, idBussines: Int): Boolean

    @Query("SELECT * FROM StageBussinesLocal  WHERE idBussines=:idBussines AND fireBaseId=:fireBaseId")
    fun getStageById(fireBaseId: Int, idBussines: Int): StageBussinesLocal?

    @Query("DELETE FROM StageBussinesLocal")
    fun nukeTable()

}