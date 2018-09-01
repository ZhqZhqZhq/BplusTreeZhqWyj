$(document).ready(function(){
	
	// The small arrow that marks the active search icon:
	var arrow = $('<span>',{className:'arrow'}).appendTo('ul.icons');
	
	$('ul.icons li').click(function(){
		var el = $(this);
		
		if(el.hasClass('active')){
			// The icon is already active, exit
			return false;
		}	
		// Move the arrow below this icon
		arrow.stop().animate({
			left		: el.position().left,
			marginLeft	: (el.width()/2)-4
		});

});
});
function showCover(){
	
	 $("#Cover").height(pageHeight()); 
	 $("#Cover" ).width(pageWidth()); 
	 $("#Cover" ).fadeTo(200,0.5);
 }
 function hideCover(){
	 $("#Cover").fadeOut(200);
 }
/* 当前页面高度 */
function pageHeight() {
    return document.documentElement.scrollHeight;
}

/* 当前页面宽度 */
function pageWidth() {
    return document.documentElement.scrollWidth; //网页正文全文宽
}
//获取盘符
$.ajax({ 
	url : "diskRootName", 
	type : "get", 
	success: function(data){ 
	var jsonObj=eval("("+data+")");
	$.each(jsonObj, function (i, item) { 
	jQuery("#root").append("<option value="+ item+">"+ item+"</option>");
	
	}); 
	}, 
	error: function(text) {} 
	}); 

//获取数据
function getDatas(filetype){
	var content = $("#s").val();
	var root = $("#root").val();
	var way = $("input[name='way']:checked").val();
	var reindex = $("input[name='reindex']:checked").val();

	 if(typeof(filetype)=="undefined"){
		 filetype='';
	 }
	 else
	 {
		 content='';
	 }
	 
	 if(filetype==''&&content==''){
		 alert("文件名不能为空！");
	 }
	$.ajax({
		async:true,
		beforeSend:function(){
			showCover();
		},
		complete:function(){
			//alert("发送ok");
			hideCover();
		},
		url:"fileIndex",
		type:'post',
		data:{
			"content":content,
			"root"   :root,
			"way"    :way,
			"reindex"  :reindex,
			"filetype":filetype
		},
        success:function(data){
			var json = eval("("+data+")");
			if(json[0]==null){
				alert("未找到您查询的文件");
				clearContent();
			}
			else{
			setContent(json);
			}
		}

	});	  
}
//保存索引
function saveIndex(){
	var save = $("#save").val();
	$.ajax({
		
		async:true,
		url:"fileIndex",
		type:'get',
		beforeSend:function(){
			showCover();
		},
		complete:function(){
			hideCover();
		},
		
		data:{
			"save":save,
			"content":"",
			"root"   :"",
			"way"    :"",
			"reindex"  :"",
			"filetype":""
		},
		success:function(data){
			var json = eval("("+data+")");
			var con=json[0];
			if(con=="OK"){
				alert("保存成功！");
			}
		}
	});
}
//显示位置
function setLocation(){
	var content = $("#s");
	var width = content.offsetWidth;
	var left = content["offsetLeft"];
	var top = content["offsetTop"]+content.offsetHeight;
	var popDiv = $("#popDiv");
	popDiv.css({
		'border':"gray 1px solid",
		'left':left+'px',
		'top':top +'px',
		'width':width+'px'
	});
	$("#content-table").css('width',width+'px');

}
//显示内容
function setContent(contents){
	clearContent();
	setLocation();
	var size = contents.length;
	var con = contents[0];
	if(con==null&&typeof(con)!="undefined"){
		alert("未找到您搜索的文件！");
	}else if(size==0){
		alert('请输入您要搜索的文件名');
	}
	else{
		var count = 0;
		for(var i=0;i<size;i++){
			var nextNode = contents[i];
			var tr = document.createElement("tr");
			var td = document.createElement("td");
			td.setAttribute("borde","0"); 
//			td.setAttribute("bgcolor","#FFFFFF"); 
			var text = document.createTextNode(nextNode);
			td.appendChild(text);
			tr.appendChild(td);
			$("#content_table_body").append(tr);
			count++;
		}
		alert("共找到"+(count)+"个文件！");
	}   
}
function clearContent(){ 
	$("#content_table_body").empty();
}
//选择文件类型
$(function(){  //四按钮的的innerText中的值 两个<>innerText</>是正解
	$('li').click(function(){ 
        	getDatas(this.innerText);
        });
});
