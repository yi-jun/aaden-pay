/**
 * This jQuery plugin displays pagination links inside the selected elements.
 *
 * @author Gabriel Birke (birke *at* d-scribe *dot* de)
 * @version 1.2
 * @param {int} maxentries Number of entries to paginate
 * @param {Object} opts Several options (see README for documentation)
 * @return {Object} jQuery Object
 */
jQuery.fn.pagination = function(opts){
	opts = jQuery.extend({
				showCount : 1,//显示分页数
				totalCount : 10,// 总条数
				pageSize : 5,// 页面显示条数
				seriesShowCount : 4,// 连续显示分页数
				currentPage : 0,// 当前页数
				bothSidesShowCount : 1,// 两侧显示的首尾分页的条数
				clickLink : "javascript:;",// 点击时需要跳转的url
				textPrev : "上一页",// 上一页显示的文字
				textNext : "下一页",// 下一页显示的文字
				textNoShow : "...",// 分页数目显示不全时的文字
				isShowPrev : true,// 是否显示上一页
				isShowNext : true,// 是否显示下一页
				clickEvent:function(){
					return false;
				}//回调函数
			}, opts || {});
	// 计算总页数
	var maxentries = parseInt(opts.totalCount / opts.pageSize);
	if (opts.totalCount % opts.pageSize != 0) {
		maxentries = parseInt(maxentries + 1);
	}
	return this.each(function() {
		/**
		 * 计算总页数
		 */
		function pageNums() {
			return Math.ceil(maxentries / opts.showCount);
		}	
		/**
		 * 计算分页的起始和结束点，这取currentPage 和 seriesShowCount.
		 */
		function getInterval() {
			var ne_half = Math.ceil(opts.seriesShowCount / 2);
			var np = pageNums();
			var upper_limit = np - opts.seriesShowCount;
			var start = currentPage > ne_half ? Math.max(Math.min(currentPage - ne_half, upper_limit), 0) : 0;
			var end = currentPage > ne_half ? Math.min(currentPage + ne_half, np) : Math.min(opts.seriesShowCount, np);
			return [start, end];
		}
		
		/**
		 * 点击分页时处理的操作
		 */
		function pageSelected(page_id, evt) {
			currentPage = page_id;
			drawLinks();
			var continuePropagation = opts.clickEvent(page_id, panel);
			if (!continuePropagation) {
				if (evt.stopPropagation) {
					evt.stopPropagation();
				} else {
					evt.cancelBubble = true;
				}
			}
			return continuePropagation;
		}
		
		/**
		 * 此函数将分页链接插入到容器元素中
		 */
		function drawLinks() {
			panel.empty();
			var interval = getInterval();
			var np = pageNums();
			// 这个辅助函数返回一个处理函数，调用有正确page_id点击分页时处理的操作。
			var getClickHandler = function(page_id) {
						return function(evt) {
							return pageSelected(page_id, evt);
						}
					}
			//辅助函数用来产生一个单链接(如果不是当前页则产生span标签)
			var appendItem = function(page_id, appendopts) {
						page_id = page_id < 0 ? 0 : (page_id < np ? page_id : np - 1);
						appendopts = jQuery.extend({
									text : page_id + 1,
									classes : ""
								}, appendopts || {});
						if (page_id == currentPage) {
							var lnk = jQuery("<span class='current_page'>" + (appendopts.text) + "</span>");
						} else {
							var lnk = jQuery("<a>" + (appendopts.text) + "</a>").bind("click", getClickHandler(page_id)).attr('href',
									opts.clickLink.replace(/__id__/, page_id));
						}
						if (appendopts.classes) {
							lnk.addClass(appendopts.classes);
						}
						panel.append(lnk);
					}
			// 产生上一页的链接
			if (opts.textPrev && (currentPage > 0 || opts.isShowPrev)) {
				appendItem(currentPage - 1, {
							text : opts.textPrev,
							classes : "prev"
						});
			}
			// 产生起始点
			if (interval[0] > 0 && opts.bothSidesShowCount > 0) {
				var end = Math.min(opts.bothSidesShowCount, interval[0]);
				for (var i = 0; i < end; i++) {
					appendItem(i);
				}
				if (opts.bothSidesShowCount < interval[0] && opts.textNoShow) {
					jQuery("<span>" + opts.textNoShow + "</span>").appendTo(panel);
				}
			}
			// 产生内部的链接
			for (var i = interval[0]; i < interval[1]; i++) {
				appendItem(i);
			}
			// 产生结束点
			if (interval[1] < np && opts.bothSidesShowCount > 0) {
				if (np - opts.bothSidesShowCount > interval[1] && opts.textNoShow) {
					jQuery("<span>" + opts.textNoShow + "</span>").appendTo(panel);
				}
				var begin = Math.max(np - opts.bothSidesShowCount, interval[1]);
				for (var i = begin; i < np; i++) {
					appendItem(i);
				}

			}
			// 产生下一页的链接
			if (opts.textNext && (currentPage < np - 1 || opts.isShowNext)) {
				appendItem(currentPage + 1, {
							text : opts.textNext,
							classes : "next"
						});
			}
		}
		
		//从选项中提取当前页
		var currentPage = opts.currentPage;
		//创建一个显示条数和每页显示条数值
		maxentries = (!maxentries || maxentries < 0) ? 1 : maxentries;
		opts.showCount = (!opts.showCount || opts.showCount < 0) ? 1 : opts.showCount;
		//存储DOM元素，以方便从所有的内部结构中获取
		var panel = jQuery(this);
		// 获得附加功能的元素
		this.selectPage = function(page_id) {
					pageSelected(page_id);
				}
		this.prevPage = function() {
					if (currentPage > 0) {
						pageSelected(currentPage - 1);
						return true;
					} else {
						return false;
					}
				}
		this.nextPage = function() {
					if (currentPage < pageNums() - 1) {
						pageSelected(currentPage + 1);
						return true;
					} else {
						return false;
					}
				}
		// 初始化，生成分页
		drawLinks();
	});
}


