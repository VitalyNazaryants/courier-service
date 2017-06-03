create table "requests" (
  "id" UUID PRIMARY KEY NOT NULL,
  "user_id" UUID REFERENCES users (id) NOT NULL,
  "cargo_info" VARCHAR(1024),
  "deliver_by" VARCHAR(32) NOT NULL,
  "max_weight" INTEGER NOT NULL,
  "insurance" INTEGER,
  "load_unload" BOOLEAN DEFAULT FALSE,
  "sms" BOOLEAN DEFAULT FALSE,
  "confirmed" BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ
);
