package hexlet.code;

import java.time.Instant;

public class Url {
    int id;
    String name;
    Instant createdAt;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName (String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
