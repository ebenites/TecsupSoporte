package pe.edu.tecsup.appsoporte.models;

/**
 * Created by ebenites on 9/07/2017.
 */
public class Weather {

    private String location;

    private String date;

    private Double temperature;

    private Integer humidity;

    private String condition;

    private Boolean isday;

    private String icon;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Boolean getIsday() {
        return isday;
    }

    public void setIsday(Boolean isday) {
        this.isday = isday;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", condition='" + condition + '\'' +
                ", isday=" + isday +
                ", icon='" + icon + '\'' +
                '}';
    }
}
