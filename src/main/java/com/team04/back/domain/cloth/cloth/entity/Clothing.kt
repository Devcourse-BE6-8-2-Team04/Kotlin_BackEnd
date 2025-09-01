package com.team04.back.domain.cloth.cloth.entity

import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "clothing_type")
abstract class Clothing(
    @Column(nullable = false) open var clothName: ClothName,
    @Column(nullable = false) open var imageUrl: String
) : BaseEntity() {
}