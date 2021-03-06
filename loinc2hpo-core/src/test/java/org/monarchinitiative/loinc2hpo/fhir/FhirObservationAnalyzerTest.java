package org.monarchinitiative.loinc2hpo.fhir;


import com.google.common.collect.ImmutableMap;
import org.hl7.fhir.dstu3.model.Observation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.loinc2hpo.testresult.LabTestOutcome;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FhirObservationAnalyzerTest {

    private static Observation observation;
    private static Map<String, Term> hpoTermMap;

    @BeforeClass
    public static void setup() throws Exception{
        String path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseHigh.fhir").getPath();
        observation = FhirResourceRetriever.parseJsonFile2Observation(path);

        String hpo_obo = FhirObservationAnalyzerTest.class.getClassLoader().getResource("obo/hp.obo").getPath();
        Ontology hpo = OntologyLoader.loadOntology(new File(hpo_obo));
        ImmutableMap.Builder<String,Term> termmap = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<Term> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> termmap.put(term.getName(),term));
        }
        hpoTermMap = termmap.build();
        assertNull(FhirObservationAnalyzer.getObservation());
    }

    @Test
    public void setObservation() throws Exception {

        //assertNull(FhirObservationAnalyzer.getObservation());
        FhirObservationAnalyzer.setObservation(observation);
        assertNotNull(FhirObservationAnalyzer.getObservation());
    }

    @Test
    public void getHPO4ObservationOutcome() throws Exception {
        FhirObservationAnalyzer.setObservation(observation);
    }

    @Test
    public void getLoincIdOfObservation() throws Exception {

        FhirObservationAnalyzer.setObservation(observation);
        LoincId loincId = FhirObservationAnalyzer.getLoincIdOfObservation();
        assertEquals("15074-8", loincId.toString());

    }


    @Test
    public void testUniversalAnnotation() throws Exception {

        FhirObservationAnalyzer.setObservation(observation);

        Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();
        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        TermId low = hpoTermMap.get("Hypoglycemia").getId();
        TermId normal = hpoTermMap.get("Abnormality of blood glucose concentration").getId();
        TermId hi = hpoTermMap.get("Hyperglycemia").getId();

        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        /**
        LOINC2HpoAnnotationImpl glucoseAnnotation = new LOINC2HpoAnnotationImpl(loincId, loincScale);
        glucoseAnnotation.addAnnotation(internalCodes.get("L"), new HpoTerm4TestOutcome(low, false))
                .addAnnotation(internalCodes.get("N"), new HpoTerm4TestOutcome(normal, true))
                .addAnnotation(internalCodes.get("A"), new HpoTerm4TestOutcome(normal, false))
                .addAnnotation(internalCodes.get("H"), new HpoTerm4TestOutcome(hi, false));
         **/
        LOINC2HpoAnnotationImpl glucoseAnnotation = new LOINC2HpoAnnotationImpl.Builder()
                .setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(hpoTermMap.get("Hypoglycemia").getId())
                .setIntermediateValueHpoTerm(hpoTermMap.get("Abnormality of blood glucose concentration").getId())
                .setHighValueHpoTerm(hpoTermMap.get("Hyperglycemia").getId())
                .setIntermediateNegated(true)
                .build();

        testmap.put(loincId, glucoseAnnotation);

        Set<LoincId> loincIdSet = new HashSet<>();
        loincIdSet.add(loincId);
        LabTestOutcome result = FhirObservationAnalyzer.getHPO4ObservationOutcome(loincIdSet, testmap);
        System.out.println(result);

    }


    @Test
    public void getHPOFromRawValue() throws Exception {
    }

    @Test
    public void getHPOFromCodedValue() throws Exception {
    }

}