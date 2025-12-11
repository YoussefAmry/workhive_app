package com.workhive.notificationservice.soap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetNotificationsRequest", propOrder = {
        "userId"
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "getNotificationsRequest", namespace = "http://workhive.com/notification")
public class GetNotificationsRequest {

    @XmlElement(required = true)
    private Long userId;
}
