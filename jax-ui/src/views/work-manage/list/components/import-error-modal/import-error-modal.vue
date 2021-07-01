<template>
  <el-collapse v-model="activeName" accordion style="height: 500px; overflow: auto">
    <el-collapse-item :title="item.f0" :name="item.f0" v-for="item in entity" :key="item.f0">
      <pre class="text-wrap">{{ item.f1 }}</pre>
    </el-collapse-item>
  </el-collapse>
</template>
<script lang="ts" src="./import-error-modal.ts"></script>
