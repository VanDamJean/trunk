package com.yacoo.rpg.game

typealias DieValue = Int
typealias Rng = () -> Double

fun requireDieValue(v: Int): DieValue {
    require(v in 1..6) { "Invalid die value: $v" }
    return v
}
