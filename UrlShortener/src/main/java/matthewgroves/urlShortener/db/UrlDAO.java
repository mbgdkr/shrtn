package matthewgroves.urlShortener.db;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface UrlDAO {
	@SqlUpdate("CREATE TABLE IF NOT EXISTS urls (id BIGINT AUTO_INCREMENT PRIMARY KEY, url VARCHAR(2048))")
	void createUrlsTableIfNeeded();

	@SqlQuery("SELECT url FROM urls WHERE id = :id")
	String findUrlById(@Bind("id") long idVal);

	@SqlUpdate("INSERT INTO urls (id, url) VALUES (NULL, :url)")
	@GetGeneratedKeys
	long insertUrl(@Bind("url") String url);
}