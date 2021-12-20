package it.unical.unijira.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class AbstractBaseDTO {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
