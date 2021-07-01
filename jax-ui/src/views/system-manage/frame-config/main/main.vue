<template>
  <div class="main">
    <el-link :to="{ name: 'frameConfigCreatedEdit' }">
      <el-button type="primary" class="mb-10">增加配置集合</el-button>
    </el-link>
    <el-table :data="tableData" v-loading="!!loading">
      <el-table-column label="名称" prop="optsName" sortable show-overflow-tooltip />
      <el-table-column label="类型" prop="optsType" sortable />
      <el-table-column label="版本" prop="version" sortable />
      <el-table-column label="更新时间" prop="updateTime" #default="{ row }" sortable width="180">
        {{ row.updateTime | formatDate }}
      </el-table-column>
      <el-table-column label="操作" #default="{ row }" sortable width="180">
        <el-link type="primary"
                 :to="{ name: 'frameConfigCreatedEdit',
                        query: { name: row.optsName, clone: 1, type: row.optsType } }">
          克隆
        </el-link>
        <el-link type="primary"
                 class="mlr-10"
                 :to="{ name: 'frameConfigCreatedEdit',
                        query: { name: row.optsName, view: 1, type: row.optsType } }">
          查看
        </el-link>
        <el-link type="primary"
                 class="mr-10"
                 :to="{ name: 'frameConfigCreatedEdit',
                        query: { name: row.optsName, type: row.optsType } }">
          编辑
        </el-link>
        <el-link type="primary" @click="deleteFream({ optsName: row.optsName, optsType: row.optsType })">
          删除
        </el-link>
      </el-table-column>
    </el-table>
    <sharp-pagination :page-conf="pageConf" />
  </div>
</template>
<script lang="ts" src="./main.tsx"></script>
