;(function(win, lib) {
    var doc = win.document;
    var docEl = doc.documentElement;
    var metaEl = doc.querySelector('meta[name="viewport"]');
    var flexibleEl = doc.querySelector('meta[name="flexible"]');
    var dpr = 0;
    var scale = 0;
    var tid;
    var flexible = lib.flexible || (lib.flexible = {});
    
    if (metaEl) {
        console.warn('将根据已有的meta标签来设置缩放比例');
        var match = metaEl.getAttribute('content').match(/initial\-scale=([\d\.]+)/);
        if (match) {
            scale = parseFloat(match[1]);
            dpr = parseInt(1 / scale);
        }
    } else if (flexibleEl) {
        var content = flexibleEl.getAttribute('content');
        if (content) {
            var initialDpr = content.match(/initial\-dpr=([\d\.]+)/);
            var maximumDpr = content.match(/maximum\-dpr=([\d\.]+)/);
            if (initialDpr) {
                dpr = parseFloat(initialDpr[1]);
                scale = parseFloat((1 / dpr).toFixed(2));    
            }
            if (maximumDpr) {
                dpr = parseFloat(maximumDpr[1]);
                //TODO need bugfix ->scale = p').form('validate')) {
                scale = parseFloat((1 / dpr).toFixed(2));    
            }
        }
    }

    if (!dpr && !scale) {
        var isAndroid = win.navigator.appVersion.match(/android/gi);
        var isIPhone = win.navigator.appVersion.match(/iphone/gi);
        var devicePixelRatio = win.devicePixelRatio;
        if (isIPhone) {
            // iOS下，对于2和3的屏，用2倍的方案，其余的用1倍方案
            if (devicePixelRatio >= 3 && (!dpr || dpr >= 3)) {                
                dpr = 3;
            } else if (devicePixelRatio >= 2 && (!dpr || dpr >= 2)){
                dpr = 2;
            } else {
                dpr = 1;
            }
        } else {
            // 其他设备下，仍旧使用1倍的方案
            dpr = 1;
        }
        scale = 1 / dpr;
    }

    docEl.setAttribute('data-dpr', dpr);
    if (!metaEl) {
        metaEl = doc.createElement('meta');
        metaEl.setAttribute('name', 'viewport');
        metaEl.setAttribute('content', 'initial-scale=' + scale + ', maximum-scale=' + scale + ', minimum-scale=' + scale + ', user-scalable=no');
        if (docEl.firstElementChild) {
            docEl.firstElementChild.appendChild(metaEl);
        } else {
            var wrap = doc.createElement('div');
            wrap.appendChild(metaEl);
            doc.write(wrap.innerHTML);
        }
    }

    function refreshRem(){
        var width = docEl.getBoundingClientRect().width;
        if (width / dpr > 540) {
            width = 540 * dpr;
        }
        var rem = width / 10;
        docEl.style.fontSize = rem + 'px';
        flexible.rem = win.rem = rem;
    }

    win.addEventListener('resize', function() {
        clearTimeout(tid);
        tid = setTimeout(refreshRem, 300);
    }, false);
    win.addEventListener('pageshow', function(e) {
        if (e.persisted) {
            clearTimeout(tid);
            tid = setTimeout(refreshRem, 300);
        }
    }, false);

    if (doc.readyState === 'complete') {
        doc.body.style.fontSize = 12 * dpr + 'px';
    } else {
        doc.addEventListener('DOMContentLoaded', function(e) {
            doc.body.style.fontSize = 12 * dpr + 'px';
        }, false);
    }
    

    refreshRem();

    flexible.dpr = win.dpr = dpr;
    flexible.refreshRem = refreshRem;
    flexible.rem2px = function(d) {
        var val = parseFloat(d) * this.rem;
        if (typeof d === 'string' && d.match(/rem$/)) {
            val += 'px';
        }
        return val;
    }
    flexible.px2rem = function(d) {
        var val = parseFloat(d) / this.rem;
        if (typeof d === 'string' && d.match(/px$/)) {
            val += 'rem';
        }
        return val;
    }
//图750*1000 某个容器在设计图的宽度是150px*225px
//宽度：150px/750px/10=150px/75px=2rem;
//
//高度为：225px/75px=3rem;
})(window, window['lib'] || (window['lib'] = {}));
//http://www.cnblogs.com/sshch/articles/6564210.html
//以上是移动端适配JS







$(document).ready(function(){
 $(document).bind("contextmenu",function(e){
   return false;
 });
 
 
 $("#but").click(function(){
	if($("#p2_Order").val() == "" || $("#p2_Order").length == 0){
        alert("订单号不能为空！");

}else if($("#p1_MerId").val() == "" || $("#p1_MerId").length == 0){
         alert("商户id");

}else if($("#keyValue").val() == "" || $("#keyValue").length == 0){
          alert("商户秘钥");

}else if($("#p3_Amt").val() == "" || $("#p3_Amt").length == 0){
        alert("请输入支付金额");

}else if($("#p5_Pid").val() == "" || $("#p5_Pid").length == 0){
        alert("请输入商品名称");

}else if($("#p6_Pcat").val() == "" || $("#p6_Pcat").length == 0){
       alert("请输入商品种类");

}else if($("#p7_Pdesc").val() == "" || $("#p7_Pdesc").length == 0){
        alert("请输入商品描述");

}else if($("#p8_Url").val() == "" || $("#p8_Url").length == 0){
      alert("请输入接收支付成功数据的地址");

}else if($("#pa_MP").val() == "" || $("#pa_MP").length == 0){
      alert("请输入扩展信息");

}else if($("#pd_FrpId").val() == "" || $("#pd_FrpId").length == 0){
        alert("请输入支付通道编码:");

}
let p1_MerId=$("#p1_MerId").val(),
    keyValue=$("#keyValue").val(),
    p2_Order=$("#p2_Order").val(),
    p3_Amt=$("#p3_Amt").val(),
    p5_Pid=$("#p5_Pid").val(),
    p6_Pcat=$("#p6_Pcat").val(),
    p7_Pdesc=$("#p7_Pdesc").val(),
    p8_Url=$("#p8_Url").val(),
    p9_SAF=$("#p9_SAF").val(),
    pa_MP=$("#pa_MP").val(),
    pd_FrpId=$("#pd_FrpId").val(),


});

