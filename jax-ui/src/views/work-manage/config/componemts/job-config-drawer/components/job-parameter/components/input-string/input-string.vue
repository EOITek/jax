<template>
  <div class="flex width-full">
    <el-select v-model="parameter.value"
               placeholder="请选择"
               class="width-full"
               v-if="isShowSelect">
      <el-option v-for="item in parameter.candidates"
                 :key="item"
                 :label="item"
                 :value="item" />
    </el-select>

    <el-input v-model="parameter.value"
              class="width-full"
              v-else-if="['TEXT', 'PASSWORD', null].includes(parameter.inputType)"
              :show-password="parameter.inputType === 'PASSWORD'" />

    <div class="flex-vcenter width-full" v-else>
      <el-input type="textarea"
                class="flex-1"
                :autosize="{ minRows: 2, maxRows: 4}"
                placeholder="请输入内容"
                v-model="parameter.value" />
      <p class="primary-color pointer ml-5" @click="openInputJson">编辑参数</p>
    </div>

    <!-- TODO 这个待定，暂时去掉 -->
    <!-- <p class="fc-red" v-else-if="!['TEXT', 'PASSWORD', null].includes(parameter.inputType)">数据类型错误</p> -->
  </div>
</template>
<script lang="ts" src="./input-string.ts"></script>
