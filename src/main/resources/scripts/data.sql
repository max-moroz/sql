DELETE FROM foxminded.students;
DELETE FROM foxminded.groups;
DELETE FROM foxminded.courses;


ALTER SEQUENCE foxminded.global_seq RESTART WITH 10000;
