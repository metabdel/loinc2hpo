package org.monarchinitiative.loinc2hpo.testresult;

import org.jgrapht.alg.util.UnionFind;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotation;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PhenoSetUnionFind {
    private Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;
    private UnionFind<TermId> unionFind;

    public PhenoSetUnionFind(Set<TermId> hpoTermSet, Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap) {
        this.annotationMap = annotationMap;
        this.unionFind = new UnionFind<>(hpoTermSet);
        union(annotationMap);
    }

    private void union(Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap) {
        annotationMap.values().forEach(annotation -> {
            List<TermId> termInAnnot = annotation.getCandidateHpoTerms().values()
                    .stream()
                    .map(HpoTerm4TestOutcome::getId)
                    .distinct()
                    .collect(Collectors.toList());
            if (!termInAnnot.isEmpty() && termInAnnot.size() > 1) {
                //union terms in this list
                //after this, all terms that have shared LOINC tests are unioned
                TermId firstTerm = termInAnnot.get(0);
                termInAnnot.stream().skip(1).forEach(a -> this.unionFind.union(firstTerm, a));
            }
        });
    }

    public UnionFind<TermId> getUnionFind() {
        return this.unionFind;
    }

}
