<template>
  <div class="flex-column">
    <div class="flex-1 mb-10">
      <observe-debug-table :tableData="tableData" v-show="currentDataType === IconType.table" />
      <pre v-show="currentDataType === IconType.json"
           class="height-full text-wrap json-data"
           ref="JsonData">{{ jsonData }}</pre>
    </div>
    <div class="footer flex-between">
      <data-show-type @change="handlerDataTypeChange" :defaultType="currentDataType" />
    </div>
  </div>
</template>
<script lang="ts" src="./data-show.ts"></script>
<style lang="scss" scoped>
.json-data {
  border: 1px #eee solid;
  overflow-y: auto;
  padding: 8px;
  line-height: 26px;
}
</style>
