<template>
  <el-form :model="model" label-width="130px" ref="Form">
    <el-alert v-if="!isNew"
              class="mb-15"
              title="更新扩展包后，相同 Job 将被覆盖，重启任务使最新 Job 生效。"
              type="info"
              show-icon
              :closable="false" />
    <el-form-item label="扩展包"
                  prop="jarFile"
                  required="请选择扩展包文件">
      <el-button @click="$refs.file.click()" icon="el-icon-upload2">选择{{ this.allowType }}文件</el-button>
      <input hidden
             ref="file"
             type="file"
             :accept="allowType"
             @change="chooseFile($event.target.files && $event.target.files[0])" />
    </el-form-item>
    <el-form-item label="名称"
                  prop="jarName"
                  required
                  :rules="{ pattern: /^[A-za-z][\w-]*$/, message: '非法名称' }">
      <el-input v-model="model.jarName" :maxlength="255" :disabled="!isNew" />
    </el-form-item>
    <el-form-item label="扩展包名称" prop="jarFile" :rules="{ max: 128 }">
      <el-input v-model="model.jarFile" disabled />
    </el-form-item>
    <el-form-item label="版本号" prop="jarVersion" required :rules="{ max: 20 }">
      <el-input v-model="model.jarVersion" :maxlength="20" />
    </el-form-item>
    <el-form-item label="描述" prop="jarDescription">
      <el-input type="textarea"
                v-model="model.jarDescription" />
    </el-form-item>
    <el-form-item label="上传到HDFS" prop="clusterName">
      <el-select v-model="model.clusterName" placeholder="请选择集群名称">
        <el-option v-for="(item, index) in clusterTypeList" :key="index" :label="item" :value="item" />
      </el-select>
    </el-form-item>
    <div class="ml-50">
      <el-alert title="提示信息：对于高可用模式，上传的扩展包需要共享，所以请选择一个集群来存放扩展包" type="warning" :closable="false" />
    </div>
  </el-form>
</template>
<script src="./detail.ts" />
