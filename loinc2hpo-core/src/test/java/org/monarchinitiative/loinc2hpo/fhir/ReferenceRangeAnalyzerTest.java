package org.monarchinitiative.loinc2hpo.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.base.composite.BaseResourceReferenceDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.codesystems.*;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Test;

import javax.security.auth.Subject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


import static org.junit.Assert.*;

public class ReferenceRangeAnalyzerTest {

    public static String BASEURL = "http://fhirtest.uhn.ca/baseDstu3";
    public static final FhirContext ctx = FhirContext.forDstu3();
    static final IGenericClient client = ctx.newRestfulGenericClient(BASEURL);
    public static final IParser jsonParser = ctx.newJsonParser();

    String patientPath = ReferenceRangeAnalysis.class.getClassLoader().getResource("json/patient/generalPatient.fhir").getPath();
    Patient patient1 = FhirResourceRetriever.parseJsonFile2Patient(patientPath);


    @Test
    public void testCreatePatient() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/y");

        Patient james = new Patient();
        james.setId("Patient/patient_007");
        james.addIdentifier().setSystem("UConn Health Center").setValue("007");
        james.setActive(true);

        james.addName().setFamily("Bond").addGiven("James");
        try {
            Period use007 = new Period()
                    .setStart(simpleDateFormat.parse("12/01/1988"))
                    .setEnd(simpleDateFormat.parse("05/01/1999"));
            james.addName().setUse(HumanName.NameUse.NICKNAME).addGiven("007").setPeriod(use007);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        james.setGender(Enumerations.AdministrativeGender.MALE);


        Date birthday = null;
        try {
            birthday = simpleDateFormat.parse("05/13/1977");
            james.setBirthDate(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Address home = new Address();
        home.setCity("New York City").setState("New York").setCountry("USA").setPostalCode("10001");
        Address office = new Address();
        office.setCity("London").setCountry("UK").setPostalCode("01003");
        List<Address> addresses = Arrays.asList(home, office);
        james.setAddress(addresses);
        Relationship relationship = new RelationshipEnumFactory().fromCode("4");
        Coding relationCoding = new Coding();
        relationCoding.setSystem(relationCoding.getSystem()).setCode(relationship.toCode());
        CodeableConcept relation = new CodeableConcept();
        relation.addCoding(relationCoding);
        james.addContact().addRelationship(relation);

        james.addTelecom().setUse(ContactPoint.ContactPointUse.MOBILE).setValue("1-005-886-3034");
        Coding married = new Coding();
        married.setCode("1");
        married.setSystem("http://us.gov");
        married.setDisplay("married");
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding(married);
        james.setMaritalStatus(codeableConcept);

        james.setDeceased(new BooleanType(false));

        String jamesJson = jsonParser.setPrettyPrint(true).encodeResourceToString(james);
        System.out.println(jamesJson);

    }

    @Test
    public void testCreateObservation() throws Exception {

        Observation glucose = new Observation();
        glucose.setId("f001");

        Identifier identifier = new Identifier();
        identifier.setSystem("http://uconnhealth.edu/observations").setValue("ob0000000000005").setUse(Identifier.IdentifierUse.OFFICIAL);
        glucose.addIdentifier(identifier);

        glucose.setStatus(Observation.ObservationStatus.FINAL);

        Coding loinc = new Coding().setSystem("http://loinc.org").setCode("15074-8").setDisplay("glucose test");
        Coding snomed = new Coding().setSystem("http://snomed.gov").setCode("CONCEPT00004").setDisplay("snomed glucose");
        glucose.setCode(new CodeableConcept().addCoding(loinc).addCoding(snomed));

        glucose.getSubject().setReference("Patient/patient_007").setDisplay("J. Bond");

        Calendar effective = Calendar.getInstance();
        effective.setTimeZone(TimeZone.getTimeZone(ZoneId.of("-4")));
        effective.set(2017, Calendar.NOVEMBER, 5, 10, 30, 0);
        //glucose.setEffective(new DateTimeType(effective));
        Period perid = new Period();
        Calendar start = Calendar.getInstance();
        start.setTimeZone(TimeZone.getDefault());
        start.set(2007, Calendar.MARCH, 23, 10, 30, 0);
        Calendar end = Calendar.getInstance();
        end.setTimeZone(TimeZone.getDefault());
        end.set(2017, Calendar.NOVEMBER, 23, 23, 59, 59);
        perid.setStart(start.getTime()).setEnd(end.getTime());
        glucose.setEffective(perid);

        Reference performer = new Reference();
        performer.setReference("Nurse/beauty");
        glucose.addPerformer(performer);

        Quantity value = new Quantity();
        value.setValue(6.3).setUnit("mmol/L");
        value.setSystem("http://unitsofmeasure.org").setCode("mmol/L");
        glucose.setValue(value);

        CodeableConcept interpretation = new CodeableConcept();
        Coding high = new Coding().setCode(V3ObservationInterpretation.H.toCode()).setSystem(V3ObservationInterpretation.H.getSystem());
        interpretation.addCoding(high);
        glucose.setInterpretation(interpretation);

        Observation.ObservationReferenceRangeComponent reference = new Observation.ObservationReferenceRangeComponent();
        SimpleQuantity low = new SimpleQuantity();
        low.setValue(3.1).setUnit("mmol/l").setSystem("http://unitsofmeasure.org").setCode("mmol/L");
        SimpleQuantity hi = new SimpleQuantity();
        hi.setValue(6.2).setUnit("mmol/l").setSystem("http://unitsofmeasure.org").setCode("mmol/L");
        //Range range = new Range();
        //range.setHigh(hi);
        //range.setLow(low);
        reference.setHigh(hi);
        reference.setLow(low);
        Coding sex = new Coding().setCode(AdministrativeGender.MALE.toCode()).setSystem(AdministrativeGender.MALE.getSystem());
        reference.addAppliesTo(new CodeableConcept().addCoding(sex));
        Coding race = new Coding();
        race.setCode("2106-3").setSystem(new V3RaceEnumFactory().fromCode("2106-3").getSystem());
        reference.addAppliesTo(new CodeableConcept().addCoding(race));
        Coding nationality = new Coding().setDisplay("UK");
        Extension extension = new Extension();
        extension.setUrl("http://uconn.edu/nationality").setValue(new StringType("UK"));
        nationality.addExtension(extension);
        reference.addAppliesTo(new CodeableConcept().addCoding(nationality));
        Range ageRange = new Range();
        SimpleQuantity ageL = new SimpleQuantity();
        ageL.setValue(50.0).setUnit("Year").setCode("Y").setSystem("http://randomeurl.com");
        SimpleQuantity ageH = new SimpleQuantity();
        ageH.setValue(99.0).setUnit("Year").setCode("Y").setSystem("http://randomeurl.com");
        ageRange.setLow(ageL).setHigh(ageH);
        //CodeableConcept agethreshold = new CodeableConcept();
        //agethreshold.addCoding()




        glucose.addReferenceRange(reference);


        System.out.println(jsonParser.setPrettyPrint(true).encodeResourceToString(glucose));

        assertEquals(true, glucose.hasEffective());
        assertEquals(true, glucose.getEffectivePeriod().hasStart());
        assertEquals(true, glucose.getEffectivePeriod().hasStartElement());

        System.out.println(glucose.getEffectivePeriod().getStart());

    }


    @Test
    public void testGetDate1() throws Exception {

        String path2observation1 = ReferenceRangeAnalysis.class.getClassLoader().getResource("json/glucoseHigh.fhir").getPath();
        Observation observation1 = FhirResourceRetriever.parseJsonFile2Observation(path2observation1);

        assertNotNull(observation1);
        ReferenceRangeAnalyzer analyzer1 = new ReferenceRangeAnalyzer(null, observation1);
        assertNotNull(analyzer1.getTestDate());
        Date now = new Date();
        assertEquals(true, analyzer1.getTestDate().before(now));
        //System.out.println(analyzer1.getTestDate());



    }

    @Test
    public void getTestDate15() throws Exception {




    }

    @Test
    public void getTestDate2() throws Exception {
    }

    @Test
    public void getTestDate3() throws Exception {
    }

    @Test
    public void getTestDate4() throws Exception {
    }

    @Test
    public void getTestDate5() throws Exception {

        String path2observation1 = ReferenceRangeAnalysis.class.getClassLoader().getResource("json/ecoli.fhir").getPath();
        Observation observation1 = FhirResourceRetriever.parseJsonFile2Observation(path2observation1);

        assertNotNull(observation1);
        ReferenceRangeAnalyzer analyzer1 = new ReferenceRangeAnalyzer(null, observation1);
        assertNotNull(analyzer1.getTestDate());
        Date now = new Date();
        assertEquals(true, analyzer1.getTestDate().before(now));
        System.out.println(analyzer1.getTestDate());
    }

    @Test
    public void getObservationsFromTestServer() {
        List<Observation> observationList = new ArrayList<>();

        Bundle observationBundle = client.search().forResource(Observation.class)
                .prettyPrint()
                .returnBundle(Bundle.class)
                .execute();

        System.out.println("bundle size: " + observationBundle.getEntry().size());
        while(true) {
            observationBundle.getEntry()
                    .forEach(p -> observationList.add((Observation) p.getResource()));
            if (observationBundle.getLink(IBaseBundle.LINK_NEXT ) != null) {
                observationBundle = client.loadPage().next(observationBundle).execute();
            } else {
                break;
            }
        }

        System.out.println(observationList.size());
    }

    @Test
    public void testDuration() {
        Calendar fromC = Calendar.getInstance();
        fromC.setTimeZone(TimeZone.getDefault());
        fromC.set(2016, Calendar.JUNE, 26, 4, 00, 00);
        DateTimeType from = new DateTimeType(fromC.getTime());
        Calendar nowC = Calendar.getInstance();
        nowC.setTimeZone(TimeZone.getDefault());
        nowC.set(2017, Calendar.JUNE, 26, 4, 00, 00);
        DateTimeType now = new DateTimeType(nowC);
        long duration = (long) ((now.getValue().getTime() - from.getValue().getTime())/(1000.0 * 60*60*24));
        System.out.println("duration: " + duration);

    }
    @Test
    public void getAge() throws Exception {



    }

    @Test
    public void getSex() throws Exception {
    }

    @Test
    public void getAddress() throws Exception {
    }

    @Test
    public void getRace() throws Exception {
    }

    @Test
    public void getApplicableReferenceRange() throws Exception {
    }

    @Test
    //needs Java EE
    public void sendEmail() {
        String to = "kingmanzhang@gmail.com";
        String from = "najanajaking@gmail.com";
        String host = "localhost";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        //Session session =//
    }

}