package io.github.sgpublic.aidescit.api.mariadb.domain

import javax.persistence.*

/**
 * 数据表 class_room
 */
@Entity
@Table(name = "class_room")
class ClassRoom {
    @Id
    @Column(name = "room_id")
    var id: Int = 0

    @Column(name = "room_name")
    var name: String = ""

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClassRoom) return false
        return name == other.name
    }

    override fun toString(): String {
        return name
    }
}