package com.workhive.notificationservice.soap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificationRequest", propOrder = {
        "userId",
        "message",
        "type"
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @XmlElement(required = true)
    private Long userId;

    @XmlElement(required = true)
    private String message;

    @XmlElement
    private String type;
}
