-- Create metadata tables
create table "programming_languages"
(
    "id"   bigint GENERATED ALWAYS AS IDENTITY,
    "name" text not null unique,

    primary key ("id")
);

-- Create workspace related tables
create table "workspaces"
(
    "id"   bigint GENERATED ALWAYS AS IDENTITY,
    "name" text not null unique,

    primary key ("id")
);

create table "projects"
(
    "id"        bigint GENERATED ALWAYS AS IDENTITY,
    "name"      text not null,
    "workspace" bigint references workspaces ("id"),

    primary key ("id")
);

create table "packages"
(
    "id"      bigint GENERATED ALWAYS AS IDENTITY,
    "package" text not null unique,
    "project" bigint references projects ("id"),

    primary key ("id")
);

create table "classes"
(
    "id"                   bigint GENERATED ALWAYS AS IDENTITY,
    "name"                 text                     not null,
    "package"              bigint references packages ("id"),
    "programming_language" bigint references "programming_languages" ("id"),
    "last_modified"        timestamp with time zone not null,

    primary key ("id")
);

create table "methods"
(
    "id"    bigint GENERATED ALWAYS AS IDENTITY,
    "name"  text not null,
    "class" bigint references classes ("id"),

    primary key ("id")
);

-- Create metrics related tables
create table "component_dependency_metrics"
(
    "id"                bigint GENERATED ALWAYS AS IDENTITY,
    "package"           bigint           not null references "packages" ("id"),
    "afferent_coupling" int              not null default 0,
    "efferent_coupling" int              not null default 0,
    "abstractness"      double precision not null default 0,
    "instability"       double precision not null default 0,
    "distance"          double precision not null default 0,

    primary key ("id")
);

create table "cumulative_component_dependencies"
(
    "id"               bigint GENERATED ALWAYS AS IDENTITY,
    "package"          bigint           not null references "packages" ("id"),
    "cumulative"       int              not null default (0),
    "average"          double precision not null default (0),
    "relative_average" double precision not null default (0),
    "normalized"       double precision not null default (0),

    primary key ("id")
);

create table "relational_cohesion_metrics"
(
    "id"                               bigint GENERATED ALWAYS AS IDENTITY,
    "package"                          bigint           not null references "packages" ("id"),
    "status"                           text             not null,
    "number_of_internal_relationships" int              not null default (0),
    "number_of_types"                  int              not null default (0),
    "relational_cohesion"              double precision not null default (0),

    primary key ("id")
);

create table "visibility_metrics"
(
    "id"                          bigint GENERATED ALWAYS AS IDENTITY,
    "package"                     bigint           not null references "packages" ("id"),
    "relative_visibility"         double precision not null default (0),
    "average_relative_visibility" double precision not null default (0),
    "global_relative_visibility"  double precision not null default (0),

    primary key ("id")
);

create table "authors"
(
    "id"    bigint GENERATED ALWAYS AS IDENTITY,
    "name"  text not null,
    "email" text not null,

    primary key ("id")
);

create table "class_contributions"
(
    "id"             bigint GENERATED ALWAYS AS IDENTITY,
    "class"          bigint    not null references "classes" ("id"),
    "timestamp"      timestamp not null,
    "vcs_identifier" text      not null,
    "author"         int       not null references "authors" ("id"),

    primary key ("id")
);

create table "class_complexity"
(
    "id"                bigint GENERATED ALWAYS AS IDENTITY,
    "contribution"      bigint not null references "class_contributions" ("id"),
    "complexity"        int    not null default (0),
    "complexity_rating" int    not null,

    primary key ("id")
);

create table "class_lines_of_code"
(
    "id"                     bigint GENERATED ALWAYS AS IDENTITY,
    "contribution"           bigint           not null references "class_contributions" ("id"),
    "total_lines_of_code"    int              not null default (0),
    "total_lines_of_comment" int              not null default (0),
    "comment_to_code_ratio"  double precision not null default (0),
    "size"                   int              not null,

    primary key ("id")
);

create table "package_complexity"
(
    "id"         bigint GENERATED ALWAYS AS IDENTITY,
    "package"    bigint not null references "packages" ("id"),
    "complexity" int    not null default (0),

    primary key ("id")
);

create table "package_lines_of_code"
(
    "id"                     bigint GENERATED ALWAYS AS IDENTITY,
    "package"                bigint           not null references "packages" ("id"),
    "total_lines_of_code"    int              not null default (0),
    "total_lines_of_comment" int              not null default (0),
    "comment_to_code_ratio"  double precision not null default (0),
    "package_size"           int              not null,

    primary key ("id")
);