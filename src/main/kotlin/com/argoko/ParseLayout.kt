package com.argoko

import java.awt.Color
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

data class Point(val x: Int, val y: Int)

data class Gbc(
    val gridx: Int,
    val gridy: Int,
    val width: Int,
    val height: Int
) {
    fun toGbc() = GridBagConstraints().also {
        it.gridx = gridx
        it.gridy = gridy
        it.gridwidth = width
        it.gridheight = height
        it.fill = GridBagConstraints.BOTH
        it.weightx = 1.0
        it.weighty = 1.0
    }
}

fun parseLayout(s: String, mutator: GridBagConstraints.() -> Unit) =
    s
        .lines()
        .let { lines ->
            lines
                .map { it.length }
                .toSet()
                .singleOrNull()
                ?.let { sz ->
                    lines.withIndex().flatMap { (y, line) ->
                        (0 until sz).map { x ->
                            line[x].let {
                                if (it != ' ') it to Point(x, y) else null
                            }
                        }.filterNotNull()
                    }
                } ?: error("Need all lines same length")
        }
        .groupBy { it.first }
        .mapValues { it.value.map { it.second } }
        .mapValues { (c, ps) ->
            Pair(
                ps.sortedBy { it.x }.let { it.first().x..it.last().x },
                ps.sortedBy { it.y }.let { it.first().y..it.last().y })
                .let { (xs, ys) ->
                    xs.forEach { x ->
                        ys.forEach { y ->
                            val p = Point(x, y)
                            require(p in ps) { "Point $p missing" }
                        }
                    }
                    Gbc(
                        xs.first(),
                        ys.first(),
                        xs.last() - xs.first() + 1,
                        ys.last() - ys.first() + 1
                    ).toGbc().apply { mutator() }
                }
        }


fun main() {
    val s = """
        TTTTTT
        AAACDF
        BBBCEF
        GGGGGF
    """.trimIndent()
    val x = parseLayout(
        s
    ) {}

    val l2 = """
        AaBbCcDdE
    """.trimIndent().let { parseLayout(it) { weighty = 0.0; weightx = 0.0 } }

    println(x)
    val colours =
        listOf(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.PINK, Color.ORANGE)
    SwingUtilities.invokeLater {
        val (parent, components) = JFrame("Woot").run {
            size = Dimension(800, 200)
            layout = GridBagLayout()
            val ret = x.entries.zip(colours).associate { (c_gbc, color) ->
                c_gbc.key to JPanel().apply {
                    background = color
                    layout = GridBagLayout()
                    add(JLabel("${c_gbc.key}"), GridBagConstraints().apply { fill = GridBagConstraints.BOTH })
                }.also {
                    add(it, c_gbc.value)
                }
            }
            pack()
            isVisible = true
            Pair(this, ret.toMap())
        }
        println("$components")

        parent.remove(components['T']!!)
        JPanel().apply {
            layout = GridBagLayout()
            l2.forEach { (c, gbc) ->
                if (c in 'A'..'D' || c in 'a'..'d') {
                    if (c.isUpperCase()) {
                        add(JLabel("$c").apply { preferredSize = Dimension(150, 24) }, gbc)
                    } else {
                        add(JTextField("$c").apply { preferredSize = Dimension(150, 24) }, gbc)
                    }
                } else {
                    println("Special case ! $gbc")
                    add(JPanel(), gbc.apply { weightx = 1.0; weighty = 1.0; })
                }
            }
            background = Color.PINK
            parent.add(this, x['T']?.apply { weightx = 0.0; weighty = 0.0; })
            parent.pack()
        }
    }
}