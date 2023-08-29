import{j as se,r as n,ag as d,o as u,c as p,V as t,P as o,a3 as ie,T as m,U as I,F as B,O as P,S as w,n as Y,b as we,a as i,u as X,a9 as oe,J as be,bb as ke,b9 as Fe}from"./@vue-3886a262.js";import{u as Ce}from"./vue-clipboard3-554a1e6d.js";import{u as Ne,b as Ie}from"./vue-router-6d9ab4b6.js";import{_ as Te}from"./index-fa72b4ea.js";import"./clipboard-846a92ed.js";import"./APlayer-9c029590.js";import"./element-plus-cb37424b.js";import"./lodash-es-36eb724a.js";import"./@vueuse-08bf1a74.js";import"./@element-plus-4ca69592.js";import"./@popperjs-c75af06c.js";import"./@ctrl-f8748455.js";import"./dayjs-0d8d1de0.js";import"./async-validator-10c6301d.js";import"./memoize-one-297ddbcb.js";import"./escape-html-a32e42c6.js";import"./normalize-wheel-es-ed76fb12.js";import"./@floating-ui-8a8891e3.js";import"./vue-cookies-10167fa5.js";import"./@highlightjs-a4007344.js";import"./highlight.js-c8ccfcef.js";import"./axios-4a70c6fc.js";import"./docx-preview-21a0c8be.js";import"./jszip-3fdcc0a1.js";import"./xlsx-74460a39.js";import"./vue-pdf-embed-f490ea87.js";import"./vue-a3334fb5.js";import"./dplayer-1b2f7e63.js";const Se={all:{accept:"*"},video:{accept:".mp4,.avi,.rmvb,.mkv,.mov"},music:{accept:".mp3,.wav,.wma,.mp2,.flac,.midi,.ra,.ape,.aac,.cda"},image:{accept:".jpeg,.jpg,.png,.gif,.bmp,.dds,.psd,.pdt,.webp,.xmp,.svg,.tiff"},doc:{accept:".pdf,.doc,.docx,.xls,.xlsx,.txt"},others:{accept:"*"}},Re={__name:"ShareFile",setup(D,{expose:K}){const{toClipboard:A}=Ce(),{proxy:r}=se(),$=n(document.location.origin+"/share/"),Z={shareFile:"/share/shareFile"},b=n(0),c=n({}),S=n(),H={validType:[{required:!0,message:"请选择有效期"}],codeType:[{required:!0,message:"请选择提取码类型"}],code:[{required:!0,message:"请输入提取码"},{validator:r.Verify.shareCode,message:"提取码只能是数字字母"},{min:5,message:"提取码最少5位"}]},R=n(!0),s=n({show:!1,title:"分享",buttons:[{type:"primary",text:"确定",click:v=>{E()}}]}),F=n({}),E=async()=>{if(Object.keys(F.value).length>0){s.value.show=!1;return}S.value.validate(async v=>{if(!v)return;let f={};Object.assign(f,c.value);let y=await r.Request({url:Z.shareFile,params:f});y&&(b.value=1,F.value=y.data,s.value.buttons[0].text="关闭",R.value=!1)})};K({show:v=>{R.value=!0,s.value.show=!0,b.value=0,F.value={},Y(()=>{S.value.resetFields(),c.value=Object.assign({},v)})}});const C=async()=>{await A(`链接:${$.value}${F.value.shareId} 提取码: ${F.value.code}`),r.Message.success("复制成功")};return(v,f)=>{const y=d("el-form-item"),h=d("el-radio"),x=d("el-radio-group"),U=d("el-input"),J=d("el-button"),q=d("el-form"),G=d("Dialog");return u(),p("div",null,[t(G,{show:s.value.show,title:s.value.title,buttons:s.value.buttons,width:"600px",showCancel:R.value,onClose:f[4]||(f[4]=_=>s.value.show=!1)},{default:o(()=>[t(q,{model:c.value,rules:H,ref_key:"formDataRef",ref:S,"label-width":"100px",onSubmit:f[3]||(f[3]=ie(()=>{},["prevent"]))},{default:o(()=>[t(y,{label:"文件"},{default:o(()=>[m(I(c.value.fileName),1)]),_:1}),b.value==0?(u(),p(B,{key:0},[t(y,{label:"有效期",prop:"validType"},{default:o(()=>[t(x,{modelValue:c.value.validType,"onUpdate:modelValue":f[0]||(f[0]=_=>c.value.validType=_)},{default:o(()=>[t(h,{label:0},{default:o(()=>[m("1天")]),_:1}),t(h,{label:1},{default:o(()=>[m("7天")]),_:1}),t(h,{label:2},{default:o(()=>[m("30天")]),_:1}),t(h,{label:3},{default:o(()=>[m("永久有效")]),_:1})]),_:1},8,["modelValue"])]),_:1}),t(y,{label:"提取码",prop:"codeType"},{default:o(()=>[t(x,{modelValue:c.value.codeType,"onUpdate:modelValue":f[1]||(f[1]=_=>c.value.codeType=_)},{default:o(()=>[t(h,{label:0},{default:o(()=>[m("自定义")]),_:1}),t(h,{label:1},{default:o(()=>[m("系统生成")]),_:1})]),_:1},8,["modelValue"])]),_:1}),c.value.codeType==0?(u(),P(y,{key:0,prop:"code"},{default:o(()=>[t(U,{clearable:"",placeholder:"请输入5位提取码",modelValue:c.value.code,"onUpdate:modelValue":f[2]||(f[2]=_=>c.value.code=_),modelModifiers:{trim:!0},maxLength:"5",style:{width:"130px"}},null,8,["modelValue"])]),_:1})):w("",!0)],64)):(u(),p(B,{key:1},[t(y,{label:"分享连接"},{default:o(()=>[m(I($.value)+I(F.value.shareId),1)]),_:1}),t(y,{label:"提取码"},{default:o(()=>[m(I(F.value.code),1)]),_:1}),t(y,null,{default:o(()=>[t(J,{type:"primary",onClick:C},{default:o(()=>[m("复制链接及提取码")]),_:1})]),_:1})],64))]),_:1},8,["model"])]),_:1},8,["show","title","buttons","showCancel"])])}}};const T=D=>(ke("data-v-7b6f0041"),D=D(),Fe(),D),xe={class:"top"},Ve={class:"top-op"},De={class:"btn"},$e=T(()=>i("span",{class:"iconfont icon-upload"},null,-1)),Ee=T(()=>i("span",{class:"iconfont icon-folder-add"},null,-1)),Me=T(()=>i("span",{class:"iconfont icon-del"},null,-1)),Oe=T(()=>i("span",{class:"iconfont icon-move"},null,-1)),ze={class:"search-panel"},Pe={key:0,class:"file-list"},Ue=["onMouseenter","onMouseleave"],qe=["title"],Le=["onClick"],je={key:0,class:"transfer-status"},Be={key:1,class:"transfer-status transfer-fail"},Ke={key:3,class:"edit-panel"},Ae=["onClick"],He=["onClick"],Je={class:"op"},Ge=["onClick"],Qe=["onClick"],We=["onClick"],Xe=["onClick"],Ye=["onClick"],Ze={key:0},el={key:1,class:"no-data"},ll={class:"no-data-inner"},tl=T(()=>i("div",{class:"tips"},"当前目录为空，上传你的第一个文件吧",-1)),al={class:"op-list"},ol={class:"op-item"},sl=T(()=>i("div",null,"上传文件",-1)),il=T(()=>i("div",null,"新建目录",-1)),nl={__name:"Main",emits:["addFile"],setup(D,{expose:K,emit:A}){const{proxy:r}=se();Ne(),Ie();const $=async e=>{A("addFile",{file:e.file,filePid:b.value.fileId})};K({reload:()=>{M.value=!1,v()}});const b=n({fileId:0}),c={loadDataList:"/file/loadDataList",rename:"/file/rename",newFoloder:"/file/newFoloder",getFolderInfo:"/file/getFolderInfo",delFile:"/file/delFile",changeFileFolder:"/file/changeFileFolder",createDownloadUrl:"/file/createDownloadUrl",download:"/api/file/download"},S=we(()=>{const e=Se[C.value];return e?e.accept:"*"}),H=[{label:"文件名",prop:"fileName",scopedSlots:"fileName"},{label:"修改时间",prop:"lastUpdateTime",width:200},{label:"大小",prop:"fileSize",scopedSlots:"fileSize",width:200}],R=()=>{M.value=!0,v()},s=n({}),F={extHeight:50,selectType:"checkbox"},E=n(),M=n(!0),C=n(),v=async()=>{let e={pageNo:s.value.pageNo,pageSize:s.value.pageSize,fileNameFuzzy:E.value,category:C.value,filePid:b.value.fileId};e.category!=="all"&&delete e.filePid;let l=await r.Request({url:c.loadDataList,showLoading:M,params:e});l&&(s.value=l.data,h.value=!1)},f=e=>{s.value.list.forEach(l=>{l.showOp=!1}),e.showOp=!0},y=e=>{e.showOp=!1},h=n(!1),x=n(),U=()=>{h.value||(s.value.list.forEach(e=>{e.showEdit=!1}),h.value=!0,s.value.list.unshift({showEdit:!0,fileType:0,fileId:"",filePid:b.value.fileId}),Y(()=>{x.value.focus()}))},J=e=>{const l=s.value.list[e];l.fileId?l.showEdit=!1:s.value.list.splice(e,1),h.value=!1},q=async e=>{const{fileId:l,filePid:k,fileNameReal:V}=s.value.list[e];if(V==""||V.indexOf("/")!=-1){r.Message.warning("文件名不能为空且不能含有斜杠");return}let z=c.rename;l==""&&(z=c.newFoloder);let j=await r.Request({url:z,params:{fileId:l,filePid:k,fileName:V}});j&&(s.value.list[e]=j.data,h.value=!1)},G=e=>{s.value.list[0].fileId==""&&(s.value.list.splice(0,1),e=e-1),s.value.list.forEach(k=>{k.showEdit=!1});let l=s.value.list[e];l.showEdit=!0,l.folderType==0?(l.fileNameReal=l.fileName.substring(0,l.fileName.indexOf(".")),l.fileSuffix=l.fileName.substring(l.fileName.indexOf("."))):(l.fileNameReal=l.fileName,l.fileSuffix=""),h.value=!0,Y(()=>{x.value.focus()})},_=n([]),ee=n([]),ne=e=>{ee.value=e,_.value=[],e.forEach(l=>{_.value.push(l.fileId)})},ce=e=>{r.Confirm(`你确定要删除【${e.fileName}】吗？删除的文件可在10天内通过回收站还原`,async()=>{await r.Request({url:c.delFile,params:{fileIds:e.fileId}})&&v()})},ue=()=>{_.value.length!=0&&r.Confirm("你确定要删除这些文件吗？删除的文件可在10天内通过回收站还原",async()=>{await r.Request({url:c.delFile,params:{fileIds:_.value.join(",")}})&&v()})},L=n(),O=n({}),re=e=>{O.value=e,L.value.showFolderDialog(b.value.fileId)},de=()=>{O.value={};const e=[b.value.fileId];ee.value.forEach(l=>{l.folderType==1&&e.push(l.fileId)}),L.value.showFolderDialog(e.join(","))},fe=async e=>{if(O.value.filePid===e||b.value.fileId==e){r.Message.warning("文件正在当前目录，无需移动");return}let l=[];O.value.fileId?l.push(O.value.fileId):l=l.concat(_.value),await r.Request({url:c.changeFileFolder,params:{fileIds:l.join(","),filePid:e}})&&(L.value.close(),v())},le=n(),te=n(),pe=e=>{if(e.folderType==1){te.value.openFolder(e);return}if(e.status!=2){r.Message.warning("文件正在转码中，无法预览");return}le.value.showPreview(e,0)},me=e=>{const{curFolder:l,categoryId:k}=e;b.value=l,M.value=!0,C.value=k,v()},ve=async e=>{let l=await r.Request({url:c.createDownloadUrl+"/"+e.fileId});l&&(window.location.href=c.download+"/"+l.data)},ae=n(),_e=e=>{ae.value.show(e)};return(e,l)=>{const k=d("el-button"),V=d("el-upload"),z=d("el-input"),j=d("Navigation"),Q=d("icon"),he=d("Table"),W=d("Icon"),ye=d("Preview"),ge=d("FolderSelect");return u(),p("div",null,[i("div",xe,[i("div",Ve,[i("div",De,[t(V,{"show-file-list":!1,"with-credentials":!0,multiple:!0,"http-request":$,accept:X(S)},{default:o(()=>[t(k,{type:"primary"},{default:o(()=>[$e,m(" 上传 ")]),_:1})]),_:1},8,["accept"])]),C.value=="all"?(u(),P(k,{key:0,type:"success",onClick:U},{default:o(()=>[Ee,m(" 新建文件夹 ")]),_:1})):w("",!0),t(k,{onClick:ue,type:"danger",disabled:_.value.length==0},{default:o(()=>[Me,m(" 批量删除 ")]),_:1},8,["disabled"]),t(k,{onClick:de,type:"warning",disabled:_.value.length==0},{default:o(()=>[Oe,m(" 批量移动 ")]),_:1},8,["disabled"]),i("div",ze,[t(z,{clearable:"",placeholder:"输入文件名搜索",modelValue:E.value,"onUpdate:modelValue":l[0]||(l[0]=N=>E.value=N),onKeyup:oe(R,["enter"])},{suffix:o(()=>[i("i",{class:"iconfont icon-search",onClick:R})]),_:1},8,["modelValue","onKeyup"])]),i("div",{class:"iconfont icon-refresh",onClick:v})]),t(j,{ref_key:"navigationRef",ref:te,onNavChange:me},null,512)]),s.value.list&&s.value.list.length>0?(u(),p("div",Pe,[t(he,{ref:"dataTableRef",columns:H,showPagination:!0,dataSource:s.value,fetch:v,initFetch:!1,options:F,onRowSelected:ne},{fileName:o(({index:N,row:a})=>[i("div",{class:"file-item",onMouseenter:g=>f(a),onMouseleave:g=>y(a)},[(a.fileType==3||a.fileType==1)&&a.status==2?(u(),P(Q,{key:0,cover:a.fileCover,width:32},null,8,["cover"])):(u(),p(B,{key:1},[a.folderType==0?(u(),P(Q,{key:0,fileType:a.fileType},null,8,["fileType"])):w("",!0),a.folderType==1?(u(),P(Q,{key:1,fileType:0})):w("",!0)],64)),a.showEdit?w("",!0):(u(),p("span",{key:2,class:"file-name",title:a.fileName},[i("span",{onClick:g=>pe(a)},I(a.fileName),9,Le),a.status==0?(u(),p("span",je,"转码中")):w("",!0),a.status==1?(u(),p("span",Be,"转码失败")):w("",!0)],8,qe)),a.showEdit?(u(),p("div",Ke,[t(z,{modelValue:a.fileNameReal,"onUpdate:modelValue":g=>a.fileNameReal=g,modelModifiers:{trim:!0},ref_key:"editNameRef",ref:x,maxLength:190,onKeyup:oe(g=>q(N),["enter"])},{suffix:o(()=>[m(I(a.fileSuffix),1)]),_:2},1032,["modelValue","onUpdate:modelValue","onKeyup"]),i("span",{class:be(["iconfont icon-right1",a.fileNameReal?"":"not-allow"]),onClick:g=>q(N)},null,10,Ae),i("span",{class:"iconfont icon-error",onClick:g=>J(N)},null,8,He)])):w("",!0),i("span",Je,[a.showOp&&a.fileId&&a.status==2?(u(),p(B,{key:0},[i("span",{class:"iconfont icon-share1",onClick:g=>_e(a)},"分享",8,Ge),a.folderType==0?(u(),p("span",{key:0,class:"iconfont icon-download",onClick:g=>ve(a)},"下载",8,Qe)):w("",!0),i("span",{class:"iconfont icon-del",onClick:g=>ce(a)},"删除",8,We),i("span",{class:"iconfont icon-edit",onClick:ie(g=>G(N),["stop"])},"重命名",8,Xe),i("span",{class:"iconfont icon-move",onClick:g=>re(a)},"移动",8,Ye)],64)):w("",!0)])],40,Ue)]),fileSize:o(({index:N,row:a})=>[a.fileSize?(u(),p("span",Ze,I(X(r).Utils.size2Str(a.fileSize)),1)):w("",!0)]),_:1},8,["dataSource"])])):(u(),p("div",el,[i("div",ll,[t(W,{iconName:"no_data",width:120,fit:"fill"}),tl,i("div",al,[t(V,{"show-file-list":!1,"with-credentials":!0,multiple:!0,"http-request":$,accept:X(S)},{default:o(()=>[i("div",ol,[t(W,{iconName:"file",width:60}),sl])]),_:1},8,["accept"]),C.value=="all"?(u(),p("div",{key:0,class:"op-item",onClick:U},[t(W,{iconName:"folder",width:60}),il])):w("",!0)])])])),t(ye,{ref_key:"previewRef",ref:le},null,512),t(ge,{ref_key:"folderSelectRef",ref:L,onFolderSelect:fe},null,512),t(Re,{ref_key:"shareRef",ref:ae},null,512)])}}},Ol=Te(nl,[["__scopeId","data-v-7b6f0041"]]);export{Ol as default};
