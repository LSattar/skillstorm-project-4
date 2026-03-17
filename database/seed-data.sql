-- Sample / seed data for Animal Shelter Management Platform.
-- Run schema.sql first (creates database, tables, and roles).
-- Intended for a fresh database; re-running may duplicate rows.

USE animal_shelter;

-- ---------------------------------------------------------------------------
-- 1) One shelter
-- ---------------------------------------------------------------------------
INSERT INTO shelters (name, address_line1, city, state, zip, capacity_total)
VALUES ('Skillstorm Animal Shelter', '4500 Rescue Way', 'West Hartford', 'CT', '06119', 50);

-- ---------------------------------------------------------------------------
-- 2) Two foster users (FOSTER role); password_hash = BCrypt for "password"
-- ---------------------------------------------------------------------------
INSERT INTO users (id, email, username, password_hash, display_name, phone, is_enabled)
VALUES
  ('11111111-1111-4111-8111-111111111111', 'foster1@example.com', 'foster1',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Jordan Foster', '555-0101', 1),
  ('22222222-2222-4222-8222-222222222222', 'foster2@example.com', 'foster2',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Sam Carter', '555-0102', 1);

-- ---------------------------------------------------------------------------
-- 3) Assign FOSTER role to both users
-- ---------------------------------------------------------------------------
INSERT INTO user_roles (user_id, role_id)
SELECT '11111111-1111-4111-8111-111111111111', id FROM roles WHERE name = 'FOSTER' LIMIT 1;
INSERT INTO user_roles (user_id, role_id)
SELECT '22222222-2222-4222-8222-222222222222', id FROM roles WHERE name = 'FOSTER' LIMIT 1;

-- ---------------------------------------------------------------------------
-- 4) Twenty animals (all at shelter id 1, IN_SHELTER)
--    Four cats: Toby, Nala, Keith, Patrick. Remainder: mix of dogs and cats.
-- ---------------------------------------------------------------------------
INSERT INTO animals (
  id, name, species, breed, sex, age_months,
  good_with_kids, good_with_other_pets, medically_complex, description, status,
  current_shelter_id, current_foster_user_id
) VALUES
  -- Cats: Toby, Nala, Keith, Patrick
  ('10000001-0000-4000-8000-000000000001', 'Toby', 'CAT', 'Domestic Shorthair', 'M', 24, 1, 1, 0, 'Friendly brown tabby who loves naps and treats.', 'IN_SHELTER', 1, NULL),
  ('10000002-0000-4000-8000-000000000002', 'Nala', 'CAT', 'Domestic Shorthair', 'F', 18, 1, 0, 0, 'Vocal and affectionate. Prefers to be the only pet.', 'IN_SHELTER', 1, NULL),
  ('10000003-0000-4000-8000-000000000003', 'Keith', 'CAT', 'Siamese Mix', 'M', 36, 1, 1, 0, 'Gentle giant with a calm demeanor. Good with kids.', 'IN_SHELTER', 1, NULL),
  ('10000004-0000-4000-8000-000000000004', 'Patrick', 'CAT', 'Domestic Longhair', 'M', 12, 0, 1, 0, 'Playful young cat. Best in a home without small children.', 'IN_SHELTER', 1, NULL),
  -- Additional cats and dogs
  ('10000005-0000-4000-8000-000000000005', 'Luna', 'CAT', 'Tabby', 'F', 8, 1, 1, 0, 'Kitten full of energy. Loves toys and climbing.', 'IN_SHELTER', 1, NULL),
  ('10000006-0000-4000-8000-000000000006', 'Max', 'DOG', 'Labrador Retriever', 'M', 48, 1, 1, 0, 'Friendly family dog. House-trained and leash-trained.', 'IN_SHELTER', 1, NULL),
  ('10000007-0000-4000-8000-000000000007', 'Bella', 'DOG', 'Golden Retriever', 'F', 60, 1, 1, 0, 'Calm and loving. Great with kids and other dogs.', 'IN_SHELTER', 1, NULL),
  ('10000008-0000-4000-8000-000000000008', 'Charlie', 'DOG', 'Beagle', 'M', 30, 1, 1, 0, 'Curious and sweet. Enjoys long walks and sniffing.', 'IN_SHELTER', 1, NULL),
  ('10000009-0000-4000-8000-000000000009', 'Daisy', 'DOG', 'German Shepherd', 'F', 24, 1, 0, 0, 'Loyal and protective. Needs a home without other pets.', 'IN_SHELTER', 1, NULL),
  ('10000010-0000-4000-8000-000000000010', 'Oliver', 'CAT', 'British Shorthair', 'M', 14, 1, 1, 0, 'Chill and cuddly. Adapts well to quiet homes.', 'IN_SHELTER', 1, NULL),
  ('10000011-0000-4000-8000-000000000011', 'Rocky', 'DOG', 'Boxer', 'M', 20, 1, 1, 0, 'Energetic and playful. Needs an active family.', 'IN_SHELTER', 1, NULL),
  ('10000012-0000-4000-8000-000000000012', 'Molly', 'DOG', 'Cocker Spaniel', 'F', 72, 1, 1, 1, 'Senior sweetheart. On daily medication but very loving.', 'IN_SHELTER', 1, NULL),
  ('10000013-0000-4000-8000-000000000013', 'Simba', 'CAT', 'Orange Tabby', 'M', 10, 1, 1, 0, 'Young and adventurous. Loves window perches.', 'IN_SHELTER', 1, NULL),
  ('10000014-0000-4000-8000-000000000014', 'Buddy', 'DOG', 'Mixed Breed', 'M', 36, 1, 1, 0, 'Medium-sized mutt with a big heart. Good all-around.', 'IN_SHELTER', 1, NULL),
  ('10000015-0000-4000-8000-000000000015', 'Chloe', 'CAT', 'Calico', 'F', 28, 0, 1, 0, 'Independent and dignified. Prefers older adopters.', 'IN_SHELTER', 1, NULL),
  ('10000016-0000-4000-8000-000000000016', 'Cooper', 'DOG', 'Australian Shepherd', 'M', 18, 1, 1, 0, 'Smart and active. Would excel with training and jobs.', 'IN_SHELTER', 1, NULL),
  ('10000017-0000-4000-8000-000000000017', 'Zoe', 'DOG', 'Poodle', 'F', 42, 1, 1, 0, 'Hypoallergenic coat. Gentle and trainable.', 'IN_SHELTER', 1, NULL),
  ('10000018-0000-4000-8000-000000000018', 'Felix', 'CAT', 'Black Domestic Shorthair', 'M', 6, 1, 1, 0, 'Kitten. Very social and ready for a forever home.', 'IN_SHELTER', 1, NULL),
  ('10000019-0000-4000-8000-000000000019', 'Duke', 'DOG', 'Rottweiler mix', 'M', 24, 1, 1, 0, 'Strong and loyal. Needs experienced owner.', 'IN_SHELTER', 1, NULL),
  ('10000020-0000-4000-8000-000000000020', 'Pepper', 'OTHER', 'Guinea Pig', 'F', 8, 1, 1, 0, 'Sweet guinea pig looking for a calm home.', 'IN_SHELTER', 1, NULL);

-- ---------------------------------------------------------------------------
-- 5) INTAKE events for each animal (audit consistency)
-- ---------------------------------------------------------------------------
INSERT INTO animal_events (id, animal_id, event_type, to_shelter_id, occurred_at)
VALUES
  (UUID(), '10000001-0000-4000-8000-000000000001', 'INTAKE', 1, NOW()),
  (UUID(), '10000002-0000-4000-8000-000000000002', 'INTAKE', 1, NOW()),
  (UUID(), '10000003-0000-4000-8000-000000000003', 'INTAKE', 1, NOW()),
  (UUID(), '10000004-0000-4000-8000-000000000004', 'INTAKE', 1, NOW()),
  (UUID(), '10000005-0000-4000-8000-000000000005', 'INTAKE', 1, NOW()),
  (UUID(), '10000006-0000-4000-8000-000000000006', 'INTAKE', 1, NOW()),
  (UUID(), '10000007-0000-4000-8000-000000000007', 'INTAKE', 1, NOW()),
  (UUID(), '10000008-0000-4000-8000-000000000008', 'INTAKE', 1, NOW()),
  (UUID(), '10000009-0000-4000-8000-000000000009', 'INTAKE', 1, NOW()),
  (UUID(), '10000010-0000-4000-8000-000000000010', 'INTAKE', 1, NOW()),
  (UUID(), '10000011-0000-4000-8000-000000000011', 'INTAKE', 1, NOW()),
  (UUID(), '10000012-0000-4000-8000-000000000012', 'INTAKE', 1, NOW()),
  (UUID(), '10000013-0000-4000-8000-000000000013', 'INTAKE', 1, NOW()),
  (UUID(), '10000014-0000-4000-8000-000000000014', 'INTAKE', 1, NOW()),
  (UUID(), '10000015-0000-4000-8000-000000000015', 'INTAKE', 1, NOW()),
  (UUID(), '10000016-0000-4000-8000-000000000016', 'INTAKE', 1, NOW()),
  (UUID(), '10000017-0000-4000-8000-000000000017', 'INTAKE', 1, NOW()),
  (UUID(), '10000018-0000-4000-8000-000000000018', 'INTAKE', 1, NOW()),
  (UUID(), '10000019-0000-4000-8000-000000000019', 'INTAKE', 1, NOW()),
  (UUID(), '10000020-0000-4000-8000-000000000020', 'INTAKE', 1, NOW());
