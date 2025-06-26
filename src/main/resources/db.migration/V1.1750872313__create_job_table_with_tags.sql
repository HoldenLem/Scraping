BEGIN;

CREATE TABLE jobs (
    id SERIAL PRIMARY KEY,
    job_page_url TEXT NOT NULL UNIQUE,
    position_name TEXT,
    organization_url TEXT,
    logo_url TEXT,
    organization_title TEXT,
    labor_function TEXT,
    location TEXT,
    posted_date BIGINT,
    description TEXT
);

CREATE INDEX idx_jobs_posted_date ON jobs(posted_date);
CREATE INDEX idx_jobs_labor_function ON jobs(labor_function);
CREATE INDEX idx_jobs_location ON jobs(location);

CREATE TABLE job_tags (
    job_id BIGINT NOT NULL,
    tag TEXT NOT NULL,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);

CREATE INDEX idx_job_tags_tag ON job_tags(tag);

COMMIT;
