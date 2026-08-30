CREATE TYPE organization_status AS ENUM ('ACTIVE', 'INACTIVE');

ALTER TABLE organizations ADD COLUMN IF NOT EXISTS status organization_status;

UPDATE organizations
SET status = CASE
    WHEN is_active = TRUE THEN 'ACTIVE'::organization_status
    ELSE 'INACTIVE'::organization_status
END;

ALTER TABLE organizations
  ALTER COLUMN status SET DEFAULT 'ACTIVE'::organization_status,
  ALTER COLUMN status SET NOT NULL;

ALTER TABLE organizations DROP COLUMN is_active;