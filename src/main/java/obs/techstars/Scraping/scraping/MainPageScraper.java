package obs.techstars.Scraping.scraping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainPageScraper {
    @Autowired
    private JobDetailScraper jobDetailScraper;

    private static final String BASE_URL = "https://jobs.techstars.com/jobs";

    private final Playwright playwright = Playwright.create();
    private final Browser browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions().setHeadless(false)
    );
    private final Page mainPage = browser.newPage();


    private void acceptCookiesIfPresent(Page page) {
        var locator = page.locator("button:has-text('Accept')");
        if (locator.count() > 0) {
            locator.first().click();
            log.info("Accepted cookies");
        } else {
            log.info("No cookies to accept");
        }
    }

    public List<String> scrapeJobFunction() {

        mainPage.navigate("https://jobs.techstars.com/jobs");
        acceptCookiesIfPresent(mainPage);
        Locator jobFunction = mainPage.locator("div[data-testid='filter-option-item-0'] >> text=Job function");
        jobFunction.click();
        mainPage.locator("div[data-testid='job_functions-Administration']").click();
        List<String> result = mainPage.locator("div[data-testid^='job_functions-']").allInnerTexts();
        mainPage.close();
        return result;
    }

    public List<Map<String, String>> getJobListingsBy(String jobFunction) {
        final Page page = browser.newPage();
        page.navigate(BASE_URL);
        page.waitForTimeout(100);

        acceptCookiesIfPresent(page);
        chooseJobFunction(page, jobFunction);
        page.waitForTimeout(200);
        scrollToBottom(page);

        Locator jobItems = page.locator("div[data-testid='job-list-item']");
        int count = jobItems.count();
        List<Map<String, String>> allJobsData = new ArrayList<>();
        for (int i = 0; i < count/4; i++) {
            Locator item = jobItems.nth(i);
            Locator link = item.locator("a[data-testid='read-more']");
            String jobUrl = link.getAttribute("href");
            jobUrl = "https://jobs.techstars.com" + jobUrl;


            if (!jobUrl.startsWith("https://jobs.techstars.com/companies/") ||
                    jobUrl.contains("luca-2-0476fac0-8ce1-437e-9307-3732365e8dee") || jobUrl.contains("revolute-robotics")) {
                log.info("Skipping job URL: {}", jobUrl);
                continue;
            }
            log.info("Processing job URL: {}", jobUrl);

            Page jobPage = browser.newPage();


            jobPage.navigate(jobUrl, new Page.NavigateOptions()
                    .setTimeout(60000)
                    .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));// Назва вакансії
            acceptCookiesIfPresent(jobPage);

            List<String> tags = grabTags(item);
            String logoUrl = item.locator("img[data-testid='image']").getAttribute("src");

            Map<String, String> data = jobDetailScraper.extractJobDetails(jobPage);
            data.put("job_page_url", jobUrl);
            data.put("title", item.locator("meta[itemprop='name']").getAttribute("content"));
            data.put("organization_logo", logoUrl);
            data.put("tags", String.join(", ", tags));
            allJobsData.add(data);
            String orgUrl = item.locator("a[data-testid='link']").getAttribute("href");
            if (orgUrl != null && !orgUrl.startsWith("http")) {
                orgUrl = "https://jobs.techstars.com" + orgUrl;
            }
            data.put("organization_url", orgUrl);


            jobPage.close();
        }
        saveJobDetailsToFile(allJobsData, "job-details.json");
        browser.close();
        return allJobsData;
    }


    public void saveJobDetailsToFile(
            List<Map<String, String>> data, String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
            log.info("Job details saved to {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save job details", e);
        }
    }

    private void chooseJobFunction(Page page, String jobFunction) {
        Locator jobFunctionFilter = page.locator("div[data-testid='filter-option-item-0'] >> text=Job function");
        jobFunctionFilter.click();

        Locator scroller = page.locator("div[data-test-id='virtuoso-scroller']");

        int maxScrolls = 5;
        for (int i = 0; i < maxScrolls; i++) {
            scroller.evaluate("el => el.scrollBy(0, el.offsetHeight)");
            page.waitForTimeout(500);
        }

        Locator options = scroller.locator("div[role='option'], div[class*='dmqHEX']");
        int count = options.count();
        for (int i = 0; i < count; i++) {
            Locator item = options.nth(i);
            String text = item.innerText().trim();
            if (text.equalsIgnoreCase(jobFunction)) {
                item.scrollIntoViewIfNeeded();
                page.waitForTimeout(300);
                item.click();
                System.out.println("Job function selected: " + text);
                return;
            }
        }

    }


    private void scrollToBottom(Page page) {
        page.waitForSelector("div[data-testid='job-list-item']");
        page.mouse().wheel(0, 3000);
        page.waitForTimeout(1000);

        int previousCount = -1, sameCountTimes = 0;

        while (sameCountTimes < 3) {
            Locator loadMoreBtn = page.locator("button[data-testid='load-more'][data-loading='false']");
            if (loadMoreBtn.isVisible()) {
                loadMoreBtn.scrollIntoViewIfNeeded();
                loadMoreBtn.evaluate("el => el.click()");
                page.waitForTimeout(200);
            }

            int currentCount = page.locator("div[data-testid='job-list-item']").count();
            sameCountTimes = (currentCount == previousCount) ? sameCountTimes + 1 : 0;
            previousCount = currentCount;

            page.mouse().wheel(0, 5000);
            page.waitForTimeout(1000);
        }
    }

    private List<String> grabTags(Locator item) {
        Locator tagElements = item.locator("div[data-testid='tag']");
        List<String> tags = new ArrayList<>();
        int tagCount = tagElements.count();

        for (int j = 0; j < tagCount; j++) {
            tags.add(tagElements.nth(j).innerText().trim());
        }
        log.info("Found {} tags for the job item", tagCount);
        return tags;
    }


}




