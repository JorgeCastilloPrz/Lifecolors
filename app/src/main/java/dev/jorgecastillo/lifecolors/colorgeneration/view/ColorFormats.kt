package dev.jorgecastillo.lifecolors.colorgeneration.view

fun Int.toHex() = String.format("#%06X", 0xFFFFFF and this)
