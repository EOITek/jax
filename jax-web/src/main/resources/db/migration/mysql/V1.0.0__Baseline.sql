-- ----------------------------
--  QRTZ structure Quartz
-- ----------------------------
CREATE TABLE QRTZ_JOB_DETAILS(
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME VARCHAR(190) NOT NULL,
    JOB_GROUP VARCHAR(190) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_TRIGGERS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(190) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    JOB_NAME VARCHAR(190) NOT NULL,
    JOB_GROUP VARCHAR(190) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(190) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
    REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(190) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(10) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_CRON_TRIGGERS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(190) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    CRON_EXPRESSION VARCHAR(120) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_SIMPROP_TRIGGERS(
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(190) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_BLOB_TRIGGERS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(190) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_CALENDARS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME VARCHAR(190) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_FIRED_TRIGGERS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(190) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    INSTANCE_NAME VARCHAR(190) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    SCHED_TIME BIGINT(13) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(190) NULL,
    JOB_GROUP VARCHAR(190) NULL,
    IS_NONCONCURRENT VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_SCHEDULER_STATE (
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(190) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_LOCKS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);

-- ----------------------------
--  Table structure for `tb_cluster`
-- ----------------------------
CREATE TABLE `tb_cluster` (
    `cluster_name` varchar(255) NOT NULL,
    `cluster_type` varchar(255) NOT NULL,
    `cluster_description` varchar(255) DEFAULT NULL,
    `hadoop_home` varchar(255) DEFAULT NULL,
    `hdfs_server` varchar(255) DEFAULT NULL,
    `yarn_web_url` varchar(255) DEFAULT NULL,
    `flink_server` varchar(255) DEFAULT NULL,
    `flink_web_url` varchar(255) DEFAULT NULL,
    `spark_server` varchar(255) DEFAULT NULL,
    `spark_web_url` varchar(255) DEFAULT NULL,
    `spark_history_server` varchar(255) DEFAULT NULL,
    `python_env` varchar(255) NOT NULL,
    `principal` varchar(255) DEFAULT NULL,
    `keytab` varchar(255) DEFAULT NULL,
    `timeout_ms` bigint(20) NOT NULL,
    `default_flink_cluster` bit(1) DEFAULT NULL,
    `default_spark_cluster` bit(1) DEFAULT NULL,
    `flink_opts_name` varchar(255) DEFAULT NULL,
    `spark_opts_name` varchar(255) DEFAULT NULL,
    `create_time` bigint(20) DEFAULT NULL,
    `create_by` varchar(255) DEFAULT NULL,
    `update_time` bigint(20) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`cluster_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `tb_opts_flink`
-- ----------------------------
CREATE TABLE `tb_opts_flink` (
    `flink_opts_name` varchar(255) NOT NULL,
    `opts_description` varchar(255) DEFAULT NULL,
    `entry_jar` varchar(255) NOT NULL,
    `entry_class` varchar(255) NOT NULL,
    `job_lib` varchar(255) NOT NULL,
    `home` varchar(255) NOT NULL,
    `version` varchar(255) NOT NULL,
    `yarn_queue` varchar(255) DEFAULT NULL,
    `parallelism` bigint(20) NOT NULL,
    `time_characteristic` varchar(255) NOT NULL,
    `backend` varchar(255) NOT NULL,
    `rocks_db_path` varchar(255) NOT NULL,
    `savepoint_uri` varchar(255) NOT NULL,
    `checkpoint_uri` varchar(255) NOT NULL,
    `checkpoint_interval` bigint(20) NOT NULL,
    `min_idle_state_retention_time` bigint(20) NOT NULL,
    `max_idle_state_retention_time` bigint(20) NOT NULL,
    `yarn_task_manager_num` bigint(20) NOT NULL,
    `yarn_slots` bigint(20) NOT NULL,
    `yarn_job_manager_memory` varchar(255) NOT NULL,
    `yarn_task_manager_memory` varchar(255) NOT NULL,
    `disable_operator_chaining` bit(1) NOT NULL,
    `allow_non_restore_state` bit(1) DEFAULT NULL,
    `java_options` text DEFAULT NULL,
    `conf_list` text DEFAULT NULL,
    `other_start_args` text DEFAULT NULL,
    `runtime_mode` varchar(255) DEFAULT NULL,
    `application_mode` bit(1) DEFAULT NULL,
    `create_time` bigint(20) DEFAULT NULL,
    `create_by` varchar(255) DEFAULT NULL,
    `update_time` bigint(20) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`flink_opts_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `tb_opts_spark`
-- ----------------------------
CREATE TABLE `tb_opts_spark` (
    `spark_opts_name` varchar(255) NOT NULL,
    `opts_description` varchar(255) DEFAULT NULL,
    `entry_jar` varchar(255) NOT NULL,
    `entry_class` varchar(255) NOT NULL,
    `job_lib` varchar(255) NOT NULL,
    `home` varchar(255) NOT NULL,
    `version` varchar(255) NOT NULL,
    `yarn_queue` varchar(255) DEFAULT NULL,
    `driver_memory` varchar(255) NOT NULL,
    `executor_memory` varchar(255) NOT NULL,
    `driver_cores` bigint(20) NOT NULL,
    `executor_cores` bigint(20) NOT NULL,
    `num_executors` bigint(20) NOT NULL,
    `java_options` text DEFAULT NULL,
    `conf_list` text DEFAULT NULL,
    `other_start_args` text DEFAULT NULL,
    `create_time` bigint(20) DEFAULT NULL,
    `create_by` varchar(255) DEFAULT NULL,
    `update_time` bigint(20) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`spark_opts_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `tb_jar`
-- ----------------------------
CREATE TABLE `tb_jar` (
    `jar_name` varchar(255) NOT NULL,
    `cluster_name` varchar(255) DEFAULT NULL,
    `job_type` varchar(255) DEFAULT NULL,
    `jar_path` varchar(255) NOT NULL,
    `jar_description` text,
    `jar_version` varchar(255) DEFAULT NULL,
    `jar_file` varchar(255) DEFAULT NULL,
    `support_version` varchar(255) DEFAULT NULL,
    `create_time` bigint(20) DEFAULT NULL,
    `create_by` varchar(255) DEFAULT NULL,
    `update_time` bigint(20) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`jar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `tb_job`
-- ----------------------------
CREATE TABLE `tb_job` (
    `job_name` varchar(255) NOT NULL,
    `job_type` varchar(255) NOT NULL,
    `job_meta` text NOT NULL,
    `job_role` varchar(255) DEFAULT NULL,
    `internal` bit(1) DEFAULT NULL,
    `doc` longblob DEFAULT NULL,
    `icon` longblob DEFAULT NULL,
    `jar_name` varchar(255) NOT NULL,
    PRIMARY KEY (`job_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `tb_job_share`
-- ----------------------------
CREATE TABLE tb_job_share (
    `share_name` varchar(255) NOT NULL,
    `job_name` varchar(255) NOT NULL,
    `job_config` text NOT NULL,
    `create_time` bigint(20) DEFAULT NULL,
    `create_by` varchar(255) DEFAULT NULL,
    `update_time` bigint(20) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    PRIMARY KEY (share_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `tb_pipeline`
-- ----------------------------
CREATE TABLE `tb_pipeline` (
    `pipeline_name` varchar(255) NOT NULL,
    `cluster_name` varchar(255) NOT NULL,
    `opts_name` varchar(255) DEFAULT NULL,
    `pipeline_type` varchar(255) NOT NULL,
    `pipeline_source` varchar(255) DEFAULT NULL,
    `pipeline_config` text NOT NULL,
    `start_cmd` text DEFAULT NULL,
    `pipeline_ui` text,
    `pipeline_status` varchar(255) DEFAULT NULL,
    `internal_status` varchar(255) DEFAULT NULL,
    `pipe_description` text,
    `flink_save_point` varchar(255) DEFAULT NULL,
    `flink_check_point` varchar(255) DEFAULT NULL,
    `flink_job_id` varchar(255) DEFAULT NULL,
    `spark_rest_url` varchar(255) DEFAULT NULL,
    `spark_submission_id` varchar(255) DEFAULT NULL,
    `spark_app_id` varchar(255) DEFAULT NULL,
    `yarn_app_id` varchar(255) DEFAULT NULL,
    `track_url` varchar(255) DEFAULT NULL,
    `flink_save_point_incompatible` bit(1) DEFAULT NULL,
    `flink_save_point_disable` bit(1) DEFAULT NULL,
    `deleting` bit(1) DEFAULT NULL,
    `processing` bit(1) DEFAULT NULL,
    `process_time` bigint(20) DEFAULT NULL,
    `trigger_by` varchar(255) DEFAULT NULL,
    `create_time` bigint(20) DEFAULT NULL,
    `create_by` varchar(255) DEFAULT NULL,
    `update_time` bigint(20) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`pipeline_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `tb_pipeline_job`
-- ----------------------------
CREATE TABLE `tb_pipeline_job` (
    `pipeline_name` varchar(255) NOT NULL,
    `job_id` varchar(255) NOT NULL,
    `job_name` varchar(255) NOT NULL,
    `job_config` text NOT NULL,
    `opts` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `tb_pipeline_log`
-- ----------------------------
CREATE TABLE `tb_pipeline_log` (
    `pipeline_name` varchar(255) NOT NULL,
    `log_type` varchar(255) NOT NULL,
    `pipeline_log` longtext DEFAULT NULL,
    `create_time` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `tb_pipeline_console`
-- ----------------------------
CREATE TABLE `tb_pipeline_console` (
    `pipeline_name` varchar(255) NOT NULL,
    `op_type` varchar(255) DEFAULT NULL,
    `op_time` bigint(20) DEFAULT NULL,
    `log_content` text,
    `create_time` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `tb_spark_event`
-- ----------------------------
CREATE TABLE `tb_spark_event` (
    `pipeline_name` varchar(255) NOT NULL,
    `event_log` text,
    `create_time` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `tb_user`
-- ----------------------------
CREATE TABLE `tb_user` (
    `user_account` varchar(255) NOT NULL,
    `user_password` varchar(255) NOT NULL,
    `user_name` varchar(255) NOT NULL,
    `user_email` varchar(255) DEFAULT NULL,
    `user_phone` varchar(255) DEFAULT NULL,
    `user_disabled` int(11) DEFAULT NULL,
    `login_time` bigint(20) DEFAULT NULL,
    `session_token` varchar(255) DEFAULT NULL,
    `session_expire` bigint(20) DEFAULT NULL,
    `create_time` bigint(20) DEFAULT NULL,
    `create_by` varchar(255) DEFAULT NULL,
    `update_time` bigint(20) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`user_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

BEGIN;
INSERT INTO `tb_opts_flink`(flink_opts_name, opts_description, entry_jar, entry_class, job_lib, home, version, yarn_queue, parallelism, time_characteristic, backend, rocks_db_path, savepoint_uri, checkpoint_uri, checkpoint_interval, min_idle_state_retention_time, max_idle_state_retention_time, yarn_task_manager_num, yarn_slots, yarn_job_manager_memory, yarn_task_manager_memory, disable_operator_chaining, other_start_args, create_time, create_by, update_time, update_by) VALUES
('flink_standalone_opts', null, '\$\{JAX_HOME\}/jax/jar_lib/jax-flink-entry.jar', 'com.eoi.jax.flink_entry.FlinkMainEntry', '\$\{JAX_HOME\}/jax/jar_lib/flink', '\$\{JAX_HOME\}/flink', '1.9.1', null, '1', 'processing', 'rocksdb', 'file://\$\{JAX_HOME\}/flink/rocksdb', 'file://\$\{JAX_HOME\}/flink/savepoint', 'file://\$\{JAX_HOME\}/flink/checkpoint', '300000', '12', '24', '1', '1', '4096', '2048', b'0', '[]', '1605916800000', null, '1605916800000', null),
('flink_yarn_opts', null, '\$\{JAX_HOME\}/jax/jar_lib/jax-flink-entry.jar', 'com.eoi.jax.flink_entry.FlinkMainEntry', '\$\{JAX_HOME\}/jax/jar_lib/flink', '\$\{JAX_HOME\}/flink', '1.9.1', null, '1', 'processing', 'rocksdb', 'file:///tmp/rocksdb', '\$\{HDFS_SERVER\}/jax/flink/savepoint', '\$\{HDFS_SERVER\}/jax/flink/checkpoint', '300000', '12', '24', '1', '1', '4096', '2048', b'0', '[]', '1605916800000', null, '1605916800000', null);
INSERT INTO `tb_opts_spark`(spark_opts_name, opts_description, entry_jar, entry_class, job_lib, home, version, yarn_queue, driver_memory, executor_memory, driver_cores, executor_cores, num_executors, conf_list, other_start_args, create_time, create_by, update_time, update_by) VALUES
('spark_standalone_opts', null, '\$\{JAX_HOME\}/jax/jar_lib/jax-spark-entry.jar', 'com.eoi.jax.spark_entry.SparkMainEntry', '\$\{JAX_HOME\}/jax/jar_lib/spark', '\$\{JAX_HOME\}/spark', '2.4.1', null, '2048M', '2048M', '1', '1', '2', '[]', '[]', '1605916800000', null, '1605916800000', null),
('spark_yarn_opts', null, '\$\{JAX_HOME\}/jax/jar_lib/jax-spark-entry.jar', 'com.eoi.jax.spark_entry.SparkMainEntry', '\$\{JAX_HOME\}/jax/jar_lib/spark', '\$\{JAX_HOME\}/spark', '2.4.1', null, '2048M', '2048M', '1', '1', '2', '[\"spark.yarn.submit.waitAppCompletion=false\",\"spark.yarn.dist.archives=file:\$\{JAX_HOME\}/python/jax-algorithm.tar.gz#jax-algorithm\",\"spark.yarn.appMasterEnv.PYSPARK_PYTHON=\$\{PYTHON_ENV\}/bin/python\",\"spark.yarn.appMasterEnv.PYTHONPATH=jax-algorithm\",\"spark.executorEnv.PYTHONPATH=jax-algorithm\",\"spark.executorEnv.MKL_NUM_THREADS=1\",\"spark.executorEnv.OPENBLAS_NUM_THREADS=1\",\"spark.yarn.appMasterEnv.LANG=en_US.UTF-8\",\"spark.executorEnv.LANG=en_US.UTF-8\"]', '[]', '1605916800000', null, '1605916800000', null);
COMMIT;