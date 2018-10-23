package com.chopin.marketmanager.util

import java.util.regex.Pattern

class PSParse {
    fun parse(content: String, brands: Array<String>, types: Array<String>): Array<String> {
        val count = count(content)
        val brand = brand(content, brands)
        val type = type(content, types)
        val isP = ps(content)
        val price = price(content)
        return arrayOf(isP.toString(), brand, type, count.toString(), price.toString())
    }

    private fun count(content: String): Int {
        val p = Pattern.compile("[\\d]+[个只条旧台把件盒]")
        val m = p.matcher(content)
        return if (m == null) {
            0
        } else {
            val g = m.group()
            if (g == null) {
                0
            } else {
                val substring = g.substring(0, g.length - 1)
                substring.toInt()
            }
        }
    }

    private fun brand(content: String, brands: Array<String>): String {
        val sb = StringBuilder()
        for (i in 0..brands.size) {
            if (i != 0) {
                sb.append("|")
            }
            sb.append(brands[i])
        }
        val p = Pattern.compile(sb.toString())
        val m = p.matcher(content)
        return if (m == null) {
            ""
        } else {
            m.group() ?: ""
        }
    }

    private fun type(content: String, types: Array<String>): String {
        val sb = StringBuilder()
        for (i in 0..types.size) {
            if (i != 0) {
                sb.append("|")
            }
            sb.append(types[i])
        }
        val p = Pattern.compile(sb.toString())
        val m = p.matcher(content)
        return if (m == null) {
            ""
        } else {
            m.group() ?: ""
        }
    }

    private fun ps(content: String): Int {
        val p = Pattern.compile("进货|入货|买入|拿货|罗货")
        val m = p.matcher(content)
        return if (m == null) {
            val p2 = Pattern.compile("卖出|卖了|卖就|出货")
            val m2 = p2.matcher(content)
            if (m2 == null) {
                2
            } else {
                1
            }
        } else {
            0
        }
    }

    private fun price(content: String): Double {
        val p = Pattern.compile("[\\d]+[元蚊]")
        val m = p.matcher(content)
        return if (m == null) {
            0.0
        } else {
            val g = m.group()
            if (g == null) {
                0.0
            } else {
                val substring = g.substring(0, g.length - 1)
                substring.toDouble()
            }
        }
    }
}