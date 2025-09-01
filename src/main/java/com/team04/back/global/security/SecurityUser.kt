package com.team04.back.global.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class SecurityUser(
    val id: Int,
    username: String?,
    password: String?,
    val name: String,
    authorities: Collection<GrantedAuthority>
) : User(username, password, authorities)
