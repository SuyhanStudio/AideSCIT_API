package com.sgpublic.aidescit.api.mariadb.domain

import com.sgpublic.aidescit.api.module.APIModule
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "hitokoto")
class Hitokoto: Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "h_id")
    var id: Long = 0L

    @Column(name = "h_index")
    var index: Long = 0L

    @Column(name = "h_content")
    var content: String = ""

    @Column(name = "h_type")
    var type: String = ""

    @Column(name = "h_from")
    var from: String = ""

    @Column(name = "h_from_who")
    var fromWho: String = ""

    @Column(name = "h_creator")
    var creator: String = ""

    @Column(name = "h_creator_uid")
    var creatorUid: Long = 0L

    @Column(name = "h_reviewer")
    var reviewer: Long = 0L

    @Column(name = "h_insert_at")
    var insertAt: Long = APIModule.TS

    @Column(name = "h_length")
    var length: Long = 0L
}