package com.sgpublic.aidescit.api.data

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

data class ViewstateDocument(
    var document: Document = Jsoup.parse(""),
    var viewstate: String = ""
)