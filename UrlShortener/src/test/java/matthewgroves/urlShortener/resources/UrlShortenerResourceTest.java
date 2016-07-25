package matthewgroves.urlShortener.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import matthewgroves.urlShortener.UrlShortenerConfiguration;
import matthewgroves.urlShortener.api.ShortenedUrl;
import matthewgroves.urlShortener.db.UrlDAO;

public class UrlShortenerResourceTest {
	
	UrlDAO dao;
	UrlShortenerConfiguration config;
	UrlShortenerResource resource;
	
	@Before
	public void setupDao() throws Exception {
		dao = mock(UrlDAO.class);
		
		config = new UrlShortenerConfiguration();
		config.setHostname("localhost");
		config.configurePort(8080);
		
		resource = new UrlShortenerResource(dao, config);
	}
	
	@Test
	public void configurableUrlBase() throws Exception {
		config.setHostname("www.shortener.com");
		config.configurePort(12345);
		
		ShortenedUrl response = (ShortenedUrl) resource.addShortenedUrl("www.google.com").getEntity();
		
		assertThat(response.getShortenedUrl()).contains(config.getHostname())
				.contains(Integer.toString(config.getRuntimePort()));
	}
	
	@Test
	public void linkNotFound() {
		assertThat(resource.expandUrl("10").getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void linkFound() {
		final String EXAMPLE = "http://www.example.com";
		when(dao.findUrlById(anyLong())).thenReturn(EXAMPLE);
		
		Response res = resource.expandUrl("10");
		
		assertThat(res.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
		
		assertThat(res.getLocation().toString()).isEqualTo(EXAMPLE);
	}
	
}
