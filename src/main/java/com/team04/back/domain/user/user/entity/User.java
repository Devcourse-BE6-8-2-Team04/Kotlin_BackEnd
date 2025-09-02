package com.team04.back.domain.user.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// [원본 코드]
//public class User {
//}

// [임시 수정 코드]
// 테스트 실행 중 "User is not an @Entity type" 오류 발생하여 @Entity 추가
// 수정 사유: JPA 매핑 문제 해결 (ClothRecommendationHistory.user 참조 에러)
// 추후 담당자가 확인 후 필요 시 수정 바람
@Entity
public class User {
    @Id
    private Long dummy;

    public void setDummy(Long dummy) {
        this.dummy = dummy;
    }

    public Long getDummy() {
        return dummy;
    }
}
