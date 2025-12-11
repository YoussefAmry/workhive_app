package com.workhive.notificationservice.soap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificationResponse", propOrder = {
        "id",
        "userId",
        "message",
        "type",
        "isRead",
        "timestamp",
        "success",
        "errorMessage"
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    @XmlElement
    private Long id;

    @XmlElement
    private Long userId;

    @XmlElement
    private String message;

    @XmlElement
    private String type;

    @XmlElement
    private Boolean isRead;

    @XmlElement
    private String timestamp;

    @XmlElement(required = true)
    private Boolean success;

    @XmlElement
    private String errorMessage;
}
