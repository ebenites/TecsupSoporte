package pe.edu.tecsup.appsoporte.models;

import com.orm.dsl.Table;

/**
 * Created by ebenites on 09/01/2017.
 */

@Table
public class User {

    /* Basic */
    private Long id;
    private String username;
    private String fullname;

    /* Google */
    private String gid;
    private String name;
    private String email;
    private String picture;

    /* JWT Token */
    private String token;

    /* Sitec */
    private String sede;
    private Integer role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", gid=" + gid +
                ", username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", picture='" + picture + '\'' +
                ", sede='" + sede + '\'' +
                ", role=" + role +
                ", token='" + token + '\'' +
                '}';
    }
}
