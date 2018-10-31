///Ajax
///Success = 1;Error = 2;Warnning = 3;
///callback( item, Dialog, index,typeIndex,data)
///
///
///
$.ajaxExt = function (opts) {
    var p = {
        type: 'post',
        dataType: 'json',
        cache: false,
        showWaitting: false,
        beforeSend: function (xhr, settings) {
            if (opts.showWaitting) {
                $.blockUI({
                    message: '<span class="post-loading">努力加载中...</span>',
                    css: {
                        width: '200px',
                        border: 'none',
                        padding: '15px',
                        backgroundColor: '#000',
                        '-webkit-border-radius': '5px',
                        '-moz-border-radius': '5px',
                        opacity: .5,
                        color: '#fff',
                        'z-index': 9999999999
                    }
                });
            }
        },
        complete: function () {
            if (opts.showWaitting) {
                $.unblockUI();
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            toastr.error('网络异常：无法连接服务器！');
        }
    };
    return $.ajax($.extend(p, opts, {
        success: function (data) {
            if (opts.success) {
                opts.success(data);
            }
            else {
                toastr.error(data.message ? data.message : '系统错误，请联系管理员');
            }
        }
    }))
};
