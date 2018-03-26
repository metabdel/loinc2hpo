package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.*;

/**
 * Note: Patient does not have "race", which makes sense
 */
public interface ReferenceRangeAnalysis {

    Age getAge();

    Enumerations.AdministrativeGender getSex();

    Address getAddress();

    CodeableConcept getRace();

    Observation.ObservationReferenceRangeComponent getApplicableReferenceRange();





}
