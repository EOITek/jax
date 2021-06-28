/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.web.schedule.task;

import com.eoi.jax.web.dao.entity.TbPipeline;
import com.eoi.jax.web.schedule.async.SparkPipelineAsync;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class SparkTask extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(SparkTask.class);

    @Autowired
    private SparkPipelineAsync sparkPipelineAsync;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        TbPipeline pipeline = sparkPipelineAsync.getTodo();
        if (null != pipeline) {
            try {
                sparkPipelineAsync.execute(pipeline);
            } catch (Exception e) {
                logger.error("spark", e);
            }
        }
    }

}
