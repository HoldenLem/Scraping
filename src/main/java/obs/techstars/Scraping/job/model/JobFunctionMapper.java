package obs.techstars.Scraping.job.model;

public class JobFunctionMapper {
    public static JobFunctionDTO toDto(JobFunction jobFunction) {
        if (jobFunction == null) {
            return null;
        }
        return JobFunctionDTO.builder()
                .id(jobFunction.getId())
                .name(jobFunction.getName())
                .build();
    }

    public static JobFunction toEntity(JobFunctionDTO jobFunctionDTO) {
        if (jobFunctionDTO == null) {
            return null;
        }
        return JobFunction.builder()
                .id(jobFunctionDTO.getId())
                .name(jobFunctionDTO.getName())
                .build();
    }
}
