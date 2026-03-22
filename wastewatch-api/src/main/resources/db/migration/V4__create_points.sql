CREATE TYPE transaction_type AS ENUM (
    'SUBMISSION', 'RESOLUTION_BONUS', 'UPVOTE', 'REDEMPTION', 'REVERSAL'
);

CREATE TABLE points (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    citizen_id       UUID            NOT NULL REFERENCES citizens(id) ON DELETE CASCADE,
    points           INTEGER         NOT NULL,
    transaction_type transaction_type NOT NULL,
    reference_id     UUID,
    created_at       TIMESTAMPTZ     NOT NULL DEFAULT now()
);