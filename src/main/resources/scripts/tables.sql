DROP TABLE IF EXISTS foxminded.students CASCADE;
DROP TABLE IF EXISTS foxminded.courses CASCADE;
DROP TABLE IF EXISTS foxminded.groups CASCADE;
DROP TABLE IF EXISTS foxminded.students_courses CASCADE;


CREATE TABLE IF NOT EXISTS foxminded.groups (
group_id SERIAL PRIMARY KEY,
group_name VARCHAR (50) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS group_id_idx ON foxminded.groups (group_id);

CREATE SEQUENCE IF NOT EXISTS foxminded.global_seq START 10000;

CREATE TABLE IF NOT EXISTS foxminded.students (
student_id INT PRIMARY KEY DEFAULT nextval ('foxminded.global_seq'),
group_id INT,
first_name VARCHAR(15) NOT NULL,
last_name VARCHAR (35) NOT NULL,
FOREIGN KEY (group_id)
REFERENCES foxminded.groups (group_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS student_id_idx ON foxminded.students (student_id);

CREATE TABLE IF NOT EXISTS foxminded.courses (
course_id SERIAL PRIMARY KEY,
course_name VARCHAR (35) NOT NULL,
course_description TEXT NOT NULL,
UNIQUE (course_id, course_name)
);

CREATE UNIQUE INDEX IF NOT EXISTS course_id_idx ON foxminded.courses (course_id);