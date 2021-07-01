<template>
  <el-form ref="Form" :model="configFormData" label-width="140px" :disabled="disabled">
    <el-form-item v-for="config in configData"
                  :prop="config.name"
                  :required="config.required"
                  :key="config.name">
      <template #label>
        <div class="flex-vcenter" style="display: inline-flex; width: 120px" >
          <span class="text-ellipsis"
                style="width: 100px"
                v-tooltip="config.name">{{ config.name }}</span>
          <i class="el-icon-question primary-color mlr-2" v-tooltip="config.description"></i>
        </div>
      </template>
      <el-input v-model="config.value"
                v-if="config.type === 'STRING'"
                class="width-300" />
      <el-switch v-model="config.value" v-if="config.type === 'BOOL'" />
      <el-input-number v-model="config.value"
                       v-if="config.type === 'LONG'"
                       :min="Number.MIN_SAFE_INTEGER"
                       :max="Number.MAX_SAFE_INTEGER" />
      <input-json v-model="config.value"
                  v-if="config.type === 'LIST'"
                  class="width-500"
                  :isReadonly="disabled"
                  :options="{ minLines: 3, maxLines: 10 }" />
    </el-form-item>
  </el-form>
</template>
<script lang="ts" src="./config.ts"></script>
