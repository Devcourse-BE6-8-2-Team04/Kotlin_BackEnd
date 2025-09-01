package com.team04.back.domain.cloth.cloth.enums

import com.team04.back.domain.cloth.cloth.enums.Category.*

enum class ClothName(val category: Category) {
    // 상의 (TOP)
    T_SHIRT(TOP),       // 반팔
    SWEATSHIRT(TOP),    // 맨투맨
    HOODIE(TOP),        // 후드티
    SHIRT(TOP),         // 셔츠
    DRESS_SHIRT(TOP),   // 드레스 셔츠
    BLOUSE(TOP),        // 블라우스
    SWEATER(TOP),       // 스웨터
    CARDIGAN(TOP),      // 가디건
    COAT(TOP),          // 코트
    JACKET(TOP),        // 일반 자켓
    LEATHER_JACKET(TOP), // 가죽 자켓
    DENIM_JACKET(TOP),   // 데님 자켓
    BLAZER(TOP),        // 블레이저
    PADDING(TOP),       // 패딩
    VEST(TOP),          // 조끼
    WINDBREAKER(TOP),   // 바람막이
    FUNCTIONAL_T_SHIRT(TOP), // 기능성 티셔츠

    // 하의 (BOTTOM)
    JEANS(BOTTOM),      // 청바지
    SLACKS(BOTTOM),     // 슬랙스
    SHORTS(BOTTOM),     // 반바지
    SKIRT(BOTTOM),      // 치마
    JOGGER_PANTS(BOTTOM), // 조거 팬츠
    TRACK_PANTS(BOTTOM),  // 츄리닝 바지
    LEGGINGS(BOTTOM),   // 레깅스
    CARGO_PANTS(BOTTOM),  // 카고 바지
    CORDUROY_PANTS(BOTTOM), // 골덴 바지
    CHINOS(BOTTOM),        // 치노스
    SKI_PANTS(BOTTOM),      // 스키 바지

    // 신발 (SHOES)
    SNEAKERS(SHOES),       // 스니커즈
    ATHLETIC_SHOES(SHOES), // 운동화
    FLATS(SHOES),          // 플랫슈즈
    HEELS(SHOES),          // 하이힐
    LOAFERS(SHOES),        // 로퍼
    SLIPPERS(SHOES),       // 슬리퍼
    LEATHER_BOOTS(SHOES),  // 가죽 부츠
    FUR_BOOTS(SHOES),      // 털 부츠
    RAIN_BOOTS(SHOES),      // 장화
    SANDALS(SHOES),         // 샌들
    OXFORDS(SHOES),         // 옥스포드
    HIKING_SHOES(SHOES),   // 하이킹 신발
    ANKLE_BOOTS(SHOES),      // 앵클 부츠

    // 기타 (EXTRA)
    HAT(EXTRA),            // 모자
    CAP(EXTRA),            // 캡
    BEANIE(EXTRA),         // 비니
    SCARF(EXTRA),          // 목도리
    GLOVES(EXTRA),         // 장갑
    BELT(EXTRA),           // 벨트
    BAG(EXTRA),            // 가방
    BACKPACK(EXTRA),       // 백팩
    CROSSBODY_BAG(EXTRA),  // 크로스백
    SUNGLASSES(EXTRA),     // 선글라스
    UMBRELLA(EXTRA)        // 우산
}