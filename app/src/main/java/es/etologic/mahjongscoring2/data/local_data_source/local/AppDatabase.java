package es.etologic.mahjongscoring2.data.local_data_source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import es.etologic.mahjongscoring2.data.local_data_source.local.converters.CombinationDescriptionTypeConverter;
import es.etologic.mahjongscoring2.data.local_data_source.local.converters.DateConverter;
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.CombinationsDao;
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.GamesDao;
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.PlayersDao;
import es.etologic.mahjongscoring2.data.local_data_source.local.daos.RoundsDao;
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
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "MahjongScoring2";
    private static final Object LOCK = new Object();
    public static final int NOT_SET_ID = 0;

    private static volatile AppDatabase sInstance;

    public static AppDatabase getInstance(@NonNull Context context) {
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

    public abstract PlayersDao getPlayersDao();

    public abstract GamesDao getGamesDao();

    public abstract RoundsDao getRoundsDao();

    public abstract CombinationsDao getCombinationsDao();
}