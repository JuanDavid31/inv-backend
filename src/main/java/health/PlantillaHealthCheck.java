package health;

import com.codahale.metrics.health.HealthCheck;

public class PlantillaHealthCheck extends HealthCheck {

    private String plantilla;

    public PlantillaHealthCheck(String plantilla){
        this.plantilla = plantilla;
    }

    @Override
    protected Result check() throws Exception {
        final String saying = String.format(plantilla, "TEST");
        if (!saying.contains("TEST")) {
            return Result.unhealthy("La plantilla no agregar nombres");
        }
        return Result.healthy();
    }
}
