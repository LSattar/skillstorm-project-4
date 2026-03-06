package com.skillstorm.animalshelter.dtos.request;

public class ApproveApplicationRequest {

    private String decisionNotes;

    public ApproveApplicationRequest() {
    }

    public String getDecisionNotes() {
        return decisionNotes;
    }

    public void setDecisionNotes(String decisionNotes) {
        this.decisionNotes = decisionNotes;
    }
}
