package com.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(value = {AuditingEntityListener.class})    //Auditing 적용하기 위해 추가
@MappedSuperclass   // 공통 매핑 정보가 필요할때 쓰는 어노테이션, 부모 클래스를 상속받는 자식 클래스에 매핑 정보만 제공
@Setter
@Getter
public abstract class BaseTimeEntity {

    @CreatedDate    // 엔티티가 생성되어 저장될때 시간을 자동 저장함
    @Column(updatable = false)
    private LocalDateTime regTime ;

    @LastModifiedDate   // 엔티티의 값을 변경할 때 시간을 자동으로 저장함
    private LocalDateTime updateTime ;
}
