<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8">
<title>主页</title>
<link rel="stylesheet" type="text/css" href="css/bootstrap.css" />
<link rel="stylesheet" type="text/css"
	href="css/bootstrap-theme.min.css" />
<script src="//cdn.bootcss.com/jquery/3.1.0/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script type="text/javascript">
	
</script>
</head>
<frameset rows="70px,*" border="1">
	<frame name="top" noresize="noresize" src='/wechat/top.html'>
	<frameset cols="140px,*">
		<frame name="left" noresize="noresize" src='/wechat/left.html' />
		<frame name="right" src='/wechat/right.html' />
	</frameset>
</frameset>
</html>