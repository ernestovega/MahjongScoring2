package es.etologic.mahjongscoring2.data.repository.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.data.repository.local.converters.CombinationDescriptionTypeConverter;
import es.etologic.mahjongscoring2.data.repository.local.converters.DateConverter;
import es.etologic.mahjongscoring2.data.repository.local.daos.CombinationsDao;
import es.etologic.mahjongscoring2.data.repository.local.daos.GamesDao;
import es.etologic.mahjongscoring2.data.repository.local.daos.PlayersDao;
import es.etologic.mahjongscoring2.data.repository.local.daos.RoundsDao;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.entities.Round;

@Database (entities = {
        Player.class,
        Game.class,
        Round.class,
        Combination.class}, version = 1)
@TypeConverters ({ DateConverter.class, CombinationDescriptionTypeConverter.class })
abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "MahjongScoring2";
    private static final Object LOCK = new Object();

    private static volatile AppDatabase sInstance;

    static AppDatabase getInstance(@NonNull Context context) {
        if(sInstance == null) {
            synchronized(LOCK) {
                if(sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return sInstance;
    }

    abstract PlayersDao getPlayersDao();

    abstract GamesDao getGamesDao();

    abstract RoundsDao getRoundsDao();

    abstract CombinationsDao getCombinationsDao();
}