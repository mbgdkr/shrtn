package matthewgroves.urlShortener;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.server.SimpleServerFactory;

/**
 * Configuration class
 * 
 * @author Matthew Groves <matthew.b.groves@gmail.com>
 *
 */
public class UrlShortenerConfiguration extends Configuration {
	@Valid
	@NotNull
	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();
	
	public DataSourceFactory getDataSourceFactory() {
		return database;
	}
	
	@NotEmpty
	@JsonProperty
	private String hostname;
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	// A "virtual" property, not actually in the config file
	private int port = -1;
	
	public int getRuntimePort() {
		return port;
	}
	
	public void setRuntimePort(int port) {
		this.port = port;
	}
	
	/**
	 * Convenience function for setting the port # programmatically
	 * 
	 * @param port
	 *            - the desired port #
	 * @throws Exception
	 *             - if the set was unsuccessful
	 */
	public void configurePort(int port) throws Exception {
		// If port number has already been initialized, do not allow this to
		// happen
		if (this.port != -1)
			throw new Exception(
					"Server already started.  Port number must be set before setRuntimePort() has been called.");
		
		ServerFactory s = getServerFactory();
		if (s instanceof DefaultServerFactory) {
			DefaultServerFactory serverFactory = (DefaultServerFactory) s;
			// This will only set the port of the first assignable connector
			// that is found
			for (ConnectorFactory connector : serverFactory.getApplicationConnectors()) {
				if (connector.getClass().isAssignableFrom(HttpConnectorFactory.class)) {
					((HttpConnectorFactory) connector).setPort(port);
					return;
				}
			}
		} else if (s instanceof SimpleServerFactory) {
			SimpleServerFactory serverFactory = (SimpleServerFactory) s;
			HttpConnectorFactory connector = (HttpConnectorFactory) serverFactory.getConnector();
			if (connector.getClass().isAssignableFrom(HttpConnectorFactory.class)) {
				connector.setPort(port);
				return;
			}
		}
		
		throw new Exception("Could not find a ServerFactory to set the port number for.");
	}
}