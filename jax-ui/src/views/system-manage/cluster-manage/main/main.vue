<template>
  <div class="main">
    <div class="flex flex-wrap plr-15" v-if="clusterList.length">
      <cluster-card class="mr-10 mb-10"
                    @fetchData="fetchData"
                    v-for="cluster in clusterList"
                    :key="cluster.clusterName"
                    :clusterDetail="cluster" />
      <cluster-card class="flex-center flex-column fc-primary pointer" @click.native="openCreatedEditModal">
        <i class="el-icon-plus add-icon mb-2"></i>
        <p>添加</p>
      </cluster-card>
    </div>
    <div v-else class="height-full flex-center flex-column">
      <div class="no-data-icon"></div>
      <p class="mt-25 mb-15">空空如也，您可以</p>
      <el-button type="primary"
                 class="mb-10"
                 @click.native="openCreatedEditModal"
                 style="width: 116px">
        创建集群
      </el-button>
    </div>
  </div>
</template>
<script lang="ts" src="./main.ts"></script>
<style lang="scss" scoped>
.main {
  height: calc(100vh - 175px);
  overflow-y: auto;
  margin: 0 -15px;
}

.add-icon {
  font-size: 36px;
  font-weight: 900;
}

.no-data-icon {
  width: 136px;
  height: 130px;
  background: url('~@images/no-data.svg');
}
</style>
