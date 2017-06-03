create table "delivery_blocks" (
  "id" UUID PRIMARY KEY NOT NULL,
  "request_id" UUID REFERENCES requests (id) NOT NULL,
  "otkuda" BOOLEAN DEFAULT TRUE,
  "address" VARCHAR(1024),
  "phone" VARCHAR(12) NOT NULL,
  "time_from" TIMESTAMPTZ NOT NULL,
  "time_to" TIMESTAMPTZ NOT NULL,
  "description" VARCHAR(1024),
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ
);
