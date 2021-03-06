package org.monarchinitiative.loinc2hpo.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.valueset.BundleTypeEnum;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.*;
import org.junit.Ignore;
import org.junit.Test;


import java.io.File;

import static org.junit.Assert.*;

public class FhirObservationRetrieverTest {
    @Test
    public void testParseJsonFile2Observation() throws Exception{

        String path = getClass().getClassLoader().getResource("json/glucoseHigh.fhir").getPath();
        Observation observation = (Observation) new FhirResourceParserDstu3().parse(new File(path));
        assertNotNull(observation);
        assertEquals("Observation", observation.getResourceType().toString());
    }

    @Test (expected = DataFormatException.class)
    public void testParseJsonFile2ObservationException() throws Exception{
        String path = getClass().getClassLoader().getResource("json/malformedObservation.fhir").getPath();
        Observation observation = (Observation) new FhirResourceParserDstu3().parse(path);
    }

    @Test
    @Ignore
    public void testUploadBundle() {
        // Create a patient object
        Patient patient = new Patient();
        patient.addIdentifier()
                .setSystem("http://acme.org/mrns")
                .setValue("12345");
        patient.addName()
                .setFamily("Jameson")
                .addGiven("J")
                .addGiven("Jonah");
        patient.setGender(Enumerations.AdministrativeGender.MALE);

// Give the patient a temporary UUID so that other resources in
// the transaction can refer to it
        patient.setId(IdDt.newRandomUuid());

// Create an observation object
        Observation observation = new Observation();
        observation.setStatus(Observation.ObservationStatus.FINAL);
        observation
                .getCode()
                .addCoding()
                .setSystem("http://loinc.org")
                .setCode("789-8")
                .setDisplay("Erythrocytes [#/volume] in Blood by Automated count");
        observation.setValue(
                new SimpleQuantity()
                        .setValue(4.12)
                        .setUnit("10 trillion/L")
                        .setSystem("http://unitsofmeasure.org")
                        .setCode("10*12/L"));

// The observation refers to the patient using the ID, which is already
// set to a temporary UUID
        observation.setSubject(new Reference(patient.getId()));

// Create a bundle that will be used as a transaction
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.TRANSACTION);

// Add the patient as an entry. This entry is a POST with an
// If-None-Exist header (conditional create) meaning that it
// will only be created if there isn't already a Patient with
// the identifier 12345
        bundle.addEntry()
                .setFullUrl(patient.getId())
                .setResource(patient)
                .getRequest()
                .setUrl("Patient")
                //.setIfNoneExist("identifier=http://acme.org/mrns|12345")
                .setMethod(Bundle.HTTPVerb.POST);

// Add the observation. This entry is a POST with no header
// (normal create) meaning that it will be created even if
// a similar resource already exists.
        bundle.addEntry()
                .setResource(observation)
                .getRequest()
                .setUrl("Observation")
                .setMethod(Bundle.HTTPVerb.POST);

        FhirResourceParser parser = new FhirResourceParserDstu3();
        System.out.println(parser.toJson(patient));
        System.out.println(parser.toJson(observation));
        System.out.println(parser.toJson(bundle));

        // Create a client and post the transaction to the server
        // Log the request
        FhirContext ctx = FhirContext.forDstu3();
        IGenericClient client = ctx.newRestfulGenericClient(org.monarchinitiative.loinc2hpo.Constants.HAPIFHIRTESTSERVER);
        Bundle resp = client.transaction().withBundle(bundle).execute();

// Log the response
        System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));


    }

}