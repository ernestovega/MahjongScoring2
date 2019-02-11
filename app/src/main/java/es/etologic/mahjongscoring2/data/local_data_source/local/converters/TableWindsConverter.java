package es.etologic.mahjongscoring2.data.local_data_source.local.converters;

import android.arch.persistence.room.TypeConverter;

import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.EAST;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NONE;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NORTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.SOUTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.WEST;

public class TableWindsConverter {

    @TypeConverter
    public static TableWinds toStatus(int status) {

        if (status == EAST.getCode()) {
            return EAST;
        } else if (status == SOUTH.getCode()) {
            return SOUTH;
        } else if (status == WEST.getCode()) {
            return WEST;
        } else if (status == NORTH.getCode()) {
            return NORTH;
        } else {
            return NONE;
        }
    }

    @TypeConverter
    public static Integer toInteger(TableWinds tableWinds) {
        return tableWinds.getCode();
    }
}
