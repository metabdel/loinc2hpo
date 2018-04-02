package org.monarchinitiative.loinc2hpo.util;

import net.sf.cglib.core.Local;
import org.apache.jena.base.Sys;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.junit.Test;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class TimeDurationTest {

    @Test
    public void testZoneConversion() {
        Calendar from = Calendar.getInstance();
        from.setTimeZone(TimeZone.getDefault());
        from.setTime(new Date());

        DateTimeType now = new DateTimeType(from.getTime());
        TimeZone zone = now.getTimeZone();

        System.out.println("now: " + now);
        System.out.println("timezone: " + zone);

        //ZoneId.getAvailableZoneIds().forEach(System.out::println);
        Calendar to = Calendar.getInstance();
        to.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        to.setTime(new Date());

        DateTimeType nowInBeijing = new DateTimeType(to.getTime());
        System.out.println("now at beijing: " + nowInBeijing);
        System.out.println("timezone: " + nowInBeijing.getTimeZone());

    }

    @Test
    public void testPeriod() {
        LocalDate from = LocalDate.of(2017, Month.MARCH, 30);
        //from.atTime(Off)
        LocalDate to = LocalDate.now();
        Period period = Period.between(from, to);
        System.out.println(period.getYears());

    }
    @Test
    public void getDuration() throws Exception {
    }

    @Test
    public void getDurationInDays() throws Exception {
    }

    @Test
    public void getDurationInWeeks() throws Exception {
    }

    @Test
    public void getDurationInMonths() throws Exception {
    }

    @Test
    public void getDurationInYears() throws Exception {
    }

}