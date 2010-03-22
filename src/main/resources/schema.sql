EXEC dbms_aqadm.create_queue_table(queue_table=>'JMS_QUEUE_TABLE', queue_payload_type=>'sys.aq$_jms_text_message',multiple_consumers=>false);
EXEC dbms_aqadm.create_queue(queue_name=>'testmq', queue_table=>'JMS_QUEUE_TABLE');
EXEC dbms_aqadm.start_queue(queue_name=>'testmq');

create table work_item_tbl (id NUMBER UNIQUE, text VARCHAR2(255));