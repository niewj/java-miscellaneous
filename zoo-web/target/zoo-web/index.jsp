<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7; IE=EDGE">
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache, must-revalidate" />

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>niewj.com</title>

    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.0/css/bootstrap.min.css">
    <!-- 可选的Bootstrap主题文件（一般不用引入） -->
    <link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.0/css/bootstrap-theme.min.css">

    <link rel="stylesheet" href="http://localhost/zoo-web/md/css/editormd.min.css" />
    <style type="text/css">
        /*整体宽度1280px*/
        body {
            width: 1280px;
        }
    </style>

</head>
<body>

<h2>WTF !</h2>

<div class="editormd" id="test-editormd">
    <textarea class="editormd-markdown-textarea" name="test-editormd-markdown-doc"></textarea>
    <!-- 第二个隐藏文本域，用来构造生成的HTML代码，方便表单POST提交，这里的name可以任意取，后台接受时以这个name键为准 -->
    <textarea class="editormd-html-textarea" name="text"></textarea>
</div>

<!--代码-->
<!-- =============================================================================== -->
<!-- =============================================================================== -->
<!-- =============================================================================== -->
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
<script src="http://cdn.bootcss.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<script src="http://localhost/zoo-web/md/editormd.min.js"></script>
<script type="application/javascript">
    $(function() {
        editormd("test-editormd", {
            width   : "90%",
            height  : 640,
            syncScrolling : "single",
            //你的lib目录的路径，我这边用JSP做测试的
            path    : "http://localhost/zoo-web/md/lib/",
            //这个配置在simple.html中并没有，但是为了能够提交表单，使用这个配置可以让构造出来的HTML代码直接在第二个隐藏的textarea域中，方便post提交表单。
            saveHTMLToTextarea : true
        });
    });
</script>
</body>
</html>