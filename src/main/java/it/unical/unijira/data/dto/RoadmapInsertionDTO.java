package it.unical.unijira.data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.unical.unijira.data.dto.items.ItemDTO;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoadmapInsertionDTO {

    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startingDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)

    private LocalDate endingDate;

    private ItemDTO item;

    private Long roadmapId;
}
