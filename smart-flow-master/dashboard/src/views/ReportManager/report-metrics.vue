<template>
  <lay-row space="20" style="margin-top: 10px; margin-left: 5px;">
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
              <router-link :to="{path: '/report-manager/metrics-detail/' + data.id}">
                 查看&nbsp;&nbsp;
              </router-link>
            </div>
          </template>
    </lay-table>
  </lay-row>
</template>

<script>
import {onMounted, ref} from "vue";
import {metrics_list} from "../../api/module/api";

export default {

    setup() {
      const columns2 = [
        {
          title: "记录ID",
          width: "10px",
          key: "id",
        }, {
          title: "引擎名称",
          width: "80px",
          key: "engineName"
        },{
           title: "引擎md5",
           width: "80px",
           key: "md5"
         }, {
          title: "主机",
          width: "50px",
          key: "host",
        }, {
          title: "IP",
          width: "60px",
          key: "address"
        }, {
          title: "上报时间",
          width: "80px",
          key: "reportTime"
        },
        {
          title: "接收时间",
          width: "80px",
          key: "created"
        },
        {
          title: "操作",
          width: "80px",
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
      const query = ref({engineName: "", host: ""});

      const loadData = async () => {
        const params = query.value;
        params.pageNo = page.value.current;
        params.pageSize = page.value.limit;
        const {data} = await metrics_list(params);
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
      }

      return {
        page,
        query,
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