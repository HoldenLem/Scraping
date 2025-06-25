package obs.techstars.Scraping;

import obs.techstars.Scraping.scraping.MainPageScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


import java.net.MalformedURLException;
import java.net.URISyntaxException;

@SpringBootApplication
public class ScrapingApplication {

	public static void main(String[] args) throws MalformedURLException, URISyntaxException {
		ApplicationContext context = SpringApplication.run(ScrapingApplication.class, args);
		MainPageScraper scrapingService = context.getBean(MainPageScraper.class);
		scrapingService.getJobListingsBy("Design");

	}

}
