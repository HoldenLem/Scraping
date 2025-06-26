package obs.techstars.Scraping.job;

import obs.techstars.Scraping.job.model.JobFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobFunctionsRepository extends JpaRepository<JobFunction, Long> {

}
