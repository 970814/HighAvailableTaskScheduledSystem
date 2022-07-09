drop database  if exists dtss;
create database dtss DEFAULT CHARACTER SET utf8;
show databases ;
use dtss;

drop table if exists `schedule_task`;
drop table if exists `sub_task`;
create table `schedule_task` (
    `task_id` varchar(64) NOT NULL PRIMARY KEY,  -- 该任务的唯一标识，使用zip包的SHA256值
    `name` varchar(64) DEFAULT NULL ,            -- 任务名称，用于前端展示，可以重名，可修改
    `period` long DEFAULT NULL,                  -- 执行周期
    `task_dag` varchar(8192) NOT NULL,           -- 子任务间的执行依赖关系
    `enabled` bool NOT NULL,                     -- 任务总开关
    `status` int NOT NULL,                       -- 运行状态 结束0 等待1 运行2
    `max_iter_cnt` int DEFAULT NULL              -- 最大执行次数
);
create table `sub_task` (
    `task_pid` varchar(64) NOT NULL,                -- 父任务id
    `sub_task_id` varchar(32) NOT NULL,             -- 子任务id，通常是.job文件的名称
    `activation_value` int NOT NULL,                -- 激活值
    `start_threshold` int NOT NULL,                 -- 启动阈值
    `status` int NOT NULL,                          -- 运行状态 结束0 等待1 运行2
    `command` varchar(8192) NOT NULL                -- 子任务的shell命令
);

show tables;
select * from schedule_task;
select * from sub_task;

truncate table schedule_task;
truncate table sub_task;

insert into schedule_task(task_id,period,task_dag,enabled,status,max_iter_cnt) values (
'2CFDCADF6B2C103FDE4CE1680D5F8319E3E794461BAE859D86D3E9E4AD48F2B6',
60000,
'{"subTskIds":["A","B","C","D","E"],"tskDeps":[{"startId":"A","endId":"C"},{"startId":"B","endId":"C"},{"startId":"B","endId":"D"},{"startId":"C","endId":"E"},{"startId":"D","endId":"E"}]}',
0,0,0);







