package com.NET_SETTR.NET_SETTR.dto;

import java.time.LocalDateTime;

public class SubscriptionDetailsResponse {

    private boolean subscribed;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean expired;

    public SubscriptionDetailsResponse(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public SubscriptionDetailsResponse(
            boolean subscribed,
            String status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean expired
    ) {
        this.subscribed = subscribed;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expired = expired;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public boolean isExpired() {
        return expired;
    }
}
