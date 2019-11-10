package es.etologic.mahjongscoring2.app.model

enum class WiningHandStepsPages(val code: Int) {
    
    DISCARDER(0),
    POINTS(1);
    
    companion object {
        
        fun getFromCode(code: Int): WiningHandStepsPages {
            return when (code) {
                1 -> POINTS
                else -> DISCARDER
            }
        }
    }
}
