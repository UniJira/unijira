package it.unical.unijira.data.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.URL;


@Getter
@Setter
@ToString
public class NotifyDTO {

    private String title;
    private String message;
    private URL target;
    private Long userId;

}
