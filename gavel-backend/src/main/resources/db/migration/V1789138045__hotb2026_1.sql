alter table "projects"
    add column "repeated_analysis_failure_count" int not null default 0,
    add column "total_lines_of_code" int not null default 0,
    add column "total_lines_of_comments" int not null default 0,
    add column "comment_to_code_ratio" double precision not null default (0),
    add column "number_of_types" int not null default 0,
    add column "number_of_packages" int not null default 0,
    add column "number_of_findings" int not null default 0,
    add column "number_of_high_priority_findings" int not null default 0,
    add column "defect_density" double precision not null default (0),
    add column "high_defect_density" double precision not null default (0);