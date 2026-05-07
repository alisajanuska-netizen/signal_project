package com.alerts;

public class PriorityAlertDecorator extends AlertDecorator {
    public PriorityAlertDecorator(Alert alert) {
        super(alert);
    }

    @Override
    public String getCondition() {
        return "HIGH PRIORITY: " + super.getCondition();
    }
}
