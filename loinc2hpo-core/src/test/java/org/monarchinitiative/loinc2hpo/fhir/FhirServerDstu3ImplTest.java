package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.Constants;

import java.util.List;

import static org.junit.Assert.*;

public class FhirServerDstu3ImplTest {

    FhirServer fhirServer = new FhirServerDstu3Impl(Constants.HAPIFHIRTESTSERVER);

    @Test
    public void restifulGenericClient() throws Exception {
    }

    @Test
    public void getBaseAddress() throws Exception {
    }

    @Test
    @Ignore
    public void getPatient() throws Exception {
    }

    @Test
    @Ignore
    public void getPatient1() throws Exception {
    }

    @Test
    @Ignore
    public void getPatient2() throws Exception {

        Patient patient = fhirServer.getPatient("2959435").get(0);
        FhirResourceParser parser = new FhirResourceParserDstu3();
        parser.setPrettyPrint(true);
        //System.out.println(parser.toJson(patient));
        System.out.println(patient.getIdElement().getIdPart());

        List<Observation> observationList = fhirServer.getObservation(patient);
        assertFalse(observationList.isEmpty());
        assertEquals(10, observationList.size());
        //observationList.stream().map(parser::toJson).forEach(System.out::println);

    }

    @Test
    @Ignore
    public void getPatient3() throws Exception {
        List<Patient> patient = fhirServer.getPatient("Harry", "Metz", "002-837-6481", "79442-0781");
        assertTrue(!patient.isEmpty());
    }

    @Test
    @Ignore
    public void getObservation() throws Exception {
    }

    @Test
    @Ignore
    public void upload() throws Exception {
    }

    @Test
    @Ignore
    public void upload1() throws Exception {
    }

}