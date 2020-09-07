package com.qtransfer.mod7e.utils;

import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PythonSyntaxHighlighter {
    public static int STATE_TEXT = 1; // 普通文本
    public static int STATE_DOUBLE_QUOTE = 2; // 双引号
    public static int STATE_SINGLE_QUOTE = 3; // 单引号
    public static int STATE_MULTI_LINE_COMMENT = 4; // 多行注释
    public static int STATE_LINE_COMMENT = 5; // 单行注释

    String[] literalArray = { "None", "True", "False" };    //字面常量
    String[] keywordArray = { "and", "as", "assert", "break", "class", "continue", "def", "del", "elif", "else", "except",
            "finally", "for", "from", "global", "if", "import", "in", "is", "lambda", "nonlocal", "not", "or", "pass",
            "raise", "return", "try", "while", "with", "yield" };                  //关键词
    String[] primitiveTypeArray = {"__isub__", "__repr__", "__ne__", "__dir__", "__rlshift__", "__ipow__", "__imul__",
            "__rand__", "__init__", "__format__", "__enter__", "__xor__", "__imod__", "__mul__", "__getattr__",
            "__ixor__", "__bytes__", "__exit__", "__delitem__", "__abs__", "__delete__", "__iter__", "__get__",
            "__new__", "__getitem__", "__rxor__", "__rpow__", "__int__", "__set__", "__hash__", "__ior__", "__len__",
            "__invert__", "__call__", "__radd__", "__ge__", "__rfloordiv__", "__rdivmod__", "__rsub__", "__rrshift__",
            "__le__", "__ror__", "__rmul__", "__rtruediv__", "__iadd__", "__round__", "__bool__", "__reversed__",
            "__delattr__", "__or__", "__rmod__", "__setattr__", "__floordiv__", "__ilshift__", "__sub__", "__itruediv__",
            "__divmod__", "__ifloordiv__", "__iand__", "__truediv__", "__irshift__", "__complex__", "__index__",
            "__setitem__", "__eq__", "__mod__", "__neg__", "__float__", "__and__", "__lshift__", "__del__", "__pow__",
            "__str__", "__getattribute__", "__pos__", "__lt__", "__gt__", "__rshift__", "__contains__", "__add__",
            "print","range", "len", "repr", "str", "bytes", "hash", "bool", "format", "dir", "divmod", "power", "abs", "complex",
            "int", "float", "round", "reversed"};           //魔术方法
    String[] symArray = { "=", "+", "-", "*", "/", ">", "<", "[", "]", "{", "}", "%", ",", "!", "|", "&", "^", ":" };           //符号

    Set<String> literalSet = new HashSet<String>(Arrays.asList(literalArray));
    Set<String> keywordSet = new HashSet<String>(Arrays.asList(keywordArray));
    Set<String> primitiveTypeSet = new HashSet<String>(Arrays.asList(primitiveTypeArray));
    Set<String> symSet = new HashSet<String>(Arrays.asList(symArray));

    public TextFormatting formatting_literal=TextFormatting.DARK_AQUA;
    public TextFormatting formatting_keyword=TextFormatting.GOLD;
    public TextFormatting formatting_primitive=TextFormatting.LIGHT_PURPLE;
    public TextFormatting formatting_constant=TextFormatting.BLUE;
    public TextFormatting formatting_str=TextFormatting.DARK_GREEN;
    public TextFormatting formatting_sym=TextFormatting.AQUA;
    public TextFormatting formatting_comment=TextFormatting.GRAY;

    public TextFormatting formatting_normal=TextFormatting.WHITE;

    public final int tabSize=4;

    public String process(String src) {
        int currentState = STATE_TEXT;
        int identifierLength = 0;
        int currentIndex = -1;
        char currentChar = 0;
        String identifier = "";
        StringBuffer out = new StringBuffer();

        while (++currentIndex != src.length() - 1) {
            currentChar = src.charAt(currentIndex);
            if (Character.isJavaIdentifierPart(currentChar)) {
                out.append(currentChar);
                ++identifierLength;
                continue;
            }
            if (identifierLength > 0) {
                identifier = out.substring(out.length() - identifierLength);
                if (currentState == STATE_TEXT) {
                    if (literalSet.contains(identifier)) { // identifier is a
                        // literal
                        out.insert(out.length() - identifierLength,
                                formatting_literal.toString());
                        out.append(formatting_normal.toString());
                    } else if (keywordSet.contains(identifier)) { // identifier
                        // is a keyword
                        out.insert(out.length() - identifierLength,
                                formatting_keyword.toString());
                        out.append(formatting_normal.toString());
                    } else if (primitiveTypeSet.contains(identifier)) { // identifier
                        // is a primitive type
                        out.insert(out.length() - identifierLength,
                                formatting_primitive.toString());
                        out.append(formatting_normal.toString());
                    } else if (identifier.matches("[0-9]+.?[0-9]*")) { // identifier
                        // is a constant
                        out.insert(out.length() - identifierLength,
                                formatting_constant.toString());
                        out.append(formatting_normal.toString());
                    }/* else if (Character.isUpperCase(identifier.charAt(0))) { // identifier
                        // is non-primitive type
                        out.insert(out.length() - identifierLength,
                                "<div class=\"nonPrimitiveTypeStyle\">");
                        out.append("</div>");
                    }*/
                }
            }

            switch (currentChar) {
                // because I handle the "greater than" and "less than" marks
                // somewhere else, I comment them out here
                // case '<':
                // out.append("&lt;");
                // break;
                // case '>':
                // out.append("&gt;");
                // break;
                case '\"':
                    out.append('\"');
                    if (currentState == STATE_TEXT) {
                        currentState = STATE_DOUBLE_QUOTE;
                        out.insert(out.length() - ("\"").length(),
                                formatting_str.toString());
                    } else if (currentState == STATE_DOUBLE_QUOTE) {
                        currentState = STATE_TEXT;
                        out.append(formatting_normal.toString());
                    }
                    break;
                case '\'':
                    out.append("\'");
                    if (currentState == STATE_TEXT) {
                        currentState = STATE_SINGLE_QUOTE;
                        out.insert(out.length() - ("\'").length(),
                                formatting_str.toString());
                    } else if (currentState == STATE_SINGLE_QUOTE) {
                        currentState = STATE_TEXT;
                        out.append(formatting_normal.toString());
                    }
                    break;
                /*case '\\':
                    out.append("\\");
                    if (currentState == STATE_DOUBLE_QUOTE
                            || currentState == STATE_SINGLE_QUOTE) {
                        // treat as a character escape sequence
                        out.append(src.charAt(++currentIndex));
                    }
                    break;*/
                // if you want to translate tabs into spaces, uncomment the
                // following lines
                /*case '\t':
                    // replace tabs with tabsize number of spaces
                    for (int i = 0; i < tabSize; i++)
                        out.append(" ");
                break;*/
                /*case '*':
                    out.append('*');
                    if (currentState == STATE_TEXT && currentIndex > 0
                            && src.charAt(currentIndex - 1) == '/') {
                        out.insert(out.length() - ("/*").length(),
                                "<div class=\"multiLineCommentStyle\">");
                        currentState = STATE_MULTI_LINE_COMMENT;
                    }
                    break;*/
                case '#':
                    out.append("#");
                    if (currentState == STATE_TEXT && currentIndex > 0) {
                        out.insert(out.length() - 1, formatting_comment.toString());
                        currentState = STATE_LINE_COMMENT;
                    } else if (currentState == STATE_MULTI_LINE_COMMENT) {
                        //out.append("</div>");
                        currentState = STATE_TEXT;
                    }
                    break;
                case '\r':
                case '\n':
                    // end single line comments
                    if (currentState == STATE_LINE_COMMENT) {
                        out.insert(out.length(), formatting_normal.toString());
                        currentState = STATE_TEXT;
                    }
                    if (currentChar == '\r' && currentIndex < src.length() - 1) {
                        out.append("\r\n");
                        ++currentIndex;
                    } else
                        out.append('\n');
                    break;
                /*case 0:
                    if (currentState == STATE_LINE_COMMENT
                            && currentIndex == (src.length() - 1))
                        out.append("</div>");
                    break;*/
                default: // everything else
                    if(currentState==STATE_TEXT && symSet.contains(currentChar+""))
                        out.append(formatting_sym.toString()).append(currentChar).append(formatting_normal.toString());
                    else
                        out.append(currentChar);
            }
            identifierLength = 0;
        }
        return out.toString();
    }

    // test the program by reading a Java file as a String and letting the
    // program process the source code
    /*public static void main(String args[]) throws Exception {
        File file = new File(
                "src/JavaSyntaxHighterDemo/JavaSyntaxHighlighter.java");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF-8"));
        StringBuffer sb = new StringBuffer();
        String temp = null;
        while ((temp = br.readLine()) != null) {
            sb.append(temp).append('\n');
        }
        String src = sb.toString();
        JavaSyntaxHighlighter jsh = new JavaSyntaxHighlighter();
        System.out.println(jsh.process(src));
    }*/
}
