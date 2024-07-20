<template>
  <div id="tool-bar" style="height: 50px; display:flex; justify-content: space-between;
                    align-items:center;border: 0 solid #c7bfbf; border-bottom-width: 1px;">
    <div>
      &nbsp;&nbsp;&nbsp; <lay-button v-on:click="addNode">添加节点</lay-button><lay-button v-on:click="addCondition">添加条件</lay-button>
<!--      <lay-button v-on:click="addCombo">添加子流程</lay-button>-->
    </div>
    <div>
      <lay-button v-on:click="exportImg">导出图片</lay-button>
      <lay-button v-on:click="saveConfirm" style="margin-right: 10px;">保存配置</lay-button>
    </div>
  </div>
  <div id="engine_pic"/>

  <lay-layer type="drawer" v-model="componentInfoVisible" title="组件信息" area="40%" v-on:close="processComponentInfo(componentInfo)">
    <h2 style="margin-top: 5px; margin-bottom: 5px">基本信息</h2>
    <div class="layui-form-item line-item">
      <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>组件名称</label>
      <div class="layui-input-block line-block">
        <input type="text" name="title" v-model="componentInfo.name"
               autocomplete="off" class="layui-input" style="width: 50%;">
      </div>
    </div>

    <div class="layui-form-item">
      <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>组件类型</label>
      <div class="layui-input-block">
        <lay-select v-model="componentInfo.type" style="width: 50%;">
            <lay-select-option v-for="(field, index) in componentInfo.optionTypes" :key="index" :value="field.value">{{ field.name }}</lay-select-option>
        </lay-select>
      </div>
    </div>

    <div class="layui-form-item" v-if="componentInfo.type === 'BASIC' || componentInfo.type === 'IF' || componentInfo.type === 'CHOOSE'">
      <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>组件子类型</label>
      <div class="layui-input-block line-block" style="display: flex; align-items: center;">
        <input type="radio" id="id1" name="subtype" value="1" v-model="componentInfo.subtype"/><label for="id1">基本</label> &nbsp;&nbsp;&nbsp;
        <input type="radio" id="id2" name="subtype" value="2" v-model="componentInfo.subtype"/><label for="id2">脚本</label>
      </div>
    </div>

    <div class="layui-form-item" v-if="componentInfo.subtype === '1' && componentInfo.type !== 'SUBPROCESS'">
      <label class="layui-form-label line-label">
        <lay-tooltip :visible="false" trigger="hover" content="填写类名或者spring bean名称" position="right">
          <div @mouseover="visible=true" @mouseleave="visible=false">
            <span class="layui-required layui-icon">*</span>执行类<i class="layui-icon layui-icon-about" style="color: #bea542;"></i>
          </div>
        </lay-tooltip>
      </label>
      <div class="layui-input-block line-block">
        <input type="text" name="title" v-model="componentInfo.describe"
               autocomplete="off" class="layui-input" style="width: 50%;">
      </div>
    </div>

    <div class="layui-form-item">
      <label class="layui-form-label line-label">标准属性列表</label>
      <div class="layui-input-block line-block">
        <lay-table :columns="columns2" :data-source="componentInfo.attributes" :size="md"
                   skin='line' even="true" resize="true" style="height: auto;">

          <template #name="{ data }">
            <lay-select v-if="edingKeys.includes(data)" v-model="data.name" >
              <lay-select-option value="-1">请选择属性</lay-select-option>
              <lay-select-option v-for="(field, index) in standardAttributes" :key="index"
                                 v-bind:disabled="checkStandardAttributeDisabled(componentInfo, field.name, data)"
                                 :value="field.name">{{ field.name }}</lay-select-option>
            </lay-select>
            <span v-else>
                    <lay-tooltip :visible="false" trigger="hover" v-bind:content="data.remark" position="right">
                        <div @mouseover="visible=true" @mouseleave="visible=false">
                          {{data.name}}<i class="layui-icon layui-icon-about" style="color: #1E9FFF;"></i>
                        </div>
                      </lay-tooltip>
            </span>
          </template>


          <template #value="{ data }">
            <lay-input v-if="edingKeys.includes(data)" v-model="data.value">
            </lay-input>
            <span v-else>
        {{ data.value }}
      </span>
          </template>

          <template #opt="{ data }">
            <div v-if="!edingKeys.includes(data)">
              <lay-button @click="editHandle(data)"><i class="layui-icon layui-icon-edit" style="color: #bea542;"></i></lay-button>
            </div>
            <div v-if="edingKeys.includes(data)">
              <lay-button @click="changeValue(data, componentInfo, 1)"><i class="layui-icon layui-icon-ok" style="color: #bea542;"></i></lay-button>
            </div>
            <div>
              <lay-button @click="removeField(componentInfo, data)"><i class="layui-icon layui-icon-delete" style="color: #bea542;"></i></lay-button>
            </div>

          </template>
          <template v-slot:footer>
            <div style="display:flex;justify-content:center;height: auto;">
              <lay-button size="sm" type="primary" v-on:click="addField(componentInfo)">新增</lay-button>
            </div>
          </template>
        </lay-table>
      </div>
    </div>

    <div class="layui-form-item" style="display:flex; align-items: center">
      <lay-tooltip :visible="false" trigger="hover" content="使用此功能请确保相关扩展已经实现" position="right">
        <div @mouseover="visible=true" @mouseleave="visible=false">
          扩展属性<i class="layui-icon layui-icon-about" style="color: #bea542;"></i>
        </div>
      </lay-tooltip>
      <div style="margin-left: 20px;">
        <lay-checkbox type="checkbox" name="switch" skin="switch" v-model="componentInfo.enabledExtensionAttributes" label="开启"/>
      </div>
    </div>

    <div class="layui-form-item" v-show="componentInfo.enabledExtensionAttributes">
      <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>属性内容</label>
      <div class="layui-input-block line-block">
        <div id="extensionAttrEditor" ref="extensionAttrEditor" class="layout-edit-line2"/>
      </div>
    </div>

    <h2 v-if="componentInfo.subtype === '2'" style="margin-top: 5px; margin-bottom: 5px">脚本信息</h2>
    <div class="layui-form-item" v-if="componentInfo.subtype === '2'">
      <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>脚本名称</label>
      <div class="layui-input-block line-block">
        <input type="text" name="title" v-model="componentInfo.scriptName"
               autocomplete="off" class="layui-input" style="width: 50%;">
      </div>
    </div>

    <div class="layui-form-item" v-if="componentInfo.subtype === '2'">
      <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>脚本类型</label>
      <div class="layui-input-block line-block" style="display: flex; align-items: center">
        <lay-select v-model="componentInfo.scriptTypeIndex" showSearch="true" style="width: 30%;">
          <lay-select-option value="ognl">ognl</lay-select-option>
          <lay-select-option value="groovy">groovy</lay-select-option>
          <lay-select-option value="javascript">javascript/js</lay-select-option>
          <lay-select-option value="qlexpress">qlexpress</lay-select-option>
          <lay-select-option value="-1">其他</lay-select-option>
        </lay-select>
        &nbsp;&nbsp;
        <lay-input type="text" name="inputScriptType" v-model="componentInfo.scriptType" v-if="componentInfo.scriptTypeIndex === '-1'"
                   autocomplete="off" class="layui-input" style="width: 50%;"/>
      </div>
    </div>

    <div class="layui-form-item" v-show="componentInfo.subtype === '2'">
      <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>脚本内容</label>
      <div class="layui-input-block line-block">
        <div id="scriptEditor" ref="scriptEditor" class="layout-edit-line3"/>
      </div>
    </div>

    <div class="layui-form-item" style="display:flex;align-items: center;"  v-show="componentInfo.subtype === '2' && componentInfo.type === 'BASIC'">
      <lay-tooltip :visible="false" trigger="hover" content="使用此功能请开启rollback属性为true" position="right">
        <div @mouseover="visible=true" @mouseleave="visible=false">
          回滚脚本 <i class="layui-icon layui-icon-about" style="color: #bea542;"></i>
        </div>
      </lay-tooltip>
      <div class="layui-input-block" style="margin-left: 30px;">
        <lay-checkbox name="switch2" skin="switch" v-model="componentInfo.enabledRollbackScriptEdit" label="开启"/>
      </div>
    </div>

    <div class="layui-form-item" v-if="componentInfo.subtype === '2' && componentInfo.type === 'BASIC' && componentInfo.enabledRollbackScriptEdit">
      <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>脚本类型</label>
      <div class="layui-input-block line-block" style="display: flex; align-items: center">
        <lay-select v-model="componentInfo.rollbackScriptTypeIndex" showSearch="true" style="width: 30%;">
          <lay-select-option value="ognl">ognl</lay-select-option>
          <lay-select-option value="groovy">groovy</lay-select-option>
          <lay-select-option value="javascript">javascript/js</lay-select-option>
          <lay-select-option value="qlexpress">qlexpress</lay-select-option>
          <lay-select-option value="-1">其他</lay-select-option>
        </lay-select>
        &nbsp;&nbsp;
        <lay-input type="text" name="inputRollScriptType" v-model="componentInfo.rollbackScriptType" v-if="componentInfo.rollbackScriptTypeIndex === '-1'"
                   autocomplete="off" class="layui-input" style="width: 30%;"/>
        </div>
    </div>

    <div class="layui-form-item" v-show="componentInfo.subtype === '2' && componentInfo.type === 'BASIC' && componentInfo.enabledRollbackScriptEdit">
      <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>脚本内容</label>
      <div class="layui-input-block line-block">
          <div id="rollbackScriptEditor" ref="rollbackScriptEditor" class="layout-edit-line3"/>
      </div>
    </div>

  </lay-layer>

  <lay-layer v-model="edgeInfoVisible" title="编辑边" type="drawer" v-on:close="changeLabel(edgeInfo)" area="40%">
    <div class="layui-form-item line-item" style="margin-left: 5px; width: 60%; margin-top: 5px;">
      <label class="layui-form-label line-label">边信息</label>
      <div class="layui-input-block line-block">
        <input type="text" name="title" v-model="edgeInfo.label"
               autocomplete="off" class="layui-input">
      </div>
    </div>

    <div class="layui-form-item line-item" style="margin-left: 5px;width: 60%;">
      <label class="layui-form-label line-label">类型</label>
      <div class="layui-input-block line-block">
        <lay-select v-model="edgeInfo.type">
          <lay-select-option value="line">直线</lay-select-option>
          <lay-select-option value="polyline">折线</lay-select-option>
        </lay-select>
      </div>
    </div>

    <div class="layui-form-item line-item" style="margin-left: 5px;">
      <lay-button v-on:click="changeLabel(edgeInfo)">确认</lay-button>
    </div>

  </lay-layer>


  <lay-layer v-model="engineInfoVisible" title="保存确认" area="27%">
    <div class="layui-form-item line-item" style="margin-left: 5px;width: 80%; margin-top: 2px;">
      <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>引擎名称</label>
      <div class="layui-input-block line-block">
        <input type="text" v-model="engineInfo.name"
               autocomplete="off" class="layui-input">
      </div>
    </div>

    <div class="layui-form-item line-item" style="margin-left: 5px;width: 80%;">
      <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>流水线名称</label>
      <div class="layui-input-block line-block">
        <input type="text" v-model="engineInfo.process"
               autocomplete="off" class="layui-input">
      </div>
    </div>

    <div class="layui-form-item line-item" style="margin-left: 5px;">
      <lay-button v-on:click="save(engineInfo)">确认</lay-button>
    </div>

  </lay-layer>
</template>

<style>
.layout-edit-line3 {
  display: flex;
  height: 200px;
  flex-direction: row;
}

.layout-edit-line2 {
  display: flex;
  height: 100px;
  flex-direction: row;
}
</style>

<script>
import {onMounted, ref} from "vue";
import {useRouter} from 'vue-router';
import {g6_detail_save, trace_detail_g6, standard_attributes} from "../../api/module/api";
import G6 from '@antv/g6';
import * as monaco from "monaco-editor";
import {layer} from "@layui/layui-vue";

var scriptEditor;
var rollbackScriptEditor;
var extensionAttrEditor;
var number = 1;

export default {

  updated() {
    this.initEditor(this.componentInfo);
  },

  setup: function () {
    const router = useRouter();
    let id = -1;
    let url = window.location.href;

    if (url.split("/").length > 1) {
      let all = url.split('/');

      if (all.length > 0 && !isNaN(parseFloat(all[all.length - 1]))) {
        id = all[all.length - 1];
      }
    }

    let random = 1;
    const componentInfoVisible = ref(false);
    const engineInfoVisible = ref(false);
    const componentInfo = ref({});
    const edgeInfoVisible = ref(false);
    const edgeInfo = ref({});
    const engineInfo = ref({});
    const edingKeys = ref([]);
    const standardAttributes = ref([]);
    const columns2 = [
      {
        title: "名称",
        width: "80px",
        key: "name",
        customSlot: 'name'
      }, {
        title: "值",
        width: "120px",
        key: "value",
        ellipsisTooltip: true,
        customSlot: "value"
      }, {
        title: "操作",
        width: "60px",
        key: "opt",
        customSlot: 'opt'
      }
    ];
    const data = {
      // 点集
      nodes: [],
      // 边集
      edges: [],
    };

    let g6Graph;

    onMounted(() => {

      const graph = new G6.Graph({
        container: 'engine_pic', // String | HTMLElement，必须，在 Step 1 中创建的容器 id 或容器本身
        width: window.innerWidth, // Number，必须，图的宽度
        height: window.innerHeight, // Number，必须，图的高度
        fitCenter: false,
        groupByTypes: true,
        groupType: 'combo',
        modes: {
          default: ['click-select', 'activate-relations',
            "drag-combo", "drag-node",
            {
              type: 'scroll-canvas',
              direction: 'both',
              scalableRange: -1
            },
            {
              type: "brush-select",
              trigger: 'drag',
            },

            {
              type: "create-edge",
              trigger: "click",
              key: 'alt',
              shouldBegin: function (e, ignored) {
                if (e.item.getType() !== 'node') {
                  return false;
                }

                const edges = e.item.getEdges();
                let size = 0;
                for (let i = 0; i < edges.length; i++) {
                  if (edges[i].getModel().source === e.item.getModel().id) {
                    size++;
                  }
                }

                const comp = e.item.getModel().componentInfo;
                if (comp.type === 'BASIC' && size === 0) {
                  return true;
                }

                if (comp.type === 'IF' && size < 2) {
                  return true;
                }

                return comp.type === 'CHOOSE';
              },
              shouldEnd: function (e, self) {
                const source = graph.findById(self.source);
                if (!source || !e.item) {
                  return false;
                }

                // Combo not allowed
                if (source.getType() === 'combo' || e.item.getType() === 'combo') {
                  return false;
                }

                // cycle not allowed.
                return source.getModel().id !== e.item.getModel().id;
              }
            }],
        },
        defaultEdge: {
          type: 'line',
          style: {
            lineWidth: 1,
            stroke: 'black',
            lineAppendWidth: 5,
            endArrow: {
              path: 'M 0,0 L 10,3 L 10,-3 Z',
              fill: 'black',
              strokeOpacity: 0.8,
              fillOpacity: 0.8
              // ...
            },
          }

        },

        defaultNode: {
          size: [200, 50],
          type: 'rect'
        },

        defaultCombo: {
          padding: 30,
          type: 'rect',
          labelCfg: {
            position: 'top',
          },
          style: {
            stroke: "grey",
            lineDash: 10,
          }
        }
      });

      const load_standard_attributes = async () => {
        const result = await standard_attributes();
        standardAttributes.value = result.data;
      }

      load_standard_attributes();

      if (id > 0) {
        let middle = window.outerWidth * 0.5 - window.outerWidth * 0.5 * 0.15;
        const loadData = async () => {
          const result = await trace_detail_g6(id);

          for (let j = 0; j < result.data.nodes.length; j++) {
            let node = result.data.nodes[j];
            node.x = middle + (node.x * 300);
            node.y = (node.y * 150 + 40);

            if (node.id === '#start' || node.id === '#end') {
              node.style.fill = 'rgba(0,204,0,0.5)';
            } else {
              node.label = node.title;
            }

            if (node.type === 'diamond') {
              node.size = [200, 110];
              if (node.componentInfo) {
                node.componentInfo.optionTypes = [
                  {
                    name: "IF组件",
                    value: "IF"
                  },
                  {
                    name: "CHOOSE组件",
                    value: "CHOOSE"
                  },
                ];
              }

            } else {

              if (node.componentInfo) {
                node.componentInfo.optionTypes = [
                  {
                    name: "基本组件",
                    value: "BASIC"
                  },
                ];
              }
            }

            if (node.componentInfo) {
              if (node.componentInfo.extensionAttributes) {
                node.componentInfo.enabledExtensionAttributes = true;
              } else {
                node.componentInfo.enabledExtensionAttributes = false;
              }

              if (node.componentInfo.rollbackScript) {
                node.componentInfo.enabledRollbackScriptEdit = true;
              } else {
                node.componentInfo.enabledRollbackScriptEdit = false;
              }

            }

            if (node.componentInfo && !node.componentInfo.script) {
              node.componentInfo.subtype = '1';
              node.componentInfo.scriptTypeIndex = "javascript";
              node.componentInfo.rollbackScriptTypeIndex = "javascript";
            } else if (node.componentInfo) {
              node.componentInfo.subtype = '2';
              node.componentInfo.scriptTypeIndex = node.componentInfo.scriptType;
              node.componentInfo.rollbackScriptTypeIndex = node.componentInfo.rollbackScriptType;

              if (node.componentInfo.scriptTypeIndex === 'js') {
                node.componentInfo.scriptTypeIndex = 'javascript';
              }

              if (node.componentInfo.rollbackScriptTypeIndex === 'js') {
                node.componentInfo.rollbackScriptTypeIndex = 'javascript';
              }

              if (node.componentInfo.scriptTypeIndex !== 'javascript'
                  && node.componentInfo.scriptTypeIndex !== 'groovy'
                  && node.componentInfo.scriptTypeIndex !== 'ognl'
                  && node.componentInfo.scriptTypeIndex !== 'qlexpress') {
                node.componentInfo.scriptTypeIndex = '-1';
              }

              if (node.componentInfo.rollbackScriptTypeIndex !== 'javascript'
                  && node.componentInfo.rollbackScriptTypeIndex !== 'groovy'
                  && node.componentInfo.rollbackScriptTypeIndex !== 'ognl'
                  && node.componentInfo.rollbackScriptTypeIndex !== 'qlexpress') {
                node.componentInfo.rollbackScriptTypeIndex = '-1';
              }
            }
          }

          for (let j = 0; j < result.data.combos.length; j++) {
            result.data.combos[j].componentInfo.subtype = '1';
            result.data.combos[j].componentInfo.optionTypes = [
              {
                name: "子流程组件",
                value: "SUBPROCESS"
              },
              {
                name: "适配器",
                value: "ADAPTER"
              },
            ];
          }

          data.edges = result.data.edges;
          data.combos = result.data.combos;
          data.nodes = result.data.nodes;
          engineInfo.value = {
            name: result.data.name,
            process: result.data.process,
            id: result.data.id,
          }

          graph.data(data); // 读取 Step 2 中的数据源到图上
          graph.render(); // 渲染图
        }

        loadData();
      }

      graph.on("node:dblclick", (ev) => {
        // node.
        const item = ev.item;
        let component = item.getModel().componentInfo;

        let id = item.getModel().id;
        if (id === '#start' || id === '#end') {
          return;
        }

        componentInfoVisible.value = !componentInfoVisible.value;
        componentInfo.value = component;
        componentInfo.value.id = item.getModel().id;
      });

      graph.on("combo:dblclick", (ev) => {
        // node.
        const item = ev.item;
        let component = item.getModel().componentInfo;
        componentInfoVisible.value = !componentInfoVisible.value;
        componentInfo.value = component;
        componentInfo.value.id = item.getModel().id;
      });

      graph.on("edge:dblclick", (ev) => {
        // edge.
        const item = ev.item;
        let label = item.getModel().label;

        const source = item.getModel().source;
        const sourceNode = g6Graph.findById(source);

        if (sourceNode.getModel().type !== 'diamond') {
          return;
        }

        edgeInfo.value = {
          label: label,
          edgeId: item.getModel().id,
          type: item.getModel().type,
        }

        edgeInfoVisible.value = !edgeInfoVisible.value;

      });

      graph.on("edge:click", (ev) => {
        // edge.
        const item = ev.item;
        graph.setItemState(item, "selected", true);

      });

      if (id <= -1) {
        graph.data(data); // 读取 Step 2 中的数据源到图上
        graph.render(); // 渲染图
      }

      g6Graph = graph;
    });

    // Monitor keys: delete, ctrl+c, ctrl+v, ctrl+z, ctrl+shift+z
    window.document.onkeydown = function(event) {
      if (engineInfoVisible.value || componentInfoVisible.value
          || edgeInfoVisible.value) {
        return;
      }

      // delete / backspace
      if (event.key === 'Backspace' || event.key === 'Delete') {
        const deleteOpt = {
          opt: 'delete',
          nodes: [],
          edges: [],
          combos: []
        };

        let deleted = false;
        let k = 0;
        const edges = g6Graph.findAllByState('edge', 'selected');
        for (let i = 0; i < edges.length; i++) {
          deleteOpt.edges[k++] = edges[i].getModel();
          g6Graph.removeItem(edges[i], false);
          deleted = true
        }

        const nodes = g6Graph.findAllByState('node', 'selected');

        for (let i = 0; i < nodes.length; i++) {
          deleteOpt.nodes[i] = nodes[i].getModel();
          const relatedEdges = nodes[i].getEdges();
          for (let j = 0; j < relatedEdges.length; j++) {
            deleteOpt.edges[k++] = relatedEdges[j].getModel();
          }
          g6Graph.removeItem(nodes[i], false);
          deleted = true
        }

        const combos = g6Graph.findAllByState('combo', 'selected');
        for (let i = 0; i < combos.length; i++) {
          deleteOpt.combos[i] = combos[i].getModel();
          g6Graph.removeItem(combos[i], false);
          deleted = true;
        }

        if (!deleted) {
          return;
        }

        // push undo
        g6Graph.pushStack('delete', deleteOpt, 'undo');

        // clear redo;
        const stack = g6Graph.getRedoStack();
        if (stack == null || stack.length === 0) {
          return;
        }
        while(stack.pop());
      }
      // ctrl+c
      else if ((event.ctrlKey || event.metaKey) && event.key === 'c') {
      }

      // ctrl+v
      else if ((event.ctrlKey || event.metaKey) && event.key === 'v') {

      }

      // ctrl+z
      else if ((event.ctrlKey || event.metaKey) && event.key === 'z') {
        // peek stack top op, revert it
        const stack = g6Graph.getUndoStack();
        if (stack == null || stack.length === 0) {
          return;
        }

        const item = stack.pop();
        if (item) {
          const opt = item.data;
          if (opt.opt === 'delete') {
            for (let i = 0; i < opt.nodes.length; i++) {
              g6Graph.addItem('node', opt.nodes[i], false);
            }

            for (let i = 0; i < opt.edges.length; i++) {
              g6Graph.addItem('edge', opt.edges[i], false);
            }

            for (let i = 0; i < opt.combos.length; i++) {
              g6Graph.addItem('combo', opt.combos[i], false);
            }
          }

          if (opt.opt) {
            // push redo
            g6Graph.pushStack('delete', opt, 'redo');
          }
        }

      }
      // ctrl+shift+z
      else if ((event.ctrlKey || event.metaKey) && event.shiftKey && event.key === 'z') {

      }
    }

    return {
      componentInfoVisible,
      componentInfo,
      edgeInfo,
      edgeInfoVisible,
      columns2,
      engineInfoVisible,
      engineInfo,
      edingKeys,
      standardAttributes,
      addField(componentInfo) { // 添加表单项
        const data = {name: '-1', value: ''}
        componentInfo.attributes.push(data);
        this.editHandle(data);
      },
      removeField(componentInfo, data) { // 删除表单项
        componentInfo.attributes.splice(componentInfo.attributes.indexOf(data),1);
        this.deleteEdit(data);
      },

      editHandle (data) {
        edingKeys.value.push(data);
      },
      deleteEdit  (data) {
        const index = edingKeys.value.indexOf(data);
        if (index < 0) {
          return;
        }
        edingKeys.value.splice(index,1);
      },

       changeValue (data, componentInfo, type){
        if (data.name === '-1') {
          layer.notifiy({
            title:"提示",
            content:'请选择属性',
            icon:2
          })
          return;
        }

         componentInfo.attributes.forEach(element => {
           if (element === data) {
             element.name = data.name;
             element.value = data.value;
             this.standardAttributes.forEach(p => {
               if (p.name === data.name) {
                 element.remark = p.remark;
               }
             });
           }
         });
           componentInfo.attributes.forEach(element => {
             if(element.name === data.name) {
               element.value = data.value;
             }
           });

           this.deleteEdit(data);

      },

      checkStandardAttributeDisabled(componentInfo, name, data) {
        if (data.name === name) {
          return false;
        }

        for (let i = 0; i < componentInfo.attributes.length; i++) {
          if(componentInfo.attributes[i].name === name) {
            return true;
          }
        }

        return false;
      },


      addNode() {
        const point = g6Graph.getViewPortCenterPoint();
        g6Graph.addItem('node', {
          id: 'anonymous-node-' + random++,
          label: "Node",
          componentInfo:{
            type: "BASIC",
            subtype: '1',
            scriptType: 'javascript',
            script: '',
            rollbackScript: '',
            rollbackScriptType: 'javascript',
            extensionAttributes: "",
            attributes: [],
            optionTypes: [
              {
                name: "基本组件",
                value: "BASIC"
              }
            ],
            scriptTypeIndex: 'javascript',
            rollbackScriptTypeIndex: 'javascript',
            enabledRollbackScriptEdit: false,
            enabledExtensionAttributes: false,
          },
          x: point.x,
          y: point.y,
          anchorPoints: [[0, 0.5], [0, 1], [0.5, 0], [0.5, 1]]
        }, true);
      },
      addCondition() {
        const point = g6Graph.getViewPortCenterPoint();
        g6Graph.addItem('node', {
          id: 'anonymous-condition-' + random++,
          type: 'diamond',
          size: [200, 100],
          label: "Condition",
          componentInfo:{
            type: "IF",
            subtype: '1',
            scriptType: 'javascript',
            script: '',
            rollbackScript: '',
            rollbackScriptType: 'javascript',
            extensionAttributes: "",
            attributes: [],
            optionTypes: [
              {
                name: "IF组件",
                value: "IF"
              },
              {
                name: "CHOOSE组件",
                value: "CHOOSE"
              },
            ],
            scriptTypeIndex: 'javascript',
            rollbackScriptTypeIndex: 'javascript',
            enabledRollbackScriptEdit: false,
            enabledExtensionAttributes: false,
          },
          x: point.x,
          y: point.y,
          anchorPoints: [[0, 0.5], [1, 0.5], [0.5, 0], [0.5, 1]]
        }, true);
      },

      addCombo() {
        const point = g6Graph.getViewPortCenterPoint();
        g6Graph.addItem('combo', {
          id: 'anonymous-combo-' + random++,
          type: 'rect',
          size: [200, 100],
          componentInfo:{
            type: "SUBPROCESS",
            extensionAttributes: "",
            attributes: [],
            optionTypes: [
              {
                name: "子流程组件",
                value: "SUBPROCESS"
              },
              {
                name: "适配器",
                value: "ADAPTER"
              },
            ],
            enabledRollbackScriptEdit: false,
            enabledExtensionAttributes: false,
          },
          x: point.x,
          y: point.y
        }, true);
      },

      exportImg() {
        g6Graph.downloadFullImage("graph", "image/png", {});
      },

      save(engineInfo) {
        const nodes = g6Graph.getNodes();
        const edges = g6Graph.getEdges();
        const combos = g6Graph.getCombos();
        const params = {
          name: engineInfo.name,
          process: engineInfo.process,
          id: engineInfo.id,
          nodes:[],
          edges:[],
          combos:[]
        }

        for (let i = 0; i < nodes.length; i++) {
          params.nodes[i] = nodes[i].getModel();
          let compInfo = params.nodes[i].componentInfo;
          if (compInfo && compInfo.subtype === '2') {
            compInfo.describe = compInfo.scriptName;
          }
          if (compInfo && !compInfo.enabledExtensionAttributes) {
            compInfo.extensionAttributes = null;
          }

          if (compInfo && !compInfo.enabledRollbackScriptEdit) {
            compInfo.rollbackScriptType = null;
          }
        }

        for (let i = 0; i < edges.length; i++) {
          params.edges[i] = edges[i].getModel();
        }

        for (let i = 0; i < combos.length; i++) {
          params.combos[i] = combos[i].getModel();
        }

        const saveAsync = async() => {
          const result = await g6_detail_save(params);
          if (result.success) {
            router.push({path: "/engines"})
          }
        }

        saveAsync();
      },

      initEditor(obj) {
        if (scriptEditor) {
          scriptEditor.dispose();
          scriptEditor = null;
        }

        if (rollbackScriptEditor) {
          rollbackScriptEditor.dispose();
          rollbackScriptEditor = null;
        }

        if (extensionAttrEditor) {
          extensionAttrEditor.dispose();
          extensionAttrEditor = null;
        }


        if (!scriptEditor && document.getElementById("scriptEditor")) {
          scriptEditor = monaco.editor.create(document.getElementById("scriptEditor"), {
            theme: "vs-dark", // 主题
            value: "", // 默认显示的值
            language: 'js',
            folding: true, // 是否折叠
            foldingHighlight: true, // 折叠等高线
            foldingStrategy: "indentation", // 折叠方式  auto | indentation
            showFoldingControls: "always", // 是否一直显示折叠 always | mouseover
            disableLayerHinting: true, // 等宽优化
            emptySelectionClipboard: false, // 空选择剪切板
            selectionClipboard: false, // 选择剪切板
            automaticLayout: true, // 自动布局
            codeLens: false, // 代码镜头
            suggestFontSize: 14,
            fontSize: 14,
            scrollBeyondLastLine: false, // 滚动完最后一行后再滚动一屏幕
            colorDecorators: true, // 颜色装饰器
            accessibilitySupport: "off", // 辅助功能支持  "auto" | "off" | "on"
            lineNumbers: "on", // 行号 取值： "on" | "off" | "relative" | "interval" | function
            lineNumbersMinChars: 5, // 行号最小字符   number
            readOnly: false, //是否只读  取值 true | false
          });
        }

        if (!extensionAttrEditor && document.getElementById("extensionAttrEditor")) {
          extensionAttrEditor = monaco.editor.create(document.getElementById("extensionAttrEditor"), {
            theme: "vs-dark", // 主题
            value: "", // 默认显示的值
            language: 'properties',
            folding: true, // 是否折叠
            foldingHighlight: true, // 折叠等高线
            foldingStrategy: "indentation", // 折叠方式  auto | indentation
            showFoldingControls: "always", // 是否一直显示折叠 always | mouseover
            disableLayerHinting: true, // 等宽优化
            emptySelectionClipboard: false, // 空选择剪切板
            selectionClipboard: false, // 选择剪切板
            automaticLayout: true, // 自动布局
            codeLens: false, // 代码镜头
            suggestFontSize: 14,
            fontSize: 14,
            scrollBeyondLastLine: false, // 滚动完最后一行后再滚动一屏幕
            colorDecorators: true, // 颜色装饰器
            accessibilitySupport: "off", // 辅助功能支持  "auto" | "off" | "on"
            lineNumbers: "on", // 行号 取值： "on" | "off" | "relative" | "interval" | function
            lineNumbersMinChars: 5, // 行号最小字符   number
            readOnly: false, //是否只读  取值 true | false
          });
        }

        if (document.getElementById("rollbackScriptEditor") && !rollbackScriptEditor) {
          rollbackScriptEditor = monaco.editor.create(document.getElementById("rollbackScriptEditor"), {
            theme: "vs-dark", // 主题
            value: "", // 默认显示的值
            language: 'js',
            folding: true, // 是否折叠
            foldingHighlight: true, // 折叠等高线
            foldingStrategy: "indentation", // 折叠方式  auto | indentation
            showFoldingControls: "always", // 是否一直显示折叠 always | mouseover
            disableLayerHinting: true, // 等宽优化
            emptySelectionClipboard: false, // 空选择剪切板
            selectionClipboard: false, // 选择剪切板
            automaticLayout: true, // 自动布局
            codeLens: false, // 代码镜头
            suggestFontSize: 14,
            fontSize: 14,
            scrollBeyondLastLine: false, // 滚动完最后一行后再滚动一屏幕
            colorDecorators: true, // 颜色装饰器
            accessibilitySupport: "off", // 辅助功能支持  "auto" | "off" | "on"
            lineNumbers: "on", // 行号 取值： "on" | "off" | "relative" | "interval" | function
            lineNumbersMinChars: 5, // 行号最小字符   number
            readOnly: false, //是否只读  取值 true | false
          });
        }

        if (scriptEditor && obj.script) {
          scriptEditor.setValue(obj.script);
        }

        if (rollbackScriptEditor && obj.rollbackScript) {
          rollbackScriptEditor.setValue(obj.rollbackScript);
        }

        if (extensionAttrEditor && obj.extensionAttributes) {
          extensionAttrEditor.setValue(obj.extensionAttributes);
        }
      },

      processComponentInfo(obj) {
        if (scriptEditor) {
          obj.script = scriptEditor.getValue();
        }

        if (rollbackScriptEditor) {
          obj.rollbackScript = rollbackScriptEditor.getValue();
        }

        if (extensionAttrEditor) {
          obj.extensionAttributes = extensionAttrEditor.getValue();
        }

        if (obj.scriptTypeIndex !== '-1') {
          obj.scriptType = obj.scriptTypeIndex;
        }
        if (obj.rollbackScriptTypeIndex !== '-1') {
          obj.rollbackScriptType = obj.rollbackScriptTypeIndex;
        }

        if (obj && obj.name) {
          const item = g6Graph.findById(obj.id);
          if (item.getType() === 'combo') {
            item.getModel().label = (obj.type === 'SUBPROCESS' ? "子流程:" : "适配器:") + obj.name;
          } else if (item.getType() === 'node') {
            if (obj.type === 'IF' || obj.type === 'CHOOSE') {
              item.getModel().label = obj.name;
            } else if (item.getModel().title !== obj.name){
              item.getModel().title = obj.name;
              item.getModel().label = obj.name;
            }
          }
          g6Graph.updateItem(item, item.getModel(), false);
        }
      },

      saveConfirm() {
        engineInfoVisible.value = !engineInfoVisible.value;
      },
      changeLabel(edgeInfo) {
        const edgeId = edgeInfo.edgeId;
        const label = edgeInfo.label;
        const type = edgeInfo.type;

        const item = g6Graph.findById(edgeId);
        if (item && label) {
          item.getModel().label = label;
        }

        if (item && type) {
          item.getModel().type = type;
        }

        edgeInfoVisible.value = !edgeInfoVisible.value;
        g6Graph.refreshItem(item);
      }
    }
  },

}
</script>