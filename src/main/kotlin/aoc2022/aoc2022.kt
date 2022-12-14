package aoc2022

import java.io.File
import java.util.*
import kotlin.collections.*
import kotlin.math.*

fun main(args: Array<String>) {
    // println(day1())
    // println(day2())
    // println(day3())
    // println(day4())
    // println(day5())
    // println(day6())
    // println(day7())
    // println(day8())
    // println(day9())
    // println(day10())
    // println(day11())
    // println(day12())
    println(day13())
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

    grid.indices
        .flatMap { y -> grid.indices.map { x -> Pair(x, y) } }
        .map { (x, y) ->
            Pair(
                dirs.any { (dx, dy) ->
                    (1..grid.size)
                        .map { s -> Pair(x + dx * s, y + dy * s) }
                        .filter { (tx, ty) -> tx in grid.indices && ty in grid.indices }
                        .all { (tx, ty) -> grid[ty][tx] < grid[y][x] }
                },
                dirs.fold(1) { acc, (dx, dy) ->
                    acc * (1..grid.size)
                        .map { s -> Pair(x + dx * s, y + dy * s) }
                        .filter { (tx, ty) -> tx + dx in grid.indices && ty + dy in grid.indices }
                        .takeWhile { (tx, ty) -> grid[ty][tx] < grid[y][x] }
                        .count().plus(1)
                })
        }.let { trees ->
            Pair(
                trees.count { (visible, _) -> visible },
                trees.maxOf { (_, score) -> score })
        }
}

fun day9() = File("input/day09.txt").readLines().let { lines ->
    data class Point(var x: Int = 0, var y: Int = 0)

    val rope = List(10) { Point() }
    val visited1 = HashSet<Point>()
    val visited2 = HashSet<Point>()

    lines
        .map { it.split(" ") }
        .map { (dir, s) -> Pair(dir, s.toInt()) }
        .forEach { (dir, s) ->
            repeat(s) {
                when (dir) {
                    "U" -> rope[0].y++
                    "R" -> rope[0].x++
                    "D" -> rope[0].y--
                    "L" -> rope[0].x--
                }

                rope.windowed(2).forEach { (prev, curr) ->
                    val dx = prev.x - curr.x
                    val dy = prev.y - curr.y

                    if (abs(dx) > 1 || abs(dy) > 1) {
                        curr.x += dx.sign
                        curr.y += dy.sign
                    }
                }

                visited1 += rope[1].copy()
                visited2 += rope.last().copy()
            }
        }

    Pair(visited1.size, visited2.size)
}

fun day10() = File("input/day10.txt").readLines().let { lines ->
    var x = 1
    var signal = 0
    val screen = Array(6) { Array(40) { "." } }

    lines.map { it.split(" ") + "_" }
        .flatMap {
            if (it[0] == "addx") listOf(listOf("noop", "_"), it)
            else listOf(it)
        }
        .forEachIndexed { clock, (op, arg) ->
            val row = clock / 40
            val col = clock.mod(40)
            if (col in (x - 1)..(x + 1)) screen[row][col] = "#"
            if (op == "addx") x += arg.toInt()
            if (col == 20) signal += x * clock
        }

    screen.forEach { println(it.joinToString("")) }
    signal
}

fun day11() = File("input/day11.txt").readText().let { text ->
    data class Monkey(
        val items: MutableList<Long>,
        val op: (Long) -> Long,
        val test: Long,
        val m1: Int,
        val m2: Int,
        var inspections: Long = 0
    )

    val monkeys = text
        .split("\r\n\r\n")
        .map { it.split("\n") }
        .map { input ->
            val args = input[2].drop(19).trim().split(" ")
            Monkey(
                items = input[1].drop(18).split(", ").map { it.trim().toLong() }.toMutableList(),
                op = { v ->
                    val arg2 = if (args[2] == "old") v else args[2].toLong()
                    val f = Int::and
                    when (args[1]) {
                        "*" -> v * arg2
                        "+" -> v + arg2
                        else -> 0L
                    }
                },
                test = input[3].takeLastWhile { it != ' ' }.trim().toLong(),
                m1 = input[4].takeLastWhile { it != ' ' }.trim().toInt(),
                m2 = input[5].takeLastWhile { it != ' ' }.trim().toInt(),
            )
        }

    val testSum = monkeys.map { it.test }.reduce { a, b -> a * b }

    repeat(10000) {
        monkeys.forEach { m ->
            val items = m.items.toList()
            m.items.clear()
            items.forEach { item ->
                m.inspections++
                val inspected = m.op(item).mod(testSum) // / 3
                val receiver = if (inspected.mod(m.test) == 0L) m.m1 else m.m2
                monkeys[receiver].items.add(inspected)
            }
        }
    }

    monkeys.map { it.inspections }.sorted().takeLast(2).let { (a, b) -> a * b }
}

fun day12() = File("input/day12.txt").readLines().let { lines ->
    data class Point(val x: Int, val y: Int)
    data class Cell(val char: String, val height: Int, val position: Point)

    val grid = lines.mapIndexed { y, line ->
        line
            .map { it.toString() }
            .map { Pair(it, it.replace('S', 'a').replace('E', 'z')) }
            .mapIndexed { x, (char, height) ->
                Cell(char, height[0] - 'a', Point(x, y))
            }
    }

    val positions = grid.flatMap { line -> line.map { it.position } }.toSet()

    val visited = HashSet<Point>()
    val queue = PriorityQueue<Pair<Int, Point>> { (p1), (p2) -> p1 - p2 }
    val start = grid.flatten().find { (char) -> char == "S" }!!.position
    val end = grid.flatten().find { (char) -> char == "E" }!!.position

    queue.add(Pair(0, end))

    var part2 = 0

    while (queue.isNotEmpty()) {
        val (step, head) = queue.poll()
        if (head == start) return@let Pair(step, part2)
        val height = grid[head.y][head.x].height

        if (head.y == 0 || head.y == grid.lastIndex ||
            head.x == 0 || head.x == grid[0].lastIndex
        ) {
            if (height == 0 && part2 == 0) part2 = step
        }

        queue.addAll(
            listOf(
                Point(head.x + 1, head.y + 0),
                Point(head.x + 0, head.y + 1),
                Point(head.x - 1, head.y + 0),
                Point(head.x + 0, head.y - 1)
            )
                .filter { it !in visited }
                .filter { it in positions }
                .filter { height - grid[it.y][it.x].height <= 1 }
                .also { visited += it }
                .map { Pair(step + 1, it) })
    }

    return@let null
}

fun day13() = File("input/day13.txt").readLines().let { lines ->
    fun parse(line: String): ArrayList<Any> {
        val stack = arrayListOf(ArrayList<Any>())
        line.split(",")
            .flatMap { text -> text.split("[").flatMap { listOf("[", it) }.drop(1) }
            .flatMap { text -> text.split("]").flatMap { listOf("]", it) }.drop(1) }
            .filter { it != "" }
            .forEach { t ->
                when (t) {
                    "[" -> stack += ArrayList<Any>()
                    "]" -> stack.removeLast().let { stack.last().add(it) }
                    else -> stack.last() += t.toInt()
                }
            }
        return stack.last()
    }

    fun compare(left: Any, right: Any): Boolean? {
        if (left !is Int && right is Int) return compare(left, arrayListOf(right))
        if (left is Int && right !is Int) return compare(arrayListOf(left), right)

        if (left is Int && right is Int) {
            return when {
                left < right -> true
                left > right -> false
                else -> null
            }
        } else if (left is ArrayList<*> && right is ArrayList<*>) {
            val compared = left.zip(right).firstNotNullOfOrNull { (a, b) -> compare(a, b) }
            return compared ?: when {
                left.size < right.size -> true
                left.size > right.size -> false
                else -> null
            }
        }

        return true
    }

    val p1 = lines
        .chunked(3)
        .withIndex()
        .filter { (_, group) -> compare(parse(group[0]), parse(group[1]))!! }
        .sumOf { (ix) -> ix + 1 }

    val p2 = lines
        .filter { it != "" }
        .plus(listOf("[[2]]", "[[6]]"))
        .sortedWith { a, b -> if (compare(parse(a), parse(b)) != true) 1 else -1 }
        .map { it }.let { list ->
            val key1 = list.indexOf("[[2]]") + 1
            val key2 = list.indexOf("[[6]]") + 1
            key1 * key2
        }

    Pair(p1, p2)
}