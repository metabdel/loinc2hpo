package org.monarchinitiative.loinc2hpo.fhir;


import com.google.common.collect.ImmutableMap;
import org.hl7.fhir.dstu3.model.Observation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.exception.ReferenceNotFoundException;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import static org.junit.Assert.*;

public class ObservationAnalysisFromQnValueTest {
    private static Observation[] observations = new Observation[2];
    private static Map<String, Term> hpoTermMap;
    private static Map<TermId, Term> hpoTermMap2;
    private static Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();


    @BeforeClass
    public static void setup() throws MalformedLoincCodeException, IOException, DataFormatException {
        String path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseHighNoInterpretation.fhir").getPath();
        Observation observation1 = FhirResourceRetriever.parseJsonFile2Observation(path);
        path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseNoInterpretationNoReference.fhir").getPath();
        Observation observation2 = FhirResourceRetriever.parseJsonFile2Observation(path);
        observations[0] = observation1;
        observations[1] = observation2;

        String hpo_obo = FhirObservationAnalyzerTest.class.getClassLoader().getResource("obo/hp.obo").getPath();
        Ontology hpo = null;

        hpo = OntologyLoader.loadOntology(new File(hpo_obo));

        ImmutableMap.Builder<String,Term> termmap = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<TermId,Term> termmap2 = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<Term> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> {
                termmap.put(term.getName(),term);
                termmap2.put(term.getId(), term);
            });
        }
        hpoTermMap = termmap.build();
        hpoTermMap2 = termmap2.build();


        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        Term low = hpoTermMap.get("Hypoglycemia");
        Term normal = hpoTermMap.get("Abnormality of blood glucose concentration");
        Term hi = hpoTermMap.get("Hyperglycemia");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(low.getId())
                .setIntermediateValueHpoTerm(normal.getId())
                .setIntermediateNegated(true)
                .setHighValueHpoTerm(hi.getId());

        LOINC2HpoAnnotationImpl annotation15074 = loinc2HpoAnnotationBuilder.build();


        testmap.put(loincId, annotation15074);

        loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        loincId = new LoincId("600-7");
        loincScale = LoincScale.string2enum("Nom");
        Term forCode1 = hpoTermMap.get("Recurrent E. coli infections");
        Term forCode2 = hpoTermMap.get("Recurrent Staphylococcus aureus infections");
        Term positive = hpoTermMap.get("Recurrent bacterial infections");

        Code code1 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
        Code code2 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setHighValueHpoTerm(positive.getId())
                .addAdvancedAnnotation(code1, new HpoTerm4TestOutcome(forCode1.getId(), false))
                .addAdvancedAnnotation(code2, new HpoTerm4TestOutcome(forCode2.getId(), false));

        LOINC2HpoAnnotationImpl annotation600 = loinc2HpoAnnotationBuilder.build();

        testmap.put(loincId, annotation600);
    }

    @Test
    public void testGetInterpretationCodes1() throws Exception {

        LoincId loincId = new LoincId("15074-8");
        ObservationAnalysisFromQnValue analyzer = new ObservationAnalysisFromQnValue(loincId, observations[0], testmap);
        assertNotNull(analyzer.getHPOforObservation());
        System.out.println(analyzer.getHPOforObservation().getId());
    }


    @Test (expected = ReferenceNotFoundException.class)
    public void testGetInterpretationCodes2() throws Exception {

        LoincId loincId = new LoincId("15074-8");
        ObservationAnalysisFromQnValue analyzer = new ObservationAnalysisFromQnValue(loincId, observations[1], testmap);
        analyzer.getHPOforObservation();
    }

}