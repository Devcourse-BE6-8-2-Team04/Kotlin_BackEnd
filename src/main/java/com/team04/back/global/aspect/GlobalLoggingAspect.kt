import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class GlobalLoggingAspect{
    private val logger = LoggerFactory.getLogger(this::class.java)

    // 모든 Controller의 메서드 호출 전에 요청 정보를 로그로 남김
    @Before("execution(* com.team04.back..*Controller.*(..))")
    fun logRequest(joinPoint: JoinPoint) {
        val method = joinPoint.signature.toShortString()
        val args = joinPoint.args.joinToString(", ")
        logger.info("Request: $method with args: [$args]")
    }

    // 모든 Controller의 메서드 호출 후에 반환값을 로그로 남김
    @AfterReturning(pointcut = "execution(* com.team04.back..*Controller.*(..))", returning = "result")
    fun logResponse(joinPoint: JoinPoint, result: Any?) {
        val method = joinPoint.signature.toShortString()
        logger.info("Response from $method: $result")
    }
}