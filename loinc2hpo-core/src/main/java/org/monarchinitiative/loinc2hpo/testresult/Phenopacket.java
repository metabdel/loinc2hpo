package org.monarchinitiative.loinc2hpo.testresult;

import org.hl7.fhir.dstu3.model.Patient;

import java.util.Set;

public interface Phenopacket {

    Patient getPatient();

    Set<LabTestResultInHPO> getPhenotypes();
}
