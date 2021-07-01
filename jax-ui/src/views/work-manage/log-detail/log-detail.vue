<template>
  <div class="flex-column flex-1">
    <el-link :to="fromRouter" class="mlr-15 pt-15">
      <i class="el-icon-back" aria-hidden="true"></i> 返回上级
    </el-link>
    <main class="main-content flex-column"
          v-infinite-scroll="fetchData"
          :infinite-scroll-delay="200"
          :infinite-scroll-immediate="false">
      <h3 class="mb-20">{{ name + '日志详情' }}</h3>
      <el-collapse v-model="activeNames" v-if="allLogData.length" class="flex-1">
        <el-collapse-item :title="groupKey"
                          :name="groupKey"
                          v-for="(groupItem,groupKey) in LogGroupData"
                          :key="groupKey">
          <template slot="title">
            <el-tag :type="groupItem.opType === 'start' ? 'success' : 'info' " effect="dark">
              {{ groupItem.opType }}
            </el-tag>
            <span class="ml-15">{{+groupItem.opTime | formatDate }}</span>
          </template>
          <pre class="pl-15 text-wrap">{{ groupItem.displayText }}</pre>
        </el-collapse-item>
      </el-collapse>
      <div v-else class="flex-1 flex-center flex-column">
        <div class="no-data-icon"></div>
        <span class="mt-15">暂无数据</span>
      </div>
    </main>
  </div>
</template>
<script lang="ts" src="./log-detail.ts"></script>
<style lang="scss" scoped>
.main-content {
  height: calc(100vh - 100px);
  overflow:auto
}

.no-data-icon {
  width: 136px;
  height: 130px;
  background: url('~@images/no-data.svg');
}
</style>
