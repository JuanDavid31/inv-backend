import dao.*;
import filter.AuthFilter;
import health.PlantillaHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.kotlin.KotlinPlugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import rest.*;
import usecase.CorreoUseCase;
import usecase.FotoUseCase;
import usecase.ProblematicaUseCase;
import util.CorreoUtils;
import util.JWTUtils;
import ws.InteraccionWebsocketServlet;

import javax.servlet.ServletRegistration;

public class App extends Application<ConfiguracionApp> {

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "App investigación";
    }

    @Override
    public void initialize(Bootstrap<ConfiguracionApp> bootstrap) {
        bootstrap.addBundle(new JdbiExceptionsBundle());
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false)
        ));
    }

    public void run(ConfiguracionApp configuracionApp, Environment environment) throws Exception {
        ServletRegistration.Dynamic miServlet = environment.servlets().addServlet("miServlet", new InteraccionWebsocketServlet());
        miServlet.setAsyncSupported(true);
        miServlet.addMapping("/ws/*");

        System.out.println(configuracionApp.adminEmail);
        System.out.println(configuracionApp.adminPass);

        //Utils
        JWTUtils jwtUtils = new JWTUtils(configuracionApp.jwtKey);
        CorreoUtils correoUtils = new CorreoUtils(configuracionApp.adminEmail, configuracionApp.adminPass);

        //JDBI y plugins
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuracionApp.getDataSourceFactory(), "postgres");
        jdbi.installPlugin(new PostgresPlugin());
        jdbi.installPlugin(new KotlinPlugin());

        //DAOs
        DaoPersona daoPersona = new DaoPersona(jdbi);
        DaoProblematica daoProblematica = new DaoProblematica(jdbi);
        DaoInvitacion daoInvitacion = new DaoInvitacion(jdbi);
        DaoNodo daoNodo = new DaoNodo(jdbi);
        DaoReaccion daoReaccion = new DaoReaccion(jdbi);
        DaoGrupo daoGrupo = new DaoGrupo(jdbi);
        DaoEscrito daoEscrito = new DaoEscrito(jdbi);

        //Use cases
        FotoUseCase fotoUseCase = new FotoUseCase(daoNodo);
        ProblematicaUseCase problematicaUseCase = new ProblematicaUseCase(daoProblematica);
        CorreoUseCase correoUseCase = new CorreoUseCase(daoPersona, correoUtils);

        //Filtros
        final AuthFilter authFilter = new AuthFilter(jwtUtils);

        //Resources
        final AuthResource authResource = new AuthResource(daoPersona, jwtUtils, correoUseCase);

        final PersonaResource personaResource = new PersonaResource(daoPersona, daoProblematica, daoNodo);

        final PersonaInvitacionResource personaInvitacionResource = new PersonaInvitacionResource(daoInvitacion);

        final ProblematicaResource problematicaResource = new ProblematicaResource(daoInvitacion, problematicaUseCase);

        final InvitacionResource invitacionResource = new InvitacionResource(daoInvitacion);

        final NodoResource nodoResource = new NodoResource(fotoUseCase, daoNodo);

        final GrupoResource grupoResource = new GrupoResource(daoReaccion);

        final ProblematicaEscritoResource problematicaEscritoResource = new ProblematicaEscritoResource(daoEscrito);

        final ProblematicaReaccionResource problematicaReaccionResource = new ProblematicaReaccionResource(daoGrupo);

        final ProblematicaGrupoResource problematicaGrupoResource = new ProblematicaGrupoResource(daoGrupo);

        final ProblematicaPersonaResource problematicaPersonaResource = new ProblematicaPersonaResource(daoEscrito, fotoUseCase);

        //Healthchecks
        final PlantillaHealthCheck plantillaCheck = new PlantillaHealthCheck(configuracionApp.getPlantilla());

        //Registro de resouces y otras cosas
        environment.jersey().register(MultiPartFeature.class);
        environment.healthChecks().register("template", plantillaCheck);
        environment.jersey().register(new JsonProcessingExceptionMapper(true));
        environment.jersey().register(authFilter);
        environment.jersey().register(authResource);
        environment.jersey().register(personaResource);
        environment.jersey().register(problematicaResource);
        environment.jersey().register(invitacionResource);
        environment.jersey().register(nodoResource);
        environment.jersey().register(grupoResource);
        environment.jersey().register(problematicaEscritoResource);
        environment.jersey().register(problematicaReaccionResource);
        environment.jersey().register(problematicaGrupoResource);
        environment.jersey().register(problematicaPersonaResource);
        environment.jersey().register(personaInvitacionResource);
    }
}