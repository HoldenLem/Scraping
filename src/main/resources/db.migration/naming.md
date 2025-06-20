File names should follow the pattern (note two underscores after epoch): \
` V[small_number].[epoch_time_seconds]__[text_describing_the_purpose].sql `

Flyway migrations should contain a major version (which usually maps to a new release, so let's keep it `1` for now) followed by an epoch string in seconds (10 digits) to ensure the sequentiality of the migration: \
` V1.1699976173__Remove_CDN_domains.sql `

DO NOT include an epoch string that is in millis (13 digits). Because the millis string is longer than the seconds string, it has the potential to mess up the ordering: \
` V1.1699976173711__Remove_CDN_domains.sql `
