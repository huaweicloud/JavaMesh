webpackJsonp([1],{0:function(n,t){},"D0/4":function(n,t){},JMTj:function(n,t){},NHnr:function(n,t,e){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r=e("7+uW"),a={render:function(){var n=this.$createElement,t=this._self._c||n;return t("div",{attrs:{id:"app"}},[t("router-view")],1)},staticRenderFns:[]};var i=e("VU/8")({name:"App"},a,!1,function(n){e("D0/4")},null,null).exports,o=e("/ocq"),s={name:"PluginsInfo",data:function(){return{pluginsInfo:[]}},mounted:function(){var n=this;this.$http.get("http://127.0.0.1:8900/sermant/getPluginsInfo").then(function(t){console.info(t.body),n.pluginsInfo=t.body},function(n){console.error(n)})}},u={render:function(){var n=this,t=n.$createElement,e=n._self._c||t;return e("div",{staticClass:"hi"},[e("table",{attrs:{border:"1"}},[e("td",[n._v("appName")]),n._v(" "),e("td",[n._v("instanceId")]),n._v(" "),e("td",[n._v("ip")]),n._v(" "),e("td",[n._v("version")]),n._v(" "),e("td",[n._v("heartbeatTime")]),n._v(" "),e("td",[n._v("plugin")]),n._v(" "),n._l(n.pluginsInfo,function(t){return e("tr",{key:t},[e("td",[n._v(n._s(t.appName))]),n._v(" "),e("td",[n._v(n._s(t.instanceId))]),n._v(" "),e("td",[n._v(n._s(t.ip))]),n._v(" "),e("td",[n._v(n._s(t.version))]),n._v(" "),e("td",[n._v(n._s(t.heartbeatTime))]),n._v(" "),e("td",[n._v(n._s(t.pluginsMap))])])})],2)])},staticRenderFns:[]};var v=e("VU/8")(s,u,!1,function(n){e("JMTj")},"data-v-3e145dbd",null).exports;r.a.use(o.a);var _=new o.a({routes:[{path:"/",name:"PluginsInfo",component:v}]}),p=e("8+8L");r.a.use(p.a),r.a.config.productionTip=!1,new r.a({el:"#app",router:_,components:{App:i},template:"<App/>"})}},["NHnr"]);
//# sourceMappingURL=app.js.map