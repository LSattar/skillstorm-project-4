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
-- 2) Seed users (STAFF, ADOPTER, FOSTER); password_hash = BCrypt for "password"
-- ---------------------------------------------------------------------------
INSERT INTO users (id, email, username, password_hash, display_name, phone, is_enabled)
VALUES
  -- Staff
  ('33333333-3333-4333-8333-333333333333', 'staff1@example.com', 'staff1',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Alex Rivera', '555-0201', 1),
  ('44444444-4444-4444-8444-444444444444', 'staff2@example.com', 'staff2',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Taylor Morgan', '555-0202', 1),
  -- Adopters
  ('55555555-5555-4555-8555-555555555555', 'adopter1@example.com', 'adopter1',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Casey Brooks', '555-0301', 1),
  ('66666666-6666-4666-8666-666666666666', 'adopter2@example.com', 'adopter2',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Jamie Patel', '555-0302', 1),
  -- Fosters
  ('11111111-1111-4111-8111-111111111111', 'foster1@example.com', 'foster1',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Jordan Foster', '555-0101', 1),
  ('22222222-2222-4222-8222-222222222222', 'foster2@example.com', 'foster2',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Sam Carter', '555-0102', 1);

-- ---------------------------------------------------------------------------
-- 3) Assign roles to seed users
-- ---------------------------------------------------------------------------
INSERT INTO user_roles (user_id, role_id)
SELECT '33333333-3333-4333-8333-333333333333', id FROM roles WHERE name = 'STAFF' LIMIT 1;
INSERT INTO user_roles (user_id, role_id)
SELECT '44444444-4444-4444-8444-444444444444', id FROM roles WHERE name = 'STAFF' LIMIT 1;
INSERT INTO user_roles (user_id, role_id)
SELECT '55555555-5555-4555-8555-555555555555', id FROM roles WHERE name = 'ADOPTER' LIMIT 1;
INSERT INTO user_roles (user_id, role_id)
SELECT '66666666-6666-4666-8666-666666666666', id FROM roles WHERE name = 'ADOPTER' LIMIT 1;
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

-- ---------------------------------------------------------------------------
-- 6) Animal photos (metadata for S3 objects)
--    Bucket: ljs-animal-shelter-photos (see application.yml / project-requirements).
--    Upload objects to keys animals/<animal_id>/primary.jpg before browsing photos.
--    INSERT IGNORE allows re-running this file without duplicate s3_key errors.
-- ---------------------------------------------------------------------------
INSERT IGNORE INTO animal_photos (id, animal_id, s3_key, url, is_primary, content_type)
VALUES
(UUID(), '10000001-0000-4000-8000-000000000001', 'animals/10000001-0000-4000-8000-000000000001/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000001-0000-4000-8000-000000000001/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000002-0000-4000-8000-000000000002', 'animals/10000002-0000-4000-8000-000000000002/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000002-0000-4000-8000-000000000002/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000003-0000-4000-8000-000000000003', 'animals/10000003-0000-4000-8000-000000000003/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000003-0000-4000-8000-000000000003/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000004-0000-4000-8000-000000000004', 'animals/10000004-0000-4000-8000-000000000004/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000004-0000-4000-8000-000000000004/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000005-0000-4000-8000-000000000005', 'animals/10000005-0000-4000-8000-000000000005/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000005-0000-4000-8000-000000000005/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000006-0000-4000-8000-000000000006', 'animals/10000006-0000-4000-8000-000000000006/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000006-0000-4000-8000-000000000006/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000007-0000-4000-8000-000000000007', 'animals/10000007-0000-4000-8000-000000000007/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000007-0000-4000-8000-000000000007/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000008-0000-4000-8000-000000000008', 'animals/10000008-0000-4000-8000-000000000008/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000008-0000-4000-8000-000000000008/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000009-0000-4000-8000-000000000009', 'animals/10000009-0000-4000-8000-000000000009/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000009-0000-4000-8000-000000000009/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000010-0000-4000-8000-000000000010', 'animals/10000010-0000-4000-8000-000000000010/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000010-0000-4000-8000-000000000010/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000011-0000-4000-8000-000000000011', 'animals/10000011-0000-4000-8000-000000000011/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000011-0000-4000-8000-000000000011/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000012-0000-4000-8000-000000000012', 'animals/10000012-0000-4000-8000-000000000012/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000012-0000-4000-8000-000000000012/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000013-0000-4000-8000-000000000013', 'animals/10000013-0000-4000-8000-000000000013/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000013-0000-4000-8000-000000000013/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000014-0000-4000-8000-000000000014', 'animals/10000014-0000-4000-8000-000000000014/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000014-0000-4000-8000-000000000014/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000015-0000-4000-8000-000000000015', 'animals/10000015-0000-4000-8000-000000000015/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000015-0000-4000-8000-000000000015/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000016-0000-4000-8000-000000000016', 'animals/10000016-0000-4000-8000-000000000016/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000016-0000-4000-8000-000000000016/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000017-0000-4000-8000-000000000017', 'animals/10000017-0000-4000-8000-000000000017/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000017-0000-4000-8000-000000000017/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000018-0000-4000-8000-000000000018', 'animals/10000018-0000-4000-8000-000000000018/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000018-0000-4000-8000-000000000018/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000019-0000-4000-8000-000000000019', 'animals/10000019-0000-4000-8000-000000000019/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000019-0000-4000-8000-000000000019/primary.jpg', 1, 'image/jpeg'),
(UUID(), '10000020-0000-4000-8000-000000000020', 'animals/10000020-0000-4000-8000-000000000020/primary.jpg', 'https://ljs-animal-shelter-photos.s3.us-east-1.amazonaws.com/animals/10000020-0000-4000-8000-000000000020/primary.jpg', 1, 'image/jpeg');

-- ---------------------------------------------------------------------------
-- 7) Additional adopters for recommendation testing
-- ---------------------------------------------------------------------------
INSERT INTO users (id, email, username, password_hash, display_name, phone, is_enabled)
VALUES
  ('77777777-7777-4777-8777-777777777777', 'adopter3@example.com', 'adopter3',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Morgan Lee', '555-0303', 1),
  ('88888888-8888-4888-8888-888888888888', 'adopter4@example.com', 'adopter4',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Riley Chen', '555-0304', 1),
  ('99999999-9999-4999-8999-999999999999', 'adopter5@example.com', 'adopter5',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Avery Johnson', '555-0305', 1),
  ('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', 'adopter6@example.com', 'adopter6',
   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
   'Parker Diaz', '555-0306', 1);

INSERT INTO user_roles (user_id, role_id)
SELECT '77777777-7777-4777-8777-777777777777', id FROM roles WHERE name = 'ADOPTER' LIMIT 1;
INSERT INTO user_roles (user_id, role_id)
SELECT '88888888-8888-4888-8888-888888888888', id FROM roles WHERE name = 'ADOPTER' LIMIT 1;
INSERT INTO user_roles (user_id, role_id)
SELECT '99999999-9999-4999-8999-999999999999', id FROM roles WHERE name = 'ADOPTER' LIMIT 1;
INSERT INTO user_roles (user_id, role_id)
SELECT 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', id FROM roles WHERE name = 'ADOPTER' LIMIT 1;

-- ---------------------------------------------------------------------------
-- 8) Adopter profiles + questionnaires (designed for recommendation scoring)
-- ---------------------------------------------------------------------------
INSERT INTO adopter_profiles (
  user_id, address_line1, city, state, zip, household_size, housing_type,
  has_yard, has_kids, has_other_pets, needs_good_with_kids, needs_good_with_other_pets,
  willing_medically_complex, notes
) VALUES
  ('55555555-5555-4555-8555-555555555555', '12 Maple St', 'Hartford', 'CT', '06103', 5, 'HOUSE', 1, 1, 0, 1, 0, 0,
   'Big family with multiple kids in a busy household. Looking for family-friendly playful dog.'),
  ('66666666-6666-4666-8666-666666666666', '88 Cedar Ave Apt 4B', 'New Haven', 'CT', '06510', 2, 'APARTMENT', 0, 0, 1, 0, 1, 1,
   'Quiet home and calm environment in a small space apartment. Prefer low noise companion.'),
  ('77777777-7777-4777-8777-777777777777', '401 Elm St', 'Stamford', 'CT', '06901', 3, 'CONDO', 0, 0, 0, 0, 0, 0,
   'Very active lifestyle. Jogging and hiking every day, no yard but lots of outdoor time.'),
  ('88888888-8888-4888-8888-888888888888', '17 Pine Ln', 'Norwalk', 'CT', '06850', 1, 'HOUSE', 1, 0, 0, 0, 0, 1,
   'Experienced rescue owner with training background. Handled anxious dogs before.'),
  ('99999999-9999-4999-8999-999999999999', '230 River Rd', 'Bridgeport', 'CT', '06604', 4, 'HOUSE', 1, 1, 1, 1, 1, 0,
   'Family with kids and another gentle dog. Want calm social pet good with everyone.'),
  ('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', '9 Oak Court', 'Westport', 'CT', '06880', 1, 'APARTMENT', 0, 0, 0, 0, 0, 0,
   'Small apartment with limited space. Wants mellow pet and quiet routine.');

INSERT INTO adopter_questionnaires (
  id, user_id, schema_version, household_size, housing_type, has_yard, has_kids, has_other_pets,
  needs_good_with_kids, needs_good_with_other_pets, willing_medically_complex, notes
) VALUES
  ('f1000001-0000-4000-8000-000000000001', '55555555-5555-4555-8555-555555555555', 1, 5, 'HOUSE', 1, 1, 0, 1, 0, 0,
   'Large family and busy household. Wants energetic, playful, good with kids companion.'),
  ('f1000002-0000-4000-8000-000000000002', '66666666-6666-4666-8666-666666666666', 1, 2, 'APARTMENT', 0, 0, 1, 0, 1, 1,
   'Quiet home, calm environment, low noise. Open to medically complex pets.'),
  ('f1000003-0000-4000-8000-000000000003', '77777777-7777-4777-8777-777777777777', 1, 3, 'CONDO', 0, 0, 0, 0, 0, 0,
   'No yard and limited space, but very active lifestyle with runs daily and hiking.'),
  ('f1000004-0000-4000-8000-000000000004', '88888888-8888-4888-8888-888888888888', 1, 1, 'HOUSE', 1, 0, 0, 0, 0, 1,
   'Experienced owner, rescue background, handled anxious and reactive behavior.'),
  ('f1000005-0000-4000-8000-000000000005', '99999999-9999-4999-8999-999999999999', 1, 4, 'HOUSE', 1, 1, 1, 1, 1, 0,
   'Family-friendly needed. Must be good with kids and other pets.'),
  ('f1000006-0000-4000-8000-000000000006', 'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', 1, 1, 'APARTMENT', 0, 0, 0, 0, 0, 0,
   'Small space and low activity. Looking for mellow quiet pet.');

-- ---------------------------------------------------------------------------
-- 9) Additional animals with notes to trigger keyword/signal logic
-- ---------------------------------------------------------------------------
INSERT INTO animals (
  id, name, species, breed, sex, age_months,
  good_with_kids, good_with_other_pets, medically_complex, description, status,
  current_shelter_id, current_foster_user_id
) VALUES
  ('10000021-0000-4000-8000-000000000021', 'Scout', 'DOG', 'Border Collie Mix', 'M', 22, 1, 1, 0,
   'High energy and very active. Playful and family-friendly dog that needs exercise.', 'IN_SHELTER', 1, NULL),
  ('10000022-0000-4000-8000-000000000022', 'Misty', 'CAT', 'Russian Blue', 'F', 30, 1, 1, 0,
   'Shy and timid at first. Prefers a calm home and quiet environment with low traffic.', 'IN_SHELTER', 1, NULL),
  ('10000023-0000-4000-8000-000000000023', 'Rex', 'DOG', 'German Shepherd Mix', 'M', 40, 0, 0, 0,
   'Reactive with strong prey drive and separation anxiety. Needs special handling and experienced owner.', 'IN_SHELTER', 1, NULL),
  ('10000024-0000-4000-8000-000000000024', 'Poppy', 'DOG', 'Mixed Breed', 'F', 16, 1, 1, 1,
   'Sweet young dog, good with kids and other pets. Medically complex but stable with routine meds.', 'IN_SHELTER', 1, NULL),
  ('10000025-0000-4000-8000-000000000025', 'Winston', 'CAT', 'Domestic Shorthair', 'M', 50, 1, 0, 0,
   'Calm companion cat for apartment life and quiet evenings.', 'IN_FOSTER', NULL, '11111111-1111-4111-8111-111111111111'),
  ('10000026-0000-4000-8000-000000000026', 'Nova', 'DOG', 'Australian Cattle Dog', 'F', 26, 1, 1, 0,
   'Energetic and athletic dog. Thrives with active lifestyle and training.', 'IN_FOSTER', NULL, '22222222-2222-4222-8222-222222222222');

INSERT INTO animal_events (id, animal_id, event_type, to_shelter_id, to_foster_user_id, occurred_at, notes)
VALUES
  (UUID(), '10000021-0000-4000-8000-000000000021', 'INTAKE', 1, NULL, NOW(), 'Intake for recommendation test profile'),
  (UUID(), '10000022-0000-4000-8000-000000000022', 'INTAKE', 1, NULL, NOW(), 'Intake for recommendation test profile'),
  (UUID(), '10000023-0000-4000-8000-000000000023', 'INTAKE', 1, NULL, NOW(), 'Intake for recommendation test profile'),
  (UUID(), '10000024-0000-4000-8000-000000000024', 'INTAKE', 1, NULL, NOW(), 'Intake for recommendation test profile'),
  (UUID(), '10000025-0000-4000-8000-000000000025', 'FOSTER_MOVE', NULL, '11111111-1111-4111-8111-111111111111', NOW(), 'Placed in foster for recommendation test'),
  (UUID(), '10000026-0000-4000-8000-000000000026', 'FOSTER_MOVE', NULL, '22222222-2222-4222-8222-222222222222', NOW(), 'Placed in foster for recommendation test');
