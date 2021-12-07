package it.unical.unijira.data.dto;

import lombok.*;

import java.net.URL;


@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class NotifyDTO {

    private String title;
    private String message;
    private URL target;
    private Long userId;

}
