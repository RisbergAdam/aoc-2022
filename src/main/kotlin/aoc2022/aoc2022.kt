package aoc2022

import java.io.File
import kotlin.collections.ArrayDeque

fun main(args: Array<String>) {
    // println(day1())
    // println(day2())
    // println(day3())
    // println(day4())
    println(day5())
}

fun day1() = File("input/day01.txt").readText()
    .split("\r\n\r\n") // Thanks windows
    .map { it.split("\r\n").sumOf(String::toInt) }
    .sorted().let { Pair(it.last(), it.takeLast(3).sum()) }

fun day2() = File("input/day02.txt").readLines()
    .map { Pair(it[0] - 'A', it[2] - 'X') }
    .let {
        Pair(
            it.sumOf { (a, b) -> (b - a + 1).mod(3) * 3 + b + 1 },
            it.sumOf { (a, b) -> (b + a - 1).mod(3) + b * 3 + 1 }
        )
    }

fun day3() = File("input/day03.txt").readLines().let { list ->
    fun prio(c: Char) = (('a'..'z') + ('A'..'Z')).indexOf(c) + 1
    Pair(
        list
            .map { it.take(it.length / 2).toSet() intersect it.drop(it.length / 2).toSet() }
            .sumOf { prio(it.first()) },
        list
            .map { it.toSet() }.chunked(3)
            .map { (a, b, c) -> a intersect b intersect c }
            .sumOf { prio(it.first()) }
    )
}

fun day4() = File("input/day04.txt").readLines()
    .map { it.split(Regex("[-,]")).map(String::toInt) }
    .let { lines ->
        Pair(
            lines.count { (a1, a2, b1, b2) -> a1 in b1..b2 && a2 in b1..b2 || b1 in a1..a2 && b2 in a1..a2 },
            lines.count { (a1, a2, b1, b2) -> a1 in b1..b2 || a2 in b1..b2 || b1 in a1..a2 || b2 in a1..a2 },
        )
    }

fun day5() = File("input/day05.txt").readLines().let { lines ->
    val boxes1 = Array(9) { ArrayDeque<Char>() }
    val boxes2 = Array(9) { ArrayDeque<Char>() }
    lines
        .takeWhile { it != "" }
        .reversed()
        .forEach { line ->
            line
                .withIndex()
                .filter { (_, c) -> c.isLetter() }
                .forEach { (x, c) ->
                    boxes1[x / 4].add(c)
                    boxes2[x / 4].add(c)
                }
        }

    lines.dropWhile { it != "" }.drop(1).forEach { line ->
        val (n, source, target) = Regex("\\d+").findAll(line).map { it.value.toInt() - 1 }.toList()
        (0..n).forEach { i ->
            boxes1[target].add(boxes1[source].removeLast())
            boxes2[target].add(boxes2[source].removeAt(boxes2[source].size - n + i - 1))
        }
    }

    Pair(
        boxes1.map { it.last() }.joinToString(""),
        boxes2.map { it.last() }.joinToString("")
    )
}