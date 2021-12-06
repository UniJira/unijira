package it.unical.unijira.data.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URL;

@Getter
@Setter
@ToString
public class ProjectDTO {

    private Long id;
    private String name;
    private String key;
    private URL icon;
    private Long ownerId;

}
