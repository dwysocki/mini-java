/** Mini-Java grammar **/

grammar MiniJava;

@header {
    package mini_java.antlr;
}

goal
    :   mainClassDeclaration classDeclaration* EOF
    ;

mainClassDeclaration
    :   'class' ID
        mainClassBody
    ;

classDeclaration
    :   'class' ID ('extends' type)?
        classBody
    ;

mainClassBody
    :   '{' mainMethod '}'
    ;

mainMethod
    :   mainMethodDeclaration '{' statement '}'
    ;

mainMethodDeclaration
    :   'public' 'static' 'void' 'main' '(' 'String' '[' ']' ID ')'
    ;

classBody
    :   '{' varDeclaration* methodDeclaration* '}'
    ;

varDeclaration
    :   type ID ';'
    ;

methodDeclaration
    :   'public' type ID formalParameters
        '{' varDeclaration* statement* 'return' expression ';' '}'
    ;

formalParameters
    :   '(' formalParameterList? ')'
    ;

formalParameterList
    :   formalParameter (',' formalParameter)*
    ;

formalParameter
    :   type ID
    ;

type
    :   intArrayType
    |   booleanType
    |   intType
    |   ID
    ;

intArrayType
    :   'int' '[' ']'
    ;

booleanType
    :   'boolean'
    ;

intType
    :   'int'
    ;

statement
    :   '{' statement* '}'
    |   ifElseStatement
    |   whileStatement
    |   printStatement
    |   assignStatement
    |   arrayAssignStatement
    ;

ifElseStatement
    :   'if' '(' expression ')'
            statement
        'else'
            statement
    ;

whileStatement
    :   'while' '(' expression ')'
            statement
    ;

printStatement
    :   'System.out.println' '(' expression ')' ';'
    ;

assignStatement
    :   ID '=' expression ';'
    ;

arrayAssignStatement
    :   ID '[' expression ']' '=' expression ';'
    ;

expression
    :   expression '&&' expression
    |   expression '<'  expression
    |   expression '+'  expression
    |   expression '-'  expression
    |   expression '*'  expression
    |   expression '[' expression ']'
    |   expression '.' 'length'
    |   expression '.' ID '(' (expression (',' expression)*)? ')'
    |   INT
    |   booleanLit
    |   ID
    |   'this'
    |   'new' 'int' '[' expression ']'
    |   'new' ID '(' ')'
//  |   'new' ID '(' ')' ')'
//      { notifyErrorListeners("Too many parentheses"); }
//  |   'new' ID '('
//      { notifyErrorListeners("Missing closing ')'"); }
    |   '!' expression
    |   '(' expression ')'
//  |   '(' expression ')' ')'
//      { notifyErrorListeners("Too many parentheses"); }
//  |   '(' expression
//      { notifyErrorListeners("Missing closing ')'"); }
    ;

ID
    :   [a-zA-Z_][0-9a-zA-Z_]*
    ;

INT
    :   '0'
    |   [1-9][0-9]*
    ;

booleanLit
    :   'true'
    |   'false'
    ;

WS
    :   [ \r\t\n]+ -> skip
    ;
