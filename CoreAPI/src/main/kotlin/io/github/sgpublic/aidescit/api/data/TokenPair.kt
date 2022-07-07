package io.github.sgpublic.aidescit.api.data

import org.springframework.web.bind.annotation.RequestParam
import java.io.Serializable

/**
 * token
 * @param access access_token
 * @param refresh refresh_token
 */
@Suppress("KDocUnresolvedReference")
class TokenPair (
    @RequestParam(name = "access_token", required = false, defaultValue = "")
    var access: String = "",
    @RequestParam(name = "refresh_token", required = false, defaultValue = "")
    var refresh: String = ""
): Serializable