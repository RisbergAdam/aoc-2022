package aoc2022

import java.io.File
import kotlin.collections.*

fun main(args: Array<String>) {
    // println(day1())
    // println(day2())
    // println(day3())
    // println(day4())
    // println(day5())
    // println(day6())
    // println(day7())
    println(day8())
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

fun day5() = File("input/day05.txt").readText().let { text ->
    val boxes1 = Array(9) { ArrayList<String>() }
    val boxes2 = Array(9) { ArrayList<String>() }

    text.split("\n").forEach { line ->
        Regex("[A-Z]").findAll(line).forEach { match ->
            boxes1[match.range.first / 4].add(match.value)
            boxes2[match.range.first / 4].add(match.value)
        }
    }

    Regex("move (\\d+) from (\\d+) to (\\d+)").findAll(text).forEach { match ->
        val (n, from, to) = match.destructured.toList().map { it.toInt() - 1 }
        (0..n).forEach { i ->
            boxes1[to].add(0, boxes1[from].removeAt(0))
            boxes2[to].add(0, boxes2[from].removeAt(n - i))
        }
    }

    Pair(
        boxes1.joinToString("") { it[0] },
        boxes2.joinToString("") { it[0] }
    )
}

fun day6() = File("input/day06.txt").readText().let { line ->
    Pair(
        line.windowed(4).indexOfFirst { it.toSet().size == 4 } + 4,
        line.windowed(14).indexOfFirst { it.toSet().size == 14 } + 14,
    )
}

fun day7() = File("input/day07.txt").readLines().let { lines ->
    val acc0 = Pair(listOf<String>(), mapOf<List<String>, Int>())
    lines
        .map { it.split(" ") + "_" }
        .fold(acc0) { (pwd, dirs), (t, cmd, arg) ->
            when {
                t == "$" && cmd == "cd" && arg == ".." -> Pair(pwd.dropLast(1), dirs)
                t == "$" && cmd == "cd" -> Pair(pwd + arg, dirs)
                t == "$" && cmd == "ls" -> Pair(pwd, dirs)
                t == "dir" -> Pair(pwd, dirs)
                else -> Pair(pwd, pwd
                    .mapIndexed { ix, _ -> pwd.take(ix + 1) }
                    .fold(dirs) { acc, path ->
                        acc + mapOf(path to t.toInt().plus(acc[path] ?: 0))
                    })
            }
        }.let { (_, dirs) ->
            val sizes = dirs.toList().map { (_, size) -> size }
            val free = 70000000 - sizes.maxOf { it }
            Pair(
                sizes.filter { it <= 100000 }.sum(),
                sizes.filter { free + it > 30000000 }.minOf { it })
        }
}

fun day8() = File("input/day08.txt").readLines().let { lines ->
    val grid = lines.map { line -> line.map { it - '0' } }
    val dirs = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))

    var visibleCount = 0
    var maxScore  = 0

    grid.indices
        .flatMap { y -> grid.indices.map { x -> Pair(x, y) } }
        .forEach { (x, y) ->
            var visible = false
            outer@for ((dx, dy) in dirs) {
                for (s in 1..grid.size) {
                    val tx = x + dx * s
                    val ty = y + dy * s
                    if (tx !in grid.indices || ty !in grid.indices) visible = true
                    else if (grid[ty][tx] >= grid[y][x]) break
                }
            }

            var score  = 1
            for ((dx, dy) in dirs) {
                for (s in 1..grid.size) {
                    val tx = x + dx * s
                    val ty = y + dy * s
                    if (tx !in grid.indices || ty !in grid.indices) {
                        score *= s - 1
                        break
                    }
                    if (grid[ty][tx] >= grid[y][x]) {
                        score *= s
                        break
                    }
                }
            }

            if (visible) visibleCount++
            if (score > maxScore) maxScore = score
        }

    Pair(visibleCount, maxScore)
}