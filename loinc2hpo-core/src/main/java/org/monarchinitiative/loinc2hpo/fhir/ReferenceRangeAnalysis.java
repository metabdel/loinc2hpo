package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.*;

import java.util.List;

/**
 * Note: Patient does not have "race", which makes sense
 */
public interface ReferenceRangeAnalysis {

    Age getAge() throws Exception;

    Enumerations.AdministrativeGender getSex();

    List<Address> getAddress();

    CodeableConcept getRace();

    Observation.ObservationReferenceRangeComponent getApplicableReferenceRange();





}
