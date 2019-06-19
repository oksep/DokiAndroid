package com.dokiwa.dokidoki.center.ext

/**
 * Created by Septenary on 2019-06-20.
 */
fun <T> MutableList<T>.swap(index1: Int, index2: Int): MutableList<T> {
    val tmp = this[index1] // 'this' corresponds to the list
    this[index1] = this[index2]
    this[index2] = tmp
    return this
}