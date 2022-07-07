package io.github.sgpublic.aidescit.api.mariadb.dao

import io.github.sgpublic.aidescit.api.mariadb.domain.ClassRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/** 数据表 class_room 操作 */
@Repository
interface ClassRoomRepository: JpaRepository<ClassRoom, Short>