package org.monarchinitiative.loinc2hpo.codesystems;

@FunctionalInterface
public interface CodeConverter {

    Code convertToInternalCode(Code code);

}
