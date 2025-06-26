CREATE TABLE IF NOT EXISTS jobs_tags (
  jobs_id BIGINT NOT NULL,
  tags VARCHAR(255),
  CONSTRAINT fk_jobs FOREIGN KEY (jobs_id) REFERENCES jobs(id)
);