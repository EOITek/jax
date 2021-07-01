<template>
  <el-form ref="Form" :model="clusterData" label-width="150px">
    <el-form-item label="集群名字" prop="clusterName" required>
      <el-input v-model="clusterData.clusterName"
                :disabled="!!clusterDetail"
                placeholder="提交任务时指定集群名字，可以将任务运行在这个集群上" />
    </el-form-item>
    <el-form-item label="集群类型" prop="clusterType" required>
      <el-select v-model="clusterData.clusterType"
                 placeholder="请选择集群类型"
                 @change="clusterTypeChange"
                 class="width-full"
                 :disabled="!!clusterDetail">
        <el-option v-for="item in ClusterTypeList"
                   :key="item.value"
                   :label="item.label"
                   :value="item.value" />
      </el-select>
    </el-form-item>
    <el-form-item label="提交超时" prop="timeoutMs">
      <el-input-number v-model="clusterData.timeoutMs"
                       :min="Number.MIN_SAFE_INTEGER"
                       class="mr-10"
                       :max="Number.MAX_SAFE_INTEGER"
                       label="描述文字"
                       step-strictly
                       :step="1" />S
    </el-form-item>
    <el-form-item label="描述" prop="clusterDescription">
      <el-input v-model="clusterData.clusterDescription" />
    </el-form-item>

    <div v-if="clusterData.clusterType === ClusterTypeValue.sparkStandalone">
      <el-form-item label="SPARK_WEB_ADDR" prop="sparkWebUrl" key="sparkWebUrl" required>
        <el-input v-model="clusterData.sparkWebUrl" placeholder="http://localhost:8088" />
      </el-form-item>
      <el-form-item label="SPARK_MASTER" prop="sparkServer" key="sparkServer" required>
        <el-input v-model="clusterData.sparkServer" placeholder="spark://localhost:7077" />
      </el-form-item>
    </div>

    <div v-if="clusterData.clusterType === ClusterTypeValue.flinkStandalone">
      <el-form-item label="FLINK_WEB_ADDR" prop="flinkWebUrl" key="flinkWebUrl" required>
        <el-input v-model="clusterData.flinkWebUrl" placeholder="http://localhost:8081" />
      </el-form-item>
      <el-form-item label="FLINK_JOB_MASTER" prop="flinkServer" key="flinkServer" required>
        <el-input v-model="clusterData.flinkServer" placeholder="localhost:8081" />
      </el-form-item>
    </div>

    <div v-if="clusterData.clusterType === ClusterTypeValue.yarn">
      <el-form-item label="HADOOP_HOME" prop="hadoopHome" key="hadoopHome">
        <el-input v-model="clusterData.hadoopHome" placeholder="配置JAX所在服务器的路径，可用${JAX_HOME}引用相对位置" />
      </el-form-item>
      <el-form-item label="PYTHON_HOME" prop="pythonEnv" key="pythonEnv">
        <el-input v-model="clusterData.pythonEnv" placeholder="配置python环境" />
      </el-form-item>
      <el-form-item label="Kerberos Principal" prop="principal" key="principal">
        <el-input v-model="clusterData.principal" placeholder="如果hadoop集群开启认证，填写Kerberos认证对应的principal" />
      </el-form-item>
      <el-form-item label="Kerberos Keytab" prop="keytab" key="keytab">
        <el-input v-model="clusterData.keytab"
                  type="textarea"
                  :autosize="{ minRows: 3, maxRows: 6 }"
                  placeholder="如果hadoop集群开启认证，填写Kerberos认证对应的keytab文件。配置JAX所在服务器的路径，可用${JAX_HOME}引用相对位置" />
      </el-form-item>
    </div>

    <el-form-item label="Flink框架集"
                  prop="flinkOptsName"
                  key="flinkOptsName"
                  v-if="clusterData.clusterType === ClusterTypeValue.yarn
                          || clusterData.clusterType === ClusterTypeValue.flinkStandalone"
                  required>
      <el-select v-model="clusterData.flinkOptsName" placeholder="请选择框架" class="width-full">
        <el-option v-for="item in flinkFrameOptsList"
                   :key="item.flinkOptsName"
                   :label="item.flinkOptsName"
                   :value="item.flinkOptsName" />
      </el-select>
    </el-form-item>
    <el-form-item label="Spark框架集"
                  prop="sparkOptsName"
                  key="sparkOptsName"
                  v-if="clusterData.clusterType === ClusterTypeValue.yarn
                          || clusterData.clusterType === ClusterTypeValue.sparkStandalone"
                  required>
      <el-select v-model="clusterData.sparkOptsName" placeholder="请选择框架" class="width-full">
        <el-option v-for="item in sparkFrameOptsList"
                   :key="item.sparkOptsName"
                   :label="item.sparkOptsName"
                   :value="item.sparkOptsName" />
      </el-select>
    </el-form-item>
    <el-form-item key="defaultSparkCluster">
      <el-checkbox label="默认Spark运行集群"
                   v-model="clusterData.defaultSparkCluster"
                   v-if="clusterData.clusterType === ClusterTypeValue.yarn
                          || clusterData.clusterType === ClusterTypeValue.sparkStandalone" />
      <el-checkbox label="默认Flink运行集群"
                   v-model="clusterData.defaultFlinkCluster"
                   v-if="clusterData.clusterType === ClusterTypeValue.yarn
                          || clusterData.clusterType === ClusterTypeValue.flinkStandalone" />
    </el-form-item>
  </el-form>
</template>
<script lang="ts" src="./created-edit-modal.ts"></script>
