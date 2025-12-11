package com.workhive.notificationservice.soap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetNotificationsResponse", propOrder = {
        "notifications",
        "totalCount"
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetNotificationsResponse {

    @XmlElement
    private List<NotificationResponse> notifications;

    @XmlElement
    private Integer totalCount;
}
