package com.argoko

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.yaml.snakeyaml.Yaml


fun main() {

    val mapper = ObjectMapper(YAMLFactory()).apply {
        registerModule(KotlinModule())
        configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
    }

    val xs = ClassLoader.getSystemResourceAsStream("layoutexample.yaml").use {
        mapper.readValue( it, Layout::class.java)
    }

    println(xs)
}