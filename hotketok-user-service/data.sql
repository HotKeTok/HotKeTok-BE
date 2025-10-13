-- in user-service/src/main/resources/data.sql

-- H2 DB는 기본적으로 ID가 1부터 자동 증가합니다.
-- ID 101, 102를 맞추기 위해 더미 데이터를 넣거나 ID 값을 직접 지정하는 로직이 필요할 수 있습니다.
-- 아래는 자동 증가를 활용하는 예시입니다.

INSERT INTO USERS (LOG_IN_ID, PASSWORD, NAME, PHONE_NUMBER, CURRENT_ADDRESS, ROLE)
VALUES ('user101', 'password123', '가나다', '010-1234-5678', '동작구 상도로', 'OWNER');

INSERT INTO USERS (LOG_IN_ID, PASSWORD, NAME, PHONE_NUMBER, CURRENT_ADDRESS, ROLE)
VALUES ('user102', 'password123', '라마바', '010-1234-5679', '동작구 상도로', 'TENANT');

INSERT INTO USERS (LOG_IN_ID, PASSWORD, NAME, PHONE_NUMBER, CURRENT_ADDRESS, ROLE)
VALUES ('user103', 'password123', '메종 인테리어', '02-123-4567', '숭실대학교', 'VENDOR');