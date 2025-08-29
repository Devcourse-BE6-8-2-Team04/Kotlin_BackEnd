package com.team04.back.global.aspect.aop

import com.team04.back.domain.history.history.entity.History
import com.team04.back.global.annotation.TrackHistory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootTest
@AutoConfigureMockMvc
class HistoryAspectTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var historyRepository: TestHistoryRepository

    @TestConfiguration
    class DummyControllerConfig {
        @RestController
        class HistoryDummyController {
            @TrackHistory("TEST_ACTION")
            @GetMapping("/test/view-dummy-test")
            fun testHistory() = "ok"
        }
    }

    @Test
    fun `TrackHistory 어노테이션 작동 확인`() {
        mockMvc.get("/test/view-dummy-test") {
            header("X-Forwarded-For", "1.2.3.4")
        }.andExpect {
            status { isOk() }
        }

        val histories: List<History> = historyRepository.findAll()
        assertThat(histories).isNotEmpty()

        val last = histories.last()
        assertThat(last.path).isEqualTo("TEST_ACTION")
        assertThat(last.ipAddress).isEqualTo("1.2.3.4")

        historyRepository.deleteAllByPath("TEST_ACTION")
    }
}
