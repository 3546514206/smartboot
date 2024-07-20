<template>
  <lay-row space="20" style="margin-top: 10px; margin-left: 5px;">
    <lay-col sm="6" md="6">
      <lay-input placeholder="引擎名称" v-model="engineName"></lay-input>
    </lay-col>

    <lay-col sm="6" md="6">
      <lay-button prefix-icon="layui-icon-search" v-on:click="search()">搜索</lay-button>
       <lay-button placement="bottom">
         <router-link :to="{path: '/engines/engines-edit/-1'}">
           新增<i class="layui-icon">&#xe654;</i>
         </router-link>
       </lay-button>
      <lay-button placement="bottom" >
        <div style="position: relative">
          <router-link :to="{path: '/engines/engine-edit-g6/-1'}">
            新增<i class="layui-icon">&#xe654;</i><sup style="color: red;position: absolute; top: -8px; left: 42px;">new</sup>
          </router-link>
        </div>
      </lay-button>
    </lay-col>
  </lay-row>
  <lay-row space="10">
    <lay-table :columns="columns2" :data-source="dataSource2" :size="md"
               skin='line' :page="page" even="true">
      <template #status="{ data }">
        <div v-if="data.status === 0">
          <lay-badge type="dot" theme="green" ripple></lay-badge>
          正常
        </div>
        <div v-if="data.status === 1">
          <lay-badge type="dot"></lay-badge>
          已下线
        </div>
      </template>
      <template #options="{ data }">
        <div>
          <router-link :to="{path: '/engines/engines-history/' + data.id}">
             历史记录&nbsp;&nbsp;
          </router-link>
        </div>
        <div>
          <router-link :to="{path: '/engines/engines-edit/' + data.id}">
            编辑&nbsp;&nbsp;
          </router-link>
        </div>
        <div>
          <router-link :to="{path: '/engines/engine-edit-g6/' + data.id}" style="display: flex; justify-content: center;">
            <div style="position: relative">
              编辑<sup style="color: red; position: absolute;top:-8px;">new</sup>
            </div>
          </router-link>
        </div>
        <div v-if="data.status === 0">
          <a style="padding-left: 28px;" href="javascript:void(0)" v-on:click="offline(data.id)">下线&nbsp;&nbsp;</a>
        </div>
        <div v-if="data.status === 1">
          <a style="padding-left: 28px;" href="javascript:void(0)" v-on:click="online(data.id)">上线&nbsp;&nbsp;</a>
        </div>
      </template>
    </lay-table>
  </lay-row>
</template>

<script>
import {onMounted, ref} from "vue";
import {engines, engine_online, engine_offline} from "../../api/module/api";

export default {

    setup() {
      const page = ref({
        total: 0,
        limit: 10,
        current: 1,
        showCount:true
      });
      const columns2 = [
        {
          title: "引擎ID",
          width: "30px",
          key: "id",
        }, {
          title: "引擎名称",
          width: "120px",
          key: "engineName"
        }, {
          title: "版本",
          width: "20px",
          key: "version",
        }, {
          title: "状态",
          width: "50px",
          key: "status",
          customSlot: "status"
        }, {
          title: "创建时间",
          width: "80px",
          key: "created"
        }, {
          title: "最后修改时间",
          width: "80px",
          key: "updated"
        },
        {
          title: "操作",
          width: "120px",
          key: "options",
          customSlot: "options"
        }
      ]

      const dataSource2 = ref([])
      const engineName = ref("");

      const loadData = async (pageNo, pageSize) => {
        const {data} = await engines(engineName.value, pageNo, pageSize);
        dataSource2.value = data.list;
        page.value.current = data.pageNo;
        page.value.total = data.total;
        page.value.limit = data.pageSize;
      };
      loadData(page.value.current, page.value.limit);


      return {
        page,
        engineName,
        columns2,
        dataSource2,
        offline(engineId) {

          const func = async () => {
            const data = await engine_offline(engineId);
            await loadData(page.value.current, page.value.limit);
          };
          func();
        },
        online(engineId) {
          const func = async () => {
            const data = await engine_online(engineId);
            await loadData(page.value.current, page.value.limit);
          };
          func();
        },
        search() {
          page.value.current = 1;
          loadData(page.value.current, page.value.limit);
        }
      }
    },

}
</script>