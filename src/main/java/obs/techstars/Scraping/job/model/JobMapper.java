package obs.techstars.Scraping.job.model;

public class JobMapper {
    public static JobDto from(Job job) {
        if (job == null) {
            return null;
        }

        return JobDto.builder()
                .jobPageUrl(job.getJobPageUrl())
                .positionName(job.getPositionName())
                .organizationUrl(job.getOrganizationUrl())
                .logoUrl(job.getLogoUrl())
                .organizationTitle(job.getOrganizationTitle())
                .laborFunction(job.getLaborFunction())
                .location(job.getLocation())
                .postedDate(job.getPostedDate())
                .description(job.getDescription())
                .tags(job.getTags())
                .build();
    }

    public static Job from(JobDto dto) {
        if (dto == null) {
            return null;
        }

        return Job.builder()
                .jobPageUrl(dto.getJobPageUrl())
                .positionName(dto.getPositionName())
                .organizationUrl(dto.getOrganizationUrl())
                .logoUrl(dto.getLogoUrl())
                .organizationTitle(dto.getOrganizationTitle())
                .laborFunction(dto.getLaborFunction())
                .location(dto.getLocation())
                .postedDate(dto.getPostedDate())
                .description(dto.getDescription())
                .tags(dto.getTags())
                .build();
    }
}
