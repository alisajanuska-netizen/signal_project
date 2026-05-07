package com.alerts;

public class RepeatedAlertDecorator extends AlertDecorator {
    public RepeatedAlertDecorator(Alert alert) {
        super(alert);
    }

    @Override
    public String getCondition() {
        return "REPEATED: " + super.getCondition();
    }
}
