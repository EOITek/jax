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

package com.eoi.jax.web.service;

import com.eoi.jax.web.common.ResponseCode;
import com.eoi.jax.web.common.exception.BizException;
import com.eoi.jax.web.dao.entity.TbJobShare;
import com.eoi.jax.web.dao.service.TbJobShareService;
import com.eoi.jax.web.model.job.JobResp;
import com.eoi.jax.web.model.job.JobShareReq;
import com.eoi.jax.web.model.job.JobShareResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobShareService {
    @Autowired
    private TbJobShareService tbJobShareService;

    @Autowired
    private JobService jobService;

    public List<JobShareResp> list() {
        List<JobResp> jobList = jobService.listJob();
        List<TbJobShare> shareList = tbJobShareService.list();
        List<JobShareResp> result = new ArrayList<>();
        for (TbJobShare jobShare : shareList) {
            Optional<JobResp> jobResp = jobList.stream().filter(p -> p.getJobName().equals(jobShare.getJobName())).findFirst();
            jobResp.ifPresent(resp -> result.add(new JobShareResp().respFrom(jobShare, resp)));
        }
        return result;
    }

    public JobShareResp get(String shareName) {
        TbJobShare jobShare = exist(shareName, false, true);
        JobResp jobResp = jobService.getJob(jobShare.getJobName());
        return new JobShareResp().respFrom(jobShare, jobResp);
    }

    public TbJobShare delete(String shareName) {
        TbJobShare jobShare = exist(shareName, false, true);
        tbJobShareService.removeById(shareName);
        return jobShare;
    }

    public JobShareResp create(JobShareReq req) {
        exist(req.getShareName(), true, false);
        JobResp jobResp = jobService.getJob(req.getJobName());
        TbJobShare entity = req.toEntity(new TbJobShare());
        tbJobShareService.save(entity);
        return new JobShareResp().respFrom(entity, jobResp);
    }

    public JobShareResp update(JobShareReq req) {
        exist(req.getShareName(), false, true);
        JobResp jobResp = jobService.getJob(req.getJobName());
        TbJobShare entity = req.toEntity(new TbJobShare());
        tbJobShareService.updateById(entity);
        return new JobShareResp().respFrom(entity, jobResp);
    }

    public boolean exist(String shareName) {
        return exist(shareName, false, false) != null;
    }

    private TbJobShare exist(String shareName, boolean existThrow, boolean notExistThrow) {
        TbJobShare entity = tbJobShareService.getById(shareName);
        if (entity != null && existThrow) {
            throw new BizException(ResponseCode.JOB_SHARE_EXIST);
        }
        if (entity == null && notExistThrow) {
            throw new BizException(ResponseCode.JOB_SHARE_NOT_EXIST);
        }
        return entity;
    }
}
