package matthewgroves.urlShortener;

import org.skife.jdbi.v2.DBI;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import matthewgroves.urlShortener.db.UrlDAO;
import matthewgroves.urlShortener.resources.UrlShortenerResource;

/**
 * The main application class for the URL shortener
 * 
 * @author Matthew Groves <matthew.b.groves@gmail.com>
 *
 */
public class UrlShortenerApplication extends Application<UrlShortenerConfiguration> {
	public static void main(String[] args) throws Exception {
		new UrlShortenerApplication().run(args);
	}
	
	@Override
	public String getName() {
		return "shrtn";
	}
	
	@Override
	public void initialize(Bootstrap<UrlShortenerConfiguration> bootstrap) {
		// nothing to do yet
	}
	
	/**
	 * Initialize services
	 */
	@Override
	public void run(UrlShortenerConfiguration configuration, Environment environment) throws Exception {
		// TODO - Set up Docker for MySQL DB
		// TODO - Apply principles from here:
		// http://www.dropwizard.io/0.9.3/docs/manual/core.html
		
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
		final UrlDAO dao = jdbi.onDemand(UrlDAO.class);
		environment.jersey().register(new UrlShortenerResource(dao, configuration.getHttpConnectionFactory()));
	}
}