import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ConfiguracionApp extends Configuration {

    @JsonProperty
    @NotEmpty
    public String jwtKey = "";

    @NotEmpty
    public String adminEmail = "";

    @NotEmpty
    public String adminPass = "";

    @NotEmpty
    public String neverBounceKey = "";

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty
    public String getAdminEmail() {
        return adminEmail;
    }

    @JsonProperty
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    @JsonProperty
    public String getAdminPass() {
        return adminPass;
    }

    @JsonProperty
    public void setAdminPass(String adminPass) {
        this.adminPass = adminPass;
    }

    @JsonProperty
    public String getNeverBounceKey() {
        return neverBounceKey;
    }

    @JsonProperty
    public void setNeverBounceKey(String neverBounceKey) {
        this.neverBounceKey = neverBounceKey;
    }
}