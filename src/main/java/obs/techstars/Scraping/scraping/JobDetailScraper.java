package obs.techstars.Scraping.scraping;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class JobDetailScraper {

    public Map<String, String> extractJobDetails(Page jobPage) {
        Map<String, String> data = new HashMap<>();

        Locator heading = jobPage.locator("h2.sc-beqWaB.jqWDOR");
        data.put("position_name", heading.first().innerText());

        Locator locationDivs = jobPage.locator("div.sc-beqWaB.bpXRKw");
        int count = locationDivs.count();
        String jobFunction = "";
        String location = "";
        if (count >= 2) {
            jobFunction = locationDivs.nth(1).innerText();
            location = locationDivs.nth(2).innerText();

            log.info("Job Function: {}", jobFunction);
            log.info("Location:  {}", location);
        }
        data.put("job_function", jobFunction);
        data.put("location", location);

        Locator postedElement = jobPage.locator("div.sc-beqWaB.gRXpLa");
        String date = postedElement.innerText();
        log.info("Posted date: {}", date);
        data.put("posted_date", date);

        Locator jobDescriptionDiv = jobPage.locator("div#job-description");
        if (jobDescriptionDiv.count() == 0) {
            jobDescriptionDiv = jobPage.locator("div[data-testid='careerPage']");
        }
        String jobDescription = jobDescriptionDiv.innerHTML();
        log.info("Job description length: {}", jobDescription.length());
        data.put("description", jobDescription);
        return data;
    }
}
