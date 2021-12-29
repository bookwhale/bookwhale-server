#schema 선택
use bookwhale;

#character utf8mb4 확인
show variables like 'c%';

#Timezone 확인 후 설정
select @@time_zone, now();
set time_zone = 'Asia/Seoul';
