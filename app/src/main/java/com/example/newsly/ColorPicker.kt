package com.example.newsly

object ColorPicker {
    val colors= arrayOf("#f8b500","#3391a1","#33a17a","#ffa8e2","#79dbdb","#f15656","#5c9f76","#f4dfdb","#c8d9a3","#8cb8ad","#b2907e","#9d78a1","#f0e68c")
    var index=1
    fun getColor():String{
        return colors[index++ %colors.size]
    }
}