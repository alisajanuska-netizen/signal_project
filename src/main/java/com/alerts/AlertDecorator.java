package com.alerts;

public class AlertDecorator extends Alert {
    private Alert alert;

    public AlertDecorator(Alert alert) {
        super(alert.getPatientId(), alert.getCondition(), alert.getTimestamp());
        this.alert = alert;
    }

    @Override
    public String getPatientId() {
        return alert.getPatientId();
    }

    @Override
    public String getCondition() {
        return alert.getCondition();
    }

    @Override
    public long getTimestamp() {
        return alert.getTimestamp();
    }
}
