package aoc2022

import java.io.File

fun main(args: Array<String>) {
    // println(day1())
    println(day2())
}

fun day1() = File("input/day01.txt").readText()
    .split("\r\n\r\n") // Thanks windows
    .map { it.trim().split("\r\n").sumOf(String::toInt) }
    .sorted().let { Pair(it.last(), it.takeLast(3).sum()) }

fun day2() = File("input/day02.txt").readLines()
    .map { Pair(it[0] - 'A', it[2] - 'X') }
    .let { Pair(
        it.sumOf { (a, b) -> b + 1 + (b - a + 1).mod(3) * 3 },
        it.sumOf { (a, b) -> 1 + (b + a - 1).mod(3) + b * 3 }
    )}
