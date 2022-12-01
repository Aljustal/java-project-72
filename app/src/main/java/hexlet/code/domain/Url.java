package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
public class Url extends Model {
    @Id
    private long  id;
    private String name;
    @WhenCreated
    private Instant createdAt;
    @OneToMany
    private List<UrlCheck> urlChecks;
    public Url(String urlName) {
        this.name = urlName;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName (String urlName) {
        this.name = urlName;
    }

    public String getCreatedAt() {
        final String patternFORMAT = "dd.MM.yyyy HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patternFORMAT)
                .withZone(ZoneId.systemDefault());
        return formatter.format(this.createdAt);
    }

    public List<UrlCheck> getUrlChecks() {
        return urlChecks;
    }
}
