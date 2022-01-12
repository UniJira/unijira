package it.unical.unijira.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractBaseDTO {
    private String createdAt;
    private String updatedAt;
}
