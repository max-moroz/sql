--show group with less than 10 students
SELECT g.group_name, COUNT(s.group_id)
FROM foxminded.groups AS g
LEFT JOIN foxminded.students AS s ON g.group_id = s.group_id
GROUP BY g.group_name
HAVING COUNT(*) < 10;

--show group with 10 students
SELECT g.group_name, COUNT(s.group_id)
FROM foxminded.groups AS g
LEFT JOIN foxminded.students AS s ON g.group_id = s.group_id
GROUP BY g.group_name
HAVING COUNT(*) = 10;

--show > 10 students group
SELECT g.group_name, COUNT(s.group_id)
FROM foxminded.groups AS g
LEFT JOIN foxminded.students AS s ON g.group_id = s.group_id
GROUP BY g.group_name
HAVING COUNT(*) > 10;

--show group with nil students
SELECT g.group_name, COUNT(s.group_id)
FROM foxminded.groups AS g
LEFT JOIN foxminded.students AS s ON g.group_id = s.group_id
GROUP BY g.group_name
HAVING COUNT(s.group_id) = 0;
