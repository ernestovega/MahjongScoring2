package com.etologic.mahjongscoring2.app.utils

object StringUtils {
    
    fun isEmpty(text: String?): Boolean {
        return text == null || text.trim().isEmpty()
    }
}
