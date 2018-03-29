package org.monarchinitiative.loinc2hpo.fhir;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.exception.*;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.Loinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;

import java.util.*;
import java.util.stream.Collectors;

public class ObservationAnalysisFromInterpretation implements ObservationAnalysis {

    private static final Logger logger = LogManager.getLogger();
    private LoincId loincId;
    private CodeableConcept interpretationField;  //this is the interpretation field of a fhir loinc observation
    private Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap; //this is the annotation map that we need to interpret the result

    public ObservationAnalysisFromInterpretation(LoincId loincId, CodeableConcept interpretation, Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap) {
        this.loincId = loincId;
        this.interpretationField = interpretation;
        this.annotationMap = annotationMap;
    }

    public Set<Code> getInterpretationCodes() {
        Set<Code> interpretationCodes = new HashSet<>();
        interpretationField.getCoding().forEach(x -> {
            Code interpretationcode = Code.getNewCode()
                    .setSystem(x.getSystem())
                    .setCode(x.getCode());
            interpretationCodes.add(interpretationcode);
        });
        return interpretationCodes;
    }


    /**
     * This function checks whether the interpretation code has a manually annotated HPO term (it happens when the curator overwrites the default HPO term mapped from internal codes.
     * @return
     */
    private boolean isInterpretationCodeManuallyMapped(Set<Code> interpretationCodes, UniversalLoinc2HPOAnnotation annotation){

        Map<Code, HpoTermId4LoincTest> advancedAnnotationMap = annotation.getAdvancedAnnotationTerms();
        for (Code code : interpretationCodes) {
            if (advancedAnnotationMap.containsKey(code)) {
                return true;
            }
        }
        return false;

    }

    @Override
    public HpoTermId4LoincTest getHPOforObservation() throws UnsupportedCodingSystemException,
            AmbiguousResultsFoundException, AnnotationNotFoundException, UnrecognizedCodeException {

        //get the annotation class for this loinc code
        UniversalLoinc2HPOAnnotation annotationForLoinc = annotationMap.get(this.loincId);
        if (annotationForLoinc == null) throw new AnnotationNotFoundException();
        //get all interpretation codes in in an observation. Expect one in most cases.
        Set<Code> interpretationCodes = getInterpretationCodes();

        //first check whether the interpretation codes have been manually mapped
        if (isInterpretationCodeManuallyMapped(interpretationCodes, annotationForLoinc)) {
            Set<HpoTermId4LoincTest> outcome = interpretationCodes.stream()
                    .filter(p -> annotationForLoinc.getAdvancedAnnotationTerms().containsKey(p))
                    .map(p -> annotationForLoinc.getAdvancedAnnotationTerms().get(p))
                    .distinct()
                    .collect(Collectors.toSet());
            if (outcome.size() == 1) {
                return outcome.iterator().next();
            } else if (outcome.size() > 1) {
                throw new AmbiguousResultsFoundException();
            }
            //move on to the following code
        }

        //here we use a map to store the results: since there could be more than one interpretation coding system,
        //we try them all and store the results in a map <external code, result in internal code>
        List<Code> distinct = interpretationCodes.stream()
                //filter out interpretation codes whose coding system are not mapped by us
                .filter(p -> CodeSystemConvertor.getCodeContainer().getCodeSystemMap().containsKey(p.getSystem()))
                .map(p -> {
                    Code internalCode = null;
                    try {
                        internalCode = CodeSystemConvertor.convertToInternalCode(p);
                    } catch (InternalCodeNotFoundException e) {
                        logger.error("This should not happen because we have tested the condition");
                    }
                    return  internalCode;
                })
                .distinct().collect(Collectors.toList());
        if (distinct.size() == 1) {
            HpoTermId4LoincTest hpoTermId4LoincTest = annotationForLoinc.loincInterpretationToHPO(distinct.get(0));
            if (hpoTermId4LoincTest == null) throw new UnrecognizedCodeException();
            return hpoTermId4LoincTest;
        } else {
            throw new AmbiguousResultsFoundException();
        }
    }

}
