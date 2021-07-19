package com.sgpublic.aidescit.api.data

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * 包含 __VIEWSTATE 的 Document
 * @param document [org.jsoup.nodes.Document]
 * @param viewstate __VIEWSTATE
 */
data class ViewstateDocument(
    var document: Document = Jsoup.parse(""),
    var viewstate: String = "",
    var generator: String = "",
)