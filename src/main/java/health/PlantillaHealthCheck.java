package health;

import com.codahale.metrics.health.HealthCheck;

public class PlantillaHealthCheck extends HealthCheck {

    private String plantilla;

    public PlantillaHealthCheck(){

    }

    @Override
    protected Result check() throws Exception {
        /*return Result.unhealthy("La plantilla no agregar nombres");*/
        return Result.healthy();
    }
}
