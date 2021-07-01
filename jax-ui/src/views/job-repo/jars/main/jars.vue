<template>
  <main class="mt-10">
    <el-button type="primary" @click="upload('java')">导入java包</el-button>
    <el-button type="primary" @click="upload('python')">导入python包</el-button>
    <el-table class="mt-15" :data="tableData" v-loading="!!loading" ref="Table" row-key="jarName">
      <el-table-column prop="jarFile" label="包名称" show-overflow-tooltip />
      <el-table-column width="100" prop="jarVersion" label="版本号" />
      <el-table-column prop="jarDescription" label="描述" show-overflow-tooltip />
      <el-table-column width="80" prop="jobList.length" label="Job数量" />
      <el-table-column width="150" prop="createTime" label="创建时间" formatter="formatDate" />
      <el-table-column width="150" prop="updateTime" label="更新时间" formatter="formatDate" />
      <el-table-column width="100" label="操作" #default="{ row }">
        <el-link type="primary" @click="update(row)">更新</el-link>
        <el-link class="ml-10" type="primary" @click="remove(row)">删除</el-link>
      </el-table-column>
    </el-table>
  </main>
</template>
<script src="./jars.ts" />
