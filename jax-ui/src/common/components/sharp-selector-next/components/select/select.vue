<template>
  <el-select :size="size"
             ref="Select"
             v-if="editing"
             v-model="item.value"
             automatic-dropdown
             @visible-change="visibleChange"
             @change="change">
    <el-option v-for="item in options"
               :key="item.value"
               :value="item.value"
               :label="item.label" />
  </el-select>
  <span v-else class="pointer" @click="autofocus">{{displayText}}</span>
</template>
<script src="./select.ts"></script>
