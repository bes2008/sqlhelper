select *
from (
     select ID_, SOFTWARE_ID_
     from TM_SOFTWARE_INSTANCE
    where 1 =1 and (
        lower(NAME_) like CONCAT(CONCAT('%',?),'%')
        or lower(VERSION_) like CONCAT(CONCAT('%',?),'%')
    )
    order by NAME_
)