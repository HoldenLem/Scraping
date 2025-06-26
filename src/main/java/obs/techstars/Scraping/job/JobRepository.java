package obs.techstars.Scraping.job;

import obs.techstars.Scraping.job.model.Job;
import obs.techstars.Scraping.job.model.JobFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    boolean existsByJobPageUrl(String jobPageUrl);

}
