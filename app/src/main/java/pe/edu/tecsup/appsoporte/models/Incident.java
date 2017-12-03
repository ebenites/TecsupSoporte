package pe.edu.tecsup.appsoporte.models;

import java.util.Date;

public class Incident {

    private Integer id;

    private Integer customerid;

    private String customer;

    private String phone;

    private String sede;

    private String location;

    private String comments;

    private Date created;

    private Date updated;

    private Integer technicalid;

    private String technical;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerid() {
        return customerid;
    }

    public void setCustomerid(Integer customerid) {
        this.customerid = customerid;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Integer getTechnicalid() {
        return technicalid;
    }

    public void setTechnicalid(Integer technicalid) {
        this.technicalid = technicalid;
    }

    public String getTechnical() {
        return technical;
    }

    public void setTechnical(String technical) {
        this.technical = technical;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "id=" + id +
                ", customerid=" + customerid +
                ", customer='" + customer + '\'' +
                ", phone='" + phone + '\'' +
                ", sede='" + sede + '\'' +
                ", location='" + location + '\'' +
                ", comments='" + comments + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", technicalid=" + technicalid +
                ", technical='" + technical + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
