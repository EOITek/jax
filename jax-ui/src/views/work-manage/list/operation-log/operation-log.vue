<template>
  <div>
    <header class="text-right">
      <el-link class="fc-primary"
              :to="{ name: 'WorkManageLogDetail',
                params: { name: model.pipelineName, jobType: model.pipelineType },
                query: { isHistory } }">
        日志详情
        <i class="el-icon-right"></i>
      </el-link>
    </header>
    <div class="table-wrapper mt-10">
      <el-table :data="tableData"
                ref="Table"
                highlight-current-row
                @current-change="currentRow = $event"
                style="min-height: 360px">
        <el-table-column type="index" width="40" />
        <el-table-column width="150" prop="createTime" label="创建时间" formatter="formatDate" />
        <el-table-column width="80" prop="logType" label="日志类型" />
        <el-table-column label="日志内容" />
      </el-table>
      <aside v-if="currentRow"
             v-text="currentRow.pipelineLog"
             class="font-din text-pre-wrap fs-12"></aside>
    </div>
    <sharp-pagination :page-conf="pageConf" @change="selectFirstRow" />
  </div>
</template>
<script src="./operation-log.ts" />
<style scoped>
  .table-wrapper {
    position: relative;
  }

  .text-pre-wrap {
    padding: 8px 10px;
    position: absolute;
    top: 30px;
    right: 1px;
    bottom: 1px;
    background: white;
    width: 712px;
    overflow-y: scroll;
    border-left: 1px solid #EBEEF5;
  }
</style>
