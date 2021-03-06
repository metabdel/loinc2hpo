package org.monarchinitiative.loinc2hpo.testresult;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.SharedResourceCollection;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Ignore
/**
 * Only manually run this test
 */
public class PhenoSetUnionFindTest {

    private static Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();
    private static Map<String, Term> hpoTermMap;
    private static Map<TermId, Term> hpoTermMap2;
    private static Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;
    private static PhenoSetUnionFind unionFind;

    @BeforeClass
    public static void setup() throws Exception{
        ResourceCollection resourceCollection = SharedResourceCollection.resourceCollection;

        hpoTermMap = resourceCollection.hpoTermMapFromName();
        hpoTermMap2 = resourceCollection.hpoTermMap();
        Ontology hpo = resourceCollection.getHPO();
        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = resourceCollection.annotationMap();

        unionFind = new PhenoSetUnionFind(hpo.getTermMap().values().stream().map(Term::getId).collect(Collectors.toSet()), annotationMap);

    }

    @Test @Ignore
    public void test1() throws Exception {
        Term term1 = hpoTermMap.get("Hypocapnia");
        Term term2 = hpoTermMap.get("Hypercapnia");
        assertTrue(unionFind.getUnionFind().inSameSet(term1.getId(), term2.getId()));
        Term term3 = hpoTermMap.get("Nitrituria");
        assertFalse(unionFind.getUnionFind().inSameSet(term1.getId(), term3.getId()));
    }

}