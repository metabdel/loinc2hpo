package org.monarchinitiative.loinc2hpo.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Observation;
import org.junit.Test;

import static org.junit.Assert.*;

public class FhirObservationRetrieverTest {




    @Test
    public void testParseJsonFile2Observation(){

        String path = getClass().getClassLoader().getResource("json/glucoseHigh.fhir").getPath();
        Observation observation = FhirResourceRetriever.parseJsonFile2Observation(path);
        assertNotNull(observation);
        assertEquals("Observation", observation.getResourceType().toString());
    }

    @Test
    public void testParseJsonFile2ObservationException(){
        String path = getClass().getClassLoader().getResource("json/malformedObservation.fhir").getPath();
        Observation observation = FhirResourceRetriever.parseJsonFile2Observation(path);
        assertNull(observation);
    }

    @Test
    public void retrieveObservationFromServer() throws Exception {

    }

}