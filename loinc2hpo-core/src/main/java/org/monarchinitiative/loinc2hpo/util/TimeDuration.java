package org.monarchinitiative.loinc2hpo.util;

import org.hl7.fhir.dstu3.model.DateTimeType;

import java.util.Date;

public class TimeDuration {

    public enum TimeUnitEnum {
        YEAR,
        MONTH,
        WEEK,
        DAY
    }

    private Date from;
    private Date to;
    private long durationMill;

    public TimeDuration(DateTimeType from, DateTimeType to) {

        this.from = from.getValue();
        this.to = from.getValue();
        this.durationMill = this.to.getTime() - this.from.getTime();

    }

    public TimeDuration(Date from, Date to) {

        this.from = from;
        this.to = to;
        this.durationMill = this.to.getTime() - this.from.getTime();

    }

    public double getDuration(TimeUnitEnum timeUnit) {
        switch (timeUnit) {
            case YEAR:
                return getDurationInYears();
            case MONTH:
                return getDurationInMonths();
            case WEEK:
                return getDurationInWeeks();
            case DAY:
                return getDurationInDays();
            default:
                return Double.MIN_VALUE;

        }
    }

    public double getDurationInDays() {

        return this.durationMill / (1000.0 * 60 * 60 * 24);

    }

    public double getDurationInWeeks() {

        return this.durationMill / (1000.0 * 60 * 60 * 24 * 7);

    }

    public double getDurationInMonths() {

        return 0.0;
    }

    public double getDurationInYears() {

        return 0.0;
    }




}
