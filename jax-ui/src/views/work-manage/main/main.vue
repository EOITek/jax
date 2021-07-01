<template>
  <main class="main-content">
    <h3 class="mb-20">作业管理</h3>
    <r-tabset ref="tabset">
      <router-link :to="{ name: 'work-manage/list', params: { pipelineType: 'streaming' } }">流作业</router-link>
      <router-link :to="{ name: 'work-manage/list', params: { pipelineType: 'batch' } }">批作业</router-link>
    </r-tabset>
  </main>
</template>
