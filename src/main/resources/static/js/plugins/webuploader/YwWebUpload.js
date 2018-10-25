/**
 * Created by wangfei on 2016/7/12.
 * options = {
 *          checkWholeMd5Url : checkWholeMd5Url,//根据文件整个md5值检查是否存在已经上传的该附件
 *          fileMergeUrl : fileMergeUrl,//文件合并请求
 *          checkUploadUrl : checkUploadUrl,//检查分配是否已上传请求
 *          uploadUrl : uploadUrl,//上传请求
 *          deleteTempUrl : deleteTempUrl //取消上传操作时请求
 *          formData : {}
 *          }
 */
$.ywWebUpload = function (options) {
    var cfgUrl = {
        deleteTempUrl: "/webupload/deleteTemp",
        checkWholeMd5Url: "/webupload/isExistWholeFile",
        checkUploadUrl: '/webupload/checkUpload',
        fileMergeUrl: "/webupload/fileMerge",
        uploadUrl: '/webupload/upload'
    };

    var cfg = {
        deleteTempUrl: options.ctx + cfgUrl.deleteTempUrl,
        checkWholeMd5Url: options.ctx + cfgUrl.checkWholeMd5Url,
        checkUploadUrl: options.ctx + cfgUrl.checkUploadUrl,
        fileMergeUrl: options.ctx + cfgUrl.fileMergeUrl,
        uploadUrl: options.ctx + cfgUrl.uploadUrl
    };


    //var fileMd5 = [];//[id,md5]
    var attachmentArray = [];//文件在attachment表中的id
    var uploader;
    var $wrap;
    var id;
    var $list;
    var $fileTpl;

    /**
     * 初始化页面
     */
    function initPage() {
        // console.log('初始化页面:initPage');
        id = 'picker';
        $wrap = $("#uploader");
        $fileTpl = $(".raw_type");
        $list = $(".dialog_att_list", $wrap);
        $(".btn_blue", $wrap).on("click", function () {
            if (uploader.isInProgress()) {
                $(".webupload_tips", $wrap).html('有正在上传的附件').show();
                setTimeout(function () {
                    $(".webupload_tips", $wrap).hide();
                }, 2000);
                return false;
            }
            if ((typeof options.onOk) == "function") {
                options.onOk(attachmentArray);
            }
            destroy();
        });
        $(".btn_gray", $wrap).on("click", function () {
            if (uploader.isInProgress()) {
                $(".webupload_tips", $wrap).html('有正在上传的附件,请先取消上传').show();
                setTimeout(function () {
                    $(".webupload_tips", $wrap).hide();
                }, 2000);
                return false;
            }
            if ((typeof options.onCancel) == "function") {
                options.onCancel();
            }
            destroy();
        });
    }

    /**
     * 销毁
     */
    function destroy() {
        // console.log('销毁:destroy');
        $wrap.hide();
        try {
            uploader.destroy();
            uploader = null;
        } catch (e) {
        }
        $wrap.remove();
        $wrap = null;
        //fileMd5 = null;
        attachmentArray = null;
    }

    /**
     * 在所有分片都上传完毕后，且没有错误后request，用来做分片验证，此时如果promise被reject，当前文件上传会触发错误。
     * @param file
     * @returns {*}
     */
    function chunkUploadFinish(file) {
        // console.log('在所有分片都上传完毕后:chunkUploadFinish');
        var deferred = $.Deferred();
        var chunks = file.blocks.length;
        var data = {
            chunks: chunks,
            wholeMd5: file.md5,
            ext: file.ext,  //文件扩展名称
            originalFileName: file.name
        };
        if (options.formData)
            jQuery.extend(data, options.formData);
        // console.log(cfg.fileMergeUrl);
        $.ajax({
            cache: false,
            async: true,
            type: "post",
            dataType: "json",
            url: cfg.fileMergeUrl,//fileMerge
            data: data,
            success: function (result) {
                var $li = $('#' + file.id);
                if (result.result == 1) {
                    $li.find('a.icon_att ').css("display", "none");
                    $li.find('.ctrl_finish ').css("display", "block");
                    $li.find('.probar_wrapper ').css("display", "none");
                    $li.find('.ft_l_listItem_info').text("已上传");
                    attachmentArray.push(result.data);
                    deferred.resolve();
                } else {
                    deferred.reject(result.message);
                }
            },
            error: function () {
                deferred.reject("服务器出错，请重新上传。");
            }
        });
        return deferred.promise();
    }

    /**
     * 断点续传，每个分片在发送之前
     */
    function preupload(cutedFile) {
        // console.log('断点续传:preupload');
        //分片验证是否已传过，用于断点续传
        var deferred = new WebUploader.Base.Deferred();
        uploader.md5File(cutedFile.blob).then(function (md5) {
            var data = {
                chunk: cutedFile.chunk,
                chunks: cutedFile.chunks,
                md5: md5,
                wholeMd5: cutedFile.file.md5
            };
            if (options.formData)
                jQuery.extend(data, options.formData);
            $.ajax({
                type: "POST",
                dataType: "text/json",
                url: cfg.checkUploadUrl,//checkUpload
                data: data,
                cache: false,
                // timeout: 3000, //todo 超时的话，只能认为该分片未上传过
                dataType: "json"
            }).then(function (object) {
                if (object.data != '') { //若存在，返回失败给WebUploader，表明该分块不需要上传
                    // console.log("skip:"+"chunk:"+cutedFile.chunk+" md5:"+md5);
                    deferred.reject();
                } else {
                    deferred.resolve();
                }
            }, function () {
                //任何形式的验证失败，都触发重新上传
                deferred.resolve();
            });
        });
        return deferred.promise();
    }

    /**
     * 获取文件图标
     * @param ext
     * @returns {string}
     */
    function getFileIcon(ext) {
        switch (ext.toLowerCase()) {
            case 'zip':
            case 'tar':
            case 'rar':
            case '7z':
                return 'fu_rar.gif';
            case 'txt':
            case 'sql':
            case 'java':
            case 'html':
            case 'htm':
                return 'fu_txt.gif';
            case 'doc':
            case 'docx':
                return 'fu_doc.gif';
            case 'xlsx':
            case 'xls':
                return 'fu_exl.gif';
            case 'jpg':
            case 'gif':
            case 'png':
                return 'fu_jpg.gif';
            case 'pdf':
                return 'fu_pdf.gif';
            case 'ppt':
            case 'pptx':
                return 'fu_ppt.gif';
            default:
                return 'fu_blank.gif';
        }

    }

    /**
     * 文件添加的时候触发
     * @param file
     */
    function fileQueued(file) {
        // console.log('文件添加的时候触发:fileQueued');
        var $li = $('<li id="' + file.id + '"></li>');
        var ftpl = $fileTpl.clone();
        ftpl.css("display", "block");
        ftpl.find(".ft_j_filename").text(file.name);
        ftpl.find("img").attr('src', ftpl.find("img").attr('src') + getFileIcon(file.ext));
        $li.append(ftpl);
        $list.append($li);
        uploader.md5File(file).progress(function (percentage) {
            $li.find('.probar_value').css("width", percentage * 100 + '%');
            $li.find('.ft_l_listItem_info').text('文件扫描中:' + parseInt(percentage * 100) + '%');
        }).then(function (val) {
            file.md5 = val;
            //验证整个文件的md5，系统中存在这个文件，则在后台copy
            var data = {
                wholeMd5: val,
                ext: file.ext,
                fileName: file.name
            };

            if (options.formData)
                jQuery.extend(data, options.formData);
            $.ajax({
                cache: false,
                async: true,
                type: "post",
                dataType: "json",
                url: cfg.checkWholeMd5Url,//isExistWholeFile
                data: data,
                success: function (result) {
                    // console.log(result);
                    if (result.result == 1)//存在，直接后台复制
                    {
                        setTimeout(function () {
                            $li.find('.probar_value').css("width", 0.5 * 100 + '%');
                            $li.find('.ft_l_listItem_info').text("上传中" + parseInt(0.5 * 100) + '%');
                        }, 1000);
                        setTimeout(function () {
                            $li.find('.probar_value').css("width", 1 * 100 + '%');
                            $li.find('.ft_l_listItem_info').text("上传中" + parseInt(1 * 100) + '%');
                        }, 1000);
                        setTimeout(function () {
                            var $li = $('#' + file.id);
                            $li.find('a.icon_att ').css("display", "none");
                            $li.find('.ctrl_finish ').css("display", "block");
                            $li.find('.probar_wrapper ').css("display", "none");
                            $li.find('.ft_l_listItem_info').text("已上传");
                        }, 1000);
                        attachmentArray.push(result.data);
                    }
                    else//不存在，上传
                    {
                        file.kretry = 0;
                        uploader.upload(file);
                    }
                }
            });
        });
    }

    /**
     * 文件上传进度监控 // 文件上传过程中创建进度条实时显示。
     * @param file
     * @param percentage
     */
    function uploadProgress(file, percentage) {
        // console.log('文件上传进度监控:uploadProgress');
        var $li = $('#' + file.id);
        if (!file.uploadProgress)
            file.uploadProgress = 0;
        if (percentage > file.uploadProgress && uploader.isInProgress()) {
            $li.find('.probar_value').css("width", percentage * 100 + '%');
            $li.find('.ft_l_listItem_info').text("上传中" + parseInt(percentage * 100) + '%');
            file.uploadProgress = percentage;
        }
    }

    /**
     * 某个文件开始上传前触发，一个文件只会触发一次。
     * @param file
     */
    function uploadStart(file) {
        // console.log('某个文件开始上传前触发:uploadStart');
        var $li = $('#' + file.id);
        $li.find('a.icon_att ').css("display", "block");
        //给class为ctrl_del的绑定click事件
        $li.off('click', '.ctrl_del');
        $li.off('click', '.ctrl_pause');
        $li.on('click', '.ctrl_del', function () {
            uploader.cancelFile(file);
            $.ajax({
                cache: false,
                async: true,
                type: "post",
                dataType: "json",
                url: cfg.deleteTempUrl,
                data: {
                    fileName: file.originalFileName,
                    wholeMd5: file.md5
                },
                success: function () {
                    $li.find('a.icon_att ').css("display", "none");
                    $li.find('.ft_l_listItem_info').text("已取消");
                }
            });
            $li.find('a.icon_att ').css("display", "none");
        });
        $li.on('click', '.ctrl_pause', function (e) {
            var thisObj = $(this);
            var fid = $(e.target).parents("li").attr('id');
            var cfile = uploader.getFile(fid);
            if (thisObj.hasClass("icon_att_coutinue")) //从暂停到运行
            {
                if (cfile.getStatus() == 'error') {
                    cfile.kretry = 0;
                    uploader.retry(cfile);
                }
                else {
                    cfile.kretry = 0;
                    uploader.upload(cfile);
                }
                thisObj.removeClass("icon_att_coutinue").addClass("icon_att_pause");
            }
            else {
                uploader.stop(cfile);
                thisObj.removeClass("icon_att_pause").addClass("icon_att_coutinue");
            }
        });
    }

    /**
     * 文件暂停的时候
     * @param file
     */
    function stopUpload(file) {
        // console.log('文件暂停的时候:stopUpload');
        var $li = $('#' + file.id);
        $li.find('.ft_l_listItem_info').text("暂停");
    }

    /**
     *当文件的分块在发送前触发
     * @Param object 块对象
     * @Param formdata 参数
     * */
    function uploadBeforeSend(object, formData, headers) {
        // console.log('当文件的分块在发送前触发:uploadBeforeSend');
        headers.Accept = "application/json, text/javascript, */*";//解决http错误
        if (options.formData)
            jQuery.extend(formData, options.formData);
        formData.wholeMd5 = object.file.md5;//添加参数，整个文件的md5
    }

    /**
     * 上传出错的时候触发
     * @param file
     * @param reason
     */
    function uploadError(file, reason) {
        // console.log('上传出错的时候触发:uploadError');
        var $li = $('#' + file.id);
        if (!file.kretry)
            file.kretry = 0;
        //如果网络出问题，或者错误，等待10m继续上传。
        /*if ((reason.indexOf('http') >= 0
            || reason.indexOf('abort') >= 0 || reason.indexOf('timeout') >= 0) && file.kretry < 60) //出问题
        {
            setTimeout(function () {
                uploader.retry(file);
            }, 10 * 1000);
            file.kretry = file.kretry + 1;
            return;
        }
        $li.find('.ft_l_listItem_info').text("上传出错。");
        $li.find(".ctrl_pause").removeClass("icon_att_pause").addClass("icon_att_coutinue");
        alert(file.name + "上传出错:" + reason + ",请点击继续上传按钮继续上传。");*/
        alert('上传出错');
    }

    /**
     * method:after-send-file
     * 在所有分片都上传完毕后，且没有错误后request，用来做分片验证，此时如果promise被reject，当前文件上传会触发错误。
     * para:file: File对象
     */
    initPage();

    WebUploader.Uploader.register({
        'after-send-file': 'chunkUploadFinish',
        'before-send': 'preupload'
    }, {
        chunkUploadFinish: chunkUploadFinish,
        preupload: preupload
    });

    var wuopts = {
        resize: false,// 不压缩image
        swf: options.ctx + '/js/webupload/Uploader.swf', // swf文件路径
        server: cfg.uploadUrl,// 文件接收服务端。
        pick: '#' + id, // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        chunked: true,
        prepareNextFile: true,
        chunkSize: 1 * 1024 * 1024,//如果要分片，分多大一片？ 默认大小为5M.
        chunkRetry: 5,//如果某个分片由于网络问题出错，允许自动重传多少次？
        threads: 4,//上传并发数。允许同时最大上传进程数
        compress: false,
        prefix: 'wf',
        timeout: 5 * 60 * 1000,
        formData: {}//文件上传请求的参数表，每次发送都会发送此对象中的参数
    };

    if (options.accept){
        /*图片上传的*/
        wuopts.accept = options.accept;
    }

    uploader = WebUploader.create(wuopts);
    // 当有文件添加进来的时候
    uploader.on('fileQueued', fileQueued);
    uploader.on('uploadProgress', uploadProgress);
    uploader.on('stopUpload', stopUpload);
    uploader.on('uploadStart', uploadStart);
    uploader.on('uploadBeforeSend', uploadBeforeSend);
    uploader.on('uploadError', uploadError);
};

