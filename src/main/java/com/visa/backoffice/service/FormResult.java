package com.visa.backoffice.service;

import java.util.List;

public class FormResult {

    private final String message;
    private final List<Long> demandeIds;

    public FormResult(String message, List<Long> demandeIds) {
        this.message = message;
        this.demandeIds = demandeIds;
    }

    public String getMessage() {
        return message;
    }

    public List<Long> getDemandeIds() {
        return demandeIds;
    }
}
