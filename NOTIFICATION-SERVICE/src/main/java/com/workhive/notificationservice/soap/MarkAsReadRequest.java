package com.workhive.notificationservice.soap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MarkAsReadRequest", propOrder = {
        "notificationId"
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "markAsReadRequest", namespace = "http://workhive.com/notification")
public class MarkAsReadRequest {

    @XmlElement(required = true)
    private Long notificationId;
}
