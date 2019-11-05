package es.etologic.mahjongscoring2.domain.model.enums

enum class TableWinds(val code: Int) {
    NONE(0),
    EAST(1),
    SOUTH(2),
    WEST(3),
    NORTH(4);
    
    val index: Int
        get() = code - 1
    
    companion object {
        
        fun getFromCode(code: Int): TableWinds {
            return when (code) {
                1 -> EAST
                2 -> SOUTH
                3 -> WEST
                4 -> NORTH
                else -> NONE
            }
        }
    }
}
