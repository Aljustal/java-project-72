package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;

@Entity
public class Url extends Model {
    @Id
    private long  id;
    private String name;
    @WhenCreated
    private Instant createdAt;
    @OneToMany (cascade = CascadeType.ALL)
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
    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<UrlCheck> getUrlChecks() {
        return urlChecks;
    }

    public Instant getLastCheckDate() {
        if (!urlChecks.isEmpty()) {
            return urlChecks.get(urlChecks.size() - 1).getCreatedAt();
        }
        return null;
    }
    public Integer getLastCheckStatus() {
        if (!urlChecks.isEmpty()) {
            return urlChecks.get(urlChecks.size() - 1).getStatusCode();
        }
        return null;
    }
}
