import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ConfiguracionApp extends Configuration {

    @NotEmpty //Lanza excepciones si no estan en el YAML
    private String plantilla;

    @NotEmpty
    private String nombrePorDefecto = "Tipo x";

    @JsonProperty
    @NotEmpty
    public String jwtKey = "";

    @NotEmpty
    public String adminEmail = "";

    @NotEmpty
    public String adminPass = "";

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty
    public String getPlantilla() {
        return plantilla;
    }

    @JsonProperty
    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    @JsonProperty
    public String getNombrePorDefecto() {
        return nombrePorDefecto;
    }

    @JsonProperty
    public void setNombrePorDefecto(String name) {
        this.nombrePorDefecto = name;
    }

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
}
