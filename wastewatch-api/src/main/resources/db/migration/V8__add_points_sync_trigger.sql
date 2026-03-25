-- Keep citizens.total_points in sync with points ledger
CREATE OR REPLACE FUNCTION update_citizen_total_points()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE citizens
  SET total_points = (
    SELECT COALESCE(SUM(points), 0)
    FROM points
    WHERE citizen_id = NEW.citizen_id
  )
  WHERE id = NEW.citizen_id;
  RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER sync_citizen_total_points
  AFTER INSERT ON points
  FOR EACH ROW
  EXECUTE FUNCTION update_citizen_total_points();