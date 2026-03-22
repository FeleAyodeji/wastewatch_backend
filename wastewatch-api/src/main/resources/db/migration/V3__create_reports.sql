CREATE TYPE waste_category AS ENUM (
    'SEWAGE', 'SOLID_WASTE', 'HAZARDOUS', 'MEDICAL_WASTE', 'E_WASTE'
);

CREATE TYPE health_risk AS ENUM ('HIGH', 'MEDIUM');

CREATE TYPE report_status AS ENUM (
    'SUBMITTED', 'ACKNOWLEDGED', 'IN_PROGRESS', 'RESOLVED', 'ESCALATED'
);

CREATE TABLE reports (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    citizen_id           UUID REFERENCES citizens(id)    ON DELETE SET NULL,
    authority_id         UUID REFERENCES authorities(id) ON DELETE SET NULL,
    lat                  DECIMAL(9, 6)   NOT NULL,
    lng                  DECIMAL(9, 6)   NOT NULL,
    address              TEXT,
    photo_url            TEXT,
    waste_category       waste_category  NOT NULL,
    is_anonymous         BOOLEAN         NOT NULL DEFAULT false,
    status               report_status   NOT NULL DEFAULT 'SUBMITTED',
    health_risk          health_risk     NOT NULL,
    health_warning       TEXT,
    community_alert      BOOLEAN         NOT NULL DEFAULT false,
    upvote_count         INTEGER         NOT NULL DEFAULT 0,
    created_at           TIMESTAMPTZ     NOT NULL DEFAULT now(),
    resolved_at          TIMESTAMPTZ,
    escalated_at         TIMESTAMPTZ,
    resolution_photo_url TEXT
);