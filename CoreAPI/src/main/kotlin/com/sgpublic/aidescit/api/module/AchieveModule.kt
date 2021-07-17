package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import com.sgpublic.aidescit.api.data.AchieveData
import com.sgpublic.aidescit.api.mariadb.dao.StudentAchieveRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AchieveModule {
    @Autowired
    private lateinit var achieve: StudentAchieveRepository
    @Autowired
    private lateinit var session: SessionModule

    fun get(username: String, year: String = SemesterInfoProperty.YEAR,
                   semester: Short = SemesterInfoProperty.SEMESTER): AchieveData {
        achieve.getAchieve(username, year, semester).run {
            if (this == null || isExpired()){
                return refresh(username, year, semester)
            }
            return getContent()
        }
    }

    private fun refresh(username: String, year: String, semester: Short): AchieveData {
        val session = session.get(username).session
        TODO()
    }
}