<template>
  <div id="tool-bar" style="height: 50px; display:flex; position: absolute; width: 85%; background-color: white;
                    align-items:center;border: 0 solid #c7bfbf; border-bottom-width: 0.5px;">
    <div>
      <lay-col sm="6" md="6">
        <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>引擎</label>
        <div class="layui-input-block">
          <lay-select v-model="query.engineName" v-on:change="select_changed_list(1)" style="border: 0.5px solid #c7bfbf">
            <lay-select-option v-for="(field, index) in engine_list" :key="index" :value="field">{{ field }}</lay-select-option>
          </lay-select>
        </div>
      </lay-col>
    </div>
    <div>
      <lay-col sm="6" md="6">
        <label class="layui-form-label line-label"><span class="layui-required layui-icon">*</span>版本</label>
        <div class="layui-input-block">
          <lay-select v-model="query.md5" v-on:change="select_changed_list(2)" style="border: 0.5px solid #c7bfbf">
            <lay-select-option v-for="(field, index) in version_list" :key="index" :value="field">{{ field }}</lay-select-option>
          </lay-select>
        </div>
      </lay-col>
    </div>
    <div>
      <lay-col sm="6" md="6">
        <label class="layui-form-label line-label">主机</label>
        <div class="layui-input-block">
          <lay-select v-model="query.host" style="border: 0.5px solid #c7bfbf">
            <lay-select-option key="-1" value="-1">-- 所有主机 --</lay-select-option>
            <lay-select-option v-for="(field, index) in host_list" :key="index" :value="field">{{ field }}</lay-select-option>
          </lay-select>
        </div>
      </lay-col>
    </div>
    <div style="margin-left: 10px">
      <lay-col sm="6" md="6">
        <lay-button prefix-icon="layui-icon-search" v-on:click="select_changed()">搜索</lay-button>
      </lay-col>
    </div>
  </div>

  <div id="engine_pic"></div>

  <lay-layer type="drawer" v-model="componentInfoVisible" title="组件信息" area="40%">
    <h3 style="padding-left: 20px;">基本信息</h3>
    <div class="layui-form-item line-item">
      <label class="layui-form-label line-label">组件名称</label>
      <div class="layui-input-block line-block">
        <input type="text" name="title" v-model="componentInfo.name"
               autocomplete="off" readonly class="layui-input" disabled>
      </div>
    </div>
    <div class="layui-form-item">
      <label class="layui-form-label line-label">组件描述</label>
      <div class="layui-input-block line-block">
        <input type="text" name="title" v-model="componentInfo.describe"
               autocomplete="off" class="layui-input" disabled>
      </div>
    </div>
    <div class="layui-form-item">
      <label class="layui-form-label line-label">组件类型</label>
      <div class="layui-input-block line-block">
        <input type="text" name="title" v-model="componentInfo.typeDesc"
               autocomplete="off" class="layui-input" disabled>
      </div>
    </div>
    <div class="layui-form-item" v-if="componentInfo.attributes.length > 0">
      <label class="layui-form-label line-label">属性列表</label>
      <div class="layui-input-block line-block">
        <lay-table :columns="columns2" :data-source="componentInfo.attributes" :size="md"
                   skin='line' even="true">
          <template #name="{ data }">
            {{data.name}}
            <lay-tooltip :visible="false" trigger="hover" v-bind:content="data.remark" position="right">
              <div @mouseover="visible=true" @mouseleave="visible=false">
                <i class="layui-icon layui-icon-about" style="color: #1E9FFF;"></i>
              </div>
            </lay-tooltip>

          </template>
        </lay-table>
      </div>
    </div>

    <div class="layui-form-item" v-if="componentInfo.extensionAttributes">
      <label class="layui-form-label line-label">扩展属性</label>
      <div class="layui-input-block line-block">
        <div>
          <highlightjs language='shell' :code="componentInfo.extensionAttributes"/>
        </div>
      </div>
    </div>

    <h3 style="padding-left: 20px;" v-if="componentInfo.script != null">脚本信息</h3>
    <div class="layui-form-item" v-if="componentInfo.script != null">
      <label class="layui-form-label line-label">脚本名称</label>
      <div class="layui-input-block line-block">
        <input type="text" name="title" v-model="componentInfo.scriptName"
               autocomplete="off" class="layui-input" disabled>
      </div>
    </div>

    <div class="layui-form-item" v-if="componentInfo.script != null">
      <div class="layui-input-block line-block">
        <div>
          <highlightjs :language='componentInfo.scriptType' :code="componentInfo.script"/>
        </div>
      </div>
    </div>

    <div class="layui-form-item" v-if="componentInfo.rollbackScript != null">
      <label class="layui-form-label line-label">回滚脚本</label>
      <div class="layui-input-block line-block">
        <input type="text" name="title" v-model="componentInfo.rollbackScriptName"
               class="layui-input" disabled>
      </div>
    </div>

    <div class="layui-form-item" v-if="componentInfo.rollbackScript != null">
      <div class="layui-input-block line-block">
        <div>
          <div>
            <highlightjs :language='componentInfo.rollbackScriptType' :code="componentInfo.rollbackScript"/>
          </div>
        </div>
      </div>
    </div>

    <h3 style="padding-left: 20px;">运行信息</h3>
    <div class="layui-form-item" v-if="customData.started != null">
      <label class="layui-form-label line-label">统计开始</label>
      <div class="layui-input-block line-block">
        <input type="text" name="title" v-model="customData.started"
               autocomplete="off" class="layui-input" disabled>
      </div>
    </div>

    <div class="layui-form-item" v-if="customData && customData.flag != null">
      <label class="layui-form-label line-label">统计信息</label>
      <!-- 与属性同表格，包含开始执行时间、执行耗时、回滚时间、回滚耗时     -->
      <div class="layui-input-block line-block excute-block">
        <div v-for="(value, key) in customData">
          <span class="excute-text" v-if="key !== 'ex' && key !== 'started' && key !== 'flag'">
             <label v-text="key"/>：<label v-text="value"/><br/>
          </span>

        </div>
      </div>
    </div>


  </lay-layer>


</template>

<style>
.line-item {
  margin-top: 12px;
}
.line-label {
  text-align: right;
}
.line-block {
  padding-right: 24px;
}
.excute-text {
  font-size: 13px;
  line-height: 20px;
}
.excute-block {
  margin-right: 24px;
  /* background: rgba(239, 239, 239, 0.3);
  border: 1px solid #eee; */
  padding: 8px;
}
.layui-input {
  padding-left: 8px;
  border: none;
  background: none;
}
</style>

<script>
import {onMounted, ref} from "vue";
import G6 from '@antv/g6';
import {management_engines, management_view} from "../../api/module/api";

export default {

  setup() {
    const data = {
      // 点集
      nodes: [],
      // 边集
      edges: [],
    };

    const componentInfoVisible = ref(false);
    const componentInfo = ref({});
    const customData = ref({});
    const info = ref({versions: {}, hosts: {}});
    const query = ref({engineName: "", host: "-1", md5: ""});
    const engine_list = ref([]);
    const version_list = ref([]);
    const host_list = ref([]);
    const columns2 = [
      {
        title: "名称",
        width: "30px",
        key: "name",
        customSlot: 'name'
      }, {
        title: "值",
        width: "120px",
        key: "value"
      }
    ]

    let width = window.innerWidth * 0.8;
    let height = window.innerHeight * 0.8;
    let middle = window.outerWidth * 0.5 - window.outerWidth * 0.5 * 0.15;

    const ICON_MAP = {
      a: 'https://gw.alipayobjects.com/mdn/rms_8fd2eb/afts/img/A*0HC-SawWYUoAAAAAAAAAAABkARQnAQ',
      b: 'https://gw.alipayobjects.com/mdn/rms_8fd2eb/afts/img/A*sxK0RJ1UhNkAAAAAAAAAAABkARQnAQ',
    };

    G6.registerNode(
        'card-node',
        {
          drawShape: function drawShape(cfg, group) {
            const color = cfg.customStyle.color;
            const r = 2;
            const shape = group.addShape('rect', {
              attrs: {
                x: -100,
                y: 0,
                width: 200,
                height: 60,
                stroke: color,
                radius: r,
              },
              // must be assigned in G6 3.3 and later versions. it can be any string you want, but should be unique in a custom item type
              name: 'main-box',
              draggable: true,
            });


            group.addShape('rect', {
              attrs: {
                x: 0 - 100,
                y: 0,
                width: 200,
                height: 20,
                fill: color,
                radius: [r, r, 0, 0],
              },
              // must be assigned in G6 3.3 and later versions. it can be any string you want, but should be unique in a custom item type
              name: 'title-box',
              draggable: true,
            });

            // left icon
            group.addShape('image', {
              attrs: {
                x: -96,
                y: 2,
                height: 16,
                width: 16,
                cursor: 'pointer',
                img: ICON_MAP[cfg.nodeType || 'app'],
              },
              // must be assigned in G6 3.3 and later versions. it can be any string you want, but should be unique in a custom item type
              name: 'node-icon',
            });

            // title text
            group.addShape('text', {
              attrs: {
                textBaseline: 'top',
                y: 2,
                x: -76,
                lineHeight: 20,
                text: cfg.title,
                fill: '#fff',
              },
              // must be assigned in G6 3.3 and later versions. it can be any string you want, but should be unique in a custom item type
              name: 'title',
            });

            // The content list
            cfg.panels.forEach((item, index) => {
              // name text
              group.addShape('text', {
                attrs: {
                  textBaseline: 'top',
                  y: 25,
                  x: 24 + index * 60 - 100,
                  lineHeight: 20,
                  text: item.title,
                  fill: 'rgba(0,0,0, 0.4)',
                },
                // must be assigned in G6 3.3 and later versions. it can be any string you want, but should be unique in a custom item type
                name: `index-title-${index}`,
              });

              // value text
              group.addShape('text', {
                attrs: {
                  textBaseline: 'top',
                  y: 42,
                  x: 24 + index * 60 - 100,
                  lineHeight: 20,
                  text: item.value,
                  fill: '#595959',
                },
                // must be assigned in G6 3.3 and later versions. it can be any string you want, but should be unique in a custom item type
                name: `index-value-${index}`,
              });
            });
            return shape;
          },
        },
        'single-node',
    );

    let graph;
    onMounted(() => {
      graph = new G6.Graph({
        container: 'engine_pic', // String | HTMLElement，必须，在 Step 1 中创建的容器 id 或容器本身
        width: width, // Number，必须，图的宽度
        height: height, // Number，必须，图的高度
        fitCenter: false,
        groupByTypes: true,
        groupType: 'combo',
        modes: {
          default: ['click-select', 'activate-relations'],
        },
        defaultEdge: {
          type: 'line',
          style: {
            lineWidth: 1,
            stroke: '#9900ff',
            endArrow: {
              path: 'M 0,0 L 10,3 L 10,-3 Z',
              fill: '#9900ff',
              strokeOpacity: 0.8,
              fillOpacity: 0.8
              // ...
            },
          }

        },

        defaultNode: {
          type: 'card-node',
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
      graph.data(data);
      graph.render(); // 渲染图

      graph.on("node:click", (ev) => {
        // node.
        const item = ev.item;
        let component = item.getModel().componentInfo;

        let id = item.getModel().id;
        if (id === '#start' || id === '#end') {
          return;
        }

        componentInfoVisible.value = !componentInfoVisible.value;
        componentInfo.value = component;
        customData.value = item.getModel().customData;

      });

      graph.on("combo:click", (ev) => {
        // node.
        const item = ev.item;
        let component = item.getModel().componentInfo;
        componentInfoVisible.value = !componentInfoVisible.value;
        componentInfo.value = component;
        customData.value = item.getModel().customData;

      });

      const loadData = async () => {
        const {data} = await management_engines();
        engine_list.value = data.engines;
        info.value.versions = data.versions;
        info.value.hosts = data.hosts;

        if (engine_list.value.length > 0) {
          query.value.engineName = engine_list.value[0];
          version_list.value = info.value.versions[query.value.engineName];

          if (version_list.value && version_list.value.length > 0) {
            query.value.md5 = version_list.value[0];
            host_list.value = info.value.hosts[query.value.md5];
          }
        }
      }
      loadData();
    });



    return {
      componentInfoVisible,
      componentInfo,
      customData,
      columns2,
      info,
      query,
      engine_list,
      version_list,
      host_list,
      select_changed() {
        const load = async () => {
          const result = await management_view(query.value);
          info.value = result.data;
          data.edges = result.data.edges;
          data.combos = result.data.combos;
          const width = window.innerWidth * 0.8;
          let maxwidth = 0;
          let maxheight = 0;
          for (let j = 0; j < result.data.nodes.length; j++) {
            result.data.nodes[j].x = middle + (result.data.nodes[j].x * 300);
            result.data.nodes[j].y = (result.data.nodes[j].y * 150 + 90);

            maxwidth = Math.max(maxwidth, Math.abs(result.data.nodes[j].x));
            maxheight = Math.max(maxheight, Math.abs(result.data.nodes[j].y));
            result.data.nodes[j].nodeType = 'a';

            if (result.data.nodes[j].id === '#start' || result.data.nodes[j].id === '#end') {
              result.data.nodes[j].style.width = 200;
              result.data.nodes[j].style.height = 50  ;
              result.data.nodes[j].style.fill = 'rgba(0,204,0,0.5)';
              result.data.nodes[j].style.radius = [2, 2, 0, 0];
            }
          }

          data.nodes = result.data.nodes;

          graph.data(data);
          graph.changeSize(width, maxheight + 100);
          graph.render();
        }
        load();
      },
      select_changed_list(type) {
        if (type === 1) {
          if (engine_list.value.length > 0) {
            version_list.value = info.value.versions[query.value.engineName];
          }

          if (version_list.value && version_list.value.length > 0) {
            query.value.md5 = version_list.value[0];
          }
        } else if (type === 2) {
          if (version_list.value && version_list.value.length > 0) {
            host_list.value = info.value.hosts[query.value.md5];
            query.value.host = "-1";
          }
        }
      }
    }

  },

}
</script>