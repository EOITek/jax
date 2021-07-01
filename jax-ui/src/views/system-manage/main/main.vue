<template>
  <main class="main-content">
    <h3 class="mb-20">系统管理</h3>
    <r-tabset ref="tabset">
      <router-link :to="{ name: 'clusterManage' }">集群管理</router-link>
      <router-link :to="{ name: 'frameConfig' }">框架配置</router-link>
    </r-tabset>
  </main>
</template>
