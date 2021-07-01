<template>
  <el-form ref="Form" :model="optsFormData" label-width="140px">
    <el-select v-model="fieldName"
               placeholder="请选择"
               class="mr-10 width-200 mb-15">
      <el-option v-for="item in keyList"
                 :key="item.name"
                 :label="item.name"
                 :value="item.name" />
    </el-select>
    <i class="el-icon-circle-plus-outline pointer fc-primary fs-18 ml-15"
       @click="addoptsDataItem"></i>
    <el-form-item label="集群名称" >
      <el-select v-model="clusterName" class="mr-10 width-200" :disabled="!isEditClusterName">
          <el-option v-for="item in clusterList"
                     :key="item"
                     :label="item"
                     :value="item" />
        </el-select>
    </el-form-item>
    <el-form-item v-for="(opts, index) in optsDataList"
                  :prop="opts.name"
                  :label="opts.name"
                  required
                  :key="opts.name">
      <template #label>
        <div class="flex-vcenter" style="display: inline-flex; width: 120px" >
          <span class="text-ellipsis"
                style="width: 100px"
                v-tooltip="opts.name">{{ opts.name }}</span>
          <i class="el-icon-question primary-color mlr-2" v-tooltip="opts.description"></i>
        </div>
      </template>
      <div class="flex-vcenter" style="min-height: 32px">
        <el-input v-model="opts.value"
                  v-if="opts.type === 'STRING'"
                  class="width-200" />
        <el-switch v-model="opts.value" v-if="opts.type === 'BOOL'" />
        <el-input-number v-model="opts.value"
                         v-if="opts.type === 'LONG'"
                         :min="Number.MIN_SAFE_INTEGER"
                         :max="Number.MAX_SAFE_INTEGER" />
        <input-json v-model="opts.value"
                    v-if="opts.type === 'LIST'"
                    class="width-400"
                    :options="{ minLines: 3, maxLines: 10 }" />
        <i class="el-icon-delete pointer fc-red fs-18 ml-15"
           @click="optsDataList.splice(index, 1)"></i>

      </div>
    </el-form-item>
  </el-form>
</template>
<script lang="ts" src="./advanced-config-modal.ts"></script>
