show databases;
use dtss;

create table `schedule_task`
(
    `task_id` varchar(64) NOT NULL PRIMARY KEY, -- 该任务的唯一标识，使用zip包的SHA256值
    `period` int DEFAULT NULL,                  -- 执行周期
    `task_dag` varchar(8192) NOT NULL,          -- 子任务间的执行依赖关系
    `enabled` bool NOT NULL,                    -- 任务总开关
    `status` int NOT NULL,                      -- 运行状态 结束0 等待1 运行2
    `max_iter_cnt` int DEFAULT NULL             -- 最大执行次数
);
show tables;
select * from schedule_task;

create table `sub_task`
(
    `task_pid` varchar(64) NOT NULL,                -- 父任务id
    `sub_task_id` varchar(32) NOT NULL,             -- 子任务id，通常是.job文件的名称
    `activation_value` int NOT NULL,                -- 激活值
    `start_threshold` int NOT NULL,                 -- 启动阈值
    `status` int NOT NULL,                          -- 运行状态 结束0 等待1 运行2
    `command` varchar(8192) NOT NULL                -- 子任务的shell命令
);






