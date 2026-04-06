INSERT INTO APP_USER (username, password, role) VALUES
    ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ADMIN');

INSERT INTO GYM_CLASS (name, instructor, description, day_of_week, start_time, duration_minutes, max_participants)
VALUES
    ( 'Swamp Mud Conditioning', 'Shrek',
     'Full body conditioning in knee-dep swamp mud.',
     'Tuesday', '07:30', 55, 4),
    ( 'Royal Ego Bench Press', 'Prince Charming',
     'Upper body strength training with mirror breaks between sets.',
     'Thursday', '18:00', 60, 10),
    ( 'Warrior Core Clash', 'Fiona',
     'Combat inspired core training', 'Sunday', '10:15',
     40, 15);

INSERT INTO BOOKING (participant_name, email, booked_at, gym_class_id)
VALUES
    ('Puss in Boots', 'puss@boots.com', NOW(), 1),
    ('Gingerbread Man', 'gingy@cookie.com', NOW(), 1),
    -- Jag blev tvingad av Fred
    ('Lars Strömberg', 'lars@edugrade.se', NOW(), 1),

    ('Big Bad Wolf', 'wolf@storybook.com', NOW(), 2),
    ('Three Blind Mice', 'mice@storybook.com', NOW(), 2),

    ('Dragon', 'dragon@swamp.com', NOW(), 3),
    ('Donkey', 'donkey@swamp.com', NOW(), 3);