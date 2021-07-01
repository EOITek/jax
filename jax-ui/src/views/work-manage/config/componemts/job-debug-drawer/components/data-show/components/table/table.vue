<template>
  <el-table :data="tableData" height="100%" ref="Table">
    <el-table-column :prop="key"
                     :label="key"
                     v-for="(value, key) in tableColumns"
                     :formatter="formatter"
                     show-overflow-tooltip
                     :key="key" />
  </el-table>
</template>
<script lang="ts" src="./table.ts"></script>
