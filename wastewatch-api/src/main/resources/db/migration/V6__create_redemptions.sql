CREATE TYPE redemption_type   AS ENUM ('AIRTIME', 'DATA_BUNDLE');
CREATE TYPE redemption_status AS ENUM ('PENDING', 'SUCCESS', 'FAILED', 'REVERSED');

CREATE TABLE redemptions (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    citizen_id       UUID              NOT NULL REFERENCES citizens(id) ON DELETE CASCADE,
    type             redemption_type   NOT NULL,
    points_spent     INTEGER           NOT NULL,
    phone_number     VARCHAR(20)       NOT NULL,
    interswitch_ref  VARCHAR(100),
    status           redemption_status NOT NULL DEFAULT 'PENDING',
    created_at       TIMESTAMPTZ       NOT NULL DEFAULT now()
);