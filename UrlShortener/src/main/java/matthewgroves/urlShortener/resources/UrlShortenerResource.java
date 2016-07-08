package matthewgroves.urlShortener.resources;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;

import matthewgroves.urlShortener.HttpConnectionFactory;
import matthewgroves.urlShortener.api.ShortenedUrl;
import matthewgroves.urlShortener.db.UrlDAO;

/**
 * The /shrtn resource class
 * 
 * @author Matthew Groves <matthew.b.groves@gmail.com>
 *
 */
@Path("shrtn")
public class UrlShortenerResource {
	private UrlDAO dao;
	private HttpConnectionFactory httpConnection;
	
	public UrlShortenerResource(UrlDAO dao, HttpConnectionFactory httpConnection) {
		this.dao = dao;
		this.httpConnection = httpConnection;
		
		this.dao.createUrlsTableIfNeeded();
	}
	
	/**
	 * Gets the full URL for the desired shortened URL {@link id}
	 * 
	 * @param id
	 *            - the id argument to lookup in the DB and expand to a full URL
	 * @return Redirects user to expanded URL, if it exists. Otherwise, sends a
	 *         404 status. If an exception occurs, a 500 status is sent with the
	 *         exception in the response body.
	 */
	@GET
	@Path("{id}")
	@Timed
	// TODO - This should return a POJO, not a Response
	public Response expandUrl(@PathParam("id") String id) {
		// TODO - Why this shouldn't be done: http://12factor.net
		// TODO ENHANCEMENT - log this expansion into a new table in the DB
		// (date/time, user's IP, other info from request?)
		try {
			// The id portion of a shortened URL is really just the base-36
			// encoded version of its id in the database
			long idVal = Long.parseLong(id, 36);
			String url = dao.findUrlById(idVal);
			if (url != null) {
				URI redirectTo = new URI(url);
				return Response.seeOther(redirectTo).build();
			}
		} catch (Exception e) {
			return Response.serverError().entity(e).build();
		}
		
		return Response.status(404).build();
	}
	
	/**
	 * Creates a new entry in the DB for the given {@link fullUrl}
	 * 
	 * @param fullUrl
	 *            - the full URL the user wishes to shorten
	 * @return If success, this returns a {@link ShortenedUrl} record.
	 *         Otherwise, an HTTP 500 status is returned with the exception in
	 *         the response body.
	 */
	@POST
	@Timed
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	// TODO - This should return a POJO, not a Response
	public Response addShortenedUrl(String fullUrl) {
		try {
			if (fullUrl != null && fullUrl.length() > 0 && fullUrl.length() < 2048) {
				long id = dao.insertUrl(fullUrl);
				
				ShortenedUrl sUrl = new ShortenedUrl(id, fullUrl, "http://" + httpConnection.getHost() + ":"
						+ httpConnection.getPort() + "/shrtn/" + Long.toString(id, 36));
				return Response.ok().entity(sUrl).build();
			}
		} catch (Exception e) {
			return Response.serverError().entity(e).build();
		}
		
		return Response.serverError().build();
	}
}