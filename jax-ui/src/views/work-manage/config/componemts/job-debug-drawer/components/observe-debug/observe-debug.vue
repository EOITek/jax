<template>
  <div class="flex-column height-full" style="position: relative;">
    <!-- 头部job展示栏 -->
    <div class="header flex-vcenter width-full mb-10">
      <div class="flex-1 job-list-wrap" style="overflow: hidden;" ref="JobListWrap">
        <ul class="flex job-list" ref="JobListBox" v-if="Object.keys(observeDebugObj).length">
          <li class="job text-ellipsis plr-10 pointer"
              v-for="(value, key) in observeDebugObj"
              :key="key"
              :class="currentJobId === key && 'active'"
              @click="currentJobId = key">
            {{ `${value[0].display}[${value[0].slot}]` }}
          </li>
        </ul>
        <p class="width-full flex-center" v-else>暂无数据</p>
      </div>
      <i class="el-icon-arrow-left fs-18 pointer"
         @click="handlerjobListBoxMove('l')"></i>
      <i class="el-icon-arrow-right fs-18 pointer"
         @click="handlerjobListBoxMove('r')"></i>
    </div>

    <data-show class="flex-1" :currentData="currentData" />

  </div>
</template>
<script lang="ts" src="./observe-debug.ts"></script>
<style lang="scss" scoped>
.header {
  height: 30px;
  overflow: auto;
}

.job-list {
  width: fit-content;
  transition: all .5s;

  .job {
    height: 30px;
    text-align: center;
    line-height: 30px;
    border: 1px #eee solid;
    border-radius: 2px;
    width: 200px;

    &.active {
      background-color: $primary-color;
      color: #fff;
    }
  }
}
</style>
