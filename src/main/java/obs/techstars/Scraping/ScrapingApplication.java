package obs.techstars.Scraping;

import obs.techstars.Scraping.job.model.JobDto;
import obs.techstars.Scraping.job.JobService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

@SpringBootApplication
public class ScrapingApplication {

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        ApplicationContext context = SpringApplication.run(ScrapingApplication.class, args);

        JobService jobService = context.getBean(JobService.class);
        List<JobDto> jobs = jobService.saveJobDetailsByFunction("Software Engineering");
        System.out.println("Saved jobs: " + jobs.size());
    }

}
