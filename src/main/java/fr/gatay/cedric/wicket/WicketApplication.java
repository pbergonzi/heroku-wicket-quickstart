package fr.gatay.cedric.wicket;

import fr.gatay.cedric.wicket.core.RedisPageManagerProvider;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.protocol.http.WebApplication;
import org.jboss.weld.environment.servlet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.BeanManager;
import java.net.URI;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 */
public class WicketApplication extends WebApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(WicketApplication.class);

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<HomePage> getHomePage() {
        return HomePage.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();

        try {
            URI dbUri = new URI(System.getenv("DATABASE_URL"));

            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
            System.setProperty("hibernate.connection.url", dbUrl);
            System.setProperty("hibernate.connection.user", username);
            System.setProperty("hibernate.connection.password", password);
            LOGGER.debug("JDBC URL : {}", System.getProperty("hibernate.connection.url"));
            LOGGER.debug("JDBC User : {}", System.getProperty("hibernate.connection.user"));
            LOGGER.debug("JDBC Password : {}", System.getProperty("hibernate.connection.password"));
        } catch (Exception e) {
            LOGGER.error("Unable to extract database url");
        }
        setPageManagerProvider(new RedisPageManagerProvider(this));

        BeanManager manager = (BeanManager) getServletContext().getAttribute(
                Listener.BEAN_MANAGER_ATTRIBUTE_NAME);

        new CdiConfiguration(manager).configure(this);
    }

}
