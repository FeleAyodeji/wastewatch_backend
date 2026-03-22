-- Authority dashboard: filter by authority, risk, status
CREATE INDEX idx_reports_authority
    ON reports(authority_id, health_risk, status);

-- Citizen dashboard: my reports newest first
CREATE INDEX idx_reports_citizen
    ON reports(citizen_id, created_at DESC);

-- Geo proximity search for upvoting map
CREATE EXTENSION IF NOT EXISTS earthdistance CASCADE;
CREATE INDEX idx_reports_geo
    ON reports USING gist(ll_to_earth(lat::float8, lng::float8));

-- Points history for a citizen
CREATE INDEX idx_points_citizen
    ON points(citizen_id, created_at DESC);

-- LGA leaderboard
CREATE INDEX idx_citizens_lga_points
    ON citizens(lga_id, total_points DESC);