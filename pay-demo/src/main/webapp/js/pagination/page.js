document.write("<link rel=\"stylesheet\" type=\"text/css\" href=\""+basePath+"/js/pagination/pagination.css\"/>"
		+ "<script type=\"text/javascript\" src=\""+basePath+"/js/pagination/jquery.pagination.js\"></script>");
/* 测试用法
 	// 异步请求方式
	$("#page").pagination({
		totalCount : 50,
		pageSize : 10,
		callback : function(page_index,_this) {
			//点击分页后处理的操作
		}
	});
	// 同步方式请求
	$("#page").pagination({
		totalCount : totalCount,
		pageSize : pageSize,
		currentPage : pageNo-1,
		clickEvent : function(page_index){
			hrefToBlank(basePath+"/prd/list", {
				"pageSize" :pageSize,
				"pageNo":parseInt(page_index + 1)
			});
		}
	});
*/