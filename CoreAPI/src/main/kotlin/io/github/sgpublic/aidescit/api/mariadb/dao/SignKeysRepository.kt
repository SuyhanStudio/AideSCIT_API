package io.github.sgpublic.aidescit.api.mariadb.dao

import io.github.sgpublic.aidescit.api.core.util.SignUtil
import io.github.sgpublic.aidescit.api.mariadb.domain.SignKeys
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/** 数据表 sign_keys 操作 */
@Repository
interface SignKeysRepository: JpaRepository<SignKeys, String> {
    /**
     * 从数据库中调取最新的 AppKey
     * @param platform 平台名称，{"web", "android"}，可空，默认为 "web"
     * @return 返回满足条件的 AppKey，若平台不存在则返回 null
     */
    @Query("select `app_key` from `sign_keys` where `platform`=:platform order by `build` desc limit 1", nativeQuery = true)
    fun getAppKey(@Param("platform") platform: String = SignUtil.PLATFORM_WEB): String?

    /**
     * 从数据库中调取 AppSecret
     * @param appKey AppSecret 对应的 AppKey，可空，默认为 web 平台最新 AppKey
     * @param platform AppSecret 对应的平台名称，可空，默认为 "web"
     * @return 返回满足条件的 AppSecret，若 AppKey 与 platform 不对应则返回 null
     */
    @Query("select `app_secret` from `sign_keys` where `app_key`=:appKey and `platform`=:platform", nativeQuery = true)
    fun getAppSecret(
        @Param("appKey") appKey: String = getAppKey()!!,
        @Param("platform") platform: String = SignUtil.PLATFORM_WEB
    ): String?
}