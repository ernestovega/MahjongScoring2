package com.etologic.mahjongscoring2.app.model

import com.etologic.mahjongscoring2.business.model.enums.TableWinds

class Seat(var wind: TableWinds, var name: String, var points: Int, var penalty: Int, var state: SeatStates)
