<template>
  <div class="menu-box">
    <router-link class="link" to="/work-manage">作业管理</router-link>
    <router-link class="link ml-10" to="/job-repo">算子管理</router-link>
    <router-link class="link ml-10" to="/system-manage">系统管理</router-link>
  </div>
</template>
<style lang="scss" scoped>
.menu-box {
  display: flex;
  flex: 1;

  .link {
    font-size: 14px;
    font-weight: 400;
    text-decoration: none;
    color: white;
    line-height: 42px;
    padding: 0 20px;
    display: block;
  }

  .router-link-active {
    font-weight: 600;
    color: white;
    background: rgba(255,255,255,0.10);
  }
}
</style>
