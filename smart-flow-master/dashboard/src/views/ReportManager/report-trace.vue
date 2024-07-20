<template>
  <lay-row space="20" style="margin-top: 10px;margin-left: 5px;">
    <lay-col sm="6" md="6">
      <lay-input placeholder="链路ID" v-model="query.traceId"></lay-input>
    </lay-col>
    <lay-col sm="6" md="6">
      <lay-input placeholder="引擎名称" v-model="query.engineName"></lay-input>
    </lay-col>
    <lay-col sm="6" md="6">
      <lay-input placeholder="主机" v-model="query.host"></lay-input>
    </lay-col>

    <lay-col sm="6" md="6">
      <lay-button prefix-icon="layui-icon-search" v-on:click="search()">搜索</lay-button>
    </lay-col>
  </lay-row>
  <lay-row space="10">
    <lay-table :columns="columns2" :data-source="dataSource2" :size="md"
               skin='line' even="true" :page="page" @change="change">
      <template #options="{ data }">
        <div>
          <router-link :to="{path: '/report-manager/trace-detail/' + data.id}">
            查看&nbsp;&nbsp;
          </router-link>
        </div>
      </template>
      <template #escaped="{ data }">
        {{data.escaped}}ms
      </template>
      <template #status="{ data }">
        <lay-tooltip :visible="false" trigger="hover" v-bind:content="data.message" position="right">
          <div v-if="data.status === 0" @mouseover="visible=true" @mouseleave="visible=false">
            <lay-badge type="dot"></lay-badge>
            失败
          </div>
        </lay-tooltip>

        <div v-if="data.status === 1">
          <lay-badge type="dot" theme="green" ripple></lay-badge>
          正常
        </div>
      </template>
    </lay-table>
  </lay-row>
</template>

<script>
import {onMounted, ref} from "vue";
import {trace_list} from "../../api/module/api";
import {layer} from "@layui/layui-vue";

export default {

  setup() {
    const columns2 = [
      {
        title: "链路ID",
        width: "100px",
        key: "traceId",
      }, {
        title: "耗时",
        width: "10px",
        key: "escaped",
        customSlot: "escaped"
      }
      , {
        title: "状态",
        width: "5px",
        key: "status",
        customSlot: "status"
      },{
        title: "引擎名称",
        width: "60px",
        key: "engineName"
      },{
        title: "引擎md5",
        width: "60px",
        key: "md5"
      }, {
        title: "主机",
        width: "50px",
        key: "host",
      }, {
        title: "IP",
        width: "50px",
        key: "address"
      }, {
        title: "上报时间",
        width: "65px",
        key: "reportTime"
      },
      {
        title: "操作",
        width: "50px",
        key: "options",
        customSlot: "options"
      }
    ]

    const page = ref({
      total: 0,
      limit: 10,
      current: 1,
      showCount:true
    });
    const dataSource2 = ref([])
    const query = ref({engineName: "", host: "", traceId: ""});

    const loadData = async () => {
      const params = query.value;
      params.pageNo = page.value.current;
      params.pageSize = page.value.limit;
      const {data} = await trace_list(params);
      dataSource2.value = data.list;
      page.value.current = data.pageNo;
      page.value.total = data.total;
      page.value.limit = data.pageSize;
    };
    loadData();

    const change = ({ current, limit }) => {
      page.value.current = current;
      page.value.limit = limit;
      loadData()
    };

    return {
      query,
      page,
      columns2,
      change,
      dataSource2,
      search() {
        page.value.current = 1;
        loadData();
      }
    }
  },

}
</script>