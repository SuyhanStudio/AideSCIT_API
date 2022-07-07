package io.github.sgpublic.aidescit.api.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AvailableClassRoomController {

    @RequestMapping("/aidescit/available_class_room")
    fun findEmptyClassRoom(sign: String) {

    }
}