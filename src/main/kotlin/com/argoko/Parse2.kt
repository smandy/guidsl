package com.argoko

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.awt.Color
import java.awt.Dimension
import java.awt.GridBagConstraints
import javax.swing.JFrame

data class Element(var name : String,
                   var layout : String,
                   var dim : Dimension?,
                   var parent : String?) {
    val theLayout: Map<Char, GridBagConstraints> by lazy {
        parseLayout(layout) {}
    }
}

fun lazyColor( s : String?) : () -> Color? = {
    s?.let {
        Class.forName("java.awt.color")
            .getField(it)?.let { it.get(null) as Color }
    }
}

data class JPanelDef( val parent : String,
                      val name : String,
                      val background : String?) {
    val backgroundColor : Color? by lazy(lazyColor(background))
}
data class JLabelDef( val parent : String,
                      val name : String,
                      val text : String)
data class TextFieldDef( val parent : String,
                      val name : String,
                      val text : String)

data class JButtonDef( val parent : String,
                         val name : String,
                         val text : String)

data class Layout(val frame : Element,
                  var layouts : List<Element>?,
                  val panels : List<JPanelDef>?,
                  val buttons : List<JButtonDef>?,
                  var labels : List<JLabelDef>?,
                  var constraints : Map<String,GridBagConstraints>,
                  var textfields : List<TextFieldDef>,
                  var dimensions : List<Dimension>?) {
}

data class Person( val first : String, val last : String)

fun main() {
    val mapper = ObjectMapper(YAMLFactory()).apply {
        registerModule(KotlinModule())
        configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
    }

    val x = ClassLoader.getSystemResourceAsStream("layoutexample.yaml").use {
        //val x = mapper.readValues( it)
        val x = mapper.readValue( it, Layout::class.java)
        //val y = mapper.readValue( it, Layout::class.java)
        ////val y = mapper.readValue(it, Person::class.java)
        x
    }

    println("${x}")
    println( x.layouts?.map { it.theLayout } )

    fun getTuple(name : String, parent : String): Pair<String, Pair<String, String>> {
        val (parent,subComponent) = name.split(".")
        return parent to ( subComponent to name )
    }

/*    val children : List<Pair<String,Pair<String,String>>> =
        (x.labels?.map { getTuple(it.name, it.parent) } ?: emptyList()) +
                (x.panels?.map { getTuple(it.name, it.parent) } ?: emptyList()) +
            x.textfields.map { getTuple(it.parent) }*/


}