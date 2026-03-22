CREATE TABLE upvotes (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    citizen_id  UUID        NOT NULL REFERENCES citizens(id)  ON DELETE CASCADE,
    report_id   UUID        NOT NULL REFERENCES reports(id)   ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_citizen_report UNIQUE (citizen_id, report_id)
);