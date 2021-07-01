<template>
  <div class="cluster-card">
    <slot>
      <!-- 头部标题 -->
      <div class="flex header" v-if="clusterDetail">
        <p class="flex-1 pl-10 text-ellipsis"
           style="line-height: 30px"
           v-tooltip="clusterDetail.clusterName">
          {{ clusterDetail.clusterName }}
        </p>
        <div class="frame-type text-center fc-white"
             :style="{ backgroundColor: getClusterTypeValue('bgColor') }">
          {{ clusterDetail.clusterType | ClusterType}}
        </div>
      </div>

      <div class="plr-10 ptb-10" v-if="clusterDetail">
        <p class="flex mb-5">
          <span class="pr-10">{{ getClusterTypeValue('webUrlLabel')}}:</span>
          <a class="flex-1 text-ellipsis pointer"
             :href="getClusterDetailValue('webUrlField')"
             target="_blank"
             v-tooltip="getClusterDetailValue('webUrlField')">
            {{ getClusterDetailValue('webUrlField') }}
          </a>
        </p>
        <p class="flex mb-5">
          <span class="pr-10" >{{ getClusterTypeValue('serverLabel') }}:</span>
          <span class="flex-1 text-ellipsis"
                v-tooltip="getClusterDetailValue('serverField')">
            {{ getClusterDetailValue('serverField') }}
          </span>
        </p>
        <div class="flex mb-5" v-for="utilization in utilizationList" :key="utilization.label">
          <span class="pr-10" style="width: 60px">{{ utilization.label }}:</span>
          <template v-if="!utilization.loading">
            <el-progress v-if="utilization.utilization || utilization.utilization === 0"
                         :stroke-width="10"
                         :color="customColors"
                         v-tooltip="`${utilization.used} / ${utilization.total}`"
                         :percentage="utilization.utilization"
                         style="width: 175px" />
            <span v-else class="fc-red">NaN</span>
          </template>
          <i class="el-icon-loading fs-16" style="height: 16px" v-else></i>
        </div>
        <div class="flex mb-5"
             style="height: 16px"
             v-if="clusterDetail.clusterType === ClusterTypeValue.flinkStandalone"></div>
        <div class="flex-between action" style="align-items: flex-end;">
          <div class="flex" style="height: 16px">
            <p class="flex-vcenter fc-primary mr-10 pointer" @click="openCreatedEditModal">
              <i class="el-icon-edit-outline fs-16" />编辑
            </p>
            <p class="flex-vcenter fc-primary pointer" @click="deleteCluster">
              <i class="el-icon-delete fs-16"></i>删除
            </p>
          </div>
          <div class="default-icon"
               v-if="clusterDetail.defaultFlinkCluster || clusterDetail.defaultSparkCluster"></div>
        </div>
      </div>
    </slot>
  </div>
</template>
<script lang="ts" src="./cluster-card.tsx"></script>
<style src="./cluster-card.scss" lang="scss" scoped></style>
