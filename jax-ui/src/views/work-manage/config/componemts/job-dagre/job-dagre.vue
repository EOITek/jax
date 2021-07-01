<template>
  <div ref="Div" class="job-dagre width-full" id="JobDagre" tabindex="-1" @keydown="keyDown">
    <div ref="ImageMinimap" id="ImageMinimap" class="image-minimap" unselectable="on" onselectstart="return false;"></div>
    <div class="flex-vcenter toolbar plr-15 pointer">
      <i class="el-icon-copy-document fs-20"
         title="复制"
         :class="!isSelectNode && 'disabled'"
         @click="handlerIsOpenCopyToggle"></i>
      <i class="fa fa-clipboard fs-20"
         title="粘贴"
         :class="!isCopytNode && 'disabled'"
         @click="handlerPaste()"></i>
      <!-- TODO: 暂时隐藏这个功能    -->
      <!-- <img src="@/assets/images/repeal.svg"
           class="select-none"
           title="撤销"
           @click="disposeUndoOrRedo()"/>
      <img src="@/assets/images/recover.svg"
           class="select-none"
           title="恢复"
           @click="disposeUndoOrRedo('redo')"/> -->
      <i class="el-icon-zoom-in fs-20"
         title="放大"
         @click="handlerGraphZoom('zoomIn')"></i>
      <i class="el-icon-zoom-out fs-20"
         title="缩小"
         @click="handlerGraphZoom('zoomOut')"></i>
      <i class="el-icon-delete fs-20"
         title="删除"
         :class="!isSelectItem && 'disabled'"
         @click="handlerDeleteNodes()"></i>
      <img src="@/assets/images/autoZoom.svg"
           class="select-none"
           title="自适应屏幕"
           @click="graph.fitView([20, 20])">
      <img src="@/assets/images/layout.svg"
           class="select-none"
           title="一键布局"
           @click="layout"/>
    </div>
  </div>
</template>
<script lang="ts" src="./job-dagre.ts"></script>
<style lang="scss" scoped>
.job-dagre {
  min-width: 500px;
  height: 100%;
  position: relative;
}

.image-minimap {
  position: absolute;
  bottom: 10px;
  left: 10px;
  border: 1px solid #e2e2e2;
  background: #fff;
}

.toolbar {
  border: 1px solid #e2e2e2;
  height: 36px;
  width: 280px;
  justify-content: space-between;
  position: absolute;
  background: #fff;
}

img {
  width: 20px;
}
</style>
