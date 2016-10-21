/**
 */
;(function()
{
	// CommonJS
	typeof(require) != 'undefined' ? SyntaxHighlighter = require('shCore').SyntaxHighlighter : null;

	function Brush()
	{
		var keywords =	'abstract as break switch case try catch finally ' +
						'continue default do while if else for ' +
						'true false null none' +
						'extends implements constructor ' +
						'import export enhancement module ' +
						'native new is ' +
						'package module class object enum annotation interface trait ' +
						'public private protected ' +
						'in out val var selead override ' +
						'return super synchronized this throw implicit ' 
					//	+ 'static transient volatile strictfp ';

		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.singleLineCComments,	css: 'comments' },		// one line comments
			{ regex: /\/\{([^\*][\s\S]*)?\}\//gm,						css: 'comments' },	 	// multiline comments
			{ regex: /\/\*(?!\*\/)\*[\s\S]*?\*\//gm,					css: 'preprocessor' },	// documentation comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },		// strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },		// strings
			{ regex: /(\b([\d_]+(\.[\d]+)?[NZSLGfdmi]?(E[\d]+)?)\b)|#([a-fA-F0-9_])+|\$([0-1_])+/gi, css: 'value' },			// numbers
			{ regex: /{{/g,												css: 'color1' },		// annotation @anno
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		css: 'keyword' }		// java keyword
			];

		this.forHtmlScript({
			left	: /(&lt;|<)%[@!=]?/g, 
			right	: /%(&gt;|>)/g 
		});
	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['lense'];

	SyntaxHighlighter.brushes.Sense = Brush;

	// CommonJS
	typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
