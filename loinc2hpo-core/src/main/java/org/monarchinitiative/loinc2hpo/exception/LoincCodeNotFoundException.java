package org.monarchinitiative.loinc2hpo.exception;

public class LoincCodeNotFoundException extends ObservationFieldNotFoundException {

    public LoincCodeNotFoundException() {
        super();
    }
    public LoincCodeNotFoundException(String msg) {
        super(msg);
    }
}
