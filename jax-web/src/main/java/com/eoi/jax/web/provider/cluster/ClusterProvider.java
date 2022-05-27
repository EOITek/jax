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

package com.eoi.jax.web.provider.cluster;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.web.common.config.AppConfig;
import com.eoi.jax.web.common.consts.ClusterType;
import com.eoi.jax.web.common.util.HadoopUtil;
import com.eoi.jax.web.dao.entity.TbCluster;
import com.eoi.jax.web.dao.entity.TbOptsFlink;
import com.eoi.jax.web.dao.entity.TbOptsSpark;
import com.eoi.jax.web.model.cluster.ClusterReq;
import com.eoi.jax.web.model.opts.OptsFlinkReq;
import com.eoi.jax.web.model.opts.OptsSparkReq;
import org.apache.hadoop.conf.Configuration;
import org.springframework.stereotype.Service;

@Service
public class ClusterProvider {

    public Cluster fromEntity(TbCluster clusterEntity, TbOptsFlink flinkEntity, TbOptsSpark sparkEntity) {
        return new Cluster(clusterEntity, flinkEntity, sparkEntity);
    }

    public TbOptsFlink defaultFlinkOpts() {
        return new ClusterFlinkOpts().withDefault().toDb();
    }

    public TbOptsSpark defaultSparkOpts() {
        return new ClusterSparkOpts().withDefault().toDb();
    }

    public OptsFlinkReq fillFlinkOptsReq(OptsFlinkReq req) {
        ClusterFlinkOpts flinkOpts = new ClusterFlinkOpts().withDefault();
        if (StrUtil.isEmpty(req.getHome())) {
            req.setHome(ClusterVariable.extractVariable(flinkOpts.getHome()));
        }
        if (StrUtil.isEmpty(req.getEntryJar())) {
            req.setEntryJar(ClusterVariable.extractVariable(flinkOpts.getEntryJar()));
        }
        if (StrUtil.isEmpty(req.getEntryClass())) {
            req.setEntryClass(flinkOpts.getEntryClass());
        }
        if (StrUtil.isEmpty(req.getJobLib())) {
            req.setJobLib(ClusterVariable.extractVariable(flinkOpts.getJobLib()));
        }
        if (StrUtil.isEmpty(req.getVersion())) {
            req.setVersion(flinkOpts.getVersion());
        }
        return req;
    }

    public OptsSparkReq fillSparkOptsReq(OptsSparkReq req) {
        ClusterSparkOpts sparkOpts = new ClusterSparkOpts().withDefault();
        if (StrUtil.isEmpty(req.getHome())) {
            req.setHome(ClusterVariable.extractVariable(sparkOpts.getHome()));
        }
        if (StrUtil.isEmpty(req.getEntryJar())) {
            req.setEntryJar(ClusterVariable.extractVariable(sparkOpts.getEntryJar()));
        }
        if (StrUtil.isEmpty(req.getEntryClass())) {
            req.setEntryClass(sparkOpts.getEntryClass());
        }
        if (StrUtil.isEmpty(req.getJobLib())) {
            req.setJobLib(ClusterVariable.extractVariable(sparkOpts.getJobLib()));
        }
        if (StrUtil.isEmpty(req.getVersion())) {
            req.setVersion(sparkOpts.getVersion());
        }
        return req;
    }

    public ClusterReq fillCreateClusterReq(ClusterReq req) {
        Cluster cluster = new Cluster().withDefault();
        if (ClusterType.YARN.isEqual(req.getClusterType())) {
            if (StrUtil.isEmpty(req.getHadoopHome())) {
                req.setHadoopHome(ClusterVariable.extractVariable(cluster.getHadoopHome()));
            }
            if (StrUtil.isEmpty(req.getFlinkServer())) {
                req.setFlinkServer(AppConfig.FLINK_YARN_SERVER);
            }
            if (StrUtil.isEmpty(req.getSparkServer())) {
                req.setSparkServer(AppConfig.SPARK_YARN_SERVER);
            }
        }
        fillHadoopMetaInfo(req);
        if (StrUtil.isEmpty(req.getPythonEnv())) {
            req.setPythonEnv(cluster.getPythonEnv());
        }
        return req;
    }

    public ClusterReq fillUpdateClusterReq(ClusterReq req) {
        fillHadoopMetaInfo(req);
        return req;
    }

    private ClusterReq fillHadoopMetaInfo(ClusterReq req) {
        if (ClusterType.YARN.isEqual(req.getClusterType()) && StrUtil.isNotEmpty(req.getHadoopHome())) {
            Configuration hadoopConf = HadoopUtil.getHadoopConf(req.getHadoopHome());
            if (null != hadoopConf) {
                req.setHdfsServer(hadoopConf.get(HadoopUtil.HDFS_DEFAULT_NAME_KEY));
                req.setYarnWebUrl(HadoopUtil.getYarnWebUrl(hadoopConf));
            }
        }
        return req;
    }
}
