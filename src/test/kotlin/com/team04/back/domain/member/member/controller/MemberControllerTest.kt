package com.team04.back.domain.member.member.controller

import com.team04.back.domain.member.member.entity.Gender
import com.team04.back.domain.member.member.entity.Tendency
import com.team04.back.domain.member.member.service.MemberService
import jakarta.servlet.http.Cookie
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiV1MemberControllerTest @Autowired constructor(
    val memberService: MemberService,
    val mvc: MockMvc
) {

    @Test
    @DisplayName("회원가입")
    fun t1() {
        val content = """
        {
            "userId": "testuser",
            "password": "1234",
            "email": "usernew@example.com",
            "age": 30,
            "gender": "MALE",
            "tendency": "NEUTRAL"
        }
    """.trimIndent()

        mvc.perform(post("/api/v1/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)
        )
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.resultCode").value("201-1"))
            .andExpect(jsonPath("$.msg").value("testuser님 환영합니다. 회원가입이 완료되었습니다."))
            .andExpect(jsonPath("$.data").exists())
    }

    @Test
    @DisplayName("로그인")
    fun loginTest() {
        val userId = "user1"
        val password = "password1"
        val encodedPassword = BCryptPasswordEncoder().encode(password)

        val resultActions = mvc.perform(
            post("/api/v1/auth/login")  // 경로 수정
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {
                "username": "$userId",  
                "password": "$password"
            }
        """.trimIndent())
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("200-1"))
            .andExpect(jsonPath("$.msg").value("${userId}님 환영합니다."))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.apiKey").isNotEmpty)
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty)

            val member = requireNotNull(memberService.findByUsername(userId))

            resultActions.andExpect { result ->
                val apiKeyCookie = result.response.getCookie("apiKey")
                assertNotNull(apiKeyCookie)
                assertEquals(member.apiKey, apiKeyCookie.value)
                assertEquals("/", apiKeyCookie.path)
                assertTrue(apiKeyCookie.isHttpOnly)

                val accessTokenCookie = result.response.getCookie("accessToken")
                assertNotNull(accessTokenCookie)
                assertTrue(accessTokenCookie.value.isNotBlank())
                assertEquals("/", accessTokenCookie.path)
                assertTrue(accessTokenCookie.isHttpOnly)
            }
    }


    @Test
    @DisplayName("내 정보 조회")
    @WithUserDetails("user1")
    fun t3() {
        val userId = "user1"
        val member = requireNotNull(memberService.findByUsername(userId))

        mvc.perform(get("/api/v1/members/me"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(member.id))
            .andExpect(jsonPath("$.createDate").value(Matchers.startsWith(member.createDate.toString().substring(0, 20))))
            .andExpect(jsonPath("$.modifyDate").value(Matchers.startsWith(member.modifyDate.toString().substring(0, 20))))
            .andExpect(jsonPath("$.userId").value(member.userId))
            .andExpect(jsonPath("$.email").value(member.email))
            .andExpect(jsonPath("$.age").value(member.age))
            .andExpect(jsonPath("$.gender").value(member.gender.name))
            .andExpect(jsonPath("$.tendency").value(member.tendency.name))
    }


    @Test
    @DisplayName("내 정보, with apiKey Cookie")
    fun t4() {
        val member = requireNotNull(memberService.findByUsername("user1"))
        val apiKeyCookie = Cookie("apiKey", member.apiKey)

        mvc.perform(
            get("/api/v1/members/me")
                .cookie(apiKeyCookie)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(member.id))
            .andExpect(jsonPath("$.userId").value(member.userId))
            .andExpect(jsonPath("$.email").value(member.email))
            .andExpect(jsonPath("$.age").value(member.age))
            .andExpect(jsonPath("$.gender").value(member.gender.name))
            .andExpect(jsonPath("$.tendency").value(member.tendency.name))
    }

    @Test
    @DisplayName("로그아웃 - 로그인 상태에서 쿠키 포함")
    fun t6() {
        // 1. 로그인 요청 (API 호출 후 쿠키 획득)
        val loginResult = mvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "username": "user1",
                    "password": "password1"
                }
            """.trimIndent())
        ).andReturn()

        // 2. 로그인 후 쿠키 추출
        val apiKeyCookie = loginResult.response.getCookie("apiKey")
        val accessTokenCookie = loginResult.response.getCookie("accessToken")

        assertNotNull(apiKeyCookie, "apiKey 쿠키가 존재해야 합니다.")
        assertNotNull(accessTokenCookie, "accessToken 쿠키가 존재해야 합니다.")

        // 3. 로그아웃 요청 시 쿠키 포함
        val resultActions = mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/auth/logout")
                .cookie(apiKeyCookie, accessTokenCookie)
        ).andDo { print() }

        // 4. 로그아웃 응답 검증
        resultActions.andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("200-1"))
            .andExpect(jsonPath("$.msg").value("로그아웃 되었습니다."))
            .andExpect {
                val clearedApiKeyCookie = it.response.getCookie("apiKey")
                assertEquals("", clearedApiKeyCookie?.value)
                assertEquals(0, clearedApiKeyCookie?.maxAge)
                assertEquals("/", clearedApiKeyCookie?.path)
                assertTrue(clearedApiKeyCookie?.isHttpOnly == true)

                val clearedAccessTokenCookie = it.response.getCookie("accessToken")
                assertEquals("", clearedAccessTokenCookie?.value)
                assertEquals(0, clearedAccessTokenCookie?.maxAge)
                assertEquals("/", clearedAccessTokenCookie?.path)
                assertTrue(clearedAccessTokenCookie?.isHttpOnly == true)
            }
    }

    @Test
    @DisplayName("엑세스 토큰이 만료되었거나 유효하지 않다면 apiKey를 통해서 재발급")
    fun t7() {
        val member = requireNotNull(memberService.findByUsername("user1"))
        val invalidAccessToken = "expired-or-invalid-access-token"

        mvc.perform(
            get("/api/v1/members/me")
                .header("Authorization", "Bearer expired-or-invalid-access-token")
                .cookie(Cookie("apiKey", member.apiKey))
        ).andDo { print() }
            .andExpect(status().isOk)
            .andExpect {
                val accessTokenCookie = it.response.getCookie("accessToken")
                assertNotNull(accessTokenCookie)
                assertTrue(accessTokenCookie.value.isNotBlank())
                assertEquals("/", accessTokenCookie.path)
                assertTrue(accessTokenCookie.isHttpOnly)

                val headerAuthorization = it.response.getHeader("Authorization")
                assertNotNull(headerAuthorization)
                assertEquals(headerAuthorization, "Bearer ${accessTokenCookie.value}")

            }
    }

    @Test
    @DisplayName("Authorization 헤더가 Bearer 형식이 아닐 때 오류")
    fun t8() {
        mvc.perform(
            get("/api/v1/members/me")
                .header("Authorization", "key")
        ).andDo { print() }
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.resultCode").value("401-2"))
            .andExpect(jsonPath("$.msg").value("Authorization 헤더가 Bearer 형식이 아닙니다."))
    }



    @BeforeAll
    fun setupAll() {
        if (memberService.findByUsername("user1") == null) {
            memberService.join(
                userId = "user1",
                password = "1234",
                email = "usernew@example.com",
                age = 30,
                gender = Gender.MALE,
                tendency = Tendency.NEUTRAL
            )
        }
        val member = memberService.findByUsername("user1")
    }
}
