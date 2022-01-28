package it.unical.unijira.data.dto;

import lombok.*;

import java.net.URL;

@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class DocumentDTO extends AbstractBaseDTO {

    private Long id;
    private URL path;
    private String filename;
    private String mime;
    private Long projectId;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private URL userAvatar;
    private String userUsername;

}
