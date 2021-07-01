<template>
  <div class="flex-column flex-1">
    <el-link :to="{ name: 'frameConfig' }" class="pt-15 mlr-15">
      <i class="el-icon-back" aria-hidden="true"></i> 返回上级
    </el-link>
    <main class="main-content">
      <h3 class="mb-20">{{ actionType }}{{ name || '框架配置集合' }}</h3>
      <el-form ref="Form" :model="frameData" label-width="140px" :disabled="!!view">
        <el-form-item label="集合名字" prop="optsName" required>
          <el-input v-model="frameData.optsName"
                    class="width-300"
                    :disabled="!!(name && !clone)" />
        </el-form-item>
        <el-form-item label="框架类型" prop="optsType" required>
          <el-select v-model="frameData.optsType"
                     placeholder="请选择框架类型"
                     class="width-300"
                     @change="optsTypeChange"
                     :disabled="!!(name && !clone)">
            <el-option v-for="item in FrameTypeList"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="版本" prop="version" required>
          <el-input v-model="frameData.version" class="width-300" />
        </el-form-item>
      </el-form>
      <ceated-edit-config :configData="frameData.optsList" :disabled="!!view" ref="CeatedEditConfig" />
      <div class="flex-center mt-35" v-if="!view">
        <el-button @click="$router.push({ name: 'frameConfig' })" class="mr-10">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </div>
    </main>
  </div>
</template>
<script lang="ts" src="./created-edit.ts"></script>
