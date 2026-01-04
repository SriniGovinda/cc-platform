create table if not exists audit_logs (
  audit_id bigserial primary key,
  user_id bigint,
  action varchar(50) not null,
  resource varchar(100),
  details varchar(2000),
  success boolean not null,
  created_at timestamptz not null default now()
);

create table if not exists export_jobs (
  job_id bigserial primary key,
  user_id bigint not null,
  card_id bigint,
  from_date date not null,
  to_date date not null,
  filter_status varchar(20),
  status varchar(20) not null,
  record_count int,
  s3_key varchar(512),
  error_message varchar(1000),
  created_at timestamptz not null default now(),
  completed_at timestamptz
);

create index if not exists idx_export_jobs_user on export_jobs(user_id);
create index if not exists idx_export_jobs_status on export_jobs(status, created_at);
