<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8"/>
    <title><#if (content.title)??><#escape x as x?xml>${content.title}</#escape><#else>Lense</#if></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <meta name="keywords" content="">
    <meta name="generator" content="JBake">
    
    <script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

	  ga('create', 'UA-77665803-1', 'auto');
	  ga('send', 'pageview');

	</script>
  
	<script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/shCore.js"></script>

	<script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/shBrushScala.js"></script>
	<script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/shBrushCSharp.js"></script>
	<script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/shBrushDart.js"></script>
	<script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/shBrushJava.js"></script>
		
	<script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/shBrushLense.js"></script>
	<style>
	 .console {
	    background : black;
	    color: white;
	    width: 500px;
	 }
	 p {
	 	text-align:justified;
	 }
	 pre {
	 	background-color:white !important;
	 	border:0px !important;
	 	padding:0px !important;
	 	overflow: visible !important;
	 }
	 pre code {
	 	padding:2px !important;
	 	margin : 0px !important;
	 }
	</style>

    <!-- Le styles -->
    <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/bootstrap.min.css" rel="stylesheet">
    <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/asciidoctor.css" rel="stylesheet">
    <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/base.css" rel="stylesheet">
    <link href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/prettify.css" rel="stylesheet">
  	<link rel="stylesheet" type="text/css" href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/shCore.css"  >
	<link rel="stylesheet" type="text/css" href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>css/shThemeLense.css" >
	
    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/html5shiv.min.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->

    <link rel="shortcut icon" href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>icon.png">
  </head>
  <body onload="prettyPrint()">
    <div id="wrap">
   