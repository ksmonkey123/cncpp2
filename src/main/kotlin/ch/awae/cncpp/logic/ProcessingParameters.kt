package ch.awae.cncpp.logic

data class ProcessingParameters(
    val homingParameters: HomingParameters?,
    val workSpeed: Int,
    val travelSpeed: Int,
)

data class HomingParameters(
    val levelBed: Boolean,
    val zeroOffset: Point
)

data class Point(val x: Int, val y: Int)