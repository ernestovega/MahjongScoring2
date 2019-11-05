package es.etologic.mahjongscoring2.app.model

import es.etologic.mahjongscoring2.domain.model.enums.TableWinds

class Seat(var wind: TableWinds, var name: String, var points: Int, var penalty: Int, var state: SeatStates)
