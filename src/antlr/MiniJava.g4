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
    # andExpression
    |   expression '<'  expression
    # ltExpression  
    |   expression '+'  expression
    # addExpression
    |   expression '-'  expression
    # subExpression
    |   expression '*'  expression
    # mulExpression
    |   expression '[' expression ']'
    # arrayAccessExpression
    |   expression '.' 'length'
    # arrayLengthExpression
    |   expression '.' ID '(' (expression (',' expression)*)? ')'
    # methodCallExpression
    |   INT
    # intLitExpression
    |   booleanLit
    # booleanLitExpression
    |   ID
    # identifierExpression
    |   'this'
    # thisExpression
    |   'new' 'int' '[' expression ']'
    # arrayInstantiationExpression
    |   'new' ID '(' ')'
    # objectInstantiationExpression
    |   '!' expression
    # notExpression
    |   '(' expression ')'
    # parenExpression
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

COMMENT
    : '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;
