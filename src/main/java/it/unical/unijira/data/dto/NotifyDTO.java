package it.unical.unijira.data.dto;

import it.unical.unijira.data.models.Notify;
import lombok.*;

import java.net.URL;


@Builder
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class NotifyDTO extends AbstractBaseDTO {

    private Long id;
    private String title;
    private String message;
    private Notify.Priority priority;
    private URL target;
    private Long userId;
    private boolean read;

}
