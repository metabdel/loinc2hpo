package org.monarchinitiative.loinc2hpo.codesystems;

/**
 * This is an class for coded values. This correspond to the Coding class in hapi-fhir with some modification (equal method)
 */
public class Code {
    private String system;
    private String code;
    private String display;
    private String definition;

    public Code(){

    }
    public Code(String system, String Code, String display){
        this.system = system;
        this.code = code;
        this.display = display;

    }
    public static Code getNewCode(){
        return new Code();
    }
    public String getSystem() {
        return system;
    }

    public Code setSystem(String system) {
        this.system = system;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Code setCode(String code) {
        this.code = code;
        return this;
    }

    public String getDisplay() {
        return display;
    }

    public Code setDisplay(String display) {
        this.display = display;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public Code setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Code) {
            Code other = (Code) obj;
            return other.getSystem().equals(this.system) && other.getCode().equals(this.code);
        }
        return false;
    }
    @Override
    public int hashCode(){

        return system.hashCode() + code.hashCode()*37;

    }

    @Override
    public String toString(){

        String toString = String.format("System: %s; Code: %s, Display: %s, Definition: %s", system, code, display, definition);
        return toString;

    }
}
