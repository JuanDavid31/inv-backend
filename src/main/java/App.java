import annotation.filter.AuthFilter;
import annotation.filter.EventBroadcasterFilter;
import dao.*;
import health.PlantillaHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.kotlin.KotlinPlugin;
import org.jdbi.v3.core.mapper.CaseStrategy;
import org.jdbi.v3.core.mapper.MapMappers;
import org.jdbi.v3.postgres.PostgresPlugin;
import rest.*;
import rest.sse.EventPublisher;
import rest.sse.EventosEventSourceServlet;
import usecase.*;
import util.*;
import ws.InteraccionWebsocketServlet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;

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

    public void run(ConfiguracionApp configuracionApp, Environment environment) {

        // Enable CORS headers
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization,extension");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        //-------------------------------------------------------------------------------------------------------

        //SessionHandler para mantener sesiones con los clientes conectados a los endpoint de SSE.
        environment.servlets().setSessionHandler(new SessionHandler());

        EventPublisher eventPublisher = new EventPublisher();

        //ws://localhost:8080/colaboracion
        ServletRegistration.Dynamic miServlet = environment.servlets().addServlet("InteraccionServlet", new InteraccionWebsocketServlet());
        miServlet.setAsyncSupported(true);
        miServlet.addMapping("/colaboracion/*");

        //Server sent events servlets
        environment
            .servlets()
            .addServlet("EventosSse", new EventosEventSourceServlet(eventPublisher))
            .addMapping("/eventos");

        //Utils
        JWTUtils jwtUtils = new JWTUtils(configuracionApp.jwtKey);
        CorreoUtils correoUtils = new CorreoUtils(configuracionApp.adminEmail, configuracionApp.adminPass, configuracionApp.neverBounceKey);
        FotoUtils fotoUtils = new FotoUtils();
        S3Utils s3Utils = new S3Utils();

        //JDBI y plugins
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuracionApp.getDataSourceFactory(), "postgres")
                .installPlugin(new PostgresPlugin())
                .installPlugin(new KotlinPlugin());
        jdbi.getConfig(MapMappers.class).setCaseChange(CaseStrategy.NOP);
        //----------------------

        //DAOs
        DaoPersona daoPersona = new DaoPersona(jdbi);
        DaoProblematica daoProblematica = new DaoProblematica(jdbi);
        DaoInvitacion daoInvitacion = new DaoInvitacion(jdbi);
        DaoNodo daoNodo = new DaoNodo(jdbi);
        DaoRelacion daoRelacion = new DaoRelacion(jdbi);
        DaoReaccion daoReaccion = new DaoReaccion(jdbi);
        DaoGrupo daoGrupo = new DaoGrupo(jdbi);
        DaoEscrito daoEscrito = new DaoEscrito(jdbi);

        //Use cases
        FotoUseCase fotoUseCase = new FotoUseCase(daoNodo, fotoUtils, s3Utils);
        ProblematicaUseCase problematicaUseCase = new ProblematicaUseCase(daoProblematica, eventPublisher);
        CorreoUseCase correoUseCase = new CorreoUseCase(daoPersona, correoUtils);
        InvitacionUseCase invitacionUseCase = new InvitacionUseCase(daoInvitacion, daoPersona, eventPublisher);
        NodoUseCase nodoUseCase = SingletonUtils.guardarNodoUseCase(new NodoUseCase(daoNodo, daoRelacion));
        GrupoUseCase grupoUseCase = new GrupoUseCase(daoGrupo, daoReaccion, nodoUseCase);
        ReaccionUseCase reaccionUseCase = new ReaccionUseCase(daoReaccion);
        EscritoUseCase escritoUseCase = new EscritoUseCase(daoEscrito);
        PersonaUseCase personaUseCase = new PersonaUseCase(correoUtils, jwtUtils, daoPersona);
        RelacionUseCase relacionUseCase = new RelacionUseCase(daoRelacion);
        SingletonUtils.guardarRelacionUseCase(relacionUseCase);

        //Filtros
        final AuthFilter authFilter = new AuthFilter(jwtUtils);
        final EventBroadcasterFilter eventBroadcasterFilter = new EventBroadcasterFilter();

        //Resources
        final AuthResource authResource = new AuthResource(personaUseCase, correoUseCase);
        final PersonaResource personaResource = new PersonaResource(personaUseCase);
        final PersonaProblematicaResource personaProblematicaResource = new PersonaProblematicaResource(problematicaUseCase, daoNodo, grupoUseCase, reaccionUseCase);
        final PersonaInvitacionResource personaInvitacionResource = new PersonaInvitacionResource(invitacionUseCase);
        final ProblematicaResource problematicaResource = new ProblematicaResource(invitacionUseCase, problematicaUseCase);
        final InvitacionResource invitacionResource = new InvitacionResource(invitacionUseCase);
        final NodoResource nodoResource = new NodoResource(fotoUseCase, nodoUseCase);
        final GrupoResource grupoResource = new GrupoResource(reaccionUseCase);
        final ProblematicaEscritoResource problematicaEscritoResource = new ProblematicaEscritoResource(escritoUseCase);
        final ProblematicaReaccionResource problematicaReaccionResource = new ProblematicaReaccionResource(SingletonUtils.guardarGrupoUseCase(grupoUseCase));
        final ProblematicaGrupoResource problematicaGrupoResource = new ProblematicaGrupoResource(grupoUseCase, escritoUseCase);
        final ProblematicaPersonaResource problematicaPersonaResource = new ProblematicaPersonaResource(escritoUseCase, fotoUseCase, personaUseCase);

        //Healthchecks
        final PlantillaHealthCheck plantillaCheck = new PlantillaHealthCheck();

        //Registro de resouces y otras cosas
        environment.jersey().register(MultiPartFeature.class);
        environment.healthChecks().register("template", plantillaCheck);
        environment.jersey().register(new JsonProcessingExceptionMapper(true));
        environment.jersey().register(authFilter);
        environment.jersey().register(eventBroadcasterFilter);
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
        environment.jersey().register(personaProblematicaResource);

        environment.lifecycle().manage(s3Utils);
    }
}