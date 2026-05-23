package com.ruttu.project_02_backend.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private boolean departureRecommendEnalbed;
    private int boardingAlertTime;
    private boolean ttsEnabled;
    private boolean briefingEnabled;
    private String briefingType;
    private String briefingTime;
}
