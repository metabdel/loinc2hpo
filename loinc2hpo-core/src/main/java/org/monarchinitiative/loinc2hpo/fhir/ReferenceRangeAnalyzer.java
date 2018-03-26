package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.*;

public class ReferenceRangeAnalyzer implements ReferenceRangeAnalysis {


    @Override
    public Age getAge() {
        return null;
    }

    @Override
    public Enumerations.AdministrativeGender getSex() {
        return null;
    }

    @Override
    public Address getAddress() {
        return null;
    }

    @Override
    public CodeableConcept getRace() {
        return null;
    }

    @Override
    public Observation.ObservationReferenceRangeComponent getApplicableReferenceRange() {
        return null;
    }
}
