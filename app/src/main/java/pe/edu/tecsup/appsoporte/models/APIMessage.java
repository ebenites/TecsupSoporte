package pe.edu.tecsup.appsoporte.models;

/**
 * Created by ebenites on 31/01/2017.
 */

public class APIMessage {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "APIMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
