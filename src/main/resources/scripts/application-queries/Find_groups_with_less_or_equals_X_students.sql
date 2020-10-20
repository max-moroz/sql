SELECT g.group_name, COUNT(s.group_id)
FROM foxminded.groups AS g
LEFT JOIN foxminded.students AS s
ON g.group_id = s.group_id
GROUP BY g.group_name
HAVING COUNT(*) <= ?;
