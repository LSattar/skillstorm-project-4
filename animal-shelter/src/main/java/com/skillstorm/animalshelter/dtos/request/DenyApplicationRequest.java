package com.skillstorm.animalshelter.dtos.request;

public class DenyApplicationRequest {

    private String decisionNotes;

    public DenyApplicationRequest() {
    }

    public String getDecisionNotes() {
        return decisionNotes;
    }

    public void setDecisionNotes(String decisionNotes) {
        this.decisionNotes = decisionNotes;
    }
}
