<template>
  <main>
    <header class="flex flex-vcenter">
      <el-dropdown trigger="click">
        <el-button type="primary" :disabled="!selection.length || !!loading">
          批量操作 <i class="el-icon-arrow-down" aria-hidden="true"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="updateStatus(selection, 'start')">启动</el-dropdown-item>
          <el-dropdown-item @click.native="stopAffirm(selection)">停止</el-dropdown-item>
          <el-dropdown-item @click.native="remove(selection)">删除</el-dropdown-item>
          <el-dropdown-item @click.native="handleExport(selection)" v-if="!isHistory">导出</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-input class="ml-10 width-400"
                v-model="search"
                placeholder="按作业名称和描述筛选"
                @change="fetchData"
                clearable />
      <el-select class="ml-10 width-300"
                 v-if="isHistory"
                 v-model="triggerBy"
                 @change="fetchData"
                 clearable
                 placeholder="请选择批作业">
        <el-option v-for="(item, index) in fetchWorks" :key="index" :label="item" :value="item" />
      </el-select>
      <el-link icon="el-icon-refresh"
               type="primary"
               class="ml-10 fs-18"
               @click="fetchData"
               :disabled="!!loading" />
      <el-button v-if="!isHistory"
                 class="ml-auto"
                 v-tooltip="['支持导入.pipeline文件', '支持导入由多个.pipeline文件压缩成的.zip文件'].join('\n')"
                 @click="$refs.InputFile.click()"
                 icon="el-icon-download">导入pipeline</el-button>
      <input hidden
             ref="InputFile"
             type="file"
             accept=".zip,.pipeline"
             @change="chooseFile($refs.InputFile.files[0])" />
      <el-button class="ml-15"
                 type="primary"
                 v-if="!isHistory"
                 @click="$router.push({
                   name: 'work-manage/config',
                   params: { id: isStreaming ? 'create_streaming' : 'create_batch' },
                   query: { jobType: isStreaming ? 'streaming': 'batch' }
                 })">
        创建作业
      </el-button>
    </header>
    <el-table class="mt-15"
              :data="tableData"
              v-loading="!!loading"
              ref="Table"
              @selection-change="x => selection = x"
              row-key="pipelineName"
              @filter-change="filterChange">
      <el-table-column type="selection" width="42" />
      <el-table-column prop="pipelineName"
                       label="作业名称"
                       show-overflow-tooltip/>
      <el-table-column prop="pipeDescription"
                       label="作业描述"
                       show-overflow-tooltip />
      <el-table-column width="100"
                       prop="pipelineStatus"
                       column-key="pipelineStatus"
                       :filters="filterOptions.pipelineStatus"
                       filter-placement="bottom"
                       label="状态"
                       #default="{ row }">
        <pipeline-status :value="row.pipelineStatus" />
      </el-table-column>
      <el-table-column width="100"
                       prop="internalStatus"
                       label="内部状态"
                       column-key="internalStatus"
                       :filters="filterOptions.internalStatus" />
      <el-table-column width="150"
                       prop="updateTime"
                       label="更新时间"
                       formatter="formatDate" />
      <el-table-column :width="isHistory ? 120: 200" label="操作" #default="{ row }">
        <el-link type="primary"
                 v-if="!isHistory"
                 :to="{
                   name: 'work-manage/config',
                   params: { id: row.pipelineName },
                   query: { jobType: row.pipelineType }
                 }">
          作业编排
        </el-link>
        <el-link class="ml-10" type="primary" @click="showOperationLog(row)">操作日志</el-link>
        <el-dropdown trigger="click" class="ml-10">
          <el-link type="primary" class="fs-12">更多</el-link>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="updateStatus([row], 'start')">启动</el-dropdown-item>
            <el-dropdown-item @click.native="stopAffirm([row])">停止</el-dropdown-item>
            <el-dropdown-item @click.native="remove([row])">删除</el-dropdown-item>
            <el-dropdown-item @click.native="clonePipeline(row)" v-if="!isHistory">克隆</el-dropdown-item>
            <el-dropdown-item v-if="row.trackUrl" @click.native="openTrack(row)">追踪</el-dropdown-item>
            <el-dropdown-item v-if="row.pipelineStatus === 'RUNNING' && isStreaming"
                              @click.native="openFinkDiagnose(row)">
              诊断
            </el-dropdown-item>
            <el-dropdown-item v-if="isShowDashboardUrl(row, 'detailUrl')"
                              @click.native="openDashboard(row.dashboard && row.dashboard.detailUrl)">
              仪表盘详情
            </el-dropdown-item>
            <el-dropdown-item v-if="isShowDashboardUrl(row, 'summaryUrl')"
                              @click.native="openDashboard(row.dashboard && row.dashboard.summaryUrl)">
              仪表盘概览
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-table-column>
    </el-table>
    <sharp-pagination :page-conf="pageConf" />
  </main>
</template>
<script src="./list.tsx" />
<style lang="scss" scoped>
span {
  &[data-value=WAITING_START], &[data-value=STARTING], &[data-value=RUNNING], &[data-value=RESTARTING], &[data-value=FINISHED] {
    color: #7EC42C;
  }

  &[data-value=WAITING_STOP], &[data-value=STOPPING], &[data-value=STOPPED], &[data-value=DELETING] {
    color: #F5A623;
  }

  &[data-value=START_FAILED], &[data-value=STOP_FAILED], &[data-value=FAILED] {
    color: #ED0000;
  }
}
</style>
