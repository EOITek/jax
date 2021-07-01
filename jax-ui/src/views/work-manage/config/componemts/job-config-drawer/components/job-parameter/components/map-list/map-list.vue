<template>
  <div class="flex-column width-full plr-10">
    <div class="flex-vcenter plr-10"
         style="margin-bottom: 16px"
         v-for="(mapParameter,index) in viewDataMap"
         :key="mapParameter.prop">
      <el-input v-model="mapParameter.key" class="flex-1" />
      <span class="mlr-5">:</span>
      <el-input v-model="mapParameter.value" class="flex-1" />

      <i class="el-icon-delete pointer fc-red fs-18 ml-10"
         @click="viewDataMap.splice(index, 1)"></i>
    </div>
    <i class="el-icon-circle-plus-outline pointer fc-primary fs-18 mb-10"
       @click="viewDataMap.push({ key: '', value: '' })"></i>
  </div>
</template>
<script lang="ts" src="./map-list.ts"></script>
