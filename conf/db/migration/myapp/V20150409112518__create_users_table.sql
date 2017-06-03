create table "users" (
  "id" UUID PRIMARY KEY NOT NULL,
  "type" VARCHAR(20) NOT NULL,
  "name" VARCHAR(1024) NOT NULL,
  "phone" VARCHAR(12) NOT NULL,
  "address" VARCHAR(1024),
  "passport" VARCHAR(1024),
  "email" VARCHAR(1024) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ
);
