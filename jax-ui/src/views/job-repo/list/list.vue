<template>
  <main>
    <div class="mb-10 mt-10">
      <el-input class="width-400"
                v-model="search"
                placeholder="按名称，类型，显示，描述进行筛选"
                @change="fetchData"
                clearable />
      <el-link icon="el-icon-refresh"
               type="primary"
               class="ml-10 fs-18"
               @click="fetchData"
               :disabled="!!loading" />
    </div>
    <el-table :data="tableData" v-loading="!!loading" ref="Table" row-key="jobName">
      <el-table-column type="expand" #default="tableRow">
        <el-table :data="tableRow.row.jobMeta.parameters.parameters" row-key="name">
          <el-table-column prop="name" label="名称" width="150" />
          <el-table-column prop="label" label="标签" width="150" />
          <el-table-column prop="type" label="类型" width="80" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="optional" label="可选" formatter="yesNo" width="80" />
          <el-table-column prop="defaultValue" label="默认值" width="120" />
        </el-table>
      </el-table-column>
      <el-table-column width="180" prop="jobMeta.jobInfo.name" label="名称" show-overflow-tooltip />
      <el-table-column width="100" prop="jobType" label="类型" />
      <el-table-column width="200" prop="jobMeta.jobInfo.display" label="显示" show-overflow-tooltip />
      <el-table-column prop="jobMeta.jobInfo.description" label="描述" show-overflow-tooltip />
      <el-table-column width="280" label="操作" #default="{ row }">
        <el-link type="primary"
                 class="ml-10"
                 :to="{ name: 'work-manage/list',
                   params: { pipelineType: row.jobType },
                   query: { jobName: row.jobName } }">查看关联作业</el-link>
        <el-link type="primary" class="ml-10" v-if="row.hasDoc" @click="onExampleClick(row)">查看示例文档</el-link>
      </el-table-column>
      <template slot="empty">
        暂无可查看的 Job，请先<!--
     --><el-link type="primary"
                 :to="{ name: 'job-repo/jars' }"
                 style="display: inline; vertical-align: baseline;">上传扩展包</el-link>
      </template>
    </el-table>
    <sharp-pagination :page-conf="pageConf" />
  </main>
</template>
<script src="./list.ts" />
