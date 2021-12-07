package it.unical.unijira.data.models;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractBaseEntity implements Serializable {

    @Column
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

}
