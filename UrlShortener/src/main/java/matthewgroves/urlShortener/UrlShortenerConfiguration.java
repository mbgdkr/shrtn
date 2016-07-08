package matthewgroves.urlShortener;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

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
	
	@Valid
	@NotNull
	@JsonProperty
	private HttpConnectionFactory httpConnection = new HttpConnectionFactory();
	
	public HttpConnectionFactory getHttpConnectionFactory() {
		return httpConnection;
	}
}