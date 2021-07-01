<template>
  <div class="flex-column flex-1">
    <el-link :to="{ name: 'work-manage/list', params: { pipelineType: jobType } }" class="pt-15 mlr-15">
      <i class="el-icon-back" aria-hidden="true"></i> 返回上级
    </el-link>
    <main class="main-content flex-column flex-1">
      <h3 class="mb-20">运行时诊断日志</h3>
      <div class="card-wrap" style="--repeat-num: 2" >
        <el-card v-for="FinkItem in FinkDiagnoseTypeList" :key="FinkItem.value" shadow="never">
          <!-- 头部 -->
          <div slot="header" class="flex-between">
            <h3>{{ FinkItem.label }}</h3>
            <el-link v-if="FinkItem.url" type="primary" target="_blank" :href="FinkItem.url">
              {{ FinkItem.urlLabel }}
            </el-link>
            <i class="el-icon-refresh fs-20 fc-primary pointer"
               v-if="isLog(FinkItem)"
               @click="getFinkDiagnoseData(FinkItem)"></i>
          </div>
          <div v-loading="FinkItem.loading">
            <!-- 日志诊断 -->
            <DynamicScroller :items="FinkItem.data"
                             :min-item-size="10"
                             class="log-diagnose pr-10"
                             v-if="isLog(FinkItem)"
                             keyField="detectId">
              <template #default="{ item, index, active }">
                <DynamicScrollerItem :item="item" :data-index="index" :active="active">
                  <div class="flex mb-10">
                    <span class="mr-10">{{ item.detectId }}: </span>
                    <pre :style="{ color: FinkStatus[item.status] ? FinkStatus[item.status].bgColor : '' }"
                         class="text-wrap flex-1">{{ item.errorMessage }}</pre>
                  </div>
                </DynamicScrollerItem>
              </template>
            </DynamicScroller>
            <!-- 其他指标 -->
            <ul class="card-wrap plr-10 ptb-10" style="--min-width: 220px" ref="Card" v-else>
              <li v-for="item in FinkItem.data"
                  :key="item.detectId"
                  class="card flex-between mr-30 mb-10">
                {{ item.detectId | FinkDetectIdType }}
                <span class="status"
                      :class="item.errorMessage && 'pointer'"
                      @click="openErrorMessageModal(item.errorMessage)"
                      :style="{ background: FinkStatus[item.status] ? FinkStatus[item.status].bgColor : '' }"></span>
              </li>
            </ul>
          </div>
        </el-card>
      </div>
    </main>
  </div>
</template>
<script lang="ts" src="./main.tsx"></script>
<style lang="scss" src="./main.scss" scoped></style>
