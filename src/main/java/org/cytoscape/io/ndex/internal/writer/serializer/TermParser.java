package org.cytoscape.io.ndex.internal.writer.serializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TermParser {

	private List<JdexTermObject> termList;
	private Map<String, String> termMap;

	private String termString;
	private List<String> tokenList;
	private Iterator<String> tokenItr;
	private String currentToken;
	private String nextToken;

	int termNum = 0;

	public TermParser(List<JdexTermObject> termList, Map<String, String> termMap) {
		this.termList = termList;
	this.termMap = termMap;
	}

	public String parse(String termString) {
		this.termString = termString;
		this.tokenList = createTokenList(this.termString);
		this.tokenItr = tokenList.iterator();
		currentToken="";
		nextToken="";
		readToken();
		readToken();
		return parseTerm(false).getId();
	}

	private List<String> createTokenList(String termString) {
		System.out.println("termString ==="+termString);
		List<String> tokens = new ArrayList<String>();

		// create token list
		String targetPattern = "([^:\\(\\),]*)(:|\\(|\\)|,|$)";
		Pattern pat = Pattern.compile(targetPattern);
		Matcher mat = pat.matcher(termString);

		while (mat.find()) {
			if (mat.group(0).equals("")) {

			} else {
				if (!mat.group(1).equals(""))
					tokens.add(mat.group(1).trim());
				if (!mat.group(2).equals(""))
					tokens.add(mat.group(2).trim());
			}
		}
		return tokens;
	}

	private void readToken() {
		if (tokenItr.hasNext()) {
			currentToken = nextToken;
			nextToken = tokenItr.next();
		}else if(nextToken != null){
			currentToken = nextToken;
			nextToken = null;
		}else{
			//error
		}
	}

	private JdexTermObject getId(JdexTermObject term){
		
		if(termMap.containsKey(term.getName())){
			String id = termMap.get(term.getName());
			term.setId(id);
					
			return term;
			
		}else{
			termList.add(term);
			int id = termList.lastIndexOf(term);
			term.setId(String.valueOf(id));
			termMap.put(term.getName(), String.valueOf(id));
			return term;
		}
		
		
			
	}

	// parseTermが返すもの

	// 自身が解析した文字列
	// 自身が解析した文字列のtermidもしくは即値
	// termのidか即値かの区別用変数

	// // jdexに出力するデータ

	// term = termName + "(" params ")" | termName
	private JdexTermObject parseTerm(boolean isParam) {
		String name = parseTermName();
		if (currentToken.equals("(")) {
			// 関数名を登録
			JdexTermObject functionName = getId(new TermName(name,"-1", "1", isParam));
			System.out.println("value = " + functionName.getName() + "  id=" + functionName.getId());

			parseSymbol("(");
			name = name + "(";
			// termのリストを得る。
			List<JdexTermObject> params = parseParams();
			name = name + params.get(0).getName();
			for (int i = 1; i < params.size(); i++) {
				name = name + "," + params.get(i).getName();
			}
			name = name + ")";
			parseSymbol(")");

			// 関数を登録
			JdexTermObject function = getId(new TermFunction(name, "-1",functionName,params));
			System.out.println("value = " + function.getName() + "  id=" + function.getId());
			return function;
		}
		// 関数でなければここで名前を登録
		// prefixの有無とisParamで区別できる
		JdexTermObject data = getId(new TermName(name, "-1","1", isParam));
		System.out.println("value = " + data.getName() + "  id=" + data.getId());
		return data;
	}

	// termName = prefix + ":" + name | name
	private String parseTermName() {
		String temp = "";
		if ((nextToken!=null)&&nextToken.equals(":")) {
			temp = parsePrefix() + parseSymbol(":") + parseName();
			//

		} else {
			temp = parseName();
			//

		}
		// TODO prefix除いた文字列と、prefixのidを返す
		// TODO prefix無ければprefixidは-1とか適当な数字

		return temp;
	}

	// prefix = .+
	private String parsePrefix() {
		// TODO prefixからprefixIdを得て、返す。

		String prefix = new String(currentToken);
		readToken();
		return prefix;
	}

	// name = .+
	private String parseName() {
		// 文字列返す
		String name = new String(currentToken);
		readToken();
		return name;

	}

	// params = param + (, param)*
	private List<JdexTermObject> parseParams() {
		List<JdexTermObject> paramList = new ArrayList<JdexTermObject>();
		JdexTermObject param = parseParam();
		paramList.add(param);
		while (currentToken.equals(",")) {
			parseSymbol(",");
			param = parseParam();
			paramList.add(param);
		}
		return paramList;
	}

	// param = term
	private JdexTermObject parseParam() {
		return parseTerm(true);
	}

	// symbol = : | ( | ) | ,
	private String parseSymbol(String symbol) {
		// symbolに何かする。
		// 文字列返す
		// debug
		// System.out.println(symbol);
		readToken();
		return symbol;
	}


}
