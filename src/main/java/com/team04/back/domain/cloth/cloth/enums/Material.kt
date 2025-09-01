package com.team04.back.domain.cloth.cloth.enums

import com.team04.back.domain.cloth.cloth.enums.Category.*

enum class Material(val categories: Set<Category>) {
    COTTON(setOf(TOP, BOTTOM)),            // 면
    POLYESTER(setOf(TOP, BOTTOM, SHOES)),  // 폴리에스테르
    WOOL(setOf(TOP, BOTTOM)),              // 울(모직)
    LINEN(setOf(TOP, BOTTOM)),             // 린넨
    NYLON(setOf(TOP, BOTTOM, SHOES)),      // 나일론
    DENIM(setOf(TOP, BOTTOM)),             // 데님
    LEATHER(setOf(TOP, SHOES)),            // 가죽
    FLEECE(setOf(TOP)),                    // 플리스
    SILK(setOf(TOP, BOTTOM)),                      // 실크
    CASHMERE(setOf(TOP)),                  // 캐시미어
    CORDUROY(setOf(TOP, BOTTOM))           // 코듀로이(골덴)
}