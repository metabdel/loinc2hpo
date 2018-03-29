package org.monarchinitiative.loinc2hpo.fhir;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpo.exception.ObservationFieldNotFoundException;


import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReferenceRangeAnalyzer implements ReferenceRangeAnalysis {

    private Patient patient;
    private Observation observation;
    private static final Logger logger = LogManager.getLogger();

    public ReferenceRangeAnalyzer(Patient patient, Observation observation) {
        this.patient = patient;
        this.observation = observation;
    }

    public Date getTestDate() throws ObservationFieldNotFoundException {
        Date testDate = null;

        if (observation.hasEffectivePeriod()) {
            try {
                if (observation.getEffectivePeriod().hasStart()) {
                    testDate = observation.getEffectivePeriod().getStart();
                    logger.trace("Test 1 passed");
                } else if (observation.getEffectivePeriod().hasStartElement()) {
                    testDate = observation.getEffectivePeriod().getStartElement().getValue();
                    logger.trace("Test 15 passed");
                }
                return testDate;
            } catch (FHIRException e) {
                logger.trace("Should not happen 222");
            }

        }
        //first try to get test date from effective
        if (observation.hasEffectiveDateTimeType()) {
            try {
                testDate = observation.getEffectiveDateTimeType().getValue();
                logger.trace("Test 2 passed");
                return testDate;
            } catch (FHIRException e) {
                logger.trace("Should not happen 111");
            }
        }


        /**
        if (observation.hasIssued()) {
            testDate = observation.getIssued();
            logger.trace("Test 4 passed");
            return testDate;
        }
        if (observation.hasIssuedElement()) {
            testDate = observation.getIssuedElement().getValue();
            logger.trace("Test 5 passed");
            return testDate;
        }
         **/

        throw new ObservationFieldNotFoundException();
    }

    @Override
    public Age getAge() throws Exception {

        if (!patient.hasBirthDate()) {
            throw new Exception("No birthday in patient resource");
        }
        Age patientAge = null;
        Date birthday = patient.getBirthDate();
        Date testDate = getTestDate();
        long diff = TimeUnit.DAYS.convert(testDate.getTime() - birthday.getTime(), TimeUnit.MILLISECONDS);



        return patientAge;
    }

    @Override
    public Enumerations.AdministrativeGender getSex() {
        return patient.getGender();
    }

    @Override
    public List<Address> getAddress() {
        return patient.getAddress();
    }

    @Override
    public CodeableConcept getRace() {
        return null;
    }

    @Override
    public Observation.ObservationReferenceRangeComponent getApplicableReferenceRange() {
        return null;
    }
}
