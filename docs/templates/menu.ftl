	<!-- Fixed navbar -->
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <a class="navbar-brand" href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>index.html">Lense</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>news.html">News</a></li>
            <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>download.html">Download</a></li>
            <li><a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>documents.html">Documentation</a></li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Development<b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="https://github.com/sergiotaborda/lense-lang">Source Code</a></li>
                <!--
                <li><a href="#">Another action</a></li>
                <li><a href="#">Something else here</a></li>
                <li class="divider"></li>
                <li class="dropdown-header">Nav header</li>
                <li><a href="#">Separated link</a></li>
                <li><a href="#">One more separated link</a></li>
                -->
              </ul>
            </li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Content<b class="caret"></b></a>
              <ul class="dropdown-menu">
                <#list all_content as post>
                	<li><a href="${post.uri}">${post.title}</a></li>
               </#list>
              </ul>
            </li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>
    <div class="container">