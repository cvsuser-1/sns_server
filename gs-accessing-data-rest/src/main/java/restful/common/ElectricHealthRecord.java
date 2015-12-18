package restful.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ElectricHealthRecord {

    @JsonIgnore
    @ManyToOne
    private Patient patient;

    @Id
    @GeneratedValue
    private Long id;

    ElectricHealthRecord() { // jpa only
    }

    public ElectricHealthRecord(Patient patient, String uri, String description) {
        this.uri = uri;
        this.description = description;
        this.patient = patient;
    }

    public String uri;
    public String description;

    public Patient getPatient() {
        return patient;
    }

    public Long getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public String getDescription() {
        return description;
    }
}