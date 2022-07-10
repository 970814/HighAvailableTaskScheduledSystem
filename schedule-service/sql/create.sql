drop database  if exists dtss;
create database dtss DEFAULT CHARACTER SET utf8;
show databases ;
use dtss;

drop table if exists `schedule_task`;
drop table if exists `sub_task`;
create table `schedule_task`
(
    `task_id`      varchar(64)   NOT NULL PRIMARY KEY, -- 该任务的唯一标识，使用zip包的SHA256值
    `name`         varchar(64) DEFAULT NULL,           -- 任务名称，用于前端展示，可以重名，可修改
    `period`       long        DEFAULT NULL,           -- 执行周期
    `task_dag`     varchar(8192) NOT NULL,             -- 子任务间的执行依赖关系
    `enabled`      bool          NOT NULL,             -- 任务总开关
    `status`       int           NOT NULL,             -- 运行状态 结束0 等待1 运行2
    `max_iter_cnt` int         DEFAULT NULL            -- 最大执行次数
);

create table `sub_task`
(
    `task_pid`         varchar(64)   NOT NULL, -- 父任务id
    `sub_task_id`      varchar(32)   NOT NULL, -- 子任务id，通常是.job文件的名称
    `activation_value` int           NOT NULL, -- 激活值
    `start_threshold`  int           NOT NULL, -- 启动阈值
    `status`           int           NOT NULL, -- 运行状态 结束0 等待1 运行2
    `command`          varchar(8192) NOT NULL  -- 子任务的shell命令
);

drop table execution_record;
create table execution_record
(
    `tx_id`          varchar(64) NOT NULL,     -- 执行事务id
    `task_id`        varchar(64) NOT NULL,     -- 定时任务id
    `sub_task_id`    varchar(32) NULL,         -- 子任务id，如果为null，表示为主任务
    `start_datetime` varchar(32) DEFAULT NULL, -- 开始时间
    `end_datetime`   varchar(32) DEFAULT NULL, -- 结束时间
    `cost_time`      int DEFAULT NULL,         -- 花费时间
    `result`         varchar(32) DEFAULT NULL, -- 结果 运行/成功/失败
    unique key `tx_task_sub_id` (`tx_id`, `task_id`, `sub_task_id`)
);




show tables;
select * from schedule_task;
select * from sub_task;
select * from execution_record;
select sub_task_id, start_datetime, end_datetime, cost_time, result, tx_id, task_id
from execution_record;



truncate table schedule_task;
truncate table sub_task;
truncate table execution_record;

insert into schedule_task(task_id,period,task_dag,enabled,status,max_iter_cnt) values (
'2CFDCADF6B2C103FDE4CE1680D5F8319E3E794461BAE859D86D3E9E4AD48F2B6',
60000,
'{"subTskIds":["A","B","C","D","E"],"tskDeps":[{"startId":"A","endId":"C"},{"startId":"B","endId":"C"},{"startId":"B","endId":"D"},{"startId":"C","endId":"E"},{"startId":"D","endId":"E"}]}',
0,0,0);






