create table "requests" (
  "id" UUID PRIMARY KEY NOT NULL,
  "user_id" UUID REFERENCES users (id),
  "from_address" VARCHAR(1024) NOT NULL,
  "from_date" TIMESTAMPTZ NOT NULL,
  "from_hour" INTEGER,
  "to_hour" INTEGER,
  "phone_from" VARCHAR(12) NOT NULL,
  "cargo_info" VARCHAR(1024),
  "deliver_by" VARCHAR(32) NOT NULL,
  "max_weight" INTEGER NOT NULL,
  "info" VARCHAR(1024),

  "from_address" VARCHAR(1024) NOT NULL,
  "from_date" TIMESTAMPTZ NOT NULL,
  "from_hour" INTEGER,
  "to_hour" INTEGER,
  "phone_from" VARCHAR(12) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ
);
