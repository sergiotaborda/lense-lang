/**
 */
;(function()
{
	// CommonJS
	typeof(require) != 'undefined' ? SyntaxHighlighter = require('shCore').SyntaxHighlighter : null;

	function Brush()
	{
		var keywords =	'abstract break switch case try catch finally default ' +
						'continue default do while if else for ' +
						'true false null ' +
						'extends implements ' +
						'import export module ' +
						'native new ' +
						'package module class enum annotation interface trait ' +
						'public private protected  ' +
						'in out val var  selead object override ' +
						'return super synchronized this throw ' 
					//	+ 'static transient volatile strictfp ';

		this.regexList = [
			{ regex: SyntaxHighlighter.regexLib.singleLineCComments,	css: 'comments' },		// one line comments
			{ regex: /\/\*([^\*][\s\S]*)?\*\//gm,						css: 'comments' },	 	// multiline comments
			{ regex: /\/\*(?!\*\/)\*[\s\S]*?\*\//gm,					css: 'preprocessor' },	// documentation comments
			{ regex: SyntaxHighlighter.regexLib.doubleQuotedString,		css: 'string' },		// strings
			{ regex: SyntaxHighlighter.regexLib.singleQuotedString,		css: 'string' },		// strings
			{ regex: /\b([\d]+(\.[\d]+)?[ISLGFDMJ]?(E[\d]+)?|#([a-fA-F0-9]_)+|$([0-1]_)+)\b/gi,				css: 'value' },			// numbers
			{ regex: /{{/g,												css: 'color1' },		// annotation @anno
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		css: 'keyword' }		// java keyword
			];

		this.forHtmlScript({
			left	: /(&lt;|<)%[@!=]?/g, 
			right	: /%(&gt;|>)/g 
		});
	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['sense'];

	SyntaxHighlighter.brushes.Sense = Brush;

	// CommonJS
	typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
