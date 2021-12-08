package it.unical.unijira.data.dto;

import lombok.*;

import java.net.URL;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class ProjectDTO {

    private Long id;
    private String name;
    private String key;
    private URL icon;
    private Long ownerId;

}
