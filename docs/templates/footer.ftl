		</div>
		<div id="push"></div>
    </div>
    
    <div id="footer">
      <div class="container">
        <p class="muted credit">Lense is an open source project and a community driven effort</p>
      </div>
    </div>
    
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/jquery-1.11.1.min.js"></script>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/bootstrap.min.js"></script>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/prettify.js"></script>
    <script>
    	SyntaxHighlighter.config.tagName = 'code';
		SyntaxHighlighter.defaults.toolbar = false;
		SyntaxHighlighter.all();
    
    </script>
  </body>
</html>