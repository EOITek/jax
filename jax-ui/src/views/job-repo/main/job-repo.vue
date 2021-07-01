<template>
  <main class="main-content">
    <h3 class="mb-20">算子管理</h3>
    <r-tabset ref="tabset">
      <router-link to="/job-repo/list">算子列表</router-link>
      <router-link to="/job-repo/jars">扩展包管理</router-link>
    </r-tabset>
  </main>
</template>

<style lang="scss" scoped>
  .main-content /deep/ nav {
    border-bottom: 0;
    bottom: 0;
    margin-bottom: 0;
  }
</style>
