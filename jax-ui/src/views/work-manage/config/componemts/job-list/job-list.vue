<template>
  <div class="job-list width-300 ptb-10 height-full flex-column"
       style="border-right: 1px solid #F0F0F0">
    <el-input prefix-icon="el-icon-search"
              v-model="searchParam"
              class="mlr-10 mb-10"
              style="width: calc(100% - 20px)"
              placeholder="搜索关键字" />
    <el-collapse value="source"
                 accordion
                 v-if="jobList.length"
                 style="overflow-y: auto"
                 class="flex-column flex-1">
      <el-collapse-item :name="groupKey"
                        v-for="(groupItem, groupKey) in filteredGroupObj"
                        style="max-height: calc(100% - 120px)"
                        :key="groupKey">
        <template slot="title">
          <div class="height-full mr-20" :class="`bg-${groupKey}`" style="width: 5px"></div>
          <span class="fs-16">{{ groupKey | JobRoleType }}</span>
          <span style="margin-left: 195px">{{ groupItem.length }}</span>
        </template>
        <ul>
          <li draggable="true"
              class="pl-20 mr-20 job-list-item flex-vcenter"
              style="height: 40px;cursor: move;"
              v-for="job in groupItem"
              :key="job.jobName + job.shareName"
              @dragstart="handlerDragstart($event,job)">
            <el-tooltip class="item"
                        effect="dark"
                        transition=""
                        :offset="500"
                        placement="right"
                        :content="[`名称：${job.jobMeta.jobInfo.display}`,
                          `描述：${job.jobMeta.jobInfo.description}`].join('\n')">
              <div class="flex width-full">
                <img class="job-icon mr-15" :src="job.iconUrl || JobDefaultIcon[job.jobRole]">
                <p class="flex-1 text-ellipsis">
                  {{ isShare(groupKey) ? job.shareName : job.jobMeta.jobInfo.display }}
                </p>
                <i class="el-icon-delete pointer fc-red fs-18 ml-15"
                   v-if="isShare(groupKey)"
                   @click="handleRemoveJobShare(job.shareName)"></i>
              </div>
            </el-tooltip>
          </li>
        </ul>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script lang="ts" src="./job-list.ts"></script>
<style lang="scss" scoped>
::v-deep .el-collapse-item__wrap {
  height: calc(100% - 40px);
  overflow-y: auto !important;
}

::v-deep .el-collapse-item__header {
  height: 40px;
}

.job-list {
  border-right: 1px solid #F0F0F0;
  overflow: hidden;
  overflow-y: auto;
}

.job-icon {
  width: 25px;
  height: 25px;
}
</style>
