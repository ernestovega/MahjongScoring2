package es.etologic.mahjongscoring2.app.model

enum class GamePages(val code: Int) {
    TABLE(0),
    LIST(1);
    
    companion object {
        
        fun getFromCode(code: Int): GamePages {
            return when (code) {
                1 -> LIST
                else -> TABLE
            }
        }
    }
}
