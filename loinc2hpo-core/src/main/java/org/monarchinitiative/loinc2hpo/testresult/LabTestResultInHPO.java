package org.monarchinitiative.loinc2hpo.testresult;

import com.github.phenomics.ontolib.ontology.data.TermId;

//@TODO: this interface and implementing class is too similar to HpoTermId4LoincTest

public interface LabTestResultInHPO {


    public TermId getTermId();

    public boolean isNegated();

}
