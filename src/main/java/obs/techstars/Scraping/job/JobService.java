package obs.techstars.Scraping.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import obs.techstars.Scraping.job.model.*;
import obs.techstars.Scraping.scraping.MainPageScraper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final JobFunctionsRepository jobFunctionsRepository;

    private final MainPageScraper mainPageScraper;

    public List<JobFunctionDTO> saveJobFunctions() {
        List<String> jobFunctionsString = mainPageScraper.scrapeJobFunction();
        List<JobFunction> jobFunctions = jobFunctionsString.stream()
                .map(name -> JobFunction.builder().name(name).build())
                .toList();
        List<JobFunction> savedJobFunctions = jobFunctionsRepository.saveAll(jobFunctions);


        return savedJobFunctions.stream().
                map(JobFunctionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<JobDto> saveJobDetailsByFunction(String jobFunction) {
        List<Map<String, String>> jobDataList = mainPageScraper.getJobListingsBy(jobFunction);

        List<Job> jobsToSave = jobDataList.stream()
                .filter(data -> !jobRepository.existsByJobPageUrl(data.get("job_page_url")))
                .map(data -> Job.builder()
                        .jobPageUrl(data.get("job_page_url"))
                        .positionName(data.get("position_name"))
                        .organizationUrl(data.get("organization_url"))
                        .logoUrl(data.get("organization_logo"))
                        .organizationTitle(data.get("organization_title"))
                        .laborFunction(data.get("labor_function"))
                        .location(data.get("location"))
                        .postedDate(parsePostedDate(data.get("posted_date")))
                        .description(data.get("description"))
                        .tags(parseTags(data.get("tags")))
                        .build())
                .toList();

        jobRepository.saveAll(jobsToSave);
        return jobsToSave.stream()
                .map(JobMapper::from)
                .toList();
    }

    private Long parsePostedDate(String posted) {
        if (posted == null || posted.isBlank()) return null;
        try {
            String[] parts = posted.split(" on ");
            if (parts.length == 2) {
                LocalDate date = LocalDate.parse(parts[1].trim(), DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH));
                return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            }
        } catch (Exception e) {
            log.warn("Can't parse posted date: {}", posted);
        }
        return null;
    }

    private List<String> parseTags(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }


}
