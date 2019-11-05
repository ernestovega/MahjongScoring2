package es.etologic.mahjongscoring2.data.local_data_source.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.etologic.mahjongscoring2.domain.model.Combination
import io.reactivex.Single

@Dao
interface CombinationsDao {
    
    @Query("SELECT * FROM Combinations")
    fun getAll(): Single<List<Combination>>
    
    @Insert
    fun bulkInsert(combinations: List<Combination>)
    
    @Query(
        "SELECT * FROM Combinations " +
            "WHERE combinationName LIKE :filter " +
            "OR combinationDescription LIKE :filter " +
            "ORDER BY combinationPoints, combinationName ASC"
    )
    fun getFiltered(filter: String): Single<List<Combination>>
}
